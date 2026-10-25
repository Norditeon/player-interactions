// Cerebro del servidor para el consentimiento: recibe peticiones de la rueda y respuestas de
// los clientes (vía RegistroRed) y también las entregas (desde GestorEntregas). Las valida
// contra Configuracion y PlanAcercamiento, guarda las pendientes en RegistroSolicitudes y,
// cuando toca, arranca la interacción en InteraccionesEnCurso.
// También caduca solicitudes cada tick y limpia al desconectarse un jugador.
package com.leonardo.interacciones.servidor;

import com.leonardo.interacciones.Configuracion;
import com.leonardo.interacciones.dominio.ModoPeticion;
import com.leonardo.interacciones.dominio.RegistroSolicitudes;
import com.leonardo.interacciones.dominio.ResultadoPeticion;
import com.leonardo.interacciones.dominio.SolicitudPendiente;
import com.leonardo.interacciones.dominio.TipoInteraccion;
import com.leonardo.interacciones.red.PedirInteraccion;
import com.leonardo.interacciones.red.ResponderSolicitud;
import com.leonardo.interacciones.red.SolicitudRecibida;
import com.leonardo.interacciones.servidor.PlanAcercamiento.Planificacion;
import java.util.Optional;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class GestorSolicitudes {

    private static final int TICKS_POR_SEGUNDO = 20;
    /** Solo el hilo principal del servidor toca el registro, así que no necesita sincronización. */
    private static final RegistroSolicitudes REGISTRO = new RegistroSolicitudes();

    private GestorSolicitudes() {
    }

    public static void alPedir(ServerPlayer solicitante, PedirInteraccion peticion) {
        Entity entidad = solicitante.level().getEntity(peticion.idEntidadObjetivo());
        // Las entregas solo se piden con un objeto en la mano (GestorEntregas), nunca desde la rueda.
        if (!(entidad instanceof ServerPlayer objetivo) || objetivo == solicitante || peticion.tipo().entregaObjeto()) {
            AvisosJugador.resultado(solicitante, ResultadoPeticion.OBJETIVO_INVALIDO);
            return;
        }
        pedir(solicitante, objetivo, peticion.tipo(), peticion.modo(), ItemStack.EMPTY);
    }

    /**
     * Valida y, según el modo, arranca la interacción ya (FORZAR) o envía la solicitud (PEDIR).
     * Devuelve false si se rechazó (el solicitante ya recibió el motivo).
     */
    static boolean pedir(ServerPlayer solicitante, ServerPlayer objetivo, TipoInteraccion tipo,
                         ModoPeticion modo, ItemStack objeto) {
        ResultadoPeticion resultado = validar(solicitante, modo);
        // Se planifica ya al pedir para avisar enseguida si no hay sitio, no tras esperar la respuesta.
        Planificacion plan = planificarEncuentro(solicitante, objetivo, tipo);
        if (resultado == ResultadoPeticion.ACEPTADA && !plan.valida()) {
            resultado = plan.resultado();
        }
        if (resultado != ResultadoPeticion.ACEPTADA) {
            AvisosJugador.resultado(solicitante, resultado);
            return false;
        }
        REGISTRO.anotarUso(solicitante.getUUID(), tickActual(solicitante));
        if (modo == ModoPeticion.FORZAR) {
            InteraccionesEnCurso.iniciar(solicitante, objetivo, tipo, true, plan.destinos());
            return true;
        }
        return enviarSolicitud(solicitante, objetivo, tipo, objeto);
    }

    public static void alResponder(ServerPlayer objetivo, ResponderSolicitud respuesta) {
        Optional<SolicitudPendiente> pendiente = REGISTRO.tomar(objetivo.getUUID(), tickActual(objetivo));
        if (pendiente.isEmpty()) {
            AvisosJugador.sinSolicitudPendiente(objetivo);
            return;
        }
        SolicitudPendiente solicitud = pendiente.get();
        ServerPlayer solicitante = objetivo.server.getPlayerList().getPlayer(solicitud.solicitante());
        if (solicitante == null) {
            AvisosJugador.sinSolicitudPendiente(objetivo);
            GestorEntregas.alCerrarSinEntrega(objetivo.server, solicitud);
        } else if (!respuesta.acepta()) {
            AvisosJugador.rechazada(solicitante, objetivo, solicitud.tipo());
            GestorEntregas.alCerrarSinEntrega(objetivo.server, solicitud);
        } else {
            aceptar(solicitante, objetivo, solicitud);
        }
    }

    /** Entre la petición y el "sí" pueden haberse movido (o soltado el objeto): se vuelve a comprobar todo. */
    private static void aceptar(ServerPlayer solicitante, ServerPlayer objetivo, SolicitudPendiente solicitud) {
        Planificacion plan = planificarEncuentro(solicitante, objetivo, solicitud.tipo());
        ResultadoPeticion resultado = plan.resultado();
        if (plan.valida() && solicitud.tipo().entregaObjeto() && !GestorEntregas.sigueEnMano(solicitante, objetivo)) {
            resultado = ResultadoPeticion.ENTREGA_FALLIDA;
        }
        if (resultado == ResultadoPeticion.ACEPTADA) {
            InteraccionesEnCurso.iniciar(solicitante, objetivo, solicitud.tipo(), false, plan.destinos());
        } else {
            AvisosJugador.resultado(objetivo, resultado);
            AvisosJugador.resultado(solicitante, resultado);
            GestorEntregas.alCerrarSinEntrega(objetivo.server, solicitud);
        }
    }

    @SubscribeEvent
    public static void alTerminarTick(ServerTickEvent.Post evento) {
        MinecraftServer servidor = evento.getServer();
        for (SolicitudPendiente caducada : REGISTRO.retirarCaducadas(servidor.getTickCount())) {
            ServerPlayer solicitante = servidor.getPlayerList().getPlayer(caducada.solicitante());
            if (solicitante != null) {
                AvisosJugador.sinRespuesta(solicitante, caducada.tipo());
            }
            GestorEntregas.alCerrarSinEntrega(servidor, caducada);
        }
    }

    @SubscribeEvent
    public static void alDesconectarse(PlayerEvent.PlayerLoggedOutEvent evento) {
        MinecraftServer servidor = evento.getEntity().getServer();
        for (SolicitudPendiente cancelada : REGISTRO.olvidarJugador(evento.getEntity().getUUID())) {
            GestorEntregas.alCerrarSinEntrega(servidor, cancelada);
        }
    }

    private static ResultadoPeticion validar(ServerPlayer solicitante, ModoPeticion modo) {
        if (modo == ModoPeticion.FORZAR && !Configuracion.forzarPermitido()) {
            return ResultadoPeticion.FORZAR_DESACTIVADO;
        }
        long enfriamiento = (long) Configuracion.SEGUNDOS_ENTRE_INTERACCIONES.get() * TICKS_POR_SEGUNDO;
        return REGISTRO.comprobarEnfriamiento(solicitante.getUUID(), tickActual(solicitante), enfriamiento);
    }

    private static boolean enviarSolicitud(ServerPlayer solicitante, ServerPlayer objetivo,
                                           TipoInteraccion tipo, ItemStack objeto) {
        int segundos = Configuracion.SEGUNDOS_PARA_RESPONDER.get();
        long ahora = tickActual(solicitante);
        var solicitud = new SolicitudPendiente(
                solicitante.getUUID(), objetivo.getUUID(), tipo, ahora + (long) segundos * TICKS_POR_SEGUNDO);
        ResultadoPeticion resultado = REGISTRO.registrar(solicitud, ahora);
        if (resultado != ResultadoPeticion.ACEPTADA) {
            AvisosJugador.resultado(solicitante, resultado);
            return false;
        }
        String nombre = solicitante.getGameProfile().getName();
        PacketDistributor.sendToPlayer(objetivo, new SolicitudRecibida(nombre, tipo, objeto, segundos));
        AvisosJugador.solicitudEnviada(solicitante, objetivo, tipo);
        return true;
    }

    /** Comprobaciones que dependen de dónde y cómo están los dos ahora mismo. */
    private static Planificacion planificarEncuentro(ServerPlayer actor, ServerPlayer receptor, TipoInteraccion tipo) {
        if (!estanCerca(actor, receptor)) {
            return new Planificacion(ResultadoPeticion.DEMASIADO_LEJOS, null);
        }
        if (InteraccionesEnCurso.participa(actor) || InteraccionesEnCurso.participa(receptor)) {
            return new Planificacion(ResultadoPeticion.YA_INTERACTUANDO, null);
        }
        return PlanAcercamiento.planificar(actor, receptor, tipo);
    }

    private static boolean estanCerca(ServerPlayer uno, ServerPlayer otro) {
        double maxima = Configuracion.DISTANCIA_MAXIMA.get();
        return uno.level() == otro.level() && uno.distanceToSqr(otro) <= maxima * maxima;
    }

    private static long tickActual(ServerPlayer jugador) {
        return jugador.server.getTickCount();
    }
}

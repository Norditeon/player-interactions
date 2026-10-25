// Interacciones que están ocurriendo ahora mismo, tick a tick, en dos fases:
//   1. ACERCANDO: desliza a ambos jugadores hasta su distancia de contacto, cara a cara.
//   2. ACTUANDO: el gesto se anima; si se separan antes de tiempo, se corta para los dos. En
//      el momento de contacto el objeto cambia de mano (entregas, GestorEntregas) o llega el
//      golpe (EjecutorInteraccion.impactar).
// En la disposición POR_DETRAS el receptor queda de espaldas a quien hace el gesto.
// La arranca GestorSolicitudes con un PlanAcercamiento válido; al llegar al contacto pide a
// EjecutorInteraccion que el gesto empiece (animación, partículas, sonido, mensajes).
package com.leonardo.interacciones.servidor;

import com.leonardo.interacciones.dominio.Acercamiento;
import com.leonardo.interacciones.dominio.Acercamiento.Destinos;
import com.leonardo.interacciones.dominio.Acercamiento.Posicion;
import com.leonardo.interacciones.dominio.Disposicion;
import com.leonardo.interacciones.dominio.TipoInteraccion;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.RelativeMovement;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class InteraccionesEnCurso {

    /** 6 ticks = 0.3 s: lo bastante corto para no notarse como un tirón, lo bastante largo para verse. */
    private static final int TICKS_ACERCAMIENTO = 6;
    /** Cuánto pueden separarse (en bloques, más allá del contacto) antes de cortar el gesto. */
    private static final double MARGEN_SEPARACION = 1.25;

    private enum Fase { ACERCANDO, ACTUANDO }

    private static final class EnCurso {
        final UUID actor;
        final UUID receptor;
        final TipoInteraccion tipo;
        final boolean forzada;
        final Posicion origenActor;
        final Posicion origenReceptor;
        final Destinos destinos;
        Fase fase = Fase.ACERCANDO;
        int ticksEnFase;

        EnCurso(ServerPlayer actor, ServerPlayer receptor, TipoInteraccion tipo, boolean forzada, Destinos destinos) {
            this.actor = actor.getUUID();
            this.receptor = receptor.getUUID();
            this.tipo = tipo;
            this.forzada = forzada;
            this.origenActor = PlanAcercamiento.posicion(actor);
            this.origenReceptor = PlanAcercamiento.posicion(receptor);
            this.destinos = destinos;
        }

        boolean involucraA(UUID jugador) {
            return actor.equals(jugador) || receptor.equals(jugador);
        }
    }

    /** Solo el hilo principal del servidor la toca. */
    private static final List<EnCurso> ACTIVAS = new ArrayList<>();

    private InteraccionesEnCurso() {
    }

    static boolean participa(ServerPlayer jugador) {
        return ACTIVAS.stream().anyMatch(enCurso -> enCurso.involucraA(jugador.getUUID()));
    }

    static void iniciar(ServerPlayer actor, ServerPlayer receptor, TipoInteraccion tipo, boolean forzada, Destinos destinos) {
        // Quien entrega sigue con la mano extendida mientras se acercan; el gesto la sustituirá.
        if (!tipo.entregaObjeto()) {
            EmocionesEnCurso.detener(actor);
        }
        EmocionesEnCurso.detener(receptor);
        ACTIVAS.add(new EnCurso(actor, receptor, tipo, forzada, destinos));
    }

    @SubscribeEvent
    public static void alTerminarTick(ServerTickEvent.Post evento) {
        Iterator<EnCurso> iterador = ACTIVAS.iterator();
        while (iterador.hasNext()) {
            EnCurso enCurso = iterador.next();
            if (avanzar(evento.getServer(), enCurso)) {
                iterador.remove();
                // Si una entrega se cortó antes de que las manos se juntaran, la oferta ya no vale.
                if (enCurso.tipo.entregaObjeto()) {
                    GestorEntregas.olvidarOferta(enCurso.actor);
                }
            }
        }
    }

    /** Avanza un tick; devuelve true cuando la interacción terminó (o se canceló). */
    private static boolean avanzar(MinecraftServer servidor, EnCurso enCurso) {
        ServerPlayer actor = servidor.getPlayerList().getPlayer(enCurso.actor);
        ServerPlayer receptor = servidor.getPlayerList().getPlayer(enCurso.receptor);
        boolean siguenAhi = actor != null && receptor != null && actor.isAlive() && receptor.isAlive()
                && actor.level() == receptor.level();
        if (!siguenAhi) {
            detenerSiEstabaActuando(enCurso, actor, receptor);
            return true;
        }
        enCurso.ticksEnFase++;
        return enCurso.fase == Fase.ACERCANDO
                ? acercar(enCurso, actor, receptor)
                : actuar(enCurso, actor, receptor);
    }

    private static boolean acercar(EnCurso enCurso, ServerPlayer actor, ServerPlayer receptor) {
        double fraccion = (double) enCurso.ticksEnFase / TICKS_ACERCAMIENTO;
        Posicion posicionActor = Acercamiento.intermedia(enCurso.origenActor, enCurso.destinos.actor(), fraccion);
        Posicion posicionReceptor = Acercamiento.intermedia(enCurso.origenReceptor, enCurso.destinos.receptor(), fraccion);
        colocar(actor, posicionActor, enCurso.destinos.receptor(), receptor.getEyeHeight());
        colocar(receptor, posicionReceptor, haciaDondeMiraElReceptor(enCurso), actor.getEyeHeight());
        if (enCurso.ticksEnFase >= TICKS_ACERCAMIENTO) {
            enCurso.fase = Fase.ACTUANDO;
            enCurso.ticksEnFase = 0;
            EmocionesEnCurso.olvidar(actor);
            EjecutorInteraccion.comenzar(actor, receptor, enCurso.tipo, enCurso.forzada);
        }
        return false;
    }

    private static boolean actuar(EnCurso enCurso, ServerPlayer actor, ServerPlayer receptor) {
        TipoInteraccion tipo = enCurso.tipo;
        if (enCurso.ticksEnFase >= tipo.duracionTicks()) {
            return true;
        }
        int tickDeContacto = Math.round(tipo.duracionTicks() * TipoInteraccion.MOMENTO_CONTACTO);
        // Tras un golpe el retroceso los separa a propósito: eso no corta la reacción.
        boolean puedeSepararse = tipo.golpea() && enCurso.ticksEnFase > tickDeContacto;
        double limite = tipo.distanciaContacto() + MARGEN_SEPARACION;
        if (!puedeSepararse && actor.distanceToSqr(receptor) > limite * limite) {
            EjecutorInteraccion.detener(actor, receptor);
            return true;
        }
        if (enCurso.ticksEnFase == tickDeContacto && !alContacto(actor, receptor, tipo)) {
            EjecutorInteraccion.detener(actor, receptor);
            return true;
        }
        EjecutorInteraccion.duranteGesto(actor, receptor, tipo, enCurso.ticksEnFase);
        return false;
    }

    /** Lo que pasa cuando las manos (o el pie) llegan al otro. false = la interacción se cancela. */
    private static boolean alContacto(ServerPlayer actor, ServerPlayer receptor, TipoInteraccion tipo) {
        if (tipo.entregaObjeto()) {
            return GestorEntregas.entregar(actor, receptor, tipo);
        }
        if (tipo.golpea()) {
            EjecutorInteraccion.impactar(actor, receptor, tipo);
        }
        return true;
    }

    private static Posicion haciaDondeMiraElReceptor(EnCurso enCurso) {
        return enCurso.tipo.disposicion() == Disposicion.POR_DETRAS
                ? Acercamiento.puntoOpuesto(enCurso.destinos.actor(), enCurso.destinos.receptor())
                : enCurso.destinos.actor();
    }

    private static void detenerSiEstabaActuando(EnCurso enCurso, ServerPlayer actor, ServerPlayer receptor) {
        if (enCurso.fase == Fase.ACTUANDO && (actor != null || receptor != null)) {
            EjecutorInteraccion.detener(actor, receptor);
        }
    }

    /**
     * Pone al jugador en la posición indicada mirando al otro. Ojo: en el servidor x/y/z de
     * teleport son SIEMPRE absolutos; las banderas X/Y/Z solo hacen que el paquete al cliente
     * viaje como diferencia respecto a donde está, lo que evita tirones.
     */
    private static void colocar(ServerPlayer jugador, Posicion posicion, Posicion mirandoA, float alturaOjosDelOtro) {
        double dx = mirandoA.x() - posicion.x();
        double dz = mirandoA.z() - posicion.z();
        double dy = (mirandoA.y() + alturaOjosDelOtro) - (posicion.y() + jugador.getEyeHeight());
        float giro = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float inclinacion = (float) -(Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * Mth.RAD_TO_DEG);
        jugador.connection.teleport(posicion.x(), posicion.y(), posicion.z(), giro, inclinacion,
                EnumSet.of(RelativeMovement.X, RelativeMovement.Y, RelativeMovement.Z));
    }
}

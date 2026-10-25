// Emociones que cada jugador está haciendo ahora mismo (como mucho una por jugador).
// Recibe las peticiones de la ruleta (HacerEmocion / DetenerMiEmocion, vía RegistroRed) y
// también las que lanza el servidor por su cuenta (ofrecer un objeto, desde GestorEntregas).
// Avisa a los clientes cercanos (ReproducirEmocion / DetenerAnimacion), también a quien llega
// después y empieza a ver al jugador, y cada tick pide a EfectosEmocion sus partículas.
// Las emociones siguen aunque el jugador camine: solo se cortan desde la ruleta.
package com.leonardo.interacciones.servidor;

import com.leonardo.interacciones.dominio.ResultadoPeticion;
import com.leonardo.interacciones.dominio.TipoEmocion;
import com.leonardo.interacciones.red.DetenerAnimacion;
import com.leonardo.interacciones.red.ReproducirEmocion;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class EmocionesEnCurso {

    /** Evita que alguien inunde de paquetes a los demás cambiando de emoción sin parar. */
    private static final int TICKS_ENTRE_EMOCIONES = 10;

    private record Emocion(TipoEmocion tipo, int tickInicio) {
    }

    /** Solo el hilo principal del servidor las toca. */
    private static final Map<UUID, Emocion> ACTIVAS = new HashMap<>();
    private static final Map<UUID, Integer> ULTIMA_PETICION = new HashMap<>();

    private EmocionesEnCurso() {
    }

    public static void alPedirEmocion(ServerPlayer jugador, TipoEmocion tipo) {
        // Las no elegibles (ofrecer) solo las lanza el servidor: un cliente no puede pedirlas.
        if (!tipo.elegible() || demasiadoSeguido(jugador)) {
            return;
        }
        if (InteraccionesEnCurso.participa(jugador)) {
            AvisosJugador.resultado(jugador, ResultadoPeticion.YA_INTERACTUANDO);
        } else if (!jugador.isAlive() || jugador.isSpectator() || jugador.isSleeping() || jugador.isPassenger()) {
            AvisosJugador.resultado(jugador, ResultadoPeticion.NO_DISPONIBLE);
        } else {
            iniciar(jugador, tipo);
        }
    }

    public static void alPedirDetener(ServerPlayer jugador) {
        Emocion actual = ACTIVAS.get(jugador.getUUID());
        if (actual != null && actual.tipo().elegible()) {
            detener(jugador);
        }
    }

    static void iniciar(ServerPlayer jugador, TipoEmocion tipo) {
        ACTIVAS.put(jugador.getUUID(), new Emocion(tipo, jugador.server.getTickCount()));
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(jugador, new ReproducirEmocion(jugador.getId(), tipo));
    }

    static void detener(ServerPlayer jugador) {
        if (ACTIVAS.remove(jugador.getUUID()) != null) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(jugador, new DetenerAnimacion(jugador.getId()));
        }
    }

    /** La quita sin avisar a los clientes: la animación que se envía justo después la sustituye. */
    static void olvidar(ServerPlayer jugador) {
        ACTIVAS.remove(jugador.getUUID());
    }

    @SubscribeEvent
    public static void alTerminarTick(ServerTickEvent.Post evento) {
        MinecraftServer servidor = evento.getServer();
        int ahora = servidor.getTickCount();
        Iterator<Map.Entry<UUID, Emocion>> iterador = ACTIVAS.entrySet().iterator();
        while (iterador.hasNext()) {
            Map.Entry<UUID, Emocion> entrada = iterador.next();
            ServerPlayer jugador = servidor.getPlayerList().getPlayer(entrada.getKey());
            Emocion emocion = entrada.getValue();
            int transcurridos = ahora - emocion.tickInicio();
            // Las no cíclicas terminan solas también en el cliente: no hace falta avisar.
            if (jugador == null || (!emocion.tipo().ciclica() && transcurridos >= emocion.tipo().duracionTicks())) {
                iterador.remove();
            } else {
                EfectosEmocion.alAvanzar(jugador, emocion.tipo(), transcurridos);
            }
        }
    }

    /** Quien se acerca (o entra al servidor) y empieza a ver a alguien emocionándose, también lo ve. */
    @SubscribeEvent
    public static void alEmpezarAVer(PlayerEvent.StartTracking evento) {
        if (evento.getTarget() instanceof ServerPlayer visto && evento.getEntity() instanceof ServerPlayer quienMira) {
            Emocion emocion = ACTIVAS.get(visto.getUUID());
            if (emocion != null) {
                PacketDistributor.sendToPlayer(quienMira, new ReproducirEmocion(visto.getId(), emocion.tipo()));
            }
        }
    }

    /** Al reaparecer el jugador es una entidad nueva: la emoción anterior ya no tiene a quién animar. */
    @SubscribeEvent
    public static void alReaparecer(PlayerEvent.PlayerRespawnEvent evento) {
        ACTIVAS.remove(evento.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void alDesconectarse(PlayerEvent.PlayerLoggedOutEvent evento) {
        ACTIVAS.remove(evento.getEntity().getUUID());
        ULTIMA_PETICION.remove(evento.getEntity().getUUID());
    }

    private static boolean demasiadoSeguido(ServerPlayer jugador) {
        int ahora = jugador.server.getTickCount();
        Integer ultima = ULTIMA_PETICION.put(jugador.getUUID(), ahora);
        return ultima != null && ahora - ultima < TICKS_ENTRE_EMOCIONES;
    }
}

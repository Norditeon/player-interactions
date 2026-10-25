// Efectos visibles de una interacción: avisa a los clientes cercanos para que la animen
// (ReproducirInteraccion) o la corten (DetenerAnimacion), lanza partículas y sonido, y
// muestra los mensajes. Lo llama InteraccionesEnCurso cuando los jugadores ya están juntos.
package com.leonardo.interacciones.servidor;

import com.leonardo.interacciones.dominio.TipoInteraccion;
import com.leonardo.interacciones.red.DetenerAnimacion;
import com.leonardo.interacciones.red.ReproducirInteraccion;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

final class EjecutorInteraccion {

    private EjecutorInteraccion() {
    }

    /** Los jugadores ya están en contacto (InteraccionesEnCurso los acercó): empieza el gesto. */
    static void comenzar(ServerPlayer actor, ServerPlayer receptor, TipoInteraccion tipo, boolean forzada) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                actor, new ReproducirInteraccion(actor.getId(), receptor.getId(), tipo));
        lanzarEfectos(actor, receptor, tipo);
        // En las entregas el mensaje sale cuando el objeto cambia de mano (GestorEntregas).
        if (!tipo.entregaObjeto()) {
            AvisosJugador.realizada(actor, receptor, tipo, forzada, null);
        }
    }

    /** Corta la animación en los clientes. Cualquiera de los dos puede ser null (desconectado). */
    static void detener(ServerPlayer actor, ServerPlayer receptor) {
        for (ServerPlayer jugador : new ServerPlayer[]{actor, receptor}) {
            if (jugador != null) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(jugador, new DetenerAnimacion(jugador.getId()));
            }
        }
    }

    private static void lanzarEfectos(ServerPlayer actor, ServerPlayer receptor, TipoInteraccion tipo) {
        ServerLevel nivel = actor.serverLevel();
        Vec3 puntoMedio = actor.getEyePosition().add(receptor.getEyePosition()).scale(0.5);
        nivel.sendParticles(particulaDe(tipo), puntoMedio.x, puntoMedio.y + 0.3, puntoMedio.z,
                6, 0.35, 0.25, 0.35, 0.02);
        nivel.playSound(null, puntoMedio.x, puntoMedio.y, puntoMedio.z,
                sonidoDe(tipo), SoundSource.PLAYERS, 0.8F, 1.0F + nivel.random.nextFloat() * 0.2F);
    }

    /** Corazones para gestos de cariño, chispas verdes para gestos de amistad. */
    private static ParticleOptions particulaDe(TipoInteraccion tipo) {
        return switch (tipo) {
            case ABRAZAR, BESO_MEJILLA, BESO_BOCA, TOMAR_MANO, PALMADITAS_CABEZA, ENTREGAR_FLOR -> ParticleTypes.HEART;
            case CHOCAR_CINCO, CHOCAR_PUNOS, ESTRECHAR_MANO, ENTREGAR_OBJETO -> ParticleTypes.HAPPY_VILLAGER;
        };
    }

    private static SoundEvent sonidoDe(TipoInteraccion tipo) {
        return switch (tipo) {
            case ABRAZAR, TOMAR_MANO, PALMADITAS_CABEZA -> SoundEvents.WOOL_PLACE;
            case BESO_MEJILLA, BESO_BOCA, ENTREGAR_FLOR -> SoundEvents.AMETHYST_BLOCK_CHIME;
            case CHOCAR_CINCO, CHOCAR_PUNOS, ESTRECHAR_MANO -> SoundEvents.PLAYER_ATTACK_WEAK;
            case ENTREGAR_OBJETO -> SoundEvents.BUNDLE_INSERT;
        };
    }
}

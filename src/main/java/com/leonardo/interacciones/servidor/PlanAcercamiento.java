// Decide si dos jugadores pueden juntarse para un gesto y dónde quedará cada uno.
// Lo usa GestorSolicitudes al pedir y al aceptar; el resultado lo ejecuta InteraccionesEnCurso.
// La geometría pura está en Acercamiento (dominio); aquí se comprueba el mundo real.
package com.leonardo.interacciones.servidor;

import com.leonardo.interacciones.dominio.Acercamiento;
import com.leonardo.interacciones.dominio.Acercamiento.Destinos;
import com.leonardo.interacciones.dominio.Acercamiento.Posicion;
import com.leonardo.interacciones.dominio.ResultadoPeticion;
import com.leonardo.interacciones.dominio.TipoInteraccion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

final class PlanAcercamiento {

    /** Más de medio bloque de desnivel y los gestos (y el deslizamiento) se verían mal. */
    private static final double DESNIVEL_MAXIMO = 0.6;

    /** destinos es null cuando resultado no es ACEPTADA. */
    record Planificacion(ResultadoPeticion resultado, Destinos destinos) {

        boolean valida() {
            return resultado == ResultadoPeticion.ACEPTADA;
        }
    }

    private PlanAcercamiento() {
    }

    static Planificacion planificar(ServerPlayer actor, ServerPlayer receptor, TipoInteraccion tipo) {
        if (!disponible(actor) || !disponible(receptor)) {
            return fallo(ResultadoPeticion.NO_DISPONIBLE);
        }
        if (Math.abs(actor.getY() - receptor.getY()) > DESNIVEL_MAXIMO) {
            return fallo(ResultadoPeticion.DISTINTA_ALTURA);
        }
        Destinos destinos = Acercamiento.calcular(posicion(actor), posicion(receptor), tipo.distanciaContacto());
        if (!caminoLibre(actor, destinos.actor()) || !caminoLibre(receptor, destinos.receptor())) {
            return fallo(ResultadoPeticion.SIN_ESPACIO);
        }
        return new Planificacion(ResultadoPeticion.ACEPTADA, destinos);
    }

    static Posicion posicion(ServerPlayer jugador) {
        return new Posicion(jugador.getX(), jugador.getY(), jugador.getZ());
    }

    /** En estas situaciones moverlo o animarlo rompería lo que está haciendo. */
    private static boolean disponible(ServerPlayer jugador) {
        return jugador.isAlive() && !jugador.isSpectator() && !jugador.isSleeping()
                && !jugador.isPassenger() && !jugador.isFallFlying() && !jugador.isSwimming();
    }

    /** Comprueba a mitad y al final del trayecto para no atravesar una pared fina. */
    private static boolean caminoLibre(ServerPlayer jugador, Posicion destino) {
        double dx = destino.x() - jugador.getX();
        double dz = destino.z() - jugador.getZ();
        AABB caja = jugador.getBoundingBox();
        return jugador.level().noCollision(jugador, caja.move(dx / 2, 0, dz / 2))
                && jugador.level().noCollision(jugador, caja.move(dx, 0, dz));
    }

    private static Planificacion fallo(ResultadoPeticion motivo) {
        return new Planificacion(motivo, null);
    }
}

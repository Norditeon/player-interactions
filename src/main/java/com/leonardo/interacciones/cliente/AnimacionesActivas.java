// Qué animación está haciendo cada jugador ahora mismo (un gesto entre dos o una emoción),
// desde cuándo y si se está cortando. La rellena y vacía ReceptorCliente (paquetes del
// servidor); la consulta PosesInteraccion en cada fotograma. El tiempo se mide con el
// tickCount de cada entidad para que la animación vaya sincronizada con su movimiento.
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.animacion.Animacion;
import com.leonardo.interacciones.animacion.CatalogoAnimaciones;
import com.leonardo.interacciones.animacion.CatalogoEmociones;
import com.leonardo.interacciones.animacion.Papel;
import com.leonardo.interacciones.dominio.TipoEmocion;
import com.leonardo.interacciones.dominio.TipoInteraccion;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.entity.Entity;

final class AnimacionesActivas {

    /** Ticks para entrar en un bucle o salir de una animación cortada sin dar un salto. */
    private static final float TICKS_TRANSICION = 5;
    private static final int SIN_CORTAR = -1;

    record EnCurso(Animacion animacion, int duracionTicks, boolean ciclica, int tickInicio, int tickCorte) {

        /** 0..1 dentro de la animación (o de la vuelta actual, si es cíclica). */
        float progreso(float edadEnTicks) {
            float vueltas = (edadEnTicks - tickInicio) / duracionTicks;
            return ciclica ? vueltas - (float) Math.floor(vueltas) : vueltas;
        }

        /** Multiplica el peso de la pose: sube al entrar en un bucle y baja al cortar. */
        float intensidad(float edadEnTicks) {
            float entrada = ciclica ? Math.min(1, (edadEnTicks - tickInicio) / TICKS_TRANSICION) : 1;
            float salida = tickCorte == SIN_CORTAR ? 1 : 1 - (edadEnTicks - tickCorte) / TICKS_TRANSICION;
            return Math.max(0, Math.min(entrada, salida));
        }

        boolean terminada(float edadEnTicks) {
            float transcurrido = edadEnTicks - tickInicio;
            // transcurrido < 0: el id se reutilizó para otra entidad (cambio de mundo).
            return transcurrido < 0
                    || (!ciclica && transcurrido >= duracionTicks)
                    || (tickCorte != SIN_CORTAR && edadEnTicks - tickCorte >= TICKS_TRANSICION);
        }
    }

    /** Por id de entidad: es lo que llega por red y lo que tiene el modelo al dibujarse. */
    private static final Map<Integer, EnCurso> POR_ENTIDAD = new HashMap<>();

    private AnimacionesActivas() {
    }

    static void iniciarInteraccion(Entity actor, Entity receptor, TipoInteraccion tipo) {
        iniciar(actor, CatalogoAnimaciones.de(tipo, Papel.ACTOR), tipo.duracionTicks(), false);
        iniciar(receptor, CatalogoAnimaciones.de(tipo, Papel.RECEPTOR), tipo.duracionTicks(), false);
    }

    static void iniciarEmocion(Entity entidad, TipoEmocion tipo) {
        iniciar(entidad, CatalogoEmociones.de(tipo), tipo.duracionTicks(), tipo.ciclica());
    }

    /** No la quita de golpe: la deja desvanecerse unos ticks. */
    static void cortar(Entity entidad) {
        EnCurso actual = POR_ENTIDAD.get(entidad.getId());
        if (actual != null && actual.tickCorte() == SIN_CORTAR) {
            POR_ENTIDAD.put(entidad.getId(), new EnCurso(actual.animacion(), actual.duracionTicks(),
                    actual.ciclica(), actual.tickInicio(), entidad.tickCount));
        }
    }

    /** La animación en curso de la entidad; las terminadas se borran al consultarlas. */
    static Optional<EnCurso> de(Entity entidad, float edadEnTicks) {
        EnCurso animacion = POR_ENTIDAD.get(entidad.getId());
        if (animacion != null && animacion.terminada(edadEnTicks)) {
            POR_ENTIDAD.remove(entidad.getId());
            return Optional.empty();
        }
        return Optional.ofNullable(animacion);
    }

    private static void iniciar(Entity entidad, Animacion animacion, int duracionTicks, boolean ciclica) {
        POR_ENTIDAD.put(entidad.getId(), new EnCurso(animacion, duracionTicks, ciclica, entidad.tickCount, SIN_CORTAR));
    }
}

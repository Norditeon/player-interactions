// Qué animación está haciendo cada jugador ahora mismo (un gesto entre dos o una emoción),
// desde cuándo y si se está cortando. La rellena y vacía ReceptorCliente (paquetes del
// servidor); la consultan en cada fotograma PosesInteraccion (piezas del modelo),
// TransformacionCuerpo (cuerpo entero), ObjetoEnElAire y SigiloAgachado. El tiempo se mide
// con el tickCount de cada entidad para que la animación vaya sincronizada con su movimiento.
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.animacion.Animacion;
import com.leonardo.interacciones.animacion.CatalogoAnimaciones;
import com.leonardo.interacciones.animacion.CatalogoEmociones;
import com.leonardo.interacciones.animacion.Papel;
import com.leonardo.interacciones.animacion.Postura;
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

    /**
     * La pose de este instante. postura.peso() ya incluye la entrada/salida suave; intensidad es
     * solo esa entrada/salida (los giros del cuerpo entero la usan sin el peso del fotograma).
     * emocion es null si es un gesto entre dos.
     */
    record Pose(Postura postura, float intensidad, TipoEmocion emocion, float progreso) {

        boolean mueveElCuerpo() {
            return postura.elevacion() != 0 || postura.vuelta() != 0 || postura.voltereta() != 0;
        }
    }

    private record EnCurso(Animacion animacion, int duracionTicks, boolean ciclica, TipoEmocion emocion,
                           int tickInicio, int tickCorte) {

        /** 0..1 dentro de la animación (o de la vuelta actual, si es cíclica). */
        float progreso(float edadEnTicks) {
            float vueltas = (edadEnTicks - tickInicio) / duracionTicks;
            return ciclica ? vueltas - (float) Math.floor(vueltas) : vueltas;
        }

        /** Sube al entrar en un bucle y baja al cortar. */
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

    static void iniciarInteraccion(Entity actor, Entity receptor, TipoInteraccion tipo, boolean forzada) {
        iniciar(actor, CatalogoAnimaciones.de(tipo, Papel.ACTOR, forzada), tipo.duracionTicks(), false, null);
        iniciar(receptor, CatalogoAnimaciones.de(tipo, Papel.RECEPTOR, forzada), tipo.duracionTicks(), false, null);
    }

    static void iniciarEmocion(Entity entidad, TipoEmocion tipo) {
        iniciar(entidad, CatalogoEmociones.de(tipo), tipo.duracionTicks(), tipo.ciclica(), tipo);
    }

    /** No la quita de golpe: la deja desvanecerse unos ticks. */
    static void cortar(Entity entidad) {
        EnCurso actual = POR_ENTIDAD.get(entidad.getId());
        if (actual != null && actual.tickCorte() == SIN_CORTAR) {
            POR_ENTIDAD.put(entidad.getId(), new EnCurso(actual.animacion(), actual.duracionTicks(),
                    actual.ciclica(), actual.emocion(), actual.tickInicio(), entidad.tickCount));
        }
    }

    /** La pose en curso de la entidad; las animaciones terminadas se borran al consultarlas. */
    static Optional<Pose> poseDe(Entity entidad, float edadEnTicks) {
        EnCurso animacion = POR_ENTIDAD.get(entidad.getId());
        if (animacion == null) {
            return Optional.empty();
        }
        if (animacion.terminada(edadEnTicks)) {
            POR_ENTIDAD.remove(entidad.getId());
            return Optional.empty();
        }
        float progreso = animacion.progreso(edadEnTicks);
        float intensidad = animacion.intensidad(edadEnTicks);
        Postura postura = animacion.animacion().posturaEn(progreso);
        return Optional.of(new Pose(postura.conPeso(postura.peso() * intensidad), intensidad, animacion.emocion(), progreso));
    }

    static Optional<TipoEmocion> emocionDe(Entity entidad) {
        return poseDe(entidad, entidad.tickCount).map(Pose::emocion);
    }

    private static void iniciar(Entity entidad, Animacion animacion, int duracionTicks, boolean ciclica, TipoEmocion emocion) {
        POR_ENTIDAD.put(entidad.getId(),
                new EnCurso(animacion, duracionTicks, ciclica, emocion, entidad.tickCount, SIN_CORTAR));
    }
}

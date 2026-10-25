// Qué pasa con el objeto de la mano en las emociones que lo lanzan:
//   - MALABAR: sale de la mano, sube y baja, y vuelve a la mano en cada vuelta;
//   - LANZAR: sale disparado y ya no vuelve (el servidor lo tira de verdad).
// Lo consultan el cliente (ocultar el objeto de la mano y dibujarlo en el aire) y el servidor
// (EfectosEmocion, para saber en qué tick soltarlo). Lógica pura, con tests.
package com.leonardo.interacciones.animacion;

import com.leonardo.interacciones.dominio.TipoEmocion;
import java.util.OptionalDouble;

public final class ObjetoLanzado {

    /** Momentos (0..1 de la vuelta) en que el malabar suelta y atrapa el objeto. */
    public static final float SOLTAR_MALABAR = 0.2F;
    public static final float ATRAPAR_MALABAR = 0.8F;
    /** Bloques que sube el objeto por encima de la mano en el malabar. */
    public static final double ALTURA_MALABAR = 1.1;
    /** Momento (0..1) en que LANZAR suelta el objeto: coincide con el brazo estirado del catálogo. */
    public static final float SOLTAR_LANZAR = 0.45F;

    private ObjetoLanzado() {
    }

    /** Altura (en bloques sobre la mano) del objeto del malabar, o vacío si está en la mano. */
    public static OptionalDouble alturaMalabar(float progreso) {
        if (progreso < SOLTAR_MALABAR || progreso > ATRAPAR_MALABAR) {
            return OptionalDouble.empty();
        }
        // Parábola: 0 al soltar y al atrapar, ALTURA_MALABAR en el punto medio.
        double t = (progreso - SOLTAR_MALABAR) / (ATRAPAR_MALABAR - SOLTAR_MALABAR);
        return OptionalDouble.of(4 * ALTURA_MALABAR * t * (1 - t));
    }

    /** true si en este momento la mano no debe dibujar el objeto (está en el aire o ya se lanzó). */
    public static boolean manoSinObjeto(TipoEmocion tipo, float progreso) {
        return switch (tipo) {
            case MALABAR -> alturaMalabar(progreso).isPresent();
            case LANZAR -> progreso >= SOLTAR_LANZAR;
            default -> false;
        };
    }
}

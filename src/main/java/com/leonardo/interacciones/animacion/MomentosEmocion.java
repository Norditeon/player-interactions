// Momentos clave (0..1 de la animación o de cada vuelta) en que una emoción produce un efecto:
// un golpe contra el suelo, el tajo de la espada, el beso que sale volando. Los usan las
// coreografías (para colocar el fotograma) y EfectosEmocion en el servidor (para lanzar
// partículas y sonidos en el mismo instante). Una sola fuente para que no se desincronicen.
package com.leonardo.interacciones.animacion;

public final class MomentosEmocion {

    public static final float SALE_EL_BESO = 0.6F;
    public static final float TAJO_ESPADA = 0.35F;
    public static final float PRIMER_GOLPE_AL_SUELO = 0.3F;
    public static final float SEGUNDO_GOLPE_AL_SUELO = 0.8F;
    public static final float ATERRIZA_MORTAL = 0.9F;

    private MomentosEmocion() {
    }
}

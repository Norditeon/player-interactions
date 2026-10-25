// Emociones que mueven o giran el cuerpo entero: giro, voltereta mortal, acostarse, sentarse
// mirando el horizonte, meditar, suplicar de rodillas, golpear el suelo y caminar sigiloso.
// Las registra CatalogoEmociones.
//
// Medidas útiles (píxeles; 16 = un bloque): la pierna mide 12, así que sentado en el suelo
// el cuerpo baja ~11-12, y de rodillas (piernas hacia atrás a 1.1 rad) baja ~6.5.
// Las vueltas completas terminan en 2π (no en 0) para que el cuerpo no "desgire" al acabar.
package com.leonardo.interacciones.animacion;

import static com.leonardo.interacciones.animacion.Animacion.ciclo;
import static com.leonardo.interacciones.animacion.Fotograma.en;
import static com.leonardo.interacciones.animacion.Postura.REPOSO;
import static com.leonardo.interacciones.animacion.Postura.activa;
import static com.leonardo.interacciones.animacion.Suavizado.ACELERAR;
import static com.leonardo.interacciones.animacion.Suavizado.FRENAR;
import static com.leonardo.interacciones.animacion.Suavizado.LINEAL;
import static com.leonardo.interacciones.animacion.Suavizado.SUAVE;

final class EmocionesDeCuerpoEntero {

    private static final float VUELTA_COMPLETA = (float) (Math.PI * 2);
    private static final float BAJADA_SENTADO = 11;
    private static final float BAJADA_DE_RODILLAS = 6.5F;

    private EmocionesDeCuerpoEntero() {
    }

    /** Una vuelta completa sobre sí mismo con los brazos abiertos. */
    static Animacion giro() {
        Postura preparar = activa().brazoDerecho(-0.2F, 0, 0.6F).brazoIzquierdo(-0.2F, 0, -0.6F).cabeza(0, 0, 0);
        Postura girando = activa().brazoDerecho(-0.2F, 0, 1.4F).brazoIzquierdo(-0.2F, 0, -1.4F).cabeza(-0.1F, 0, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.15F, SUAVE, preparar),
                en(0.85F, SUAVE, girando.vuelta(VUELTA_COMPLETA)),
                en(1, SUAVE, REPOSO.vuelta(VUELTA_COMPLETA)));
    }

    /** Se agacha, salta con los brazos arriba, da la vuelta hacia atrás encogido y aterriza. */
    static Animacion mortal() {
        Postura agacharse = activa().torso(0.25F, 0).elevacion(2)
                .brazoDerecho(0.6F, 0, 0.2F).brazoIzquierdo(0.6F, 0, -0.2F)
                .piernaDerecha(0, 0, 0).piernaIzquierda(0, 0, 0);
        Postura despegar = agacharse.torso(0, 0).elevacion(-6).voltereta(0.6F)
                .brazoDerecho(-3.0F, 0, 0.2F).brazoIzquierdo(-3.0F, 0, -0.2F);
        Postura encogido = despegar.elevacion(-9).voltereta((float) Math.PI)
                .brazoDerecho(-1.2F, -0.3F, 0).brazoIzquierdo(-1.2F, 0.3F, 0)
                .piernaDerecha(-1.3F, 0, 0.1F).piernaIzquierda(-1.3F, 0, -0.1F);
        Postura aterrizar = agacharse.torso(0.2F, 0).elevacion(1.5F).voltereta(VUELTA_COMPLETA)
                .brazoDerecho(-0.5F, 0, 0.8F).brazoIzquierdo(-0.5F, 0, -0.8F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.15F, FRENAR, agacharse),
                en(0.3F, FRENAR, despegar),
                en(0.55F, LINEAL, encogido),
                en(0.8F, LINEAL, encogido.elevacion(-4).voltereta(VUELTA_COMPLETA * 0.9F)),
                en(MomentosEmocion.ATERRIZA_MORTAL, FRENAR, aterrizar),
                en(1, SUAVE, REPOSO.voltereta(VUELTA_COMPLETA)));
    }

    /** Tumbado boca arriba con las manos detrás de la cabeza y una pierna cruzada. */
    static Animacion acostarse() {
        Postura tumbado = activa().voltereta((float) (Math.PI / 2)).elevacion(12)
                .cabeza(0, 0, 0).brazoDerecho(-2.9F, -0.3F, 0.4F).brazoIzquierdo(-2.9F, 0.3F, -0.4F)
                .piernaDerecha(-0.1F, 0, -0.15F).piernaIzquierda(0, 0, 0);
        return ciclo(SUAVE, tumbado, tumbado.cabeza(0, 0.25F, 0.1F));
    }

    /** Sentado con las rodillas recogidas y abrazadas, mirando a lo lejos. */
    static Animacion sentarseMirandoHorizonte() {
        Postura sentado = activa().elevacion(BAJADA_SENTADO).torso(0.15F, 0).cabeza(-0.25F, 0.1F, 0)
                .piernaDerecha(-2.0F, 0.1F, 0.05F).piernaIzquierda(-2.0F, -0.1F, -0.05F)
                .brazoDerecho(-1.4F, -0.35F, 0).brazoIzquierdo(-1.4F, 0.35F, 0);
        return ciclo(SUAVE, sentado, sentado.torso(0.2F, 0).cabeza(-0.3F, -0.1F, 0.05F));
    }

    /** Piernas cruzadas, manos en las rodillas, levitando un poco arriba y abajo. */
    static Animacion meditar() {
        Postura abajo = activa().elevacion(10).torso(0, 0).cabeza(0.05F, 0, 0)
                .piernaDerecha(-1.45F, 0.75F, 0).piernaIzquierda(-1.45F, -0.75F, 0)
                .brazoDerecho(-0.5F, 0, 0.5F).brazoIzquierdo(-0.5F, 0, -0.5F);
        return ciclo(SUAVE, abajo, abajo.elevacion(8));
    }

    /** De rodillas con las manos juntas en alto, rogando. */
    static Animacion suplicar() {
        Postura rogar = activa().elevacion(BAJADA_DE_RODILLAS).torso(0.1F, 0).cabeza(-0.3F, 0, 0)
                .piernaDerecha(1.1F, 0, 0.05F).piernaIzquierda(1.1F, 0, -0.05F)
                .brazoDerecho(-1.9F, -0.5F, 0).brazoIzquierdo(-1.9F, 0.5F, 0);
        return ciclo(SUAVE, rogar, rogar.torso(0.2F, 0).brazoDerecho(-2.1F, -0.5F, 0).brazoIzquierdo(-2.1F, 0.5F, 0));
    }

    /** De rodillas, golpeando el suelo con un puño y luego con el otro, con rabia. */
    static Animacion golpearSuelo() {
        Postura base = activa().elevacion(BAJADA_DE_RODILLAS).torso(0.5F, 0).cabeza(0.5F, 0, 0)
                .piernaDerecha(1.1F, 0, 0.05F).piernaIzquierda(1.1F, 0, -0.05F);
        Postura derechoArriba = base.brazoDerecho(-2.2F, 0, 0.1F).brazoIzquierdo(-0.4F, 0, -0.1F);
        Postura ambosAbajo = base.brazoDerecho(-0.3F, 0, 0.1F).brazoIzquierdo(-0.3F, 0, -0.1F);
        Postura izquierdoArriba = base.brazoDerecho(-0.4F, 0, 0.1F).brazoIzquierdo(-2.2F, 0, -0.1F);
        return Animacion.de(
                en(0, LINEAL, derechoArriba),
                en(MomentosEmocion.PRIMER_GOLPE_AL_SUELO, ACELERAR, ambosAbajo),
                en(0.5F, FRENAR, izquierdoArriba),
                en(MomentosEmocion.SEGUNDO_GOLPE_AL_SUELO, ACELERAR, ambosAbajo),
                en(1, FRENAR, derechoArriba));
    }

    /**
     * Agachado (el cliente activa el agacharse de verdad), brazos adelante con cautela y la
     * cabeza vigilando a los lados. Las piernas las mueve Minecraft al caminar agachado.
     */
    static Animacion sigilo() {
        Postura cautela = activa().cabeza(0, 0.4F, 0).brazoDerecho(-0.9F, -0.2F, 0).brazoIzquierdo(-0.7F, 0.25F, 0);
        return ciclo(SUAVE, cautela, cautela.cabeza(0, -0.4F, 0));
    }
}

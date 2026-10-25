// Emociones de pose y actitud: enamorado, beso al aire, entrenar puñetazos, "alto ahí",
// perreo, pose sexy, pose de héroe y derrotado bajo la lluvia. Las registra CatalogoEmociones.
package com.leonardo.interacciones.animacion;

import static com.leonardo.interacciones.animacion.Animacion.ciclo;
import static com.leonardo.interacciones.animacion.Fotograma.en;
import static com.leonardo.interacciones.animacion.Postura.REPOSO;
import static com.leonardo.interacciones.animacion.Postura.activa;
import static com.leonardo.interacciones.animacion.Suavizado.FRENAR;
import static com.leonardo.interacciones.animacion.Suavizado.LINEAL;
import static com.leonardo.interacciones.animacion.Suavizado.SUAVE;

final class EmocionesDePose {

    private EmocionesDePose() {
    }

    /** Manos juntas junto a la mejilla, mirada soñadora hacia arriba, meciéndose con un pie levantado. */
    static Animacion enamorado() {
        Postura soniar = activa().torso(0, 0.08F).cabeza(-0.15F, 0, 0.3F)
                .brazoDerecho(-2.0F, -0.7F, 0).brazoIzquierdo(-2.0F, 0.7F, 0)
                .piernaDerecha(0.5F, 0, 0).piernaIzquierda(0, 0, 0);
        return ciclo(SUAVE, soniar, soniar.torso(0, -0.08F).cabeza(-0.15F, 0, -0.3F));
    }

    /** Se lleva la mano a los labios y lanza el beso hacia delante. */
    static Animacion besoAlAire() {
        Postura labios = activa().torso(0, 0).cabeza(0.1F, 0, 0).brazoDerecho(-2.0F, -0.65F, 0);
        Postura lanzar = activa().torso(0.1F, 0).cabeza(-0.1F, 0, 0.15F).brazoDerecho(-1.7F, 0.1F, 0.4F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.3F, SUAVE, labios),
                en(0.45F, LINEAL, labios),
                en(MomentosEmocion.SALE_EL_BESO, FRENAR, lanzar),
                en(0.8F, LINEAL, lanzar),
                en(1, SUAVE, REPOSO));
    }

    /** Guardia de boxeo con jabs alternos y el peso del cuerpo de un pie al otro. */
    static Animacion entrenarPunetazos() {
        Postura guardia = activa().torso(0.1F, 0).cabeza(0.1F, 0, 0)
                .brazoDerecho(-1.2F, -0.5F, 0).brazoIzquierdo(-1.2F, 0.5F, 0)
                .piernaDerecha(0.2F, 0, 0).piernaIzquierda(-0.2F, 0, 0);
        Postura jabDerecho = guardia.torso(0.15F, 0.04F).brazoDerecho(-1.57F, -0.25F, 0);
        return ciclo(FRENAR, jabDerecho, guardia, jabDerecho.espejo(), guardia);
    }

    /** Las dos palmas hacia delante, empujando el aire: "no, no avances". La cabeza niega. */
    static Animacion detenerAlto() {
        Postura alto = activa().torso(-0.05F, 0).cabeza(0, 0.2F, 0)
                .brazoDerecho(-1.45F, -0.15F, 0).brazoIzquierdo(-1.45F, 0.15F, 0);
        return ciclo(SUAVE, alto, alto.cabeza(0, -0.2F, 0).brazoDerecho(-1.55F, -0.15F, 0).brazoIzquierdo(-1.55F, 0.15F, 0));
    }

    /** Inclinado, manos en las rodillas, piernas abiertas y rebotando rápido con la cadera. */
    static Animacion perreo() {
        Postura abajo = activa().torso(0.6F, 0.06F).cabeza(-0.3F, 0, 0).elevacion(3.5F)
                .brazoDerecho(-0.6F, -0.2F, 0).brazoIzquierdo(-0.6F, 0.2F, 0)
                .piernaDerecha(0, 0, 0.25F).piernaIzquierda(0, 0, -0.25F);
        return ciclo(SUAVE, abajo, abajo.torso(0.55F, -0.06F).elevacion(2));
    }

    /** Una mano en la cadera y otra detrás de la cabeza, cadera ladeada y una pierna cruzada delante. */
    static Animacion poseSexy() {
        Postura pose = activa().torso(0, 0.12F).cabeza(0, 0.2F, -0.15F)
                .brazoDerecho(-2.8F, 0.5F, 0.3F).brazoIzquierdo(0.2F, 0, -0.6F)
                .piernaDerecha(-0.15F, 0, -0.12F).piernaIzquierda(0, 0, 0);
        return ciclo(SUAVE, pose, pose.torso(0, 0.15F).cabeza(0, 0.25F, -0.2F));
    }

    /** Puños en la cadera, pecho fuera, piernas abiertas y la mirada alta a lo lejos. */
    static Animacion poseHeroe() {
        Postura heroe = activa().torso(-0.08F, 0).cabeza(-0.25F, 0.3F, 0)
                .brazoDerecho(0.1F, 0, 0.5F).brazoIzquierdo(0.1F, 0, -0.5F)
                .piernaDerecha(0, 0, 0.12F).piernaIzquierda(0, 0, -0.12F);
        return ciclo(SUAVE, heroe, heroe.cabeza(-0.25F, 0.2F, 0));
    }

    /** Mira al cielo con los brazos caídos y los hombros vencidos, mientras le llueve encima. */
    static Animacion derrotadoBajoLaLluvia() {
        Postura vencido = activa().torso(0.05F, 0).cabeza(-0.6F, 0, 0)
                .brazoDerecho(0.05F, 0, 0.15F).brazoIzquierdo(0.05F, 0, -0.15F)
                .piernaDerecha(0, 0, 0).piernaIzquierda(0, 0, 0);
        return ciclo(SUAVE, vencido, vencido.torso(0.05F, 0.03F).cabeza(-0.5F, 0, 0.05F));
    }
}

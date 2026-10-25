// Coreografías de entregar un objeto o una flor: quien da parte de la pose de ofrecer
// (CatalogoEmociones) y las manos se juntan en TipoInteraccion.MOMENTO_CONTACTO.
// Las registra CatalogoAnimaciones.
package com.leonardo.interacciones.animacion;

import static com.leonardo.interacciones.animacion.Fotograma.en;
import static com.leonardo.interacciones.animacion.Postura.REPOSO;
import static com.leonardo.interacciones.animacion.Postura.activa;
import static com.leonardo.interacciones.animacion.Suavizado.ACELERAR;
import static com.leonardo.interacciones.animacion.Suavizado.FRENAR;
import static com.leonardo.interacciones.animacion.Suavizado.LINEAL;
import static com.leonardo.interacciones.animacion.Suavizado.SUAVE;

import com.leonardo.interacciones.dominio.TipoInteraccion;

final class Entregas {

    private Entregas() {
    }

    /** Ya tenía la mano extendida: la acerca, el otro toma el objeto (0.45) y retira la mano. */
    static Animacion entregarObjetoQuienDa() {
        Postura alcanzar = activa().torso(0.1F, 0).cabeza(0.15F, 0, 0).brazoDerecho(-0.8F, -0.38F, 0);
        return Animacion.de(
                en(0, LINEAL, CatalogoEmociones.OFRECIENDO_OBJETO),
                en(0.3F, SUAVE, alcanzar),
                en(TipoInteraccion.MOMENTO_CONTACTO, LINEAL, alcanzar),
                en(0.65F, SUAVE, activa().torso(0.04F, 0).cabeza(0.1F, 0, 0).brazoDerecho(-0.4F, -0.2F, 0).conPeso(0.8F)),
                en(1, SUAVE, REPOSO));
    }


    /** Alarga la mano, toma el objeto, lo lleva al pecho y lo mira. */
    static Animacion entregarObjetoQuienRecibe() {
        Postura alcanzar = activa().torso(0.1F, 0).cabeza(0.15F, 0, 0).brazoDerecho(-0.8F, -0.38F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.3F, SUAVE, alcanzar),
                en(TipoInteraccion.MOMENTO_CONTACTO, LINEAL, alcanzar),
                en(0.62F, SUAVE, activa().torso(0.02F, 0).cabeza(0.25F, 0, 0).brazoDerecho(-0.55F, -0.6F, 0)),
                en(0.8F, SUAVE, activa().torso(0.02F, 0).cabeza(0.3F, 0.1F, 0.08F).brazoDerecho(-0.6F, -0.55F, 0)),
                en(1, SUAVE, REPOSO));
    }


    /** Con vergüenza: acerca la flor mirando de reojo, la suelta y esconde la mano, más tímido aún. */
    static Animacion entregarFlorQuienDa() {
        Postura timido = CatalogoEmociones.timidoOfreciendo(0.04F, 0.25F);
        Postura alcanzar = timido.torso(0.08F, 0).cabeza(0.35F, 0.15F, 0.1F).brazoDerecho(-0.6F, -0.32F, 0);
        Postura esconder = timido.cabeza(0.45F, 0.35F, 0.12F).brazoDerecho(0.3F, 0, 0).piernaDerecha(0, 0.3F, 0);
        return Animacion.de(
                en(0, LINEAL, timido),
                en(0.28F, SUAVE, alcanzar),
                en(TipoInteraccion.MOMENTO_CONTACTO, LINEAL, alcanzar),
                en(0.62F, SUAVE, esconder),
                en(0.82F, SUAVE, esconder.torso(0.04F, 0.05F).piernaDerecha(0, 0.05F, 0)),
                en(1, SUAVE, REPOSO));
    }


    /** Toma la flor con las dos manos, se la lleva al pecho y se mece contento. */
    static Animacion entregarFlorQuienRecibe() {
        Postura alcanzar = activa().torso(0.06F, 0).cabeza(0.2F, 0, 0)
                .brazoDerecho(-0.62F, -0.35F, 0).brazoIzquierdo(-0.62F, 0.35F, 0);
        Postura alPecho = activa().torso(0.02F, 0).cabeza(0.15F, 0, 0.2F)
                .brazoDerecho(-0.5F, -0.75F, 0).brazoIzquierdo(-0.5F, 0.75F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.28F, SUAVE, alcanzar),
                en(TipoInteraccion.MOMENTO_CONTACTO, LINEAL, alcanzar),
                en(0.62F, SUAVE, alPecho),
                en(0.78F, SUAVE, alPecho.torso(0.02F, 0.05F).cabeza(0.1F, 0, 0.25F)),
                en(0.9F, SUAVE, alPecho.torso(0.02F, -0.04F).cabeza(0.1F, 0, 0.15F)),
                en(1, SUAVE, REPOSO));
    }
}

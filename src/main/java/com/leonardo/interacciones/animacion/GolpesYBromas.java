// Coreografías de las interacciones que no piden permiso: cachetada, puñetazo, empujón,
// patada en el trasero y nalgada. Las registra CatalogoAnimaciones.
//
// El golpe llega en TipoInteraccion.MOMENTO_CONTACTO (ahí el servidor aplica daño y
// retroceso). Quien lo recibe se queda en reposo hasta justo antes del contacto y reacciona
// de golpe después, así no "adivina" el golpe. En la patada y la nalgada (POR_DETRAS) el
// receptor está de espaldas a quien lo hace.
package com.leonardo.interacciones.animacion;

import static com.leonardo.interacciones.animacion.Fotograma.en;
import static com.leonardo.interacciones.animacion.Postura.REPOSO;
import static com.leonardo.interacciones.animacion.Postura.activa;
import static com.leonardo.interacciones.animacion.Suavizado.ACELERAR;
import static com.leonardo.interacciones.animacion.Suavizado.FRENAR;
import static com.leonardo.interacciones.animacion.Suavizado.LINEAL;
import static com.leonardo.interacciones.animacion.Suavizado.SUAVE;

import com.leonardo.interacciones.dominio.TipoInteraccion;

final class GolpesYBromas {

    private static final float CONTACTO = TipoInteraccion.MOMENTO_CONTACTO;
    /** Último instante en reposo de quien recibe, justo antes del contacto. */
    private static final float ANTES_DEL_GOLPE = CONTACTO - 0.01F;
    /** Cuándo se ve la reacción al golpe (muy poco después del contacto). */
    private static final float IMPACTO = CONTACTO + 0.07F;

    private GolpesYBromas() {
    }

    /** Lleva el brazo hacia fuera y lo barre de lado a lado a la altura de la cara. */
    static Animacion cachetadaQuienDa() {
        Postura cargar = activa().torso(0, -0.05F).cabeza(0, 0, 0).brazoDerecho(-1.45F, 0.7F, 0.2F);
        Postura golpe = activa().torso(0.05F, 0.05F).cabeza(0, 0, 0).brazoDerecho(-1.45F, -0.9F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.3F, FRENAR, cargar),
                en(CONTACTO, ACELERAR, golpe),
                en(0.6F, FRENAR, golpe.brazoDerecho(-1.2F, -1.1F, 0)),
                en(1, SUAVE, REPOSO));
    }

    /** La cara sale girada hacia un lado y luego se lleva la mano a la mejilla. */
    static Animacion cachetadaQuienRecibe() {
        Postura girada = activa().torso(-0.05F, -0.1F).cabeza(0.1F, -0.9F, 0.25F)
                .brazoDerecho(-0.3F, 0, 0.3F).brazoIzquierdo(-0.3F, 0, -0.3F);
        Postura manoEnMejilla = girada.cabeza(0.1F, -0.4F, 0.1F).torso(0, 0).brazoIzquierdo(-2.2F, 0.6F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(ANTES_DEL_GOLPE, LINEAL, REPOSO),
                en(IMPACTO, FRENAR, girada),
                en(0.75F, SUAVE, manoEnMejilla),
                en(1, SUAVE, REPOSO));
    }

    /** Carga el puño atrás con la guardia alta y lo lanza recto a la cara. */
    static Animacion punetazoQuienDa() {
        Postura cargar = activa().torso(-0.05F, 0).brazoDerecho(-0.6F, 0.2F, 0.15F).brazoIzquierdo(-1.2F, 0.3F, 0);
        Postura golpe = activa().torso(0.15F, 0).brazoDerecho(-1.57F, -0.25F, 0).brazoIzquierdo(-0.8F, 0.2F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.3F, FRENAR, cargar),
                en(CONTACTO, ACELERAR, golpe),
                en(0.6F, LINEAL, golpe),
                en(1, SUAVE, REPOSO));
    }

    /** La cabeza sale hacia atrás, los brazos se sueltan, y después se toca la cara. */
    static Animacion punetazoQuienRecibe() {
        Postura impacto = activa().torso(-0.25F, 0).cabeza(-0.6F, 0.15F, 0.1F)
                .brazoDerecho(-0.4F, 0, 0.6F).brazoIzquierdo(-0.4F, 0, -0.6F);
        Postura dolor = activa().torso(0.1F, 0).cabeza(0.2F, 0, 0)
                .brazoDerecho(-2.0F, -0.6F, 0).brazoIzquierdo(-0.2F, 0, -0.1F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(ANTES_DEL_GOLPE, LINEAL, REPOSO),
                en(IMPACTO, FRENAR, impacto),
                en(0.75F, SUAVE, dolor),
                en(1, SUAVE, REPOSO));
    }

    /** Recoge los brazos y empuja con las dos manos. */
    static Animacion empujarQuienDa() {
        Postura recoger = activa().torso(-0.05F, 0).brazoDerecho(-0.9F, -0.2F, 0).brazoIzquierdo(-0.9F, 0.2F, 0);
        Postura empujar = activa().torso(0.2F, 0).brazoDerecho(-1.5F, -0.15F, 0).brazoIzquierdo(-1.5F, 0.15F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.3F, FRENAR, recoger),
                en(CONTACTO, ACELERAR, empujar),
                en(0.6F, LINEAL, empujar),
                en(1, SUAVE, REPOSO));
    }

    /** Sale hacia atrás moviendo los brazos para no caerse. */
    static Animacion empujarQuienRecibe() {
        Postura trastabillar = activa().torso(-0.3F, 0).cabeza(-0.2F, 0, 0)
                .brazoDerecho(-1.2F, 0, 0.5F).brazoIzquierdo(-0.6F, 0, -0.7F);
        Postura equilibrio = trastabillar.brazoDerecho(-0.6F, 0, 0.7F).brazoIzquierdo(-1.2F, 0, -0.5F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(ANTES_DEL_GOLPE, LINEAL, REPOSO),
                en(IMPACTO, FRENAR, trastabillar),
                en(0.68F, SUAVE, equilibrio),
                en(0.82F, SUAVE, trastabillar.torso(-0.05F, 0)),
                en(1, SUAVE, REPOSO));
    }

    /** Pierna atrás para coger impulso y patada alta hacia delante, echando el cuerpo atrás. */
    static Animacion patadaQuienDa() {
        Postura cargar = activa().torso(-0.1F, 0).brazoDerecho(-0.4F, 0, 0.3F).brazoIzquierdo(-0.4F, 0, -0.3F)
                .piernaDerecha(0.6F, 0, 0).piernaIzquierda(0, 0, 0);
        Postura patada = activa().torso(-0.25F, 0).brazoDerecho(-0.6F, 0, 0.6F).brazoIzquierdo(-0.6F, 0, -0.6F)
                .piernaDerecha(-1.45F, 0, 0).piernaIzquierda(0, 0, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.3F, FRENAR, cargar),
                en(CONTACTO, ACELERAR, patada),
                en(0.6F, FRENAR, patada),
                en(1, SUAVE, REPOSO));
    }

    /** De espaldas: da un bote hacia delante con los brazos arriba y luego se agarra el trasero mirando atrás. */
    static Animacion patadaQuienRecibe() {
        Postura bote = activa().torso(-0.3F, 0).cabeza(-0.4F, 0, 0).elevacion(-3)
                .brazoDerecho(-0.3F, 0, 1.2F).brazoIzquierdo(-0.3F, 0, -1.2F)
                .piernaDerecha(0.3F, 0, 0).piernaIzquierda(0.3F, 0, 0);
        Postura dolor = activa().torso(0.15F, 0).cabeza(0.1F, 0.6F, 0)
                .brazoDerecho(0.7F, 0, 0.1F).brazoIzquierdo(0.7F, 0, -0.1F)
                .piernaDerecha(0, 0, 0).piernaIzquierda(0, 0, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(ANTES_DEL_GOLPE, LINEAL, REPOSO),
                en(IMPACTO, FRENAR, bote),
                en(0.72F, SUAVE, dolor),
                en(1, SUAVE, REPOSO));
    }

    /** Levanta la mano y la baja de golpe a la altura de la cadera del otro. */
    static Animacion nalgadaQuienDa() {
        Postura alzar = activa().torso(0.05F, 0).cabeza(0.25F, 0, 0).brazoDerecho(-1.3F, -0.1F, 0);
        Postura golpe = activa().torso(0.12F, 0).cabeza(0.25F, 0, 0).brazoDerecho(-0.9F, -0.2F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.3F, FRENAR, alzar),
                en(CONTACTO, ACELERAR, golpe),
                en(0.6F, FRENAR, golpe.brazoDerecho(-0.75F, -0.2F, 0)),
                en(1, SUAVE, REPOSO));
    }

    /** De espaldas: pega un saltito y se gira indignado con las manos atrás. */
    static Animacion nalgadaQuienRecibe() {
        Postura salto = activa().torso(-0.15F, 0).cabeza(-0.2F, 0, 0).elevacion(-2.5F)
                .brazoDerecho(-0.3F, 0, 0.4F).brazoIzquierdo(-0.3F, 0, -0.4F);
        Postura indignado = activa().torso(0.05F, 0).cabeza(0, 0.7F, 0)
                .brazoDerecho(0.6F, 0, 0.1F).brazoIzquierdo(0.6F, 0, -0.1F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(ANTES_DEL_GOLPE, LINEAL, REPOSO),
                en(IMPACTO, FRENAR, salto),
                en(0.72F, SUAVE, indignado),
                en(1, SUAVE, REPOSO));
    }
}

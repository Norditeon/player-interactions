// Emociones que usan el objeto que el jugador tiene en la mano: comer, practicar con la espada,
// hacer malabares y lanzarlo. Minecraft dibuja el objeto en la mano solo; los momentos en que
// sale volando están en ObjetoLanzado. Las registra CatalogoEmociones.
package com.leonardo.interacciones.animacion;

import static com.leonardo.interacciones.animacion.Animacion.ciclo;
import static com.leonardo.interacciones.animacion.Fotograma.en;
import static com.leonardo.interacciones.animacion.Postura.REPOSO;
import static com.leonardo.interacciones.animacion.Postura.activa;
import static com.leonardo.interacciones.animacion.Suavizado.ACELERAR;
import static com.leonardo.interacciones.animacion.Suavizado.FRENAR;
import static com.leonardo.interacciones.animacion.Suavizado.LINEAL;
import static com.leonardo.interacciones.animacion.Suavizado.SUAVE;

final class EmocionesConObjeto {

    private EmocionesConObjeto() {
    }

    /** Lleva el objeto a la boca y mastica moviendo la cabeza. */
    static Animacion comer() {
        Postura morder = activa().cabeza(0.15F, 0, 0).brazoDerecho(-1.9F, -0.65F, 0);
        return ciclo(SUAVE, morder, morder.cabeza(0.25F, 0, 0).brazoDerecho(-1.75F, -0.6F, 0));
    }

    /** Guardia con el arma en alto y tajo en diagonal hacia abajo, con el paso adelantado. */
    static Animacion practicarEspada() {
        Postura guardia = activa().torso(0, 0).cabeza(0, 0, 0)
                .brazoDerecho(-2.6F, 0.3F, 0.3F).brazoIzquierdo(-0.6F, 0.3F, 0)
                .piernaDerecha(-0.2F, 0, 0).piernaIzquierda(0.2F, 0, 0);
        Postura tajo = guardia.torso(0.2F, -0.05F).cabeza(0.15F, 0, 0).brazoDerecho(-0.6F, -0.7F, 0);
        return Animacion.de(
                en(0, LINEAL, guardia),
                en(MomentosEmocion.TAJO_ESPADA, ACELERAR, tajo),
                en(0.55F, LINEAL, tajo),
                en(1, SUAVE, guardia));
    }

    /** Baja la mano, la sube de golpe soltando el objeto, lo sigue con la mirada y lo atrapa. */
    static Animacion malabar() {
        Postura abajo = activa().cabeza(-0.1F, 0, 0).brazoDerecho(-0.9F, -0.2F, 0);
        Postura soltar = activa().cabeza(-0.4F, 0, 0).brazoDerecho(-1.6F, -0.2F, 0);
        Postura mirarArriba = activa().cabeza(-0.7F, 0, 0).brazoDerecho(-1.2F, -0.2F, 0);
        return Animacion.de(
                en(0, LINEAL, abajo),
                en(ObjetoLanzado.SOLTAR_MALABAR, ACELERAR, soltar),
                en(0.5F, SUAVE, mirarArriba),
                en(ObjetoLanzado.ATRAPAR_MALABAR, SUAVE, soltar.brazoDerecho(-1.3F, -0.2F, 0).cabeza(-0.3F, 0, 0)),
                en(1, FRENAR, abajo));
    }

    /** Brazo atrás por encima del hombro, paso adelante y lanzamiento con todo el cuerpo. */
    static Animacion lanzar() {
        Postura cargar = activa().torso(-0.15F, 0.05F).cabeza(-0.1F, 0, 0)
                .brazoDerecho(-3.3F, 0, 0.2F).brazoIzquierdo(-1.0F, 0.2F, 0)
                .piernaDerecha(0.3F, 0, 0).piernaIzquierda(-0.3F, 0, 0);
        Postura soltar = cargar.torso(0.25F, 0).cabeza(-0.05F, 0, 0)
                .brazoDerecho(-1.4F, -0.2F, 0).brazoIzquierdo(-0.3F, 0, -0.1F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.3F, SUAVE, cargar),
                en(ObjetoLanzado.SOLTAR_LANZAR, ACELERAR, soltar),
                en(0.6F, FRENAR, soltar.torso(0.3F, 0).brazoDerecho(-0.6F, -0.4F, 0)),
                en(1, SUAVE, REPOSO));
    }
}

// Cómo reacciona quien recibe un gesto afectuoso FORZADO (sin haberlo aceptado): en vez de
// devolver el gesto, se sorprende, se resiste o se aparta. Quien lo hace mantiene su
// animación normal. Las registra CatalogoAnimaciones para el papel RECEPTOR con forzada = true.
package com.leonardo.interacciones.animacion;

import static com.leonardo.interacciones.animacion.Fotograma.en;
import static com.leonardo.interacciones.animacion.Postura.REPOSO;
import static com.leonardo.interacciones.animacion.Postura.activa;
import static com.leonardo.interacciones.animacion.Suavizado.FRENAR;
import static com.leonardo.interacciones.animacion.Suavizado.LINEAL;
import static com.leonardo.interacciones.animacion.Suavizado.SUAVE;

final class ReaccionesForzadas {

    private ReaccionesForzadas() {
    }

    /** Se queda tieso con los brazos pegados al cuerpo, aparta la cara y forcejea un poco. */
    static Animacion abrazoForzado() {
        Postura tieso = activa().torso(-0.15F, 0).cabeza(-0.2F, -0.4F, 0)
                .brazoDerecho(0.15F, 0, 0.08F).brazoIzquierdo(0.15F, 0, -0.08F);
        Postura forcejeo = tieso.torso(-0.15F, 0.06F).brazoDerecho(0.15F, 0, 0.2F);
        Postura otroForcejeo = tieso.torso(-0.15F, -0.06F).brazoIzquierdo(0.15F, 0, -0.2F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.15F, FRENAR, tieso),
                en(0.3F, SUAVE, forcejeo),
                en(0.45F, SUAVE, otroForcejeo),
                en(0.6F, SUAVE, forcejeo),
                en(0.75F, SUAVE, otroForcejeo),
                en(0.88F, SUAVE, tieso.cabeza(0, 0.3F, 0)),
                en(1, SUAVE, REPOSO));
    }

    /** Se echa atrás con las manos arriba, sorprendido, y luego se limpia la boca con el brazo. */
    static Animacion besoBocaForzado() {
        Postura sorpresa = activa().torso(-0.3F, 0).cabeza(-0.3F, 0, 0)
                .brazoDerecho(-2.6F, 0, 0.4F).brazoIzquierdo(-2.6F, 0, -0.4F);
        Postura limpiarse = activa().torso(0, 0).cabeza(0.1F, -0.3F, 0)
                .brazoDerecho(-1.9F, -0.9F, 0).brazoIzquierdo(-0.2F, 0, -0.1F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.2F, FRENAR, sorpresa),
                en(0.6F, LINEAL, sorpresa),
                en(0.75F, SUAVE, limpiarse),
                en(0.88F, SUAVE, limpiarse.brazoDerecho(-1.9F, -0.4F, 0)),
                en(1, SUAVE, REPOSO));
    }

    /** Aparta la cara con un respingo y se toca la mejilla, desconcertado. */
    static Animacion besoMejillaForzado() {
        Postura respingo = activa().torso(-0.12F, 0).cabeza(-0.1F, -0.35F, 0)
                .brazoDerecho(-0.3F, 0, 0.1F).brazoIzquierdo(-0.3F, 0, -0.1F);
        Postura tocarse = respingo.cabeza(0.1F, 0.2F, 0.1F).brazoDerecho(-2.1F, -0.6F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.3F, FRENAR, respingo),
                en(0.6F, SUAVE, tocarse),
                en(0.8F, LINEAL, tocarse),
                en(1, SUAVE, REPOSO));
    }

    /** Cruza los brazos molesto y niega con la cabeza mientras le dan palmaditas. */
    static Animacion palmaditasForzadas() {
        Postura molesto = activa().torso(-0.05F, 0).cabeza(0.2F, 0, 0)
                .brazoDerecho(-0.95F, -0.85F, 0).brazoIzquierdo(-0.85F, 0.9F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.2F, FRENAR, molesto),
                en(0.35F, SUAVE, molesto.cabeza(0.2F, 0.3F, 0)),
                en(0.5F, SUAVE, molesto.cabeza(0.2F, -0.3F, 0)),
                en(0.65F, SUAVE, molesto.cabeza(0.2F, 0.3F, 0)),
                en(0.8F, SUAVE, molesto),
                en(1, SUAVE, REPOSO));
    }

    /** Reacción general: se echa atrás, aparta la mirada y se queda incómodo sin devolver el gesto. */
    static Animacion incomodo() {
        Postura apartarse = activa().torso(-0.1F, 0).cabeza(0, -0.3F, 0.1F)
                .brazoDerecho(-0.4F, 0.1F, 0.15F).brazoIzquierdo(0.1F, 0, -0.1F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.2F, FRENAR, apartarse),
                en(0.5F, SUAVE, apartarse.cabeza(0.05F, -0.4F, 0.15F)),
                en(0.8F, SUAVE, apartarse.cabeza(0, -0.2F, 0.05F)),
                en(1, SUAVE, REPOSO));
    }
}

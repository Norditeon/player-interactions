// Curvas de velocidad para ir de un fotograma al siguiente. Son las que dan naturalidad:
// un brazo real no se mueve a velocidad constante, acelera y frena.
// Las usa Animacion; cada Fotograma dice con qué curva se llega a él.
package com.leonardo.interacciones.animacion;

public enum Suavizado {
    /** Velocidad constante: solo para movimientos mecánicos o muy cortos. */
    LINEAL,
    /** Arranca y frena suave: el movimiento corporal por defecto. */
    SUAVE,
    /** Empieza lento y termina rápido: golpes (chocar los cinco, los puños). */
    ACELERAR,
    /** Empieza rápido y frena: rebotes y llegadas. */
    FRENAR;

    public float aplicar(float t) {
        float acotado = Math.max(0, Math.min(1, t));
        return switch (this) {
            case LINEAL -> acotado;
            case SUAVE -> acotado * acotado * (3 - 2 * acotado);
            case ACELERAR -> acotado * acotado * acotado;
            case FRENAR -> 1 - (1 - acotado) * (1 - acotado) * (1 - acotado);
        };
    }
}

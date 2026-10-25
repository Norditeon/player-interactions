// Cómo quedan colocados los dos jugadores durante el gesto.
// La lee InteraccionesEnCurso (servidor) al acercarlos.
package com.leonardo.interacciones.dominio;

public enum Disposicion {
    /** Mirándose de frente (abrazo, beso, puñetazo...). */
    CARA_A_CARA,
    /** Quien hace el gesto queda detrás del otro, que mira hacia delante (patada en el trasero, nalgada). */
    POR_DETRAS
}

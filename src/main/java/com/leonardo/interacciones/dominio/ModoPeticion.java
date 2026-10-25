// Cómo se inicia una interacción: pidiendo consentimiento o forzándola.
// Lo elige el jugador en el centro de la rueda y lo interpreta el servidor.
package com.leonardo.interacciones.dominio;

public enum ModoPeticion {
    /** El otro jugador recibe una solicitud y decide si acepta. */
    PEDIR,
    /** La interacción ocurre sin preguntar (si el servidor lo permite). */
    FORZAR;

    public ModoPeticion alternar() {
        return this == PEDIR ? FORZAR : PEDIR;
    }

    public static ModoPeticion porIndice(int indice) {
        return indice == FORZAR.ordinal() ? FORZAR : PEDIR;
    }
}

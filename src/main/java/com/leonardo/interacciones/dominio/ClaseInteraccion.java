// Qué tipo de interacción es, según cómo se decide si ocurre.
// Lo usan TipoInteraccion, GestorSolicitudes (servidor) y la rueda (cliente, para el color).
package com.leonardo.interacciones.dominio;

public enum ClaseInteraccion {
    /** Gesto afectuoso: pide consentimiento (o se fuerza, si el servidor lo permite). */
    GESTO,
    /** Golpe o broma: nadie lo aceptaría, así que ocurre sin preguntar (si el servidor lo permite). */
    SIN_PERMISO,
    /** Entregar lo que tienes en la mano: siempre pide que el otro lo acepte. No está en la rueda. */
    ENTREGA
}

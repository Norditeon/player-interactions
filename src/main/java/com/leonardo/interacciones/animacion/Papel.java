// Papel de cada jugador en una interacción: quien hace el gesto y quien lo recibe.
// Lo usan CatalogoAnimaciones (cada papel tiene su animación) y AnimacionesActivas (cliente).
package com.leonardo.interacciones.animacion;

public enum Papel {
    /** Quien inicia el gesto (abraza, da palmaditas, besa). */
    ACTOR,
    /** Quien lo recibe. */
    RECEPTOR
}

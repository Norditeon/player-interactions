// Una pose clave en un momento de la animación (0 = inicio, 1 = final) y la curva
// con la que se llega a ella desde el fotograma anterior. Los crea CatalogoAnimaciones.
package com.leonardo.interacciones.animacion;

public record Fotograma(float momento, Suavizado suavizado, Postura postura) {

    public static Fotograma en(float momento, Suavizado suavizado, Postura postura) {
        return new Fotograma(momento, suavizado, postura);
    }
}

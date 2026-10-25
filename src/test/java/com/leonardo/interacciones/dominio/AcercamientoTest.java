package com.leonardo.interacciones.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.leonardo.interacciones.dominio.Acercamiento.Destinos;
import com.leonardo.interacciones.dominio.Acercamiento.Posicion;
import org.junit.jupiter.api.Test;

class AcercamientoTest {

    private static final double TOLERANCIA = 1.0E-9;

    private static double distanciaHorizontal(Posicion a, Posicion b) {
        return Math.hypot(a.x() - b.x(), a.z() - b.z());
    }

    @Test
    void quedanALaDistanciaDeContactoAlrededorDelPuntoMedio() {
        Destinos destinos = Acercamiento.calcular(new Posicion(0, 64, 0), new Posicion(2, 64, 0), 0.6);

        assertEquals(0.6, distanciaHorizontal(destinos.actor(), destinos.receptor()), TOLERANCIA);
        assertEquals(0.7, destinos.actor().x(), TOLERANCIA);
        assertEquals(1.3, destinos.receptor().x(), TOLERANCIA);
    }

    @Test
    void siEstanDemasiadoPegadosSeSeparan() {
        Destinos destinos = Acercamiento.calcular(new Posicion(0, 0, 0), new Posicion(0, 0, 0.2), 1.0);

        assertEquals(-0.4, destinos.actor().z(), TOLERANCIA);
        assertEquals(0.6, destinos.receptor().z(), TOLERANCIA);
    }

    @Test
    void funcionaAunqueEstenEnElMismoPunto() {
        Destinos destinos = Acercamiento.calcular(new Posicion(5, 0, 5), new Posicion(5, 0, 5), 0.8);

        assertEquals(0.8, distanciaHorizontal(destinos.actor(), destinos.receptor()), TOLERANCIA);
    }

    @Test
    void laAlturaDeCadaUnoNoCambia() {
        Destinos destinos = Acercamiento.calcular(new Posicion(0, 64, 0), new Posicion(0, 64.5, 2), 0.6);

        assertEquals(64, destinos.actor().y(), TOLERANCIA);
        assertEquals(64.5, destinos.receptor().y(), TOLERANCIA);
    }

    @Test
    void quienEstaDeEspaldasMiraAlLadoContrarioDeQuienEstaDetras() {
        Posicion opuesto = Acercamiento.puntoOpuesto(new Posicion(0, 64, 0), new Posicion(1, 64, 0.5));

        assertEquals(2, opuesto.x(), TOLERANCIA);
        assertEquals(1, opuesto.z(), TOLERANCIA);
        assertEquals(64, opuesto.y(), TOLERANCIA);
    }

    @Test
    void elTrayectoEmpiezaYTerminaEnSusExtremos() {
        Posicion desde = new Posicion(0, 0, 0);
        Posicion hasta = new Posicion(1, 0, 0);

        assertEquals(0, Acercamiento.intermedia(desde, hasta, 0).x(), TOLERANCIA);
        assertEquals(0.5, Acercamiento.intermedia(desde, hasta, 0.5).x(), TOLERANCIA);
        assertEquals(1, Acercamiento.intermedia(desde, hasta, 1).x(), TOLERANCIA);
        assertEquals(1, Acercamiento.intermedia(desde, hasta, 3).x(), TOLERANCIA);
    }
}

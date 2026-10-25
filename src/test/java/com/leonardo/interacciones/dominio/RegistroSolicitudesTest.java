package com.leonardo.interacciones.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class RegistroSolicitudesTest {

    private final UUID ana = UUID.randomUUID();
    private final UUID beto = UUID.randomUUID();
    private final UUID carla = UUID.randomUUID();
    private final RegistroSolicitudes registro = new RegistroSolicitudes();

    private SolicitudPendiente solicitud(UUID de, UUID para, long caducaEn) {
        return new SolicitudPendiente(de, para, TipoInteraccion.ABRAZAR, caducaEn);
    }

    @Test
    void elObjetivoPuedeAceptarUnaSolicitudVigente() {
        registro.registrar(solicitud(ana, beto, 100), 0);

        var tomada = registro.tomar(beto, 50);

        assertTrue(tomada.isPresent());
        assertEquals(ana, tomada.get().solicitante());
        assertTrue(registro.tomar(beto, 51).isEmpty(), "una solicitud solo se responde una vez");
    }

    @Test
    void unaSolicitudCaducadaNoSePuedeAceptar() {
        registro.registrar(solicitud(ana, beto, 100), 0);

        assertTrue(registro.tomar(beto, 100).isEmpty());
    }

    @Test
    void nadiePuedePisarLaSolicitudDeOtraPersona() {
        registro.registrar(solicitud(ana, beto, 100), 0);

        assertEquals(ResultadoPeticion.OBJETIVO_OCUPADO, registro.registrar(solicitud(carla, beto, 120), 10));
    }

    @Test
    void elMismoSolicitantePuedeCambiarSuSolicitud() {
        registro.registrar(solicitud(ana, beto, 100), 0);

        var nueva = new SolicitudPendiente(ana, beto, TipoInteraccion.BESO_MEJILLA, 120);
        assertEquals(ResultadoPeticion.ACEPTADA, registro.registrar(nueva, 10));
        assertEquals(TipoInteraccion.BESO_MEJILLA, registro.tomar(beto, 20).orElseThrow().tipo());
    }

    @Test
    void trasCaducarOtroJugadorPuedePedir() {
        registro.registrar(solicitud(ana, beto, 100), 0);

        assertEquals(ResultadoPeticion.ACEPTADA, registro.registrar(solicitud(carla, beto, 200), 100));
    }

    @Test
    void elEnfriamientoBloqueaPeticionesSeguidas() {
        registro.anotarUso(ana, 0);

        assertEquals(ResultadoPeticion.EN_ENFRIAMIENTO, registro.comprobarEnfriamiento(ana, 59, 60));
        assertEquals(ResultadoPeticion.ACEPTADA, registro.comprobarEnfriamiento(ana, 60, 60));
        assertEquals(ResultadoPeticion.ACEPTADA, registro.comprobarEnfriamiento(beto, 1, 60));
    }

    @Test
    void retirarCaducadasDevuelveSoloLasVencidas() {
        registro.registrar(solicitud(ana, beto, 100), 0);
        registro.registrar(solicitud(ana, carla, 300), 0);

        var caducadas = registro.retirarCaducadas(150);

        assertEquals(1, caducadas.size());
        assertEquals(beto, caducadas.get(0).objetivo());
        assertTrue(registro.tomar(carla, 150).isPresent());
    }

    @Test
    void alDesconectarseSeCancelanSusSolicitudesEnviadasYRecibidas() {
        registro.registrar(solicitud(ana, beto, 100), 0);
        registro.registrar(solicitud(carla, ana, 100), 0);

        assertEquals(2, registro.olvidarJugador(ana).size());
        assertTrue(registro.tomar(beto, 1).isEmpty());
        assertTrue(registro.tomar(ana, 1).isEmpty());
    }
}

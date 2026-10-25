// Una petición de interacción que espera la respuesta del otro jugador.
// La crea el servidor al recibir una petición en modo PEDIR y la guarda RegistroSolicitudes.
package com.leonardo.interacciones.dominio;

import java.util.UUID;

public record SolicitudPendiente(UUID solicitante, UUID objetivo, TipoInteraccion tipo, long caducaEnTick) {

    public boolean caducada(long tickActual) {
        return tickActual >= caducaEnTick;
    }

    public boolean involucraA(UUID jugador) {
        return solicitante.equals(jugador) || objetivo.equals(jugador);
    }
}

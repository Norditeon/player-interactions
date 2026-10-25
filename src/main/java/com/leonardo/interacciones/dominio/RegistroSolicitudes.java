// Memoria del servidor sobre solicitudes pendientes y enfriamientos entre interacciones.
// Recibe peticiones ya validadas por GestorSolicitudes (servidor) y le responde si se
// pueden registrar, cuál aceptar y cuáles caducaron. Lógica pura: no conoce Minecraft,
// el tiempo llega como "tick actual" para poder testearla.
package com.leonardo.interacciones.dominio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class RegistroSolicitudes {

    /** Como mucho una solicitud pendiente por jugador objetivo: así la respuesta nunca es ambigua. */
    private final Map<UUID, SolicitudPendiente> pendientesPorObjetivo = new HashMap<>();
    private final Map<UUID, Long> ultimoUsoPorSolicitante = new HashMap<>();

    public ResultadoPeticion comprobarEnfriamiento(UUID solicitante, long tickActual, long ticksEnfriamiento) {
        Long ultimoUso = ultimoUsoPorSolicitante.get(solicitante);
        boolean enEspera = ultimoUso != null && tickActual - ultimoUso < ticksEnfriamiento;
        return enEspera ? ResultadoPeticion.EN_ENFRIAMIENTO : ResultadoPeticion.ACEPTADA;
    }

    public void anotarUso(UUID solicitante, long tickActual) {
        ultimoUsoPorSolicitante.put(solicitante, tickActual);
    }

    /**
     * Guarda la solicitud si el objetivo no está esperando la de otra persona.
     * Si el mismo solicitante repite, la nueva sustituye a la anterior.
     */
    public ResultadoPeticion registrar(SolicitudPendiente solicitud, long tickActual) {
        SolicitudPendiente existente = pendientesPorObjetivo.get(solicitud.objetivo());
        boolean deOtraPersona = existente != null
                && !existente.caducada(tickActual)
                && !existente.solicitante().equals(solicitud.solicitante());
        if (deOtraPersona) {
            return ResultadoPeticion.OBJETIVO_OCUPADO;
        }
        pendientesPorObjetivo.put(solicitud.objetivo(), solicitud);
        return ResultadoPeticion.ACEPTADA;
    }

    /** Saca la solicitud que espera respuesta del objetivo; vacía si no hay o ya caducó. */
    public Optional<SolicitudPendiente> tomar(UUID objetivo, long tickActual) {
        SolicitudPendiente solicitud = pendientesPorObjetivo.remove(objetivo);
        if (solicitud == null || solicitud.caducada(tickActual)) {
            return Optional.empty();
        }
        return Optional.of(solicitud);
    }

    public List<SolicitudPendiente> retirarCaducadas(long tickActual) {
        return retirarSi(solicitud -> solicitud.caducada(tickActual));
    }

    /** Al desconectarse un jugador se cancelan sus solicitudes enviadas y recibidas. */
    public List<SolicitudPendiente> olvidarJugador(UUID jugador) {
        ultimoUsoPorSolicitante.remove(jugador);
        return retirarSi(solicitud -> solicitud.involucraA(jugador));
    }

    private List<SolicitudPendiente> retirarSi(java.util.function.Predicate<SolicitudPendiente> condicion) {
        List<SolicitudPendiente> retiradas = new ArrayList<>();
        Iterator<SolicitudPendiente> iterador = pendientesPorObjetivo.values().iterator();
        while (iterador.hasNext()) {
            SolicitudPendiente solicitud = iterador.next();
            if (condicion.test(solicitud)) {
                retiradas.add(solicitud);
                iterador.remove();
            }
        }
        return retiradas;
    }
}

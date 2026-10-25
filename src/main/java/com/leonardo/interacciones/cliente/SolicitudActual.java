// La solicitud que este jugador tiene pendiente de responder (como mucho una).
// La rellena ReceptorCliente al llegar SolicitudRecibida; la leen y vacían AvisoSolicitud.
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.dominio.TipoInteraccion;
import com.leonardo.interacciones.red.SolicitudRecibida;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;

final class SolicitudActual {

    /** objeto: lo que te ofrecen en una entrega; vacío en los gestos de la rueda. */
    record Pendiente(String nombreSolicitante, TipoInteraccion tipo, ItemStack objeto, long caducaEnMilis) {
    }

    private static Pendiente pendiente;

    private SolicitudActual() {
    }

    static void recibir(SolicitudRecibida solicitud) {
        long caduca = System.currentTimeMillis() + solicitud.segundosParaResponder() * 1000L;
        pendiente = new Pendiente(solicitud.nombreSolicitante(), solicitud.tipo(), solicitud.objeto(), caduca);
    }

    /** Vacía si no hay solicitud o si ya pasó el tiempo para responder. */
    static Optional<Pendiente> actual() {
        if (pendiente != null && System.currentTimeMillis() >= pendiente.caducaEnMilis()) {
            pendiente = null;
        }
        return Optional.ofNullable(pendiente);
    }

    static int segundosRestantes() {
        return actual().map(p -> (int) Math.ceil((p.caducaEnMilis() - System.currentTimeMillis()) / 1000.0)).orElse(0);
    }

    static void olvidar() {
        pendiente = null;
    }
}

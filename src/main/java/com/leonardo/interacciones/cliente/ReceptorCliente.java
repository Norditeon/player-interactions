// Entrada de los paquetes servidor → cliente. RegistroRed los entrega aquí (en el hilo
// principal del cliente) y este archivo los reparte a SolicitudActual y AnimacionesActivas.
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.red.DetenerAnimacion;
import com.leonardo.interacciones.red.ReproducirEmocion;
import com.leonardo.interacciones.red.ReproducirInteraccion;
import com.leonardo.interacciones.red.SolicitudRecibida;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public final class ReceptorCliente {

    private ReceptorCliente() {
    }

    public static void alRecibirSolicitud(SolicitudRecibida solicitud) {
        SolicitudActual.recibir(solicitud);
    }

    public static void alReproducir(ReproducirInteraccion interaccion) {
        Entity actor = entidad(interaccion.idEntidadActor());
        Entity receptor = entidad(interaccion.idEntidadReceptor());
        if (actor != null && receptor != null) {
            AnimacionesActivas.iniciarInteraccion(actor, receptor, interaccion.tipo(), interaccion.forzada());
        }
    }

    public static void alReproducirEmocion(ReproducirEmocion emocion) {
        Entity jugador = entidad(emocion.idEntidad());
        if (jugador != null) {
            AnimacionesActivas.iniciarEmocion(jugador, emocion.tipo());
        }
    }

    public static void alDetener(DetenerAnimacion detener) {
        Entity jugador = entidad(detener.idEntidad());
        if (jugador != null) {
            AnimacionesActivas.cortar(jugador);
        }
    }

    private static Entity entidad(int id) {
        ClientLevel nivel = Minecraft.getInstance().level;
        return nivel == null ? null : nivel.getEntity(id);
    }
}

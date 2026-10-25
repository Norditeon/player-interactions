// Da de alta los paquetes del mod y a quién entrega cada uno.
// Servidor: GestorSolicitudes, GestorEntregas y EmocionesEnCurso. Cliente: ReceptorCliente
// (solo se carga en el cliente, por eso se llama dentro de una lambda y nunca por referencia directa).
package com.leonardo.interacciones.red;

import com.leonardo.interacciones.cliente.ReceptorCliente;
import com.leonardo.interacciones.servidor.EmocionesEnCurso;
import com.leonardo.interacciones.servidor.GestorEntregas;
import com.leonardo.interacciones.servidor.GestorSolicitudes;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class RegistroRed {

    /** Sube este número si cambia el formato de algún paquete: así cliente y servidor viejos no se mezclan. */
    private static final String VERSION_PROTOCOLO = "3";

    private RegistroRed() {
    }

    public static void registrar(RegisterPayloadHandlersEvent evento) {
        PayloadRegistrar registro = evento.registrar(VERSION_PROTOCOLO);

        registro.playToServer(PedirInteraccion.TIPO, PedirInteraccion.CODEC,
                (paquete, contexto) -> GestorSolicitudes.alPedir((ServerPlayer) contexto.player(), paquete));
        registro.playToServer(ResponderSolicitud.TIPO, ResponderSolicitud.CODEC,
                (paquete, contexto) -> GestorSolicitudes.alResponder((ServerPlayer) contexto.player(), paquete));
        registro.playToServer(OfrecerObjeto.TIPO, OfrecerObjeto.CODEC,
                (paquete, contexto) -> GestorEntregas.alOfrecer((ServerPlayer) contexto.player(), paquete));
        registro.playToServer(HacerEmocion.TIPO, HacerEmocion.CODEC,
                (paquete, contexto) -> EmocionesEnCurso.alPedirEmocion((ServerPlayer) contexto.player(), paquete.tipo()));
        registro.playToServer(DetenerMiEmocion.TIPO, DetenerMiEmocion.CODEC,
                (paquete, contexto) -> EmocionesEnCurso.alPedirDetener((ServerPlayer) contexto.player()));

        registro.playToClient(SolicitudRecibida.TIPO, SolicitudRecibida.CODEC,
                (paquete, contexto) -> ReceptorCliente.alRecibirSolicitud(paquete));
        registro.playToClient(ReproducirInteraccion.TIPO, ReproducirInteraccion.CODEC,
                (paquete, contexto) -> ReceptorCliente.alReproducir(paquete));
        registro.playToClient(ReproducirEmocion.TIPO, ReproducirEmocion.CODEC,
                (paquete, contexto) -> ReceptorCliente.alReproducirEmocion(paquete));
        registro.playToClient(DetenerAnimacion.TIPO, DetenerAnimacion.CODEC,
                (paquete, contexto) -> ReceptorCliente.alDetener(paquete));
    }
}

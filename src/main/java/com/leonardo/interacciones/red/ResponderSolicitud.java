// Cliente → servidor: "acepto" o "rechazo" la solicitud que tengo pendiente.
// Lo envía AvisoSolicitud al pulsar las teclas; lo procesa GestorSolicitudes.
package com.leonardo.interacciones.red;

import com.leonardo.interacciones.InteraccionesAmistosas;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ResponderSolicitud(boolean acepta) implements CustomPacketPayload {

    public static final Type<ResponderSolicitud> TIPO = new Type<>(InteraccionesAmistosas.recurso("responder"));

    public static final StreamCodec<ByteBuf, ResponderSolicitud> CODEC =
            ByteBufCodecs.BOOL.map(ResponderSolicitud::new, ResponderSolicitud::acepta);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TIPO;
    }
}

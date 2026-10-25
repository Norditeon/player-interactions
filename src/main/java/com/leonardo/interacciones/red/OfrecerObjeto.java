// Cliente → servidor: "quiero darle a este jugador lo que tengo en la mano".
// No dice qué objeto: el servidor lo lee de la mano del jugador (el cliente no es de fiar).
// Lo envía ClicEnJugador; lo procesa GestorEntregas.
package com.leonardo.interacciones.red;

import com.leonardo.interacciones.InteraccionesAmistosas;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record OfrecerObjeto(int idEntidadObjetivo) implements CustomPacketPayload {

    public static final Type<OfrecerObjeto> TIPO = new Type<>(InteraccionesAmistosas.recurso("ofrecer"));

    public static final StreamCodec<ByteBuf, OfrecerObjeto> CODEC =
            ByteBufCodecs.VAR_INT.map(OfrecerObjeto::new, OfrecerObjeto::idEntidadObjetivo);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TIPO;
    }
}

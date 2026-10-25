// Cliente → servidor: "quiero hacer esta interacción con este jugador, de este modo".
// Lo envía la rueda al elegir una opción; lo procesa GestorSolicitudes.
package com.leonardo.interacciones.red;

import com.leonardo.interacciones.InteraccionesAmistosas;
import com.leonardo.interacciones.dominio.ModoPeticion;
import com.leonardo.interacciones.dominio.TipoInteraccion;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PedirInteraccion(int idEntidadObjetivo, TipoInteraccion tipo, ModoPeticion modo)
        implements CustomPacketPayload {

    public static final Type<PedirInteraccion> TIPO = new Type<>(InteraccionesAmistosas.recurso("pedir"));

    public static final StreamCodec<ByteBuf, PedirInteraccion> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PedirInteraccion::idEntidadObjetivo,
            CodecsDominio.TIPO, PedirInteraccion::tipo,
            CodecsDominio.MODO, PedirInteraccion::modo,
            PedirInteraccion::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TIPO;
    }
}

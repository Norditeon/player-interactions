// Servidor → clientes cercanos: "anima a estos dos jugadores con esta interacción".
// Lo envía EjecutorInteraccion; el cliente lo guarda en AnimacionesActivas.
package com.leonardo.interacciones.red;

import com.leonardo.interacciones.InteraccionesAmistosas;
import com.leonardo.interacciones.dominio.TipoInteraccion;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ReproducirInteraccion(int idEntidadActor, int idEntidadReceptor, TipoInteraccion tipo)
        implements CustomPacketPayload {

    public static final Type<ReproducirInteraccion> TIPO = new Type<>(InteraccionesAmistosas.recurso("reproducir"));

    public static final StreamCodec<ByteBuf, ReproducirInteraccion> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ReproducirInteraccion::idEntidadActor,
            ByteBufCodecs.VAR_INT, ReproducirInteraccion::idEntidadReceptor,
            CodecsDominio.TIPO, ReproducirInteraccion::tipo,
            ReproducirInteraccion::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TIPO;
    }
}

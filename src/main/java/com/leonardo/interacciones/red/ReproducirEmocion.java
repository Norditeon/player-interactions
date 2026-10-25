// Servidor → clientes cercanos: "anima a este jugador con esta emoción".
// Lo envía EmocionesEnCurso; el cliente lo guarda en AnimacionesActivas.
package com.leonardo.interacciones.red;

import com.leonardo.interacciones.InteraccionesAmistosas;
import com.leonardo.interacciones.dominio.TipoEmocion;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ReproducirEmocion(int idEntidad, TipoEmocion tipo) implements CustomPacketPayload {

    public static final Type<ReproducirEmocion> TIPO = new Type<>(InteraccionesAmistosas.recurso("reproducir_emocion"));

    public static final StreamCodec<ByteBuf, ReproducirEmocion> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ReproducirEmocion::idEntidad,
            CodecsDominio.EMOCION, ReproducirEmocion::tipo,
            ReproducirEmocion::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TIPO;
    }
}

// Cliente → servidor: "quiero hacer esta emoción". Lo envía la ruleta de emociones;
// lo procesa EmocionesEnCurso, que la valida y la reenvía a los jugadores cercanos.
package com.leonardo.interacciones.red;

import com.leonardo.interacciones.InteraccionesAmistosas;
import com.leonardo.interacciones.dominio.TipoEmocion;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record HacerEmocion(TipoEmocion tipo) implements CustomPacketPayload {

    public static final Type<HacerEmocion> TIPO = new Type<>(InteraccionesAmistosas.recurso("hacer_emocion"));

    public static final StreamCodec<ByteBuf, HacerEmocion> CODEC =
            CodecsDominio.EMOCION.map(HacerEmocion::new, HacerEmocion::tipo);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TIPO;
    }
}

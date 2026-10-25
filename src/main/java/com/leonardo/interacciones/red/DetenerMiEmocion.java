// Cliente → servidor: "corta la emoción que estoy haciendo" (casilla vacía o centro de la
// ruleta). No lleva datos. Lo procesa EmocionesEnCurso.
package com.leonardo.interacciones.red;

import com.leonardo.interacciones.InteraccionesAmistosas;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DetenerMiEmocion() implements CustomPacketPayload {

    public static final DetenerMiEmocion INSTANCIA = new DetenerMiEmocion();
    public static final Type<DetenerMiEmocion> TIPO = new Type<>(InteraccionesAmistosas.recurso("detener_mi_emocion"));
    public static final StreamCodec<ByteBuf, DetenerMiEmocion> CODEC = StreamCodec.unit(INSTANCIA);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TIPO;
    }
}

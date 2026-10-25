// Servidor → clientes cercanos: "corta la animación de este jugador" (se separó a mitad de
// un gesto, cortó su emoción o se movió). Lo envían EjecutorInteraccion y EmocionesEnCurso;
// el cliente lo pasa a AnimacionesActivas.
package com.leonardo.interacciones.red;

import com.leonardo.interacciones.InteraccionesAmistosas;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DetenerAnimacion(int idEntidad) implements CustomPacketPayload {

    public static final Type<DetenerAnimacion> TIPO = new Type<>(InteraccionesAmistosas.recurso("detener_animacion"));

    public static final StreamCodec<ByteBuf, DetenerAnimacion> CODEC =
            ByteBufCodecs.VAR_INT.map(DetenerAnimacion::new, DetenerAnimacion::idEntidad);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TIPO;
    }
}

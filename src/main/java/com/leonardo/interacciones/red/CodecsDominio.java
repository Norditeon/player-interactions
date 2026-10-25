// Cómo viajan los enums del dominio por la red (como su índice entero).
// Lo usan todos los paquetes de este directorio.
package com.leonardo.interacciones.red;

import com.leonardo.interacciones.dominio.ModoPeticion;
import com.leonardo.interacciones.dominio.TipoEmocion;
import com.leonardo.interacciones.dominio.TipoInteraccion;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

final class CodecsDominio {

    static final StreamCodec<ByteBuf, TipoInteraccion> TIPO =
            ByteBufCodecs.idMapper(TipoInteraccion::porIndice, TipoInteraccion::ordinal);

    static final StreamCodec<ByteBuf, TipoEmocion> EMOCION =
            ByteBufCodecs.idMapper(TipoEmocion::porIndice, TipoEmocion::ordinal);

    static final StreamCodec<ByteBuf, ModoPeticion> MODO =
            ByteBufCodecs.idMapper(ModoPeticion::porIndice, ModoPeticion::ordinal);

    private CodecsDominio() {
    }
}

// Servidor → cliente: "este jugador te pide esta interacción; tienes N segundos".
// En las entregas lleva también el objeto ofrecido (vacío en los gestos de la rueda).
// Lo envía GestorSolicitudes; el cliente lo muestra con AvisoSolicitud.
package com.leonardo.interacciones.red;

import com.leonardo.interacciones.InteraccionesAmistosas;
import com.leonardo.interacciones.dominio.TipoInteraccion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public record SolicitudRecibida(String nombreSolicitante, TipoInteraccion tipo, ItemStack objeto, int segundosParaResponder)
        implements CustomPacketPayload {

    public static final Type<SolicitudRecibida> TIPO = new Type<>(InteraccionesAmistosas.recurso("solicitud"));

    // RegistryFriendlyByteBuf: los objetos necesitan los registros del juego para viajar.
    public static final StreamCodec<RegistryFriendlyByteBuf, SolicitudRecibida> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SolicitudRecibida::nombreSolicitante,
            CodecsDominio.TIPO, SolicitudRecibida::tipo,
            ItemStack.OPTIONAL_STREAM_CODEC, SolicitudRecibida::objeto,
            ByteBufCodecs.VAR_INT, SolicitudRecibida::segundosParaResponder,
            SolicitudRecibida::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TIPO;
    }
}

// Todos los mensajes que el servidor muestra a los jugadores (barra de acción, encima
// de la hotbar). Los usan los gestores del servidor; los textos viven en
// assets/interacciones/lang, aquí solo se eligen las claves y los argumentos.
package com.leonardo.interacciones.servidor;

import com.leonardo.interacciones.dominio.ResultadoPeticion;
import com.leonardo.interacciones.dominio.TipoInteraccion;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class AvisosJugador {

    private AvisosJugador() {
    }

    static void resultado(ServerPlayer jugador, ResultadoPeticion resultado) {
        mostrar(jugador, Component.translatable(resultado.claveTraduccion()).withStyle(ChatFormatting.GRAY));
    }

    static void solicitudEnviada(ServerPlayer solicitante, ServerPlayer objetivo, TipoInteraccion tipo) {
        mostrar(solicitante, Component.translatable("interacciones.solicitud_enviada", nombreDe(objetivo), nombreDe(tipo))
                .withStyle(ChatFormatting.GRAY));
    }

    static void rechazada(ServerPlayer solicitante, ServerPlayer objetivo, TipoInteraccion tipo) {
        mostrar(solicitante, Component.translatable("interacciones.rechazada", nombreDe(objetivo), nombreDe(tipo))
                .withStyle(ChatFormatting.GRAY));
    }

    static void sinRespuesta(ServerPlayer solicitante, TipoInteraccion tipo) {
        mostrar(solicitante, Component.translatable("interacciones.sin_respuesta", nombreDe(tipo))
                .withStyle(ChatFormatting.GRAY));
    }

    static void sinSolicitudPendiente(ServerPlayer jugador) {
        mostrar(jugador, Component.translatable("interacciones.sin_solicitud").withStyle(ChatFormatting.GRAY));
    }

    /**
     * Mensaje a ambos cuando la interacción ocurre; si fue forzada, el receptor lo sabe.
     * objeto: lo entregado (segundo argumento del texto), o null en los gestos.
     */
    static void realizada(ServerPlayer actor, ServerPlayer receptor, TipoInteraccion tipo, boolean forzada,
                          Component objeto) {
        Component queCosa = objeto == null ? Component.empty() : objeto;
        mostrar(actor, Component.translatable(tipo.claveTraduccion("hiciste"), nombreDe(receptor), queCosa));
        Component paraReceptor = Component.translatable(tipo.claveTraduccion("recibiste"), nombreDe(actor), queCosa);
        if (forzada) {
            paraReceptor = Component.translatable("interacciones.forzada", paraReceptor);
        }
        mostrar(receptor, paraReceptor);
    }

    private static Component nombreDe(ServerPlayer jugador) {
        return jugador.getDisplayName();
    }

    private static Component nombreDe(TipoInteraccion tipo) {
        return Component.translatable(tipo.claveTraduccion("nombre"));
    }

    private static void mostrar(ServerPlayer jugador, Component mensaje) {
        jugador.displayClientMessage(mensaje, true);
    }
}

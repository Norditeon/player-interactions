// Teclas del mod (configurables en Opciones → Controles): responder a una solicitud y abrir
// la ruleta de emociones. Las registra RegistroCliente; las leen cada tick AvisoSolicitud y
// AbrirRuedaEmociones.
package com.leonardo.interacciones.cliente;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

final class TeclasInteraccion {

    private static final String CATEGORIA = "key.categories.interacciones";

    static final KeyMapping ACEPTAR = crear("key.interacciones.aceptar", GLFW.GLFW_KEY_Y);
    static final KeyMapping RECHAZAR = crear("key.interacciones.rechazar", GLFW.GLFW_KEY_N);
    static final KeyMapping RUEDA_EMOCIONES = crear("key.interacciones.ruleta_emociones", GLFW.GLFW_KEY_G);

    private TeclasInteraccion() {
    }

    private static KeyMapping crear(String nombre, int teclaPorDefecto) {
        // IN_GAME: no se disparan mientras escribes en el chat o hay un menú abierto.
        return new KeyMapping(nombre, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, teclaPorDefecto, CATEGORIA);
    }
}

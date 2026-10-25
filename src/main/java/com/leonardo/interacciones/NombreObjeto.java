// Cómo se nombra un objeto en los textos del mod: "3× Diamante", o solo "Amapola" si es uno.
// Lo usan GestorEntregas (mensajes del servidor) y AvisoSolicitud (aviso en el cliente).
package com.leonardo.interacciones;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class NombreObjeto {

    private NombreObjeto() {
    }

    /** Vacío si no hay objeto (los gestos de la rueda no llevan). */
    public static Component describir(ItemStack objeto) {
        if (objeto.isEmpty()) {
            return Component.empty();
        }
        return objeto.getCount() > 1
                ? Component.literal(objeto.getCount() + "× ").append(objeto.getHoverName())
                : objeto.getHoverName();
    }
}

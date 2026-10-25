// Abre la ruleta de emociones (PantallaRuedaEmociones) al pulsar su tecla, G por defecto.
// Comprueba la tecla cada tick del cliente; solo actúa si no hay otra pantalla abierta.
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.InteraccionesAmistosas;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = InteraccionesAmistosas.ID, value = Dist.CLIENT)
public final class AbrirRuedaEmociones {

    private AbrirRuedaEmociones() {
    }

    @SubscribeEvent
    public static void alTerminarTick(ClientTickEvent.Post evento) {
        Minecraft cliente = Minecraft.getInstance();
        while (TeclasInteraccion.RUEDA_EMOCIONES.consumeClick()) {
            if (cliente.screen == null && cliente.player != null) {
                cliente.setScreen(new PantallaRuedaEmociones());
            }
        }
    }
}

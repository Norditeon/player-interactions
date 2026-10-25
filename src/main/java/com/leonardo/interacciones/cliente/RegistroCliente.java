// Altas que el cliente hace al arrancar: teclas de respuesta y la capa del HUD del aviso.
// NeoForge llama a estos métodos durante la carga del mod (bus del mod, solo cliente).
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.InteraccionesAmistosas;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = InteraccionesAmistosas.ID, value = Dist.CLIENT)
public final class RegistroCliente {

    private RegistroCliente() {
    }

    @SubscribeEvent
    public static void alRegistrarTeclas(RegisterKeyMappingsEvent evento) {
        evento.register(TeclasInteraccion.ACEPTAR);
        evento.register(TeclasInteraccion.RECHAZAR);
        evento.register(TeclasInteraccion.RUEDA_EMOCIONES);
    }

    @SubscribeEvent
    public static void alRegistrarCapas(RegisterGuiLayersEvent evento) {
        evento.registerAboveAll(InteraccionesAmistosas.recurso("aviso_solicitud"), AvisoSolicitud::dibujar);
    }
}

// Punto de entrada del mod: NeoForge lo crea al arrancar (cliente y servidor).
// Registra la configuración, los paquetes de red y los eventos del servidor.
// La parte visual se engancha sola desde el paquete cliente (@EventBusSubscriber).
package com.leonardo.interacciones;

import com.leonardo.interacciones.red.RegistroRed;
import com.leonardo.interacciones.servidor.EmocionesEnCurso;
import com.leonardo.interacciones.servidor.GestorSolicitudes;
import com.leonardo.interacciones.servidor.InteraccionesEnCurso;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(InteraccionesAmistosas.ID)
public final class InteraccionesAmistosas {

    public static final String ID = "interacciones";

    public InteraccionesAmistosas(IEventBus busDelMod, ModContainer contenedor) {
        // SERVER: cada servidor decide sus reglas y NeoForge se las sincroniza a los clientes.
        contenedor.registerConfig(ModConfig.Type.SERVER, Configuracion.ESPECIFICACION);
        // CLIENT: preferencias de cada jugador (su ruleta); un servidor dedicado no la carga.
        contenedor.registerConfig(ModConfig.Type.CLIENT, ConfiguracionCliente.ESPECIFICACION);
        busDelMod.addListener(RegistroRed::registrar);
        NeoForge.EVENT_BUS.register(GestorSolicitudes.class);
        NeoForge.EVENT_BUS.register(InteraccionesEnCurso.class);
        NeoForge.EVENT_BUS.register(EmocionesEnCurso.class);
    }

    public static ResourceLocation recurso(String ruta) {
        return ResourceLocation.fromNamespaceAndPath(ID, ruta);
    }
}

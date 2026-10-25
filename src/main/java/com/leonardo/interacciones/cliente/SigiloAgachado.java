// Mientras tu jugador hace la emoción SIGILO, mantiene pulsado "agacharse" por ti: caminas
// lento, no te caes de los bordes y los demás te ven agachado, como en Minecraft normal.
// Lo hace en el evento en que Minecraft lee las teclas de movimiento (solo tu propio jugador).
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.InteraccionesAmistosas;
import com.leonardo.interacciones.dominio.TipoEmocion;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

@EventBusSubscriber(modid = InteraccionesAmistosas.ID, value = Dist.CLIENT)
public final class SigiloAgachado {

    private SigiloAgachado() {
    }

    @SubscribeEvent
    public static void alLeerMovimiento(MovementInputUpdateEvent evento) {
        if (AnimacionesActivas.emocionDe(evento.getEntity()).filter(tipo -> tipo == TipoEmocion.SIGILO).isPresent()) {
            evento.getInput().shiftKeyDown = true;
        }
    }
}

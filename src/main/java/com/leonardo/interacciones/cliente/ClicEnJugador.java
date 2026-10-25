// Decide qué hace el clic derecho sobre otro jugador:
//   - con la mano vacía: abre PantallaRueda (gestos);
//   - con un objeto (sin agacharse): se lo ofrece (OfrecerObjeto → GestorEntregas).
// Agachado con un objeto no hace nada especial, así el objeto se puede usar como siempre.
// Escucha el evento de interacción de NeoForge en el cliente; el servidor decide el resto.
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.InteraccionesAmistosas;
import com.leonardo.interacciones.red.OfrecerObjeto;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = InteraccionesAmistosas.ID, value = Dist.CLIENT)
public final class ClicEnJugador {

    private ClicEnJugador() {
    }

    @SubscribeEvent
    public static void alInteractuarConEntidad(PlayerInteractEvent.EntityInteract evento) {
        Minecraft cliente = Minecraft.getInstance();
        Player yo = evento.getEntity();
        boolean esElJugadorLocal = evento.getLevel().isClientSide() && yo == cliente.player;
        if (!esElJugadorLocal || evento.getHand() != InteractionHand.MAIN_HAND
                || !(evento.getTarget() instanceof Player objetivo) || objetivo.isSpectator()) {
            return;
        }
        if (yo.getMainHandItem().isEmpty()) {
            cliente.setScreen(new PantallaRueda(objetivo));
        } else if (!yo.isShiftKeyDown()) {
            PacketDistributor.sendToServer(new OfrecerObjeto(objetivo.getId()));
        } else {
            return;
        }
        evento.setCanceled(true);
        evento.setCancellationResult(InteractionResult.SUCCESS);
    }
}

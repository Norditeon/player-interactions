// Deja de dibujar el objeto en la mano mientras está en el aire (malabar) o ya se lanzó.
// Engancha el principio de ItemInHandLayer.renderArmWithItem, que dibuja lo que lleva cada
// mano; NeoForge no tiene un evento ahí. Quién decide es ObjetoEnElAire. Declarado en
// interacciones.mixins.json (solo cliente).
package com.leonardo.interacciones.mixin;

import com.leonardo.interacciones.cliente.ObjetoEnElAire;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class ObjetoEnManoMixin {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void interacciones$ocultarSiVuela(LivingEntity entidad, ItemStack objeto, ItemDisplayContext contexto,
                                              HumanoidArm brazo, PoseStack pila, MultiBufferSource buferes,
                                              int luz, CallbackInfo info) {
        if (ObjetoEnElAire.ocultarEnLaMano(entidad, brazo)) {
            info.cancel();
        }
    }
}

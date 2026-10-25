// Engancha PosesInteraccion a PlayerModel.setupAnim, donde Minecraft coloca brazos y cabeza
// en cada fotograma: al principio reinicia el modelo y al final aplica el gesto. NeoForge no
// ofrece eventos en esos puntos, por eso hace falta un mixin. Declarado en
// interacciones.mixins.json (solo cliente).
package com.leonardo.interacciones.mixin;

import com.leonardo.interacciones.cliente.PosesInteraccion;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PoseJugadorMixin {

    private static final String SETUP_ANIM = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V";

    @Inject(method = SETUP_ANIM, at = @At("HEAD"))
    private void interacciones$reiniciarPose(LivingEntity entidad, float balanceo, float cantidadBalanceo,
                                             float edadEnTicks, float giroCabeza, float inclinacionCabeza,
                                             CallbackInfo info) {
        PosesInteraccion.reiniciar((PlayerModel<?>) (Object) this);
    }

    @Inject(method = SETUP_ANIM, at = @At("TAIL"))
    private void interacciones$aplicarPose(LivingEntity entidad, float balanceo, float cantidadBalanceo,
                                           float edadEnTicks, float giroCabeza, float inclinacionCabeza,
                                           CallbackInfo info) {
        PosesInteraccion.aplicar((PlayerModel<?>) (Object) this, entidad, edadEnTicks);
    }
}

// El objeto de la mano cuando sale volando en una emoción (ObjetoLanzado): lo dibuja en el
// aire durante el malabar y le dice al mixin ObjetoEnManoMixin cuándo no dibujarlo en la mano
// (en el aire, o ya lanzado). Lee la emoción en curso de AnimacionesActivas.
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.InteraccionesAmistosas;
import com.leonardo.interacciones.animacion.ObjetoLanzado;
import com.leonardo.interacciones.dominio.TipoEmocion;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.OptionalDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

@EventBusSubscriber(modid = InteraccionesAmistosas.ID, value = Dist.CLIENT)
public final class ObjetoEnElAire {

    /** Dónde está la mano respecto a los pies (bloques): a la derecha, delante y a la altura del pecho. */
    private static final float A_LA_DERECHA = 0.35F;
    private static final float DELANTE = 0.45F;
    private static final float ALTURA_MANO = 1.25F;
    private static final float ESCALA = 0.5F;
    private static final float GIRO_POR_TICK = 0.3F;

    private ObjetoEnElAire() {
    }

    /** Lo usa el mixin: true si el objeto de esta mano no debe dibujarse ahora mismo. */
    public static boolean ocultarEnLaMano(LivingEntity entidad, HumanoidArm brazo) {
        if (!(entidad instanceof Player jugador) || brazo != jugador.getMainArm()) {
            return false;
        }
        return AnimacionesActivas.poseDe(jugador, jugador.tickCount)
                .filter(pose -> pose.emocion() != null)
                .map(pose -> ObjetoLanzado.manoSinObjeto(pose.emocion(), pose.progreso()))
                .orElse(false);
    }

    @SubscribeEvent
    public static void despuesDeDibujar(RenderPlayerEvent.Post evento) {
        Player jugador = evento.getEntity();
        ItemStack objeto = jugador.getMainHandItem();
        float edad = jugador.tickCount + evento.getPartialTick();
        var pose = AnimacionesActivas.poseDe(jugador, edad).filter(p -> p.emocion() == TipoEmocion.MALABAR);
        if (objeto.isEmpty() || pose.isEmpty()) {
            return;
        }
        OptionalDouble altura = ObjetoLanzado.alturaMalabar(pose.get().progreso());
        if (altura.isPresent()) {
            float giroCuerpo = Mth.rotLerp(evento.getPartialTick(), jugador.yBodyRotO, jugador.yBodyRot);
            dibujar(evento, objeto, giroCuerpo, (float) altura.getAsDouble(), edad);
        }
    }

    private static void dibujar(RenderPlayerEvent.Post evento, ItemStack objeto, float giroCuerpo, float altura, float edad) {
        float radianes = giroCuerpo * Mth.DEG_TO_RAD;
        // Delante = (-sen, cos) del giro; derecha = (-cos, -sen): así lo hace Minecraft.
        float x = -Mth.sin(radianes) * DELANTE - Mth.cos(radianes) * A_LA_DERECHA;
        float z = Mth.cos(radianes) * DELANTE - Mth.sin(radianes) * A_LA_DERECHA;
        PoseStack pila = evento.getPoseStack();
        pila.pushPose();
        pila.translate(x, ALTURA_MANO + altura, z);
        pila.scale(ESCALA, ESCALA, ESCALA);
        pila.mulPose(Axis.YP.rotation(edad * GIRO_POR_TICK));
        Minecraft.getInstance().getItemRenderer().renderStatic(objeto, ItemDisplayContext.GROUND,
                evento.getPackedLight(), OverlayTexture.NO_OVERLAY, pila, evento.getMultiBufferSource(),
                evento.getEntity().level(), evento.getEntity().getId());
        pila.popPose();
    }
}

// Mueve el cuerpo entero del jugador al dibujarlo: subirlo o bajarlo (saltar, sentarse,
// arrodillarse), girarlo sobre sí mismo (giro) o darle la vuelta hacia atrás (mortal,
// acostarse). Se hace en RenderPlayerEvent, antes de que Minecraft dibuje el modelo, para que
// armadura, capa y objeto en la mano lo sigan igual. Lee la pose de AnimacionesActivas.
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.InteraccionesAmistosas;
import com.leonardo.interacciones.animacion.Postura;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

@EventBusSubscriber(modid = InteraccionesAmistosas.ID, value = Dist.CLIENT)
public final class TransformacionCuerpo {

    /** Altura de la cintura en bloques: el punto alrededor del que gira la voltereta. */
    private static final float ALTURA_CINTURA = 0.9F;
    private static final float PIXELES_POR_BLOQUE = 16;
    /** Por cada jugador dibujado, si antes se apiló una transformación que hay que deshacer después. */
    private static final Deque<Boolean> APILADAS = new ArrayDeque<>();

    private TransformacionCuerpo() {
    }

    /** LOWEST: si otro mod cancela el dibujo, lo hace antes y aquí ni se apila nada (no habría Post). */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void antesDeDibujar(RenderPlayerEvent.Pre evento) {
        Player jugador = evento.getEntity();
        float parcial = evento.getPartialTick();
        var pose = AnimacionesActivas.poseDe(jugador, jugador.tickCount + parcial).filter(AnimacionesActivas.Pose::mueveElCuerpo);
        APILADAS.push(pose.isPresent());
        pose.ifPresent(actual -> {
            evento.getPoseStack().pushPose();
            float giroCuerpo = Mth.rotLerp(parcial, jugador.yBodyRotO, jugador.yBodyRot);
            aplicar(evento.getPoseStack(), actual.postura(), actual.intensidad(), giroCuerpo);
        });
    }

    @SubscribeEvent
    public static void despuesDeDibujar(RenderPlayerEvent.Post evento) {
        if (!APILADAS.isEmpty() && APILADAS.pop()) {
            evento.getPoseStack().popPose();
        }
    }

    /**
     * La elevación se escala con el peso (sube y baja suave); los giros, solo con la entrada y
     * salida: una vuelta termina en 2π y escalarla haría que el cuerpo "desgirara" al acabar.
     */
    private static void aplicar(PoseStack pila, Postura postura, float intensidad, float giroCuerpo) {
        pila.translate(0, -postura.elevacion() * postura.peso() / PIXELES_POR_BLOQUE, 0);
        // Se entra en el sistema del cuerpo (mirando hacia donde mira) para que "atrás" sea su atrás.
        pila.mulPose(Axis.YP.rotationDegrees(180.0F - giroCuerpo));
        pila.mulPose(Axis.YP.rotation(postura.vuelta() * intensidad));
        pila.translate(0, ALTURA_CINTURA, 0);
        pila.mulPose(Axis.XP.rotation(postura.voltereta() * intensidad));
        pila.translate(0, -ALTURA_CINTURA, 0);
        pila.mulPose(Axis.YP.rotationDegrees(giroCuerpo - 180.0F));
    }
}

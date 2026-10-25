// Aplica al modelo del jugador la pose que toca según su animación en curso (gesto o emoción).
// La llama el mixin PoseJugadorMixin en dos momentos de PlayerModel.setupAnim:
//   - al principio (reiniciar): devuelve cada pieza a su pose inicial. El modelo es UNO
//     compartido por todos los jugadores y Minecraft no reinicia todo (p. ej. el ladeo de la
//     cabeza), así que sin esto un gesto se quedaba "pegado" en ese y en otros jugadores.
//   - al final (aplicar): mezcla la pose de Minecraft con la del gesto según su peso.
// El movimiento del cuerpo entero (elevación, vuelta, voltereta) no va aquí: lo hace
// TransformacionCuerpo al dibujar, para que lo sigan también la armadura y el objeto en la mano.
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.animacion.Giro;
import com.leonardo.interacciones.animacion.Postura;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public final class PosesInteraccion {

    /** Medidas del modelo en píxeles (16 = un bloque): largo del torso y altura del hombro bajo el cuello. */
    private static final float LARGO_TORSO = 12;
    private static final float ALTURA_HOMBRO = 2;

    private PosesInteraccion() {
    }

    public static void reiniciar(PlayerModel<?> m) {
        for (ModelPart parte : new ModelPart[]{m.head, m.hat, m.body, m.jacket, m.rightArm, m.leftArm,
                m.rightSleeve, m.leftSleeve, m.rightLeg, m.leftLeg, m.rightPants, m.leftPants}) {
            parte.resetPose();
        }
    }

    public static void aplicar(PlayerModel<?> modelo, LivingEntity entidad, float edadEnTicks) {
        // La mano en primera persona llama a setupAnim con todo a 0: ahí no se anima nada.
        if (edadEnTicks == 0) {
            return;
        }
        AnimacionesActivas.poseDe(entidad, edadEnTicks).ifPresent(pose -> {
            Postura postura = pose.postura();
            float peso = postura.peso();
            if (peso <= 0) {
                return;
            }
            inclinarTorso(modelo, postura.torso(), peso);
            girar(modelo.head, postura.cabeza(), peso);
            girar(modelo.rightArm, postura.brazoDerecho(), peso);
            girar(modelo.leftArm, postura.brazoIzquierdo(), peso);
            girar(modelo.rightLeg, postura.piernaDerecha(), peso);
            girar(modelo.leftLeg, postura.piernaIzquierda(), peso);
            copiarCapasExteriores(modelo);
        });
    }

    /**
     * El torso de Minecraft gira desde el cuello, lo que desplazaría la cadera. Para inclinarse
     * "desde la cintura" se mueve el cuello hacia delante y abajo, y con él cabeza y hombros.
     */
    private static void inclinarTorso(PlayerModel<?> m, Giro torso, float peso) {
        if (torso == null || m.crouching) {
            return;
        }
        float inclinacion = torso.x() * peso;
        float bajada = LARGO_TORSO * (1 - Mth.cos(inclinacion));
        float avance = -LARGO_TORSO * Mth.sin(inclinacion);
        m.body.xRot += inclinacion;
        m.body.zRot += torso.z() * peso;
        for (ModelPart parte : new ModelPart[]{m.body, m.head}) {
            parte.y += bajada;
            parte.z += avance;
        }
        for (ModelPart brazo : new ModelPart[]{m.rightArm, m.leftArm}) {
            brazo.y += bajada + ALTURA_HOMBRO * (Mth.cos(inclinacion) - 1);
            brazo.z += avance + ALTURA_HOMBRO * Mth.sin(inclinacion);
        }
    }

    private static void girar(ModelPart parte, Giro giro, float peso) {
        if (giro == null) {
            return;
        }
        parte.xRot = Mth.lerp(peso, parte.xRot, giro.x());
        parte.yRot = Mth.lerp(peso, parte.yRot, giro.y());
        parte.zRot = Mth.lerp(peso, parte.zRot, giro.z());
    }

    /** Las capas exteriores de la skin siguen a su pieza: Minecraft las copió antes de nuestro cambio. */
    private static void copiarCapasExteriores(PlayerModel<?> m) {
        m.hat.copyFrom(m.head);
        m.jacket.copyFrom(m.body);
        m.rightSleeve.copyFrom(m.rightArm);
        m.leftSleeve.copyFrom(m.leftArm);
        m.rightPants.copyFrom(m.rightLeg);
        m.leftPants.copyFrom(m.leftLeg);
    }
}

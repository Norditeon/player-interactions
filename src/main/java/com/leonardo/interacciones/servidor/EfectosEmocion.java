// Partículas, sonidos y efectos reales de cada emoción mientras dura: lágrimas al llorar,
// nube de lluvia al estar derrotado, notas al bailar, migas al comer, lanzar de verdad el
// objeto de la mano... Lo llama EmocionesEnCurso cada tick con los ticks transcurridos.
// Los momentos exactos (golpe al suelo, tajo, beso) vienen de MomentosEmocion y ObjetoLanzado,
// los mismos que usan las coreografías, para que efecto y animación vayan a la vez.
package com.leonardo.interacciones.servidor;

import com.leonardo.interacciones.animacion.MomentosEmocion;
import com.leonardo.interacciones.animacion.ObjetoLanzado;
import com.leonardo.interacciones.dominio.TipoEmocion;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

final class EfectosEmocion {

    /** Altura de los ojos y de la coronilla sobre los pies, en bloques. */
    private static final double ALTURA_OJOS = 1.55;
    private static final double SOBRE_LA_CABEZA = 2.15;
    /** Velocidad con la que sale el objeto lanzado (bloques por tick). */
    private static final double FUERZA_LANZAMIENTO = 1.1;
    private static final int TICKS_ANTES_DE_PODER_RECOGERLO = 40;

    private EfectosEmocion() {
    }

    static void alAvanzar(ServerPlayer jugador, TipoEmocion tipo, int ticks) {
        switch (tipo) {
            case LLORAR -> cada(ticks, 4, () -> lagrimas(jugador));
            case SUPLICAR -> cada(ticks, 12, () -> lagrimas(jugador));
            case ENOJARSE, BRAZOS_CRUZADOS -> cada(ticks, 15, () -> sobreLaCabeza(jugador, ParticleTypes.ANGRY_VILLAGER, 1));
            case PATALEAR -> patalear(jugador, ticks);
            case GOLPEAR_SUELO -> golpearSuelo(jugador, tipo, ticks);
            case ENAMORADO -> cada(ticks, 8, () -> sobreLaCabeza(jugador, ParticleTypes.HEART, 1));
            case TIMIDEZ, POSE_SEXY -> cada(ticks, 25, () -> sobreLaCabeza(jugador, ParticleTypes.HEART, 1));
            case OFRECER_FLOR -> cada(ticks, 15, () -> sobreLaCabeza(jugador, ParticleTypes.HEART, 1));
            case BESO_AL_AIRE -> enMomento(tipo, ticks, MomentosEmocion.SALE_EL_BESO,
                    () -> delante(jugador, 0.8, ALTURA_OJOS, ParticleTypes.HEART, 5));
            case REIR, SALTAR_ALEGRIA -> cada(ticks, 10, () -> sobreLaCabeza(jugador, ParticleTypes.HAPPY_VILLAGER, 2));
            case CELEBRAR -> cada(ticks, 5, () -> sobreLaCabeza(jugador, ParticleTypes.TOTEM_OF_UNDYING, 3));
            case BAILAR, BAILE_ROBOT, PERREO -> cada(ticks, 10, () -> notaMusical(jugador));
            case PENSAR -> cada(ticks, 8, () -> sobreLaCabeza(jugador, ParticleTypes.ENCHANT, 4));
            case MEDITAR -> cada(ticks, 4, () -> alrededor(jugador, 0.8, ParticleTypes.ENCHANT, 3));
            case POSE_HEROE -> cada(ticks, 10, () -> alrededor(jugador, 1.0, ParticleTypes.END_ROD, 1));
            case LLUVIA_DERROTADO -> lluviaPropia(jugador, ticks);
            case ENTRENAR_PUNOS -> cada(ticks, tipo.duracionTicks() / 2, () -> delante(jugador, 0.7, 1.3, ParticleTypes.CRIT, 3));
            case PRACTICAR_ESPADA -> enMomento(tipo, ticks, MomentosEmocion.TAJO_ESPADA, () -> tajo(jugador));
            case COMER -> cada(ticks, 4, () -> comer(jugador));
            case MORTAL -> enMomento(tipo, ticks, MomentosEmocion.ATERRIZA_MORTAL,
                    () -> aLosPies(jugador, ParticleTypes.POOF, 8));
            case GIRO -> cada(ticks, 6, () -> aLosPies(jugador, ParticleTypes.CLOUD, 2));
            case LANZAR -> enMomento(tipo, ticks, ObjetoLanzado.SOLTAR_LANZAR, () -> lanzarObjeto(jugador));
            default -> {
            }
        }
    }

    private static void cada(int ticks, int intervalo, Runnable efecto) {
        if (ticks % intervalo == 0) {
            efecto.run();
        }
    }

    /** En las cíclicas el momento se repite en cada vuelta; en las demás ocurre una vez. */
    private static void enMomento(TipoEmocion tipo, int ticks, float momento, Runnable efecto) {
        int tickDelMomento = Math.round(tipo.duracionTicks() * momento);
        int tickEnLaVuelta = tipo.ciclica() ? ticks % tipo.duracionTicks() : ticks;
        if (tickEnLaVuelta == tickDelMomento) {
            efecto.run();
        }
    }

    /** Dos gotas que caen desde los ojos, una a cada lado de la cara. */
    private static void lagrimas(ServerPlayer jugador) {
        Vec3 delante = direccionHorizontal(jugador).scale(0.28);
        Vec3 lado = new Vec3(-delante.z, 0, delante.x).normalize().scale(0.12);
        for (Vec3 ojo : new Vec3[]{lado, lado.reverse()}) {
            Vec3 punto = jugador.position().add(delante).add(ojo).add(0, ALTURA_OJOS, 0);
            jugador.serverLevel().sendParticles(ParticleTypes.FALLING_WATER, punto.x, punto.y, punto.z, 1, 0, 0, 0, 0);
        }
    }

    /** Una nube sobre la cabeza que le llueve solo a él. */
    private static void lluviaPropia(ServerPlayer jugador, int ticks) {
        ServerLevel nivel = jugador.serverLevel();
        cada(ticks, 2, () -> nivel.sendParticles(ParticleTypes.FALLING_WATER,
                jugador.getX(), jugador.getY() + 2.6, jugador.getZ(), 3, 0.35, 0.05, 0.35, 0));
        cada(ticks, 10, () -> nivel.sendParticles(ParticleTypes.CLOUD,
                jugador.getX(), jugador.getY() + 2.8, jugador.getZ(), 4, 0.4, 0.05, 0.4, 0));
    }

    private static void patalear(ServerPlayer jugador, int ticks) {
        cada(ticks, 6, () -> aLosPies(jugador, ParticleTypes.POOF, 2));
        cada(ticks, 20, () -> sobreLaCabeza(jugador, ParticleTypes.ANGRY_VILLAGER, 1));
    }

    /** Cada puñetazo al suelo levanta trocitos del bloque que pisa. */
    private static void golpearSuelo(ServerPlayer jugador, TipoEmocion tipo, int ticks) {
        Runnable golpe = () -> {
            BlockState suelo = jugador.serverLevel().getBlockState(jugador.blockPosition().below());
            if (!suelo.isAir()) {
                delante(jugador, 0.5, 0.1, new BlockParticleOption(ParticleTypes.BLOCK, suelo), 10);
            }
            sonar(jugador, SoundEvents.PLAYER_ATTACK_WEAK, 0.8F);
        };
        enMomento(tipo, ticks, MomentosEmocion.PRIMER_GOLPE_AL_SUELO, golpe);
        enMomento(tipo, ticks, MomentosEmocion.SEGUNDO_GOLPE_AL_SUELO, golpe);
        cada(ticks, 20, () -> sobreLaCabeza(jugador, ParticleTypes.ANGRY_VILLAGER, 1));
    }

    private static void tajo(ServerPlayer jugador) {
        delante(jugador, 1.0, 1.1, ParticleTypes.SWEEP_ATTACK, 1);
        sonar(jugador, SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F);
    }

    /** Migas del objeto en la boca y el sonido de masticar; no lo gasta, es de mentira. */
    private static void comer(ServerPlayer jugador) {
        ItemStack enMano = jugador.getMainHandItem();
        if (!enMano.isEmpty()) {
            delante(jugador, 0.35, ALTURA_OJOS - 0.15, new ItemParticleOption(ParticleTypes.ITEM, enMano), 3);
        }
        sonar(jugador, SoundEvents.GENERIC_EAT, 0.5F + jugador.getRandom().nextFloat() * 0.3F);
    }

    /** Lanza de verdad una unidad del objeto de la mano, lejos, hacia donde mira. */
    private static void lanzarObjeto(ServerPlayer jugador) {
        ItemStack enMano = jugador.getMainHandItem();
        if (enMano.isEmpty()) {
            return;
        }
        ItemEntity objeto = new ItemEntity(jugador.serverLevel(),
                jugador.getX(), jugador.getEyeY() - 0.3, jugador.getZ(), enMano.split(1));
        objeto.setDeltaMovement(jugador.getLookAngle().scale(FUERZA_LANZAMIENTO).add(0, 0.2, 0));
        objeto.setPickUpDelay(TICKS_ANTES_DE_PODER_RECOGERLO);
        objeto.setThrower(jugador);
        jugador.serverLevel().addFreshEntity(objeto);
        sonar(jugador, SoundEvents.SNOWBALL_THROW, 0.6F);
    }

    /** Las notas de Minecraft usan la velocidad X como color: cada una sale de un tono distinto. */
    private static void notaMusical(ServerPlayer jugador) {
        double color = jugador.getRandom().nextInt(25) / 24.0;
        jugador.serverLevel().sendParticles(ParticleTypes.NOTE,
                jugador.getX(), jugador.getY() + SOBRE_LA_CABEZA, jugador.getZ(), 0, color, 0, 0, 1);
    }

    private static void sobreLaCabeza(ServerPlayer jugador, ParticleOptions particula, int cantidad) {
        jugador.serverLevel().sendParticles(particula,
                jugador.getX(), jugador.getY() + SOBRE_LA_CABEZA, jugador.getZ(), cantidad, 0.25, 0.1, 0.25, 0);
    }

    private static void alrededor(ServerPlayer jugador, double radio, ParticleOptions particula, int cantidad) {
        jugador.serverLevel().sendParticles(particula,
                jugador.getX(), jugador.getY() + 1.0, jugador.getZ(), cantidad, radio, 0.5, radio, 0.02);
    }

    private static void aLosPies(ServerPlayer jugador, ParticleOptions particula, int cantidad) {
        jugador.serverLevel().sendParticles(particula,
                jugador.getX(), jugador.getY() + 0.1, jugador.getZ(), cantidad, 0.3, 0.05, 0.3, 0.02);
    }

    private static void delante(ServerPlayer jugador, double distancia, double altura, ParticleOptions particula, int cantidad) {
        Vec3 punto = jugador.position().add(direccionHorizontal(jugador).scale(distancia)).add(0, altura, 0);
        jugador.serverLevel().sendParticles(particula, punto.x, punto.y, punto.z, cantidad, 0.1, 0.1, 0.1, 0.02);
    }

    /** Hacia dónde mira el cuerpo, sin la inclinación de la cabeza. */
    private static Vec3 direccionHorizontal(ServerPlayer jugador) {
        float giro = jugador.getYRot() * Mth.DEG_TO_RAD;
        return new Vec3(-Mth.sin(giro), 0, Mth.cos(giro));
    }

    private static void sonar(ServerPlayer jugador, SoundEvent sonido, float tono) {
        jugador.serverLevel().playSound(null, jugador.getX(), jugador.getY(), jugador.getZ(),
                sonido, SoundSource.PLAYERS, 0.6F, tono);
    }
}

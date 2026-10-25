// Coreografía de cada interacción: los fotogramas clave del actor y del receptor.
// Lo consulta PosesInteraccion (cliente). Es el sitio donde retocar cómo se ve un gesto.
//
// Claves para leerlo (ver convenciones de ángulos en Postura):
//   - Los dos jugadores están cara a cara, así que una pose idéntica en ambos queda en
//     simetría de rotación: la mano derecha de uno se encuentra con la derecha del otro
//     en el centro (chocar los cinco, estrechar la mano), y en el abrazo el brazo alto de
//     uno coincide con el brazo bajo del otro, como en un abrazo real.
//   - Los ángulos están medidos para la distancia de contacto de TipoInteraccion: el brazo
//     mide 0.75 bloques y el hombro está a 1.375 bloques del suelo.
//   - El receptor suele ir retrasado unos ticks: reacciona en vez de moverse a la vez.
//   - Las entregas empiezan con peso 1 en la pose de "ofrecer" de CatalogoEmociones (quien da
//     ya tenía la mano extendida) y el objeto cambia de mano en TipoInteraccion.MOMENTO_ENTREGA.
package com.leonardo.interacciones.animacion;

import static com.leonardo.interacciones.animacion.Fotograma.en;
import static com.leonardo.interacciones.animacion.Postura.REPOSO;
import static com.leonardo.interacciones.animacion.Postura.activa;
import static com.leonardo.interacciones.animacion.Suavizado.ACELERAR;
import static com.leonardo.interacciones.animacion.Suavizado.FRENAR;
import static com.leonardo.interacciones.animacion.Suavizado.LINEAL;
import static com.leonardo.interacciones.animacion.Suavizado.SUAVE;

import com.leonardo.interacciones.dominio.TipoInteraccion;
import java.util.EnumMap;
import java.util.Map;

public final class CatalogoAnimaciones {

    private static final float REACCION = 0.05F;
    private static final Map<TipoInteraccion, Animacion> DEL_ACTOR = new EnumMap<>(TipoInteraccion.class);
    private static final Map<TipoInteraccion, Animacion> DEL_RECEPTOR = new EnumMap<>(TipoInteraccion.class);

    static {
        registrar(TipoInteraccion.ABRAZAR, abrazo(), abrazo().retrasada(REACCION));
        registrar(TipoInteraccion.CHOCAR_CINCO, chocarCinco(), chocarCinco().retrasada(REACCION));
        registrar(TipoInteraccion.ESTRECHAR_MANO, estrecharMano(), estrecharMano().retrasada(REACCION));
        registrar(TipoInteraccion.BESO_MEJILLA, besoMejillaQuienBesa(), besoMejillaQuienRecibe());
        registrar(TipoInteraccion.BESO_BOCA, besoBoca(-1.0F, 0.45F), besoBoca(-2.0F, 0.55F).retrasada(REACCION));
        registrar(TipoInteraccion.PALMADITAS_CABEZA, palmaditasQuienDa(), palmaditasQuienRecibe());
        registrar(TipoInteraccion.CHOCAR_PUNOS, chocarPunos(), chocarPunos().retrasada(REACCION));
        // Cara a cara, la mano derecha de uno queda frente a la izquierda del otro.
        registrar(TipoInteraccion.TOMAR_MANO, tomarMano(), tomarMano().espejada().retrasada(REACCION));
        registrar(TipoInteraccion.ENTREGAR_OBJETO, entregarObjetoQuienDa(), entregarObjetoQuienRecibe());
        registrar(TipoInteraccion.ENTREGAR_FLOR, entregarFlorQuienDa(), entregarFlorQuienRecibe());
    }

    private CatalogoAnimaciones() {
    }

    public static Animacion de(TipoInteraccion tipo, Papel papel) {
        return papel == Papel.ACTOR ? DEL_ACTOR.get(tipo) : DEL_RECEPTOR.get(tipo);
    }

    private static void registrar(TipoInteraccion tipo, Animacion actor, Animacion receptor) {
        DEL_ACTOR.put(tipo, actor);
        DEL_RECEPTOR.put(tipo, receptor);
    }

    /** Un brazo por encima del hombro y otro a la cintura, cabeza apoyada de lado, se mece y aprieta. */
    private static Animacion abrazo() {
        Postura alcanzar = activa().torso(0.10F, 0).cabeza(0.05F, 0.2F, 0)
                .brazoDerecho(-2.0F, -0.25F, 0).brazoIzquierdo(-1.25F, 0.2F, 0);
        Postura rodear = activa().torso(0.16F, 0).cabeza(0.15F, 0.5F, 0.18F)
                .brazoDerecho(-1.85F, -0.55F, 0.1F).brazoIzquierdo(-1.1F, 0.55F, -0.1F);
        Postura apretar = activa().torso(0.19F, 0).cabeza(0.18F, 0.5F, 0.2F)
                .brazoDerecho(-1.8F, -0.7F, 0.12F).brazoIzquierdo(-1.05F, 0.7F, -0.12F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.16F, SUAVE, alcanzar),
                en(0.28F, SUAVE, rodear),
                en(0.42F, SUAVE, rodear.torso(0.17F, 0.06F).cabeza(0.15F, 0.5F, 0.24F)),
                en(0.56F, SUAVE, rodear.torso(0.17F, -0.05F).cabeza(0.15F, 0.5F, 0.14F)),
                en(0.70F, SUAVE, apretar),
                en(0.84F, SUAVE, alcanzar.torso(0.04F, 0).conPeso(0.8F)),
                en(1, SUAVE, REPOSO));
    }

    /** Coger impulso hacia atrás, choque rápido arriba en el centro, rebote y bajar. */
    private static Animacion chocarCinco() {
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.28F, FRENAR, activa().torso(-0.06F, 0).cabeza(-0.1F, 0, 0).brazoDerecho(-2.9F, -0.1F, 0.25F)),
                en(0.42F, ACELERAR, activa().torso(0.10F, 0).cabeza(-0.05F, 0, 0).brazoDerecho(-2.4F, -0.45F, 0)),
                en(0.52F, FRENAR, activa().torso(0.04F, 0).cabeza(0, 0, 0).brazoDerecho(-2.6F, -0.3F, 0.1F)),
                en(0.78F, SUAVE, activa().brazoDerecho(-0.6F, -0.1F, 0).conPeso(0.6F)),
                en(1, SUAVE, REPOSO));
    }

    /** Extender la mano a la altura de la cintura, tres sacudidas, sostener y soltar. */
    private static Animacion estrecharMano() {
        Postura base = activa().torso(0.06F, 0).cabeza(0.15F, 0, 0);
        Postura extender = base.brazoDerecho(-0.75F, -0.45F, 0);
        Postura arriba = base.brazoDerecho(-0.58F, -0.45F, 0);
        Postura abajo = base.brazoDerecho(-0.88F, -0.45F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.22F, SUAVE, extender),
                en(0.32F, SUAVE, arriba),
                en(0.40F, SUAVE, abajo),
                en(0.48F, SUAVE, arriba),
                en(0.56F, SUAVE, abajo),
                en(0.64F, SUAVE, extender),
                en(0.82F, LINEAL, extender.cabeza(0.12F, 0, 0.06F)),
                en(1, SUAVE, REPOSO));
    }

    /** Se inclina, gira la cara hacia la mejilla del otro, da el beso con un pequeño empuje y vuelve. */
    private static Animacion besoMejillaQuienBesa() {
        Postura acercarse = activa().torso(0.14F, 0).cabeza(0.1F, 0.5F, 0.12F)
                .brazoDerecho(-0.9F, -0.1F, 0).brazoIzquierdo(-0.9F, 0.1F, 0);
        Postura beso = acercarse.torso(0.24F, 0).cabeza(0.15F, 0.6F, 0.16F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.25F, SUAVE, acercarse),
                en(0.45F, FRENAR, beso),
                en(0.58F, LINEAL, beso),
                en(0.75F, SUAVE, acercarse.torso(0.10F, 0).cabeza(0.05F, 0.3F, 0.05F)),
                en(1, SUAVE, REPOSO));
    }

    /** Gira la cara para ofrecer la mejilla y después ladea la cabeza, contento. */
    private static Animacion besoMejillaQuienRecibe() {
        Postura ofrecer = activa().torso(0.04F, 0).cabeza(-0.05F, 0.45F, -0.1F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.28F, SUAVE, ofrecer),
                en(0.58F, LINEAL, ofrecer),
                en(0.75F, SUAVE, activa().torso(0.02F, 0).cabeza(0.1F, 0.15F, 0.12F)),
                en(1, SUAVE, REPOSO));
    }

    /**
     * Ambos se inclinan y ladean la cabeza (al estar cara a cara quedan a lados opuestos).
     * Quien besa rodea la cintura (brazos bajos) y quien recibe, el cuello (brazos altos).
     */
    private static Animacion besoBoca(float alturaBrazos, float haciaDentro) {
        Postura acercar = activa().torso(0.12F, 0).cabeza(0.05F, 0, 0.15F)
                .brazoDerecho(alturaBrazos + 0.05F, -haciaDentro * 0.6F, 0)
                .brazoIzquierdo(alturaBrazos + 0.05F, haciaDentro * 0.6F, 0);
        Postura beso = activa().torso(0.2F, 0).cabeza(0.12F, 0, 0.28F)
                .brazoDerecho(alturaBrazos, -haciaDentro, 0).brazoIzquierdo(alturaBrazos, haciaDentro, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.22F, SUAVE, acercar),
                en(0.38F, SUAVE, beso),
                en(0.62F, SUAVE, beso.torso(0.21F, 0.04F)),
                en(0.78F, SUAVE, beso),
                en(0.90F, SUAVE, acercar.conPeso(0.7F)),
                en(1, SUAVE, REPOSO));
    }

    /** Levanta la mano sobre la cabeza del otro y da tres palmadas cortas. */
    private static Animacion palmaditasQuienDa() {
        Postura levantar = activa().torso(0.08F, 0).cabeza(0.2F, 0, 0).brazoDerecho(-2.6F, -0.35F, 0);
        Postura palmada = levantar.brazoDerecho(-2.3F, -0.35F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.20F, SUAVE, levantar),
                en(0.30F, ACELERAR, palmada),
                en(0.38F, FRENAR, levantar),
                en(0.48F, ACELERAR, palmada),
                en(0.56F, FRENAR, levantar),
                en(0.66F, ACELERAR, palmada),
                en(0.80F, SUAVE, palmada.cabeza(0.2F, 0, 0.1F)),
                en(1, SUAVE, REPOSO));
    }

    /** Agacha un poco la cabeza con las manos juntas delante y la ladea a gusto. */
    private static Animacion palmaditasQuienRecibe() {
        Postura agachar = activa().torso(0.06F, 0).cabeza(0.4F, 0, 0)
                .brazoDerecho(-0.3F, -0.25F, 0).brazoIzquierdo(-0.3F, 0.25F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.25F, SUAVE, agachar),
                en(0.50F, SUAVE, agachar.cabeza(0.35F, 0, 0.15F)),
                en(0.70F, SUAVE, agachar.cabeza(0.38F, 0, -0.1F)),
                en(0.85F, SUAVE, agachar.cabeza(0.35F, 0, 0.12F)),
                en(1, SUAVE, REPOSO));
    }

    /** Puño atrás, golpe al centro, y la mano "explota" hacia fuera. */
    private static Animacion chocarPunos() {
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.30F, FRENAR, activa().torso(-0.04F, 0).brazoDerecho(-0.6F, 0.05F, 0)),
                en(0.45F, ACELERAR, activa().torso(0.10F, 0).brazoDerecho(-1.1F, -0.4F, 0)),
                en(0.55F, LINEAL, activa().torso(0.10F, 0).brazoDerecho(-1.1F, -0.4F, 0)),
                en(0.70F, FRENAR, activa().torso(0.02F, 0).brazoDerecho(-1.9F, -0.1F, 0.6F)),
                en(0.85F, SUAVE, activa().brazoDerecho(-0.5F, 0, 0.2F).conPeso(0.6F)),
                en(1, SUAVE, REPOSO));
    }

    /** Toma la mano del otro y la balancea suavemente mientras se miran. */
    private static Animacion tomarMano() {
        Postura tomar = activa().cabeza(0.1F, 0, 0.08F).brazoDerecho(-0.55F, -0.25F, 0);
        Postura haciaDelante = tomar.brazoDerecho(-0.65F, -0.25F, 0).torso(0.02F, 0.03F);
        Postura haciaAtras = tomar.brazoDerecho(-0.45F, -0.25F, 0).torso(0.02F, -0.03F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.18F, SUAVE, tomar),
                en(0.35F, SUAVE, haciaDelante),
                en(0.52F, SUAVE, haciaAtras),
                en(0.69F, SUAVE, haciaDelante),
                en(0.85F, SUAVE, tomar),
                en(1, SUAVE, REPOSO));
    }

    /** Ya tenía la mano extendida: la acerca, el otro toma el objeto (0.45) y retira la mano. */
    private static Animacion entregarObjetoQuienDa() {
        Postura alcanzar = activa().torso(0.1F, 0).cabeza(0.15F, 0, 0).brazoDerecho(-0.8F, -0.38F, 0);
        return Animacion.de(
                en(0, LINEAL, CatalogoEmociones.OFRECIENDO_OBJETO),
                en(0.3F, SUAVE, alcanzar),
                en(TipoInteraccion.MOMENTO_ENTREGA, LINEAL, alcanzar),
                en(0.65F, SUAVE, activa().torso(0.04F, 0).cabeza(0.1F, 0, 0).brazoDerecho(-0.4F, -0.2F, 0).conPeso(0.8F)),
                en(1, SUAVE, REPOSO));
    }

    /** Alarga la mano, toma el objeto, lo lleva al pecho y lo mira. */
    private static Animacion entregarObjetoQuienRecibe() {
        Postura alcanzar = activa().torso(0.1F, 0).cabeza(0.15F, 0, 0).brazoDerecho(-0.8F, -0.38F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.3F, SUAVE, alcanzar),
                en(TipoInteraccion.MOMENTO_ENTREGA, LINEAL, alcanzar),
                en(0.62F, SUAVE, activa().torso(0.02F, 0).cabeza(0.25F, 0, 0).brazoDerecho(-0.55F, -0.6F, 0)),
                en(0.8F, SUAVE, activa().torso(0.02F, 0).cabeza(0.3F, 0.1F, 0.08F).brazoDerecho(-0.6F, -0.55F, 0)),
                en(1, SUAVE, REPOSO));
    }

    /** Con vergüenza: acerca la flor mirando de reojo, la suelta y esconde la mano, más tímido aún. */
    private static Animacion entregarFlorQuienDa() {
        Postura timido = CatalogoEmociones.timidoOfreciendo(0.04F, 0.25F);
        Postura alcanzar = timido.torso(0.08F, 0).cabeza(0.35F, 0.15F, 0.1F).brazoDerecho(-0.6F, -0.32F, 0);
        Postura esconder = timido.cabeza(0.45F, 0.35F, 0.12F).brazoDerecho(0.3F, 0, 0).piernaDerecha(0, 0.3F, 0);
        return Animacion.de(
                en(0, LINEAL, timido),
                en(0.28F, SUAVE, alcanzar),
                en(TipoInteraccion.MOMENTO_ENTREGA, LINEAL, alcanzar),
                en(0.62F, SUAVE, esconder),
                en(0.82F, SUAVE, esconder.torso(0.04F, 0.05F).piernaDerecha(0, 0.05F, 0)),
                en(1, SUAVE, REPOSO));
    }

    /** Toma la flor con las dos manos, se la lleva al pecho y se mece contento. */
    private static Animacion entregarFlorQuienRecibe() {
        Postura alcanzar = activa().torso(0.06F, 0).cabeza(0.2F, 0, 0)
                .brazoDerecho(-0.62F, -0.35F, 0).brazoIzquierdo(-0.62F, 0.35F, 0);
        Postura alPecho = activa().torso(0.02F, 0).cabeza(0.15F, 0, 0.2F)
                .brazoDerecho(-0.5F, -0.75F, 0).brazoIzquierdo(-0.5F, 0.75F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.28F, SUAVE, alcanzar),
                en(TipoInteraccion.MOMENTO_ENTREGA, LINEAL, alcanzar),
                en(0.62F, SUAVE, alPecho),
                en(0.78F, SUAVE, alPecho.torso(0.02F, 0.05F).cabeza(0.1F, 0, 0.25F)),
                en(0.9F, SUAVE, alPecho.torso(0.02F, -0.04F).cabeza(0.1F, 0, 0.15F)),
                en(1, SUAVE, REPOSO));
    }
}

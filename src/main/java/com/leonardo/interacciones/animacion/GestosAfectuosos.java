// Coreografías de los gestos afectuosos entre dos jugadores (abrazo, besos, chocar los cinco...).
// Las registra CatalogoAnimaciones; ver allí las claves para leerlas.
package com.leonardo.interacciones.animacion;

import static com.leonardo.interacciones.animacion.Fotograma.en;
import static com.leonardo.interacciones.animacion.Postura.REPOSO;
import static com.leonardo.interacciones.animacion.Postura.activa;
import static com.leonardo.interacciones.animacion.Suavizado.ACELERAR;
import static com.leonardo.interacciones.animacion.Suavizado.FRENAR;
import static com.leonardo.interacciones.animacion.Suavizado.LINEAL;
import static com.leonardo.interacciones.animacion.Suavizado.SUAVE;

import com.leonardo.interacciones.dominio.TipoInteraccion;

final class GestosAfectuosos {

    private GestosAfectuosos() {
    }

    /** Un brazo por encima del hombro y otro a la cintura, cabeza apoyada de lado, se mece y aprieta. */
    static Animacion abrazo() {
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
    static Animacion chocarCinco() {
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.28F, FRENAR, activa().torso(-0.06F, 0).cabeza(-0.1F, 0, 0).brazoDerecho(-2.9F, -0.1F, 0.25F)),
                en(0.42F, ACELERAR, activa().torso(0.10F, 0).cabeza(-0.05F, 0, 0).brazoDerecho(-2.4F, -0.45F, 0)),
                en(0.52F, FRENAR, activa().torso(0.04F, 0).cabeza(0, 0, 0).brazoDerecho(-2.6F, -0.3F, 0.1F)),
                en(0.78F, SUAVE, activa().brazoDerecho(-0.6F, -0.1F, 0).conPeso(0.6F)),
                en(1, SUAVE, REPOSO));
    }


    /** Extender la mano a la altura de la cintura, tres sacudidas, sostener y soltar. */
    static Animacion estrecharMano() {
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
    static Animacion besoMejillaQuienBesa() {
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
    static Animacion besoMejillaQuienRecibe() {
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
    static Animacion besoBoca(float alturaBrazos, float haciaDentro) {
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
    static Animacion palmaditasQuienDa() {
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
    static Animacion palmaditasQuienRecibe() {
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
    static Animacion chocarPunos() {
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
    static Animacion tomarMano() {
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

    /** Se agarran las manos en alto, chocan las cabezas como colegas y se dan una palmada en la espalda. */
    static Animacion saludoColega() {
        Postura extender = activa().torso(0.05F, 0).cabeza(0.05F, 0, 0)
                .brazoDerecho(-1.3F, -0.5F, 0).brazoIzquierdo(-0.3F, 0, -0.05F);
        Postura agarrar = activa().torso(0.18F, 0).cabeza(0.35F, 0, 0)
                .brazoDerecho(-1.0F, -0.75F, 0).brazoIzquierdo(-1.2F, 0.6F, 0);
        Postura palmada = agarrar.torso(0.08F, 0).cabeza(0.1F, 0, 0).brazoIzquierdo(-1.35F, 0.65F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.2F, SUAVE, extender),
                en(0.35F, FRENAR, agarrar),
                en(TipoInteraccion.MOMENTO_CONTACTO, ACELERAR, agarrar.torso(0.22F, 0).cabeza(0.45F, 0, 0)),
                en(0.55F, FRENAR, palmada.brazoIzquierdo(-1.1F, 0.55F, 0)),
                en(0.7F, ACELERAR, palmada),
                en(0.8F, FRENAR, palmada.brazoIzquierdo(-1.1F, 0.55F, 0)),
                en(1, SUAVE, REPOSO));
    }

    /** Toma la mano del otro con una reverencia profunda y la besa; la otra mano a la espalda, galante. */
    static Animacion besarManoQuienBesa() {
        Postura tomar = activa().torso(0.15F, 0).cabeza(0.2F, 0, 0)
                .brazoDerecho(-0.75F, -0.4F, 0).brazoIzquierdo(0.35F, 0.1F, 0);
        Postura besar = tomar.torso(0.55F, 0).cabeza(0.35F, 0, 0).brazoDerecho(-0.95F, -0.4F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.25F, SUAVE, tomar),
                en(TipoInteraccion.MOMENTO_CONTACTO, SUAVE, besar),
                en(0.62F, LINEAL, besar),
                en(0.8F, SUAVE, tomar.torso(0.08F, 0).cabeza(-0.05F, 0, 0.1F).brazoDerecho(-0.6F, -0.35F, 0)),
                en(1, SUAVE, REPOSO));
    }

    /** Ofrece la mano con delicadeza y se lleva la otra a la mejilla, sonrojado. */
    static Animacion besarManoQuienRecibe() {
        Postura ofrecer = activa().torso(0.02F, 0).cabeza(0.1F, 0, 0.1F)
                .brazoDerecho(-0.9F, -0.3F, 0).brazoIzquierdo(-0.2F, 0, -0.05F);
        Postura sonrojo = ofrecer.cabeza(0.25F, 0.25F, 0.15F).brazoIzquierdo(-2.0F, 0.6F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.22F, SUAVE, ofrecer),
                en(0.5F, SUAVE, sonrojo),
                en(0.75F, SUAVE, sonrojo.cabeza(0.2F, 0.1F, 0.2F)),
                en(1, SUAVE, REPOSO));
    }

    /** Manos a los costados del otro moviendo los dedos (se alternan rápido). */
    static Animacion cosquillasQuienHace() {
        Postura base = activa().torso(0.15F, 0).cabeza(0.15F, 0, 0)
                .brazoDerecho(-1.0F, -0.35F, 0).brazoIzquierdo(-1.0F, 0.35F, 0);
        Postura a = base.brazoDerecho(-1.1F, -0.45F, 0).brazoIzquierdo(-0.9F, 0.25F, 0);
        Postura b = base.brazoDerecho(-0.9F, -0.25F, 0).brazoIzquierdo(-1.1F, 0.45F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO), en(0.15F, SUAVE, base),
                en(0.25F, LINEAL, a), en(0.35F, LINEAL, b), en(0.45F, LINEAL, a),
                en(0.55F, LINEAL, b), en(0.65F, LINEAL, a), en(0.75F, LINEAL, b),
                en(0.85F, SUAVE, base), en(1, SUAVE, REPOSO));
    }

    /** Se encoge y se retuerce de risa, con los brazos pegados al cuerpo. */
    static Animacion cosquillasQuienRecibe() {
        Postura encogido = activa().torso(0.2F, 0).cabeza(-0.2F, 0, 0)
                .brazoDerecho(-0.4F, -0.5F, 0).brazoIzquierdo(-0.4F, 0.5F, 0);
        Postura izquierda = encogido.torso(-0.1F, 0.1F).cabeza(-0.35F, 0.3F, 0.1F);
        Postura derecha = encogido.torso(0.15F, -0.1F).cabeza(-0.3F, -0.3F, -0.1F);
        return Animacion.de(
                en(0, LINEAL, REPOSO), en(0.18F, FRENAR, encogido),
                en(0.3F, SUAVE, izquierda), en(0.42F, SUAVE, derecha), en(0.54F, SUAVE, izquierda),
                en(0.66F, SUAVE, derecha), en(0.78F, SUAVE, izquierda),
                en(0.9F, SUAVE, encogido.torso(0.05F, 0)), en(1, SUAVE, REPOSO));
    }

    /** Abraza al otro con un brazo por los hombros y le da palmadas suaves en la espalda. */
    static Animacion consolarQuienConsuela() {
        Postura abrazar = activa().torso(0.12F, 0).cabeza(0.15F, 0.3F, 0.15F)
                .brazoDerecho(-1.7F, -0.6F, 0).brazoIzquierdo(-1.2F, 0.5F, 0);
        Postura palmada = abrazar.brazoIzquierdo(-1.35F, 0.5F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO), en(0.18F, SUAVE, abrazar),
                en(0.3F, SUAVE, palmada), en(0.4F, SUAVE, abrazar), en(0.5F, SUAVE, palmada),
                en(0.6F, SUAVE, abrazar), en(0.7F, SUAVE, palmada), en(0.85F, SUAVE, abrazar),
                en(1, SUAVE, REPOSO));
    }

    /** Llora apoyado en el hombro del otro, sollozando. */
    static Animacion consolarQuienLlora() {
        Postura apoyado = activa().torso(0.2F, 0).cabeza(0.45F, 0.3F, 0.2F)
                .brazoDerecho(-0.9F, -0.3F, 0).brazoIzquierdo(-0.9F, 0.3F, 0);
        Postura sollozo = apoyado.torso(0.24F, 0.03F).elevacion(0.3F);
        return Animacion.de(
                en(0, LINEAL, REPOSO), en(0.2F, SUAVE, apoyado),
                en(0.32F, SUAVE, sollozo), en(0.44F, SUAVE, apoyado), en(0.56F, SUAVE, sollozo),
                en(0.68F, SUAVE, apoyado), en(0.8F, SUAVE, sollozo), en(1, SUAVE, REPOSO));
    }

    /**
     * Baile de salón: la mano izquierda sostiene la del otro en alto, la derecha en su cintura;
     * se mecen y marcan el paso. El otro lo hace en espejo.
     */
    static Animacion bailarJuntos() {
        Postura tomados = activa().torso(0.04F, 0.08F).cabeza(0, 0.15F, 0.08F)
                .brazoIzquierdo(-1.3F, 0, -0.5F).brazoDerecho(-1.0F, -0.6F, 0)
                .piernaDerecha(-0.2F, 0, 0).piernaIzquierda(0.15F, 0, 0);
        Postura alOtroLado = tomados.torso(0.04F, -0.08F).cabeza(0, -0.15F, -0.08F).elevacion(0.5F)
                .piernaDerecha(0.15F, 0, 0).piernaIzquierda(-0.2F, 0, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO), en(0.12F, SUAVE, tomados),
                en(0.25F, SUAVE, alOtroLado), en(0.38F, SUAVE, tomados), en(0.51F, SUAVE, alOtroLado),
                en(0.64F, SUAVE, tomados), en(0.77F, SUAVE, alOtroLado), en(0.88F, SUAVE, tomados),
                en(1, SUAVE, REPOSO));
    }
}

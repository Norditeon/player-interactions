// Coreografía de cada emoción en solitario (TipoEmocion). La consulta AnimacionesActivas
// (cliente) al empezar una emoción. Las primeras están aquí; las nuevas, repartidas por tema
// en EmocionesConObjeto, EmocionesDeCuerpoEntero y EmocionesDePose. También aporta las poses
// de "ofrecer" que reutiliza CatalogoAnimaciones para que la entrega empiece donde acabó.
//
// Reglas para escribir una emoción (ver convenciones de ángulos en Postura):
//   - Cíclica: el primer y el último fotograma son la misma pose con peso 1 (así el bucle no
//     salta). La entrada y salida suaves las pone PosesInteraccion.
//   - No cíclica: empieza y termina en REPOSO.
//   - Si una pieza se mueve en algún fotograma, se define en todos los de peso > 0; si no, se
//     quedaría con el valor del fotograma vecino.
package com.leonardo.interacciones.animacion;

import static com.leonardo.interacciones.animacion.Animacion.ciclo;
import static com.leonardo.interacciones.animacion.Fotograma.en;
import static com.leonardo.interacciones.animacion.Postura.REPOSO;
import static com.leonardo.interacciones.animacion.Postura.activa;
import static com.leonardo.interacciones.animacion.Suavizado.ACELERAR;
import static com.leonardo.interacciones.animacion.Suavizado.FRENAR;
import static com.leonardo.interacciones.animacion.Suavizado.LINEAL;
import static com.leonardo.interacciones.animacion.Suavizado.SUAVE;

import com.leonardo.interacciones.dominio.TipoEmocion;
import java.util.EnumMap;
import java.util.Map;

public final class CatalogoEmociones {

    /** Brazo extendido hacia delante con el objeto en la mano, esperando que lo acepten. */
    static final Postura OFRECIENDO_OBJETO = activa().torso(0.06F, 0).cabeza(0.15F, 0, 0).brazoDerecho(-0.95F, -0.2F, 0);

    private static final Map<TipoEmocion, Animacion> ANIMACIONES = new EnumMap<>(TipoEmocion.class);

    static {
        for (TipoEmocion tipo : TipoEmocion.values()) {
            ANIMACIONES.put(tipo, crear(tipo));
        }
    }

    private CatalogoEmociones() {
    }

    public static Animacion de(TipoEmocion tipo) {
        return ANIMACIONES.get(tipo);
    }

    /**
     * Ofrecer una flor con vergüenza: mira al suelo y de reojo, una mano en la nuca, la flor
     * extendida, el cuerpo se mece y gira la punta del pie.
     */
    static Postura timidoOfreciendo(float balanceo, float giroPie) {
        return activa().torso(0.04F, balanceo).cabeza(0.4F, 0.3F, 0.1F)
                .brazoDerecho(-0.85F, -0.2F, 0).brazoIzquierdo(-2.5F, 0.9F, 0)
                .piernaDerecha(0, giroPie, 0).piernaIzquierda(0, 0, 0);
    }

    private static Animacion crear(TipoEmocion tipo) {
        return switch (tipo) {
            case BAILAR -> bailar();
            case BAILE_ROBOT -> baileRobot();
            case SALTAR_ALEGRIA -> saltarDeAlegria();
            case SALUDAR -> saludar();
            case APLAUDIR -> aplaudir();
            case CELEBRAR -> celebrar();
            case REIR -> reir();
            case LLORAR -> llorar();
            case SENTARSE -> sentarse();
            case ENOJARSE -> enojarse();
            case PATALEAR -> patalear();
            case BRAZOS_CRUZADOS -> brazosCruzados();
            case PENSAR -> pensar();
            case ENCOGERSE -> encogerseDeHombros();
            case FACEPALM -> facepalm();
            case TIMIDEZ -> timidez();
            case PRESUMIR -> presumir();
            case REVERENCIA -> reverencia();
            case SALUDO_MILITAR -> saludoMilitar();
            case SENALAR -> senalar();
            case BOSTEZAR -> bostezar();
            case ENAMORADO -> EmocionesDePose.enamorado();
            case BESO_AL_AIRE -> EmocionesDePose.besoAlAire();
            case SUPLICAR -> EmocionesDeCuerpoEntero.suplicar();
            case GIRO -> EmocionesDeCuerpoEntero.giro();
            case MORTAL -> EmocionesDeCuerpoEntero.mortal();
            case ENTRENAR_PUNOS -> EmocionesDePose.entrenarPunetazos();
            case COMER -> EmocionesConObjeto.comer();
            case PRACTICAR_ESPADA -> EmocionesConObjeto.practicarEspada();
            case SIGILO -> EmocionesDeCuerpoEntero.sigilo();
            case MALABAR -> EmocionesConObjeto.malabar();
            case DETENER_ALTO -> EmocionesDePose.detenerAlto();
            case SENTARSE_HORIZONTE -> EmocionesDeCuerpoEntero.sentarseMirandoHorizonte();
            case LANZAR -> EmocionesConObjeto.lanzar();
            case PERREO -> EmocionesDePose.perreo();
            case POSE_SEXY -> EmocionesDePose.poseSexy();
            case POSE_HEROE -> EmocionesDePose.poseHeroe();
            case LLUVIA_DERROTADO -> EmocionesDePose.derrotadoBajoLaLluvia();
            case GOLPEAR_SUELO -> EmocionesDeCuerpoEntero.golpearSuelo();
            case MEDITAR -> EmocionesDeCuerpoEntero.meditar();
            case ACOSTARSE -> EmocionesDeCuerpoEntero.acostarse();
            case OFRECER_OBJETO -> ciclo(SUAVE, OFRECIENDO_OBJETO, OFRECIENDO_OBJETO.brazoDerecho(-1.02F, -0.2F, 0));
            case OFRECER_FLOR -> ciclo(SUAVE, timidoOfreciendo(0.04F, 0.25F), timidoOfreciendo(-0.04F, 0.05F));
        };
    }

    /** Cadera de lado a lado, un brazo arriba y otro abajo que se van turnando, rodillas que marcan el paso. */
    private static Animacion bailar() {
        Postura derecha = activa().torso(0.05F, 0.12F).cabeza(0, 0.2F, 0.12F)
                .brazoDerecho(-2.6F, 0, 0.35F).brazoIzquierdo(-0.5F, 0, -0.25F)
                .piernaDerecha(-0.25F, 0, 0.05F).piernaIzquierda(0.1F, 0, -0.05F);
        Postura centro = activa().torso(0.05F, 0).cabeza(0, 0, 0)
                .brazoDerecho(-1.4F, 0, 0.6F).brazoIzquierdo(-1.4F, 0, -0.6F)
                .piernaDerecha(0, 0, 0.05F).piernaIzquierda(0, 0, -0.05F).elevacion(1);
        return ciclo(SUAVE, derecha, centro, derecha.espejo(), centro);
    }

    /** Movimientos secos: brazos rígidos que se alternan y cabeza que gira de golpe. */
    private static Animacion baileRobot() {
        Postura a = activa().cabeza(0, 0.4F, 0).brazoDerecho(-1.57F, 0, 0).brazoIzquierdo(0, 0, 0);
        Postura b = activa().cabeza(0, -0.4F, 0).brazoDerecho(0, 0, 0).brazoIzquierdo(-1.57F, 0, 0);
        Postura ambos = activa().cabeza(0, 0, 0).brazoDerecho(-1.57F, 0, 0).brazoIzquierdo(-1.57F, 0, 0).elevacion(0.5F);
        return ciclo(FRENAR, a, b, ambos, b);
    }

    /** Agacharse, saltar con los brazos en V y las piernas recogidas, caer. */
    private static Animacion saltarDeAlegria() {
        Postura suelo = activa().torso(0, 0).cabeza(-0.2F, 0, 0)
                .brazoDerecho(-0.3F, 0, 2.5F).brazoIzquierdo(-0.3F, 0, -2.5F)
                .piernaDerecha(0, 0, 0).piernaIzquierda(0, 0, 0);
        Postura agachado = suelo.torso(0.1F, 0).elevacion(1.5F);
        Postura aire = activa().torso(0, 0).cabeza(-0.35F, 0, 0)
                .brazoDerecho(-0.2F, 0, 2.9F).brazoIzquierdo(-0.2F, 0, -2.9F)
                .piernaDerecha(0.35F, 0, 0.1F).piernaIzquierda(0.35F, 0, -0.1F).elevacion(-6);
        return Animacion.de(
                en(0, LINEAL, suelo),
                en(0.15F, SUAVE, agachado),
                en(0.5F, FRENAR, aire),
                en(0.85F, ACELERAR, suelo),
                en(1, LINEAL, suelo));
    }

    /** Levantar la mano y agitarla de lado a lado. */
    private static Animacion saludar() {
        Postura arriba = activa().cabeza(0, 0, 0.1F).brazoDerecho(-0.3F, 0, 2.5F);
        Postura fuera = arriba.brazoDerecho(-0.3F, 0, 2.85F);
        Postura dentro = arriba.brazoDerecho(-0.3F, 0, 2.2F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.2F, SUAVE, arriba),
                en(0.32F, SUAVE, fuera),
                en(0.44F, SUAVE, dentro),
                en(0.56F, SUAVE, fuera),
                en(0.68F, SUAVE, dentro),
                en(0.8F, SUAVE, arriba),
                en(1, SUAVE, REPOSO));
    }

    /** Manos delante que se abren y se juntan rápido. */
    private static Animacion aplaudir() {
        Postura abiertas = activa().cabeza(0.05F, 0, 0).brazoDerecho(-1.25F, -0.35F, 0).brazoIzquierdo(-1.25F, 0.35F, 0);
        Postura juntas = abiertas.brazoDerecho(-1.3F, -0.75F, 0).brazoIzquierdo(-1.3F, 0.75F, 0);
        return Animacion.de(en(0, LINEAL, abiertas), en(0.5F, ACELERAR, juntas), en(1, FRENAR, abiertas));
    }

    /** Puño arriba tres veces. */
    private static Animacion celebrar() {
        Postura arriba = activa().torso(-0.05F, 0).cabeza(-0.2F, 0, 0).brazoDerecho(-2.9F, -0.1F, 0.15F);
        Postura medio = activa().torso(0.05F, 0).cabeza(-0.1F, 0, 0).brazoDerecho(-2.0F, -0.2F, 0.1F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.2F, FRENAR, arriba),
                en(0.35F, ACELERAR, medio),
                en(0.5F, FRENAR, arriba),
                en(0.65F, ACELERAR, medio),
                en(0.8F, FRENAR, arriba),
                en(1, SUAVE, REPOSO));
    }

    /** Echado hacia atrás, manos en la barriga, sacudiéndose. */
    private static Animacion reir() {
        Postura a = activa().torso(-0.12F, 0).cabeza(-0.35F, 0, 0.05F)
                .brazoDerecho(-0.55F, -0.55F, 0).brazoIzquierdo(-0.55F, 0.55F, 0);
        Postura b = a.torso(-0.2F, 0.03F).cabeza(-0.45F, 0, -0.05F).elevacion(-0.5F);
        return ciclo(SUAVE, a, b);
    }

    /** Cabeza gacha, manos en la cara y sollozos que sacuden los hombros. */
    private static Animacion llorar() {
        Postura a = activa().torso(0.15F, 0).cabeza(0.5F, 0, 0)
                .brazoDerecho(-2.25F, -0.5F, 0).brazoIzquierdo(-2.25F, 0.5F, 0);
        Postura sollozo = a.torso(0.2F, 0.03F).cabeza(0.55F, 0, 0.04F).elevacion(0.5F);
        Postura otroSollozo = a.torso(0.17F, -0.03F).cabeza(0.52F, 0, -0.04F);
        return ciclo(SUAVE, a, sollozo, a, otroSollozo);
    }

    /** Sentado en el suelo con las piernas estiradas y las manos en las rodillas; respira. La cabeza queda libre. */
    private static Animacion sentarse() {
        Postura sentado = activa().torso(0.04F, 0).elevacion(11)
                .piernaDerecha(-1.5F, 0.12F, 0.05F).piernaIzquierda(-1.5F, -0.12F, -0.05F)
                .brazoDerecho(-0.75F, -0.1F, 0.05F).brazoIzquierdo(-0.75F, 0.1F, -0.05F);
        return ciclo(SUAVE, sentado, sentado.torso(0.08F, 0));
    }

    /** Inclinado hacia delante, brazos tiesos y abiertos, resoplando. */
    private static Animacion enojarse() {
        Postura a = activa().torso(0.15F, 0).cabeza(-0.15F, 0, 0)
                .brazoDerecho(0.25F, 0, 0.25F).brazoIzquierdo(0.25F, 0, -0.25F);
        Postura resoplido = a.torso(0.18F, 0.04F).brazoDerecho(0.3F, 0, 0.3F).brazoIzquierdo(0.3F, 0, -0.3F);
        return ciclo(SUAVE, a, resoplido, a, resoplido.torso(0.18F, -0.04F));
    }

    /** Pisotones alternos con los brazos estirados y la cabeza negando. */
    private static Animacion patalear() {
        Postura derechaArriba = activa().torso(0.1F, 0).cabeza(0.1F, 0.25F, 0)
                .brazoDerecho(0.15F, 0, 0.35F).brazoIzquierdo(0.15F, 0, -0.35F)
                .piernaDerecha(-0.6F, 0, 0.05F).piernaIzquierda(0.05F, 0, 0).elevacion(-0.5F);
        Postura izquierdaArriba = derechaArriba.espejo();
        return Animacion.de(
                en(0, LINEAL, derechaArriba),
                en(0.25F, ACELERAR, derechaArriba.piernaDerecha(0.05F, 0, 0).elevacion(0.5F)),
                en(0.5F, FRENAR, izquierdaArriba),
                en(0.75F, ACELERAR, izquierdaArriba.piernaIzquierda(0.05F, 0, 0).elevacion(0.5F)),
                en(1, FRENAR, derechaArriba));
    }

    /** Brazos cruzados, cabeza ladeada y golpecitos impacientes con el pie. */
    private static Animacion brazosCruzados() {
        Postura cruzados = activa().cabeza(-0.05F, 0, 0.1F)
                .brazoDerecho(-0.95F, -0.85F, 0).brazoIzquierdo(-0.85F, 0.9F, 0)
                .piernaDerecha(0, 0.2F, 0);
        Postura golpecito = cruzados.piernaDerecha(-0.2F, 0.2F, 0);
        return Animacion.de(
                en(0, LINEAL, cruzados),
                en(0.6F, LINEAL, cruzados),
                en(0.7F, FRENAR, golpecito),
                en(0.8F, ACELERAR, cruzados),
                en(0.9F, FRENAR, golpecito),
                en(1, ACELERAR, cruzados));
    }

    /** Mano en la barbilla, el otro brazo cruzado sosteniendo el codo, la mirada que vaga. */
    private static Animacion pensar() {
        Postura a = activa().cabeza(0.1F, 0.15F, 0.15F)
                .brazoDerecho(-2.1F, -0.75F, 0).brazoIzquierdo(-0.8F, 0.75F, 0);
        return ciclo(SUAVE, a, a.cabeza(0.05F, -0.1F, 0.1F));
    }

    /** Brazos abiertos con las palmas arriba y la cabeza ladeada: "ni idea". */
    private static Animacion encogerseDeHombros() {
        Postura hombros = activa().cabeza(0, 0, 0.2F).elevacion(-0.5F)
                .brazoDerecho(-0.6F, 0.15F, 0.55F).brazoIzquierdo(-0.6F, -0.15F, -0.55F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.3F, FRENAR, hombros),
                en(0.65F, LINEAL, hombros),
                en(1, SUAVE, REPOSO));
    }

    /** Mano a la cara de golpe y negar con la cabeza. */
    private static Animacion facepalm() {
        Postura golpe = activa().torso(0.12F, 0).cabeza(0.45F, 0, 0).brazoDerecho(-2.4F, -0.6F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.22F, ACELERAR, golpe),
                en(0.4F, SUAVE, golpe.cabeza(0.45F, 0.2F, 0)),
                en(0.55F, SUAVE, golpe.cabeza(0.45F, -0.2F, 0)),
                en(0.7F, SUAVE, golpe),
                en(1, SUAVE, REPOSO));
    }

    /** Manos a la espalda, mirada al suelo, balanceo y la punta del pie girando. */
    private static Animacion timidez() {
        Postura a = activa().torso(0.04F, 0.04F).cabeza(0.35F, 0.2F, 0.08F)
                .brazoDerecho(0.35F, 0, 0.05F).brazoIzquierdo(0.35F, 0, -0.05F)
                .piernaDerecha(0, 0.3F, 0).piernaIzquierda(0, 0, 0);
        Postura b = a.torso(0.04F, -0.04F).cabeza(0.35F, 0.1F, 0.04F).piernaDerecha(0, 0.05F, 0);
        return ciclo(SUAVE, a, b);
    }

    /** Brazos en alto marcando músculo, sacando pecho. */
    private static Animacion presumir() {
        Postura a = activa().torso(-0.05F, 0).cabeza(-0.1F, 0, 0)
                .brazoDerecho(-0.2F, 0, 2.0F).brazoIzquierdo(-0.2F, 0, -2.0F);
        Postura apretar = a.torso(-0.08F, 0).brazoDerecho(-0.4F, 0, 2.3F).brazoIzquierdo(-0.4F, 0, -2.3F);
        return ciclo(SUAVE, a, apretar);
    }

    /** Inclinación profunda con una mano en el pecho y la otra a la espalda. */
    private static Animacion reverencia() {
        Postura inclinado = activa().torso(0.85F, 0).cabeza(0.3F, 0, 0)
                .brazoDerecho(-0.9F, -0.9F, 0).brazoIzquierdo(0.6F, 0.3F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.35F, SUAVE, inclinado),
                en(0.6F, LINEAL, inclinado),
                en(1, SUAVE, REPOSO));
    }

    /** Mano a la frente, firme. */
    private static Animacion saludoMilitar() {
        Postura firme = activa().cabeza(-0.05F, 0, 0).brazoDerecho(-2.35F, -0.75F, 0).brazoIzquierdo(0, 0, -0.02F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.25F, FRENAR, firme),
                en(0.75F, LINEAL, firme),
                en(1, SUAVE, REPOSO));
    }

    /** Brazo estirado hacia donde mira el jugador. */
    private static Animacion senalar() {
        Postura apuntar = activa().torso(0.05F, 0).brazoDerecho(-1.6F, -0.05F, 0);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.2F, FRENAR, apuntar),
                en(0.75F, LINEAL, apuntar),
                en(1, SUAVE, REPOSO));
    }

    /** Estirarse con los brazos arriba y taparse la boca. */
    private static Animacion bostezar() {
        Postura estirar = activa().torso(-0.12F, 0).cabeza(-0.4F, 0, 0).elevacion(-0.5F)
                .brazoDerecho(-0.3F, 0, 2.8F).brazoIzquierdo(-0.3F, 0, -2.8F);
        Postura tapar = activa().torso(0, 0).cabeza(-0.1F, 0, 0)
                .brazoDerecho(-2.3F, -0.6F, 0).brazoIzquierdo(-0.1F, 0, -0.05F);
        return Animacion.de(
                en(0, LINEAL, REPOSO),
                en(0.3F, SUAVE, estirar),
                en(0.5F, LINEAL, estirar),
                en(0.7F, SUAVE, tapar),
                en(0.82F, LINEAL, tapar),
                en(1, SUAVE, REPOSO));
    }
}

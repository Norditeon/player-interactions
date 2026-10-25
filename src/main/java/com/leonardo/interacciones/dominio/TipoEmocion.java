// Catálogo de emociones que un jugador hace solo (bailar, llorar, sentarse...).
// Las elegibles forman el repertorio con el que cada jugador arma su ruleta; las demás
// las usa el servidor por su cuenta (p. ej. extender la mano mientras ofreces un objeto).
// Lo usan la ruleta y el repertorio (cliente), la red y EmocionesEnCurso (servidor).
// No depende de Minecraft para poder testearse.
package com.leonardo.interacciones.dominio;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum TipoEmocion {
    // clave, duración en ticks (en las cíclicas, lo que dura una vuelta), cíclica, elegible
    BAILAR("bailar", 40, true, true),
    BAILE_ROBOT("baile_robot", 32, true, true),
    SALTAR_ALEGRIA("saltar_alegria", 20, true, true),
    SALUDAR("saludar", 50, false, true),
    APLAUDIR("aplaudir", 12, true, true),
    CELEBRAR("celebrar", 30, false, true),
    REIR("reir", 16, true, true),
    LLORAR("llorar", 30, true, true),
    SENTARSE("sentarse", 60, true, true),
    ENOJARSE("enojarse", 30, true, true),
    PATALEAR("patalear", 12, true, true),
    BRAZOS_CRUZADOS("brazos_cruzados", 60, true, true),
    PENSAR("pensar", 60, true, true),
    ENCOGERSE("encogerse", 30, false, true),
    FACEPALM("facepalm", 40, false, true),
    TIMIDEZ("timidez", 40, true, true),
    PRESUMIR("presumir", 30, true, true),
    REVERENCIA("reverencia", 40, false, true),
    SALUDO_MILITAR("saludo_militar", 40, false, true),
    SENALAR("senalar", 30, false, true),
    BOSTEZAR("bostezar", 50, false, true),
    ENAMORADO("enamorado", 40, true, true),
    BESO_AL_AIRE("beso_al_aire", 30, false, true),
    SUPLICAR("suplicar", 30, true, true),
    GIRO("giro", 20, false, true),
    MORTAL("mortal", 24, false, true),
    ENTRENAR_PUNOS("entrenar_punos", 12, true, true),
    COMER("comer", 16, true, true),
    PRACTICAR_ESPADA("practicar_espada", 20, true, true),
    SIGILO("sigilo", 40, true, true),
    MALABAR("malabar", 30, true, true),
    DETENER_ALTO("detener_alto", 30, true, true),
    SENTARSE_HORIZONTE("sentarse_horizonte", 80, true, true),
    LANZAR("lanzar", 24, false, true),
    PERREO("perreo", 10, true, true),
    POSE_SEXY("pose_sexy", 60, true, true),
    POSE_HEROE("pose_heroe", 60, true, true),
    LLUVIA_DERROTADO("lluvia_derrotado", 60, true, true),
    GOLPEAR_SUELO("golpear_suelo", 20, true, true),
    MEDITAR("meditar", 60, true, true),
    ACOSTARSE("acostarse", 80, true, true),
    OFRECER_OBJETO("ofrecer_objeto", 40, true, false),
    OFRECER_FLOR("ofrecer_flor", 40, true, false);

    /** Identificador estable: se guarda en la configuración y en las claves de traducción. */
    private final String clave;
    private final int duracionTicks;
    /** Se repite hasta que el jugador la corta (aunque camine). */
    private final boolean ciclica;
    /** Aparece en el repertorio; las no elegibles solo las lanza el servidor. */
    private final boolean elegible;

    TipoEmocion(String clave, int duracionTicks, boolean ciclica, boolean elegible) {
        this.clave = clave;
        this.duracionTicks = duracionTicks;
        this.ciclica = ciclica;
        this.elegible = elegible;
    }

    public static List<TipoEmocion> repertorio() {
        return Arrays.stream(values()).filter(TipoEmocion::elegible).toList();
    }

    public static Optional<TipoEmocion> porClave(String clave) {
        return Arrays.stream(values()).filter(tipo -> tipo.clave.equals(clave)).findFirst();
    }

    /** Busca por posición sin lanzar excepción: los índices llegan por red y pueden ser inválidos. */
    public static TipoEmocion porIndice(int indice) {
        TipoEmocion[] todos = values();
        return indice >= 0 && indice < todos.length ? todos[indice] : BAILAR;
    }

    public String clave() {
        return clave;
    }

    public String claveTraduccion() {
        return "emocion.interacciones." + clave;
    }

    public int duracionTicks() {
        return duracionTicks;
    }

    public boolean ciclica() {
        return ciclica;
    }

    public boolean elegible() {
        return elegible;
    }
}

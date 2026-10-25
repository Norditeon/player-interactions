// Qué coreografía anima a cada jugador en cada interacción entre dos: actor, receptor y,
// si el gesto fue forzado, la reacción del receptor. Lo consulta AnimacionesActivas (cliente).
// Las coreografías viven en GestosAfectuosos, GolpesYBromas, Entregas y ReaccionesForzadas.
//
// Claves para leerlas (ver convenciones de ángulos en Postura):
//   - Cara a cara, una pose idéntica en ambos queda en simetría de rotación: la mano derecha
//     de uno se encuentra con la derecha del otro en el centro (chocar los cinco, estrechar la
//     mano), y en el abrazo el brazo alto de uno coincide con el brazo bajo del otro.
//   - Los ángulos están medidos para la distancia de contacto de TipoInteraccion: el brazo
//     mide 0.75 bloques y el hombro está a 1.375 bloques del suelo.
//   - El receptor suele ir retrasado unos ticks: reacciona en vez de moverse a la vez.
//   - El contacto (entrega, golpe) ocurre en TipoInteraccion.MOMENTO_CONTACTO.
package com.leonardo.interacciones.animacion;

import com.leonardo.interacciones.dominio.TipoInteraccion;
import java.util.EnumMap;
import java.util.Map;

public final class CatalogoAnimaciones {

    private static final float REACCION = 0.05F;
    private static final Map<TipoInteraccion, Animacion> DEL_ACTOR = new EnumMap<>(TipoInteraccion.class);
    private static final Map<TipoInteraccion, Animacion> DEL_RECEPTOR = new EnumMap<>(TipoInteraccion.class);
    private static final Map<TipoInteraccion, Animacion> DEL_RECEPTOR_FORZADO = new EnumMap<>(TipoInteraccion.class);

    static {
        registrarGestos();
        registrarGolpesYEntregas();
        registrarReaccionesForzadas();
    }

    private CatalogoAnimaciones() {
    }

    /** forzada: el receptor no aceptó; si hay una reacción propia para ese gesto, se usa esa. */
    public static Animacion de(TipoInteraccion tipo, Papel papel, boolean forzada) {
        if (papel == Papel.ACTOR) {
            return DEL_ACTOR.get(tipo);
        }
        return forzada ? DEL_RECEPTOR_FORZADO.getOrDefault(tipo, DEL_RECEPTOR.get(tipo)) : DEL_RECEPTOR.get(tipo);
    }

    /** Para los tests: los gestos que tienen una reacción forzada propia. */
    static Map<TipoInteraccion, Animacion> reaccionesForzadas() {
        return DEL_RECEPTOR_FORZADO;
    }

    private static void registrarGestos() {
        registrar(TipoInteraccion.ABRAZAR, GestosAfectuosos.abrazo(), GestosAfectuosos.abrazo().retrasada(REACCION));
        registrar(TipoInteraccion.CHOCAR_CINCO, GestosAfectuosos.chocarCinco(), GestosAfectuosos.chocarCinco().retrasada(REACCION));
        registrar(TipoInteraccion.ESTRECHAR_MANO, GestosAfectuosos.estrecharMano(), GestosAfectuosos.estrecharMano().retrasada(REACCION));
        registrar(TipoInteraccion.BESO_MEJILLA, GestosAfectuosos.besoMejillaQuienBesa(), GestosAfectuosos.besoMejillaQuienRecibe());
        registrar(TipoInteraccion.BESO_BOCA, GestosAfectuosos.besoBoca(-1.0F, 0.45F),
                GestosAfectuosos.besoBoca(-2.0F, 0.55F).retrasada(REACCION));
        registrar(TipoInteraccion.PALMADITAS_CABEZA, GestosAfectuosos.palmaditasQuienDa(), GestosAfectuosos.palmaditasQuienRecibe());
        registrar(TipoInteraccion.CHOCAR_PUNOS, GestosAfectuosos.chocarPunos(), GestosAfectuosos.chocarPunos().retrasada(REACCION));
        // Cara a cara, la mano derecha de uno queda frente a la izquierda del otro.
        registrar(TipoInteraccion.TOMAR_MANO, GestosAfectuosos.tomarMano(), GestosAfectuosos.tomarMano().espejada().retrasada(REACCION));
        registrar(TipoInteraccion.SALUDO_COLEGA, GestosAfectuosos.saludoColega(), GestosAfectuosos.saludoColega().retrasada(REACCION));
        registrar(TipoInteraccion.BESAR_MANO, GestosAfectuosos.besarManoQuienBesa(), GestosAfectuosos.besarManoQuienRecibe());
        registrar(TipoInteraccion.COSQUILLAS, GestosAfectuosos.cosquillasQuienHace(), GestosAfectuosos.cosquillasQuienRecibe());
        registrar(TipoInteraccion.CONSOLAR, GestosAfectuosos.consolarQuienConsuela(), GestosAfectuosos.consolarQuienLlora());
        // Baile de salón: uno guía con la izquierda en alto y el otro responde con la derecha.
        registrar(TipoInteraccion.BAILAR_JUNTOS, GestosAfectuosos.bailarJuntos(), GestosAfectuosos.bailarJuntos().espejada());
    }

    private static void registrarGolpesYEntregas() {
        registrar(TipoInteraccion.CACHETADA, GolpesYBromas.cachetadaQuienDa(), GolpesYBromas.cachetadaQuienRecibe());
        registrar(TipoInteraccion.PUNETAZO, GolpesYBromas.punetazoQuienDa(), GolpesYBromas.punetazoQuienRecibe());
        registrar(TipoInteraccion.EMPUJAR, GolpesYBromas.empujarQuienDa(), GolpesYBromas.empujarQuienRecibe());
        registrar(TipoInteraccion.PATADA_TRASERO, GolpesYBromas.patadaQuienDa(), GolpesYBromas.patadaQuienRecibe());
        registrar(TipoInteraccion.NALGADA, GolpesYBromas.nalgadaQuienDa(), GolpesYBromas.nalgadaQuienRecibe());
        registrar(TipoInteraccion.ENTREGAR_OBJETO, Entregas.entregarObjetoQuienDa(), Entregas.entregarObjetoQuienRecibe());
        registrar(TipoInteraccion.ENTREGAR_FLOR, Entregas.entregarFlorQuienDa(), Entregas.entregarFlorQuienRecibe());
    }

    /** Cosquillas no tiene: aunque te las hagan sin pedir, te ríes igual. */
    private static void registrarReaccionesForzadas() {
        DEL_RECEPTOR_FORZADO.put(TipoInteraccion.ABRAZAR, ReaccionesForzadas.abrazoForzado());
        DEL_RECEPTOR_FORZADO.put(TipoInteraccion.BESO_BOCA, ReaccionesForzadas.besoBocaForzado());
        DEL_RECEPTOR_FORZADO.put(TipoInteraccion.BESO_MEJILLA, ReaccionesForzadas.besoMejillaForzado());
        DEL_RECEPTOR_FORZADO.put(TipoInteraccion.PALMADITAS_CABEZA, ReaccionesForzadas.palmaditasForzadas());
        for (TipoInteraccion tipo : new TipoInteraccion[]{TipoInteraccion.CHOCAR_CINCO, TipoInteraccion.ESTRECHAR_MANO,
                TipoInteraccion.CHOCAR_PUNOS, TipoInteraccion.TOMAR_MANO, TipoInteraccion.SALUDO_COLEGA,
                TipoInteraccion.BESAR_MANO, TipoInteraccion.CONSOLAR, TipoInteraccion.BAILAR_JUNTOS}) {
            DEL_RECEPTOR_FORZADO.put(tipo, ReaccionesForzadas.incomodo());
        }
    }

    private static void registrar(TipoInteraccion tipo, Animacion actor, Animacion receptor) {
        DEL_ACTOR.put(tipo, actor);
        DEL_RECEPTOR.put(tipo, receptor);
    }
}

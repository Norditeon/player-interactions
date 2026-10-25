// Preferencias de cada jugador, guardadas en su ordenador (config/interacciones-client.toml):
// qué hay en cada casilla de su ruleta de emociones y de su rueda de interacciones. Las leen
// las ruedas y las escribe la pantalla de repertorio (todo en el cliente).
package com.leonardo.interacciones;

import com.leonardo.interacciones.dominio.TipoEmocion;
import com.leonardo.interacciones.dominio.TipoInteraccion;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ConfiguracionCliente {

    public static final int CASILLAS = 8;
    /** En el archivo, una casilla vacía se guarda como texto vacío. */
    private static final String VACIA = "";

    /** Por defecto queda una casilla vacía, que sirve para cortar la emoción en curso. */
    private static final List<String> RULETA_POR_DEFECTO = List.of(
            TipoEmocion.BAILAR.clave(), TipoEmocion.SALTAR_ALEGRIA.clave(), TipoEmocion.SALUDAR.clave(),
            TipoEmocion.LLORAR.clave(), TipoEmocion.SENTARSE.clave(), TipoEmocion.ENOJARSE.clave(),
            TipoEmocion.PATALEAR.clave(), VACIA);

    private static final List<String> RUEDA_POR_DEFECTO = List.of(
            TipoInteraccion.ABRAZAR.clave(), TipoInteraccion.CHOCAR_CINCO.clave(),
            TipoInteraccion.ESTRECHAR_MANO.clave(), TipoInteraccion.BESO_MEJILLA.clave(),
            TipoInteraccion.BESO_BOCA.clave(), TipoInteraccion.PALMADITAS_CABEZA.clave(),
            TipoInteraccion.CACHETADA.clave(), TipoInteraccion.TOMAR_MANO.clave());

    private static final ModConfigSpec.Builder CONSTRUCTOR = new ModConfigSpec.Builder();

    private static final ModConfigSpec.ConfigValue<List<? extends String>> RULETA = CONSTRUCTOR
            .comment("Emociones de cada casilla de la ruleta, en orden. \"\" = casilla vacía.",
                    "Más fácil de cambiar desde el juego: tecla de la ruleta y luego E.")
            .defineList("ruletaEmociones", RULETA_POR_DEFECTO, () -> VACIA, valor -> valor instanceof String);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> RUEDA = CONSTRUCTOR
            .comment("Interacciones de cada casilla de la rueda (clic derecho con la mano vacía). \"\" = vacía.",
                    "Más fácil de cambiar desde el juego: abre la rueda y pulsa E.")
            .defineList("ruedaInteracciones", RUEDA_POR_DEFECTO, () -> VACIA, valor -> valor instanceof String);

    public static final ModConfigSpec ESPECIFICACION = CONSTRUCTOR.build();

    private ConfiguracionCliente() {
    }

    /** Siempre CASILLAS elementos; null = casilla vacía (o con una clave que ya no existe). */
    public static List<TipoEmocion> ruleta() {
        return leer(RULETA, RULETA_POR_DEFECTO, clave -> TipoEmocion.porClave(clave).filter(TipoEmocion::elegible));
    }

    public static void guardarRuleta(List<TipoEmocion> casillas) {
        guardar(RULETA, casillas, TipoEmocion::clave);
    }

    /** Siempre CASILLAS elementos; null = casilla vacía. */
    public static List<TipoInteraccion> ruedaInteracciones() {
        return leer(RUEDA, RUEDA_POR_DEFECTO, clave -> TipoInteraccion.porClave(clave).filter(TipoInteraccion::enRepertorio));
    }

    public static void guardarRuedaInteracciones(List<TipoInteraccion> casillas) {
        guardar(RUEDA, casillas, TipoInteraccion::clave);
    }

    private static <T> List<T> leer(ModConfigSpec.ConfigValue<List<? extends String>> valor, List<String> porDefecto,
                                    Function<String, Optional<T>> buscar) {
        List<? extends String> guardadas = ESPECIFICACION.isLoaded() ? valor.get() : porDefecto;
        List<T> casillas = new ArrayList<>(CASILLAS);
        for (int i = 0; i < CASILLAS; i++) {
            casillas.add(buscar.apply(i < guardadas.size() ? guardadas.get(i) : VACIA).orElse(null));
        }
        return casillas;
    }

    private static <T> void guardar(ModConfigSpec.ConfigValue<List<? extends String>> valor, List<T> casillas,
                                    Function<T, String> clave) {
        valor.set(casillas.stream().map(tipo -> tipo == null ? VACIA : clave.apply(tipo)).toList());
        valor.save();
    }
}

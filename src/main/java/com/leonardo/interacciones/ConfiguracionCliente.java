// Preferencias de cada jugador, guardadas en su ordenador (config/interacciones-client.toml):
// qué emociones tiene en cada casilla de su ruleta. La lee la ruleta de emociones y la
// escribe la pantalla de repertorio (ambas en el cliente).
package com.leonardo.interacciones;

import com.leonardo.interacciones.dominio.TipoEmocion;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ConfiguracionCliente {

    public static final int CASILLAS_RULETA = 8;
    /** En el archivo, una casilla vacía se guarda como texto vacío. */
    private static final String VACIA = "";

    /** Por defecto queda una casilla vacía, que sirve para cortar la emoción en curso. */
    private static final List<String> RULETA_POR_DEFECTO = List.of(
            TipoEmocion.BAILAR.clave(), TipoEmocion.SALTAR_ALEGRIA.clave(), TipoEmocion.SALUDAR.clave(),
            TipoEmocion.LLORAR.clave(), TipoEmocion.SENTARSE.clave(), TipoEmocion.ENOJARSE.clave(),
            TipoEmocion.PATALEAR.clave(), VACIA);

    private static final ModConfigSpec.Builder CONSTRUCTOR = new ModConfigSpec.Builder();

    private static final ModConfigSpec.ConfigValue<List<? extends String>> RULETA = CONSTRUCTOR
            .comment("Emociones de cada casilla de la ruleta, en orden. \"\" = casilla vacía.",
                    "Más fácil de cambiar desde el juego: tecla de la ruleta y luego E.")
            .defineList("ruletaEmociones", RULETA_POR_DEFECTO, () -> VACIA, valor -> valor instanceof String);

    public static final ModConfigSpec ESPECIFICACION = CONSTRUCTOR.build();

    private ConfiguracionCliente() {
    }

    /** Siempre CASILLAS_RULETA elementos; null = casilla vacía (o con una clave que ya no existe). */
    public static List<TipoEmocion> ruleta() {
        List<? extends String> guardadas = ESPECIFICACION.isLoaded() ? RULETA.get() : RULETA_POR_DEFECTO;
        List<TipoEmocion> casillas = new ArrayList<>(CASILLAS_RULETA);
        for (int i = 0; i < CASILLAS_RULETA; i++) {
            String clave = i < guardadas.size() ? guardadas.get(i) : VACIA;
            casillas.add(TipoEmocion.porClave(clave).filter(TipoEmocion::elegible).orElse(null));
        }
        return casillas;
    }

    public static void guardarRuleta(List<TipoEmocion> casillas) {
        RULETA.set(casillas.stream().map(tipo -> tipo == null ? VACIA : tipo.clave()).toList());
        RULETA.save();
    }
}

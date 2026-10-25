package com.leonardo.interacciones.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * Todos los idiomas deben tener las mismas claves que el inglés (la referencia), con los mismos
 * marcadores %s, y las claves que se arman en tiempo de ejecución deben existir.
 */
class TraduccionesTest {

    private static final String REFERENCIA = "en_us";
    private static final String[] IDIOMAS = {"en_us", "es_es", "fr_fr", "de_de", "ru_ru", "ja_jp", "zh_cn"};
    private static final String[] SUFIJOS_INTERACCION = {"nombre", "pedido", "hiciste", "recibiste"};

    /** Los archivos lang son planos ("clave": "texto"), así que basta una expresión por línea. */
    private static final Pattern ENTRADA = Pattern.compile("^\\s*\"([^\"]+)\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"", Pattern.MULTILINE);
    private static final Pattern MARCADOR = Pattern.compile("%(\\d+\\$)?s");

    private static Map<String, String> cargar(String idioma) throws IOException {
        String ruta = "/assets/interacciones/lang/" + idioma + ".json";
        try (InputStream entrada = Objects.requireNonNull(TraduccionesTest.class.getResourceAsStream(ruta), ruta)) {
            Matcher entradas = ENTRADA.matcher(new String(entrada.readAllBytes(), StandardCharsets.UTF_8));
            return entradas.results().collect(Collectors.toMap(r -> r.group(1), r -> r.group(2)));
        }
    }

    private static long marcadores(String texto) {
        return MARCADOR.matcher(texto).results().count();
    }

    private static List<String> clavesDinamicas() {
        List<String> claves = new ArrayList<>();
        for (TipoInteraccion tipo : TipoInteraccion.values()) {
            for (String sufijo : SUFIJOS_INTERACCION) {
                claves.add(tipo.claveTraduccion(sufijo));
            }
        }
        for (TipoEmocion tipo : TipoEmocion.values()) {
            claves.add(tipo.claveTraduccion());
        }
        for (ResultadoPeticion resultado : ResultadoPeticion.values()) {
            claves.add(resultado.claveTraduccion());
        }
        return claves;
    }

    @Test
    void todasLasClavesDinamicasEstanTraducidas() throws IOException {
        for (String idioma : IDIOMAS) {
            Set<String> textos = cargar(idioma).keySet();
            List<String> faltan = clavesDinamicas().stream().filter(clave -> !textos.contains(clave)).toList();
            assertTrue(faltan.isEmpty(), idioma + " no traduce: " + faltan);
        }
    }

    @Test
    void todosLosIdiomasTienenLasMismasClavesYMarcadores() throws IOException {
        Map<String, String> referencia = cargar(REFERENCIA);
        for (String idioma : IDIOMAS) {
            Map<String, String> textos = cargar(idioma);
            assertEquals(referencia.keySet(), textos.keySet(), idioma);
            referencia.forEach((clave, texto) ->
                    assertEquals(marcadores(texto), marcadores(textos.get(clave)), idioma + " " + clave));
        }
    }
}

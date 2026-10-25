package com.leonardo.interacciones.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/** Las claves que se arman en tiempo de ejecución deben existir en los dos idiomas. */
class TraduccionesTest {

    private static final String[] IDIOMAS = {"es_es", "en_us"};
    private static final String[] SUFIJOS_INTERACCION = {"nombre", "pedido", "hiciste", "recibiste"};

    /** Los archivos lang son planos ("clave": "texto"), así que basta con leer las claves. */
    private static final Pattern CLAVE = Pattern.compile("^\\s*\"([^\"]+)\"\\s*:", Pattern.MULTILINE);

    private static Set<String> cargar(String idioma) throws IOException {
        String ruta = "/assets/interacciones/lang/" + idioma + ".json";
        try (InputStream entrada = Objects.requireNonNull(TraduccionesTest.class.getResourceAsStream(ruta), ruta)) {
            Matcher claves = CLAVE.matcher(new String(entrada.readAllBytes(), StandardCharsets.UTF_8));
            return claves.results().map(resultado -> resultado.group(1)).collect(Collectors.toSet());
        }
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
            Set<String> textos = cargar(idioma);
            List<String> faltan = clavesDinamicas().stream().filter(clave -> !textos.contains(clave)).toList();
            assertTrue(faltan.isEmpty(), idioma + " no traduce: " + faltan);
        }
    }

    @Test
    void ambosIdiomasTienenLasMismasClaves() throws IOException {
        assertEquals(cargar("es_es"), cargar("en_us"));
    }
}

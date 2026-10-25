// Catálogo de interacciones entre dos jugadores: los gestos de la rueda (mano vacía) y las
// entregas de objetos (clic derecho con algo en la mano).
// Lo usan la rueda (cliente) para mostrarlas, la red para enviarlas y el servidor
// para validarlas y ejecutarlas. No depende de Minecraft para poder testearse.
package com.leonardo.interacciones.dominio;

import java.util.Arrays;
import java.util.List;

public enum TipoInteraccion {
    // Distancias de contacto: el jugador mide 0.6 de ancho, así que por debajo de 0.6 los
    // cuerpos se empujarían; los brazos miden 0.75, lo que marca hasta dónde llegan las manos.
    ABRAZAR("abrazar", 70, 0.62, false),
    CHOCAR_CINCO("chocar_cinco", 26, 0.95, false),
    ESTRECHAR_MANO("estrechar_mano", 50, 0.95, false),
    BESO_MEJILLA("beso_mejilla", 40, 0.62, false),
    BESO_BOCA("beso_boca", 50, 0.62, false),
    PALMADITAS_CABEZA("palmaditas_cabeza", 60, 0.75, false),
    CHOCAR_PUNOS("chocar_punos", 26, 1.2, false),
    TOMAR_MANO("tomar_mano", 80, 0.8, false),
    ENTREGAR_OBJETO("entregar_objeto", 40, 0.9, true),
    ENTREGAR_FLOR("entregar_flor", 50, 0.75, true);

    /**
     * En qué punto del gesto de entrega (0..1) el objeto pasa de una mano a otra. Coincide con
     * el fotograma de CatalogoAnimaciones en el que las manos se juntan.
     */
    public static final float MOMENTO_ENTREGA = 0.45F;

    /** Identificador estable para claves de traducción (no cambia aunque se reordene el enum). */
    private final String clave;
    /** Cuánto dura la animación, en ticks (20 ticks = 1 segundo). */
    private final int duracionTicks;
    /** Distancia en bloques entre los centros de los dos jugadores durante el gesto. */
    private final double distanciaContacto;
    /** true = pasa un objeto de quien lo ofrece a quien lo acepta; no aparece en la rueda. */
    private final boolean entregaObjeto;

    TipoInteraccion(String clave, int duracionTicks, double distanciaContacto, boolean entregaObjeto) {
        this.clave = clave;
        this.duracionTicks = duracionTicks;
        this.distanciaContacto = distanciaContacto;
        this.entregaObjeto = entregaObjeto;
    }

    /** Los gestos que se eligen en la rueda, en su orden. */
    public static List<TipoInteraccion> deLaRueda() {
        return Arrays.stream(values()).filter(tipo -> !tipo.entregaObjeto).toList();
    }

    /**
     * Clave en los archivos lang. Sufijos usados: "nombre" (rueda), "pedido" (aviso al objetivo),
     * "hiciste" (mensaje al actor) y "recibiste" (mensaje al receptor). En las entregas el
     * segundo argumento es el objeto.
     */
    public String claveTraduccion(String sufijo) {
        return "interaccion.interacciones." + clave + "." + sufijo;
    }

    public int duracionTicks() {
        return duracionTicks;
    }

    public double distanciaContacto() {
        return distanciaContacto;
    }

    public boolean entregaObjeto() {
        return entregaObjeto;
    }

    /** Busca por posición sin lanzar excepción: los índices llegan por red y pueden ser inválidos. */
    public static TipoInteraccion porIndice(int indice) {
        TipoInteraccion[] todos = values();
        return indice >= 0 && indice < todos.length ? todos[indice] : ABRAZAR;
    }
}

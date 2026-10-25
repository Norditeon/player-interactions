// Catálogo de interacciones entre dos jugadores: gestos afectuosos, golpes y bromas (que no
// piden permiso) y entregas de objetos (clic derecho con algo en la mano).
// Lo usan la rueda y su repertorio (cliente), la red, y el servidor para validarlas y
// ejecutarlas. No depende de Minecraft para poder testearse.
package com.leonardo.interacciones.dominio;

import static com.leonardo.interacciones.dominio.ClaseInteraccion.ENTREGA;
import static com.leonardo.interacciones.dominio.ClaseInteraccion.GESTO;
import static com.leonardo.interacciones.dominio.ClaseInteraccion.SIN_PERMISO;
import static com.leonardo.interacciones.dominio.Disposicion.CARA_A_CARA;
import static com.leonardo.interacciones.dominio.Disposicion.POR_DETRAS;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum TipoInteraccion {
    // Distancias de contacto: el jugador mide 0.6 de ancho, así que por debajo de 0.6 los
    // cuerpos se empujarían; los brazos miden 0.75, lo que marca hasta dónde llegan las manos.
    // Daño en puntos de vida (2 = un corazón). Empuje: fuerza del retroceso (0.4 = golpe normal).
    // Columnas: clave, ticks, distancia de contacto, clase, disposición, daño, empuje.
    ABRAZAR("abrazar", 70, 0.62, GESTO, CARA_A_CARA, 0, 0),
    CHOCAR_CINCO("chocar_cinco", 26, 0.95, GESTO, CARA_A_CARA, 0, 0),
    ESTRECHAR_MANO("estrechar_mano", 50, 0.95, GESTO, CARA_A_CARA, 0, 0),
    BESO_MEJILLA("beso_mejilla", 40, 0.62, GESTO, CARA_A_CARA, 0, 0),
    BESO_BOCA("beso_boca", 50, 0.62, GESTO, CARA_A_CARA, 0, 0),
    PALMADITAS_CABEZA("palmaditas_cabeza", 60, 0.75, GESTO, CARA_A_CARA, 0, 0),
    CHOCAR_PUNOS("chocar_punos", 26, 1.2, GESTO, CARA_A_CARA, 0, 0),
    TOMAR_MANO("tomar_mano", 80, 0.8, GESTO, CARA_A_CARA, 0, 0),
    SALUDO_COLEGA("saludo_colega", 40, 0.7, GESTO, CARA_A_CARA, 0, 0),
    BESAR_MANO("besar_mano", 50, 0.85, GESTO, CARA_A_CARA, 0, 0),
    COSQUILLAS("cosquillas", 50, 0.62, GESTO, CARA_A_CARA, 0, 0),
    CONSOLAR("consolar", 70, 0.62, GESTO, CARA_A_CARA, 0, 0),
    BAILAR_JUNTOS("bailar_juntos", 80, 0.8, GESTO, CARA_A_CARA, 0, 0),
    CACHETADA("cachetada", 24, 0.75, SIN_PERMISO, CARA_A_CARA, 1, 0.15F),
    PUNETAZO("punetazo", 22, 0.95, SIN_PERMISO, CARA_A_CARA, 2, 0.5F),
    EMPUJAR("empujar", 20, 0.8, SIN_PERMISO, CARA_A_CARA, 0, 0.8F),
    PATADA_TRASERO("patada_trasero", 26, 0.85, SIN_PERMISO, POR_DETRAS, 2, 0.7F),
    NALGADA("nalgada", 22, 0.65, SIN_PERMISO, POR_DETRAS, 0, 0.1F),
    ENTREGAR_OBJETO("entregar_objeto", 40, 0.9, ENTREGA, CARA_A_CARA, 0, 0),
    ENTREGAR_FLOR("entregar_flor", 50, 0.75, ENTREGA, CARA_A_CARA, 0, 0);

    /**
     * En qué punto del gesto (0..1) las manos se tocan: ahí el objeto cambia de mano o llega el
     * golpe. Coincide con el fotograma de contacto de CatalogoAnimaciones.
     */
    public static final float MOMENTO_CONTACTO = 0.45F;

    /** Identificador estable: se guarda en la configuración y en las claves de traducción. */
    private final String clave;
    /** Cuánto dura la animación, en ticks (20 ticks = 1 segundo). */
    private final int duracionTicks;
    /** Distancia en bloques entre los centros de los dos jugadores durante el gesto. */
    private final double distanciaContacto;
    private final ClaseInteraccion clase;
    private final Disposicion disposicion;
    private final float danio;
    private final float empuje;

    TipoInteraccion(String clave, int duracionTicks, double distanciaContacto, ClaseInteraccion clase,
                    Disposicion disposicion, float danio, float empuje) {
        this.clave = clave;
        this.duracionTicks = duracionTicks;
        this.distanciaContacto = distanciaContacto;
        this.clase = clase;
        this.disposicion = disposicion;
        this.danio = danio;
        this.empuje = empuje;
    }

    /** Las que se pueden poner en la rueda (todo menos las entregas), en su orden. */
    public static List<TipoInteraccion> repertorio() {
        return Arrays.stream(values()).filter(TipoInteraccion::enRepertorio).toList();
    }

    public static Optional<TipoInteraccion> porClave(String clave) {
        return Arrays.stream(values()).filter(tipo -> tipo.clave.equals(clave)).findFirst();
    }

    /** Busca por posición sin lanzar excepción: los índices llegan por red y pueden ser inválidos. */
    public static TipoInteraccion porIndice(int indice) {
        TipoInteraccion[] todos = values();
        return indice >= 0 && indice < todos.length ? todos[indice] : ABRAZAR;
    }

    public String clave() {
        return clave;
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

    public ClaseInteraccion clase() {
        return clase;
    }

    public Disposicion disposicion() {
        return disposicion;
    }

    public float danio() {
        return danio;
    }

    public float empuje() {
        return empuje;
    }

    public boolean entregaObjeto() {
        return clase == ENTREGA;
    }

    public boolean requiereConsentimiento() {
        return clase != SIN_PERMISO;
    }

    public boolean enRepertorio() {
        return clase != ENTREGA;
    }

    /** Hace algo en el momento de contacto (daño o retroceso), además de animarse. */
    public boolean golpea() {
        return danio > 0 || empuje > 0;
    }
}

// Reglas del servidor, editables en config/interacciones-server.toml.
// Las lee GestorSolicitudes para validar peticiones y la rueda (cliente) para saber
// si mostrar el modo "Forzar"; NeoForge sincroniza los valores al conectarse.
package com.leonardo.interacciones;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Configuracion {

    private static final ModConfigSpec.Builder CONSTRUCTOR = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue PERMITIR_FORZAR = CONSTRUCTOR
            .comment("Si es false, nadie puede forzar interacciones: todas piden consentimiento.")
            .define("permitirForzar", true);

    public static final ModConfigSpec.DoubleValue DISTANCIA_MAXIMA = CONSTRUCTOR
            .comment("Distancia máxima en bloques para pedir una interacción. Al ocurrir, los",
                    "jugadores se acercan solos hasta tocarse.")
            .defineInRange("distanciaMaxima", 2.5, 1.5, 6.0);

    public static final ModConfigSpec.IntValue SEGUNDOS_PARA_RESPONDER = CONSTRUCTOR
            .comment("Segundos que tiene el otro jugador para aceptar o rechazar.")
            .defineInRange("segundosParaResponder", 15, 3, 120);

    public static final ModConfigSpec.IntValue SEGUNDOS_ENTRE_INTERACCIONES = CONSTRUCTOR
            .comment("Espera mínima entre dos peticiones del mismo jugador (evita spam).")
            .defineInRange("segundosEntreInteracciones", 3, 0, 60);

    public static final ModConfigSpec ESPECIFICACION = CONSTRUCTOR.build();

    private Configuracion() {
    }

    /** En el cliente la config llega del servidor al conectarse; antes de eso se asume que no. */
    public static boolean forzarPermitido() {
        return ESPECIFICACION.isLoaded() && PERMITIR_FORZAR.get();
    }
}

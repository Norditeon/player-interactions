// Tokens de color de la interfaz (sistema de diseño terminal/Aether), en ARGB.
// Única fuente de verdad de colores para todas las pantallas, ruedas y avisos del mod.
package com.leonardo.interacciones.cliente;

final class ColoresInterfaz {

    /** bg-secondary casi opaco: los paneles se dibujan sobre el mundo y deben leerse sobre cualquier fondo. */
    static final int FONDO_PANEL = 0xE616161B;
    /** bg-primary al 85 % para los sectores de la rueda. */
    static final int FONDO_SECTOR = 0xD9111116;
    static final int BORDE = 0x13FFFFFF;
    /** bg-hover: fila bajo el ratón. */
    static final int FONDO_HOVER = 0x0EFFFFFF;
    /** border-focus: separadores que tienen que verse sobre el mundo. */
    static final int BORDE_VISIBLE = 0x33FFFFFF;
    static final int TEXTO_PRINCIPAL = 0xFFE6E6EA;
    static final int TEXTO_SECUNDARIO = 0xBDE6E6EA;
    static final int TEXTO_TENUE = 0x85E6E6EA;
    static final int ACENTO = 0xFF89B4FA;
    /** Color semántico del modo "Forzar": avisa de que se salta el consentimiento. */
    static final int DESTRUCTIVO = 0xFFF38BA8;
    /** Velo más ligero que el scrim estándar para no tapar a los jugadores. */
    static final int VELO = 0x66050509;

    private static final int ALFA_SUAVE = 0x24; // 14 %, como accent-muted

    private ColoresInterfaz() {
    }

    /** Versión translúcida de un color para fondos de selección. */
    static int suave(int color) {
        return (ALFA_SUAVE << 24) | (color & 0x00FFFFFF);
    }
}

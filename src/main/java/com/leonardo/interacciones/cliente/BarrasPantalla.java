// Barras superior (título + estado) e inferior (atajos de teclado) de las pantallas del mod.
// Las usan RuedaRadial y PantallaRepertorio para que todas compartan la misma estructura.
package com.leonardo.interacciones.cliente;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

final class BarrasPantalla {

    static final int ALTO = 24;
    static final int RELLENO = 10;
    private static final int SEPARACION_ATAJOS = 14;

    private BarrasPantalla() {
    }

    static void cabecera(GuiGraphics graficos, Font fuente, int ancho, Component titulo, Component estado, int colorEstado) {
        graficos.fill(0, 0, ancho, ALTO, ColoresInterfaz.FONDO_PANEL);
        graficos.fill(0, ALTO, ancho, ALTO + 1, ColoresInterfaz.BORDE);
        int y = (ALTO - fuente.lineHeight) / 2 + 1;
        graficos.drawString(fuente, titulo, RELLENO, y, ColoresInterfaz.TEXTO_PRINCIPAL, false);
        graficos.drawString(fuente, estado, ancho - RELLENO - fuente.width(estado), y, colorEstado, false);
    }

    static void pie(GuiGraphics graficos, Font fuente, int ancho, int alto, List<RuedaRadial.Atajo> atajos) {
        int arriba = alto - ALTO;
        graficos.fill(0, arriba, ancho, alto, ColoresInterfaz.FONDO_PANEL);
        graficos.fill(0, arriba - 1, ancho, arriba, ColoresInterfaz.BORDE);
        int x = RELLENO;
        int y = arriba + (ALTO - fuente.lineHeight) / 2 + 1;
        for (RuedaRadial.Atajo atajo : atajos) {
            x = graficos.drawString(fuente, atajo.tecla(), x, y, ColoresInterfaz.ACENTO, false) + 4;
            x = graficos.drawString(fuente, Component.translatable(atajo.claveAccion()), x, y,
                    ColoresInterfaz.TEXTO_TENUE, false) + SEPARACION_ATAJOS;
        }
    }
}

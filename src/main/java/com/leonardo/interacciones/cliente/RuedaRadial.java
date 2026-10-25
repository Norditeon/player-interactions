// Base de las ruedas de selección (interacciones y emociones): dibujo, ratón y teclado.
// Disposición: barra superior (título y estado), anillo de sectores numerados con las
// etiquetas por fuera, un centro pulsable y barra inferior con atajos. Cada rueda concreta
// (PantallaRueda, PantallaRuedaEmociones) solo dice qué hay en cada sector y qué pasa al elegir.
package com.leonardo.interacciones.cliente;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

abstract class RuedaRadial extends Screen {

    /** Atajo mostrado en la barra inferior: tecla en acento + acción atenuada. */
    record Atajo(String tecla, String claveAccion) {
    }

    private static final float RADIO_INTERIOR = 34;
    private static final float RADIO_EXTERIOR = 92;
    private static final int SEPARACION_ETIQUETA = 8;
    private static final int SIN_SELECCION = -1;
    private static final int SELECCION_CENTRO = -2;

    private int seleccion = SIN_SELECCION;

    protected RuedaRadial(Component titulo) {
        super(titulo);
    }

    protected abstract int numeroSectores();

    /** Texto del sector, o null si la casilla está vacía. */
    protected abstract Component etiqueta(int sector);

    protected abstract void alElegir(int sector);

    /** Si el centro responde al clic (y se resalta al pasar el ratón). */
    protected abstract boolean centroActivo();

    protected abstract void alPulsarCentro();

    protected abstract Component textoCentro();

    /** Línea pequeña bajo el texto del centro (p. ej. la tecla que lo activa), o null. */
    protected abstract String ayudaCentro();

    /** Color de selección: el acento, o el destructivo si la acción salta el consentimiento. */
    protected abstract int colorDeSeleccion();

    protected abstract Component estadoCabecera();

    protected int colorEstadoCabecera() {
        return ColoresInterfaz.TEXTO_TENUE;
    }

    protected abstract List<Atajo> atajos();

    /** Sector bajo el ratón (0..n-1), o negativo si es el centro o nada. */
    protected int sectorBajoRaton() {
        return seleccion;
    }

    /** Teclas propias de cada rueda (además de 1..n y Esc). true si la tecla se usó. */
    protected boolean alPulsarTecla(int tecla) {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics graficos, int ratonX, int ratonY, float parcial) {
        // Velo plano en vez del desenfoque de vainilla: los jugadores tienen que seguir viéndose.
        graficos.fill(0, 0, width, height, ColoresInterfaz.VELO);
        graficos.flush();
    }

    @Override
    public void render(GuiGraphics graficos, int ratonX, int ratonY, float parcial) {
        renderBackground(graficos, ratonX, ratonY, parcial);
        seleccion = seleccionBajo(ratonX, ratonY);
        dibujarAnillo(graficos);
        dibujarCentro(graficos);
        for (int i = 0; i < numeroSectores(); i++) {
            dibujarEtiqueta(graficos, i);
        }
        BarrasPantalla.cabecera(graficos, font, width, title, estadoCabecera(), colorEstadoCabecera());
        BarrasPantalla.pie(graficos, font, width, height, atajos());
    }

    @Override
    public boolean mouseClicked(double ratonX, double ratonY, int boton) {
        if (boton != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return super.mouseClicked(ratonX, ratonY, boton);
        }
        int bajoRaton = seleccionBajo(ratonX, ratonY);
        if (bajoRaton == SELECCION_CENTRO) {
            alPulsarCentro();
        } else if (bajoRaton >= 0) {
            alElegir(bajoRaton);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int tecla, int codigoEscaneo, int modificadores) {
        int indice = tecla - GLFW.GLFW_KEY_1;
        if (indice >= 0 && indice < numeroSectores()) {
            alElegir(indice);
            return true;
        }
        return alPulsarTecla(tecla) || super.keyPressed(tecla, codigoEscaneo, modificadores);
    }

    private float anchoSector() {
        return Mth.TWO_PI / numeroSectores();
    }

    /** Qué hay bajo el ratón: un sector, el centro, o nada. */
    private int seleccionBajo(double ratonX, double ratonY) {
        double dx = ratonX - centroX();
        double dy = ratonY - centroY();
        if (Math.sqrt(dx * dx + dy * dy) < RADIO_INTERIOR) {
            return centroActivo() ? SELECCION_CENTRO : SIN_SELECCION;
        }
        // Fuera del anillo también cuenta: así basta con mover el ratón en la dirección deseada.
        double angulo = Mth.positiveModulo(Math.atan2(dx, -dy) + anchoSector() / 2, Mth.TWO_PI);
        return (int) (angulo / anchoSector()) % numeroSectores();
    }

    private void dibujarAnillo(GuiGraphics graficos) {
        float ancho = anchoSector();
        for (int i = 0; i < numeroSectores(); i++) {
            float inicio = i * ancho - ancho / 2;
            DibujoRueda.sector(graficos, centroX(), centroY(), RADIO_INTERIOR, RADIO_EXTERIOR,
                    inicio, inicio + ancho, ColoresInterfaz.FONDO_SECTOR);
            if (i == seleccion) {
                DibujoRueda.sector(graficos, centroX(), centroY(), RADIO_INTERIOR, RADIO_EXTERIOR,
                        inicio, inicio + ancho, ColoresInterfaz.suave(colorDeSeleccion()));
                // Borde exterior de 2 px, como la barra de selección de las listas.
                DibujoRueda.sector(graficos, centroX(), centroY(), RADIO_EXTERIOR - 2, RADIO_EXTERIOR,
                        inicio, inicio + ancho, colorDeSeleccion());
            }
            DibujoRueda.lineaRadial(graficos, centroX(), centroY(), RADIO_INTERIOR, RADIO_EXTERIOR,
                    inicio, ColoresInterfaz.BORDE_VISIBLE);
        }
        int fondoCentro = seleccion == SELECCION_CENTRO
                ? ColoresInterfaz.suave(colorDeSeleccion()) : ColoresInterfaz.FONDO_PANEL;
        DibujoRueda.sector(graficos, centroX(), centroY(), 0, RADIO_INTERIOR - 1, 0, Mth.TWO_PI, fondoCentro);
    }

    private void dibujarCentro(GuiGraphics graficos) {
        String ayuda = ayudaCentro();
        int y = (int) centroY() - (ayuda != null ? font.lineHeight : font.lineHeight / 2);
        textoCentrado(graficos, textoCentro(), y, colorDeSeleccion());
        if (ayuda != null) {
            textoCentrado(graficos, Component.literal(ayuda), y + font.lineHeight + 2, ColoresInterfaz.TEXTO_TENUE);
        }
    }

    private void dibujarEtiqueta(GuiGraphics graficos, int indice) {
        boolean elegida = indice == seleccion;
        float angulo = indice * anchoSector();
        float[] numero = DibujoRueda.punto(centroX(), centroY(), (RADIO_INTERIOR + RADIO_EXTERIOR) / 2, angulo);
        String digito = String.valueOf(indice + 1);
        graficos.drawString(font, digito, (int) numero[0] - font.width(digito) / 2, (int) numero[1] - font.lineHeight / 2,
                elegida ? colorDeSeleccion() : ColoresInterfaz.TEXTO_TENUE, false);

        Component texto = etiqueta(indice);
        boolean vacia = texto == null;
        Component nombre = vacia ? Component.translatable("interacciones.ruleta.vacia") : texto;
        float[] ancla = DibujoRueda.punto(centroX(), centroY(), RADIO_EXTERIOR + SEPARACION_ETIQUETA, angulo);
        float horizontal = Mth.sin(angulo);
        int ancho = font.width(nombre);
        // Derecha del anillo: alinear a la izquierda; izquierda: a la derecha; arriba/abajo: centrado.
        int x = horizontal > 0.1F ? (int) ancla[0] : horizontal < -0.1F ? (int) ancla[0] - ancho : (int) ancla[0] - ancho / 2;
        int y = (int) ancla[1] - font.lineHeight / 2 + (int) (-Mth.cos(angulo) * font.lineHeight / 2);
        int color = vacia ? ColoresInterfaz.TEXTO_TENUE
                : elegida ? ColoresInterfaz.TEXTO_PRINCIPAL : ColoresInterfaz.TEXTO_SECUNDARIO;
        graficos.drawString(font, nombre, x, y, color, false);
    }

    private void textoCentrado(GuiGraphics graficos, Component texto, int y, int color) {
        graficos.drawString(font, texto, (int) centroX() - font.width(texto) / 2, y, color, false);
    }

    private float centroX() {
        return width / 2.0F;
    }

    private float centroY() {
        return height / 2.0F;
    }
}

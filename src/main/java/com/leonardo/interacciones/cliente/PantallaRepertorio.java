// Editor de la ruleta de emociones. A la izquierda las 8 casillas de la ruleta; a la derecha
// todo el repertorio. Se elige una casilla (clic o 1-8) y luego una emoción para ponerla ahí.
// Al salir (Esc) guarda en ConfiguracionCliente y vuelve a PantallaRuedaEmociones.
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.ConfiguracionCliente;
import com.leonardo.interacciones.dominio.TipoEmocion;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

final class PantallaRepertorio extends Screen {

    private static final List<TipoEmocion> REPERTORIO = TipoEmocion.repertorio();
    private static final int MARGEN = 16;
    private static final int ANCHO_COLUMNA_RULETA = 150;
    private static final int ALTO_FILA = 14;
    /** Espacio entre la cabecera y la primera fila: lo ocupa la etiqueta de sección. */
    private static final int ALTO_ETIQUETA = 16;
    private static final int NINGUNA = -1;

    private final List<TipoEmocion> casillas = new ArrayList<>(ConfiguracionCliente.ruleta());
    private int casillaElegida;
    private int desplazamiento;

    PantallaRepertorio() {
        super(Component.translatable("interacciones.repertorio.titulo"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        ConfiguracionCliente.guardarRuleta(casillas);
        if (minecraft != null) {
            minecraft.setScreen(new PantallaRuedaEmociones());
        }
    }

    @Override
    public void renderBackground(GuiGraphics graficos, int ratonX, int ratonY, float parcial) {
        graficos.fill(0, 0, width, height, ColoresInterfaz.FONDO_SECTOR);
    }

    @Override
    public void render(GuiGraphics graficos, int ratonX, int ratonY, float parcial) {
        renderBackground(graficos, ratonX, ratonY, parcial);
        dibujarRuleta(graficos, filaRuletaBajo(ratonX, ratonY));
        dibujarRepertorio(graficos, filaRepertorioBajo(ratonX, ratonY));
        long ocupadas = casillas.stream().filter(Objects::nonNull).count();
        BarrasPantalla.cabecera(graficos, font, width, title,
                Component.translatable("interacciones.repertorio.ocupadas", ocupadas, casillas.size()),
                ColoresInterfaz.TEXTO_TENUE);
        BarrasPantalla.pie(graficos, font, width, height, List.of(
                new RuedaRadial.Atajo("1-" + casillas.size(), "interacciones.atajo.casilla"),
                new RuedaRadial.Atajo("clic", "interacciones.atajo.asignar"),
                new RuedaRadial.Atajo("supr", "interacciones.atajo.vaciar"),
                new RuedaRadial.Atajo("esc", "interacciones.atajo.guardar")));
    }

    @Override
    public boolean mouseClicked(double ratonX, double ratonY, int boton) {
        int casilla = filaRuletaBajo(ratonX, ratonY);
        if (casilla != NINGUNA) {
            casillaElegida = casilla;
            if (boton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                casillas.set(casilla, null);
            }
            return true;
        }
        int emocion = filaRepertorioBajo(ratonX, ratonY);
        if (emocion != NINGUNA) {
            asignar(REPERTORIO.get(emocion));
            return true;
        }
        return super.mouseClicked(ratonX, ratonY, boton);
    }

    @Override
    public boolean mouseScrolled(double ratonX, double ratonY, double giroX, double giroY) {
        desplazamiento = Math.max(0, Math.min(desplazamientoMaximo(), desplazamiento - (int) (giroY * ALTO_FILA)));
        return true;
    }

    @Override
    public boolean keyPressed(int tecla, int codigoEscaneo, int modificadores) {
        int indice = tecla - GLFW.GLFW_KEY_1;
        if (indice >= 0 && indice < casillas.size()) {
            casillaElegida = indice;
            return true;
        }
        if (tecla == GLFW.GLFW_KEY_DELETE || tecla == GLFW.GLFW_KEY_BACKSPACE) {
            casillas.set(casillaElegida, null);
            return true;
        }
        return super.keyPressed(tecla, codigoEscaneo, modificadores);
    }

    /** Una emoción solo puede estar en una casilla: si ya estaba en otra, se mueve. */
    private void asignar(TipoEmocion emocion) {
        int anterior = casillas.indexOf(emocion);
        if (anterior != NINGUNA) {
            casillas.set(anterior, null);
        }
        casillas.set(casillaElegida, emocion);
    }

    private void dibujarRuleta(GuiGraphics graficos, int bajoRaton) {
        int x = MARGEN;
        int y = arribaContenido();
        graficos.drawString(font, Component.translatable("interacciones.repertorio.seccion_ruleta"), x, y,
                ColoresInterfaz.TEXTO_TENUE, false);
        for (int i = 0; i < casillas.size(); i++) {
            int fila = y + ALTO_ETIQUETA + i * ALTO_FILA;
            boolean elegida = i == casillaElegida;
            dibujarFondoFila(graficos, x, fila, ANCHO_COLUMNA_RULETA, elegida, i == bajoRaton);
            graficos.drawString(font, String.valueOf(i + 1), x + 6, fila + 3,
                    elegida ? ColoresInterfaz.ACENTO : ColoresInterfaz.TEXTO_TENUE, false);
            TipoEmocion tipo = casillas.get(i);
            Component nombre = tipo == null ? Component.translatable("interacciones.ruleta.vacia")
                    : Component.translatable(tipo.claveTraduccion());
            graficos.drawString(font, nombre, x + 20, fila + 3,
                    tipo == null ? ColoresInterfaz.TEXTO_TENUE : ColoresInterfaz.TEXTO_PRINCIPAL, false);
        }
    }

    private void dibujarRepertorio(GuiGraphics graficos, int bajoRaton) {
        int x = izquierdaRepertorio();
        int ancho = width - MARGEN - x;
        int y = arribaContenido();
        graficos.drawString(font, Component.translatable("interacciones.repertorio.seccion_repertorio"), x, y,
                ColoresInterfaz.TEXTO_TENUE, false);
        graficos.enableScissor(x, arribaLista(), x + ancho, abajoLista());
        for (int i = 0; i < REPERTORIO.size(); i++) {
            int fila = arribaLista() + i * ALTO_FILA - desplazamiento;
            TipoEmocion tipo = REPERTORIO.get(i);
            int enCasilla = casillas.indexOf(tipo);
            dibujarFondoFila(graficos, x, fila, ancho, false, i == bajoRaton);
            graficos.drawString(font, Component.translatable(tipo.claveTraduccion()), x + 6, fila + 3,
                    enCasilla == NINGUNA ? ColoresInterfaz.TEXTO_SECUNDARIO : ColoresInterfaz.TEXTO_PRINCIPAL, false);
            if (enCasilla != NINGUNA) {
                Component marca = Component.translatable("interacciones.repertorio.en_casilla", enCasilla + 1);
                graficos.drawString(font, marca, x + ancho - 6 - font.width(marca), fila + 3,
                        enCasilla == casillaElegida ? ColoresInterfaz.ACENTO : ColoresInterfaz.TEXTO_TENUE, false);
            }
        }
        graficos.disableScissor();
    }

    /** Seleccionada: fondo de acento suave y barra de 2 px; bajo el ratón: fondo hover. */
    private void dibujarFondoFila(GuiGraphics graficos, int x, int y, int ancho, boolean elegida, boolean bajoRaton) {
        if (elegida) {
            graficos.fill(x, y, x + ancho, y + ALTO_FILA, ColoresInterfaz.suave(ColoresInterfaz.ACENTO));
            graficos.fill(x, y, x + 2, y + ALTO_FILA, ColoresInterfaz.ACENTO);
        } else if (bajoRaton) {
            graficos.fill(x, y, x + ancho, y + ALTO_FILA, ColoresInterfaz.FONDO_HOVER);
        }
    }

    private int filaRuletaBajo(double ratonX, double ratonY) {
        if (ratonX < MARGEN || ratonX >= MARGEN + ANCHO_COLUMNA_RULETA) {
            return NINGUNA;
        }
        int fila = (int) Math.floor((ratonY - arribaLista()) / ALTO_FILA);
        return ratonY >= arribaLista() && fila < casillas.size() ? fila : NINGUNA;
    }

    private int filaRepertorioBajo(double ratonX, double ratonY) {
        if (ratonX < izquierdaRepertorio() || ratonX >= width - MARGEN || ratonY < arribaLista() || ratonY >= abajoLista()) {
            return NINGUNA;
        }
        int fila = (int) Math.floor((ratonY - arribaLista() + desplazamiento) / ALTO_FILA);
        return fila < REPERTORIO.size() ? fila : NINGUNA;
    }

    private int desplazamientoMaximo() {
        return Math.max(0, REPERTORIO.size() * ALTO_FILA - (abajoLista() - arribaLista()));
    }

    private int arribaContenido() {
        return BarrasPantalla.ALTO + 1 + BarrasPantalla.RELLENO;
    }

    private int arribaLista() {
        return arribaContenido() + ALTO_ETIQUETA;
    }

    private int abajoLista() {
        return height - BarrasPantalla.ALTO - BarrasPantalla.RELLENO;
    }

    private int izquierdaRepertorio() {
        return MARGEN + ANCHO_COLUMNA_RULETA + MARGEN;
    }
}

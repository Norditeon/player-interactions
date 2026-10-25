// Aviso en pantalla cuando alguien te pide una interacción o te ofrece un objeto (con su
// icono), y respuesta con teclado.
// Dibuja la SolicitudActual como capa del HUD (registrada en RegistroCliente) y, cada tick,
// convierte las pulsaciones de TeclasInteraccion en un paquete ResponderSolicitud.
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.InteraccionesAmistosas;
import com.leonardo.interacciones.NombreObjeto;
import com.leonardo.interacciones.red.ResponderSolicitud;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = InteraccionesAmistosas.ID, value = Dist.CLIENT)
public final class AvisoSolicitud {

    private static final int RELLENO = 8;
    private static final int ESPACIO_LINEAS = 4;
    private static final int SEPARACION_ATAJOS = 12;
    /** Hueco que ocupa el icono del objeto ofrecido (16 px más separación). */
    private static final int ANCHO_ICONO = 20;

    private AvisoSolicitud() {
    }

    @SubscribeEvent
    public static void alTerminarTick(ClientTickEvent.Post evento) {
        boolean acepta = TeclasInteraccion.ACEPTAR.consumeClick();
        boolean rechaza = TeclasInteraccion.RECHAZAR.consumeClick();
        // Se consumen siempre para que una pulsación vieja no responda a la próxima solicitud.
        if (SolicitudActual.actual().isEmpty() || acepta == rechaza) {
            return;
        }
        PacketDistributor.sendToServer(new ResponderSolicitud(acepta));
        SolicitudActual.olvidar();
    }

    static void dibujar(GuiGraphics graficos, DeltaTracker tiempo) {
        SolicitudActual.actual().ifPresent(pendiente -> {
            Font fuente = Minecraft.getInstance().font;
            ItemStack objeto = pendiente.objeto();
            Component pregunta = Component.translatable(pendiente.tipo().claveTraduccion("pedido"),
                    pendiente.nombreSolicitante(), NombreObjeto.describir(objeto));
            int sangria = objeto.isEmpty() ? 0 : ANCHO_ICONO;
            Component aceptar = Component.translatable("interacciones.aviso.aceptar");
            Component rechazar = Component.translatable("interacciones.aviso.rechazar");
            String teclaAceptar = TeclasInteraccion.ACEPTAR.getTranslatedKeyMessage().getString();
            String teclaRechazar = TeclasInteraccion.RECHAZAR.getTranslatedKeyMessage().getString();
            String segundos = SolicitudActual.segundosRestantes() + " s";

            int anchoAtajos = fuente.width(teclaAceptar) + 4 + fuente.width(aceptar) + SEPARACION_ATAJOS
                    + fuente.width(teclaRechazar) + 4 + fuente.width(rechazar) + SEPARACION_ATAJOS + fuente.width(segundos);
            int ancho = Math.max(fuente.width(pregunta), anchoAtajos) + sangria + RELLENO * 2;
            int alto = fuente.lineHeight * 2 + ESPACIO_LINEAS + RELLENO * 2;
            int izquierda = (graficos.guiWidth() - ancho) / 2;
            int arriba = graficos.guiHeight() / 5;

            dibujarPanel(graficos, izquierda, arriba, ancho, alto);
            if (!objeto.isEmpty()) {
                graficos.renderItem(objeto, izquierda + RELLENO, arriba + (alto - 16) / 2);
                graficos.renderItemDecorations(fuente, objeto, izquierda + RELLENO, arriba + (alto - 16) / 2);
            }
            int x = izquierda + RELLENO + sangria;
            int y = arriba + RELLENO;
            graficos.drawString(fuente, pregunta, x, y, ColoresInterfaz.TEXTO_PRINCIPAL, false);
            y += fuente.lineHeight + ESPACIO_LINEAS;
            x = dibujarAtajo(graficos, fuente, x, y, teclaAceptar, aceptar);
            x = dibujarAtajo(graficos, fuente, x, y, teclaRechazar, rechazar);
            graficos.drawString(fuente, segundos, x, y, ColoresInterfaz.TEXTO_TENUE, false);
        });
    }

    /** Panel con borde de 1 px y barra de acento de 2 px a la izquierda (estado "seleccionado"). */
    private static void dibujarPanel(GuiGraphics graficos, int x, int y, int ancho, int alto) {
        graficos.fill(x, y, x + ancho, y + alto, ColoresInterfaz.FONDO_PANEL);
        graficos.renderOutline(x, y, ancho, alto, ColoresInterfaz.BORDE_VISIBLE);
        graficos.fill(x, y, x + 2, y + alto, ColoresInterfaz.ACENTO);
    }

    private static int dibujarAtajo(GuiGraphics graficos, Font fuente, int x, int y, String tecla, Component accion) {
        x = graficos.drawString(fuente, tecla, x, y, ColoresInterfaz.ACENTO, false) + 4;
        x = graficos.drawString(fuente, accion, x, y, ColoresInterfaz.TEXTO_SECUNDARIO, false);
        return x + SEPARACION_ATAJOS;
    }
}

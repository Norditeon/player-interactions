// Primitivas geométricas para la rueda: sectores de anillo y líneas radiales.
// GuiGraphics solo sabe dibujar rectángulos, así que aquí se envían triángulos a la GPU.
// La usa PantallaRueda. Ángulos en radianes: 0 = arriba, crecen en sentido horario.
package com.leonardo.interacciones.cliente;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

final class DibujoRueda {

    /** Cada cuántos radianes se parte el arco en triángulos: suficiente para que se vea curvo. */
    private static final float PASO_ARCO = 0.05F;

    private DibujoRueda() {
    }

    static void sector(GuiGraphics graficos, float centroX, float centroY,
                       float radioInterior, float radioExterior, float desde, float hasta, int color) {
        Matrix4f matriz = graficos.pose().last().pose();
        int pasos = Math.max(1, Mth.ceil((hasta - desde) / PASO_ARCO));
        dibujarTriangulos(buffer -> {
            for (int i = 0; i < pasos; i++) {
                float a = desde + (hasta - desde) * i / pasos;
                float b = desde + (hasta - desde) * (i + 1) / pasos;
                cuadrilatero(buffer, matriz, color,
                        punto(centroX, centroY, radioInterior, a), punto(centroX, centroY, radioExterior, a),
                        punto(centroX, centroY, radioExterior, b), punto(centroX, centroY, radioInterior, b));
            }
        });
    }

    static void lineaRadial(GuiGraphics graficos, float centroX, float centroY,
                            float radioInterior, float radioExterior, float angulo, int color) {
        // Media línea de grosor a cada lado del ángulo: 1 px de ancho en total.
        float mitadGrosor = 0.5F;
        float perpX = Mth.cos(angulo) * mitadGrosor;
        float perpY = Mth.sin(angulo) * mitadGrosor;
        float[] interior = punto(centroX, centroY, radioInterior, angulo);
        float[] exterior = punto(centroX, centroY, radioExterior, angulo);
        Matrix4f matriz = graficos.pose().last().pose();
        dibujarTriangulos(buffer -> cuadrilatero(buffer, matriz, color,
                new float[]{interior[0] - perpX, interior[1] - perpY},
                new float[]{exterior[0] - perpX, exterior[1] - perpY},
                new float[]{exterior[0] + perpX, exterior[1] + perpY},
                new float[]{interior[0] + perpX, interior[1] + perpY}));
    }

    static float[] punto(float centroX, float centroY, float radio, float angulo) {
        return new float[]{centroX + Mth.sin(angulo) * radio, centroY - Mth.cos(angulo) * radio};
    }

    private static void cuadrilatero(BufferBuilder buffer, Matrix4f matriz, int color,
                                     float[] a, float[] b, float[] c, float[] d) {
        vertice(buffer, matriz, a, color);
        vertice(buffer, matriz, b, color);
        vertice(buffer, matriz, c, color);
        vertice(buffer, matriz, a, color);
        vertice(buffer, matriz, c, color);
        vertice(buffer, matriz, d, color);
    }

    private static void vertice(BufferBuilder buffer, Matrix4f matriz, float[] punto, int color) {
        buffer.addVertex(matriz, punto[0], punto[1], 0).setColor(color);
    }

    private static void dibujarTriangulos(Consumer<BufferBuilder> rellenar) {
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        rellenar.accept(buffer);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // Sin culling: el orden de los vértices cambia según el lado del anillo.
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}

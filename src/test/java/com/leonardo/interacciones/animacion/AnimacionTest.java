package com.leonardo.interacciones.animacion;

import static com.leonardo.interacciones.animacion.Fotograma.en;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.leonardo.interacciones.dominio.TipoEmocion;
import com.leonardo.interacciones.dominio.TipoInteraccion;
import java.util.List;
import org.junit.jupiter.api.Test;

class AnimacionTest {

    private static final float TOLERANCIA = 1.0E-5F;

    private final Postura brazoArriba = Postura.activa().brazoDerecho(-2, 0, 0);
    private final Animacion simple = Animacion.de(
            en(0, Suavizado.LINEAL, Postura.REPOSO),
            en(0.5F, Suavizado.LINEAL, brazoArriba),
            en(1, Suavizado.LINEAL, Postura.REPOSO));

    @Test
    void enCadaFotogramaDevuelveSuPostura() {
        assertEquals(0, simple.posturaEn(0).peso(), TOLERANCIA);
        assertEquals(brazoArriba, simple.posturaEn(0.5F));
        assertEquals(0, simple.posturaEn(1).peso(), TOLERANCIA);
    }

    @Test
    void desdeElReposoElPesoSubeSinCambiarElAngulo() {
        Postura aMitad = simple.posturaEn(0.25F);

        assertEquals(0.5F, aMitad.peso(), TOLERANCIA);
        assertEquals(-2, aMitad.brazoDerecho().x(), TOLERANCIA);
        assertNull(aMitad.cabeza(), "lo que ningún fotograma toca se deja a Minecraft");
    }

    @Test
    void fueraDeRangoSeQuedaEnLosExtremos() {
        assertEquals(0, simple.posturaEn(-1).peso(), TOLERANCIA);
        assertEquals(0, simple.posturaEn(2).peso(), TOLERANCIA);
    }

    @Test
    void lasCurvasEmpiezanEnCeroYTerminanEnUno() {
        for (Suavizado curva : Suavizado.values()) {
            assertEquals(0, curva.aplicar(0), TOLERANCIA, curva.name());
            assertEquals(1, curva.aplicar(1), TOLERANCIA, curva.name());
        }
    }

    @Test
    void retrasarMantieneInicioYFinal() {
        List<Fotograma> retrasados = simple.retrasada(0.1F).fotogramas();

        assertEquals(0, retrasados.get(0).momento(), TOLERANCIA);
        assertEquals(0.55F, retrasados.get(1).momento(), TOLERANCIA);
        assertEquals(1, retrasados.get(2).momento(), TOLERANCIA);
    }

    @Test
    void elEspejoCambiaDeBrazoYDeSentido() {
        Postura derecha = Postura.activa().brazoDerecho(-1, -0.3F, 0.2F);

        Postura reflejada = derecha.espejo();

        assertNull(reflejada.brazoDerecho());
        assertEquals(new Giro(-1, 0.3F, -0.2F), reflejada.brazoIzquierdo());
    }

    @Test
    void lasInteraccionesTerminanEnReposoYEmpiezanEnReposoSalvoQuienEntrega() {
        for (TipoInteraccion tipo : TipoInteraccion.values()) {
            for (Papel papel : Papel.values()) {
                List<Fotograma> fotogramas = CatalogoAnimaciones.de(tipo, papel).fotogramas();
                String nombre = tipo + "/" + papel;
                comprobarMomentosOrdenados(fotogramas, nombre);
                // Quien entrega ya tenía la mano extendida (emoción de ofrecer): empieza con peso 1.
                float pesoInicial = tipo.entregaObjeto() && papel == Papel.ACTOR ? 1 : 0;
                assertEquals(pesoInicial, fotogramas.get(0).postura().peso(), TOLERANCIA, nombre);
                assertEquals(0, ultimo(fotogramas).postura().peso(), TOLERANCIA, nombre);
            }
        }
    }

    @Test
    void lasEmocionesCiclicasCierranElBucleYLasDemasVuelvenAlReposo() {
        for (TipoEmocion tipo : TipoEmocion.values()) {
            List<Fotograma> fotogramas = CatalogoEmociones.de(tipo).fotogramas();
            comprobarMomentosOrdenados(fotogramas, tipo.name());
            if (tipo.ciclica()) {
                assertEquals(fotogramas.get(0).postura(), ultimo(fotogramas).postura(), tipo.name());
                assertEquals(1, fotogramas.get(0).postura().peso(), TOLERANCIA, tipo.name());
            } else {
                assertEquals(0, fotogramas.get(0).postura().peso(), TOLERANCIA, tipo.name());
                assertEquals(0, ultimo(fotogramas).postura().peso(), TOLERANCIA, tipo.name());
            }
        }
    }

    @Test
    void elRepertorioSoloTieneEmocionesElegiblesYSeEncuentranPorClave() {
        assertTrue(TipoEmocion.repertorio().stream().allMatch(TipoEmocion::elegible));
        assertTrue(TipoEmocion.repertorio().size() >= 20);
        assertEquals(TipoEmocion.LLORAR, TipoEmocion.porClave("llorar").orElseThrow());
        assertTrue(TipoEmocion.porClave("no_existe").isEmpty());
    }

    private static Fotograma ultimo(List<Fotograma> fotogramas) {
        return fotogramas.get(fotogramas.size() - 1);
    }

    private static void comprobarMomentosOrdenados(List<Fotograma> fotogramas, String nombre) {
        assertEquals(0, fotogramas.get(0).momento(), TOLERANCIA, nombre);
        assertEquals(1, ultimo(fotogramas).momento(), TOLERANCIA, nombre);
        for (int i = 1; i < fotogramas.size(); i++) {
            assertTrue(fotogramas.get(i).momento() > fotogramas.get(i - 1).momento(), nombre + " #" + i);
        }
    }
}

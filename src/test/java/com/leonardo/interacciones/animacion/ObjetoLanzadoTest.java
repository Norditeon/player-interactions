package com.leonardo.interacciones.animacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.leonardo.interacciones.dominio.TipoEmocion;
import org.junit.jupiter.api.Test;

class ObjetoLanzadoTest {

    private static final double TOLERANCIA = 1.0E-6;

    @Test
    void elMalabarSubeHastaSuAlturaEnElPuntoMedioYVuelveALaMano() {
        float medio = (ObjetoLanzado.SOLTAR_MALABAR + ObjetoLanzado.ATRAPAR_MALABAR) / 2;

        assertEquals(ObjetoLanzado.ALTURA_MALABAR, ObjetoLanzado.alturaMalabar(medio).orElseThrow(), TOLERANCIA);
        assertEquals(0, ObjetoLanzado.alturaMalabar(ObjetoLanzado.SOLTAR_MALABAR).orElseThrow(), TOLERANCIA);
        assertTrue(ObjetoLanzado.alturaMalabar(0.1F).isEmpty(), "antes de soltarlo está en la mano");
        assertTrue(ObjetoLanzado.alturaMalabar(0.9F).isEmpty(), "después de atraparlo está en la mano");
    }

    @Test
    void laManoSoloSeVaciaMientrasElObjetoVuela() {
        assertTrue(ObjetoLanzado.manoSinObjeto(TipoEmocion.MALABAR, 0.5F));
        assertFalse(ObjetoLanzado.manoSinObjeto(TipoEmocion.MALABAR, 0.05F));
        assertFalse(ObjetoLanzado.manoSinObjeto(TipoEmocion.LANZAR, 0.2F));
        assertTrue(ObjetoLanzado.manoSinObjeto(TipoEmocion.LANZAR, 0.9F), "lanzado ya no vuelve");
        assertFalse(ObjetoLanzado.manoSinObjeto(TipoEmocion.COMER, 0.5F));
    }
}

package com.leonardo.interacciones.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TipoInteraccionTest {

    @Test
    void losGolpesNoPidenPermisoYHacenElDanioPedido() {
        assertFalse(TipoInteraccion.CACHETADA.requiereConsentimiento());
        assertEquals(1, TipoInteraccion.CACHETADA.danio(), "medio corazón");
        assertEquals(2, TipoInteraccion.PUNETAZO.danio(), "un corazón");
        assertEquals(2, TipoInteraccion.PATADA_TRASERO.danio(), "un corazón");
        assertEquals(0, TipoInteraccion.NALGADA.danio());
        assertFalse(TipoInteraccion.NALGADA.requiereConsentimiento());
    }

    @Test
    void laPatadaYLaNalgadaSeHacenPorDetras() {
        assertEquals(Disposicion.POR_DETRAS, TipoInteraccion.PATADA_TRASERO.disposicion());
        assertEquals(Disposicion.POR_DETRAS, TipoInteraccion.NALGADA.disposicion());
        assertEquals(Disposicion.CARA_A_CARA, TipoInteraccion.ABRAZAR.disposicion());
    }

    @Test
    void lasEntregasPidenPermisoPeroNoEstanEnLaRueda() {
        assertTrue(TipoInteraccion.ENTREGAR_FLOR.requiereConsentimiento());
        assertFalse(TipoInteraccion.repertorio().contains(TipoInteraccion.ENTREGAR_OBJETO));
        assertTrue(TipoInteraccion.repertorio().contains(TipoInteraccion.NALGADA));
    }

    @Test
    void ningunaDistanciaDeContactoHaceQueLosCuerposSeEmpujen() {
        for (TipoInteraccion tipo : TipoInteraccion.values()) {
            assertTrue(tipo.distanciaContacto() >= 0.6, tipo.name());
        }
    }

    @Test
    void seEncuentranPorClave() {
        assertEquals(TipoInteraccion.BESAR_MANO, TipoInteraccion.porClave("besar_mano").orElseThrow());
        assertTrue(TipoInteraccion.porClave("no_existe").isEmpty());
    }
}

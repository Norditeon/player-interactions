// Resultado de validar una petición de interacción en el servidor.
// Cada rechazo tiene su clave de traducción para explicárselo al jugador que la pidió.
package com.leonardo.interacciones.dominio;

public enum ResultadoPeticion {
    ACEPTADA("aceptada"),
    DEMASIADO_LEJOS("demasiado_lejos"),
    EN_ENFRIAMIENTO("en_enfriamiento"),
    OBJETIVO_OCUPADO("objetivo_ocupado"),
    FORZAR_DESACTIVADO("forzar_desactivado"),
    OBJETIVO_INVALIDO("objetivo_invalido"),
    YA_INTERACTUANDO("ya_interactuando"),
    NO_DISPONIBLE("no_disponible"),
    DISTINTA_ALTURA("distinta_altura"),
    SIN_ESPACIO("sin_espacio"),
    SIN_OBJETO("sin_objeto"),
    ENTREGA_FALLIDA("entrega_fallida"),
    GOLPES_DESACTIVADOS("golpes_desactivados");

    private final String clave;

    ResultadoPeticion(String clave) {
        this.clave = clave;
    }

    public String claveTraduccion() {
        return "interacciones.resultado." + clave;
    }
}

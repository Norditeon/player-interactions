// Rotación de una pieza del modelo (cabeza, torso, brazo) en radianes sobre sus tres ejes.
// Lo usan Postura para describir una pose y PosesInteraccion (cliente) para aplicarla.
package com.leonardo.interacciones.animacion;

public record Giro(float x, float y, float z) {

    /**
     * Mezcla dos giros. null significa "lo que haga Minecraft": si falta uno, se usa el otro
     * tal cual y el peso de la Postura se encarga de fundirlo con la pose normal.
     */
    public static Giro interpolar(Giro desde, Giro hasta, float t) {
        if (desde == null) {
            return hasta;
        }
        if (hasta == null) {
            return desde;
        }
        return new Giro(
                desde.x + (hasta.x - desde.x) * t,
                desde.y + (hasta.y - desde.y) * t,
                desde.z + (hasta.z - desde.z) * t);
    }
}

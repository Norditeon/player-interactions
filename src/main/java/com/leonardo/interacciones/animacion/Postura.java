// Pose completa de un jugador en un instante de la animación.
// La definen los fotogramas de CatalogoAnimaciones y CatalogoEmociones, la mezcla Animacion
// y la aplica PosesInteraccion (cliente) sobre el modelo.
//
// Convenciones (radianes, vistas desde el propio jugador):
//   brazo x negativo = levantarlo hacia delante (-1.57 horizontal, -3.14 vertical arriba);
//   brazo derecho y negativo / izquierdo y positivo = llevarlo hacia el centro del cuerpo;
//   brazo derecho z positivo / izquierdo z negativo = abrirlo hacia el lado (±2.6 = brazos en V arriba);
//   pierna x negativo = adelantarla, x positivo = atrasarla; z como en los brazos (abrir);
//   cabeza x positivo = mirar abajo, y positivo = girar a la izquierda, z = ladear;
//   torso x positivo = inclinarse hacia delante (sin mover los pies), z = balanceo lateral;
//   elevación en píxeles (16 = un bloque): negativo sube todo el cuerpo, positivo lo baja.
package com.leonardo.interacciones.animacion;

public record Postura(float peso, Giro cabeza, Giro torso, Giro brazoDerecho, Giro brazoIzquierdo,
                      Giro piernaDerecha, Giro piernaIzquierda, float elevacion) {

    /** Pose normal de Minecraft: peso 0, nada que cambiar. Empieza y termina cada gesto no cíclico. */
    public static final Postura REPOSO = new Postura(0, null, null, null, null, null, null, 0);

    /** Punto de partida para escribir una pose que se aplica por completo. */
    public static Postura activa() {
        return new Postura(1, null, null, null, null, null, null, 0);
    }

    public Postura conPeso(float nuevoPeso) {
        return new Postura(nuevoPeso, cabeza, torso, brazoDerecho, brazoIzquierdo, piernaDerecha, piernaIzquierda, elevacion);
    }

    public Postura cabeza(float x, float y, float z) {
        return new Postura(peso, new Giro(x, y, z), torso, brazoDerecho, brazoIzquierdo, piernaDerecha, piernaIzquierda, elevacion);
    }

    public Postura torso(float inclinacion, float balanceo) {
        return new Postura(peso, cabeza, new Giro(inclinacion, 0, balanceo), brazoDerecho, brazoIzquierdo,
                piernaDerecha, piernaIzquierda, elevacion);
    }

    public Postura brazoDerecho(float x, float y, float z) {
        return new Postura(peso, cabeza, torso, new Giro(x, y, z), brazoIzquierdo, piernaDerecha, piernaIzquierda, elevacion);
    }

    public Postura brazoIzquierdo(float x, float y, float z) {
        return new Postura(peso, cabeza, torso, brazoDerecho, new Giro(x, y, z), piernaDerecha, piernaIzquierda, elevacion);
    }

    public Postura piernaDerecha(float x, float y, float z) {
        return new Postura(peso, cabeza, torso, brazoDerecho, brazoIzquierdo, new Giro(x, y, z), piernaIzquierda, elevacion);
    }

    public Postura piernaIzquierda(float x, float y, float z) {
        return new Postura(peso, cabeza, torso, brazoDerecho, brazoIzquierdo, piernaDerecha, new Giro(x, y, z), elevacion);
    }

    public Postura elevacion(float pixeles) {
        return new Postura(peso, cabeza, torso, brazoDerecho, brazoIzquierdo, piernaDerecha, piernaIzquierda, pixeles);
    }

    /** La misma pose con izquierda y derecha intercambiadas (brazos, piernas, giro y ladeo). */
    public Postura espejo() {
        return new Postura(peso, reflejar(cabeza), reflejar(torso), reflejar(brazoIzquierdo), reflejar(brazoDerecho),
                reflejar(piernaIzquierda), reflejar(piernaDerecha), elevacion);
    }

    private static Giro reflejar(Giro giro) {
        return giro == null ? null : new Giro(giro.x(), -giro.y(), -giro.z());
    }

    public static Postura interpolar(Postura desde, Postura hasta, float t) {
        return new Postura(
                desde.peso + (hasta.peso - desde.peso) * t,
                Giro.interpolar(desde.cabeza, hasta.cabeza, t),
                Giro.interpolar(desde.torso, hasta.torso, t),
                Giro.interpolar(desde.brazoDerecho, hasta.brazoDerecho, t),
                Giro.interpolar(desde.brazoIzquierdo, hasta.brazoIzquierdo, t),
                Giro.interpolar(desde.piernaDerecha, hasta.piernaDerecha, t),
                Giro.interpolar(desde.piernaIzquierda, hasta.piernaIzquierda, t),
                desde.elevacion + (hasta.elevacion - desde.elevacion) * t);
    }
}

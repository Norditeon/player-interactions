// Geometría del acercamiento: dónde tiene que quedar cada jugador para que el gesto
// se toque de verdad, y por dónde pasa en cada tick del deslizamiento.
// La usa el servidor (PlanAcercamiento e InteraccionesEnCurso). Lógica pura, con tests.
package com.leonardo.interacciones.dominio;

public final class Acercamiento {

    /** Posición en el mundo, en bloques (y = pies). */
    public record Posicion(double x, double y, double z) {
    }

    public record Destinos(Posicion actor, Posicion receptor) {
    }

    private Acercamiento() {
    }

    /**
     * Ambos se mueven lo mismo hacia (o desde) su punto medio hasta quedar a la distancia de
     * contacto. La altura no cambia: solo se desliza en horizontal.
     */
    public static Destinos calcular(Posicion actor, Posicion receptor, double distanciaContacto) {
        double dx = receptor.x() - actor.x();
        double dz = receptor.z() - actor.z();
        double distancia = Math.sqrt(dx * dx + dz * dz);
        if (distancia < 1.0E-4) {
            // Están uno encima del otro: cualquier dirección sirve para separarlos.
            dx = 0;
            dz = 1;
            distancia = 1;
        }
        double haciaReceptorX = dx / distancia;
        double haciaReceptorZ = dz / distancia;
        double medioX = (actor.x() + receptor.x()) / 2;
        double medioZ = (actor.z() + receptor.z()) / 2;
        double mitad = distanciaContacto / 2;
        return new Destinos(
                new Posicion(medioX - haciaReceptorX * mitad, actor.y(), medioZ - haciaReceptorZ * mitad),
                new Posicion(medioX + haciaReceptorX * mitad, receptor.y(), medioZ + haciaReceptorZ * mitad));
    }

    /** Punto del trayecto para una fracción 0..1, arrancando y frenando suave. */
    public static Posicion intermedia(Posicion desde, Posicion hasta, double fraccion) {
        double t = Math.max(0, Math.min(1, fraccion));
        double suave = t * t * (3 - 2 * t);
        return new Posicion(
                desde.x() + (hasta.x() - desde.x()) * suave,
                desde.y() + (hasta.y() - desde.y()) * suave,
                desde.z() + (hasta.z() - desde.z()) * suave);
    }
}

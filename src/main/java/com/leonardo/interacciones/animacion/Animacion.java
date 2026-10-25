// Secuencia de fotogramas clave de un gesto y cálculo de la pose en cualquier instante.
// La construye CatalogoAnimaciones y la consulta PosesInteraccion (cliente) cada fotograma.
package com.leonardo.interacciones.animacion;

import java.util.Arrays;
import java.util.List;

public record Animacion(List<Fotograma> fotogramas) {

    public static Animacion de(Fotograma... fotogramas) {
        return new Animacion(List.of(fotogramas));
    }

    /** Vuelta cíclica: las poses se reparten a partes iguales y al final se vuelve a la primera. */
    public static Animacion ciclo(Suavizado curva, Postura... poses) {
        Fotograma[] fotogramas = new Fotograma[poses.length + 1];
        for (int i = 0; i < poses.length; i++) {
            fotogramas[i] = Fotograma.en((float) i / poses.length, i == 0 ? Suavizado.LINEAL : curva, poses[i]);
        }
        fotogramas[poses.length] = Fotograma.en(1, curva, poses[0]);
        return de(fotogramas);
    }

    /** Pose para un progreso 0..1, mezclando los dos fotogramas que lo rodean. */
    public Postura posturaEn(float progreso) {
        Fotograma anterior = fotogramas.get(0);
        if (progreso <= anterior.momento()) {
            return anterior.postura();
        }
        for (Fotograma siguiente : fotogramas) {
            if (progreso <= siguiente.momento()) {
                float tramo = siguiente.momento() - anterior.momento();
                float local = tramo <= 0 ? 1 : (progreso - anterior.momento()) / tramo;
                return Postura.interpolar(anterior.postura(), siguiente.postura(), siguiente.suavizado().aplicar(local));
            }
            anterior = siguiente;
        }
        return anterior.postura();
    }

    public Animacion espejada() {
        return new Animacion(fotogramas.stream()
                .map(f -> new Fotograma(f.momento(), f.suavizado(), f.postura().espejo()))
                .toList());
    }

    /**
     * La misma animación empezando un poco más tarde y terminando a la vez: sirve para que
     * quien recibe el gesto reaccione en vez de moverse en espejo exacto.
     */
    public Animacion retrasada(float retraso) {
        Fotograma[] desplazados = fotogramas.stream()
                .map(f -> f.momento() <= 0 ? f
                        : new Fotograma(retraso + f.momento() * (1 - retraso), f.suavizado(), f.postura()))
                .toArray(Fotograma[]::new);
        return new Animacion(Arrays.asList(desplazados));
    }
}

// Ruleta de emociones: se abre con su tecla (G por defecto) y muestra las 8 casillas que el
// jugador eligió (ConfiguracionCliente). Elegir una emoción envía HacerEmocion; una casilla
// vacía o el centro envían DetenerMiEmocion y cortan la que esté en curso.
// E abre PantallaRepertorio para cambiar qué hay en cada casilla; I, para cambiar la rueda de
// interacciones (así se puede editar sin tener a otro jugador delante).
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.ConfiguracionCliente;
import com.leonardo.interacciones.dominio.TipoEmocion;
import com.leonardo.interacciones.red.DetenerMiEmocion;
import com.leonardo.interacciones.red.HacerEmocion;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

final class PantallaRuedaEmociones extends RuedaRadial {

    private final List<TipoEmocion> casillas = ConfiguracionCliente.ruleta();

    PantallaRuedaEmociones() {
        super(Component.translatable("interacciones.ruleta.titulo"));
    }

    @Override
    protected int numeroSectores() {
        return casillas.size();
    }

    @Override
    protected Component etiqueta(int sector) {
        TipoEmocion tipo = casillas.get(sector);
        return tipo == null ? null : Component.translatable(tipo.claveTraduccion());
    }

    @Override
    protected void alElegir(int sector) {
        TipoEmocion tipo = casillas.get(sector);
        PacketDistributor.sendToServer(tipo == null ? DetenerMiEmocion.INSTANCIA : new HacerEmocion(tipo));
        onClose();
    }

    @Override
    protected boolean centroActivo() {
        return true;
    }

    @Override
    protected void alPulsarCentro() {
        PacketDistributor.sendToServer(DetenerMiEmocion.INSTANCIA);
        onClose();
    }

    @Override
    protected boolean alPulsarTecla(int tecla) {
        if (minecraft == null) {
            return false;
        }
        if (tecla == GLFW.GLFW_KEY_E) {
            minecraft.setScreen(new PantallaRepertorio<>(coleccion()));
            return true;
        }
        if (tecla == GLFW.GLFW_KEY_I) {
            minecraft.setScreen(new PantallaRepertorio<>(PantallaRueda.coleccion(PantallaRuedaEmociones::new)));
            return true;
        }
        return false;
    }

    /** Lo que edita PantallaRepertorio para esta ruleta. */
    static PantallaRepertorio.Coleccion<TipoEmocion> coleccion() {
        return new PantallaRepertorio.Coleccion<>(
                Component.translatable("interacciones.repertorio.titulo"),
                TipoEmocion.repertorio(), ConfiguracionCliente.ruleta(), ConfiguracionCliente::guardarRuleta,
                tipo -> Component.translatable(tipo.claveTraduccion()), PantallaRuedaEmociones::new);
    }

    @Override
    protected Component textoCentro() {
        return Component.translatable("interacciones.ruleta.detener");
    }

    @Override
    protected String ayudaCentro() {
        return null;
    }

    @Override
    protected int colorDeSeleccion() {
        return ColoresInterfaz.ACENTO;
    }

    @Override
    protected Component estadoCabecera() {
        return Component.translatable("interacciones.ruleta.estado");
    }

    @Override
    protected List<Atajo> atajos() {
        return List.of(
                new Atajo("1-" + casillas.size(), "interacciones.atajo.elegir"),
                new Atajo("clic", "interacciones.atajo.confirmar"),
                new Atajo("e", "interacciones.atajo.editar_emociones"),
                new Atajo("i", "interacciones.atajo.editar_interacciones"),
                new Atajo("esc", "interacciones.atajo.cerrar"));
    }
}

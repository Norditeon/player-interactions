// Rueda de interacciones que se abre al hacer clic derecho con la mano vacía sobre un jugador.
// La abre ClicEnJugador; al elegir una opción envía PedirInteraccion al servidor y se cierra.
// Sus 8 casillas las elige el jugador (ConfiguracionCliente; E abre PantallaRepertorio).
// El centro (o Tab) alterna entre Pedir y Forzar; los golpes y bromas se marcan en el color
// destructivo porque ocurren sin preguntar, sea cual sea el modo.
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.Configuracion;
import com.leonardo.interacciones.ConfiguracionCliente;
import com.leonardo.interacciones.dominio.ModoPeticion;
import com.leonardo.interacciones.dominio.TipoInteraccion;
import com.leonardo.interacciones.red.PedirInteraccion;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

final class PantallaRueda extends RuedaRadial {

    private final Player objetivo;
    private final List<TipoInteraccion> casillas = ConfiguracionCliente.ruedaInteracciones();
    private final boolean puedeForzar = Configuracion.forzarPermitido();
    private ModoPeticion modo = ModoPeticion.PEDIR;

    PantallaRueda(Player objetivo) {
        super(Component.translatable("interacciones.rueda.titulo", objetivo.getDisplayName()));
        this.objetivo = objetivo;
    }

    /**
     * Lo que edita PantallaRepertorio para la rueda de interacciones. volver: a qué pantalla regresar
     * (esta rueda apuntando al mismo jugador, o la ruleta de emociones si se editó desde allí).
     */
    static PantallaRepertorio.Coleccion<TipoInteraccion> coleccion(Supplier<Screen> volver) {
        return new PantallaRepertorio.Coleccion<>(
                Component.translatable("interacciones.repertorio.titulo_rueda"),
                TipoInteraccion.repertorio(), ConfiguracionCliente.ruedaInteracciones(),
                ConfiguracionCliente::guardarRuedaInteracciones,
                tipo -> Component.translatable(tipo.claveTraduccion("nombre")), volver);
    }

    @Override
    public void tick() {
        // Si el otro jugador se va o se aleja mucho, la rueda ya no tiene sentido.
        double limite = Configuracion.DISTANCIA_MAXIMA.get() * 2;
        if (minecraft == null || minecraft.player == null || objetivo.isRemoved()
                || minecraft.player.distanceToSqr(objetivo) > limite * limite) {
            onClose();
        }
    }

    @Override
    protected int numeroSectores() {
        return casillas.size();
    }

    @Override
    protected Component etiqueta(int sector) {
        TipoInteraccion tipo = casillas.get(sector);
        return tipo == null ? null : Component.translatable(tipo.claveTraduccion("nombre"));
    }

    @Override
    protected void alElegir(int sector) {
        TipoInteraccion tipo = casillas.get(sector);
        if (tipo != null) {
            PacketDistributor.sendToServer(new PedirInteraccion(objetivo.getId(), tipo, modo));
        }
        onClose();
    }

    @Override
    protected boolean centroActivo() {
        return puedeForzar;
    }

    @Override
    protected void alPulsarCentro() {
        modo = modo.alternar();
    }

    @Override
    protected boolean alPulsarTecla(int tecla) {
        if (tecla == GLFW.GLFW_KEY_TAB && puedeForzar) {
            modo = modo.alternar();
            return true;
        }
        if (tecla == GLFW.GLFW_KEY_E && minecraft != null) {
            minecraft.setScreen(new PantallaRepertorio<>(coleccion(() -> new PantallaRueda(objetivo))));
            return true;
        }
        return false;
    }

    @Override
    protected Component textoCentro() {
        return Component.translatable(claveModo());
    }

    @Override
    protected String ayudaCentro() {
        return puedeForzar ? "tab" : null;
    }

    @Override
    protected int colorDeSeleccion() {
        int sector = sectorBajoRaton();
        boolean sinPermiso = sector >= 0 && casillas.get(sector) != null && !casillas.get(sector).requiereConsentimiento();
        return sinPermiso || modo == ModoPeticion.FORZAR ? ColoresInterfaz.DESTRUCTIVO : ColoresInterfaz.ACENTO;
    }

    @Override
    protected Component estadoCabecera() {
        return puedeForzar ? Component.translatable(claveModo()) : Component.translatable("interacciones.rueda.solo_pedir");
    }

    @Override
    protected int colorEstadoCabecera() {
        boolean forzando = puedeForzar && modo == ModoPeticion.FORZAR;
        return forzando ? ColoresInterfaz.DESTRUCTIVO : puedeForzar ? ColoresInterfaz.ACENTO : ColoresInterfaz.TEXTO_TENUE;
    }

    @Override
    protected List<Atajo> atajos() {
        List<Atajo> atajos = new ArrayList<>(List.of(
                new Atajo("1-" + casillas.size(), "interacciones.atajo.elegir"),
                new Atajo("clic", "interacciones.atajo.confirmar")));
        if (puedeForzar) {
            atajos.add(new Atajo("tab", "interacciones.atajo.modo"));
        }
        atajos.add(new Atajo("e", "interacciones.atajo.editar"));
        atajos.add(new Atajo("esc", "interacciones.atajo.cerrar"));
        return atajos;
    }

    private String claveModo() {
        return modo == ModoPeticion.FORZAR ? "interacciones.modo.forzar" : "interacciones.modo.pedir";
    }
}

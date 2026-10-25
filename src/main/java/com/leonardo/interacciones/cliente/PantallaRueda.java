// Rueda de interacciones que se abre al hacer clic derecho con la mano vacía sobre un jugador.
// La abre ClicEnJugador; al elegir un gesto envía PedirInteraccion al servidor y se cierra.
// El centro (o Tab) alterna entre Pedir y Forzar, si el servidor permite forzar.
package com.leonardo.interacciones.cliente;

import com.leonardo.interacciones.Configuracion;
import com.leonardo.interacciones.dominio.ModoPeticion;
import com.leonardo.interacciones.dominio.TipoInteraccion;
import com.leonardo.interacciones.red.PedirInteraccion;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

final class PantallaRueda extends RuedaRadial {

    private static final List<TipoInteraccion> OPCIONES = TipoInteraccion.deLaRueda();

    private final Player objetivo;
    private final boolean puedeForzar = Configuracion.forzarPermitido();
    private ModoPeticion modo = ModoPeticion.PEDIR;

    PantallaRueda(Player objetivo) {
        super(Component.translatable("interacciones.rueda.titulo", objetivo.getDisplayName()));
        this.objetivo = objetivo;
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
        return OPCIONES.size();
    }

    @Override
    protected Component etiqueta(int sector) {
        return Component.translatable(OPCIONES.get(sector).claveTraduccion("nombre"));
    }

    @Override
    protected void alElegir(int sector) {
        PacketDistributor.sendToServer(new PedirInteraccion(objetivo.getId(), OPCIONES.get(sector), modo));
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
        return modo == ModoPeticion.FORZAR ? ColoresInterfaz.DESTRUCTIVO : ColoresInterfaz.ACENTO;
    }

    @Override
    protected Component estadoCabecera() {
        return puedeForzar ? Component.translatable(claveModo()) : Component.translatable("interacciones.rueda.solo_pedir");
    }

    @Override
    protected int colorEstadoCabecera() {
        return puedeForzar ? colorDeSeleccion() : ColoresInterfaz.TEXTO_TENUE;
    }

    @Override
    protected List<Atajo> atajos() {
        List<Atajo> atajos = new ArrayList<>(List.of(
                new Atajo("1-" + OPCIONES.size(), "interacciones.atajo.elegir"),
                new Atajo("clic", "interacciones.atajo.confirmar")));
        if (puedeForzar) {
            atajos.add(new Atajo("tab", "interacciones.atajo.modo"));
        }
        atajos.add(new Atajo("esc", "interacciones.atajo.cerrar"));
        return atajos;
    }

    private String claveModo() {
        return modo == ModoPeticion.FORZAR ? "interacciones.modo.forzar" : "interacciones.modo.pedir";
    }
}

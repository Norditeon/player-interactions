// Entregas de objetos entre jugadores: clic derecho con algo en la mano sobre otro jugador.
// Recibe OfrecerObjeto (vía RegistroRed), pide consentimiento con GestorSolicitudes, guarda
// qué se ofreció y pone a quien ofrece a extender la mano (EmocionesEnCurso). Cuando las
// manos se juntan, InteraccionesEnCurso llama a entregar() y el objeto cambia de inventario.
// Las flores (etiqueta minecraft:flowers) se entregan de una en una y con vergüenza.
package com.leonardo.interacciones.servidor;

import com.leonardo.interacciones.NombreObjeto;
import com.leonardo.interacciones.dominio.ModoPeticion;
import com.leonardo.interacciones.dominio.ResultadoPeticion;
import com.leonardo.interacciones.dominio.SolicitudPendiente;
import com.leonardo.interacciones.dominio.TipoEmocion;
import com.leonardo.interacciones.dominio.TipoInteraccion;
import com.leonardo.interacciones.red.OfrecerObjeto;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public final class GestorEntregas {

    /** Una oferta vale solo para el jugador al que se hizo: si ofreces otra cosa a otro, la anterior caduca. */
    private record Oferta(UUID receptor, ItemStack objeto) {
    }

    /** Qué ofrece cada jugador (por UUID de quien da). Solo el hilo principal del servidor lo toca. */
    private static final Map<UUID, Oferta> OFERTAS = new HashMap<>();

    private GestorEntregas() {
    }

    public static void alOfrecer(ServerPlayer dador, OfrecerObjeto peticion) {
        Entity entidad = dador.level().getEntity(peticion.idEntidadObjetivo());
        if (!(entidad instanceof ServerPlayer receptor) || receptor == dador) {
            AvisosJugador.resultado(dador, ResultadoPeticion.OBJETIVO_INVALIDO);
            return;
        }
        ItemStack enMano = dador.getMainHandItem();
        if (enMano.isEmpty()) {
            AvisosJugador.resultado(dador, ResultadoPeticion.SIN_OBJETO);
            return;
        }
        boolean esFlor = enMano.is(ItemTags.FLOWERS);
        ItemStack ofrecido = esFlor ? enMano.copyWithCount(1) : enMano.copy();
        TipoInteraccion tipo = esFlor ? TipoInteraccion.ENTREGAR_FLOR : TipoInteraccion.ENTREGAR_OBJETO;
        if (GestorSolicitudes.pedir(dador, receptor, tipo, ModoPeticion.PEDIR, ofrecido)) {
            OFERTAS.put(dador.getUUID(), new Oferta(receptor.getUUID(), ofrecido));
            EmocionesEnCurso.iniciar(dador, esFlor ? TipoEmocion.OFRECER_FLOR : TipoEmocion.OFRECER_OBJETO);
        }
    }

    /**
     * ¿Sigue teniendo en la mano lo que le ofreció a este receptor? (puede haber cambiado de
     * casilla, soltado el objeto u ofrecido otra cosa a otra persona después).
     */
    static boolean sigueEnMano(ServerPlayer dador, ServerPlayer receptor) {
        Oferta oferta = OFERTAS.get(dador.getUUID());
        ItemStack enMano = dador.getMainHandItem();
        return oferta != null && oferta.receptor().equals(receptor.getUUID())
                && ItemStack.isSameItemSameComponents(enMano, oferta.objeto())
                && enMano.getCount() >= oferta.objeto().getCount();
    }

    /** La solicitud terminó sin entrega (rechazo, caducidad, desconexión, sin sitio): baja la mano. */
    static void alCerrarSinEntrega(MinecraftServer servidor, SolicitudPendiente solicitud) {
        if (!solicitud.tipo().entregaObjeto()) {
            return;
        }
        Oferta oferta = OFERTAS.get(solicitud.solicitante());
        // Si después ofreció otra cosa a otra persona, esa oferta (y su mano extendida) siguen en pie.
        if (oferta == null || !oferta.receptor().equals(solicitud.objetivo())) {
            return;
        }
        OFERTAS.remove(solicitud.solicitante());
        ServerPlayer dador = servidor.getPlayerList().getPlayer(solicitud.solicitante());
        if (dador != null) {
            EmocionesEnCurso.detener(dador);
        }
    }

    static void olvidarOferta(UUID dador) {
        OFERTAS.remove(dador);
    }

    /** El objeto pasa de la mano de quien da al inventario de quien recibe (o a sus pies si no cabe). */
    static boolean entregar(ServerPlayer dador, ServerPlayer receptor, TipoInteraccion tipo) {
        if (!sigueEnMano(dador, receptor)) {
            OFERTAS.remove(dador.getUUID());
            AvisosJugador.resultado(dador, ResultadoPeticion.ENTREGA_FALLIDA);
            AvisosJugador.resultado(receptor, ResultadoPeticion.ENTREGA_FALLIDA);
            return false;
        }
        ItemStack ofrecido = OFERTAS.remove(dador.getUUID()).objeto();
        Component descripcion = NombreObjeto.describir(ofrecido);
        dador.getMainHandItem().shrink(ofrecido.getCount());
        ItemStack paraDar = ofrecido.copy();
        receptor.getInventory().add(paraDar);
        if (!paraDar.isEmpty()) {
            receptor.drop(paraDar, false);
        }
        receptor.level().playSound(null, receptor.getX(), receptor.getY(), receptor.getZ(),
                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5F, 1.2F);
        AvisosJugador.realizada(dador, receptor, tipo, false, descripcion);
        return true;
    }
}

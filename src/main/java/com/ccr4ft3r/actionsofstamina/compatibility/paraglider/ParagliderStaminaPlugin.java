package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.api.movement.Movement;
import tictim.paraglider.api.plugin.ParagliderPlugin;
import tictim.paraglider.api.stamina.Stamina;
import tictim.paraglider.api.stamina.StaminaFactory;
import tictim.paraglider.api.stamina.StaminaPlugin;
import tictim.paraglider.api.vessel.VesselContainer;
import tictim.paraglider.impl.stamina.BotWStamina;
import tictim.paraglider.impl.stamina.ServerBotWStamina;

/**
 * Paragliders (20.1.x) stamina plugin, discovered by Paragliders' own annotation scan (so it only loads when
 * Paragliders is present). While {@link ParagliderConfig#PARAGLIDE} is enabled, Paragliders reads its stamina from
 * the AoS {@link StaminaBackend} (1000 per feather, the same scale as Paragliders' 1000 per wheel) and neither
 * regenerates nor drains its own: the paragliding cost is AoS's {@link ParaglideAction} drain, and running or
 * swimming are charged by AoS's own sprint and swim actions only, since Paragliders' own {@link Stamina#update} never
 * runs. Otherwise everything falls through to the default BotW stamina.
 * <p>
 * The stamina wheel is hidden per player through {@link Stamina#renderStaminaWheel()} rather than
 * {@link StaminaPlugin#removeStaminaWheel()}, which Paragliders reads once while loading, before configs exist:
 * disabling the integration then brings the wheel back.
 */
@ParagliderPlugin
public class ParagliderStaminaPlugin implements StaminaPlugin {

    @Override
    public StaminaFactory getStaminaFactory() {
        return new AoSStaminaFactory();
    }

    public static class AoSStaminaFactory implements StaminaFactory {
        @Override
        @NotNull
        public Stamina createServerInstance(@NotNull ServerPlayer player) {
            return new AoSParagliderStamina(player, new ServerBotWStamina(VesselContainer.get(player)), true);
        }

        @Override
        @NotNull
        public Stamina createRemoteInstance(@NotNull Player player) {
            // A client only knows its own player's stamina: other players keep Paragliders' own.
            return new AoSParagliderStamina(player, new BotWStamina(VesselContainer.get(player)), false);
        }

        @Override
        @NotNull
        public Stamina createLocalClientInstance(@NotNull LocalPlayer player) {
            return localStamina(player);
        }

        /**
         * Takes {@link Object} so the verifier never checks {@link LocalPlayer} against {@link Player}: that check
         * would load the client-only class when this factory loads on a dedicated server.
         */
        private static Stamina localStamina(Object player) {
            Player local = (Player) player;
            return new AoSParagliderStamina(local, new BotWStamina(VesselContainer.get(local)), true);
        }
    }

    /** Wraps Paragliders' default stamina; the player is kept so no side-specific lookup is needed. */
    public static class AoSParagliderStamina implements Stamina {
        private final Player player;
        private final Stamina fallback;
        /** False for other players seen from a client, whose stamina isn't known there. */
        private final boolean readsBackend;

        public AoSParagliderStamina(Player player, Stamina fallback, boolean readsBackend) {
            this.player = player;
            this.fallback = fallback;
            this.readsBackend = readsBackend;
        }

        private boolean enabled() {
            return readsBackend && ParagliderConfig.PARAGLIDE.enabled();
        }

        private StaminaBackend backend() {
            return StaminaBackends.of(player);
        }

        @Override
        public int stamina() {
            return enabled() ? backend().availableStamina(player) : fallback.stamina();
        }

        @Override
        public void setStamina(int stamina) {
            if (!enabled()) fallback.setStamina(stamina);
        }

        @Override
        public int maxStamina() {
            return enabled() ? backend().maxStamina(player) : fallback.maxStamina();
        }

        @Override
        public boolean isDepleted() {
            if (!enabled()) return fallback.isDepleted();
            StaminaBackend backend = backend();
            return backend.exhausted(player) || backend.availableStamina(player) <= 0;
        }

        @Override
        public void setDepleted(boolean depleted) {
            fallback.setDepleted(depleted);
        }

        /** Paragliders' own regen/drain logic runs only when this compat is disabled. */
        @Override
        public void update(Movement movement) {
            if (!enabled()) fallback.update(movement);
        }

        @Override
        public int giveStamina(int amount, boolean simulate) {
            return enabled() ? 0 : fallback.giveStamina(amount, simulate);
        }

        @Override
        public int takeStamina(int amount, boolean simulate, boolean ignoreDepletion) {
            return enabled() ? 0 : fallback.takeStamina(amount, simulate, ignoreDepletion);
        }

        /** Paragliders' stamina wheel is hidden while AoS drives stamina. */
        @Override
        public boolean renderStaminaWheel() {
            return !enabled() && fallback.renderStaminaWheel();
        }
    }
}

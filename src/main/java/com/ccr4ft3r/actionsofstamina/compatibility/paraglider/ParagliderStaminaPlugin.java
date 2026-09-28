package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.api.plugin.ParagliderPlugin;
import tictim.paraglider.api.stamina.Stamina;
import tictim.paraglider.api.stamina.StaminaFactory;
import tictim.paraglider.api.stamina.StaminaPlugin;
import tictim.paraglider.impl.stamina.BotWStamina;

/**
 * Paragliders (21.1.x) stamina plugin, discovered by Paragliders' own annotation scan (so it only loads when
 * Paragliders is present). While {@link ParagliderConfig#PARAGLIDE} is enabled, Paragliders reads its stamina from
 * the AoS {@link StaminaBackend} (1000 per feather, the same scale as Paragliders' 1000 per wheel) and neither
 * regenerates nor drains its own: the paragliding cost is AoS's {@link ParaglideAction} drain, and running or
 * swimming are charged by AoS's own sprint and swim actions only, since Paragliders' state deltas never run.
 * Otherwise everything falls through to the default BotW stamina.
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
            return new AoSParagliderStamina(player, true);
        }

        @Override
        @NotNull
        public Stamina createRemoteInstance(@NotNull Player player) {
            // A client only knows its own player's stamina: other players keep Paragliders' own.
            return new AoSParagliderStamina(player, false);
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
            return new AoSParagliderStamina((Player) player, true);
        }
    }

    /** Wraps Paragliders' default stamina; the player is kept so no side-specific lookup is needed. */
    public static class AoSParagliderStamina implements Stamina {
        private final Player player;
        private final BotWStamina fallback;
        /** False for other players seen from a client, whose stamina isn't known there. */
        private final boolean readsBackend;

        public AoSParagliderStamina(Player player, boolean readsBackend) {
            this.player = player;
            this.fallback = new BotWStamina(player);
            this.readsBackend = readsBackend;
        }

        private boolean enabled() {
            return readsBackend && ParagliderConfig.PARAGLIDE.enabled();
        }

        private StaminaBackend backend() {
            return StaminaBackends.of(player);
        }

        @Override
        public double stamina() {
            return enabled() ? backend().availableStamina(player) : fallback.stamina();
        }

        @Override
        public void setStamina(double stamina, boolean sync) {
            if (!enabled()) fallback.setStamina(stamina, sync);
        }

        @Override
        public double maxStamina() {
            return enabled() ? backend().maxStamina(player) : fallback.maxStamina();
        }

        @Override
        public double extraStamina() {
            return enabled() ? 0 : fallback.extraStamina();
        }

        @Override
        public void setExtraStamina(double extraStamina, boolean sync) {
            if (!enabled()) fallback.setExtraStamina(extraStamina, sync);
        }

        @Override
        public boolean isDepleted() {
            if (!enabled()) return fallback.isDepleted();
            StaminaBackend backend = backend();
            return backend.exhausted(player) || backend.availableStamina(player) <= 0;
        }

        @Override
        public void setDepleted(boolean depleted, boolean sync) {
            fallback.setDepleted(depleted, sync);
        }

        @Override
        public boolean isDirty() {
            return fallback.isDirty();
        }

        @Override
        public void setDirty(boolean dirty) {
            fallback.setDirty(dirty);
        }

        @Override
        public double giveStamina(double amount, boolean simulate, boolean sync) {
            return enabled() ? 0 : fallback.giveStamina(amount, simulate, sync);
        }

        @Override
        public double takeStamina(double amount, boolean simulate, boolean ignoreDepletion, boolean silent,
                                  boolean a, boolean b) {
            return enabled() ? 0 : fallback.takeStamina(amount, simulate, ignoreDepletion, silent, a, b);
        }

        /** Paragliders' stamina wheel is hidden while AoS drives stamina. */
        @Override
        public boolean renderStaminaWheel() {
            return !enabled() && fallback.renderStaminaWheel();
        }

        /** Paragliders' own regen/drain logic runs only when this compat is disabled. */
        @Override
        public boolean updateWithDefaultLogic(boolean client) {
            return !enabled() && fallback.updateWithDefaultLogic(client);
        }
    }
}

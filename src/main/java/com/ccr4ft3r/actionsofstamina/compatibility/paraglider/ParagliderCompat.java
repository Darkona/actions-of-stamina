package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaUnits;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

/**
 * Paragliders compatibility, safe to load without it: calls into the mod go through {@link ParagliderBridge}. The
 * stamina side lives in {@link ParagliderStaminaPlugin}, which only Paragliders' own plugin scan ever loads.
 * <p>
 * Stamina Vessels (from shrines and bargains) make the AoS bar larger, {@code feathers_per_vessel} each, as they make
 * Paragliders' own wheel larger. Set on joining a level (login, respawn, dimension change) and then only when
 * Paragliders reports a vessel change, never polled.
 */
public final class ParagliderCompat {

    public static final String MOD_ID = "paraglider";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);
    /** Source of the vessels' max stamina bonus (Green Feathers: the id of its {@code max_feathers} modifier). */
    public static final ResourceLocation VESSELS = ActionsOfStamina.id("paragliders/stamina_vessels");

    private ParagliderCompat() {
    }

    public static boolean isActive() {
        return LOADED && ParagliderConfig.PARAGLIDE.enabled();
    }

    public static boolean isParagliding(Player player) {
        return isActive() && ParagliderBridge.isParagliding(player);
    }

    /** Server, on joining a level: listens for vessel changes and sets the bonus for the vessels the player has. */
    public static void onJoin(ServerPlayer player) {
        if (!LOADED) return;
        // Also with the integration off: a bonus set before the config changed is taken away.
        applyVessels(player, isActive() ? ParagliderBridge.listenAndCountVessels(player) : 0);
    }

    /** Server: sets the max stamina bonus for {@code vessels} Stamina Vessels on the active backend. */
    static void applyVessels(ServerPlayer player, int vessels) {
        int feathers = isActive() ? vessels * ParagliderConfig.FEATHERS_PER_VESSEL.getAsInt() : 0;
        StaminaBackends.server().setMaxBonus(player, VESSELS, feathers * StaminaUnits.PER_FEATHER);
    }
}

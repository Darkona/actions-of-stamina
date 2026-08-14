package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaUnits;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

/**
 * Paragliders compatibility, safe to load without it: calls into the mod go through {@link ParagliderBridge}.
 * Paragliders 1.7 has no stamina plugin API, so while the integration is enabled mixins on its player movement
 * ({@code ParagliderPlayerMovementMixin}) read its stamina from the AoS backend (1000 per feather, the same scale as
 * Paragliders' 1000 per wheel) and stop its own regeneration and drain: the paragliding cost is AoS's
 * {@link ParaglideAction} drain, and running or swimming are charged by AoS's own sprint and swim actions only. Its
 * stamina wheel is hidden ({@code ParagliderStaminaWheelMixin}). Disabled, Paragliders keeps its own stamina.
 * <p>
 * Stamina Vessels (from shrines and bargains) make the AoS bar larger, {@code feathers_per_vessel} each, as they make
 * Paragliders' own wheel larger. Set on joining a level (login, respawn, dimension change) and then only when
 * Paragliders marks a vessel change for syncing ({@code ParagliderServerMovementMixin}), never polled.
 */
public final class ParagliderCompat {

    public static final String MOD_ID = "paraglider";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);
    /** Source of the vessels' max stamina bonus (Green Feathers: the id of its {@code max_feathers} modifier). */
    public static final ResourceLocation VESSELS = ActionsOfStamina.id("paragliders/stamina_vessels");

    private ParagliderCompat() {
    }

    public static final ActionType PARAGLIDE = ActionTypes.register(ActionsOfStamina.id("paraglide"), ParagliderConfig.PARAGLIDE,
            ParagliderCompat::isActive, ParaglideAction::new);

    /** Mod construction: registers the action type above (set when this class loads). */
    public static void registerActions() {
    }

    public static boolean isActive() {
        return LOADED && ParagliderConfig.PARAGLIDE.enabled();
    }

    public static boolean isParagliding(Player player) {
        return isActive() && ParagliderBridge.isParagliding(player);
    }

    /**
     * From the mixins: whether this player's Paragliders stamina is AoS's. {@code remote} is true for other players
     * seen from a client, whose stamina isn't known there: they keep Paragliders' own.
     */
    public static boolean readsBackend(boolean remote) {
        return !remote && ParagliderConfig.PARAGLIDE.enabled();
    }

    /** From the mixins: the stamina Paragliders sees, what the backend can still spend. */
    public static int stamina(Player player) {
        return StaminaBackends.of(player).availableStamina(player);
    }

    public static int maxStamina(Player player) {
        return StaminaBackends.of(player).maxStamina(player);
    }

    /** From the mixins: Paragliders' depleted state (no paragliding, no sprinting) while AoS can't pay. */
    public static boolean depleted(Player player) {
        StaminaBackend backend = StaminaBackends.of(player);
        return backend.exhausted(player) || backend.availableStamina(player) <= 0;
    }

    /** Server, on joining a level: sets the bonus for the vessels the player has. */
    public static void onJoin(ServerPlayer player) {
        if (!LOADED) return;
        // Also with the integration off: a bonus set before the config changed is taken away.
        applyVessels(player, isActive() ? ParagliderBridge.staminaVessels(player) : 0);
    }

    /** Server, from the mixin: the player's Stamina Vessels changed (commands and bargains included). */
    public static void onVesselsChanged(ServerPlayer player) {
        if (LOADED) applyVessels(player, ParagliderBridge.staminaVessels(player));
    }

    /** Server: sets the max stamina bonus for {@code vessels} Stamina Vessels on the active backend. */
    static void applyVessels(ServerPlayer player, int vessels) {
        int feathers = isActive() ? vessels * ParagliderConfig.FEATHERS_PER_VESSEL.get() : 0;
        StaminaBackends.server().setMaxBonus(player, VESSELS, feathers * StaminaUnits.PER_FEATHER);
    }
}

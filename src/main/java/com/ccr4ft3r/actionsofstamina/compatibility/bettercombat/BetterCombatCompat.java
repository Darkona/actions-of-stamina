package com.ccr4ft3r.actionsofstamina.compatibility.bettercombat;

import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

/**
 * Better Combat compatibility, safe to load without it: calls into the mod go through {@link BetterCombatBridge}
 * (server and common) and {@link BetterCombatClientBridge} (client).
 * <p>
 * The server charges each swing when Better Combat's attack request arrives (a mixin at the head of its
 * {@code ServerNetwork.handleAttackRequest}), and drops the swing when it can't be paid. The client cancels the
 * upswing right away through Better Combat's own {@code ATTACK_START} event and {@code cancelUpswing()} API, so a
 * swing the player can't afford never plays out.
 */
public final class BetterCombatCompat {

    public static final String MOD_ID = "bettercombat";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);

    private BetterCombatCompat() {
    }

    public static boolean isActive() {
        return LOADED && BetterCombatConfig.SWING.enabled();
    }

    /** Mod bus (loading and reloading): the server config changed, so the category multipliers are parsed again. */
    public static void onConfigLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() != AoSServerConfig.SPEC) return;
        BetterCombatConfig.reloadCategories();
        if (LOADED) BetterCombatBridge.clearCategoryCache();
    }

    /** Game bus: datapacks reloaded or synced (Better Combat's weapon registry is rebuilt with them). */
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (LOADED) BetterCombatBridge.clearCategoryCache();
    }

    /** Client setup. */
    public static void initClient() {
        if (LOADED) BetterCombatClientBridge.register();
    }

    /** Whether Better Combat swings this item (so its swings, not the vanilla attack, are charged). */
    public static boolean handlesAttacksWith(ItemStack stack) {
        return isActive() && BetterCombatBridge.hasAttacks(stack);
    }

    /**
     * Charges a swing; false when it can't be paid and must be dropped. {@code onlyCheck}: decide without charging,
     * for the network thread, which must leave the charge to the server thread.
     */
    public static boolean chargeSwing(ServerPlayer player, int comboCount, boolean onlyCheck) {
        return !isActive() || BetterCombatBridge.chargeSwing(player, comboCount, onlyCheck);
    }
}

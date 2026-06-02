package com.ccr4ft3r.actionsofstamina.compatibility.bettercombat;

import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * Better Combat compatibility, safe to load without it: calls into the mod go through {@link BetterCombatBridge}
 * (server and common) and {@link BetterCombatClientBridge} (client).
 * <p>
 * The server charges each swing when Better Combat runs an attack request on the server thread (a mixin at the head
 * of the task its {@code ServerNetwork} schedules for it), and drops the swing when it can't be paid. The client cancels the
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

    /**
     * Mod bus (loading and reloading): the server config changed, so the category multipliers are parsed again.
     * <p>
     * Forge 43's file watcher, on its own thread, can report a reload while the stopping server unloads the config
     * (the file is saved on unload). Nothing to read then: development builds throw, production reads the defaults. The
     * next load parses the list again.
     */
    public static void onConfigLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() != AoSServerConfig.SPEC || !AoSServerConfig.SPEC.isLoaded()) return;
        try {
            BetterCombatConfig.reloadCategories();
        } catch (IllegalStateException unloaded) {
            return;
        }
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

    /** Charges a swing (server thread); false when it can't be paid and must be dropped. {@code onlyCheck}: decide without charging. */
    public static boolean chargeSwing(ServerPlayer player, int comboCount, boolean onlyCheck) {
        return !isActive() || BetterCombatBridge.chargeSwing(player, comboCount, onlyCheck);
    }
}

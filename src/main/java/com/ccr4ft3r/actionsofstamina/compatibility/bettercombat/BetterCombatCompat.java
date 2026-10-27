package com.ccr4ft3r.actionsofstamina.compatibility.bettercombat;

import com.ccr4ft3r.actionsofstamina.config.compat.BetterCombatConfig;
import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.content.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import dev.darkona.jdlib.api.compat.Bridges;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

public final class BetterCombatCompat {

    public static final String MOD_ID = "bettercombat";
    public static final boolean LOADED = Bridges.isLoaded(MOD_ID);

    public static final ActionType SWING = ActionType.register(ActionsOfStamina.id("bettercombat/swing"), MOD_ID, BetterCombatConfig.SWING);

    private BetterCombatCompat() {
    }

    public static void onConfigLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() != ActionsConfig.SPEC) return;
        BetterCombatConfig.reloadCategories();
        if (LOADED) BetterCombatBridge.clearCategoryCache();
    }

    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (LOADED) BetterCombatBridge.clearCategoryCache();
    }

    public static void initClient() {
        if (LOADED) BetterCombatClientBridge.register();
    }

    public static boolean handlesAttacksWith(ItemStack stack) {
        return SWING.enabled() && BetterCombatBridge.hasAttacks(stack);
    }

    public static boolean chargeSwing(ServerPlayer player, int comboCount, boolean onlyCheck) {
        return !SWING.enabled() || BetterCombatBridge.chargeSwing(player, comboCount, onlyCheck);
    }
}

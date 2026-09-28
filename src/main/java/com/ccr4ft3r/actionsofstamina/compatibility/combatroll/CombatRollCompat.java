package com.ccr4ft3r.actionsofstamina.compatibility.combatroll;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

/**
 * Combat Roll compatibility, safe to load without it: calls into the mod go through {@link CombatRollBridge}. The
 * server charges each roll on Combat Roll's {@code PLAYER_START_ROLLING} event; the client refuses to start a roll
 * the stamina can't pay for (a mixin on {@code RollManager.isRollAvailable}).
 */
public final class CombatRollCompat {

    public static final String MOD_ID = "combat_roll";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);
    static final ResourceLocation SOURCE = ActionsOfStamina.id("combat_roll/roll");

    private CombatRollCompat() {
    }

    public static boolean isActive() {
        return LOADED && CombatRollConfig.ROLL.enabled();
    }

    /** Common setup. */
    public static void init() {
        if (LOADED) CombatRollBridge.register();
    }

    /** Client, from the mixin: whether the local player can pay for a roll now. */
    public static boolean canRoll(Player player) {
        if (!isActive()) return true;
        return StaminaBackends.of(player).canSpend(player, SOURCE, CombatRollConfig.ROLL.cost());
    }
}

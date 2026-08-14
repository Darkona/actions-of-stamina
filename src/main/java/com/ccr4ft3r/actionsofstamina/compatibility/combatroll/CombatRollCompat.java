package com.ccr4ft3r.actionsofstamina.compatibility.combatroll;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

/**
 * Combat Roll compatibility, safe to load without it: calls into the mod go through {@link CombatRollBridge}. A roll
 * is the one-off action {@link #ROLL}: the server performs it on Combat Roll's {@code PLAYER_START_ROLLING} event; the
 * client refuses to start a roll the stamina can't pay for (a mixin on {@code RollManager.isRollAvailable}).
 */
public final class CombatRollCompat {

    public static final String MOD_ID = "combatroll";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);

    public static final ActionType ROLL = ActionTypes.register(ActionsOfStamina.id("combat_roll/roll"), CombatRollConfig.ROLL,
            CombatRollCompat::isActive, Action::new);

    private CombatRollCompat() {
    }

    /** Mod construction: registers the action type above (set when this class loads). */
    public static void registerActions() {
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
        return !isActive() || PlayerActions.canPerform(player, ROLL);
    }
}

package com.ccr4ft3r.actionsofstamina.compatibility.combatroll;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.PlayerActions;
import dev.darkona.jdlib.api.compat.Bridges;
import net.minecraft.world.entity.player.Player;

public final class CombatRollCompat {

    public static final String MOD_ID = "combat_roll";
    public static final boolean LOADED = Bridges.isLoaded(MOD_ID);

    public static final ActionType ROLL = ActionType.register(ActionsOfStamina.id("combat_roll/roll"), CombatRollConfig.ROLL,
            CombatRollCompat::isActive, Action::new);

    private CombatRollCompat() {
    }

    public static void registerActions() {
    }

    public static boolean isActive() {
        return LOADED && CombatRollConfig.ROLL.enabled();
    }

    public static void init() {
        if (LOADED) CombatRollBridge.register();
    }

    public static boolean canRoll(Player player) {
        return !isActive() || PlayerActions.canPerform(player, ROLL);
    }
}

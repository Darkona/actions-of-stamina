package com.ccr4ft3r.actionsofstamina.compatibility.epicfight;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import net.minecraft.world.entity.player.Player;
import dev.darkona.jdlib.api.compat.Bridges;

public final class EpicFightCompat {

    public static final String MOD_ID = "epicfight";
    public static final boolean LOADED = Bridges.isLoaded(MOD_ID);

    public static final ActionType DODGE = skill("dodge", EpicFightConfig.DODGE);
    public static final ActionType GUARD = skill("guard", EpicFightConfig.GUARD);
    public static final ActionType INNATE = skill("innate", EpicFightConfig.INNATE);
    public static final ActionType MOVER = skill("mover", EpicFightConfig.MOVER);
    public static final ActionType BASIC_ATTACK = skill("basic_attack", EpicFightConfig.BASIC_ATTACK);

    private EpicFightCompat() {
    }

    private static ActionType skill(String name, ActionCostConfig costs) {
        return ActionTypes.register(ActionsOfStamina.id("epicfight/" + name), costs, () -> isActive() && costs.enabled(), Action::new);
    }

    public static void registerActions() {
    }

    public static boolean isActive() {
        return LOADED && EpicFightConfig.ENABLED.getAsBoolean();
    }

    public static boolean inBattleMode(Player player) {
        return isActive() && EpicFightConfig.BASIC_ATTACK.enabled() && EpicFightBridge.isEpicFightMode(player);
    }

    public static void init() {
        if (LOADED) EpicFightBridge.register();
    }
}

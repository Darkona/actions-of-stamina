package com.ccr4ft3r.actionsofstamina.compatibility.epicfight;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

/**
 * Epic Fight compatibility, safe to load without it: calls into the mod go through {@link EpicFightBridge}.
 * <p>
 * Epic Fight posts {@code SkillConsumeEvent} on both sides whenever a skill is about to spend its resource (on the
 * client only to check, on the server to consume). For the configured categories, a skill that would spend Epic
 * Fight stamina spends AoS stamina instead: its resource is switched to none when the AoS stamina can pay, and the
 * event is cancelled (the skill fails) when it can't.
 */
public final class EpicFightCompat {

    public static final String MOD_ID = "epicfight";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);

    public static final ActionType DODGE = skill("dodge", EpicFightConfig.DODGE);
    public static final ActionType GUARD = skill("guard", EpicFightConfig.GUARD);
    public static final ActionType INNATE = skill("innate", EpicFightConfig.INNATE);
    public static final ActionType MOVER = skill("mover", EpicFightConfig.MOVER);
    public static final ActionType BASIC_ATTACK = skill("basic_attack", EpicFightConfig.BASIC_ATTACK);

    private EpicFightCompat() {
    }

    /** A skill category, or the basic attack: a one-off action while Epic Fight and its section are on. */
    private static ActionType skill(String name, ActionCostConfig costs) {
        return ActionTypes.register(ActionsOfStamina.id("epicfight/" + name), costs, () -> isActive() && costs.enabled(), Action::new);
    }

    /** Mod construction: registers the action types above (set when this class loads). */
    public static void registerActions() {
    }

    public static boolean isActive() {
        return LOADED && EpicFightConfig.ENABLED.getAsBoolean();
    }

    /**
     * Whether Epic Fight is handling this player's attacks (its battle mode). Its basic attacks are then charged by
     * this compat, and AoS's vanilla attack cost stands aside so a swing isn't charged twice.
     */
    public static boolean inBattleMode(Player player) {
        return isActive() && EpicFightConfig.BASIC_ATTACK.enabled() && EpicFightBridge.isEpicFightMode(player);
    }

    /** Common setup. */
    public static void init() {
        if (LOADED) EpicFightBridge.register();
    }
}

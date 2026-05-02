package com.ccr4ft3r.actionsofstamina.compatibility.epicfight;

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

    private EpicFightCompat() {
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

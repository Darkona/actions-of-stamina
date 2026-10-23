package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.world.entity.player.Player;

public class MineAction extends Action {

    private final boolean scaleWithHardness;
    private final double maxHardnessMultiplier;
    private final boolean slowsWhenExhausted;
    private final float exhaustedBreakSpeed;
    private double pending;

    public MineAction(ActionType type) {
        super(type);
        this.scaleWithHardness = AoSServerConfig.MINE_SCALE_WITH_HARDNESS.getAsBoolean();
        this.maxHardnessMultiplier = AoSServerConfig.MINE_MAX_HARDNESS_MULTIPLIER.getAsDouble();
        this.slowsWhenExhausted = AoSServerConfig.MINE_BLOCK_WHEN_EXHAUSTED.getAsBoolean();
        this.exhaustedBreakSpeed = (float) AoSServerConfig.MINE_EXHAUSTED_BREAK_SPEED.getAsDouble();
    }

    public void mined(Player player, float hardness) {
        if (PlayerActions.isExempt(player)) return;
        pending += scaleWithHardness ? cost * Math.min(Math.max(hardness, 0.0), maxHardnessMultiplier) : cost;
        if (++timesPerformed < timesPerformedToExhaust) return;
        timesPerformed = 0;
        int amount = (int) Math.round(pending);
        pending = 0;
        if (amount > 0) StaminaBackends.of(player).spend(player, source, amount, cooldown);
    }

    public float breakSpeedMultiplier(Player player) {
        return slowsWhenExhausted && !canPerform(player) ? exhaustedBreakSpeed : 1.0f;
    }

}

package com.ccr4ft3r.actionsofstamina.content.actions;

import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class MineAction extends Action {

    private final boolean scaleWithHardness;
    private final double maxHardnessMultiplier;
    private final boolean slowsWhenExhausted;
    private final float exhaustedBreakSpeed;
    private double pending;

    public MineAction(ActionType type) {
        super(type);
        this.scaleWithHardness = ActionsConfig.MINE_SCALE_WITH_HARDNESS.getAsBoolean();
        this.maxHardnessMultiplier = ActionsConfig.MINE_MAX_HARDNESS_MULTIPLIER.getAsDouble();
        this.slowsWhenExhausted = ActionsConfig.MINE_BLOCK_WHEN_EXHAUSTED.getAsBoolean();
        this.exhaustedBreakSpeed = (float) ActionsConfig.MINE_EXHAUSTED_BREAK_SPEED.getAsDouble();
    }

    @Override
    public void onBlockBroken(Player player, BlockEvent.BreakEvent event) {
        mined(player, event.getState().getDestroySpeed(event.getLevel(), event.getPos()));
    }

    @Override
    public void onBreakSpeed(Player player, PlayerEvent.BreakSpeed event) {
        float multiplier = breakSpeedMultiplier(player);
        if (multiplier < 1.0f) event.setNewSpeed(event.getNewSpeed() * multiplier);
    }

    public void mined(Player player, float hardness) {
        if (PlayerActions.isExempt(player)) return;
        pending += scaleWithHardness ? cost * Math.min(Math.max(hardness, 0.0), maxHardnessMultiplier) : cost;
        if (++timesPerformed < timesPerformedToExhaust) return;
        timesPerformed = 0;
        int amount = (int) Math.round(pending);
        pending = 0;
        if (amount > 0) StaminaBackends.of(player).spend(player, type.id(), amount, cooldown);
    }

    public float breakSpeedMultiplier(Player player) {
        return slowsWhenExhausted && !canPerform(player) ? exhaustedBreakSpeed : 1.0f;
    }

}

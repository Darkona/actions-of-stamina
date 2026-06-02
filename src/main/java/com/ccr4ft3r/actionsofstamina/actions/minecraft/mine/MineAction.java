package com.ccr4ft3r.actionsofstamina.actions.minecraft.mine;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Mining: breaking a block, charged once every few blocks. With {@code scale_with_hardness}, each block adds the
 * cost times its hardness (stone 1.5, obsidian 50, capped). Mining is never cancelled: without the stamina, the
 * block breaks for free, and with {@code block_when_exhausted} breaking gets slower until the stamina is back.
 */
public class MineAction extends Action {

    public static final String actionName = "mine_action";
    public static final ResourceLocation SOURCE = ActionsOfStamina.id("mine");

    private final boolean scaleWithHardness;
    private final double maxHardnessMultiplier;
    private final boolean slowsWhenExhausted;
    private final float exhaustedBreakSpeed;
    /** Stamina added up by the blocks broken since the last charge. */
    private double pending;

    public MineAction() {
        super(SOURCE, AoSServerConfig.MINE);
        this.scaleWithHardness = AoSServerConfig.MINE_SCALE_WITH_HARDNESS.get();
        this.maxHardnessMultiplier = AoSServerConfig.MINE_MAX_HARDNESS_MULTIPLIER.get();
        this.slowsWhenExhausted = AoSServerConfig.MINE_BLOCK_WHEN_EXHAUSTED.get();
        this.exhaustedBreakSpeed = AoSServerConfig.MINE_EXHAUSTED_BREAK_SPEED.get().floatValue();
    }

    /** Server: a block of {@code hardness} was broken. Every {@code times_to_charge} blocks, their added-up cost is charged. */
    public void mined(Player player, float hardness) {
        if (PlayerActions.isNotExhaustable(player)) return;
        pending += scaleWithHardness ? cost * Math.min(Math.max(hardness, 0.0), maxHardnessMultiplier) : cost;
        if (++timesPerformed < timesPerformedToExhaust) return;
        timesPerformed = 0;
        int amount = (int) Math.round(pending);
        pending = 0;
        // All or nothing: a player short of it mines on for free (and slower, with block_when_exhausted).
        if (amount > 0) StaminaBackends.of(player).spend(player, source, amount, cooldown);
    }

    /**
     * Break speed multiplier (both sides, while mining: client and server must agree on the progress): below 1 when
     * {@code block_when_exhausted} and the player can't afford mining.
     */
    public float breakSpeedMultiplier(Player player) {
        return slowsWhenExhausted && !canPerform(player) ? exhaustedBreakSpeed : 1.0f;
    }

    @Override
    public String name() {
        return actionName;
    }

    @Override
    public int id() {
        return MINE;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}

package com.ccr4ft3r.actionsofstamina.actions.minecraft.jump;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class JumpAction implements Action {

    public static final ResourceLocation name = new ResourceLocation(ActionsOfStamina.MOD_ID, "jump_action");
    public int cooldown = 40;
    private int timesPerformed;
    private long lastPerformed;
    private int cost;
    private int minCost;
    private int timesPerformedToExhaust;

    public JumpAction() {
        this.timesPerformed = 0;
        this.lastPerformed = 0;
        this.cost = AoSCommonConfig.JUMPING_COST.get();
        this.minCost = AoSCommonConfig.JUMPING_MINIMUM_COST.get();
        this.timesPerformedToExhaust = AoSCommonConfig.JUMPING_TIMES_PERFORMED_TO_EXHAUST.get();
    }

    @Override
    public ResourceLocation getName() {
        return name;
    }

    @Override
    public double getFeathersPerSecond() {
        return 0;
    }

    @Override
    public boolean isRegenInhibitor() {
        return false;
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public int getMinCost() {
        return minCost;
    }

    @Override
    public int getTimesPerformedToExhaust() {
        return timesPerformedToExhaust;
    }

    @Override
    public boolean wasPerforming() {
        return false;
    }

    @Override
    public boolean isPerforming() {
        return false;
    }

    @Override
    public void setPerforming(boolean performing) {

    }

    @Override
    public void atStart(Player player) {

    }

    @Override
    public void atFinish(Player player) {

    }

    @Override
    public boolean perform(Player player) {
        return false;
    }

    @Override
    public int timesPerformed() {
        return 0;
    }

    @Override
    public long getLastPerformed() {
        return 0;
    }

    @Override
    public void tick() {

    }

    @Override
    public CompoundTag saveNBTData() {
        return null;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {

    }

    @Override
    public int getCooldown() {
        return 0;
    }
}

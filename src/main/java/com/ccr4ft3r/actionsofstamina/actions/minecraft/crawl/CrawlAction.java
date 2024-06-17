package com.ccr4ft3r.actionsofstamina.actions.minecraft.crawl;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.capability.IActionCapability;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class CrawlAction implements Action {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean canPerform(Player player) {
        return false;
    }

    @Override
    public double getFeathersPerSecond() {
        return 0;
    }

    @Override
    public boolean isRegenInhibitor() {
        return AoSCommonConfig.INHIBIT_REGEN_WHEN_CRAWLING.get();
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public int getMinCost() {
        return 0;
    }

    @Override
    public int getTimesPerformedToExhaust() {
        return 0;
    }


    @Override
    public boolean isPerforming() {
        return false;
    }

    @Override
    public void setPerforming(boolean performing) {

    }

    @Override
    public void beginPerforming(Player player) {

    }

    @Override
    public void finishPerforming(Player player) {

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
    public int getLastPerformed() {
        return 0;
    }

    @Override
    public void tick(Player player, IActionCapability capability) {

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

    @Override
    public int getStaminaCostPerTick() {
        return 0;
    }

    @Override
    public void setActionState(boolean state) {

    }
}

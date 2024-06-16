package com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ElytraFlightAction implements Action {
    @Override
    public ResourceLocation getName() {
        return null;
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

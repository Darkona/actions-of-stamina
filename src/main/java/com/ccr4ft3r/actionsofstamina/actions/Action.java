package com.ccr4ft3r.actionsofstamina.actions;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface Action {

    ResourceLocation getName();

    double getFeathersPerSecond();

    boolean isRegenInhibitor();

    int getCost();

    int getMinCost();

    int getTimesPerformedToExhaust();

    boolean isPerforming();

    void setPerforming(boolean performing);

    void atStart(Player player);

    void atFinish(Player player);

    boolean perform(Player player);

    int timesPerformed();

    long getLastPerformed();

    void tick();

    default boolean hasEnoughFeathers(int feathers) {

        if (getMinCost() == 0) return true;
        int feathersMin = Math.max(getCost(), getMinCost());
        return feathers >= feathersMin;
    }

    CompoundTag saveNBTData();

    void loadNBTData(CompoundTag nbt);

    int getCooldown();
}

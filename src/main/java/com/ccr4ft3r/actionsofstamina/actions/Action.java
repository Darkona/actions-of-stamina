package com.ccr4ft3r.actionsofstamina.actions;


import com.ccr4ft3r.actionsofstamina.capability.IActionCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface Action {

    String getName();

    boolean canPerform(Player player);

    double getFeathersPerSecond();

    boolean isRegenInhibitor();

    int getCost();

    int getMinCost();

    int getTimesPerformedToExhaust();

    boolean isPerforming();

    void setPerforming(boolean performing);

    void beginPerforming(Player player);

    void finishPerforming(Player player);

    boolean perform(Player player);

    int timesPerformed();

    int getLastPerformed();

    void tick(Player player, IActionCapability capability);

    CompoundTag saveNBTData();

    void loadNBTData(CompoundTag nbt);

    int getCooldown();

    int getStaminaCostPerTick();

    void setActionState(boolean state);
}

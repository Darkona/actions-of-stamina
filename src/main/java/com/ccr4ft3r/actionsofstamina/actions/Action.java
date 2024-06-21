package com.ccr4ft3r.actionsofstamina.actions;


import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

public interface Action {

    String getName();

    String getDebugString();

    boolean canPerform(Player p);

    boolean wasPerforming();

    double getFeathersPerSecond();

    boolean isRegenInhibitor();

    int getCost();

    int getMinCost();

    int getTimesPerformedToExhaust();

    boolean isPerforming();

    void setPerforming(boolean performing);

    void beginPerforming(Player p);

    void finishPerforming(Player p);

    boolean perform(Player p);

    int timesPerformed();

    int getLastPerformed();

    void tick(Player p, PlayerActions capability, TickEvent.Phase phase);

    CompoundTag saveNBTData();

    void loadNBTData(CompoundTag nbt);

    int getCooldown();

    int getStaminaCostPerTick();

    void setActionState(boolean state);
}

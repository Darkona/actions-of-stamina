package com.ccr4ft3r.actionsofstamina.capability;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Map;
import java.util.Optional;

public interface IActionCapability {

    static boolean cannotBeExhausted(Player player) {
        return player == null || player instanceof FakePlayer || player.isCreative() || player.isSpectator();
    }

    Map<String, Boolean> getActionStates();

    boolean isClientMoving();

    void setClientMoving(boolean moving);

    Vec3 getLastPosition();

    void setLastPosition(Vec3 lastPosition);

    Map<String, Action> getEnabledActions();

    void addEnabledAction(Action action);

    Optional<Action> getAction(String actionId);

    CompoundTag saveNBTData();

    void loadNBTData(CompoundTag nbt);

    void copyFrom(IActionCapability oldStore);

    void setClientJumping(boolean b);
}

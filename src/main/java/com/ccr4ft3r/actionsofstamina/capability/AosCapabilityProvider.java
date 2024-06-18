package com.ccr4ft3r.actionsofstamina.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AosCapabilityProvider implements ICapabilitySerializable<CompoundTag> {

    public static Capability<PlayerActions> PLAYER_ACTIONS = CapabilityManager.get(new CapabilityToken<>() {
    });

    PlayerActions playerActions = new PlayerActions();
    final LazyOptional<PlayerActions> capOptional = LazyOptional.of(() -> playerActions);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_ACTIONS) {
            return capOptional.cast();
        } else return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return playerActions.saveNBTData();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        playerActions.loadNBTData(nbt);
    }
}

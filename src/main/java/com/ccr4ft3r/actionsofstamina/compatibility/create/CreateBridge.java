package com.ccr4ft3r.actionsofstamina.compatibility.create;

import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

final class CreateBridge {

    private CreateBridge() {
    }

    static void stop(BlockEntity blockEntity) {
        if (!blockEntity.isRemoved() && blockEntity instanceof HandCrankBlockEntity crank
                && !(blockEntity instanceof ValveHandleBlockEntity) && crank.inUse > 1) {
            crank.inUse = 1;
        }
    }
}

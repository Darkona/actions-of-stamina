package com.ccr4ft3r.actionsofstamina.compatibility.create;

import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Direct Create calls; only reached through {@link CreateCompat} when Create is loaded. */
final class CreateBridge {

    private CreateBridge() {
    }

    /**
     * Ends the crank's current turn on the next tick, where Create itself stops its rotation (the server then syncs
     * the speed). Another player still turning it simply turns it again. A valve handle's turn is a set angle: left alone.
     */
    static void stop(BlockEntity blockEntity) {
        // isRemoved on the BlockEntity type: through Create's class it would need Ponder on the compile classpath.
        if (!blockEntity.isRemoved() && blockEntity instanceof HandCrankBlockEntity crank
                && !(blockEntity instanceof ValveHandleBlockEntity) && crank.inUse > 1) {
            crank.inUse = 1;
        }
    }
}

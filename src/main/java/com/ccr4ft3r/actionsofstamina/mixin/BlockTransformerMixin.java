package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.till.TillAction;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Working a block with a tool, both sides: the block a transform would turn the clicked one into is charged as the
 * till action, before the block, the tool or the sound change. A change the player can't afford finds no block, so
 * nothing happens. The first transform that finds a block is the one that runs, so it is charged once.
 */
@Mixin(BlockTransformer.class)
public abstract class BlockTransformerMixin {

    @WrapOperation(method = "transformBlock", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/feature/stateproviders/BlockStateProvider;getOptionalState(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState actionsofstamina$workNeedsStamina(BlockStateProvider provider, LevelAccessor level, RandomSource random, BlockPos pos,
                                                         Operation<BlockState> original, @Local(argsOnly = true) UseOnContext context) {
        BlockState state = original.call(provider, level, random, pos);
        return state == null || TillAction.mayWork(context.getPlayer()) ? state : null;
    }
}

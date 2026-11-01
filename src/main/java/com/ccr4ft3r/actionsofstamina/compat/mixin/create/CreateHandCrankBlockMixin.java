package com.ccr4ft3r.actionsofstamina.compat.mixin.create;

import com.ccr4ft3r.actionsofstamina.compat.create.CreateCompat;
import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandCrankBlock.class)
public abstract class CreateHandCrankBlockMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$turnNeedsStamina(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                                   BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!CreateCompat.turnCrank(player, level, pos)) cir.setReturnValue(InteractionResult.FAIL);
    }
}

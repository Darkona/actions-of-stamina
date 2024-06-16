package com.ccr4ft3r.actionsofstamina.mixin;

import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.cannotBeExhausted;

@Pseudo
@Mixin(HandCrankBlock.class)
public class HandCrankBlockMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void stopCranking(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
       /* if (worldIn.isClientSide && shouldStop(player, CRANKING))
            cir.setReturnValue(InteractionResult.FAIL);
        if (!worldIn.isClientSide && shouldStop( player, CRANKING)) {
            getPlayerData(player).set(CRANKING, false, (ServerPlayer) player);
            cir.setReturnValue(InteractionResult.FAIL);
        }*/
    }

    @Inject(method = "use", at = @At("RETURN"))
    public void updateCranking(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (cannotBeExhausted(player) || cir.getReturnValue() != InteractionResult.SUCCESS)
            return;
        //getPlayerData(player).set(CRANKING, true, (ServerPlayer) player);
    }
}
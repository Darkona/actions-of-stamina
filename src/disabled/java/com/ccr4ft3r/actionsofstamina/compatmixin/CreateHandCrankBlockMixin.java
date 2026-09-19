package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.ccr4ft3r.actionsofstamina.compatibility.create.CreateCompat;
import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Create, both sides (applied only when it is loaded): each turn of a hand crank is reported to AoS, and a turn the
 * player can't afford doesn't happen (nor does the held item's own use). The valve handle overrides this method.
 */
@Mixin(HandCrankBlock.class)
public abstract class CreateHandCrankBlockMixin {

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$turnNeedsStamina(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                                   InteractionHand hand, BlockHitResult hit,
                                                   CallbackInfoReturnable<ItemInteractionResult> cir) {
        if (!CreateCompat.turnCrank(player, level, pos)) cir.setReturnValue(ItemInteractionResult.FAIL);
    }
}

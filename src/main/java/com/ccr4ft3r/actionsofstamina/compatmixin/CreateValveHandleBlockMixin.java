package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.ccr4ft3r.actionsofstamina.compatibility.create.CreateCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

/**
 * Create, both sides (applied only when it is loaded): a valve handle turn is reported to AoS when it happens (a handle
 * still turning refuses the click), and a turn the player can't afford doesn't happen (the click is still taken).
 * Dyeing the handle is left alone.
 */
@Mixin(value = ValveHandleBlock.class, remap = false)
public abstract class CreateValveHandleBlockMixin {

    @WrapOperation(method = "clicked", at = @At(value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/crank/ValveHandleBlock;onBlockEntityUse(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Ljava/util/function/Function;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult actionsofstamina$turnNeedsStamina(ValveHandleBlock block, BlockGetter level, BlockPos pos,
                                                                Function<?, InteractionResult> action,
                                                                Operation<InteractionResult> original,
                                                                @Local(argsOnly = true) Player player) {
        if (!CreateCompat.mayTurnValve(player)) return InteractionResult.PASS;
        InteractionResult result = original.call(block, level, pos, action);
        if (result == InteractionResult.SUCCESS) CreateCompat.valveTurned(player);
        return result;
    }
}

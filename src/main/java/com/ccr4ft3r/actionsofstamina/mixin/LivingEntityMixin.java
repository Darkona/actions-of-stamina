package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.climb.ClimbAction;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    /**
     * Climbing up without the stamina for it: the {@code onClimbable()} check that lifts an entity pushing against a
     * climbable (or jumping in it) says no, so the lift doesn't happen. Everything else about the climbable stays:
     * the slow fall, and holding on while sneaking. Only asked while pushing or jumping, out of water: a ladder under
     * water lifts through the water movement, which is left alone.
     */
    @WrapOperation(method = "handleRelativeFrictionAndCalculateMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onClimbable()Z"))
    private boolean actionsofstamina$climbUpWithStamina(LivingEntity self, Operation<Boolean> original) {
        if (!original.call(self)) return false;
        return !(self instanceof Player player) || ClimbAction.mayClimbUp(player);
    }
}

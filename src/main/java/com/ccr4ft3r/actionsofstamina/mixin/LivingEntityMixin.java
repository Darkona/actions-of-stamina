package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @WrapOperation(method = "handleRelativeFrictionAndCalculateMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onClimbable()Z"))
    private boolean actionsofstamina$climbUpWithStamina(LivingEntity self, Operation<Boolean> original) {
        if (!original.call(self)) return false;
        return !(self instanceof Player player) || VanillaActions.mayClimbUp(player);
    }
}

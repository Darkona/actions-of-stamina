package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.climb.ClimbAction;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    /**
     * Cancels a jump the player can't afford. Players jump through this method on the client; a server player's jump
     * is charged by {@code ServerPlayerMixin}, before the jump's statistic and food exhaustion.
     */
    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$stopJumping(CallbackInfo ci) {
        if (!((Object) this instanceof Player self) || self instanceof ServerPlayer) return;
        // A Wall-Jump TXF double jump already paid its own cost: it isn't a normal jump.
        if (WallJumpCompat.consumeDoubleJump(self)) return;
        if (!PlayerActions.perform(self, VanillaActions.JUMP)) ci.cancel();
    }

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

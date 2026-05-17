package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraAction;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    /** Cancels a jump the player can't afford. */
    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$stopJumping(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        // A Wall-Jump TXF double jump already paid its own cost: it isn't a normal jump.
        if (WallJumpCompat.consumeDoubleJump(self)) return;
        Action jump = PlayerActions.get(self).getAction(Action.JUMP);
        if (jump != null && !jump.perform(self)) {
            ci.cancel();
        }
    }

    /**
     * No gliding off with stamina wings the player can't afford. Both sides: the client then never asks, and the
     * server refuses a client that asks anyway. Only runs when a jump in the air tries to open the wings.
     */
    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$stopFallFlyingStart(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player) (Object) this;
        if (PlayerActions.isNotExhaustable(self)) return;
        Action elytra = PlayerActions.get(self).getAction(Action.ELYTRA);
        if (elytra != null && ElytraAction.wearsStaminaWings(self) && !elytra.canPerform(self)) {
            cir.setReturnValue(false);
        }
    }
}

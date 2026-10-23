package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.ElytraAction;
import com.ccr4ft3r.actionsofstamina.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$stopJumping(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (WallJumpCompat.consumeDoubleJump(self)) return;
        if (!PlayerActions.perform(self, VanillaActions.JUMP)) ci.cancel();
    }

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$stopFallFlyingStart(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player) (Object) this;
        if (ElytraAction.wearsStaminaWings(self) && !PlayerActions.canPerform(self, VanillaActions.ELYTRA)) {
            cir.setReturnValue(false);
        }
    }
}

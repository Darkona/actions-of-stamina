package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    /** Cancels a jump the player can't afford. */
    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$stopJumping(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        Action jump = PlayerActions.get(self).getAction(Action.JUMP);
        if (jump != null && !jump.perform(self)) {
            ci.cancel();
        }
    }
}

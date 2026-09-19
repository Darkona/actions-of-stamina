package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraAction;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    /**
     * No gliding off with stamina wings the player can't afford. Both sides: the client then never asks, and the
     * server refuses a client that asks anyway. Only runs when a jump in the air tries to open the wings.
     */
    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$stopFallFlyingStart(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player) (Object) this;
        if (ElytraAction.wearsStaminaWings(self) && !PlayerActions.canPerform(self, VanillaActions.ELYTRA)) {
            cir.setReturnValue(false);
        }
    }
}

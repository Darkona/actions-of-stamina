package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.config.AoSAction;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.shouldStop;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntityMixin {

    @Inject(method = "jumpFromGround", at = @At(value = "HEAD"), cancellable = true)
    public void stopJumping(CallbackInfo ci) {
        System.out.println("Entering PlayerMixin:stopJumping ");
        if (shouldStop(AoSAction.JUMPING)) {
            jumping = false;
            ci.cancel();
        }
    }

}

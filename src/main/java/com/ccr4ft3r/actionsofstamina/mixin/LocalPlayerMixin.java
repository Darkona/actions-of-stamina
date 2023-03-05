package com.ccr4ft3r.actionsofstamina.mixin;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;
import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.*;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @Shadow
    public abstract void setSprinting(boolean p_108751_);

    @Shadow
    public Input input;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSprinting()Z", ordinal = 2))
    public void aiStepInjected(CallbackInfo ci) {
        if (!hasEnoughFeathers(getProfile().costsForSprinting, getProfile().minForSprinting))
            setSprinting(false);
        if (!hasEnoughFeathers(getProfile().costsForJumping, getProfile().minForJumping)) {
            input.jumping = false;
        }
    }
}
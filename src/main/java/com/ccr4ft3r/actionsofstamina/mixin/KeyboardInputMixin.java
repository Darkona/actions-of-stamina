package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;
import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.*;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

    @Inject(method = "tick", at = @At(value = "RETURN"))
    public void tickInjected(boolean p_108582_, CallbackInfo ci) {
        if (getProfile().forJumping.get() && !hasEnoughFeathers(getProfile().costsForJumping, getProfile().minForJumping))
            jumping = false;
        boolean isCrawling = PlayerUtil.isCrawling(Minecraft.getInstance().player);
        if (getProfile().forCrawling.get() && !hasEnoughFeathers(getProfile().costsForCrawling, getProfile().minForCrawling) && isCrawling) {
            this.forwardImpulse = 0;
            this.leftImpulse = 0;
        }
    }
}
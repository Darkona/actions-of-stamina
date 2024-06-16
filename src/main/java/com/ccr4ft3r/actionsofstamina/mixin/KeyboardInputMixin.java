package com.ccr4ft3r.actionsofstamina.mixin;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

    @Inject(method = "tick", at = @At(value = "RETURN"))
    public void stopJumpingAndCrawling(boolean p_234118_, float p_234119_, CallbackInfo ci) {
        /*LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !PlayerUtil.cannotBeExhausted(player) && (PlayerUtil.isCrawling(player) && shouldStop(player, CRAWLING)) ||
                (shouldStop(player, SWIMMING) && PLAYER_DATA.isMoving() && player.isInWater())
        ) {
            this.forwardImpulse *= 0.2f;
            this.leftImpulse *= 0.2f;
        }*/
    }
}
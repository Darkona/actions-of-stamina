package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ccr4ft3r.actionsofstamina.config.AoSAction.*;
import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;
import static com.ccr4ft3r.actionsofstamina.events.ClientHandler.*;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

    @Inject(method = "tick", at = @At(value = "RETURN"))
    public void stopJumpingAndCrawling(boolean p_234118_, float p_234119_, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (  (PlayerUtil.isCrawling(player) && shouldStop(CRAWLING)) ||
              (shouldStop(SWIMMING)&& PLAYER_DATA.isMoving() && player.isInWater())
            ) {
            this.forwardImpulse *= 0.2f;
            this.leftImpulse *= 0.2f;
        }
    }
}
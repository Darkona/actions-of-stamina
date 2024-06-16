package com.ccr4ft3r.actionsofstamina.mixin;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends PlayerMixin {

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSprinting()Z", ordinal = 1))
    public void stopSprinting(CallbackInfo ci) {
        // if(shouldStop((Player) (Object) this, SPRINTING)) this.setSprinting(false);
    }


}
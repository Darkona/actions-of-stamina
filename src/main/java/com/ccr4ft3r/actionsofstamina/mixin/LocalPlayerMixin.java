package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintAction;
import com.ccr4ft3r.actionsofstamina.capability.AosCapabilityProvider;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends PlayerMixin {

    @Shadow
    private boolean wasSprinting;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSprinting()Z", ordinal = 1))
    public void stopSprinting(CallbackInfo ci) {

        this.getCapability(AosCapabilityProvider.PLAYER_ACTIONS, null)
            .ifPresent(a -> a.getAction(SprintAction.actionName)
                             .ifPresent(w -> {
                                 if (!w.canPerform((LocalPlayer) (Object) this)) {
                                     setSprinting(false);
                                 }
                             }));
    }


}
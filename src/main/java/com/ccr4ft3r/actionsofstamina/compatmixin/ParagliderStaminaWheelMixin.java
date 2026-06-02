package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.client.overlay.StaminaWheelOverlay;

/** Paragliders 1.7, client side (applied only when it is loaded): its stamina wheel is hidden while AoS drives stamina. */
@Mixin(value = StaminaWheelOverlay.class, remap = false)
public abstract class ParagliderStaminaWheelMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$hideWheel(CallbackInfo ci) {
        if (ParagliderCompat.isActive()) ci.cancel();
    }
}

package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.event.ParagliderClientEventHandler;

/**
 * Paragliders 1.6, client side (applied only when it is loaded): its in-game stamina wheel, drawn from its overlay
 * handler, is hidden while AoS drives stamina. The wheel on the statue bargain screen stays.
 */
@Mixin(value = ParagliderClientEventHandler.class, remap = false)
public abstract class ParagliderStaminaWheelMixin {

    @Inject(method = "afterGameOverlayRender", at = @At("HEAD"), cancellable = true)
    private static void actionsofstamina$hideWheel(CallbackInfo ci) {
        if (ParagliderCompat.isActive()) ci.cancel();
    }
}

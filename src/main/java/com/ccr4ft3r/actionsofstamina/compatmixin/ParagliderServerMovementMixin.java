package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.impl.movement.ServerPlayerMovement;

/**
 * Paragliders 20.1, server side (applied only when it is loaded): its vessel container has no change listener, but
 * every real change of a player's Stamina Vessels (commands and bargains included, never simulations) marks them for
 * syncing here, after the new count is set. AoS resizes the bar then.
 */
@Mixin(value = ServerPlayerMovement.class, remap = false)
public abstract class ParagliderServerMovementMixin {

    @Inject(method = "markStaminaVesselChanged", at = @At("HEAD"))
    private void actionsofstamina$vesselsChanged(CallbackInfo ci) {
        ParagliderCompat.onVesselsChanged(((ServerPlayerMovement) (Object) this).player());
    }
}

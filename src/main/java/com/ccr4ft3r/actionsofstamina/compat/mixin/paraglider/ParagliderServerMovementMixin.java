package com.ccr4ft3r.actionsofstamina.compat.mixin.paraglider;

import com.ccr4ft3r.actionsofstamina.compat.paraglider.ParagliderCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.impl.movement.ServerPlayerMovement;

@Mixin(value = ServerPlayerMovement.class, remap = false)
public abstract class ParagliderServerMovementMixin {

    @Inject(method = "markStaminaVesselChanged", at = @At("HEAD"))
    private void actionsofstamina$vesselsChanged(CallbackInfo ci) {
        ParagliderCompat.onVesselsChanged(((ServerPlayerMovement) (Object) this).player());
    }
}

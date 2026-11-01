package com.ccr4ft3r.actionsofstamina.compat.mixin.parcool;

import com.alrex.parcool.api.stamina.RegisterParCoolStaminaTypeEvent;
import com.ccr4ft3r.actionsofstamina.compat.parcool.ParcoolStaminaType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RegisterParCoolStaminaTypeEvent.class, remap = false)
public abstract class ParcoolStaminaTypeEventMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void actionsofstamina$registerStaminaType(CallbackInfo ci) {
        ParcoolStaminaType.register((RegisterParCoolStaminaTypeEvent) (Object) this);
    }
}

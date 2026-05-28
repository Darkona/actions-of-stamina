package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.alrex.parcool.api.stamina.RegisterParCoolStaminaTypeEvent;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolStaminaType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ParCool (applied only when it is loaded): AoS's stamina type joins ParCool's registry when ParCool creates its
 * registration event, in its constructor, before it posts the event and freezes the registry. Forge 47 constructs
 * mods in parallel, so a listener added from AoS's constructor could come too late.
 */
@Mixin(value = RegisterParCoolStaminaTypeEvent.class, remap = false)
public abstract class ParcoolStaminaTypeEventMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void actionsofstamina$registerStaminaType(CallbackInfo ci) {
        ParcoolStaminaType.register((RegisterParCoolStaminaTypeEvent) (Object) this);
    }
}

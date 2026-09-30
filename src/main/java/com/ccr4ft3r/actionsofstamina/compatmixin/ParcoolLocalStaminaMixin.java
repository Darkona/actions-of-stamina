package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolStamina;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * ParCool 3 (applied only when it is loaded): the local player's stamina handler, built from ParCool's client option
 * {@code used_stamina} or the server's {@code forced_stamina}, is AoS's {@link ParcoolStamina} when ParCool would use
 * its own. ParCool 3 has no stamina type registry to add it to.
 */
@Mixin(LocalStamina.class)
public abstract class ParcoolLocalStaminaMixin {

    @ModifyExpressionValue(method = "changeType", at = @At(value = "INVOKE",
            target = "Lcom/alrex/parcool/common/stamina/StaminaType;newHandler(Lnet/minecraft/world/entity/player/Player;)Lcom/alrex/parcool/common/stamina/IParCoolStaminaHandler;"))
    private IParCoolStaminaHandler actionsofstamina$aosStamina(IParCoolStaminaHandler original) {
        return ParcoolStamina.replace(original);
    }
}

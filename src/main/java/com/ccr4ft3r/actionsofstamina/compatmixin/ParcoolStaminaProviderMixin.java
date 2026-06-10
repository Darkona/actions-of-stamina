package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.provider.StaminaProvider;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolStamina;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * ParCool 3 (applied only when it is loaded): the local player's stamina, built from ParCool's client option
 * {@code used_stamina}, is AoS's {@link ParcoolStamina} when ParCool would use its own. ParCool 3 has no stamina type
 * registry to add it to.
 */
@Mixin(value = StaminaProvider.class, remap = false)
public abstract class ParcoolStaminaProviderMixin {

    @ModifyExpressionValue(method = "<init>", at = @At(value = "INVOKE",
            target = "Lcom/alrex/parcool/common/capability/IStamina$Type;newInstance(Lnet/minecraft/world/entity/player/Player;)Lcom/alrex/parcool/common/capability/IStamina;"))
    private IStamina actionsofstamina$aosStamina(IStamina original, Player player) {
        return ParcoolStamina.replace(original, player);
    }
}

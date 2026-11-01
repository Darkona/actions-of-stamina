package com.ccr4ft3r.actionsofstamina.compat.mixin.combatroll;

import com.ccr4ft3r.actionsofstamina.compat.combatroll.CombatRollCompat;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.combatroll.internals.RollManager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RollManager.class, remap = false)
public abstract class CombatRollManagerMixin {

    @ModifyReturnValue(method = "isRollAvailable", at = @At("RETURN"))
    private boolean actionsofstamina$needsStamina(boolean original, @Local(argsOnly = true) Player player) {
        return original && CombatRollCompat.canRoll(player);
    }
}

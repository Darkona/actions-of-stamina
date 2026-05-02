package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollCompat;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.combat_roll.internals.RollManager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Combat Roll, client side (applied only when it is loaded): a roll isn't available while the local player's
 * stamina can't pay for it. The server charges the roll through Combat Roll's own start event.
 */
@Mixin(RollManager.class)
public abstract class CombatRollManagerMixin {

    @ModifyReturnValue(method = "isRollAvailable", at = @At("RETURN"))
    private boolean actionsofstamina$needsStamina(boolean original, @Local(argsOnly = true) Player player) {
        return original && CombatRollCompat.canRoll(player);
    }
}

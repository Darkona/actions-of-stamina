package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ElytraItem.class)
public abstract class ElytraItemMixin {

    /** NeoForge's {@code IItemExtension#canElytraFly}, overridden by ElytraItem: no flight without stamina. */
    @ModifyReturnValue(method = "canElytraFly", at = @At("RETURN"))
    private boolean actionsofstamina$stopElytraFly(boolean original, @Local(argsOnly = true) LivingEntity entity) {
        if (!original || !(entity instanceof Player player) || PlayerActions.isNotExhaustable(player)) return original;
        Action elytra = PlayerActions.get(player).getAction(Action.ELYTRA);
        return elytra == null || elytra.canPerform(player);
    }
}

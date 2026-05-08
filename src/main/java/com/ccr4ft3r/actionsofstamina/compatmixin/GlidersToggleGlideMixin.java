package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.venturecraft.gliders.network.MessageToggleGlide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Gliders, server side (applied only when it is loaded): the glider stays folded when the player asks to open it
 * without the stamina to begin. Gliders' toggle handler then goes on as for a closed glider (no opening sound, camera
 * back to first person). Closing is never refused.
 */
@Mixin(MessageToggleGlide.class)
public abstract class GlidersToggleGlideMixin {

    @WrapOperation(method = "handle", at = @At(value = "INVOKE",
            target = "Lnet/venturecraft/gliders/common/item/GliderItem;setGlide(Lnet/minecraft/world/item/ItemStack;Z)V"))
    private static void actionsofstamina$deployNeedsStamina(ItemStack glider, boolean open, Operation<Void> original,
                                                           @Local ServerPlayer sender) {
        if (open && !GlidersCompat.canDeploy(sender)) return;
        original.call(glider, open);
    }
}

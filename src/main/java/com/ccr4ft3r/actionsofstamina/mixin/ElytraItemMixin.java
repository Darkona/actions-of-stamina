package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.data.ServerData;
import com.ccr4ft3r.actionsofstamina.data.ServerPlayerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;
import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.*;

@Mixin(ElytraItem.class)
public class ElytraItemMixin {

    @Inject(method = "canElytraFly", at = @At("HEAD"), cancellable = true, remap = false)
    private void regenerateFeathersHeadInjected(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof ServerPlayer))
            return;
        ServerPlayerData playerData = ServerData.getPlayerData((Player) entity);
        if (!playerData.isFlying())
            return;
        if (hasEnoughFeathers(getProfile().costsForJumping, getProfile().minForJumping))
            return;
        cir.setReturnValue(false);
    }
}
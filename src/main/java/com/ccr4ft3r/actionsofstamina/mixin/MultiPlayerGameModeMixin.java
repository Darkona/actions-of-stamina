package com.ccr4ft3r.actionsofstamina.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.ccr4ft3r.actionsofstamina.config.AoSAction.*;
import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

    @Inject(method = "useItem", at = @At(value = "HEAD"), cancellable = true)
    public void stopBlocking(Player p_105236_, Level level, InteractionHand p_105238_, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemstack = p_105236_.getItemInHand(p_105238_);
        if (!(itemstack.getItem() instanceof ShieldItem))
            return;
        if (shouldStop(HOLDING_THE_SHIELD))
            cir.setReturnValue(InteractionResult.PASS);
    }
}
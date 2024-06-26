package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraAction;
import com.ccr4ft3r.actionsofstamina.capability.AosCapabilityProvider;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ElytraItem.class)
public class ElytraItemMixin {

    @Inject(method = "canElytraFly", at = @At("HEAD"), cancellable = true, remap = false)
    private void stopElytraFly(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player player)
            player.getCapability(AosCapabilityProvider.PLAYER_ACTIONS)
                  .ifPresent(actions -> {
                      actions.getAction(ElytraAction.actionName).ifPresent(action -> {
                          if (!PlayerActions.isNotExhaustable(player) && !action.canPerform(player)) {
                              cir.setReturnValue(false);
                          }
                      });
                  });

    }
}
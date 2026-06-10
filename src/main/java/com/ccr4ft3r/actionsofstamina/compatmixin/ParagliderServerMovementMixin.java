package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.capabilities.ServerPlayerMovement;

/**
 * Paragliders 1.6, server side (applied only when it is loaded): every change of a player's Stamina Vessels
 * (commands, bargains, vessel items, and loading or copying the player's data) goes through this setter. AoS resizes
 * the bar then, once the player is connected; loading and respawning are covered by the join handler.
 */
@Mixin(value = ServerPlayerMovement.class, remap = false)
public abstract class ParagliderServerMovementMixin {

    @Inject(method = "setStaminaVessels", at = @At("TAIL"))
    private void actionsofstamina$vesselsChanged(int staminaVessels, CallbackInfo ci) {
        if (((ServerPlayerMovement) (Object) this).player instanceof ServerPlayer player && player.connection != null) {
            ParagliderCompat.onVesselsChanged(player);
        }
    }
}

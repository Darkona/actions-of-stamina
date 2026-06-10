package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.entity.player.Player;
import tictim.paraglider.capabilities.ClientPlayerMovement;
import tictim.paraglider.capabilities.PlayerMovement;
import tictim.paraglider.capabilities.RemotePlayerMovement;

/**
 * Paragliders 1.6, both sides (applied only when it is loaded): it has no stamina plugin API, so while the
 * integration is enabled its player movement reads its stamina from the AoS backend, and neither regenerates nor
 * drains its own. Other players seen from a client keep Paragliders' own values, as their stamina isn't known there.
 */
@Mixin(value = PlayerMovement.class, remap = false)
public abstract class ParagliderPlayerMovementMixin {

    @Shadow
    @Final
    public Player player;

    private boolean actionsofstamina$readsBackend() {
        Object self = this;
        return ParagliderCompat.readsBackend(self instanceof RemotePlayerMovement && !(self instanceof ClientPlayerMovement));
    }

    @ModifyReturnValue(method = "getStamina", at = @At("RETURN"))
    private int actionsofstamina$stamina(int original) {
        return actionsofstamina$readsBackend() ? ParagliderCompat.stamina(player) : original;
    }

    @ModifyReturnValue(method = "getMaxStamina", at = @At("RETURN"))
    private int actionsofstamina$maxStamina(int original) {
        return actionsofstamina$readsBackend() ? ParagliderCompat.maxStamina(player) : original;
    }

    @ModifyReturnValue(method = "isDepleted", at = @At("RETURN"))
    private boolean actionsofstamina$depleted(boolean original) {
        return actionsofstamina$readsBackend() ? ParagliderCompat.depleted(player) : original;
    }

    /** Reads the depleted field directly, not {@code isDepleted()}. */
    @ModifyReturnValue(method = "canUseParaglider", at = @At("RETURN"))
    private boolean actionsofstamina$canParaglide(boolean original) {
        return actionsofstamina$readsBackend() ? player.isCreative() || !ParagliderCompat.depleted(player) : original;
    }

    /** Paragliders' own regeneration and drain run only while the integration is off. */
    @Inject(method = "updateStamina", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$noOwnStamina(CallbackInfo ci) {
        if (actionsofstamina$readsBackend()) ci.cancel();
    }

    @Inject(method = "giveStamina", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$noGive(int amount, boolean simulate, CallbackInfoReturnable<Integer> cir) {
        if (actionsofstamina$readsBackend()) cir.setReturnValue(0);
    }

    @Inject(method = "takeStamina", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$noTake(int amount, boolean simulate, boolean ignoreDepletion, CallbackInfoReturnable<Integer> cir) {
        if (actionsofstamina$readsBackend()) cir.setReturnValue(0);
    }
}

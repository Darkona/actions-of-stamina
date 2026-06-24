package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraAction;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.data.AosPlayerData;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalStamina;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin implements AosPlayerData {

    /** Transient: rebuilt from the config whenever the player joins a level. */
    @Unique
    private final PlayerActions actionsofstamina$actions = new PlayerActions();
    @Unique
    private final InternalStamina actionsofstamina$internalStamina = new InternalStamina();

    @Override
    public PlayerActions actionsofstamina$actions() {
        return actionsofstamina$actions;
    }

    @Override
    public InternalStamina actionsofstamina$internalStamina() {
        return actionsofstamina$internalStamina;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void actionsofstamina$saveStamina(CompoundTag tag, CallbackInfo ci) {
        tag.put(InternalStamina.SAVE_KEY, actionsofstamina$internalStamina.save());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void actionsofstamina$loadStamina(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains(InternalStamina.SAVE_KEY)) actionsofstamina$internalStamina.load(tag.getCompound(InternalStamina.SAVE_KEY));
    }

    /** Cancels a jump the player can't afford. */
    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$stopJumping(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        // A Wall-Jump TXF double jump already paid its own cost: it isn't a normal jump.
        if (WallJumpCompat.consumeDoubleJump(self)) return;
        if (!PlayerActions.perform(self, Action.JUMP)) ci.cancel();
    }

    /**
     * No gliding off with stamina wings the player can't afford. Both sides: the client then never asks, and the
     * server refuses a client that asks anyway. Only runs when a jump in the air tries to open the wings.
     */
    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$stopFallFlyingStart(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player) (Object) this;
        if (ElytraAction.wearsStaminaWings(self) && !PlayerActions.canPerform(self, Action.ELYTRA)) {
            cir.setReturnValue(false);
        }
    }
}

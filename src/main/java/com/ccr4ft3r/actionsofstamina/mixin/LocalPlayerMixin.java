package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.jump.JumpAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintAction;
import com.ccr4ft3r.actionsofstamina.capability.AosCapabilityProvider;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LocalPlayer.class)
public class LocalPlayerMixin extends Player {


    public LocalPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    /*@Inject(method = "aiStep", at = @At(value = "INVOKE", target="setJumping()"))
    public void stopJumping(CallbackInfo ci) {

        getCapability(AosCapabilityProvider.PLAYER_ACTIONS, null)
                .ifPresent(a -> a.getAction(JumpAction.actionName)
                                 .ifPresent(w -> {
                                     jumping = jumping && w.perform(this);
                                 }));

    }*/

    @Inject(method = "canStartSprinting", at = @At(value = "RETURN"), cancellable = true)
    public void canStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        getCapability(AosCapabilityProvider.PLAYER_ACTIONS, null)
                .ifPresent(a -> a.getAction(SprintAction.actionName)
                                 .ifPresent(w -> cir.setReturnValue(w.canPerform(this) && cir.getReturnValueZ())));
    }
}
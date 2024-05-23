package com.ccr4ft3r.actionsofstamina.mixin;

import net.bettercombat.mixin.LivingEntityMixin;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ccr4ft3r.actionsofstamina.config.AoSAction.*;
import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends LivingEntity {

    protected LocalPlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Shadow
    public Input input;


    @Inject(method = "aiStep",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LivingEntity;isSprinting()Z",
            ordinal = 2))

    public void stopSprintingAndJumping(CallbackInfo ci) {
        if (shouldStop(SPRINTING))
            setSprinting(false);
        if (shouldStop(JUMPING))
            input.jumping = false;
    }
}
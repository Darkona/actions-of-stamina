package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    public void startAttackHeadInjected(CallbackInfoReturnable<Boolean> cir) {
        if (getProfile().forAttacking.get() && !PlayerUtil.hasEnoughFeathers(getProfile().costsForAttacking, getProfile().minForAttacking))
            cir.setReturnValue(false);
    }
}
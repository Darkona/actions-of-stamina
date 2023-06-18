package com.ccr4ft3r.actionsofstamina.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.ccr4ft3r.actionsofstamina.config.AoSAction.*;
import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;

@Mixin(value = Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    protected abstract boolean startAttack();

    @Shadow
    protected abstract void continueAttack(boolean p_91387_);

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;startAttack()Z"))
    public boolean stopAttacking(Minecraft instance) {
        if (shouldStop(ATTACKING))
            return false;
        return startAttack();
    }

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;continueAttack(Z)V"))
    public void stopAttacking(Minecraft instance, boolean direction) {
        if (shouldStop(ATTACKING))
            return;
        continueAttack(direction);
    }
}
package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.content.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.content.actions.VanillaActions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @ModifyReturnValue(method = "canStartSprinting", at = @At("RETURN"))
    private boolean actionsofstamina$sprintNeedsStamina(boolean original) {
        if (!original) return false;
        LocalPlayer self = (LocalPlayer) (Object) this;
        ActionType action = (self.isInWater() || self.isInLava()) && !self.onGround() ? VanillaActions.SWIM : VanillaActions.SPRINT;
        return PlayerActions.canPerform(self, action);
    }
}

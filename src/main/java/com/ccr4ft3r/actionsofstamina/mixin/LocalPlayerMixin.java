package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    /**
     * No sprinting off without the stamina to begin, as vanilla refuses it without food. The player's own client
     * starts the sprint again on every tick the key is held, so ending it from the action tick alone leaves the
     * player sprinting for free (with the sprint flag flickering on the server): the start itself is refused. Both of
     * {@code aiStep}'s sprint starts (the double tap and the held key) require {@code hasEnoughImpulseToStartSprinting},
     * and nothing else calls it. In water off the ground, sprinting is swimming, and the swim action decides.
     */
    @ModifyReturnValue(method = "hasEnoughImpulseToStartSprinting", at = @At("RETURN"))
    private boolean actionsofstamina$sprintNeedsStamina(boolean original) {
        if (!original) return false;
        LocalPlayer self = (LocalPlayer) (Object) this;
        ActionType action = (self.isInWater() || self.isInLava()) && !self.isOnGround() ? VanillaActions.SWIM : VanillaActions.SPRINT;
        return PlayerActions.canPerform(self, action);
    }
}

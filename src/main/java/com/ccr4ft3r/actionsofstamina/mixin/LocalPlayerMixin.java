package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.Action;
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
     * player sprinting for free (with the sprint flag flickering on the server): the start itself is refused. In
     * water off the ground, sprinting is swimming, and the swim action decides.
     */
    @ModifyReturnValue(method = "canStartSprinting", at = @At("RETURN"))
    private boolean actionsofstamina$sprintNeedsStamina(boolean original) {
        if (!original) return false;
        LocalPlayer self = (LocalPlayer) (Object) this;
        int action = (self.isInWater() || self.isInLava()) && !self.onGround() ? Action.SWIM : Action.SPRINT;
        return PlayerActions.canPerform(self, action);
    }
}

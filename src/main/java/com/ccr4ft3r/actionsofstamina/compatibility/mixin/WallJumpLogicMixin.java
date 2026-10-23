package com.ccr4ft3r.actionsofstamina.compatibility.mixin;

import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.jahirtrap.walljump.logic.WallJumpLogic;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WallJumpLogic.class)
public abstract class WallJumpLogicMixin {

    @Inject(method = "doWallJump", at = @At("HEAD"))
    private static void actionsofstamina$letGoWhenShort(LocalPlayer player, CallbackInfo ci) {
        if (WallJumpLogic.ticksWallClinged > 0 && !WallJumpCompat.canCling(player)) WallJumpLogic.ticksWallClinged = 0;
    }

    @ModifyReturnValue(method = "canWallCling", at = @At("RETURN"))
    private static boolean actionsofstamina$clingNeedsStamina(boolean original, @Local(argsOnly = true) LocalPlayer player) {
        return original && WallJumpCompat.canCling(player);
    }

    @Inject(method = "doWallJump", cancellable = true,
            at = @At(value = "NEW", target = "com/jahirtrap/walljump/network/message/MessageWallJump"))
    private static void actionsofstamina$wallJumpNeedsStamina(LocalPlayer player, CallbackInfo ci) {
        if (!WallJumpCompat.tryWallJump(player)) ci.cancel();
    }
}

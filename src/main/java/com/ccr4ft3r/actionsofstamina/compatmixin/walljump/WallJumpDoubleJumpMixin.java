package com.ccr4ft3r.actionsofstamina.compatmixin.walljump;

import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.jahirtrap.walljump.logic.DoubleJumpLogic;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DoubleJumpLogic.class)
public abstract class WallJumpDoubleJumpMixin {

    @Shadow
    private static boolean jumpKey;

    @Inject(method = "doDoubleJump", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;jumpFromGround()V"))
    private static void actionsofstamina$doubleJumpNeedsStamina(LocalPlayer player, CallbackInfo ci) {
        if (WallJumpCompat.tryDoubleJump(player)) return;
        jumpKey = true;
        ci.cancel();
    }

    @Inject(method = "doDoubleJump", at = @At("RETURN"))
    private static void actionsofstamina$doubleJumpDone(LocalPlayer player, CallbackInfo ci) {
        WallJumpCompat.endDoubleJump();
    }
}

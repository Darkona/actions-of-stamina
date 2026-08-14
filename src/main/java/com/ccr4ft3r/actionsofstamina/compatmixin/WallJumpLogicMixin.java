package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.jahirtrap.walljump.logic.WallJumpLogic;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Wall-Jump TXF, client side (applied only when it is loaded): a wall can't be grabbed, or held any longer, without
 * the stamina for it, and a wall jump the stamina can't pay for doesn't happen. The server charges through
 * {@code ActionPerformedPacket} and the wall cling action.
 */
@Mixin(value = WallJumpLogic.class, remap = false)
public abstract class WallJumpLogicMixin {

    /** Lets go of the wall when the grip can't be paid any more; Wall-Jump TXF then treats the player as falling. */
    @Inject(method = "doWallJump", at = @At("HEAD"))
    private static void actionsofstamina$letGoWhenShort(LocalPlayer player, CallbackInfo ci) {
        if (WallJumpLogic.ticksWallClinged > 0 && !WallJumpCompat.canCling(player)) WallJumpLogic.ticksWallClinged = 0;
    }

    @ModifyReturnValue(method = "canWallCling", at = @At("RETURN"))
    private static boolean actionsofstamina$clingNeedsStamina(boolean original, @Local(argsOnly = true) LocalPlayer player) {
        return original && WallJumpCompat.canCling(player);
    }

    /** Right before Wall-Jump TXF tells its server about the jump and pushes the player off the wall. */
    @Inject(method = "doWallJump", cancellable = true,
            at = @At(value = "NEW", target = "com/jahirtrap/walljump/network/message/MessageWallJump"))
    private static void actionsofstamina$wallJumpNeedsStamina(LocalPlayer player, CallbackInfo ci) {
        if (!WallJumpCompat.tryWallJump(player)) ci.cancel();
    }
}

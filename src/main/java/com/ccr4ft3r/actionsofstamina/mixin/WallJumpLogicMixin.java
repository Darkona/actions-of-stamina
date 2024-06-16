package com.ccr4ft3r.actionsofstamina.mixin;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


// Commented out as WallJump is not available for 1.20.1
//@Mixin(targets = "forge.genandnic.walljump.logic.WallJumpLogic")
public class WallJumpLogicMixin {

    //@Inject(method = "doWallJump", at = @At("HEAD"), cancellable = true, remap = false)
    private static void stopWallJump(CallbackInfo ci) {
        /*if (shouldStop(AoSAction.WALL_JUMPING))
            ci.cancel();*/
    }

    //@Inject(method = "doWallClingJump", at = @At("HEAD"), remap = false)
    private static void exhaustForWallJump(CallbackInfo ci) {
        //PacketHandler.sendToServer(new ServerboundPacket(PLAYER_WALL_JUMP));
    }
}
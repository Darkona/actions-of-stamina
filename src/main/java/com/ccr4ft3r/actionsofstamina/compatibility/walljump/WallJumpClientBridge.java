package com.ccr4ft3r.actionsofstamina.compatibility.walljump;

import com.jahirtrap.walljump.logic.WallJumpLogic;

final class WallJumpClientBridge {

    private WallJumpClientBridge() {
    }

    static boolean isClinging() {
        return WallJumpLogic.ticksWallClinged > 0;
    }
}

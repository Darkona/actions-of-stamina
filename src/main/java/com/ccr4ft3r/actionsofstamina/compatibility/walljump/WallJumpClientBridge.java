package com.ccr4ft3r.actionsofstamina.compatibility.walljump;

import com.jahirtrap.walljump.logic.WallJumpLogic;

/** Direct Wall-Jump TXF client calls; only reached through {@link WallJumpCompat} on the client. */
final class WallJumpClientBridge {

    private WallJumpClientBridge() {
    }

    /** Wall-Jump TXF keeps the local player's cling in static state: positive while holding on or sliding down. */
    static boolean isClinging() {
        return WallJumpLogic.ticksWallClinged > 0;
    }
}

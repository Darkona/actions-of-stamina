package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import dev.darkona.jdlib.api.debug.DebugLines;
import dev.darkona.jdlib.api.hud.JDDebugOverlay;
import net.minecraft.client.player.LocalPlayer;

public final class AoSHudDebugOverlay {

    private static final String TITLE = "Actions of Stamina";
    private static final int TOP = 11;

    public static final JDDebugOverlay.Spec SPEC = new JDDebugOverlay.Spec(JDDebugOverlay.Side.RIGHT, TOP, TITLE,
            AoSServerConfig.ENABLE_DEBUGGING::getAsBoolean, AoSHudDebugOverlay::describe);

    private AoSHudDebugOverlay() {
    }

    private static void describe(LocalPlayer player, DebugLines lines) {
        for (Action action : PlayerActions.get(player).getActions()) {
            if (action != null) lines.add(action.debugString());
        }
    }
}

package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;

/** Client only. Debug HUD listing each enabled action's state; drawn only when debugging is enabled. */
public final class AoSHudDebugOverlay {

    private static final String TITLE = "Actions of Stamina";

    public static final LayeredDraw.Layer LAYER = (guiGraphics, deltaTracker) -> render(guiGraphics);

    private AoSHudDebugOverlay() {
    }

    private static void render(GuiGraphics guiGraphics) {
        if (!AoSServerConfig.ENABLE_DEBUGGING.get()) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui) return;

        PlayerActions playerActions = PlayerActions.get(player);
        Font font = mc.font;
        int screenWidth = guiGraphics.guiWidth();
        int titleX = screenWidth - 5 - font.width(TITLE);
        int y = 11;
        guiGraphics.drawString(font, TITLE, titleX, y, 0xFFFFFF);
        for (Action action : playerActions.getActions()) {
            if (action == null) continue;
            String actionInfo = action.debugString();
            if (actionInfo == null) continue;
            guiGraphics.drawString(font, actionInfo, screenWidth - 5 - font.width(actionInfo), y += 10, 0xFFFFFF);
        }
    }
}

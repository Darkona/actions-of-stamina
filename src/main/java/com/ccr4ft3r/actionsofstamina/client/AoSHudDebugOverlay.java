package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.client.gui.GuiLayer;

/** Client only. Debug HUD listing each enabled action's state; drawn only when debugging is enabled. */
public final class AoSHudDebugOverlay {

    private static final String TITLE = "Actions of Stamina";

    public static final GuiLayer LAYER = (guiGraphics, deltaTracker) -> render(guiGraphics);

    private AoSHudDebugOverlay() {
    }

    private static void render(GuiGraphicsExtractor guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        // The player first: the server config behind the switch is only loaded while there is one.
        if (player == null || mc.gui.hud.isHidden() || !AoSServerConfig.ENABLE_DEBUGGING.getAsBoolean()) return;

        PlayerActions playerActions = PlayerActions.get(player);
        Font font = mc.font;
        int screenWidth = guiGraphics.guiWidth();
        int titleX = screenWidth - 5 - font.width(TITLE);
        int y = 11;
        guiGraphics.text(font, TITLE, titleX, y, 0xFFFFFFFF);
        for (Action action : playerActions.getActions()) {
            if (action == null) continue;
            String actionInfo = action.debugString();
            if (actionInfo == null) continue;
            guiGraphics.text(font, actionInfo, screenWidth - 5 - font.width(actionInfo), y += 10, 0xFFFFFFFF);
        }
    }
}

package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.gui.IIngameOverlay;

/** Client only. Debug HUD listing each enabled action's state; drawn only when debugging is enabled. */
public final class AoSHudDebugOverlay {

    private static final String TITLE = "Actions of Stamina";

    public static final String NAME = "Actions of Stamina Debug";

    public static final IIngameOverlay OVERLAY = (gui, poseStack, partialTick, width, height) -> render(poseStack, width);

    private AoSHudDebugOverlay() {
    }

    private static void render(PoseStack poseStack, int screenWidth) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        // The player first: the server config behind the switch is only loaded while there is one.
        if (player == null || mc.options.hideGui || !AoSServerConfig.ENABLE_DEBUGGING.get()) return;

        PlayerActions playerActions = PlayerActions.get(player);
        Font font = mc.font;
        int titleX = screenWidth - 5 - font.width(TITLE);
        int y = 11;
        font.drawShadow(poseStack, TITLE, titleX, y, 0xFFFFFF);
        for (Action action : playerActions.getActions()) {
            if (action == null) continue;
            String actionInfo = action.debugString();
            if (actionInfo == null) continue;
            font.drawShadow(poseStack, actionInfo, screenWidth - 5 - font.width(actionInfo), y += 10, 0xFFFFFF);
        }
    }
}

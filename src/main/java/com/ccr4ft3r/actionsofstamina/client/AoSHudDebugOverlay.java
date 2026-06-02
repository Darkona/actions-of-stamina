package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/** Client only. Debug HUD listing each enabled action's state; drawn only when debugging is enabled. */
public final class AoSHudDebugOverlay {

    private static final String TITLE = "Actions of Stamina";

    public static final IGuiOverlay OVERLAY = (gui, poseStack, partialTick, width, height) -> render(poseStack, width);

    private AoSHudDebugOverlay() {
    }

    private static void render(PoseStack poseStack, int screenWidth) {
        if (!AoSServerConfig.ENABLE_DEBUGGING.get()) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui) return;

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

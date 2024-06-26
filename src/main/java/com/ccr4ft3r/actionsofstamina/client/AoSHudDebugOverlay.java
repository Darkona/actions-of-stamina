package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(Dist.CLIENT)
public class AoSHudDebugOverlay {


    public static PlayerActions playerActions;

    public static final IGuiOverlay Actions = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1);
        var debugInfo = "Actions of Stamina";
        byte l = 1;
        if (AoSCommonConfig.ENABLE_DEBUGGING.get()) {
            guiGraphics.drawString(Minecraft.getInstance().font, debugInfo, getXOffset(guiGraphics, screenWidth, debugInfo), l += 10, 0xFFFFFF);
            guiGraphics.drawString(Minecraft.getInstance().font, "Moving: " + playerActions.isMoving(), getXOffset(guiGraphics, screenWidth, debugInfo), l += 10, 0xFFFFFF);
            for (var action : playerActions.getEnabledActions().values()) {
                var actionInfo = action.debugString();
                guiGraphics.drawString(Minecraft.getInstance().font, actionInfo, getXOffset(guiGraphics, screenWidth, actionInfo), l += 10, 0xFFFFFF);
            }
        }

    };

    private static int getXOffset(GuiGraphics guiGraphics, int screenWidth, String text) {
        return screenWidth - 5 - Minecraft.getInstance().font.width(text);
    }
}

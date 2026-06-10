package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.config.AoSClientConfig;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalBackend;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalStamina;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

/**
 * Client only. The internal stamina bar, one row above the food bar (it takes a row of {@code gui.right_height} so
 * other right-side HUD rows stack above it). Drawn only while the internal backend is in use; with Green Feathers,
 * Green Feathers draws its own HUD.
 */
public final class InternalStaminaHud {

    private static final int WIDTH = 81;
    private static final int HEIGHT = 5;
    private static final int ROW = 10;
    private static final int BACKGROUND = 0xA0000000;
    private static final int FILL = 0xFF52C44A;
    private static final int FILL_EXHAUSTED = 0xFFC44A4A;
    private static final int FILL_EDGE = 0xFF8BE884;

    public static final String NAME = "Actions of Stamina";

    public static final IIngameOverlay OVERLAY = (gui, poseStack, partialTick, width, height) -> render(gui, poseStack, width, height);

    private InternalStaminaHud() {
    }

    private static void render(ForgeIngameGui gui, PoseStack poseStack, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui || mc.gameMode == null || !mc.gameMode.canHurtPlayer()) return;
        if (!AoSClientConfig.SHOW_HUD.get() || !InternalBackend.enabled()) return;
        if (StaminaBackends.client().kind() != StaminaBackend.Kind.INTERNAL) return;

        InternalStamina s = InternalBackend.data(player);
        int max = InternalBackend.INSTANCE.maxStamina(player);
        if (max <= 0) return;
        int stamina = InternalBackend.INSTANCE.stamina(player);

        int x = screenWidth / 2 + 91 - WIDTH + AoSClientConfig.HUD_X_OFFSET.get();
        int y = screenHeight - gui.right_height + 2 + AoSClientConfig.HUD_Y_OFFSET.get();
        gui.right_height += ROW;

        int filled = (int) ((long) Math.min(stamina, max) * (WIDTH - 2) / max);
        GuiComponent.fill(poseStack, x, y, x + WIDTH, y + HEIGHT, BACKGROUND);
        if (filled > 0) {
            // Fills from the right, like the food bar.
            int right = x + WIDTH - 1;
            GuiComponent.fill(poseStack, right - filled, y + 1, right, y + HEIGHT - 1, s.exhausted() ? FILL_EXHAUSTED : FILL);
            if (!s.exhausted()) GuiComponent.fill(poseStack, right - filled, y + 1, right - filled + 1, y + HEIGHT - 1, FILL_EDGE);
        }
    }
}

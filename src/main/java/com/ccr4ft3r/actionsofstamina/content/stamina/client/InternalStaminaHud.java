package com.ccr4ft3r.actionsofstamina.content.stamina.client;

import com.ccr4ft3r.actionsofstamina.config.ClientConfig;
import com.ccr4ft3r.actionsofstamina.content.stamina.InternalBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.InternalStamina;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public final class InternalStaminaHud {

    static final int WIDTH = 81;
    static final int HEIGHT = 5;
    static final int ROW = 10;
    static final int TOP_GAP = 2;
    private static final int BACKGROUND = 0xA0000000;
    private static final int FILL = 0xFF52C44A;
    private static final int FILL_EXHAUSTED = 0xFFC44A4A;
    private static final int FILL_EDGE = 0xFF8BE884;

    public static final IGuiOverlay OVERLAY = (gui, graphics, partialTick, width, height) -> render(gui, graphics);

    private InternalStaminaHud() {
    }

    public static void receive(int stamina, int maxStamina, boolean exhausted, int regenDelay) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) InternalBackend.data(player).receive(stamina, maxStamina, exhausted, regenDelay);
    }

    static int x(int guiWidth, int xOffset) {
        return guiWidth / 2 + 91 - WIDTH + xOffset;
    }

    static int y(int guiHeight, int rightHeight, int yOffset) {
        return guiHeight - rightHeight + TOP_GAP + yOffset;
    }

    static int fillWidth(int stamina, int max) {
        return (int) ((long) Math.min(stamina, max) * (WIDTH - 2) / max);
    }

    private static void render(ForgeGui gui, GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui || mc.gameMode == null || !mc.gameMode.canHurtPlayer()) return;
        if (!ClientConfig.SHOW_HUD.get() || !InternalBackend.enabled()) return;
        if (StaminaBackends.client().kind() != StaminaBackend.Kind.INTERNAL) return;

        int max = InternalBackend.INSTANCE.maxStamina(player);
        if (max <= 0) return;
        InternalStamina s = InternalBackend.data(player);
        int filled = fillWidth(InternalBackend.INSTANCE.stamina(player), max);

        int x = x(graphics.guiWidth(), ClientConfig.HUD_X_OFFSET.get());
        int y = y(graphics.guiHeight(), gui.rightHeight, ClientConfig.HUD_Y_OFFSET.get());
        gui.rightHeight += ROW;

        graphics.fill(x, y, x + WIDTH, y + HEIGHT, BACKGROUND);
        if (filled > 0) {
            int right = x + WIDTH - 1;
            graphics.fill(right - filled, y + 1, right, y + HEIGHT - 1, s.exhausted() ? FILL_EXHAUSTED : FILL);
            if (!s.exhausted()) graphics.fill(right - filled, y + 1, right - filled + 1, y + HEIGHT - 1, FILL_EDGE);
        }
    }
}

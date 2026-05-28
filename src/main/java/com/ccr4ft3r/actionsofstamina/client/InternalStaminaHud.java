package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.config.AoSClientConfig;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalBackend;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalStamina;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/**
 * Client only. The internal stamina bar, one row above the food bar (it takes a row of {@code gui.rightHeight} so
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

    public static final IGuiOverlay OVERLAY = (gui, graphics, partialTick, width, height) -> render(gui, graphics);

    private InternalStaminaHud() {
    }

    private static void render(ForgeGui gui, GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui || mc.gameMode == null || !mc.gameMode.canHurtPlayer()) return;
        if (!AoSClientConfig.SHOW_HUD.get() || !InternalBackend.enabled()) return;
        if (StaminaBackends.client().kind() != StaminaBackend.Kind.INTERNAL) return;

        InternalStamina s = InternalBackend.data(player);
        int max = InternalBackend.INSTANCE.maxStamina(player);
        if (max <= 0) return;
        int stamina = InternalBackend.INSTANCE.stamina(player);

        int x = graphics.guiWidth() / 2 + 91 - WIDTH + AoSClientConfig.HUD_X_OFFSET.get();
        int y = graphics.guiHeight() - gui.rightHeight + 2 + AoSClientConfig.HUD_Y_OFFSET.get();
        gui.rightHeight += ROW;

        int filled = (int) ((long) Math.min(stamina, max) * (WIDTH - 2) / max);
        graphics.fill(x, y, x + WIDTH, y + HEIGHT, BACKGROUND);
        if (filled > 0) {
            // Fills from the right, like the food bar.
            int right = x + WIDTH - 1;
            graphics.fill(right - filled, y + 1, right, y + HEIGHT - 1, s.exhausted() ? FILL_EXHAUSTED : FILL);
            if (!s.exhausted()) graphics.fill(right - filled, y + 1, right - filled + 1, y + HEIGHT - 1, FILL_EDGE);
        }
    }
}

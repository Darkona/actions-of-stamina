package com.ccr4ft3r.actionsofstamina.content.stamina.client;

import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.InternalBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.content.stamina.InternalStamina;
import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.ClientConfig;
import dev.darkona.jdlib.api.hud.HudFrame;
import dev.darkona.jdlib.api.hud.HudGate;
import dev.darkona.jdlib.api.hud.HudSide;
import dev.darkona.jdlib.api.hud.JDHudBars;
import dev.darkona.jdlib.api.hud.VanillaLayer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;

public final class InternalStaminaHud {

    static final int WIDTH = 81;
    static final int HEIGHT = 5;
    static final int ROW = 10;
    static final int TOP_GAP = 2;
    private static final int BACKGROUND = 0xA0000000;
    private static final int FILL = 0xFF52C44A;
    private static final int FILL_EXHAUSTED = 0xFFC44A4A;
    private static final int FILL_EDGE = 0xFF8BE884;

    private static final HudGate GATE = HudGate.standard().and(mc -> ClientConfig.SHOW_HUD.getAsBoolean()
            && InternalBackend.enabled() && StaminaBackends.client().kind() == StaminaBackend.Kind.INTERNAL);

    private InternalStaminaHud() {
    }

    public static void register() {
        JDHudBars.register(spec());
    }

    static JDHudBars.Spec spec() {
        return new JDHudBars.Spec(ActionsOfStamina.id("internal_stamina"), VanillaLayer.FOOD_LEVEL, JDHudBars.Order.ABOVE,
                HudSide.RIGHT, 0, GATE, () -> true, ClientConfig.HUD_X_OFFSET::getAsInt, ClientConfig.HUD_Y_OFFSET::getAsInt,
                0, InternalStaminaHud::paint);
    }

    static int fillWidth(int stamina, int max) {
        return (int) ((long) Math.min(stamina, max) * (WIDTH - 2) / max);
    }

    private static int paint(HudFrame frame) {
        LocalPlayer player = frame.player();
        int max = InternalBackend.INSTANCE.maxStamina(player);
        if (max <= 0) return 0;
        InternalStamina s = InternalBackend.data(player);
        int filled = fillWidth(InternalBackend.INSTANCE.stamina(player), max);

        GuiGraphics graphics = frame.graphics();
        int x = frame.edgeX() - WIDTH;
        int y = frame.topY() + TOP_GAP;
        graphics.fill(x, y, x + WIDTH, y + HEIGHT, BACKGROUND);
        if (filled > 0) {
            int right = x + WIDTH - 1;
            graphics.fill(right - filled, y + 1, right, y + HEIGHT - 1, s.exhausted() ? FILL_EXHAUSTED : FILL);
            if (!s.exhausted()) graphics.fill(right - filled, y + 1, right - filled + 1, y + HEIGHT - 1, FILL_EDGE);
        }
        return ROW;
    }
}

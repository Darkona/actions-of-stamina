package com.ccr4ft3r.actionsofstamina.stamina.client;

import com.ccr4ft3r.actionsofstamina.stamina.client.InternalStaminaHud;
import dev.darkona.jdlib.api.hud.HudSide;
import dev.darkona.jdlib.api.hud.JDHudBars;
import dev.darkona.jdlib.api.hud.StackMath;
import dev.darkona.jdlib.api.hud.VanillaLayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InternalStaminaHudSpecTest {

    private static final int[][] WINDOWS = {{427, 240}, {854, 480}, {320, 180}, {1000, 563}, {301, 175}};
    private static final int[] OFFSETS = {-30, -5, 0, 4, 17};
    private static final int[] RIGHT_HEIGHTS = {39, 49, 59, 71};

    private static int oldX(int guiWidth, int xOffset) {
        return guiWidth / 2 + 91 - 81 + xOffset;
    }

    private static int oldY(int guiHeight, int rightHeight, int yOffset) {
        return guiHeight - rightHeight + 2 + yOffset;
    }

    @Test
    void theSpecSitsAboveTheFoodRowOnTheRight() {
        JDHudBars.Spec spec = InternalStaminaHud.spec();
        assertEquals(VanillaLayer.FOOD_LEVEL, spec.anchor());
        assertEquals(JDHudBars.Order.ABOVE, spec.order());
        assertEquals(HudSide.RIGHT, spec.side());
        assertEquals(true, spec.stacks().getAsBoolean());
    }

    @Test
    void positionMatchesTheOldLayerForEveryCombination() {
        for (int dx : OFFSETS) {
            for (int dy : OFFSETS) {
                for (int[] w : WINDOWS) {
                    for (int right : RIGHT_HEIGHTS) {
                        int edge = StackMath.edgeX(HudSide.RIGHT, w[0], dx);
                        int top = StackMath.topY(w[1], StackMath.stackHeight(true, right, 0), dy);
                        String at = "off=" + dx + "," + dy + " window=" + w[0] + "x" + w[1] + " right=" + right;
                        assertEquals(oldX(w[0], dx), edge - InternalStaminaHud.WIDTH, at);
                        assertEquals(oldY(w[1], right, dy), top + InternalStaminaHud.TOP_GAP, at);
                    }
                }
            }
        }
    }

    @Test
    void theFillIsProportionalAndNeverExceedsTheInnerWidth() {
        assertEquals(0, InternalStaminaHud.fillWidth(0, 20000));
        assertEquals(79, InternalStaminaHud.fillWidth(20000, 20000));
        assertEquals(79, InternalStaminaHud.fillWidth(30000, 20000));
        assertEquals(39, InternalStaminaHud.fillWidth(10000, 20000));
    }
}

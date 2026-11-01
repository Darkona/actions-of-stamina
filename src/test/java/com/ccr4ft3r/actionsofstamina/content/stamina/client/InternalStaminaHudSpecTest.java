package com.ccr4ft3r.actionsofstamina.content.stamina.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InternalStaminaHudSpecTest {

    @Test
    void theBarSitsOnTheRightOfTheHotbarAboveTheFoodRow() {
        assertEquals(427 / 2 + 91 - 81 - 5, InternalStaminaHud.x(427, -5));
        assertEquals(240 - 49 + 2 + 4, InternalStaminaHud.y(240, 49, 4));
    }

    @Test
    void theFillIsProportionalAndNeverExceedsTheInnerWidth() {
        assertEquals(0, InternalStaminaHud.fillWidth(0, 20000));
        assertEquals(79, InternalStaminaHud.fillWidth(20000, 20000));
        assertEquals(79, InternalStaminaHud.fillWidth(30000, 20000));
        assertEquals(39, InternalStaminaHud.fillWidth(10000, 20000));
    }
}

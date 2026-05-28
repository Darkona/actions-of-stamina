package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaUnits;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalBackend;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.assertValueEqual;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.player;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.tick;

/**
 * The internal stamina, called directly (whichever backend is active). Default config: 20 feathers, 0.5 feathers
 * per second of regeneration, a 30-tick minimum regen delay after spending, 30% to recover from exhaustion.
 */
@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class InternalBackendTests {

    private static final ResourceLocation TEST = ActionsOfStamina.id("test");
    private static final InternalBackend BACKEND = InternalBackend.INSTANCE;
    private static final int FULL = StaminaUnits.ofFeathers(20);

    @GameTest(template = "empty")
    public static void newPlayersStartFull(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        assertValueEqual(helper, BACKEND.stamina(player), FULL, "stamina before the first tick");
        tick(player, 1);
        assertValueEqual(helper, BACKEND.stamina(player), FULL, "stamina");
        assertValueEqual(helper, BACKEND.maxStamina(player), FULL, "max stamina");
        helper.assertFalse(BACKEND.exhausted(player), "exhausted");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void spendsAreAllOrNothing(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        helper.assertFalse(BACKEND.spend(player, TEST, StaminaUnits.ofFeathers(25), 0), "spend beyond the bar");
        assertValueEqual(helper, BACKEND.stamina(player), FULL, "stamina after a refused spend");
        helper.assertTrue(BACKEND.canSpend(player, TEST, StaminaUnits.ofFeathers(5)), "can spend 5");
        assertValueEqual(helper, BACKEND.stamina(player), FULL, "checking spends nothing");
        helper.assertTrue(BACKEND.spend(player, TEST, StaminaUnits.ofFeathers(5), 0), "spend 5");
        assertValueEqual(helper, BACKEND.stamina(player), StaminaUnits.ofFeathers(15), "stamina after spending 5");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void regenerationWaitsForTheDelay(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        BACKEND.spend(player, TEST, StaminaUnits.ofFeathers(1), 60);
        tick(player, 60);
        assertValueEqual(helper, BACKEND.stamina(player), StaminaUnits.ofFeathers(19), "stamina during the delay");
        tick(player, 20);
        // 0.5 feathers per second: 19.5 feathers after one second of regeneration (the tick ending the delay doesn't regenerate).
        int stamina = BACKEND.stamina(player);
        helper.assertTrue(stamina >= 19450 && stamina <= 19500, "stamina after one second of regen: " + stamina);
        tick(player, 60);
        assertValueEqual(helper, BACKEND.stamina(player), FULL, "regeneration stops at the max");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void spendingEverythingExhausts(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        helper.assertTrue(BACKEND.spend(player, TEST, FULL, 0), "spend the whole bar");
        helper.assertTrue(BACKEND.exhausted(player), "exhausted with an empty bar");
        helper.assertFalse(BACKEND.canSpend(player, TEST, 0), "nothing can be spent while exhausted");
        helper.assertFalse(BACKEND.spend(player, TEST, 1, 0), "spending while exhausted");

        // 30 ticks of delay, then 25 stamina a tick up to 30% of the bar (6000): 240 ticks.
        tick(player, 260);
        helper.assertTrue(BACKEND.exhausted(player), "still exhausted below the recovery threshold");
        tick(player, 20);
        helper.assertFalse(BACKEND.exhausted(player), "recovered at 30%");
        helper.assertTrue(BACKEND.canSpend(player, TEST, StaminaUnits.ofFeathers(1)), "can act again");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void drainsCarryFractions(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        for (int i = 0; i < 8; i++) {
            helper.assertTrue(BACKEND.drain(player, TEST, 0.25, true), "drain tick " + i);
            tick(player, 1);
        }
        // Eight quarter units add up to exactly 2; the drain blocks regeneration, so nothing refills them.
        assertValueEqual(helper, BACKEND.stamina(player), FULL - 2, "stamina after a fractional drain");
        BACKEND.stopDrain(player, TEST);
        tick(player, 1);
        assertValueEqual(helper, BACKEND.stamina(player), FULL, "regenerates once stopped");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void drainsBlockRegenerationUntilTheyStop(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        BACKEND.spend(player, TEST, StaminaUnits.ofFeathers(5), 0);
        tick(player, 30);
        int start = BACKEND.stamina(player);
        for (int i = 0; i < 20; i++) {
            BACKEND.drain(player, TEST, 0, true);
            tick(player, 1);
        }
        assertValueEqual(helper, BACKEND.stamina(player), start, "no regeneration while a regen-blocking drain runs");

        // Not refreshed any more: it times out after a few ticks and regeneration resumes.
        tick(player, 30);
        helper.assertTrue(BACKEND.stamina(player) > start, "regeneration after the drain timed out");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void drainingPastEmptyExhausts(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        helper.assertTrue(BACKEND.drain(player, TEST, StaminaUnits.ofFeathers(15), true), "first drain tick");
        helper.assertFalse(BACKEND.drain(player, TEST, StaminaUnits.ofFeathers(15), true), "second drain tick can't be paid");
        assertValueEqual(helper, BACKEND.stamina(player), 0, "stamina");
        helper.assertTrue(BACKEND.exhausted(player), "exhausted");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void creativePlayersAreExempt(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        player.setGameMode(GameType.CREATIVE);
        helper.assertTrue(BACKEND.spend(player, TEST, StaminaUnits.ofFeathers(50), 0), "creative spend");
        assertValueEqual(helper, BACKEND.stamina(player), FULL, "creative stamina");
        helper.succeed();
    }
}

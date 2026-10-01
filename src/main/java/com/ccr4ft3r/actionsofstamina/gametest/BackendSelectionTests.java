package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.stamina.BackendMode;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaUnits;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.assertFalse;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.assertTrue;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.assertValueEqual;

/** Which backend runs, and that the Feathers of Fatigue one really spends feathers when Feathers of Fatigue is installed. */
@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class BackendSelectionTests {

    private static final ResourceLocation TEST = ActionsOfStamina.id("test");

    @GameTest(template = "empty")
    public static void configChoiceResolves(GameTestHelper helper) {
        assertValueEqual(helper, StaminaBackends.resolve(BackendMode.AUTO, true), StaminaBackend.Kind.FEATHERS, "auto with Feathers of Fatigue");
        assertValueEqual(helper, StaminaBackends.resolve(BackendMode.AUTO, false), StaminaBackend.Kind.INTERNAL, "auto without Feathers of Fatigue");
        assertValueEqual(helper, StaminaBackends.resolve(BackendMode.INTERNAL, true), StaminaBackend.Kind.INTERNAL, "internal with Feathers of Fatigue");
        assertValueEqual(helper, StaminaBackends.resolve(BackendMode.FEATHERS, false), StaminaBackend.Kind.INTERNAL, "feathers without Feathers of Fatigue");
        assertValueEqual(helper, StaminaBackends.resolve(BackendMode.FEATHERS, true), StaminaBackend.Kind.FEATHERS, "feathers with Feathers of Fatigue");
        helper.succeed();
    }

    /** With the default {@code backend = auto}. */
    @GameTest(template = "empty")
    public static void activeBackendFollowsTheInstall(GameTestHelper helper) {
        StaminaBackend.Kind expected = StaminaBackends.FEATHERS_LOADED ? StaminaBackend.Kind.FEATHERS : StaminaBackend.Kind.INTERNAL;
        assertValueEqual(helper, StaminaBackends.server().kind(), expected, "server backend");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void feathersBackendSpendsFeathers(GameTestHelper helper) {
        if (!StaminaBackends.FEATHERS_LOADED) {
            helper.succeed();
            return;
        }
        StaminaBackend feathers = StaminaBackends.byKind(StaminaBackend.Kind.FEATHERS);
        ServerPlayer player = TestSupport.player(helper);
        int before = feathers.stamina(player);
        assertTrue(helper, feathers.spend(player, TEST, StaminaUnits.ofFeathers(2), 0), "spend 2 feathers");
        assertValueEqual(helper, feathers.stamina(player), before - StaminaUnits.ofFeathers(2), "feathers after spending 2");
        helper.succeed();
    }

    /** With Feathers of Fatigue, AoS takes over player actions: its basic exertion is off, so nothing is charged twice. */
    @GameTest(template = "empty")
    public static void feathersLeavesPlayerActionsToAos(GameTestHelper helper) {
        if (!StaminaBackends.FEATHERS_LOADED) {
            helper.succeed();
            return;
        }
        assertTrue(helper, FeathersTestHooks.playerActionOwners().contains(ActionsOfStamina.MOD_ID), "AoS took over player actions");
        helper.succeed();
    }
}

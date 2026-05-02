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
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Which backend runs, and that the Green Feathers one really spends feathers when Green Feathers is installed. */
@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class BackendSelectionTests {

    private static final ResourceLocation TEST = ActionsOfStamina.id("test");

    @GameTest(template = "empty")
    public static void configChoiceResolves(GameTestHelper helper) {
        helper.assertValueEqual(StaminaBackends.resolve(BackendMode.AUTO, true), StaminaBackend.Kind.FEATHERS, "auto with Green Feathers");
        helper.assertValueEqual(StaminaBackends.resolve(BackendMode.AUTO, false), StaminaBackend.Kind.INTERNAL, "auto without Green Feathers");
        helper.assertValueEqual(StaminaBackends.resolve(BackendMode.INTERNAL, true), StaminaBackend.Kind.INTERNAL, "internal with Green Feathers");
        helper.assertValueEqual(StaminaBackends.resolve(BackendMode.FEATHERS, false), StaminaBackend.Kind.INTERNAL, "feathers without Green Feathers");
        helper.assertValueEqual(StaminaBackends.resolve(BackendMode.FEATHERS, true), StaminaBackend.Kind.FEATHERS, "feathers with Green Feathers");
        helper.succeed();
    }

    /** With the default {@code backend = auto}. */
    @GameTest(template = "empty")
    public static void activeBackendFollowsTheInstall(GameTestHelper helper) {
        StaminaBackend.Kind expected = StaminaBackends.FEATHERS_LOADED ? StaminaBackend.Kind.FEATHERS : StaminaBackend.Kind.INTERNAL;
        helper.assertValueEqual(StaminaBackends.server().kind(), expected, "server backend");
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
        helper.assertTrue(feathers.spend(player, TEST, StaminaUnits.ofFeathers(2), 0), "spend 2 feathers");
        helper.assertValueEqual(feathers.stamina(player), before - StaminaUnits.ofFeathers(2), "feathers after spending 2");
        helper.succeed();
    }
}

package com.ccr4ft3r.actionsofstamina.infrastructure.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackend.BackendMode;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;


@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class BackendSelectionTests {

    private static final ResourceLocation TEST = ActionsOfStamina.id("test");

    @GameTest(template = "empty")
    public static void configChoiceResolves(GameTestHelper helper) {
        TestSupport.assertValueEqual(helper, StaminaBackends.resolve(BackendMode.AUTO, true), StaminaBackend.Kind.FEATHERS, "auto with Feathers of Fatigue");
        TestSupport.assertValueEqual(helper, StaminaBackends.resolve(BackendMode.AUTO, false), StaminaBackend.Kind.INTERNAL, "auto without Feathers of Fatigue");
        TestSupport.assertValueEqual(helper, StaminaBackends.resolve(BackendMode.INTERNAL, true), StaminaBackend.Kind.INTERNAL, "internal with Feathers of Fatigue");
        TestSupport.assertValueEqual(helper, StaminaBackends.resolve(BackendMode.FEATHERS, false), StaminaBackend.Kind.INTERNAL, "feathers without Feathers of Fatigue");
        TestSupport.assertValueEqual(helper, StaminaBackends.resolve(BackendMode.FEATHERS, true), StaminaBackend.Kind.FEATHERS, "feathers with Feathers of Fatigue");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void activeBackendFollowsTheInstall(GameTestHelper helper) {
        StaminaBackend.Kind expected = StaminaBackends.FEATHERS_LOADED ? StaminaBackend.Kind.FEATHERS : StaminaBackend.Kind.INTERNAL;
        TestSupport.assertValueEqual(helper, StaminaBackends.server().kind(), expected, "server backend");
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
        helper.assertTrue(feathers.spend(player, TEST, StaminaBackend.ofFeathers(2), 0), "spend 2 feathers");
        TestSupport.assertValueEqual(helper, feathers.stamina(player), before - StaminaBackend.ofFeathers(2), "feathers after spending 2");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void feathersLeavesPlayerActionsToAos(GameTestHelper helper) {
        if (!StaminaBackends.FEATHERS_LOADED) {
            helper.succeed();
            return;
        }
        helper.assertTrue(FeathersTestHooks.playerActionOwners().contains(ActionsOfStamina.MOD_ID), "AoS took over player actions");
        helper.succeed();
    }
}

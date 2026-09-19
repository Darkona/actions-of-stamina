package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaUnits;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.player;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayer;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.tickEvent;

/**
 * The tests of {@code CompatTests} for the compats in src/disabled (ParCool, Epic Fight, Gliders), as they are for
 * Minecraft 1.21.1. They go back into {@code CompatTests} with their compat. {@code exhaust} and {@code TEST} are that
 * class's.
 */
public class DisabledCompatTests {

    @GameTest(template = "empty")
    public static void parcoolActionsAreChargedAndBlocked(GameTestHelper helper) {
        if (!ParcoolCompat.LOADED) {
            helper.succeed();
            return;
        }
        ServerPlayer player = survivalPlayer(helper);
        StaminaBackend backend = StaminaBackends.server();
        int before = backend.stamina(player);
        helper.assertFalse(ParcoolTestHooks.dodgeStartCancelled(player), "dodge may start with a full bar");
        ParcoolTestHooks.postDodgeStarted(player);
        helper.assertValueEqual(backend.stamina(player), before - StaminaUnits.ofFeathers(0.5), "stamina after a dodge");

        String state = exhaust(backend, player);
        helper.assertTrue(ParcoolTestHooks.dodgeStartCancelled(player), "dodge can't start when exhausted" + state);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void parcoolUsesAoSStaminaType(GameTestHelper helper) {
        if (!ParcoolCompat.LOADED) {
            helper.succeed();
            return;
        }
        helper.assertTrue(ParcoolTestHooks.staminaTypeRegistered(), "AoS's stamina type is registered with ParCool");
        helper.assertValueEqual(ParcoolTestHooks.defaultStaminaType(), ParcoolCompat.STAMINA_TYPE.toString(), "ParCool's default stamina_type");
        // The dev run's ParCool config holds ParCool's own "parcool:parcool" (as every existing config does): replaced.
        helper.assertValueEqual(ParcoolCompat.effectiveStaminaType(ParcoolCompat.PARCOOL_STAMINA_TYPE), ParcoolCompat.STAMINA_TYPE, "parcool:parcool is replaced");
        helper.assertValueEqual(ParcoolTestHooks.staminaType(), ParcoolCompat.STAMINA_TYPE, "stamina type ParCool picks");

        ServerPlayer player = player(helper);
        StaminaBackend backend = StaminaBackends.server();
        backend.spend(player, TEST, StaminaUnits.ofFeathers(3), 0);
        String problem = ParcoolTestHooks.checkStaminaMirrorsBackend(player, backend);
        helper.assertTrue(problem == null, "ParCool stamina: " + problem);
        exhaust(backend, player);
        problem = ParcoolTestHooks.checkStaminaMirrorsBackend(player, backend);
        helper.assertTrue(problem == null, "ParCool stamina when exhausted: " + problem);
        helper.succeed();
    }

    private static ActionCostConfig epicFightCosts(int category) {
        return switch (category) {
            case 0 -> EpicFightConfig.DODGE;
            case 1 -> EpicFightConfig.GUARD;
            case 2 -> EpicFightConfig.INNATE;
            default -> EpicFightConfig.MOVER;
        };
    }

    @GameTest(template = "empty")
    public static void epicFightSkillsSpendAoSStamina(GameTestHelper helper) {
        if (!EpicFightCompat.LOADED) {
            helper.succeed();
            return;
        }
        ServerPlayer player = survivalPlayer(helper);
        helper.assertTrue(EpicFightTestHooks.hasPatch(player), "the player has an Epic Fight server patch");
        StaminaBackend backend = StaminaBackends.server();
        for (int category = 0; category < EpicFightTestHooks.CATEGORIES; category++) {
            String name = EpicFightTestHooks.CATEGORY_NAMES[category];
            String skill = EpicFightTestHooks.skillName(category);
            helper.assertTrue(skill != null, "Epic Fight has a " + name + " skill");
            int before = backend.stamina(player);
            EpicFightTestHooks.Consume result = EpicFightTestHooks.consumeSkill(player, category);
            helper.assertFalse(result.canceled(), name + " (" + skill + ") goes ahead with a full bar");
            helper.assertTrue(result.switchedToNone(), name + " (" + skill + ") spends no Epic Fight stamina");
            helper.assertValueEqual(backend.stamina(player), before - epicFightCosts(category).cost(), "stamina after " + name);
        }

        // Starting to hold a guard, Epic Fight posts the event twice in a row on the server: charged once.
        int beforeHold = backend.stamina(player);
        EpicFightTestHooks.consumeSkill(player, 1);
        EpicFightTestHooks.Consume repeat = EpicFightTestHooks.consumeSkill(player, 1);
        helper.assertTrue(repeat.switchedToNone(), "the repeated guard post spends no Epic Fight stamina");
        helper.assertValueEqual(backend.stamina(player), beforeHold - EpicFightConfig.GUARD.cost(), "stamina after a guard hold start");

        String state = exhaust(backend, player);
        for (int category = 0; category < EpicFightTestHooks.CATEGORIES; category++) {
            String name = EpicFightTestHooks.CATEGORY_NAMES[category];
            EpicFightTestHooks.Consume result = EpicFightTestHooks.consumeSkill(player, category);
            helper.assertTrue(result.canceled(), name + " fails when it can't be paid" + state);
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void epicFightBasicAttacksAreCharged(GameTestHelper helper) {
        if (!EpicFightCompat.LOADED) {
            helper.succeed();
            return;
        }
        ServerPlayer player = survivalPlayer(helper);
        StaminaBackend backend = StaminaBackends.server();
        EpicFightTestHooks.battleMode(player, false);
        helper.assertFalse(EpicFightCompat.inBattleMode(player), "vanilla mode: AoS's vanilla attack cost applies");
        EpicFightTestHooks.battleMode(player, true);
        helper.assertTrue(EpicFightCompat.inBattleMode(player), "battle mode: AoS's vanilla attack cost stands aside");

        int before = backend.stamina(player);
        helper.assertFalse(EpicFightTestHooks.comboAttackCanceled(player), "a swing goes ahead with a full bar");
        helper.assertValueEqual(backend.stamina(player), before - EpicFightConfig.BASIC_ATTACK.cost(), "stamina after a swing");

        String state = exhaust(backend, player);
        helper.assertTrue(EpicFightTestHooks.comboAttackCanceled(player), "a swing that can't be paid doesn't happen" + state);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void glidingDrainsAndFoldsTheGlider(GameTestHelper helper) {
        if (!GlidersCompat.LOADED) {
            helper.succeed();
            return;
        }
        ServerPlayer player = survivalPlayer(helper);
        StaminaBackend backend = StaminaBackends.server();
        PlayerActions actions = PlayerActions.get(player);
        Action glide = actions.getAction(GlidersCompat.GLIDE);
        helper.assertTrue(glide != null, "the glide action exists");
        GlidersTestHooks.equipGlider(player);
        GlidersTestHooks.pressDeployKey(player);
        helper.assertTrue(GlidersTestHooks.gliderOpen(player), "a glider opens with stamina");
        helper.assertTrue(GlidersCompat.isGliding(player), "gliding with an open glider in the air");

        int before = backend.stamina(player);
        tickEvent(player, 20);
        helper.assertTrue(glide.isPerforming(), "gliding costs stamina");
        int drained = before - backend.stamina(player);
        int expected = (int) (GlidersConfig.GLIDE.perTick() * 20);
        helper.assertTrue(Math.abs(drained - expected) <= 1, "a second of gliding drains " + expected + ", drained " + drained);

        String state = exhaust(backend, player);
        tickEvent(player, 3);
        helper.assertFalse(glide.isPerforming(), "gliding stops when it can't be paid" + state);
        helper.assertFalse(GlidersTestHooks.gliderOpen(player), "the glider folds when the stamina runs out");
        GlidersTestHooks.pressDeployKey(player);
        helper.assertFalse(GlidersTestHooks.gliderOpen(player), "a glider can't be opened without stamina");
        helper.succeed();
    }
}

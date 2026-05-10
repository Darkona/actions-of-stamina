package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpConfig;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaUnits;
import com.ccr4ft3r.actionsofstamina.util.ActionFlags;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.player;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayer;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.tickEvent;

/**
 * Compat hooks against the real mods; each test passes trivially when its mod isn't installed (run them with
 * {@code -PwithCompat}). Foreign classes are only touched through {@code *TestHooks}, loaded only when the mod is.
 */
@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class CompatTests {

    private static final ResourceLocation TEST = ActionsOfStamina.id("test");

    /** Spends in small steps until nothing more can be spent (Green Feathers: through Strain too). */
    private static String exhaust(StaminaBackend backend, ServerPlayer player) {
        int spends = 0;
        while (spends < 10000 && backend.spend(player, TEST, 50, 0)) spends++;
        return " (after " + spends + " spends: stamina " + backend.stamina(player) + ", available "
                + backend.availableStamina(player) + ", exhausted " + backend.exhausted(player) + ")";
    }

    @GameTest(template = "empty")
    public static void parcoolActionsAreChargedAndBlocked(GameTestHelper helper) {
        if (!ParcoolCompat.LOADED) {
            helper.succeed();
            return;
        }
        ServerPlayer player = player(helper);
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
    public static void betterCombatSwingsAreCharged(GameTestHelper helper) {
        if (!BetterCombatCompat.LOADED) {
            helper.succeed();
            return;
        }
        helper.assertTrue(BetterCombatTestHooks.serverMixinApplied(), "mixin into Better Combat's ServerNetwork applied");
        ServerPlayer player = player(helper);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_SWORD));
        helper.assertTrue(BetterCombatCompat.handlesAttacksWith(player.getMainHandItem()), "a sword has Better Combat attacks");
        StaminaBackend backend = StaminaBackends.server();
        int before = backend.stamina(player);
        helper.assertTrue(BetterCombatCompat.chargeSwing(player, 0, false), "first swing paid");
        helper.assertValueEqual(backend.stamina(player), before - BetterCombatConfig.SWING.cost(), "stamina after one swing");

        String state = exhaust(backend, player);
        helper.assertFalse(BetterCombatCompat.chargeSwing(player, 0, false), "a swing is dropped when it can't be paid" + state);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void paraglidersReadsAoSStamina(GameTestHelper helper) {
        if (!ParagliderCompat.LOADED) {
            helper.succeed();
            return;
        }
        ServerPlayer player = player(helper);
        StaminaBackend backend = StaminaBackends.server();
        backend.spend(player, TEST, StaminaUnits.ofFeathers(3), 0);
        helper.assertTrue(ParagliderTestHooks.usesAoSStamina(player), "Paragliders' stamina is AoS's");
        helper.assertValueEqual((int) ParagliderTestHooks.stamina(player), backend.availableStamina(player), "Paragliders reads the backend");
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

    @GameTest(template = "empty")
    public static void combatRollsAreChargedAndRefused(GameTestHelper helper) {
        if (!CombatRollCompat.LOADED) {
            helper.succeed();
            return;
        }
        ServerPlayer player = player(helper);
        StaminaBackend backend = StaminaBackends.server();
        int before = backend.stamina(player);
        helper.assertTrue(CombatRollCompat.canRoll(player), "a roll is available with a full bar");
        CombatRollTestHooks.startRolling(player);
        helper.assertValueEqual(backend.stamina(player), before - CombatRollConfig.ROLL.cost(), "stamina after a roll");

        String state = exhaust(backend, player);
        helper.assertFalse(CombatRollCompat.canRoll(player), "no roll when it can't be paid" + state);
        int exhausted = backend.stamina(player);
        CombatRollTestHooks.startRolling(player);
        helper.assertValueEqual(backend.stamina(player), exhausted, "a roll charges nothing more when exhausted");
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
        ServerPlayer player = player(helper);
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
        ServerPlayer player = player(helper);
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
    public static void wallJumpMovesAreCharged(GameTestHelper helper) {
        if (!WallJumpCompat.LOADED) {
            helper.succeed();
            return;
        }
        ServerPlayer player = survivalPlayer(helper);
        StaminaBackend backend = StaminaBackends.server();
        int before = backend.stamina(player);
        WallJumpCompat.charge(player, WallJumpCompat.WALL_JUMP);
        helper.assertValueEqual(backend.stamina(player), before - WallJumpConfig.WALL_JUMP.cost(), "stamina after a wall jump");
        before = backend.stamina(player);
        WallJumpCompat.charge(player, WallJumpCompat.DOUBLE_JUMP);
        helper.assertValueEqual(backend.stamina(player), before - WallJumpConfig.DOUBLE_JUMP.cost(), "stamina after a double jump");

        PlayerActions actions = PlayerActions.get(player);
        Action cling = actions.getAction(Action.WALL_CLING);
        helper.assertTrue(cling != null, "the wall cling action exists");
        helper.assertTrue(WallJumpCompat.canCling(player), "a wall can be grabbed with stamina");
        actions.processFlags((short) ActionFlags.WALL_CLINGING);
        before = backend.stamina(player);
        tickEvent(player, 20);
        helper.assertTrue(cling.isPerforming(), "clinging");
        int drained = before - backend.stamina(player);
        int expected = (int) (WallJumpConfig.WALL_CLING.perTick() * 20);
        helper.assertTrue(Math.abs(drained - expected) <= 1, "a second of clinging drains " + expected + ", drained " + drained);

        String state = exhaust(backend, player);
        helper.assertFalse(WallJumpCompat.canCling(player), "can't hold on without stamina" + state);
        // Green Feathers settles a drain in its own tick, after AoS's: it may take one more tick to refuse it.
        tickEvent(player, 3);
        helper.assertFalse(cling.isPerforming(), "the cling stops when it can't be paid");
        actions.processFlags((short) 0);
        tickEvent(player, 1);
        helper.assertFalse(WallJumpCompat.canCling(player), "a wall can't be grabbed without stamina");
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
        Action glide = actions.getAction(Action.GLIDE);
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


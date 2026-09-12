package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpConfig;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.network.ActionPerformedPacket;
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
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.assertFalse;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.assertTrue;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.assertValueEqual;
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

    /** Spends in small steps until nothing more can be spent (Feathers of Fatigue: through strain too). */
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
        ServerPlayer player = survivalPlayer(helper);
        StaminaBackend backend = StaminaBackends.server();
        int before = backend.stamina(player);
        assertFalse(helper, ParcoolTestHooks.dodgeStartCancelled(player), "dodge may start with a full bar");
        ParcoolTestHooks.postDodgeStarted(player);
        assertValueEqual(helper, backend.stamina(player), before - StaminaUnits.ofFeathers(0.8), "stamina after a dodge");

        String state = exhaust(backend, player);
        assertTrue(helper, ParcoolTestHooks.dodgeStartCancelled(player), "dodge can't start when exhausted" + state);
        assertTrue(helper, ParcoolTestHooks.breakfallReadyCancelled(player), "a breakfall can't be readied when exhausted");
        assertFalse(helper, ParcoolTestHooks.breakfallRollCancelled(player), "a landed breakfall's roll is never refused");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void betterCombatSwingsAreCharged(GameTestHelper helper) {
        if (!BetterCombatCompat.LOADED) {
            helper.succeed();
            return;
        }
        assertTrue(helper, BetterCombatTestHooks.serverMixinApplied(), "mixin into Better Combat's ServerNetwork applied");
        ServerPlayer player = survivalPlayer(helper);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_SWORD));
        assertTrue(helper, BetterCombatCompat.handlesAttacksWith(player.getMainHandItem()), "a sword has Better Combat attacks");
        StaminaBackend backend = StaminaBackends.server();
        int before = backend.stamina(player);
        assertTrue(helper, BetterCombatCompat.chargeSwing(player, 0, false), "first swing paid");
        assertValueEqual(helper, backend.stamina(player), before - BetterCombatConfig.SWING.cost(), "stamina after one swing");

        String state = exhaust(backend, player);
        assertFalse(helper, BetterCombatCompat.chargeSwing(player, 0, false), "a swing is dropped when it can't be paid" + state);
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
        assertTrue(helper, ParagliderTestHooks.hasMovement(player), "the player has Paragliders' movement");
        assertValueEqual(helper, ParagliderTestHooks.stamina(player), backend.availableStamina(player), "Paragliders reads the backend");
        assertValueEqual(helper, ParagliderTestHooks.maxStamina(player), backend.maxStamina(player), "Paragliders' max stamina is the backend's");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void parcoolStaminaIsAoSStamina(GameTestHelper helper) {
        if (!ParcoolCompat.LOADED) {
            helper.succeed();
            return;
        }
        assertTrue(helper, ParcoolTestHooks.providerMixinApplied(), "mixin into ParCool's StaminaProvider applied");
        ServerPlayer player = player(helper);
        StaminaBackend backend = StaminaBackends.server();
        String problem = ParcoolTestHooks.checkReplacement(player);
        assertTrue(helper, problem == null, "ParCool stamina replacement: " + problem);
        backend.spend(player, TEST, StaminaUnits.ofFeathers(3), 0);
        problem = ParcoolTestHooks.checkStaminaMirrorsBackend(player, backend);
        assertTrue(helper, problem == null, "ParCool stamina: " + problem);
        exhaust(backend, player);
        problem = ParcoolTestHooks.checkStaminaMirrorsBackend(player, backend);
        assertTrue(helper, problem == null, "ParCool stamina when exhausted: " + problem);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void combatRollsAreChargedAndRefused(GameTestHelper helper) {
        if (!CombatRollCompat.LOADED) {
            helper.succeed();
            return;
        }
        ServerPlayer player = survivalPlayer(helper);
        StaminaBackend backend = StaminaBackends.server();
        int before = backend.stamina(player);
        assertTrue(helper, CombatRollCompat.canRoll(player), "a roll is available with a full bar");
        CombatRollTestHooks.startRolling(player);
        assertValueEqual(helper, backend.stamina(player), before - CombatRollConfig.ROLL.cost(), "stamina after a roll");

        String state = exhaust(backend, player);
        assertFalse(helper, CombatRollCompat.canRoll(player), "no roll when it can't be paid" + state);
        int exhausted = backend.stamina(player);
        CombatRollTestHooks.startRolling(player);
        assertValueEqual(helper, backend.stamina(player), exhausted, "a roll charges nothing more when exhausted");
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
        assertTrue(helper, EpicFightTestHooks.hasPatch(player), "the player has an Epic Fight server patch");
        StaminaBackend backend = StaminaBackends.server();
        for (int category = 0; category < EpicFightTestHooks.CATEGORIES; category++) {
            String name = EpicFightTestHooks.CATEGORY_NAMES[category];
            String skill = EpicFightTestHooks.skillName(category);
            assertTrue(helper, skill != null, "Epic Fight has a " + name + " skill");
            int before = backend.stamina(player);
            EpicFightTestHooks.Consume result = EpicFightTestHooks.consumeSkill(player, category);
            assertFalse(helper, result.canceled(), name + " (" + skill + ") goes ahead with a full bar");
            assertTrue(helper, result.switchedToNone(), name + " (" + skill + ") spends no Epic Fight stamina");
            assertValueEqual(helper, backend.stamina(player), before - epicFightCosts(category).cost(), "stamina after " + name);
        }

        String state = exhaust(backend, player);
        for (int category = 0; category < EpicFightTestHooks.CATEGORIES; category++) {
            String name = EpicFightTestHooks.CATEGORY_NAMES[category];
            EpicFightTestHooks.Consume result = EpicFightTestHooks.consumeSkill(player, category);
            assertTrue(helper, result.canceled(), name + " fails when it can't be paid" + state);
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
        assertTrue(helper, EpicFightTestHooks.hasPatch(player), "the player has an Epic Fight server patch");
        StaminaBackend backend = StaminaBackends.server();
        EpicFightTestHooks.battleMode(player, false);
        assertFalse(helper, EpicFightCompat.inBattleMode(player), "vanilla mode: AoS's vanilla attack cost applies");
        EpicFightTestHooks.battleMode(player, true);
        assertTrue(helper, EpicFightCompat.inBattleMode(player), "battle mode: AoS's vanilla attack cost stands aside");

        int before = backend.stamina(player);
        assertFalse(helper, EpicFightTestHooks.comboAttackCanceled(player), "a swing goes ahead with a full bar");
        assertValueEqual(helper, backend.stamina(player), before - EpicFightConfig.BASIC_ATTACK.cost(), "stamina after a swing");

        String state = exhaust(backend, player);
        assertTrue(helper, EpicFightTestHooks.comboAttackCanceled(player), "a swing that can't be paid doesn't happen" + state);
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
        ActionPerformedPacket.apply(player, WallJumpCompat.WALL_JUMP.index());
        assertValueEqual(helper, backend.stamina(player), before - WallJumpConfig.WALL_JUMP.cost(), "stamina after a wall jump");
        before = backend.stamina(player);
        ActionPerformedPacket.apply(player, WallJumpCompat.DOUBLE_JUMP.index());
        assertValueEqual(helper, backend.stamina(player), before - WallJumpConfig.DOUBLE_JUMP.cost(), "stamina after a double jump");

        PlayerActions actions = PlayerActions.get(player);
        Action cling = actions.getAction(WallJumpCompat.WALL_CLING);
        assertTrue(helper, cling != null, "the wall cling action exists");
        assertTrue(helper, WallJumpCompat.canCling(player), "a wall can be grabbed with stamina");
        actions.processFlags((short) ActionFlags.WALL_CLINGING);
        before = backend.stamina(player);
        tickEvent(player, 20);
        assertTrue(helper, cling.isPerforming(), "clinging");
        int drained = before - backend.stamina(player);
        int expected = (int) (WallJumpConfig.WALL_CLING.perTick() * 20);
        assertTrue(helper, Math.abs(drained - expected) <= 1, "a second of clinging drains " + expected + ", drained " + drained);

        String state = exhaust(backend, player);
        assertFalse(helper, WallJumpCompat.canCling(player), "can't hold on without stamina" + state);
        // Feathers of Fatigue settles a drain in its own tick, after AoS's: it may take one more tick to refuse it.
        tickEvent(player, 3);
        assertFalse(helper, cling.isPerforming(), "the cling stops when it can't be paid");
        actions.processFlags((short) 0);
        tickEvent(player, 1);
        assertFalse(helper, WallJumpCompat.canCling(player), "a wall can't be grabbed without stamina");
        helper.succeed();
    }
}

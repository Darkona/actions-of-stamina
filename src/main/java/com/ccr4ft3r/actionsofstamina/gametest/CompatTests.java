package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.network.ActionPerformedPacket;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaUnits;
import com.ccr4ft3r.actionsofstamina.util.ActionFlags;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.player;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayer;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.tickEvent;

/**
 * Compat hooks against the real mods; each test passes trivially when its mod isn't installed (run them with
 * {@code -PwithCompat}). Foreign classes are only touched through {@code *TestHooks}, loaded only when the mod is.
 */
public class CompatTests {

    private static final Identifier TEST = ActionsOfStamina.id("test");

    /** Spends in small steps until nothing more can be spent (Feathers of Fatigue: through strain too). */
    private static String exhaust(StaminaBackend backend, ServerPlayer player) {
        int spends = 0;
        while (spends < 10000 && backend.spend(player, TEST, 50, 0)) spends++;
        return " (after " + spends + " spends: stamina " + backend.stamina(player) + ", available "
                + backend.availableStamina(player) + ", exhausted " + backend.exhausted(player) + ")";
    }

    @GameTest(template = "empty")
    public static void betterCombatSwingsAreCharged(GameTestHelper helper) {
        if (!BetterCombatCompat.LOADED) {
            helper.succeed();
            return;
        }
        helper.assertTrue(BetterCombatTestHooks.serverMixinApplied(), "mixin into Better Combat's ServerNetwork applied");
        ServerPlayer player = survivalPlayer(helper);
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
    public static void combatRollsAreChargedAndRefused(GameTestHelper helper) {
        if (!CombatRollCompat.LOADED) {
            helper.succeed();
            return;
        }
        ServerPlayer player = survivalPlayer(helper);
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
        helper.assertValueEqual(backend.stamina(player), before - WallJumpConfig.WALL_JUMP.cost(), "stamina after a wall jump");
        before = backend.stamina(player);
        ActionPerformedPacket.apply(player, WallJumpCompat.DOUBLE_JUMP.index());
        helper.assertValueEqual(backend.stamina(player), before - WallJumpConfig.DOUBLE_JUMP.cost(), "stamina after a double jump");

        PlayerActions actions = PlayerActions.get(player);
        Action cling = actions.getAction(WallJumpCompat.WALL_CLING);
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
        // Feathers of Fatigue settles a drain in its own tick, after AoS's: it may take one more tick to refuse it.
        tickEvent(player, 3);
        helper.assertFalse(cling.isPerforming(), "the cling stops when it can't be paid");
        actions.processFlags((short) 0);
        tickEvent(player, 1);
        helper.assertFalse(WallJumpCompat.canCling(player), "a wall can't be grabbed without stamina");
        helper.succeed();
    }
}

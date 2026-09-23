package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.exhaust;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayer;

/**
 * The action type registry: every action, Minecraft's, each compat's and another mod's, is a registered type numbered
 * by its id; another mod's action is built, gated and charged like Actions of Stamina's own, finish cost included. On
 * whichever backend is active.
 */
public class ActionRegistryTests {

    @GameTest(template = "empty")
    public static void everyActionIsARegisteredType(GameTestHelper helper) {
        ActionType[] types = ActionTypes.all();
        for (int i = 0; i < types.length; i++) {
            helper.assertValueEqual(types[i].index(), i, types[i] + " slot");
            if (i > 0) helper.assertTrue(types[i - 1].id().compareTo(types[i].id()) < 0, "types sorted by id at " + types[i]);
        }
        helper.assertValueEqual(VanillaActions.ATTACK.id(), ActionsOfStamina.id("attack"), "attack id");
        helper.assertValueEqual(CombatRollCompat.ROLL.id(), ActionsOfStamina.id("combat_roll/roll"), "roll id");
        helper.assertValueEqual(WallJumpCompat.DOUBLE_JUMP.id(), ActionsOfStamina.id("walljump/double_jump"), "double jump id");
        helper.assertTrue(ActionTypes.byId(ActionsOfStamina.id("walljump/wall_cling")) != null, "a Wall-Jump TXF action is a type");
        helper.assertValueEqual(ActionTypes.byId(TestActionTypes.FINISHING_ID), TestActionTypes.finishing, "another mod's type by id");
        boolean refused = false;
        try {
            ActionTypes.register(ActionsOfStamina.id("test/late"), AoSServerConfig.JUMP, Action::new);
        } catch (IllegalStateException e) {
            refused = true;
        }
        helper.assertTrue(refused, "registering after the registry froze");
        helper.succeed();
    }

    /** A player built while the other mod's type is on, as on joining a level. */
    private static ServerPlayer playerWithOtherModsAction(GameTestHelper helper) {
        TestActionTypes.enabled = true;
        try {
            return survivalPlayer(helper);
        } finally {
            TestActionTypes.enabled = false;
        }
    }

    @GameTest(template = "empty")
    public static void otherModsActionChargesItsFinishCost(GameTestHelper helper) {
        ServerPlayer player = playerWithOtherModsAction(helper);
        int slot = TestActionTypes.finishing.index();
        PlayerActions actions = PlayerActions.get(player);
        Action action = actions.getAction(slot);
        helper.assertTrue(action != null && action.type() == TestActionTypes.finishing, "the other mod's action is built");
        StaminaBackend backend = StaminaBackends.server();
        int finishCost = TestActionTypes.finishingCosts.finishCost();
        int start = backend.stamina(player);
        actions.setActionState(TestActionTypes.finishing, true);
        action.tick(player, actions);
        helper.assertTrue(action.isPerforming(), "it begins");
        helper.assertValueEqual(backend.stamina(player), start, "beginning is free");
        actions.setActionState(TestActionTypes.finishing, false);
        action.tick(player, actions);
        helper.assertFalse(action.isPerforming(), "it ends");
        helper.assertValueEqual(backend.stamina(player), start - finishCost, "ending charges the finish cost");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void otherModsActionNeedsItsFinishCostToBegin(GameTestHelper helper) {
        ServerPlayer player = playerWithOtherModsAction(helper);
        exhaust(StaminaBackends.server(), player);
        int slot = TestActionTypes.finishing.index();
        helper.assertFalse(PlayerActions.canPerform(player, slot), "an exhausted player can't begin it");
        PlayerActions actions = PlayerActions.get(player);
        actions.setActionState(TestActionTypes.finishing, true);
        actions.getAction(slot).tick(player, actions);
        helper.assertFalse(actions.getAction(slot).isPerforming(), "it doesn't begin");
        helper.assertTrue(PlayerActions.canPerform(player, ActionTypes.count() + 5), "a slot no type has is never refused");
        helper.succeed();
    }
}

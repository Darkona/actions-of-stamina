package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.brush.BrushAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintAction;
import com.ccr4ft3r.actionsofstamina.compatibility.create.CrankAction;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.exhaust;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayer;

/**
 * The action type registry: built-in types keep their slots and ids, an addon's type gets the next slot and its action
 * is built, gated and charged like a built-in one, finish cost included. On whichever backend is active.
 */
@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class ActionRegistryTests {

    @GameTest(template = "empty")
    public static void builtInTypesKeepTheirSlots(GameTestHelper helper) {
        ActionType[] types = ActionTypes.all();
        helper.assertValueEqual(types[Action.ATTACK].id(), AttackAction.SOURCE, "attack slot");
        helper.assertValueEqual(types[Action.SPRINT].id(), SprintAction.SOURCE, "sprint slot");
        helper.assertValueEqual(types[Action.CRANK].id(), CrankAction.SOURCE, "crank slot");
        helper.assertValueEqual(types[Action.BRUSH].id(), BrushAction.SOURCE, "brush slot");
        helper.assertValueEqual(ActionTypes.byId(TestActionTypes.FINISHING_ID), TestActionTypes.finishing, "addon type by id");
        helper.assertValueEqual(TestActionTypes.finishing.index(), Action.BRUSH + 1, "addon type after the built-in ones");
        boolean refused = false;
        try {
            ActionTypes.register(ActionsOfStamina.id("test/late"), () -> false, () -> null);
        } catch (IllegalStateException e) {
            refused = true;
        }
        helper.assertTrue(refused, "registering after the registry froze");
        helper.succeed();
    }

    /** A player built while the addon type is on, as on joining a level. */
    private static ServerPlayer playerWithAddonAction(GameTestHelper helper) {
        TestActionTypes.enabled = true;
        try {
            return survivalPlayer(helper);
        } finally {
            TestActionTypes.enabled = false;
        }
    }

    @GameTest(template = "empty")
    public static void addonActionChargesItsFinishCost(GameTestHelper helper) {
        ServerPlayer player = playerWithAddonAction(helper);
        int slot = TestActionTypes.finishing.index();
        PlayerActions actions = PlayerActions.get(player);
        Action action = actions.getAction(slot);
        helper.assertTrue(action instanceof TestActionTypes.FinishingAction, "the addon's action is built");
        StaminaBackend backend = StaminaBackends.server();
        int finishCost = ParcoolConfig.byName("charge_jump").costs().finishCost();
        int start = backend.stamina(player);
        actions.setActionState(slot, true);
        action.tick(player, actions);
        helper.assertTrue(action.isPerforming(), "it begins");
        helper.assertValueEqual(backend.stamina(player), start, "beginning is free");
        actions.setActionState(slot, false);
        action.tick(player, actions);
        helper.assertFalse(action.isPerforming(), "it ends");
        helper.assertValueEqual(backend.stamina(player), start - finishCost, "ending charges the finish cost");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void addonActionNeedsItsFinishCostToBegin(GameTestHelper helper) {
        ServerPlayer player = playerWithAddonAction(helper);
        exhaust(StaminaBackends.server(), player);
        int slot = TestActionTypes.finishing.index();
        helper.assertFalse(PlayerActions.canPerform(player, slot), "an exhausted player can't begin it");
        PlayerActions actions = PlayerActions.get(player);
        actions.setActionState(slot, true);
        actions.getAction(slot).tick(player, actions);
        helper.assertFalse(actions.getAction(slot).isPerforming(), "it doesn't begin");
        helper.assertTrue(PlayerActions.canPerform(player, Action.BRUSH + 50), "a slot no type has is never refused");
        helper.succeed();
    }
}

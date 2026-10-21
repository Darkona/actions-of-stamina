package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.util.ActionFlags;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.exhaust;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayer;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.tickEvent;

/**
 * A continuous action through the real server player tick, on whichever backend is active: it begins when its flag
 * arrives, drains while it lasts, ends when the flag goes, and costs nothing while idle. Default sprint config.
 */
@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class ActionTickTests {

    @GameTest(template = "empty")
    public static void idleActionsCostNothing(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        StaminaBackend backend = StaminaBackends.server();
        int start = backend.stamina(player);
        tickEvent(player, 40);
        Action sprint = PlayerActions.get(player).getAction(VanillaActions.SPRINT);
        helper.assertTrue(sprint != null, "sprint action exists");
        helper.assertFalse(sprint.isPerforming(), "idle sprint isn't performing");
        helper.assertValueEqual(backend.stamina(player), start, "stamina while idle");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void continuousActionBeginsDrainsAndEnds(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        StaminaBackend backend = StaminaBackends.server();
        PlayerActions actions = PlayerActions.get(player);
        Action sprint = actions.getAction(VanillaActions.SPRINT);
        helper.assertTrue(sprint != null, "sprint action exists");
        tickEvent(player, 1);
        int start = backend.stamina(player);

        actions.processFlags((short) ActionFlags.SPRINTING);
        tickEvent(player, 20);
        helper.assertTrue(sprint.isPerforming(), "sprinting performs");
        helper.assertTrue(backend.stamina(player) < start, "sprinting drains: " + backend.stamina(player) + " of " + start);

        actions.processFlags((short) 0);
        tickEvent(player, 1);
        helper.assertFalse(sprint.isPerforming(), "sprint ends with its flag");
        int ended = backend.stamina(player);
        tickEvent(player, 10);
        helper.assertValueEqual(backend.stamina(player), ended, "no drain after the end (regen delay still running)");
        helper.succeed();
    }

    /**
     * Default swim config. The server sets the swimming pose from the sprint flag on every tick, so a refused swim
     * has to stop the sprint behind it, or the player swims on for free.
     */
    @GameTest(template = "empty")
    public static void refusedSwimStopsTheSprintBehindIt(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        exhaust(StaminaBackends.server(), player);
        player.setSprinting(true);
        player.setSwimming(true);
        PlayerActions.get(player).processFlags((short) ActionFlags.SWIMMING);
        tickEvent(player, 1);
        helper.assertFalse(player.isSprinting(), "swim refused: the sprint is stopped");
        helper.assertFalse(player.isSwimming(), "swim refused: the pose is dropped");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void exemptPlayersEndAndRestartActions(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        PlayerActions actions = PlayerActions.get(player);
        Action sprint = actions.getAction(VanillaActions.SPRINT);
        helper.assertTrue(sprint != null, "sprint action exists");

        actions.processFlags((short) ActionFlags.SPRINTING);
        tickEvent(player, 5);
        helper.assertTrue(sprint.isPerforming(), "sprinting performs");

        player.setGameMode(GameType.CREATIVE);
        tickEvent(player, 1);
        helper.assertFalse(sprint.isPerforming(), "creative ends the sprint");

        player.setGameMode(GameType.SURVIVAL);
        tickEvent(player, 1);
        helper.assertTrue(sprint.isPerforming(), "back in survival, the held sprint begins again");
        helper.succeed();
    }
}

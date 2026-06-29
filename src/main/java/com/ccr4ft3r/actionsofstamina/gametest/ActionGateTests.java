package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.climb.ClimbAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.row.RowAction;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.exhaust;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayer;

/**
 * The gates in front of actions, through the real right-click event, on whichever backend is active: an action the
 * player can afford goes ahead, one they can't is refused, exempt players and disabled actions are never refused.
 * Default config (shield, draw and throw on; climb and row off).
 */
@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class ActionGateTests {

    /** Whether right-clicking {@code item} in the main hand is refused. */
    private static boolean refused(ServerPlayer player, Item item) {
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item, 16));
        return NeoForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickItem(player, InteractionHand.MAIN_HAND)).isCanceled();
    }

    @GameTest(template = "empty")
    public static void affordableActionsGoAhead(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        StaminaBackend backend = StaminaBackends.server();
        helper.assertFalse(refused(player, Items.SHIELD), "raising a shield");
        helper.assertFalse(refused(player, Items.BOW), "drawing a bow");
        int before = backend.stamina(player);
        helper.assertFalse(refused(player, Items.SNOWBALL), "throwing a snowball");
        helper.assertTrue(backend.stamina(player) < before, "the throw is charged");
        helper.assertTrue(ClimbAction.mayClimbUp(player), "climbing (disabled)");
        helper.assertTrue(RowAction.mayRow(player), "rowing (disabled)");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void unaffordableActionsAreRefused(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        exhaust(StaminaBackends.server(), player);
        helper.assertTrue(refused(player, Items.SHIELD), "raising a shield");
        helper.assertTrue(refused(player, Items.BOW), "drawing a bow");
        helper.assertTrue(refused(player, Items.SNOWBALL), "throwing a snowball");
        helper.assertFalse(refused(player, Items.STICK), "using an item no action charges");
        helper.assertTrue(ClimbAction.mayClimbUp(player), "climbing (disabled)");
        helper.assertTrue(RowAction.mayRow(player), "rowing (disabled)");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void exemptPlayersAreNeverRefused(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        exhaust(StaminaBackends.server(), player);
        player.setGameMode(GameType.CREATIVE);
        helper.assertFalse(refused(player, Items.SHIELD), "raising a shield");
        helper.assertFalse(refused(player, Items.BOW), "drawing a bow");
        helper.assertFalse(refused(player, Items.SNOWBALL), "throwing a snowball");
        helper.succeed();
    }
}

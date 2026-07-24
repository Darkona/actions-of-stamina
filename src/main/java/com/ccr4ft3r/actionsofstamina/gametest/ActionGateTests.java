package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.jump.JumpAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.climb.ClimbAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.row.RowAction;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.assertValueEqual;
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
        return MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickItem(player, InteractionHand.MAIN_HAND));
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

    /** A jump-configured action whose charge goes through or not as the test says, counting the attempts. */
    private static final class ChargeProbe extends Action {
        boolean chargeGoesThrough;
        int attempts;

        ChargeProbe() {
            super(JumpAction.SOURCE, AoSServerConfig.JUMP);
        }

        @Override
        public boolean charge(Player player) {
            attempts++;
            return chargeGoesThrough;
        }

        @Override
        public String name() {
            return "charge_probe";
        }

        @Override
        public int id() {
            return Action.JUMP;
        }

        @Override
        protected void performingEffects(Player p, PlayerActions a) {
        }

        @Override
        protected void notPerformingEffects(Player player, PlayerActions a) {
        }
    }

    /** A charge that is due but doesn't go through is tried again on the next use, not after a whole new count. */
    @GameTest(template = "empty")
    public static void aFailedChargeIsTriedAgainNextUse(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        ChargeProbe probe = new ChargeProbe();
        int times = AoSServerConfig.JUMP.timesPerformedToExhaust();
        for (int i = 1; i < times; i++) helper.assertTrue(probe.perform(player), "use " + i + " before the charge is due");
        helper.assertFalse(probe.perform(player), "the due use, whose charge fails, is refused");
        probe.chargeGoesThrough = true;
        helper.assertTrue(probe.perform(player), "the next use goes ahead");
        assertValueEqual(helper, probe.attempts, 2, "charge attempts");
        for (int i = 1; i < times; i++) probe.perform(player);
        assertValueEqual(helper, probe.attempts, 2, "a charge that went through starts a new count");
        helper.succeed();
    }
}

package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.ElytraAction;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.actions.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.exhaust;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.only;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.stamina;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayer;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayerWith;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayerWithout;

@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class UseItemDispatchTests {

    private static ServerPlayer exhausted(ServerPlayer player) {
        exhaust(StaminaBackends.server(), player);
        return player;
    }

    private static void clickTimes(ServerPlayer player, ItemStack stack, ActionCostConfig costs) {
        for (int i = 0; i < costs.timesPerformedToExhaust(); i++) EventPosts.useItem(player, stack.copy());
    }

    @GameTest(template = "empty")
    public static void aShieldIsRefusedWithoutStaminaAndKeepsThePassResult(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        int before = stamina(player);
        PlayerInteractEvent.RightClickItem raised = EventPosts.useItem(player, new ItemStack(Items.SHIELD));
        helper.assertFalse(raised.isCanceled(), "raised with stamina");
        helper.assertValueEqual(stamina(player), before, "raising costs nothing on the click");
        PlayerInteractEvent.RightClickItem refused = EventPosts.useItem(exhausted(player), new ItemStack(Items.SHIELD));
        helper.assertTrue(refused.isCanceled(), "refused without stamina");
        helper.assertValueEqual(refused.getCancellationResult(), InteractionResult.PASS, "result of a refused shield");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void drawingIsRefusedForBowsTridentsAndUnchargedCrossbowsOnly(GameTestHelper helper) {
        ServerPlayer player = exhausted(survivalPlayer(helper));
        ItemStack charged = new ItemStack(Items.CROSSBOW);
        charged.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(new ItemStack(Items.ARROW)));
        helper.assertTrue(EventPosts.useItem(player, new ItemStack(Items.BOW)).isCanceled(), "bow");
        helper.assertTrue(EventPosts.useItem(player, new ItemStack(Items.TRIDENT)).isCanceled(), "trident");
        helper.assertTrue(EventPosts.useItem(player, new ItemStack(Items.CROSSBOW)).isCanceled(), "uncharged crossbow");
        helper.assertFalse(EventPosts.useItem(player, charged).isCanceled(), "charged crossbow (it only shoots)");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void aThrowIsChargedOnceAndRefusedWithFail(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        int start = stamina(player);
        helper.assertFalse(EventPosts.useItem(player, new ItemStack(Items.SNOWBALL, 16)).isCanceled(), "throw with stamina");
        helper.assertValueEqual(stamina(player), start - AoSServerConfig.THROW.cost(), "one click, one charge");
        PlayerInteractEvent.RightClickItem refused = EventPosts.useItem(exhausted(player), new ItemStack(Items.SNOWBALL, 16));
        helper.assertTrue(refused.isCanceled(), "refused without stamina");
        helper.assertValueEqual(refused.getCancellationResult(), InteractionResult.FAIL, "result of a refused throw");
        helper.assertFalse(EventPosts.useItem(player, new ItemStack(Items.STICK)).isCanceled(), "an item no action charges");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void castingARodIsChargedAndRefusedWithFail(GameTestHelper helper) {
        ServerPlayer player = survivalPlayerWith(helper, VanillaActions.FISH);
        int start = stamina(player);
        clickTimes(player, new ItemStack(Items.FISHING_ROD), AoSServerConfig.FISH);
        helper.assertValueEqual(stamina(player), start - AoSServerConfig.FISH.cost(), "the cast is charged");
        PlayerInteractEvent.RightClickItem refused = EventPosts.useItem(exhausted(player), new ItemStack(Items.FISHING_ROD));
        helper.assertTrue(refused.isCanceled(), "refused without stamina");
        helper.assertValueEqual(refused.getCancellationResult(), InteractionResult.FAIL, "result of a refused cast");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void castingARodIsFreeWhileFishingIsDisabled(GameTestHelper helper) {
        ServerPlayer player = exhausted(survivalPlayerWithout(helper, VanillaActions.FISH));
        helper.assertFalse(EventPosts.useItem(player, new ItemStack(Items.FISHING_ROD)).isCanceled(), "nothing refuses the cast");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void exemptPlayersAreNeverRefusedAndPayNothing(GameTestHelper helper) {
        for (GameType mode : new GameType[]{GameType.CREATIVE, GameType.SPECTATOR}) {
            ServerPlayer player = exhausted(survivalPlayerWith(helper, VanillaActions.FISH));
            player.setGameMode(mode);
            int start = stamina(player);
            for (ItemStack stack : new ItemStack[]{new ItemStack(Items.SHIELD), new ItemStack(Items.BOW), new ItemStack(Items.SNOWBALL), new ItemStack(Items.FISHING_ROD)}) {
                helper.assertFalse(EventPosts.useItem(player, stack).isCanceled(), mode + " with " + stack.getItem());
            }
            helper.assertValueEqual(stamina(player), start, mode + " pays nothing");
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void disabledActionsNeverRefuse(GameTestHelper helper) {
        ServerPlayer player = exhausted(survivalPlayerWithout(helper, VanillaActions.SHIELD, VanillaActions.DRAW, VanillaActions.THROW));
        helper.assertFalse(EventPosts.useItem(player, new ItemStack(Items.SHIELD)).isCanceled(), "shield");
        helper.assertFalse(EventPosts.useItem(player, new ItemStack(Items.BOW)).isCanceled(), "bow");
        helper.assertFalse(EventPosts.useItem(player, new ItemStack(Items.SNOWBALL)).isCanceled(), "snowball");
        helper.succeed();
    }

    private static final class BoostProbe extends ElytraAction {
        BoostProbe() {
            super(VanillaActions.ELYTRA);
        }

        double drain(Player player) {
            return drainPerTick(player);
        }

        double idleDrain() {
            return staminaPerTick;
        }
    }

    private static BoostProbe gliderWithProbe(ServerPlayer player, boolean gliding) {
        BoostProbe probe = new BoostProbe();
        only(player, probe);
        player.tickCount = 100;
        if (gliding) player.startFallFlying();
        return probe;
    }

    @GameTest(template = "empty")
    public static void aRocketWhileGlidingSuspendsTheFlightDrain(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        BoostProbe probe = gliderWithProbe(player, true);
        helper.assertTrue(probe.idleDrain() > 0, "gliding drains by default");
        helper.assertValueEqual(probe.drain(player), probe.idleDrain(), "drain before the rocket");
        helper.assertFalse(EventPosts.useItem(player, new ItemStack(Items.FIREWORK_ROCKET)).isCanceled(), "a rocket is never refused");
        double boosted = AoSServerConfig.ROCKET_BOOST_COSTS.getAsBoolean() ? probe.idleDrain() : 0;
        helper.assertValueEqual(probe.drain(player), boosted, "drain after the rocket");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void aRocketOnlyBoostsWhileGlidingForSurvivalPlayers(GameTestHelper helper) {
        ServerPlayer grounded = survivalPlayer(helper);
        BoostProbe groundedProbe = gliderWithProbe(grounded, false);
        EventPosts.useItem(grounded, new ItemStack(Items.FIREWORK_ROCKET));
        helper.assertValueEqual(groundedProbe.drain(grounded), groundedProbe.idleDrain(), "a rocket on the ground");

        ServerPlayer gliding = survivalPlayer(helper);
        BoostProbe stickProbe = gliderWithProbe(gliding, true);
        EventPosts.useItem(gliding, new ItemStack(Items.STICK));
        helper.assertValueEqual(stickProbe.drain(gliding), stickProbe.idleDrain(), "a stick while gliding");

        ServerPlayer creative = survivalPlayer(helper);
        BoostProbe creativeProbe = gliderWithProbe(creative, true);
        creative.setGameMode(GameType.CREATIVE);
        EventPosts.useItem(creative, new ItemStack(Items.FIREWORK_ROCKET));
        helper.assertValueEqual(creativeProbe.drain(creative), creativeProbe.idleDrain(), "a rocket for a creative player");
        helper.succeed();
    }
}

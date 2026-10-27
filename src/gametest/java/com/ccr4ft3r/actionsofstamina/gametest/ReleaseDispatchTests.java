package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.exhaust;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.stamina;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayer;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayerWith;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayerWithout;

@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class ReleaseDispatchTests {

    private static final int FULL_CHARGE = 20;
    private static final int TOO_SHORT = 3;

    private static ItemStack riptideTrident(ServerPlayer player) {
        ItemStack trident = new ItemStack(Items.TRIDENT);
        trident.enchant(player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.RIPTIDE), 1);
        return trident;
    }

    private static ServerPlayer swimmerWith(GameTestHelper helper, boolean riptide) {
        ServerPlayer player = riptide ? survivalPlayerWith(helper, VanillaActions.RIPTIDE) : survivalPlayer(helper);
        helper.setBlock(new BlockPos(0, 3, 0), Blocks.WATER);
        player.baseTick();
        return player;
    }

    @GameTest(template = "empty")
    public static void aReleasedTridentIsChargedAsAThrow(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        int start = stamina(player);
        helper.assertFalse(EventPosts.release(player, new ItemStack(Items.TRIDENT), TOO_SHORT).isCanceled(), "released too early");
        helper.assertValueEqual(stamina(player), start, "a trident that is not thrown costs nothing");
        helper.assertFalse(EventPosts.release(player, new ItemStack(Items.TRIDENT), FULL_CHARGE).isCanceled(), "thrown with stamina");
        helper.assertValueEqual(stamina(player), start - ActionsConfig.THROW.cost(), "the throw is charged");
        exhaust(StaminaBackends.server(), player);
        helper.assertTrue(EventPosts.release(player, new ItemStack(Items.TRIDENT), FULL_CHARGE).isCanceled(), "the throw is cancelled without stamina");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void aRiptideLaunchIsChargedAsARiptideNotAsAThrow(GameTestHelper helper) {
        ServerPlayer player = swimmerWith(helper, true);
        helper.assertTrue(player.isInWaterOrRain(), "the player is in water");
        int start = stamina(player);
        for (int i = 0; i < ActionsConfig.RIPTIDE.timesPerformedToExhaust(); i++) {
            helper.assertFalse(EventPosts.release(player, riptideTrident(player), FULL_CHARGE).isCanceled(), "launched with stamina");
        }
        helper.assertValueEqual(stamina(player), start - ActionsConfig.RIPTIDE.cost(), "only the riptide is charged");
        exhaust(StaminaBackends.server(), player);
        helper.assertTrue(EventPosts.release(player, riptideTrident(player), FULL_CHARGE).isCanceled(), "the launch is cancelled without stamina");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void aRiptideTridentOutOfWaterIsNotCharged(GameTestHelper helper) {
        ServerPlayer player = survivalPlayerWith(helper, VanillaActions.RIPTIDE);
        exhaust(StaminaBackends.server(), player);
        helper.assertFalse(player.isInWaterOrRain(), "the player is dry");
        helper.assertFalse(EventPosts.release(player, riptideTrident(player), FULL_CHARGE).isCanceled(), "nothing launches, nothing is refused");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void exemptPlayersAreNeverRefusedOnRelease(GameTestHelper helper) {
        for (GameType mode : new GameType[]{GameType.CREATIVE, GameType.SPECTATOR}) {
            ServerPlayer player = swimmerWith(helper, true);
            exhaust(StaminaBackends.server(), player);
            player.setGameMode(mode);
            int start = stamina(player);
            helper.assertFalse(EventPosts.release(player, new ItemStack(Items.TRIDENT), FULL_CHARGE).isCanceled(), mode + " throw");
            helper.assertFalse(EventPosts.release(player, riptideTrident(player), FULL_CHARGE).isCanceled(), mode + " riptide");
            helper.assertValueEqual(stamina(player), start, mode + " pays nothing");
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void disabledThrowAndRiptideNeverRefuse(GameTestHelper helper) {
        ServerPlayer player = survivalPlayerWithout(helper, VanillaActions.THROW);
        exhaust(StaminaBackends.server(), player);
        helper.assertFalse(EventPosts.release(player, new ItemStack(Items.TRIDENT), FULL_CHARGE).isCanceled(), "throw while disabled");
        helper.assertFalse(EventPosts.release(player, riptideTrident(player), FULL_CHARGE).isCanceled(), "riptide while disabled");
        helper.succeed();
    }
}

package com.ccr4ft3r.actionsofstamina.infrastructure.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.content.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import static com.ccr4ft3r.actionsofstamina.infrastructure.gametest.TestSupport.exhaust;
import static com.ccr4ft3r.actionsofstamina.infrastructure.gametest.TestSupport.stamina;
import static com.ccr4ft3r.actionsofstamina.infrastructure.gametest.TestSupport.survivalPlayer;
import static com.ccr4ft3r.actionsofstamina.infrastructure.gametest.TestSupport.survivalPlayerWith;

@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class ToolDispatchTests {

    private static final BlockPos BLOCK = new BlockPos(1, 2, 1);

    private static ServerPlayer exhausted(ServerPlayer player) {
        exhaust(StaminaBackends.server(), player);
        return player;
    }

    @GameTest(template = "empty")
    public static void brushingIsRefusedOnlyWithoutStaminaAndOnlyForBrushes(GameTestHelper helper) {
        ServerPlayer player = survivalPlayerWith(helper, VanillaActions.BRUSH);
        BlockPos pos = helper.absolutePos(BLOCK);
        TestSupport.assertValueEqual(helper, EventPosts.useOnBlock(player, new ItemStack(Items.BRUSH), pos).getUseItem(), Event.Result.DEFAULT, "brushing with stamina");
        exhausted(player);
        TestSupport.assertValueEqual(helper, EventPosts.useOnBlock(player, new ItemStack(Items.BRUSH), pos).getUseItem(), Event.Result.DENY, "brushing without stamina");
        TestSupport.assertValueEqual(helper, EventPosts.useOnBlock(player, new ItemStack(Items.STICK), pos).getUseItem(), Event.Result.DEFAULT, "an item that does not brush");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void brushingIsNeverRefusedForExemptPlayersOrWhileDisabled(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(BLOCK);
        ServerPlayer disabled = exhausted(survivalPlayer(helper));
        TestSupport.assertValueEqual(helper, EventPosts.useOnBlock(disabled, new ItemStack(Items.BRUSH), pos).getUseItem(), Event.Result.DEFAULT, "brush disabled");
        for (GameType mode : new GameType[]{GameType.CREATIVE, GameType.SPECTATOR}) {
            ServerPlayer player = exhausted(survivalPlayerWith(helper, VanillaActions.BRUSH));
            player.setGameMode(mode);
            TestSupport.assertValueEqual(helper, EventPosts.useOnBlock(player, new ItemStack(Items.BRUSH), pos).getUseItem(), Event.Result.DEFAULT, mode + " brushing");
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void workingABlockIsChargedAndRefusedWithoutStamina(GameTestHelper helper) {
        ServerPlayer player = survivalPlayerWith(helper, VanillaActions.TILL);
        helper.setBlock(BLOCK, Blocks.OAK_LOG);
        BlockPos pos = helper.absolutePos(BLOCK);
        ItemStack axe = new ItemStack(Items.IRON_AXE);
        int start = stamina(player);
        for (int i = 0; i < ActionsConfig.TILL.timesPerformedToExhaust(); i++) {
            helper.assertFalse(EventPosts.toolOn(player, axe, pos, ToolActions.AXE_STRIP, false).isCanceled(), "stripping with stamina");
        }
        TestSupport.assertValueEqual(helper, stamina(player), start - ActionsConfig.TILL.cost(), "stripping is charged");
        exhausted(player);
        helper.assertTrue(EventPosts.toolOn(player, axe, pos, ToolActions.AXE_STRIP, false).isCanceled(), "stripping without stamina is cancelled");
        helper.assertFalse(EventPosts.toolOn(player, axe, pos, ToolActions.AXE_STRIP, true).isCanceled(), "a simulated strip is never refused");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void onlyAToolThatChangesTheBlockIsCharged(GameTestHelper helper) {
        ServerPlayer player = exhausted(survivalPlayerWith(helper, VanillaActions.TILL));
        BlockPos pos = helper.absolutePos(BLOCK);
        helper.setBlock(BLOCK, Blocks.STONE);
        helper.assertFalse(EventPosts.toolOn(player, new ItemStack(Items.IRON_AXE), pos, ToolActions.AXE_STRIP, false).isCanceled(), "an axe on stone changes nothing");
        helper.setBlock(BLOCK, Blocks.GRASS_BLOCK);
        helper.setBlock(BLOCK.above(), Blocks.STONE);
        helper.assertFalse(EventPosts.toolOn(player, new ItemStack(Items.IRON_SHOVEL), pos, ToolActions.SHOVEL_FLATTEN, false).isCanceled(), "a shovel under a block changes nothing");
        helper.setBlock(BLOCK.above(), Blocks.AIR);
        helper.assertTrue(EventPosts.toolOn(player, new ItemStack(Items.IRON_SHOVEL), pos, ToolActions.SHOVEL_FLATTEN, false).isCanceled(), "a shovel on open grass makes a path, so it is charged");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void workingABlockIsNeverRefusedForExemptPlayersOrWhileDisabled(GameTestHelper helper) {
        helper.setBlock(BLOCK, Blocks.OAK_LOG);
        BlockPos pos = helper.absolutePos(BLOCK);
        ItemStack axe = new ItemStack(Items.IRON_AXE);
        ServerPlayer disabled = exhausted(survivalPlayer(helper));
        helper.assertFalse(EventPosts.toolOn(disabled, axe, pos, ToolActions.AXE_STRIP, false).isCanceled(), "till disabled");
        for (GameType mode : new GameType[]{GameType.CREATIVE, GameType.SPECTATOR}) {
            ServerPlayer player = exhausted(survivalPlayerWith(helper, VanillaActions.TILL));
            player.setGameMode(mode);
            int start = stamina(player);
            helper.assertFalse(EventPosts.toolOn(player, axe, pos, ToolActions.AXE_STRIP, false).isCanceled(), mode + " stripping");
            TestSupport.assertValueEqual(helper, stamina(player), start, mode + " pays nothing");
        }
        helper.succeed();
    }
}

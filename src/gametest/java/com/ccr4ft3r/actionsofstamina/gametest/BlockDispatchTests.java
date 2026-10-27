package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.exhaust;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.stamina;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayer;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayerWith;

@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class BlockDispatchTests {

    private static final BlockPos BLOCK = new BlockPos(1, 2, 1);
    private static final float STONE_HARDNESS = 1.5F;
    private static final float BASE_SPEED = 10.0F;

    private static ServerPlayer exhausted(ServerPlayer player) {
        exhaust(StaminaBackends.server(), player);
        return player;
    }

    private static ServerPlayer slowedWhenExhausted(GameTestHelper helper) {
        boolean before = ActionsConfig.MINE_BLOCK_WHEN_EXHAUSTED.getAsBoolean();
        ActionsConfig.MINE_BLOCK_WHEN_EXHAUSTED.set(true);
        try {
            return survivalPlayerWith(helper, VanillaActions.MINE);
        } finally {
            ActionsConfig.MINE_BLOCK_WHEN_EXHAUSTED.set(before);
        }
    }

    @GameTest(template = "empty")
    public static void minedBlocksAreChargedInBatchesByHardness(GameTestHelper helper) {
        ServerPlayer player = survivalPlayerWith(helper, VanillaActions.MINE);
        helper.setBlock(BLOCK, Blocks.STONE);
        BlockPos pos = helper.absolutePos(BLOCK);
        int times = ActionsConfig.MINE.timesPerformedToExhaust();
        double hardness = ActionsConfig.MINE_SCALE_WITH_HARDNESS.getAsBoolean() ? Math.min(STONE_HARDNESS, ActionsConfig.MINE_MAX_HARDNESS_MULTIPLIER.getAsDouble()) : 1.0;
        int start = stamina(player);
        for (int i = 1; i < times; i++) {
            helper.assertFalse(EventPosts.breakBlock(player, pos).isCanceled(), "block " + i + " is never cancelled");
            helper.assertValueEqual(stamina(player), start, "block " + i + " is not charged yet");
        }
        EventPosts.breakBlock(player, pos);
        helper.assertValueEqual(stamina(player), start - (int) Math.round(ActionsConfig.MINE.cost() * hardness * times), "the batch is charged");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void miningIsFreeForExemptPlayersAndWhileDisabled(GameTestHelper helper) {
        helper.setBlock(BLOCK, Blocks.STONE);
        BlockPos pos = helper.absolutePos(BLOCK);
        ServerPlayer disabled = survivalPlayer(helper);
        int start = stamina(disabled);
        for (int i = 0; i < 2 * ActionsConfig.MINE.timesPerformedToExhaust(); i++) EventPosts.breakBlock(disabled, pos);
        helper.assertValueEqual(stamina(disabled), start, "mining disabled");
        for (GameType mode : new GameType[]{GameType.CREATIVE, GameType.SPECTATOR}) {
            ServerPlayer player = survivalPlayerWith(helper, VanillaActions.MINE);
            player.setGameMode(mode);
            int before = stamina(player);
            for (int i = 0; i < 2 * ActionsConfig.MINE.timesPerformedToExhaust(); i++) EventPosts.breakBlock(player, pos);
            helper.assertValueEqual(stamina(player), before, mode + " pays nothing");
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void breakSpeedIsCutOnlyWhenExhaustedAndConfiguredTo(GameTestHelper helper) {
        BlockState stone = Blocks.STONE.defaultBlockState();
        ServerPlayer slowed = slowedWhenExhausted(helper);
        helper.assertValueEqual(EventPosts.breakSpeed(slowed, stone, BASE_SPEED).getNewSpeed(), BASE_SPEED, "with stamina");
        exhausted(slowed);
        float cut = BASE_SPEED * (float) ActionsConfig.MINE_EXHAUSTED_BREAK_SPEED.getAsDouble();
        helper.assertValueEqual(EventPosts.breakSpeed(slowed, stone, BASE_SPEED).getNewSpeed(), cut, "without stamina");

        ServerPlayer unconfigured = exhausted(survivalPlayerWith(helper, VanillaActions.MINE));
        helper.assertValueEqual(EventPosts.breakSpeed(unconfigured, stone, BASE_SPEED).getNewSpeed(), BASE_SPEED, "without stamina, option off");

        ServerPlayer creative = exhausted(slowedWhenExhausted(helper));
        creative.setGameMode(GameType.CREATIVE);
        helper.assertValueEqual(EventPosts.breakSpeed(creative, stone, BASE_SPEED).getNewSpeed(), BASE_SPEED, "exempt player");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void placedBlocksAreChargedInBatchesAndNeverRefused(GameTestHelper helper) {
        ServerPlayer player = survivalPlayerWith(helper, VanillaActions.BUILD);
        helper.setBlock(BLOCK, Blocks.STONE);
        BlockPos pos = helper.absolutePos(BLOCK);
        int times = ActionsConfig.BUILD.timesPerformedToExhaust();
        helper.assertTrue(ActionsConfig.BUILD.cost() > 0, "building costs something");
        int start = stamina(player);
        for (int i = 1; i < times; i++) {
            helper.assertFalse(EventPosts.placeBlock(player, new ItemStack(Items.STONE, 64), pos).isCanceled(), "block " + i);
            helper.assertValueEqual(stamina(player), start, "block " + i + " is not charged yet");
        }
        EventPosts.placeBlock(player, new ItemStack(Items.STONE, 64), pos);
        helper.assertValueEqual(stamina(player), start - ActionsConfig.BUILD.cost(), "the batch is charged");
        exhausted(player);
        helper.assertFalse(EventPosts.placeBlock(player, new ItemStack(Items.STONE, 64), pos).isCanceled(), "placing is never refused");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void onlyABlockPlacedFromTheHandIsCharged(GameTestHelper helper) {
        ServerPlayer player = survivalPlayerWith(helper, VanillaActions.BUILD);
        helper.setBlock(BLOCK, Blocks.STONE);
        BlockPos pos = helper.absolutePos(BLOCK);
        int start = stamina(player);
        for (int i = 0; i < 2 * ActionsConfig.BUILD.timesPerformedToExhaust(); i++) {
            EventPosts.placeBlock(player, new ItemStack(Items.DIRT, 64), pos);
            EventPosts.placeBlock(player, ItemStack.EMPTY, pos);
        }
        helper.assertValueEqual(stamina(player), start, "a block that is not the held item is free");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void buildingIsFreeForExemptPlayersAndWhileDisabled(GameTestHelper helper) {
        helper.setBlock(BLOCK, Blocks.STONE);
        BlockPos pos = helper.absolutePos(BLOCK);
        ServerPlayer disabled = survivalPlayer(helper);
        int start = stamina(disabled);
        for (int i = 0; i < 2 * ActionsConfig.BUILD.timesPerformedToExhaust(); i++) EventPosts.placeBlock(disabled, new ItemStack(Items.STONE, 64), pos);
        helper.assertValueEqual(stamina(disabled), start, "building disabled");
        for (GameType mode : new GameType[]{GameType.CREATIVE, GameType.SPECTATOR}) {
            ServerPlayer player = survivalPlayerWith(helper, VanillaActions.BUILD);
            player.setGameMode(mode);
            int before = stamina(player);
            for (int i = 0; i < 2 * ActionsConfig.BUILD.timesPerformedToExhaust(); i++) EventPosts.placeBlock(player, new ItemStack(Items.STONE, 64), pos);
            helper.assertValueEqual(stamina(player), before, mode + " pays nothing");
        }
        helper.succeed();
    }
}

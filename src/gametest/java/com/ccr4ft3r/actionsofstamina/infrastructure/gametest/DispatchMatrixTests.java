package com.ccr4ft3r.actionsofstamina.infrastructure.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.content.actions.Action;
import com.ccr4ft3r.actionsofstamina.content.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.content.actions.VanillaActions;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import static com.ccr4ft3r.actionsofstamina.infrastructure.gametest.TestSupport.only;
import static com.ccr4ft3r.actionsofstamina.infrastructure.gametest.TestSupport.player;
import static com.ccr4ft3r.actionsofstamina.infrastructure.gametest.TestSupport.survivalPlayer;

@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class DispatchMatrixTests {

    private static final BlockPos STONE = new BlockPos(1, 2, 1);

    private static final class Recorder extends Action {
        private final List<String> log;
        private final boolean cancels;

        Recorder(ActionType type, List<String> log) {
            this(type, log, false);
        }

        Recorder(ActionType type, List<String> log, boolean cancels) {
            super(type);
            this.log = log;
            this.cancels = cancels;
        }

        private void record() {
            log.add(type.id().getPath());
        }

        @Override
        public void onAttack(Player player, AttackEntityEvent event) {
            record();
        }

        @Override
        public void onUseItem(Player player, PlayerInteractEvent.RightClickItem event) {
            record();
            if (cancels) event.setCanceled(true);
        }

        @Override
        public void onUseBlock(Player player, PlayerInteractEvent.RightClickBlock event) {
            record();
        }

        @Override
        public void onStopUsing(Player player, LivingEntityUseItemEvent.Stop event) {
            record();
            if (cancels) event.setCanceled(true);
        }

        @Override
        public void onToolModification(Player player, BlockEvent.BlockToolModificationEvent event) {
            record();
        }

        @Override
        public void onBlockBroken(Player player, BlockEvent.BreakEvent event) {
            record();
        }

        @Override
        public void onBlockPlaced(Player player, BlockEvent.EntityPlaceEvent event) {
            record();
        }

        @Override
        public void onBreakSpeed(Player player, PlayerEvent.BreakSpeed event) {
            record();
        }
    }

    private static List<String> install(ServerPlayer player, ActionType... except) {
        return install(player, List.of(), except);
    }

    private static List<String> install(ServerPlayer player, List<ActionType> cancelling, ActionType... except) {
        List<String> log = new ArrayList<>();
        List<ActionType> skipped = List.of(except);
        List<Action> recorders = new ArrayList<>();
        for (ActionType type : ActionType.all()) {
            if (!skipped.contains(type)) recorders.add(new Recorder(type, log, cancelling.contains(type)));
        }
        only(player, recorders.toArray(new Action[0]));
        return log;
    }

    private static void fireEverything(GameTestHelper helper, ServerPlayer player, Entity target) {
        BlockPos stone = helper.absolutePos(STONE);
        ItemStack axe = new ItemStack(Items.IRON_AXE);
        EventPosts.attack(player, new ItemStack(Items.IRON_SWORD), target);
        EventPosts.useItem(player, new ItemStack(Items.STICK));
        EventPosts.useOnBlock(player, new ItemStack(Items.STICK), stone);
        EventPosts.release(player, new ItemStack(Items.STICK), 20);
        EventPosts.toolOn(player, axe, stone, ItemAbilities.AXE_STRIP, true);
        EventPosts.breakBlock(player, stone);
        EventPosts.placeBlock(player, new ItemStack(Items.STONE), stone);
        EventPosts.breakSpeed(player, Blocks.STONE.defaultBlockState(), 1.0F);
    }

    @GameTest(template = "empty")
    public static void everyEventReachesItsActionsInOrder(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        List<String> log = install(player);
        helper.setBlock(STONE, Blocks.STONE);
        Entity target = helper.spawn(EntityType.ARMOR_STAND, new BlockPos(2, 2, 2));
        BlockPos stone = helper.absolutePos(STONE);

        EventPosts.attack(player, new ItemStack(Items.IRON_SWORD), target);
        helper.assertValueEqual(log, List.of("attack"), "attack");
        log.clear();
        EventPosts.useItem(player, new ItemStack(Items.STICK));
        helper.assertValueEqual(log, List.of("shield", "draw", "throw", "fish", "elytra"), "right click on an item, normal priority first, then low");
        log.clear();
        EventPosts.useOnBlock(player, new ItemStack(Items.STICK), stone);
        helper.assertValueEqual(log, List.of("brush"), "right click on a block");
        log.clear();
        EventPosts.release(player, new ItemStack(Items.STICK), 20);
        helper.assertValueEqual(log, List.of("throw", "riptide"), "releasing an item");
        log.clear();
        EventPosts.toolOn(player, new ItemStack(Items.IRON_AXE), stone, ItemAbilities.AXE_STRIP, true);
        helper.assertValueEqual(log, List.of("till"), "tool modification");
        log.clear();
        EventPosts.breakBlock(player, stone);
        helper.assertValueEqual(log, List.of("mine"), "breaking a block");
        log.clear();
        EventPosts.placeBlock(player, new ItemStack(Items.STONE), stone);
        helper.assertValueEqual(log, List.of("build"), "placing a block");
        log.clear();
        EventPosts.breakSpeed(player, Blocks.STONE.defaultBlockState(), 1.0F);
        helper.assertValueEqual(log, List.of("mine"), "break speed");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void exemptPlayersReachNoAction(GameTestHelper helper) {
        helper.setBlock(STONE, Blocks.STONE);
        Entity target = helper.spawn(EntityType.ARMOR_STAND, new BlockPos(2, 2, 2));
        for (GameType mode : new GameType[]{GameType.CREATIVE, GameType.SPECTATOR}) {
            ServerPlayer player = survivalPlayer(helper);
            List<String> log = install(player);
            player.setGameMode(mode);
            fireEverything(helper, player, target);
            helper.assertTrue(log.isEmpty(), mode + " player reached " + log);
        }
        ServerPlayer fake = player(helper);
        List<String> log = install(fake);
        fireEverything(helper, fake, target);
        helper.assertTrue(log.isEmpty(), "a fake player reached " + log);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void aDisabledActionIsSkippedAndTheRestStillFire(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        List<String> log = install(player, VanillaActions.DRAW, VanillaActions.FISH);
        EventPosts.useItem(player, new ItemStack(Items.STICK));
        helper.assertValueEqual(log, List.of("shield", "throw", "elytra"), "right click without draw and fish");
        helper.setBlock(STONE, Blocks.STONE);
        log = install(player, VanillaActions.MINE);
        EventPosts.breakBlock(player, helper.absolutePos(STONE));
        EventPosts.breakSpeed(player, Blocks.STONE.defaultBlockState(), 1.0F);
        helper.assertTrue(log.isEmpty(), "mining events without the mine action reached " + log);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void aCancelledEventDoesNotReachTheNextActions(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        List<String> log = install(player, List.of(VanillaActions.SHIELD));
        EventPosts.useItem(player, new ItemStack(Items.STICK));
        helper.assertValueEqual(log, List.of("shield"), "a shield that cancels stops the bow, the throw, the rod and the rocket");
        log = install(player, List.of(VanillaActions.THROW));
        EventPosts.useItem(player, new ItemStack(Items.STICK));
        helper.assertValueEqual(log, List.of("shield", "draw", "throw"), "a throw that cancels stops the rod and the rocket");
        log = install(player, List.of(VanillaActions.THROW));
        EventPosts.release(player, new ItemStack(Items.STICK), 20);
        helper.assertValueEqual(log, List.of("throw"), "a release that the throw cancels does not reach riptide");
        helper.succeed();
    }
}

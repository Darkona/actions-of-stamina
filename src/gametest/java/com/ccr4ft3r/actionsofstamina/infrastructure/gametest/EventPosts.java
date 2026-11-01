package com.ccr4ft3r.actionsofstamina.infrastructure.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;

final class EventPosts {

    private static final InteractionHand HAND = InteractionHand.MAIN_HAND;

    private EventPosts() {
    }

    private static <E extends Event> E post(E event) {
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    static AttackEntityEvent attack(ServerPlayer player, ItemStack held, Entity target) {
        player.setItemInHand(HAND, held);
        return post(new AttackEntityEvent(player, target));
    }

    static PlayerInteractEvent.RightClickItem useItem(ServerPlayer player, ItemStack held) {
        player.setItemInHand(HAND, held);
        return post(new PlayerInteractEvent.RightClickItem(player, HAND));
    }

    static PlayerInteractEvent.RightClickBlock useOnBlock(ServerPlayer player, ItemStack held, BlockPos pos) {
        player.setItemInHand(HAND, held);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        return post(new PlayerInteractEvent.RightClickBlock(player, HAND, pos, hit));
    }

    static LivingEntityUseItemEvent.Stop release(ServerPlayer player, ItemStack held, int ticksHeld) {
        player.setItemInHand(HAND, held);
        int remaining = held.getUseDuration() - ticksHeld;
        return post(new LivingEntityUseItemEvent.Stop(player, held, remaining));
    }

    static BlockEvent.BlockToolModificationEvent toolOn(ServerPlayer player, ItemStack held, BlockPos pos, ToolAction ability, boolean simulate) {
        player.setItemInHand(HAND, held);
        ServerLevel level = (ServerLevel) player.level();
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        UseOnContext context = new UseOnContext(level, player, HAND, held, hit);
        return post(new BlockEvent.BlockToolModificationEvent(level.getBlockState(pos), context, ability, simulate));
    }

    static BlockEvent.BreakEvent breakBlock(ServerPlayer player, BlockPos pos) {
        ServerLevel level = (ServerLevel) player.level();
        return post(new BlockEvent.BreakEvent(level, pos, level.getBlockState(pos), player));
    }

    static BlockEvent.EntityPlaceEvent placeBlock(ServerPlayer player, ItemStack held, BlockPos pos) {
        player.setItemInHand(HAND, held);
        ServerLevel level = (ServerLevel) player.level();
        BlockSnapshot snapshot = BlockSnapshot.create(level.dimension(), level, pos);
        return post(new BlockEvent.EntityPlaceEvent(snapshot, level.getBlockState(pos.below()), player));
    }

    static PlayerEvent.BreakSpeed breakSpeed(ServerPlayer player, BlockState state, float speed) {
        return post(new PlayerEvent.BreakSpeed(player, state, speed, player.blockPosition()));
    }
}

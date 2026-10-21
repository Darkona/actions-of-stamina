package com.ccr4ft3r.actionsofstamina.gametest;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.venturecraft.gliders.common.item.GliderItem;
import net.venturecraft.gliders.common.item.ItemRegistry;
import net.venturecraft.gliders.network.MessageToggleGlide;

final class GlidersTestHooks {

    private GlidersTestHooks() {
    }

    static void equipGlider(ServerPlayer player) {
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ItemRegistry.PARAGLIDER_WOOD.get()));
    }

    static void pressDeployKey(ServerPlayer player) {
        MessageToggleGlide.handle(new PacketContext<>(player, new MessageToggleGlide(), Side.SERVER));
    }

    static boolean gliderOpen(ServerPlayer player) {
        return GliderItem.isGlidingEnabled(player.getItemBySlot(EquipmentSlot.CHEST));
    }
}

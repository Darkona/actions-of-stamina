package com.ccr4ft3r.actionsofstamina.gametest;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.venturecraft.gliders.common.item.GliderItem;
import net.venturecraft.gliders.common.item.ItemRegistry;
import net.venturecraft.gliders.network.MessageToggleGlide;

/** Gliders calls for {@link CompatTests}; loaded only when Gliders is. */
final class GlidersTestHooks {

    private GlidersTestHooks() {
    }

    /** A folded wooden glider on the chest. */
    static void equipGlider(ServerPlayer player) {
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ItemRegistry.PARAGLIDER_WOOD.get()));
    }

    /** What the server does when the player presses Gliders' deploy key: its real toggle handler. */
    static void pressDeployKey(ServerPlayer player) {
        MessageToggleGlide.handle(new PacketContext<>(player, new MessageToggleGlide(), Side.SERVER));
    }

    static boolean gliderOpen(ServerPlayer player) {
        return GliderItem.isGlidingEnabled(player.getItemBySlot(EquipmentSlot.CHEST));
    }
}

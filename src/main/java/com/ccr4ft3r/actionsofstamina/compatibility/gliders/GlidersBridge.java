package com.ccr4ft3r.actionsofstamina.compatibility.gliders;

import commonnetwork.api.Network;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.venturecraft.gliders.common.compat.trinket.CuriosTrinketsUtil;
import net.venturecraft.gliders.common.item.GliderItem;
import net.venturecraft.gliders.data.GliderData;
import net.venturecraft.gliders.network.MessagePOV;
import net.venturecraft.gliders.util.GliderUtil;

/** Direct Gliders calls; only reached through {@link GlidersCompat} when Gliders is loaded. */
final class GlidersBridge {

    private GlidersBridge() {
    }

    static boolean isGliding(Player player) {
        return GliderUtil.isGlidingWithActiveGlider(player);
    }

    /** What Gliders' own toggle does when it closes the glider: folded, camera back to first person, state synced. */
    static void close(ServerPlayer player) {
        ItemStack glider = CuriosTrinketsUtil.getInstance().getFirstFoundGlider(player);
        if (!(glider.getItem() instanceof GliderItem) || !GliderItem.isGlidingEnabled(glider)) return;
        GliderItem.setGlide(glider, false);
        GliderData.setLightningTimer(player, 0);
        Network.getNetworkHandler().sendToClient(new MessagePOV(""), player);
        GliderData.sync(player);
    }
}

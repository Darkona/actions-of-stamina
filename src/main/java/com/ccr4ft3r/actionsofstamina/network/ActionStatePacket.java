package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client → server: the local player's full movement-state flags (see {@code ActionFlags}), sent on change. */
public record ActionStatePacket(short actionFlags) {

    void encode(FriendlyByteBuf buf) {
        buf.writeShort(actionFlags);
    }

    static ActionStatePacket decode(FriendlyByteBuf buf) {
        return new ActionStatePacket(buf.readShort());
    }

    /** Runs on the server main thread. */
    static void handle(ActionStatePacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null) return;
        if (ActionsOfStamina.debugging()) ActionsOfStamina.sideLog(player, "Received ActionStatePacket with flags: {}.", packet.actionFlags);
        PlayerActions.get(player).processFlags(packet.actionFlags);
    }
}

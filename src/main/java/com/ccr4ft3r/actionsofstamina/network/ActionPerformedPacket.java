package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.content.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public record ActionPerformedPacket(byte actionId) {

    void encode(FriendlyByteBuf buf) {
        buf.writeByte(actionId);
    }

    static ActionPerformedPacket decode(FriendlyByteBuf buf) {
        return new ActionPerformedPacket(buf.readByte());
    }

    public static ActionPerformedPacket of(ActionType type) {
        return new ActionPerformedPacket((byte) type.index());
    }

    static void handle(ActionPerformedPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) PlayerActions.perform(player, packet.actionId);
    }
}

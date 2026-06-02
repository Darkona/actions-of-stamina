package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Client → server: a one-off action decided on the client (the attack key) charged its cost. Client-side spends are
 * only checked or predicted, so the server spends for real through its stamina backend, with the action's own
 * source and regen delay. Used with both backends. It can only ever cost the sender.
 */
public record ActionChargePacket(byte actionId) {

    void encode(FriendlyByteBuf buf) {
        buf.writeByte(actionId);
    }

    static ActionChargePacket decode(FriendlyByteBuf buf) {
        return new ActionChargePacket(buf.readByte());
    }

    /** Runs on the server main thread. */
    static void handle(ActionChargePacket packet, Supplier<NetworkEvent.Context> context) {
        int actionId = packet.actionId;
        ServerPlayer player = context.get().getSender();
        if (player == null || actionId < 0 || actionId >= Action.COUNT) return;
        Action action = PlayerActions.get(player).getAction(actionId);
        if (action != null && !PlayerActions.isNotExhaustable(player)) action.charge(player);
    }
}

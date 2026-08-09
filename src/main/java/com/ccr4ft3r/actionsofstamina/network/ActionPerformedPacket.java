package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Client → server: a one-off action the client decided and the server never sees on its own (a swing at the air).
 * The server performs it as one it saw itself: it counts towards {@code times_performed_to_exhaust} and is charged when it is
 * due, through the server's stamina backend, with the action's own source and regen delay. Client-side spends are
 * only checked or predicted, so the server's count is the one that charges. Used with both backends. It can only
 * ever cost the sender.
 */
public record ActionPerformedPacket(byte actionId) {

    void encode(FriendlyByteBuf buf) {
        buf.writeByte(actionId);
    }

    static ActionPerformedPacket decode(FriendlyByteBuf buf) {
        return new ActionPerformedPacket(buf.readByte());
    }

    /** Runs on the server main thread. */
    static void handle(ActionPerformedPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) apply(player, packet.actionId);
    }

    /** Performs action {@code actionId} for {@code player} on the server; an id no action type has is ignored. */
    public static void apply(Player player, int actionId) {
        PlayerActions.perform(player, actionId);
    }
}

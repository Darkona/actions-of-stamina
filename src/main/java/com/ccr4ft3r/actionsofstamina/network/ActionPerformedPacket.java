package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.ccr4ft3r.actionsofstamina.ActionsOfStamina.id;

/**
 * Client → server: a one-off action the client decided and the server never sees on its own (a swing at the air).
 * The server performs it as one it saw itself: it counts towards {@code times_performed_to_exhaust} and is charged when it is
 * due, through the server's stamina backend, with the action's own source and regen delay. Client-side spends are
 * only checked or predicted, so the server's count is the one that charges. Used with both backends. It can only
 * ever cost the sender.
 */
public record ActionPerformedPacket(byte actionId) implements CustomPacketPayload {

    public static final Type<ActionPerformedPacket> TYPE = new Type<>(id("action_performed"));

    public static final StreamCodec<ByteBuf, ActionPerformedPacket> STREAM_CODEC =
            ByteBufCodecs.BYTE.map(ActionPerformedPacket::new, ActionPerformedPacket::actionId);

    @Override
    public Type<ActionPerformedPacket> type() {
        return TYPE;
    }

    /** Runs on the server main thread (default handler thread). */
    public static void handle(ActionPerformedPacket packet, IPayloadContext context) {
        apply(context.player(), packet.actionId);
    }

    /** Performs action {@code actionId} for {@code player} on the server; an id no action type has is ignored. */
    public static void apply(Player player, int actionId) {
        PlayerActions.perform(player, actionId);
    }
}

package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Client → server: the local player's full movement-state flags (see {@code ActionFlags}), sent on change. */
public record ActionStatePacket(short actionFlags) implements CustomPacketPayload {

    public static final Type<ActionStatePacket> TYPE = new Type<>(ActionsOfStamina.id("action_state"));

    public static final StreamCodec<ByteBuf, ActionStatePacket> STREAM_CODEC =
            ByteBufCodecs.SHORT.map(ActionStatePacket::new, ActionStatePacket::actionFlags);

    @Override
    public Type<ActionStatePacket> type() {
        return TYPE;
    }

    /** Runs on the server main thread (default handler thread). */
    public static void handle(ActionStatePacket packet, IPayloadContext context) {
        var player = context.player();
        ActionsOfStamina.sideLog(player, "Received ActionStatePacket with flags: {}.", packet.actionFlags);
        PlayerActions.get(player).processFlags(packet.actionFlags);
    }
}

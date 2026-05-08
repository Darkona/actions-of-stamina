package com.ccr4ft3r.actionsofstamina.compatibility.walljump;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → server: the local player just made a Wall-Jump TXF move ({@link WallJumpCompat#WALL_JUMP} or
 * {@link WallJumpCompat#DOUBLE_JUMP}) that the client already checked against its stamina; the server charges it. It
 * can only ever cost the sender. Registered with or without Wall-Jump TXF, so both sides always agree on the channel.
 */
public record WallJumpChargePacket(byte move) implements CustomPacketPayload {

    public static final Type<WallJumpChargePacket> TYPE = new Type<>(ActionsOfStamina.id("walljump_charge"));

    public static final StreamCodec<ByteBuf, WallJumpChargePacket> STREAM_CODEC =
            ByteBufCodecs.BYTE.map(WallJumpChargePacket::new, WallJumpChargePacket::move);

    @Override
    public Type<WallJumpChargePacket> type() {
        return TYPE;
    }

    /** Runs on the server main thread (default handler thread). */
    public static void handle(WallJumpChargePacket packet, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) WallJumpCompat.charge(player, packet.move);
    }
}

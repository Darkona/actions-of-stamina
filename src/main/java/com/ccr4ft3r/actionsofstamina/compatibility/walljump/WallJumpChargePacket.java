package com.ccr4ft3r.actionsofstamina.compatibility.walljump;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Client → server: the local player just made a Wall-Jump TXF move ({@link WallJumpCompat#WALL_JUMP} or
 * {@link WallJumpCompat#DOUBLE_JUMP}) that the client already checked against its stamina; the server charges it. It
 * can only ever cost the sender. Registered with or without Wall-Jump TXF, so both sides always agree on the channel.
 */
public record WallJumpChargePacket(byte move) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(move);
    }

    public static WallJumpChargePacket decode(FriendlyByteBuf buf) {
        return new WallJumpChargePacket(buf.readByte());
    }

    /** Runs on the server main thread. */
    public static void handle(WallJumpChargePacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) WallJumpCompat.charge(player, packet.move);
    }
}

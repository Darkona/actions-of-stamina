package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Server → client during the configuration phase ({@link BackendSyncTask}): which stamina backend the server uses, so the client checks against the same one. */
public record BackendSyncPacket(byte kind) implements CustomPacketPayload {

    public static final Type<BackendSyncPacket> TYPE = new Type<>(ActionsOfStamina.id("backend"));

    public static final StreamCodec<ByteBuf, BackendSyncPacket> STREAM_CODEC =
            ByteBufCodecs.BYTE.map(BackendSyncPacket::new, BackendSyncPacket::kind);

    public BackendSyncPacket(StaminaBackend.Kind kind) {
        this((byte) kind.ordinal());
    }

    @Override
    public Type<BackendSyncPacket> type() {
        return TYPE;
    }

    public static void handle(BackendSyncPacket packet, IPayloadContext context) {
        StaminaBackends.setClient(StaminaBackend.Kind.byId(packet.kind));
    }
}

package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Server → client on login: which stamina backend the server uses, so the client checks against the same one. */
public record BackendSyncPacket(byte kind) {

    public BackendSyncPacket(StaminaBackend.Kind kind) {
        this((byte) kind.ordinal());
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeByte(kind);
    }

    static BackendSyncPacket decode(FriendlyByteBuf buf) {
        return new BackendSyncPacket(buf.readByte());
    }

    /** Runs on the client main thread; common code only. */
    static void handle(BackendSyncPacket packet, Supplier<NetworkEvent.Context> context) {
        StaminaBackends.setClient(StaminaBackend.Kind.byId(packet.kind));
    }
}

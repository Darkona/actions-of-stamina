package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

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

    static void handle(BackendSyncPacket packet, Supplier<NetworkEvent.Context> context) {
        StaminaBackends.setClient(StaminaBackend.Kind.byId(packet.kind));
    }
}

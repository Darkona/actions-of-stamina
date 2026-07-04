package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpChargePacket;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalStaminaPacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class PacketHandler {

    private static final String PROTOCOL_VERSION = "6";

    private PacketHandler() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION)
             .playToServer(ActionStatePacket.TYPE, ActionStatePacket.STREAM_CODEC, ActionStatePacket::handle)
             .playToServer(ActionPerformedPacket.TYPE, ActionPerformedPacket.STREAM_CODEC, ActionPerformedPacket::handle)
             .playToServer(WallJumpChargePacket.TYPE, WallJumpChargePacket.STREAM_CODEC, WallJumpChargePacket::handle)
             .configurationToClient(BackendSyncPacket.TYPE, BackendSyncPacket.STREAM_CODEC, BackendSyncPacket::handle)
             .playToClient(InternalStaminaPacket.TYPE, InternalStaminaPacket.STREAM_CODEC, InternalStaminaPacket::handle);
    }
}

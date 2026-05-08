package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpChargePacket;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalStaminaPacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class PacketHandler {

    private static final String PROTOCOL_VERSION = "5";

    private PacketHandler() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION)
             .playToServer(ActionStatePacket.TYPE, ActionStatePacket.STREAM_CODEC, ActionStatePacket::handle)
             .playToServer(ActionChargePacket.TYPE, ActionChargePacket.STREAM_CODEC, ActionChargePacket::handle)
             .playToServer(WallJumpChargePacket.TYPE, WallJumpChargePacket.STREAM_CODEC, WallJumpChargePacket::handle)
             .playToClient(BackendSyncPacket.TYPE, BackendSyncPacket.STREAM_CODEC, BackendSyncPacket::handle)
             .playToClient(InternalStaminaPacket.TYPE, InternalStaminaPacket.STREAM_CODEC, InternalStaminaPacket::handle);
    }
}

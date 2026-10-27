package com.ccr4ft3r.actionsofstamina.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ActionsPackets {

    private static final String PROTOCOL_VERSION = "7";

    private ActionsPackets() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ActionsPackets::registerPayloads);
        modBus.addListener(BackendSyncPacket.Task::register);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION)
             .playToServer(ActionStatePacket.TYPE, ActionStatePacket.STREAM_CODEC, ActionStatePacket::handle)
             .playToServer(ActionPerformedPacket.TYPE, ActionPerformedPacket.STREAM_CODEC, ActionPerformedPacket::handle)
             .configurationToClient(BackendSyncPacket.TYPE, BackendSyncPacket.STREAM_CODEC, BackendSyncPacket::handle)
             .playToClient(InternalStaminaPacket.TYPE, InternalStaminaPacket.STREAM_CODEC, InternalStaminaPacket::handle);
    }
}

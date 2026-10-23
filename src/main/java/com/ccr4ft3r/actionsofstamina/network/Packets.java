package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.stamina.InternalStaminaPacket;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import static com.ccr4ft3r.actionsofstamina.ActionsOfStamina.id;

public final class Packets {

    private Packets() {
    }

    public record ActionStatePacket(short actionFlags) implements CustomPacketPayload {

        public static final Type<ActionStatePacket> TYPE = new Type<>(id("action_state"));

        public static final StreamCodec<ByteBuf, ActionStatePacket> STREAM_CODEC =
                ByteBufCodecs.SHORT.map(ActionStatePacket::new, ActionStatePacket::actionFlags);

        @Override
        public Type<ActionStatePacket> type() {
            return TYPE;
        }

        public static void handle(ActionStatePacket packet, IPayloadContext context) {
            var player = context.player();
            if (ActionsOfStamina.debugging()) ActionsOfStamina.sideLog(player, "Received ActionStatePacket with flags: {}.", packet.actionFlags);
            PlayerActions.get(player).processFlags(packet.actionFlags);
        }
    }

    public record ActionPerformedPacket(byte actionId) implements CustomPacketPayload {

        public static final Type<ActionPerformedPacket> TYPE = new Type<>(id("action_performed"));

        public static final StreamCodec<ByteBuf, ActionPerformedPacket> STREAM_CODEC =
                ByteBufCodecs.BYTE.map(ActionPerformedPacket::new, ActionPerformedPacket::actionId);

        @Override
        public Type<ActionPerformedPacket> type() {
            return TYPE;
        }

        public static ActionPerformedPacket of(ActionType type) {
            return new ActionPerformedPacket((byte) type.index());
        }

        public static void handle(ActionPerformedPacket packet, IPayloadContext context) {
            PlayerActions.perform(context.player(), packet.actionId);
        }
    }

    public record BackendSyncPacket(byte kind) implements CustomPacketPayload {

        public static final Type<BackendSyncPacket> TYPE = new Type<>(id("backend"));

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

    public record BackendSyncTask(ServerConfigurationPacketListener listener) implements ICustomConfigurationTask {

        public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(id("backend"));

        public static void register(RegisterConfigurationTasksEvent event) {
            if (event.getListener().hasChannel(BackendSyncPacket.TYPE)) event.register(new BackendSyncTask(event.getListener()));
        }

        @Override
        public void run(Consumer<CustomPacketPayload> sender) {
            sender.accept(new BackendSyncPacket(StaminaBackends.server().kind()));
            listener.finishCurrentTask(TYPE);
        }

        @Override
        public Type type() {
            return TYPE;
        }
    }

    private static final String PROTOCOL_VERSION = "7";


    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION)
             .playToServer(ActionStatePacket.TYPE, ActionStatePacket.STREAM_CODEC, ActionStatePacket::handle)
             .playToServer(ActionPerformedPacket.TYPE, ActionPerformedPacket.STREAM_CODEC, ActionPerformedPacket::handle)
             .configurationToClient(BackendSyncPacket.TYPE, BackendSyncPacket.STREAM_CODEC, BackendSyncPacket::handle)
             .playToClient(InternalStaminaPacket.TYPE, InternalStaminaPacket.STREAM_CODEC, InternalStaminaPacket::handle);
    }
}

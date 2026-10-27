package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import static com.ccr4ft3r.actionsofstamina.ActionsOfStamina.id;

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

    public record Task(ServerConfigurationPacketListener listener) implements ICustomConfigurationTask {

        public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(id("backend"));

        public static void register(RegisterConfigurationTasksEvent event) {
            if (event.getListener().hasChannel(BackendSyncPacket.TYPE)) event.register(new Task(event.getListener()));
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
}

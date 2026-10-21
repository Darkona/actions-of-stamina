package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;

import java.util.function.Consumer;

public record BackendSyncTask(ServerConfigurationPacketListener listener) implements ICustomConfigurationTask {

    public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(ActionsOfStamina.id("backend"));

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

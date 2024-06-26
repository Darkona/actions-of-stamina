package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.capability.AosCapabilityProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;


public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1.0.0";
    private static final SimpleChannel SIMPLE_CHANNEL = NetworkRegistry
            .newSimpleChannel(new ResourceLocation(ActionsOfStamina.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void registerMessages() {
        SIMPLE_CHANNEL.registerMessage(0,
                ActionStatePacket.class,
                ActionStatePacket::encode,
                ActionStatePacket::decode,
                ActionStatePacket::handle);
    }

    public static void sendToServer(ActionStatePacket packet) {
        ActionsOfStamina.log("Sending ServerBoundActionStatePacket to server: {}.", Integer.toBinaryString(packet.getActionFlags()) );
        SIMPLE_CHANNEL.sendToServer(packet);
    }

    /*public static void sendToPlayer(ServerPlayer player, ActionStatePacket packet) {
        ActionsOfStamina.log("Sending ServerBoundActionStatePacket to player with Action: {} state: {}", packet.getAction(), packet.getState());
        SIMPLE_CHANNEL.sendTo(packet, player.connection.getConnection(), NetworkEvent.Context.get());
    }*/
}
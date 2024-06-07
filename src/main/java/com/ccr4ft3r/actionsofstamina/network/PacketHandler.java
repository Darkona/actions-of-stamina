package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.ModConstants;
import com.ccr4ft3r.actionsofstamina.events.ExhaustionHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

import static com.ccr4ft3r.actionsofstamina.config.AoSAction.WALL_JUMPING;
import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.getProfile;
import static com.ccr4ft3r.actionsofstamina.data.ServerData.getPlayerData;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1.0.0";
    private static final SimpleChannel SIMPLE_CHANNEL = NetworkRegistry
            .newSimpleChannel(new ResourceLocation(ModConstants.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void registerMessages() {
        SIMPLE_CHANNEL.registerMessage(0, ServerboundPacket.class, ServerboundPacket::encodeOnClientSide, ServerboundPacket::new, PacketHandler::handle);
    }

    public static void sendToServer(ServerboundPacket packet) {
        SIMPLE_CHANNEL.sendToServer(packet);
    }

    private static void handle(ServerboundPacket packet, Supplier<NetworkEvent.Context> ctx) {
        final NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            switch (packet.getAction()) {
                case PLAYER_MOVING -> getPlayerData(player).setMoving(true);
                case PLAYER_STOP_MOVING -> getPlayerData(player).setMoving(false);
                case WEAPON_SWING -> ExhaustionHandler.exhaustForWeaponSwing(player);
                case PLAYER_WALL_JUMP -> getPlayerData(player).set(WALL_JUMPING, true, player);
            }
            context.setPacketHandled(true);
        });
    }
}
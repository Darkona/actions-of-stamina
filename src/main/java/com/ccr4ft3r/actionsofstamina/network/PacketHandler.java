package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpChargePacket;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalStaminaPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class PacketHandler {

    private static final String PROTOCOL_VERSION = "5";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(ActionsOfStamina.id("main"), () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private PacketHandler() {
    }

    /** Mod construction. Client-bound handlers only touch client classes through {@code ClientPackets}. */
    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(ActionStatePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
               .encoder(ActionStatePacket::encode).decoder(ActionStatePacket::decode)
               .consumer(mainThread(ActionStatePacket::handle)).add();
        CHANNEL.messageBuilder(ActionChargePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
               .encoder(ActionChargePacket::encode).decoder(ActionChargePacket::decode)
               .consumer(mainThread(ActionChargePacket::handle)).add();
        CHANNEL.messageBuilder(WallJumpChargePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
               .encoder(WallJumpChargePacket::encode).decoder(WallJumpChargePacket::decode)
               .consumer(mainThread(WallJumpChargePacket::handle)).add();
        CHANNEL.messageBuilder(BackendSyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
               .encoder(BackendSyncPacket::encode).decoder(BackendSyncPacket::decode)
               .consumer(mainThread(BackendSyncPacket::handle)).add();
        CHANNEL.messageBuilder(InternalStaminaPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
               .encoder(InternalStaminaPacket::encode).decoder(InternalStaminaPacket::decode)
               .consumer(mainThread(InternalStaminaPacket::handle)).add();
    }

    /**
     * Runs {@code handler} on the game thread. Forge 40's message builder has no consumerMainThread: packets arrive on
     * the network thread.
     */
    private static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> mainThread(BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        return (packet, context) -> {
            context.get().enqueueWork(() -> handler.accept(packet, context));
            context.get().setPacketHandled(true);
        };
    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }

    /** Fake players (machines acting as players) and connections without AoS's channel can't receive packets. */
    public static void sendToPlayer(ServerPlayer player, Object packet) {
        if (player instanceof FakePlayer || player.connection == null || !CHANNEL.isRemotePresent(player.connection.connection)) return;
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}

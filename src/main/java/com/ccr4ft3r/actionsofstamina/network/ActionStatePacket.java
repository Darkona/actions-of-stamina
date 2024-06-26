package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.capability.AosCapabilityProvider;
import com.ccr4ft3r.actionsofstamina.util.ByteFlag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ActionStatePacket {

    private final byte actionFlags;

    public byte getActionFlags() {
        return actionFlags;
    }

    public ActionStatePacket(byte actionFlags) {
        this.actionFlags = actionFlags;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(actionFlags);
    }

    public static ActionStatePacket decode(FriendlyByteBuf buf) {
        return new ActionStatePacket(buf.readByte());
    }

    public static void handle(ActionStatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        final NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            if (player == null) return;
            player.getCapability(AosCapabilityProvider.PLAYER_ACTIONS).ifPresent(a -> {
                ActionsOfStamina.sideLog(player,"Received ActionStatePacket with flags: {}.", Integer.toBinaryString(packet.actionFlags));
                a.processFlags(new ByteFlag(packet.actionFlags));
            });
            context.setPacketHandled(true);
        });
    }
}
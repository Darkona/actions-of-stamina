package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.ccr4ft3r.actionsofstamina.ActionsOfStamina.id;

public record ActionPerformedPacket(byte actionId) implements CustomPacketPayload {

    public static final Type<ActionPerformedPacket> TYPE = new Type<>(id("action_performed"));

    public static final StreamCodec<ByteBuf, ActionPerformedPacket> STREAM_CODEC =
            ByteBufCodecs.BYTE.map(ActionPerformedPacket::new, ActionPerformedPacket::actionId);

    @Override
    public Type<ActionPerformedPacket> type() {
        return TYPE;
    }

    public static void handle(ActionPerformedPacket packet, IPayloadContext context) {
        apply(context.player(), packet.actionId);
    }

    public static void apply(Player player, int actionId) {
        PlayerActions.perform(player, actionId);
    }
}

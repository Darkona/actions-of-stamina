package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import static com.ccr4ft3r.actionsofstamina.ActionsOfStamina.id;

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

package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.stamina.InternalBackend;
import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record InternalStaminaPacket(int stamina, int maxStamina, boolean exhausted) implements CustomPacketPayload {

    public static final Type<InternalStaminaPacket> TYPE = new Type<>(ActionsOfStamina.id("internal_stamina"));

    public static final StreamCodec<ByteBuf, InternalStaminaPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
                VarInt.write(buf, packet.stamina);
                VarInt.write(buf, packet.maxStamina);
                buf.writeBoolean(packet.exhausted);
            },
            buf -> new InternalStaminaPacket(VarInt.read(buf), VarInt.read(buf), buf.readBoolean()));

    @Override
    public Type<InternalStaminaPacket> type() {
        return TYPE;
    }

    public static void handle(InternalStaminaPacket packet, IPayloadContext context) {
        InternalBackend.data(context.player()).receive(packet.stamina, packet.maxStamina, packet.exhausted);
    }
}

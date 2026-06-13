package com.ccr4ft3r.actionsofstamina.stamina.internal;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Server → owning client: the internal stamina bar, sent only when it visibly changed. */
public record InternalStaminaPacket(int stamina, int maxStamina, boolean exhausted) implements CustomPacketPayload {

    public static final Type<InternalStaminaPacket> TYPE = new Type<>(ActionsOfStamina.id("internal_stamina"));

    /** Written by hand: it goes out whenever the bar moves a sync step, and composite codecs box every int. */
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

    /** Runs on the client main thread; {@code context.player()} is the local player. */
    public static void handle(InternalStaminaPacket packet, IPayloadContext context) {
        InternalStamina s = InternalBackend.data(context.player());
        s.stamina = packet.stamina;
        s.maxStamina = packet.maxStamina;
        s.exhausted = packet.exhausted;
    }
}

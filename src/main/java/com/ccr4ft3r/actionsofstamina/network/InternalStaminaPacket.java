package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.content.stamina.client.InternalStaminaHud;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public record InternalStaminaPacket(int stamina, int maxStamina, boolean exhausted, int regenDelay) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(stamina);
        buf.writeVarInt(maxStamina);
        buf.writeBoolean(exhausted);
        buf.writeVarInt(regenDelay);
    }

    public static InternalStaminaPacket decode(FriendlyByteBuf buf) {
        return new InternalStaminaPacket(buf.readVarInt(), buf.readVarInt(), buf.readBoolean(), buf.readVarInt());
    }

    static void handle(InternalStaminaPacket packet, Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> InternalStaminaHud.receive(packet.stamina, packet.maxStamina, packet.exhausted, packet.regenDelay));
    }
}

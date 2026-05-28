package com.ccr4ft3r.actionsofstamina.stamina.internal;

import com.ccr4ft3r.actionsofstamina.client.ClientPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Server → owning client: the internal stamina bar, sent only when it visibly changed. */
public record InternalStaminaPacket(int stamina, int maxStamina, boolean exhausted) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(stamina);
        buf.writeVarInt(maxStamina);
        buf.writeBoolean(exhausted);
    }

    public static InternalStaminaPacket decode(FriendlyByteBuf buf) {
        return new InternalStaminaPacket(buf.readVarInt(), buf.readVarInt(), buf.readBoolean());
    }

    /** Runs on the client main thread; the local player comes from a client-only class. */
    public static void handle(InternalStaminaPacket packet, Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPackets.internalStamina(packet));
    }

    /** Client: applies the packet to the local player's bar. */
    public void applyTo(Player player) {
        InternalStamina s = InternalBackend.data(player);
        s.stamina = stamina;
        s.maxStamina = maxStamina;
        s.exhausted = exhausted;
    }
}

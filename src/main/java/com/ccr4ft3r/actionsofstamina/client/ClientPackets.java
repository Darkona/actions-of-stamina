package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalStaminaPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/** Client only. Handlers of server → client packets that need the local player. */
public final class ClientPackets {

    private ClientPackets() {
    }

    public static void internalStamina(InternalStaminaPacket packet) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) packet.applyTo(player);
    }
}

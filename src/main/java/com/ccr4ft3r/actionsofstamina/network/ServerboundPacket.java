package com.ccr4ft3r.actionsofstamina.network;

import net.minecraft.network.FriendlyByteBuf;

public class ServerboundPacket {

    private final Action action;

    public ServerboundPacket(Action action) {
        this.action = action;
    }

    public ServerboundPacket(FriendlyByteBuf packetBuffer) {
        this.action = packetBuffer.readEnum(Action.class);
    }

    public void encodeOnClientSide(FriendlyByteBuf packetBuffer) {
        packetBuffer.writeEnum(this.action);
    }

    public Action getAction() {
        return this.action;
    }

    public enum Action {
        PLAYER_MOVING,
        PLAYER_STOP_MOVING,
        PLAYER_WALL_JUMP,
        WEAPON_SWING,
    }
}
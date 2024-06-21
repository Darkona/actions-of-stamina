package com.ccr4ft3r.actionsofstamina.network;

import net.minecraft.network.FriendlyByteBuf;

public class ActionStatePacket {

    private final String action;
    private final boolean state;

    public ActionStatePacket(String action, boolean state) {
        this.action = action;
        this.state = state;
    }

    public ActionStatePacket(FriendlyByteBuf packetBuffer) {
        action = packetBuffer.readUtf();
        state = packetBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf packetBuffer) {
        packetBuffer.writeUtf(action);
        packetBuffer.writeBoolean(state);
    }

    public String getAction() {
        return this.action;
    }

    public boolean getState() {
        return this.state;
    }

}
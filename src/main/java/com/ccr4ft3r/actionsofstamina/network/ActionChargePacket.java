package com.ccr4ft3r.actionsofstamina.network;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.ccr4ft3r.actionsofstamina.ActionsOfStamina.id;

/**
 * Client → server: a one-off action decided on the client (the attack key) charged its cost. Client-side spends are
 * only checked or predicted, so the server spends for real through its stamina backend, with the action's own
 * source and regen delay. Used with both backends. It can only ever cost the sender.
 */
public record ActionChargePacket(byte actionId) implements CustomPacketPayload {

    public static final Type<ActionChargePacket> TYPE = new Type<>(id("action_charge"));

    public static final StreamCodec<ByteBuf, ActionChargePacket> STREAM_CODEC =
            ByteBufCodecs.BYTE.map(ActionChargePacket::new, ActionChargePacket::actionId);

    @Override
    public Type<ActionChargePacket> type() {
        return TYPE;
    }

    /** Runs on the server main thread (default handler thread). */
    public static void handle(ActionChargePacket packet, IPayloadContext context) {
        int actionId = packet.actionId;
        if (actionId < 0 || actionId >= Action.COUNT) return;
        Player player = context.player();
        Action action = PlayerActions.get(player).getAction(actionId);
        if (action != null && !PlayerActions.isNotExhaustable(player)) action.charge(player);
    }
}

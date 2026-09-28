package com.ccr4ft3r.actionsofstamina.gametest;

import com.alrex.parcool.api.action.ParCoolActionEvent;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;

/** ParCool calls for {@link CompatTests}; loaded only when ParCool is. */
final class ParcoolTestHooks {

    private ParcoolTestHooks() {
    }

    static boolean dodgeStartCancelled(ServerPlayer player) {
        Parkourability parkourability = Parkourability.get(player);
        return NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStart(player, parkourability.get(ParCoolActions.DODGE))).isCanceled();
    }

    /** What the server posts when the client's start packet for a dodge arrives. */
    static void postDodgeStarted(ServerPlayer player) {
        Parkourability parkourability = Parkourability.get(player);
        NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(player, parkourability.get(ParCoolActions.DODGE)));
    }
}

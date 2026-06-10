package com.ccr4ft3r.actionsofstamina.gametest;

import net.combatroll.api.event.Event;
import net.combatroll.api.event.ServerSideRollEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/** Combat Roll calls for {@link CompatTests}; loaded only when Combat Roll is. */
final class CombatRollTestHooks {

    private CombatRollTestHooks() {
    }

    /** What Combat Roll's server does when a client's roll packet arrives: every start-rolling listener runs. */
    static void startRolling(ServerPlayer player) {
        var proxy = (Event.Proxy<ServerSideRollEvents.PlayerStartRolling>) ServerSideRollEvents.PLAYER_START_ROLLING;
        for (ServerSideRollEvents.PlayerStartRolling handler : proxy.handlers) handler.onPlayerStartedRolling(player, Vec3.ZERO);
    }
}

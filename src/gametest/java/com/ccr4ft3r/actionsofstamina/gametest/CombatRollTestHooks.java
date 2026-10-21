package com.ccr4ft3r.actionsofstamina.gametest;

import net.combat_roll.api.event.Event;
import net.combat_roll.api.event.ServerSideRollEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

final class CombatRollTestHooks {

    private CombatRollTestHooks() {
    }

    static void startRolling(ServerPlayer player) {
        var proxy = (Event.Proxy<ServerSideRollEvents.PlayerStartRolling>) ServerSideRollEvents.PLAYER_START_ROLLING;
        for (ServerSideRollEvents.PlayerStartRolling handler : proxy.handlers) handler.onPlayerStartedRolling(player, Vec3.ZERO);
    }
}

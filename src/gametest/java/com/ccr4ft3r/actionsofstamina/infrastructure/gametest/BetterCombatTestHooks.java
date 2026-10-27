package com.ccr4ft3r.actionsofstamina.infrastructure.gametest;

import net.bettercombat.network.ServerNetwork;

import java.lang.reflect.Method;

final class BetterCombatTestHooks {

    private BetterCombatTestHooks() {
    }

    static boolean serverMixinApplied() {
        for (Method method : ServerNetwork.class.getDeclaredMethods()) {
            if (method.getName().contains("actionsofstamina$chargeSwing")) return true;
        }
        return false;
    }
}

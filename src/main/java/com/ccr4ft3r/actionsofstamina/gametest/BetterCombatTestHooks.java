package com.ccr4ft3r.actionsofstamina.gametest;

import net.bettercombat.network.ServerNetwork;

import java.lang.reflect.Method;

/** Better Combat calls for {@link CompatTests}; loaded only when Better Combat is. */
final class BetterCombatTestHooks {

    private BetterCombatTestHooks() {
    }

    /** Mixin merges the injector handler into the target class, under a name that keeps ours. */
    static boolean serverMixinApplied() {
        for (Method method : ServerNetwork.class.getDeclaredMethods()) {
            if (method.getName().contains("actionsofstamina$chargeSwing")) return true;
        }
        return false;
    }
}

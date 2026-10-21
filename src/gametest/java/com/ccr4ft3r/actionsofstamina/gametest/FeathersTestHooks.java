package com.ccr4ft3r.actionsofstamina.gametest;

import com.darkona.feathersoffatigue.api.FeathersAPI;

import java.util.Set;

final class FeathersTestHooks {

    private FeathersTestHooks() {
    }

    static Set<String> playerActionOwners() {
        return FeathersAPI.getPlayerActionOwners();
    }
}

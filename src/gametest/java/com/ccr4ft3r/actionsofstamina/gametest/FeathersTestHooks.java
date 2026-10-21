package com.ccr4ft3r.actionsofstamina.gametest;

import com.darkona.feathersoffatigue.api.FeathersAPI;

import java.util.Set;

/** Feathers of Fatigue calls for {@link BackendSelectionTests}; loaded only when Feathers of Fatigue is. */
final class FeathersTestHooks {

    private FeathersTestHooks() {
    }

    /** The mods that told Feathers of Fatigue they charge player actions themselves. */
    static Set<String> playerActionOwners() {
        return FeathersAPI.getPlayerActionOwners();
    }
}

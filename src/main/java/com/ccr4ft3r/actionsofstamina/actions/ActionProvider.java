package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;

/** Builds a player's enabled actions, of every registered {@link ActionType}, from the current config (on every level join). */
public final class ActionProvider {

    private ActionProvider() {
    }

    public static void addEnabledActions(PlayerActions a) {
        for (ActionType type : ActionTypes.all()) {
            if (!type.enabled()) continue;
            Action action = type.create();
            if (action.id() != type.index()) {
                ActionsOfStamina.logger.error("Action {} reports slot {} but its type has slot {}: left out", type, action.id(), type.index());
                continue;
            }
            a.addEnabledAction(action);
        }
    }
}

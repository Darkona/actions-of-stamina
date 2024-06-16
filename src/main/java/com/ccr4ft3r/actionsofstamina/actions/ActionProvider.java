package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintAction;

public class ActionProvider {

    public static ActionProvider INSTANCE;

    private ActionProvider() {

    }

    public static ActionProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionProvider();
        }
        return INSTANCE;
    }


    public Action getActionByName(String name) {
        return switch (name) {
            case "attack_action" -> new AttackAction();
            case "sprint_action" -> new SprintAction();
            default -> null;
        };
    }
}

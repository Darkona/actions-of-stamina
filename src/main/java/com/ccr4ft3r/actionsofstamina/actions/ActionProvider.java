package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintingModifier;
import com.ccr4ft3r.actionsofstamina.capability.IActionCapability;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.darkona.feathers.capability.FeathersCapabilities;
import net.minecraft.world.entity.player.Player;

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

    public void addEnabledActions(IActionCapability a, Player player) {

        if (AoSCommonConfig.ATTACKING_ENABLED.get()) {
            a.addEnabledAction(new AttackAction());
        }

        if (AoSCommonConfig.SPRINTING_ENABLED.get()) {
            a.addEnabledAction(new SprintAction());
            player.getCapability(FeathersCapabilities.PLAYER_FEATHERS)
                  .ifPresent(f -> f.addDeltaModifier(new SprintingModifier()));
        }

    }
}

package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.crawl.CrawlAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.crawl.CrawlingModifier;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraModifier;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.jump.JumpAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintingModifier;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
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
            case AttackAction.actionName -> new AttackAction();
            case SprintAction.actionName -> new SprintAction();
            case JumpAction.actionName -> new JumpAction();
            case CrawlAction.actionName -> new CrawlAction();
            case ElytraAction.actionName -> new ElytraAction();
            default -> null;
        };
    }

    public void addEnabledActions(Player player, PlayerActions a) {

        if (AoSCommonConfig.ATTACKING_ENABLED.get()) {
            a.addEnabledAction(new AttackAction());
        }

        if (AoSCommonConfig.SPRINTING_ENABLED.get()) {
            a.addEnabledAction(new SprintAction());
            player.getCapability(FeathersCapabilities.PLAYER_FEATHERS)
                  .ifPresent(f -> f.addDeltaModifier(new SprintingModifier()));
        }

        if (AoSCommonConfig.JUMPING_ENABLED.get()) {
            a.addEnabledAction(new JumpAction());
        }

        if(AoSCommonConfig.CRAWLING_ENABLED.get()){
            a.addEnabledAction(new CrawlAction());
            player.getCapability(FeathersCapabilities.PLAYER_FEATHERS)
                  .ifPresent(f -> f.addDeltaModifier(new CrawlingModifier()));
        }

        if (AoSCommonConfig.FLYING_ENABLED.get()) {
            a.addEnabledAction(new ElytraAction());
            player.getCapability(FeathersCapabilities.PLAYER_FEATHERS)
                  .ifPresent(f -> f.addDeltaModifier(new ElytraModifier()));
        }
    }
}

package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.crawl.CrawlAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.jump.JumpAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.shield.ShieldAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.swim.SwimAction;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParaglideAction;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.darkona.feathers.capability.FeathersCapabilities;
import net.minecraft.nbt.CompoundTag;
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

    public Action getActionByName(String name, CompoundTag tag) {
        return switch (name) {
            case AttackAction.actionName -> new AttackAction(tag);
            case SprintAction.actionName -> new SprintAction(tag);
            case JumpAction.actionName -> new JumpAction(tag);
            case CrawlAction.actionName -> new CrawlAction(tag);
            case ElytraAction.actionName -> new ElytraAction(tag);
            case ShieldAction.actionName -> new ShieldAction(tag);
            case SwimAction.actionName -> new SwimAction(tag);
            case ParaglideAction.actionName -> new ParaglideAction(tag);
            default -> null;
        };
    }

    public void addEnabledActions(Player player, PlayerActions a) {

        var inhibitorEnabled = false;
        if (AoSCommonConfig.ATTACKING_ENABLED.get()) {
            a.addEnabledAction(new AttackAction());
        }

        if (AoSCommonConfig.JUMPING_ENABLED.get()) {
            a.addEnabledAction(new JumpAction());
        }

        if (AoSCommonConfig.SPRINTING_ENABLED.get()) {
            a.addEnabledAction(new SprintAction());
            inhibitorEnabled = AoSCommonConfig.INHIBIT_REGEN_WHEN_SPRINTING.get();
        }

        if (AoSCommonConfig.CRAWLING_ENABLED.get()) {
            a.addEnabledAction(new CrawlAction());
            inhibitorEnabled = AoSCommonConfig.INHIBIT_REGEN_WHEN_CRAWLING.get();
        }

        if (AoSCommonConfig.FLYING_ENABLED.get()) {
            a.addEnabledAction(new ElytraAction());
            inhibitorEnabled = AoSCommonConfig.INHIBIT_REGEN_WHEN_FLYING.get();
        }

        if (AoSCommonConfig.HOLD_SHIELD_ENABLED.get()) {
            a.addEnabledAction(new ShieldAction());
            inhibitorEnabled = AoSCommonConfig.INHIBIT_REGEN_WHEN_HOLDING_SHIELD.get();
        }

        if(AoSCommonConfig.SWIMMING_ENABLED.get()){
            a.addEnabledAction(new SwimAction());
            inhibitorEnabled = AoSCommonConfig.INHIBIT_REGEN_WHEN_SWIMMING.get();
        }

        if(ActionsOfStamina.PARAGLIDER && ParagliderConfig.PARAGLIDING_ENABLED.get()){
            a.addEnabledAction(new ParaglideAction());
            inhibitorEnabled = ParagliderConfig.INHIBIT_REGEN_WHEN_PARAGLIDING.get();
        }

        if(inhibitorEnabled) player.getCapability(FeathersCapabilities.PLAYER_FEATHERS).ifPresent(f -> f.addDeltaModifier(new RegenInhibitorModifier()));
    }
}

package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.building.BuildAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.climb.ClimbAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.crawl.CrawlAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.draw.DrawAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.fish.FishAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.jump.JumpAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.mine.MineAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.riptide.RiptideAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.row.RowAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.shield.ShieldAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.swim.SwimAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.throwing.ThrowAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.till.TillAction;
import com.ccr4ft3r.actionsofstamina.compatibility.create.CrankAction;
import com.ccr4ft3r.actionsofstamina.compatibility.create.CreateCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParaglideAction;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallClingAction;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
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

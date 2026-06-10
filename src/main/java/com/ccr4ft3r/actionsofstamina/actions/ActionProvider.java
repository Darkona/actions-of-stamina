package com.ccr4ft3r.actionsofstamina.actions;

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

/** Builds a player's enabled actions from the current config (on every level join). */
public final class ActionProvider {

    private ActionProvider() {
    }

    public static void addEnabledActions(PlayerActions a) {
        if (AoSServerConfig.ATTACK.enabled()) a.addEnabledAction(new AttackAction());
        if (AoSServerConfig.JUMP.enabled()) a.addEnabledAction(new JumpAction());
        if (AoSServerConfig.SPRINT.enabled()) a.addEnabledAction(new SprintAction());
        if (AoSServerConfig.CRAWL.enabled()) a.addEnabledAction(new CrawlAction());
        if (AoSServerConfig.ELYTRA.enabled()) a.addEnabledAction(new ElytraAction());
        if (AoSServerConfig.SHIELD.enabled()) a.addEnabledAction(new ShieldAction());
        if (AoSServerConfig.SWIM.enabled()) a.addEnabledAction(new SwimAction());
        if (AoSServerConfig.DRAW.enabled()) a.addEnabledAction(new DrawAction());
        if (AoSServerConfig.THROW.enabled()) a.addEnabledAction(new ThrowAction());
        if (AoSServerConfig.MINE.enabled()) a.addEnabledAction(new MineAction());
        if (AoSServerConfig.BUILD.enabled()) a.addEnabledAction(new BuildAction());
        if (AoSServerConfig.CLIMB.enabled()) a.addEnabledAction(new ClimbAction());
        if (AoSServerConfig.ROW.enabled()) a.addEnabledAction(new RowAction());
        if (AoSServerConfig.RIPTIDE.enabled()) a.addEnabledAction(new RiptideAction());
        if (AoSServerConfig.FISH.enabled()) a.addEnabledAction(new FishAction());
        if (AoSServerConfig.TILL.enabled()) a.addEnabledAction(new TillAction());
        if (ParagliderCompat.isActive()) a.addEnabledAction(new ParaglideAction());
        if (WallJumpCompat.isActive() && WallJumpConfig.WALL_CLING.enabled()) a.addEnabledAction(new WallClingAction());
        if (CreateCompat.isActive()) a.addEnabledAction(new CrankAction());
    }
}

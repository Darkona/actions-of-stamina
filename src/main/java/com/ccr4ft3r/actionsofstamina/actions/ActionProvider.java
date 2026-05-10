package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.crawl.CrawlAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.jump.JumpAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.shield.ShieldAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.swim.SwimAction;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlideAction;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersCompat;
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
        if (ParagliderCompat.isActive()) a.addEnabledAction(new ParaglideAction());
        if (WallJumpCompat.isActive() && WallJumpConfig.WALL_CLING.enabled()) a.addEnabledAction(new WallClingAction());
        if (GlidersCompat.isActive()) a.addEnabledAction(new GlideAction());
    }
}

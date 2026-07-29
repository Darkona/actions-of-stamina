package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.brush.BrushAction;
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
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;

import static com.ccr4ft3r.actionsofstamina.ActionsOfStamina.id;

/** Minecraft's own actions, registered in {@link ActionTypes} like any other mod's. */
public final class VanillaActions {

    public static final ActionType ATTACK = ActionTypes.register(id("attack"), AoSServerConfig.ATTACK, AttackAction::new);
    public static final ActionType JUMP = ActionTypes.register(id("jump"), AoSServerConfig.JUMP, JumpAction::new);
    public static final ActionType SPRINT = ActionTypes.register(id("sprint"), AoSServerConfig.SPRINT, SprintAction::new);
    public static final ActionType SWIM = ActionTypes.register(id("swim"), AoSServerConfig.SWIM, SwimAction::new);
    public static final ActionType ELYTRA = ActionTypes.register(id("elytra"), AoSServerConfig.ELYTRA, ElytraAction::new);
    public static final ActionType CRAWL = ActionTypes.register(id("crawl"), AoSServerConfig.CRAWL, CrawlAction::new);
    public static final ActionType SHIELD = ActionTypes.register(id("shield"), AoSServerConfig.SHIELD, ShieldAction::new);
    public static final ActionType DRAW = ActionTypes.register(id("draw"), AoSServerConfig.DRAW, DrawAction::new);
    public static final ActionType THROW = ActionTypes.register(id("throw"), AoSServerConfig.THROW, ThrowAction::new);
    public static final ActionType MINE = ActionTypes.register(id("mine"), AoSServerConfig.MINE, MineAction::new);
    public static final ActionType BUILD = ActionTypes.register(id("build"), AoSServerConfig.BUILD, BuildAction::new);
    public static final ActionType CLIMB = ActionTypes.register(id("climb"), AoSServerConfig.CLIMB, ClimbAction::new);
    public static final ActionType ROW = ActionTypes.register(id("row"), AoSServerConfig.ROW, RowAction::new);
    public static final ActionType RIPTIDE = ActionTypes.register(id("riptide"), AoSServerConfig.RIPTIDE, RiptideAction::new);
    public static final ActionType FISH = ActionTypes.register(id("fish"), AoSServerConfig.FISH, FishAction::new);
    public static final ActionType TILL = ActionTypes.register(id("till"), AoSServerConfig.TILL, TillAction::new);
    public static final ActionType BRUSH = ActionTypes.register(id("brush"), AoSServerConfig.BRUSH, BrushAction::new);

    private VanillaActions() {
    }

    /** Mod construction: registers the types above (their fields are set when this class loads). */
    public static void register() {
    }
}

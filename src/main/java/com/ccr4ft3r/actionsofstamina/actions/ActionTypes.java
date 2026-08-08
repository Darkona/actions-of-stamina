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
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlideAction;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParaglideAction;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallClingAction;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * The registry of action types: Actions of Stamina's own, in the fixed slots of {@link Action}'s constants, then the
 * ones addons {@link #register}. It freezes when loading completes (or when a player first needs its size): addon
 * types are then sorted by id and numbered after the built-in ones, so a client and a server with the same mods agree
 * on every index. Every player's actions live in an array of {@link #count()} slots indexed by
 * {@link ActionType#index()}: the tick reads it without any lookup.
 */
public final class ActionTypes {

    /** Indices travel as a byte in {@code ActionPerformedPacket}. */
    private static final int MAX_TYPES = Byte.MAX_VALUE + 1;

    private static final List<ActionType> BUILT_IN = new ArrayList<>();
    private static final List<ActionType> ADDED = new ArrayList<>();
    private static volatile ActionType[] types;

    static {
        builtIn(Action.ATTACK, AttackAction.SOURCE, AoSServerConfig.ATTACK::enabled, () -> new AttackAction());
        builtIn(Action.SPRINT, SprintAction.SOURCE, AoSServerConfig.SPRINT::enabled, () -> new SprintAction());
        builtIn(Action.JUMP, JumpAction.SOURCE, AoSServerConfig.JUMP::enabled, () -> new JumpAction());
        builtIn(Action.CRAWL, CrawlAction.SOURCE, AoSServerConfig.CRAWL::enabled, () -> new CrawlAction());
        builtIn(Action.ELYTRA, ElytraAction.SOURCE, AoSServerConfig.ELYTRA::enabled, () -> new ElytraAction());
        builtIn(Action.SHIELD, ShieldAction.SOURCE, AoSServerConfig.SHIELD::enabled, () -> new ShieldAction());
        builtIn(Action.SWIM, SwimAction.SOURCE, AoSServerConfig.SWIM::enabled, () -> new SwimAction());
        // Compat actions are built through lambdas only once their mod is known to be there.
        builtIn(Action.PARAGLIDE, ParaglideAction.SOURCE, ParagliderCompat::isActive, () -> new ParaglideAction());
        builtIn(Action.WALL_CLING, WallClingAction.SOURCE, () -> WallJumpCompat.isActive() && WallJumpConfig.WALL_CLING.enabled(), () -> new WallClingAction());
        builtIn(Action.GLIDE, GlideAction.SOURCE, GlidersCompat::isActive, () -> new GlideAction());
        builtIn(Action.DRAW, DrawAction.SOURCE, AoSServerConfig.DRAW::enabled, () -> new DrawAction());
        builtIn(Action.THROW, ThrowAction.SOURCE, AoSServerConfig.THROW::enabled, () -> new ThrowAction());
        builtIn(Action.MINE, MineAction.SOURCE, AoSServerConfig.MINE::enabled, () -> new MineAction());
        builtIn(Action.BUILD, BuildAction.SOURCE, AoSServerConfig.BUILD::enabled, () -> new BuildAction());
        builtIn(Action.CRANK, CrankAction.SOURCE, CreateCompat::isActive, () -> new CrankAction());
        builtIn(Action.CLIMB, ClimbAction.SOURCE, AoSServerConfig.CLIMB::enabled, () -> new ClimbAction());
        builtIn(Action.ROW, RowAction.SOURCE, AoSServerConfig.ROW::enabled, () -> new RowAction());
        builtIn(Action.RIPTIDE, RiptideAction.SOURCE, AoSServerConfig.RIPTIDE::enabled, () -> new RiptideAction());
        builtIn(Action.FISH, FishAction.SOURCE, AoSServerConfig.FISH::enabled, () -> new FishAction());
        builtIn(Action.TILL, TillAction.SOURCE, AoSServerConfig.TILL::enabled, () -> new TillAction());
    }

    private ActionTypes() {
    }

    private static void builtIn(int index, ResourceLocation id, BooleanSupplier enabled, Supplier<? extends Action> factory) {
        if (index != BUILT_IN.size()) throw new IllegalStateException("Built-in action " + id + " out of its slot " + index);
        ActionType type = new ActionType(id, enabled, factory);
        type.index = index;
        BUILT_IN.add(type);
    }

    /**
     * Registers an addon's action type; call it while mods are constructed or during common setup. {@code id} is also
     * the stamina source the action spends under, and must be unique. {@code enabled} usually reads the action's own
     * config ({@code ActionCostConfig::enabled}); {@code factory} builds the action for one player, whenever the player
     * joins a level, and the action's {@link Action#id()} must return the type's {@link ActionType#index()}.
     *
     * @throws IllegalStateException once the registry froze, or when the id is taken
     */
    public static synchronized ActionType register(ResourceLocation id, BooleanSupplier enabled, Supplier<? extends Action> factory) {
        if (types != null) throw new IllegalStateException("Action type " + id + " registered after the action registry froze");
        if (find(BUILT_IN, id) != null || find(ADDED, id) != null) throw new IllegalStateException("Action type " + id + " registered twice");
        if (BUILT_IN.size() + ADDED.size() >= MAX_TYPES) throw new IllegalStateException("Too many action types: " + id);
        ActionType type = new ActionType(id, enabled, factory);
        ADDED.add(type);
        return type;
    }

    /** Numbers the addon types, sorted by id, after the built-in ones; later registrations fail. Idempotent. */
    public static synchronized void freeze() {
        if (types != null) return;
        List<ActionType> all = new ArrayList<>(BUILT_IN);
        List<ActionType> added = new ArrayList<>(ADDED);
        added.sort(Comparator.comparing(ActionType::id));
        for (ActionType type : added) {
            type.index = all.size();
            all.add(type);
        }
        types = all.toArray(new ActionType[0]);
        if (!added.isEmpty()) ActionsOfStamina.logger.info("Action types added by other mods: {}", added);
    }

    /** Every type, indexed by {@link ActionType#index()}; freezes the registry. Do not modify. */
    public static ActionType[] all() {
        ActionType[] frozen = types;
        if (frozen != null) return frozen;
        freeze();
        return types;
    }

    /** How many slots each player's action array has. */
    public static int count() {
        return all().length;
    }

    @Nullable
    public static ActionType byId(ResourceLocation id) {
        for (ActionType type : all()) {
            if (type.id().equals(id)) return type;
        }
        return null;
    }

    @Nullable
    private static ActionType find(List<ActionType> list, ResourceLocation id) {
        for (ActionType type : list) {
            if (type.id().equals(id)) return type;
        }
        return null;
    }
}

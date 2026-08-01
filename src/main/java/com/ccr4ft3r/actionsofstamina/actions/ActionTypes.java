package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * The registry of action types: every action, Actions of Stamina's own ({@link VanillaActions} and each compat's) and
 * other mods', is registered here the same way. It freezes when loading completes (or when a player first needs its
 * size): the types are then sorted by id and numbered, so a client and a server with the same mods agree on every
 * index whatever order the mods registered in. Every player's actions live in an array of {@link #count()} slots
 * indexed by {@link ActionType#index()}: the tick reads it without any lookup.
 */
public final class ActionTypes {

    /** Indices travel as a byte in {@code ActionPerformedPacket}. */
    private static final int MAX_TYPES = Byte.MAX_VALUE + 1;

    private static final List<ActionType> REGISTERED = new ArrayList<>();
    private static volatile ActionType[] types;

    private ActionTypes() {
    }

    /**
     * Registers an action type whose players get it while its config section is enabled. See
     * {@link #register(ResourceLocation, ActionCostConfig, BooleanSupplier, Function)}.
     */
    public static ActionType register(ResourceLocation id, ActionCostConfig config, Function<ActionType, ? extends Action> factory) {
        return register(id, config, config::enabled, factory);
    }

    /**
     * Registers an action type; call it while mods are constructed or during common setup. {@code id} is also the
     * stamina source the action spends under, and must be unique. {@code config} is the action's cost section;
     * {@code enabled} decides whether players get the action (usually the section's switch, plus whether the mod the
     * action belongs to is there); {@code factory} builds the action for one player, whenever the player joins a level.
     *
     * @throws IllegalStateException once the registry froze, or when the id is taken
     */
    public static synchronized ActionType register(ResourceLocation id, ActionCostConfig config, BooleanSupplier enabled,
                                                   Function<ActionType, ? extends Action> factory) {
        if (types != null) throw new IllegalStateException("Action type " + id + " registered after the action registry froze");
        if (find(id) != null) throw new IllegalStateException("Action type " + id + " registered twice");
        if (REGISTERED.size() >= MAX_TYPES) throw new IllegalStateException("Too many action types: " + id);
        ActionType type = new ActionType(id, config, enabled, factory);
        REGISTERED.add(type);
        return type;
    }

    /** Numbers the types, sorted by id; later registrations fail. Idempotent. */
    public static synchronized void freeze() {
        if (types != null) return;
        List<ActionType> sorted = new ArrayList<>(REGISTERED);
        sorted.sort(Comparator.comparing(ActionType::id));
        for (int i = 0, n = sorted.size(); i < n; i++) sorted.get(i).index = i;
        types = sorted.toArray(new ActionType[0]);
        ActionsOfStamina.logger.debug("Action types: {}", sorted);
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
    private static ActionType find(ResourceLocation id) {
        for (ActionType type : REGISTERED) {
            if (type.id().equals(id)) return type;
        }
        return null;
    }
}

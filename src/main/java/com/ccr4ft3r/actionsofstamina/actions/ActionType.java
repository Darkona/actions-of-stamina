package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * A kind of action, as registered in {@link ActionTypes}: its id, its config section, whether players get it, and how
 * to build one for a player. The id is also the stamina source the action spends under. Its {@link #index()}, the slot
 * in every player's action array, is assigned when the registry freezes.
 */
public final class ActionType {

    private final ResourceLocation id;
    private final ActionCostConfig config;
    private final BooleanSupplier enabled;
    private final Function<ActionType, ? extends Action> factory;
    int index = -1;

    ActionType(ResourceLocation id, ActionCostConfig config, BooleanSupplier enabled, Function<ActionType, ? extends Action> factory) {
        this.id = id;
        this.config = config;
        this.enabled = enabled;
        this.factory = factory;
    }

    public ResourceLocation id() {
        return id;
    }

    /** The config section the action reads its costs from. */
    public ActionCostConfig config() {
        return config;
    }

    /** Slot in the players' action arrays, what {@link Action#id()} returns; only known once the registry froze. */
    public int index() {
        int slot = index;
        if (slot < 0) throw new IllegalStateException("Action type " + id + " has no index before the action registry freezes");
        return slot;
    }

    /** Whether players get this action now (read on every level join, so a config reload applies on the next one). */
    public boolean enabled() {
        return enabled.getAsBoolean();
    }

    Action create() {
        return factory.apply(this);
    }

    @Override
    public String toString() {
        return id.toString();
    }
}

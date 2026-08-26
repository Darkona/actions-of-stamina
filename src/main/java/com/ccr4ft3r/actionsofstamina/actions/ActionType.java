package com.ccr4ft3r.actionsofstamina.actions;

import net.minecraft.resources.ResourceLocation;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * A kind of action, as registered in {@link ActionTypes}: its id, whether the config turns it on, and how to build one
 * for a player. The id is also the stamina source the action spends under. Its {@link #index()}, the slot in every
 * player's action array, is assigned when the registry freezes.
 */
public final class ActionType {

    private final ResourceLocation id;
    private final BooleanSupplier enabled;
    private final Supplier<? extends Action> factory;
    int index = -1;

    ActionType(ResourceLocation id, BooleanSupplier enabled, Supplier<? extends Action> factory) {
        this.id = id;
        this.enabled = enabled;
        this.factory = factory;
    }

    public ResourceLocation id() {
        return id;
    }

    /** Slot in the players' action arrays, what {@link Action#id()} returns; only known once the registry froze. */
    public int index() {
        if (index < 0) throw new IllegalStateException("Action type " + id + " has no index before the action registry freezes");
        return index;
    }

    /** Whether players get this action now (read on every level join, so a config reload applies on the next one). */
    public boolean enabled() {
        return enabled.getAsBoolean();
    }

    Action create() {
        return factory.get();
    }

    @Override
    public String toString() {
        return id.toString();
    }
}

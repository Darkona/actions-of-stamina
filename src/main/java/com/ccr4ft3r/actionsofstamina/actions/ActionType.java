package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

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

    public ActionCostConfig config() {
        return config;
    }

    public int index() {
        int slot = index;
        if (slot < 0) throw new IllegalStateException("Action type " + id + " has no index before the action registry freezes");
        return slot;
    }

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

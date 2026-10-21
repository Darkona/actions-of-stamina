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

public final class ActionTypes {

    private static final int MAX_TYPES = Byte.MAX_VALUE + 1;

    private static final List<ActionType> REGISTERED = new ArrayList<>();
    private static volatile ActionType[] types;

    private ActionTypes() {
    }

    public static ActionType register(ResourceLocation id, ActionCostConfig config, Function<ActionType, ? extends Action> factory) {
        return register(id, config, config::enabled, factory);
    }

    public static synchronized ActionType register(ResourceLocation id, ActionCostConfig config, BooleanSupplier enabled,
                                                   Function<ActionType, ? extends Action> factory) {
        if (types != null) throw new IllegalStateException("Action type " + id + " registered after the action registry froze");
        if (find(id) != null) throw new IllegalStateException("Action type " + id + " registered twice");
        if (REGISTERED.size() >= MAX_TYPES) throw new IllegalStateException("Too many action types: " + id);
        ActionType type = new ActionType(id, config, enabled, factory);
        REGISTERED.add(type);
        return type;
    }

    public static synchronized void freeze() {
        if (types != null) return;
        List<ActionType> sorted = new ArrayList<>(REGISTERED);
        sorted.sort(Comparator.comparing(ActionType::id));
        for (int i = 0, n = sorted.size(); i < n; i++) sorted.get(i).index = i;
        types = sorted.toArray(new ActionType[0]);
        ActionsOfStamina.logger.debug("Action types: {}", sorted);
    }

    public static ActionType[] all() {
        ActionType[] frozen = types;
        if (frozen != null) return frozen;
        freeze();
        return types;
    }

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

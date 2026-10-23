package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import dev.darkona.jdlib.api.compat.Bridges;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

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

    private static final int MAX_TYPES = Byte.MAX_VALUE + 1;

    private static final Map<ResourceLocation, ActionType> BY_ID = new LinkedHashMap<>();
    private static volatile ActionType[] frozen;

    public static ActionType register(ResourceLocation id, ActionCostConfig config) {
        return register(id, config, Action::new);
    }

    public static ActionType register(ResourceLocation id, ActionCostConfig config, Function<ActionType, ? extends Action> factory) {
        return register(id, config, config::enabled, factory);
    }

    public static ActionType register(ResourceLocation id, String requiredModId, ActionCostConfig config) {
        return register(id, requiredModId, config, Action::new);
    }

    public static ActionType register(ResourceLocation id, String requiredModId, ActionCostConfig config,
                                      Function<ActionType, ? extends Action> factory) {
        boolean loaded = Bridges.isLoaded(requiredModId);
        return register(id, config, () -> loaded && config.enabled(), factory);
    }

    public static ActionType register(ResourceLocation id, String requiredModId, BooleanSupplier modSwitch, ActionCostConfig config) {
        boolean loaded = Bridges.isLoaded(requiredModId);
        return register(id, config, () -> loaded && modSwitch.getAsBoolean() && config.enabled(), Action::new);
    }

    public static synchronized ActionType register(ResourceLocation id, ActionCostConfig config, BooleanSupplier enabled,
                                                   Function<ActionType, ? extends Action> factory) {
        if (frozen != null) throw new IllegalStateException("Action type " + id + " registered after the action registry froze");
        if (BY_ID.containsKey(id)) throw new IllegalStateException("Action type " + id + " registered twice");
        if (BY_ID.size() >= MAX_TYPES) throw new IllegalStateException("Too many action types: " + id);
        ActionType type = new ActionType(id, config, enabled, factory);
        BY_ID.put(id, type);
        return type;
    }

    public static synchronized void freeze() {
        if (frozen != null) return;
        List<ActionType> sorted = new ArrayList<>(BY_ID.values());
        sorted.sort(Comparator.comparing(ActionType::id));
        BY_ID.clear();
        for (int i = 0, n = sorted.size(); i < n; i++) {
            ActionType type = sorted.get(i);
            type.index = i;
            BY_ID.put(type.id, type);
        }
        frozen = sorted.toArray(new ActionType[0]);
        ActionsOfStamina.logger.debug("Action types: {}", sorted);
    }

    public static ActionType[] all() {
        ActionType[] types = frozen;
        if (types == null) throw new IllegalStateException("The action registry has not frozen yet");
        return types;
    }

    public static int count() {
        return all().length;
    }

    @Nullable
    public static ActionType byId(ResourceLocation id) {
        all();
        return BY_ID.get(id);
    }
}

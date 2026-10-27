package com.ccr4ft3r.actionsofstamina.stamina;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend.BackendMode;
import dev.darkona.jdlib.api.compat.Bridges;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class StaminaBackends {

    public static final String FEATHERS_MOD_ID = "feathers_of_fatigue";
    public static final boolean FEATHERS_LOADED = Bridges.isLoaded(FEATHERS_MOD_ID);

    private static volatile StaminaBackend server;
    private static volatile StaminaBackend client;
    private static StaminaBackend feathers;

    private StaminaBackends() {
    }

    public static StaminaBackend of(Player player) {
        return player.level().isClientSide() ? client() : server();
    }

    public static StaminaBackend server() {
        StaminaBackend backend = server;
        return backend != null ? backend : (server = computeServer());
    }

    public static StaminaBackend client() {
        StaminaBackend backend = client;
        return backend != null ? backend : server();
    }

    public static void takeOverPlayerActions() {
        if (FEATHERS_LOADED) FeathersBackend.takeOverPlayerActions();
    }

    public static StaminaBackend.Kind resolve(BackendMode mode, boolean feathersLoaded) {
        return switch (mode) {
            case AUTO, FEATHERS -> feathersLoaded ? StaminaBackend.Kind.FEATHERS : StaminaBackend.Kind.INTERNAL;
            case INTERNAL -> StaminaBackend.Kind.INTERNAL;
        };
    }

    public static StaminaBackend byKind(StaminaBackend.Kind kind) {
        if (kind == StaminaBackend.Kind.FEATHERS && FEATHERS_LOADED) return feathers();
        return InternalBackend.INSTANCE;
    }

    private static synchronized StaminaBackend feathers() {
        if (feathers == null) feathers = FeathersBackend.create();
        return feathers;
    }

    private static StaminaBackend computeServer() {
        return byKind(resolve(configuredMode(), FEATHERS_LOADED));
    }

    private static BackendMode configuredMode() {
        return ActionsConfig.SPEC.isLoaded() ? ActionsConfig.BACKEND.get() : BackendMode.AUTO;
    }

    public static void onServerStarting() {
        if (configuredMode() == BackendMode.FEATHERS && !FEATHERS_LOADED) {
            ActionsOfStamina.logger.warn("backend = feathers but Feathers of Fatigue isn't installed: using the internal stamina");
        }
        server = computeServer();
        ActionsOfStamina.logger.info("Stamina backend: {}", server.kind());
    }

    public static void setClient(StaminaBackend.Kind kind) {
        if (kind == StaminaBackend.Kind.FEATHERS && !FEATHERS_LOADED) {
            ActionsOfStamina.logger.warn("The server uses Feathers of Fatigue, which isn't installed here: using the internal stamina");
        }
        client = byKind(kind);
    }

    public static void clearClient() {
        client = null;
        if (ServerLifecycleHooks.getCurrentServer() == null) server = null;
    }

}

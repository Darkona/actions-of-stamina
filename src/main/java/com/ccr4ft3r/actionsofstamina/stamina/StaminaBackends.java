package com.ccr4ft3r.actionsofstamina.stamina;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalBackend;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Chooses the {@link StaminaBackend}. The server resolves the {@code backend} config when it starts and tells each
 * client which one it uses (so a client with Green Feathers installed still follows a server running the internal
 * stamina); until a client hears from a server it uses its own config.
 * <p>
 * {@link FeathersBackend} is only ever touched when Green Feathers is loaded, so its classes never load without it.
 */
public final class StaminaBackends {

    public static final String FEATHERS_MOD_ID = "greenfeathers";
    public static final boolean FEATHERS_LOADED = ModList.get().isLoaded(FEATHERS_MOD_ID);

    private static volatile StaminaBackend server;
    private static volatile StaminaBackend client;
    private static StaminaBackend feathers;

    private StaminaBackends() {
    }

    /** The backend for this side of {@code player}. Cheap: no lookup beyond a side check. */
    public static StaminaBackend of(Player player) {
        return player.level.isClientSide() ? client() : server();
    }

    public static StaminaBackend server() {
        StaminaBackend backend = server;
        return backend != null ? backend : (server = byKind(resolve(configuredMode(), FEATHERS_LOADED)));
    }

    public static StaminaBackend client() {
        StaminaBackend backend = client;
        return backend != null ? backend : server();
    }

    /** Pure decision, kept separate for tests. A missing Green Feathers always means the internal stamina. */
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

    private static BackendMode configuredMode() {
        return AoSServerConfig.SPEC.isLoaded() ? AoSServerConfig.BACKEND.get() : BackendMode.AUTO;
    }

    /** Server starting: re-read the config (it may have changed since the last world). */
    public static void onServerStarting() {
        BackendMode mode = configuredMode();
        if (mode == BackendMode.FEATHERS && !FEATHERS_LOADED) {
            ActionsOfStamina.logger.warn("backend = feathers but Green Feathers isn't installed: using the internal stamina");
        }
        server = byKind(resolve(mode, FEATHERS_LOADED));
        ActionsOfStamina.logger.info("Stamina backend: {}", server.kind());
    }

    /** Client: the server told us which backend it uses. */
    public static void setClient(StaminaBackend.Kind kind) {
        if (kind == StaminaBackend.Kind.FEATHERS && !FEATHERS_LOADED) {
            ActionsOfStamina.logger.warn("The server uses Green Feathers, which isn't installed here: using the internal stamina");
        }
        client = byKind(kind);
    }

    /**
     * Client left the server. Without an integrated server still running, the server backend this client resolved for
     * itself goes too: it came from the last server's synced config, and the next server's may differ.
     */
    public static void clearClient() {
        client = null;
        if (ServerLifecycleHooks.getCurrentServer() == null) server = null;
    }
}

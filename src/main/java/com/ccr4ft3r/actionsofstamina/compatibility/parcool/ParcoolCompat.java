package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import dev.darkona.jdlib.api.compat.Bridges;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class ParcoolCompat {

    public static final String MOD_ID = "parcool";
    public static final boolean LOADED = Bridges.isLoaded(MOD_ID);
    public static final ResourceLocation STAMINA_TYPE = ActionsOfStamina.id("stamina");
    public static final ResourceLocation PARCOOL_STAMINA_TYPE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "parcool");

    private static final ActionType[] TYPES;

    static {
        ParcoolConfig.Entry[] entries = ParcoolConfig.entries();
        TYPES = new ActionType[entries.length];
        for (int i = 0; i < entries.length; i++) {
            ActionCostConfig costs = entries[i].costs();
            TYPES[i] = ActionType.register(ActionsOfStamina.id("parcool/" + entries[i].name()), costs,
                    () -> isActive() && costs.enabled() && costs.costsAnything(), Action::new);
        }
    }

    private ParcoolCompat() {
    }

    public static void registerActions() {
    }

    @Nullable
    static ActionType typeOf(String name) {
        ParcoolConfig.Entry[] entries = ParcoolConfig.entries();
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].name().equals(name)) return TYPES[i];
        }
        return null;
    }

    public static boolean isActive() {
        return LOADED && AoSServerConfig.SPEC.isLoaded() && ParcoolConfig.ENABLED.getAsBoolean();
    }

    public static void registerStaminaType() {
        if (LOADED) ParcoolStaminaType.register();
    }

    public static ResourceLocation effectiveStaminaType(ResourceLocation configured) {
        return PARCOOL_STAMINA_TYPE.equals(configured) && AoSServerConfig.SPEC.isLoaded() && isActive() ? STAMINA_TYPE : configured;
    }

    public static void init() {
        if (LOADED) ParcoolBridge.register();
    }

    public static boolean ownsSprint(Player player) {
        return isActive() && ParcoolBridge.isFastRunning(player);
    }

    public static boolean ownsSwim(Player player) {
        return isActive() && ParcoolBridge.isFastSwimming(player);
    }

    public static boolean ownsCrawl(Player player) {
        return isActive() && ParcoolBridge.isCrawling(player);
    }
}

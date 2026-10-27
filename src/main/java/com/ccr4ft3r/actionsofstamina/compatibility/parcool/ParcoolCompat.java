package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import dev.darkona.jdlib.api.compat.Bridges;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class ParcoolCompat {

    public static final String MOD_ID = "parcool";
    public static final boolean LOADED = Bridges.isLoaded(MOD_ID);
    public static final ResourceLocation STAMINA_TYPE = ActionsOfStamina.id("stamina");
    public static final ResourceLocation PARCOOL_STAMINA_TYPE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "parcool");

    private static final Map<String, ActionType> TYPES = new HashMap<>();

    static {
        for (ParcoolConfig.Entry entry : ParcoolConfig.entries()) {
            ActionCostConfig costs = entry.costs();
            TYPES.put(entry.name(), ActionType.register(ActionsOfStamina.id("parcool/" + entry.name()), MOD_ID,
                    () -> modSwitchOn() && costs.costsAnything(), costs));
        }
    }

    private ParcoolCompat() {
    }

    @Nullable
    static ActionType typeOf(String name) {
        return TYPES.get(name);
    }

    private static boolean modSwitchOn() {
        return AoSServerConfig.SPEC.isLoaded() && ParcoolConfig.ENABLED.getAsBoolean();
    }

    public static boolean isActive() {
        return LOADED && modSwitchOn();
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

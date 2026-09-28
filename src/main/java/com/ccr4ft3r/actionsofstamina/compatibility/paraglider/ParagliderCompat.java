package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

/**
 * Paragliders compatibility, safe to load without it: calls into the mod go through {@link ParagliderBridge}. The
 * stamina side lives in {@link ParagliderStaminaPlugin}, which only Paragliders' own plugin scan ever loads.
 */
public final class ParagliderCompat {

    public static final String MOD_ID = "paraglider";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);

    private ParagliderCompat() {
    }

    public static boolean isActive() {
        return LOADED && ParagliderConfig.PARAGLIDE.enabled();
    }

    public static boolean isParagliding(Player player) {
        return isActive() && ParagliderBridge.isParagliding(player);
    }
}

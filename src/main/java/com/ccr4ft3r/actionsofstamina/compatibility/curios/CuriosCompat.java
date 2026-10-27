package com.ccr4ft3r.actionsofstamina.compatibility.curios;

import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
import net.minecraft.world.entity.player.Player;
import dev.darkona.jdlib.api.compat.Bridges;

public final class CuriosCompat {

    public static final String MOD_ID = "curios";
    public static final boolean LOADED = Bridges.isLoaded(MOD_ID);
    public static final int CLIENT_REFRESH_INTERVAL = 20;

    private CuriosCompat() {
    }

    public static void init() {
        if (LOADED) CuriosBridge.register();
    }

    public static void refreshWings(Player player) {
        PlayerActions.get(player).setCurioWings(wearsStaminaWings(player));
    }

    public static boolean wearsStaminaWings(Player player) {
        return LOADED && CuriosBridge.wearsStaminaWings(player);
    }
}

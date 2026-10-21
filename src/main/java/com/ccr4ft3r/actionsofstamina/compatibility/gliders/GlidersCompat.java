package com.ccr4ft3r.actionsofstamina.compatibility.gliders;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import dev.darkona.jdlib.api.compat.Bridges;

public final class GlidersCompat {

    public static final String MOD_ID = "vc_gliders";
    public static final boolean LOADED = Bridges.isLoaded(MOD_ID);

    private GlidersCompat() {
    }

    public static final ActionType GLIDE = ActionTypes.register(ActionsOfStamina.id("gliders/glide"), GlidersConfig.GLIDE,
            GlidersCompat::isActive, GlideAction::new);

    public static void registerActions() {
    }

    public static boolean isActive() {
        return LOADED && GlidersConfig.GLIDE.enabled();
    }

    public static boolean isGliding(Player player) {
        return !player.onGround() && !player.isInWater() && GlidersBridge.isGliding(player);
    }

    public static boolean canDeploy(ServerPlayer player) {
        return !isActive() || PlayerActions.canPerform(player, GLIDE);
    }

    static void close(ServerPlayer player) {
        GlidersBridge.close(player);
    }
}

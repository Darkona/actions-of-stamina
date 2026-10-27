package com.ccr4ft3r.actionsofstamina.compatibility.gliders;

import com.ccr4ft3r.actionsofstamina.config.compat.GlidersConfig;
import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.PlayerActions;
import dev.darkona.jdlib.api.compat.Bridges;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class GlidersCompat {

    public static final String MOD_ID = "vc_gliders";
    public static final boolean LOADED = Bridges.isLoaded(MOD_ID);

    private GlidersCompat() {
    }

    public static final ActionType GLIDE = ActionType.register(ActionsOfStamina.id("gliders/glide"), MOD_ID, GlidersConfig.GLIDE,
            GlideAction::new).sensed(GlidersCompat::isGliding);

    public static boolean isGliding(Player player) {
        return !player.onGround() && !player.isInWater() && GlidersBridge.isGliding(player);
    }

    public static boolean canDeploy(ServerPlayer player) {
        return !GLIDE.enabled() || PlayerActions.canPerform(player, GLIDE);
    }

    public static class GlideAction extends Action {

        public GlideAction(ActionType type) {
            super(type);
        }

        @Override
        public void notPerformingEffects(Player player, PlayerActions a) {
            if (player instanceof ServerPlayer serverPlayer) GlidersBridge.close(serverPlayer);
        }
    }
}

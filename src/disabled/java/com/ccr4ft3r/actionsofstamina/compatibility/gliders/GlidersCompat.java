package com.ccr4ft3r.actionsofstamina.compatibility.gliders;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

/**
 * Gliders compatibility, safe to load without it: calls into the mod go through {@link GlidersBridge}.
 * <p>
 * Gliders keeps whether a glider is deployed on the glider item, which the server owns and syncs, so each side reads
 * it itself and gliding is a continuous action ({@link GlideAction}). The server refuses to deploy a glider without
 * the stamina to begin (a mixin on Gliders' toggle message), and closes it when the drain can't be paid any more.
 */
public final class GlidersCompat {

    public static final String MOD_ID = "vc_gliders";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);

    private GlidersCompat() {
    }

    public static final ActionType GLIDE = ActionTypes.register(ActionsOfStamina.id("gliders/glide"), GlidersConfig.GLIDE,
            GlidersCompat::isActive, GlideAction::new);

    /** Mod construction: registers the action type above (set when this class loads). */
    public static void registerActions() {
    }

    public static boolean isActive() {
        return LOADED && GlidersConfig.GLIDE.enabled();
    }

    /** Both sides, every tick while the glide action exists: the cheap airborne checks come first. */
    public static boolean isGliding(Player player) {
        return !player.onGround() && !player.isInWater() && GlidersBridge.isGliding(player);
    }

    /** Server, from the mixin: whether {@code player} may open a glider now. */
    public static boolean canDeploy(ServerPlayer player) {
        return !isActive() || PlayerActions.canPerform(player, GLIDE);
    }

    /** Server: the glide can't be paid for, so the player lets go of the glider. */
    static void close(ServerPlayer player) {
        GlidersBridge.close(player);
    }
}

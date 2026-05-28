package com.ccr4ft3r.actionsofstamina.compatibility.gliders;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/** Gliding with a Gliders glider: drains while it lasts, and folds the glider when it can't be paid. */
public class GlideAction extends Action {

    public static final ResourceLocation SOURCE = ActionsOfStamina.id("gliders/glide");

    public GlideAction() {
        super(SOURCE, GlidersConfig.GLIDE);
    }

    @Override
    public String name() {
        return "glide_action";
    }

    @Override
    public int id() {
        return GLIDE;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {
    }

    /** Gliding, but the stamina can't pay for it (to begin, or any more): the player lets go. */
    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        if (player instanceof ServerPlayer serverPlayer) GlidersCompat.close(serverPlayer);
    }
}

package com.ccr4ft3r.actionsofstamina.actions.minecraft.till;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Working a block with a tool: tilling, making a path, stripping a log, scraping or unwaxing copper, charged once every
 * few blocks. Any item whose block transformer (the {@code minecraft:block_transformer} component, or one a data map
 * adds) changes the block counts, so modded tools and blocks count too ({@code BlockTransformerMixin}). A change the
 * player can't afford doesn't happen.
 */
public class TillAction extends Action {

    public TillAction(ActionType type) {
        super(type);
    }

    /**
     * Whether the player may change the block now. The server charges it and refuses it without the stamina; the client
     * only refuses it, so it doesn't show a change the server won't make. Dispensers and other non-players always may.
     */
    public static boolean mayWork(@Nullable Player player) {
        if (player == null || PlayerActions.isExempt(player)) return true;
        Action till = PlayerActions.get(player).getAction(VanillaActions.TILL);
        if (till == null) return true;
        return player.level().isClientSide() ? till.canPerform(player) : till.perform(player);
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}

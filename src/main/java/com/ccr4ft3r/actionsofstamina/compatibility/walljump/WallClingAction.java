package com.ccr4ft3r.actionsofstamina.compatibility.walljump;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/** Wall-Jump TXF's wall cling: drains while the local player holds on to a wall ({@code ActionFlags.WALL_CLINGING}). */
public class WallClingAction extends Action {

    public static final ResourceLocation SOURCE = ActionsOfStamina.id("walljump/wall_cling");

    public WallClingAction() {
        super(SOURCE, WallJumpConfig.WALL_CLING);
    }

    @Override
    public String name() {
        return "wall_cling_action";
    }

    @Override
    public int id() {
        return WALL_CLING;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {
    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
    }
}

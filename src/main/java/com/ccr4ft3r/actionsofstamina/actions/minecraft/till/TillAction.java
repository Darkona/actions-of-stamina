package com.ccr4ft3r.actionsofstamina.actions.minecraft.till;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.world.BlockEvent;

/**
 * Working a block with a tool: tilling, making a path, stripping a log, scraping or unwaxing copper, charged once every
 * few blocks. Found by the tool's action, so modded tools and blocks count too. A change the player can't afford
 * doesn't happen.
 */
public class TillAction extends Action {

    public static final ResourceLocation SOURCE = ActionsOfStamina.id("till");

    public TillAction() {
        super(SOURCE, AoSServerConfig.TILL);
    }

    /** Whether the tool action is one of the block changes charged here (dousing a campfire, trimming or lighting aren't). */
    public static boolean charges(ToolAction action) {
        return action == ToolActions.HOE_TILL || action == ToolActions.SHOVEL_FLATTEN || action == ToolActions.AXE_STRIP
                || action == ToolActions.AXE_SCRAPE || action == ToolActions.AXE_WAX_OFF;
    }

    /**
     * Whether the tool use will really change the block. The event fires before the block's own answer is known (and
     * for every action an axe tries in turn), so, unless another mod already set the result, it is asked for here,
     * simulated. A path also needs air above, which the shovel checks only afterwards.
     */
    public static boolean changesBlock(BlockEvent.BlockToolModificationEvent event) {
        BlockState original = event.getState();
        BlockState result = event.getFinalState();
        if (result == original) result = original.getBlock().getToolModifiedState(original, event.getContext(), event.getToolAction(), true);
        if (result == null || result == original) return false;
        return event.getToolAction() != ToolActions.SHOVEL_FLATTEN || event.getWorld().getBlockState(event.getPos().above()).isAir();
    }

    @Override
    public int id() {
        return TILL;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}

package com.ccr4ft3r.actionsofstamina.actions.minecraft.till;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Working a block with a tool: tilling, making a path, stripping a log, scraping or unwaxing copper, charged once every
 * few blocks. Found by the tool's ability, so modded tools and blocks count too. A change the player can't afford
 * doesn't happen.
 */
public class TillAction extends Action {

    public static final String actionName = "till_action";
    public static final ResourceLocation SOURCE = ActionsOfStamina.id("till");

    public TillAction() {
        super(SOURCE, AoSServerConfig.TILL);
    }

    /** Whether the ability is one of the block changes charged here (dousing a campfire, trimming or lighting aren't). */
    public static boolean charges(ItemAbility ability) {
        return ability == ItemAbilities.HOE_TILL || ability == ItemAbilities.SHOVEL_FLATTEN || ability == ItemAbilities.AXE_STRIP
                || ability == ItemAbilities.AXE_SCRAPE || ability == ItemAbilities.AXE_WAX_OFF;
    }

    /**
     * Whether the tool use will really change the block. The event fires before the block's own answer is known (and
     * for every ability an axe tries in turn), so, unless another mod already set the result, it is asked for here,
     * simulated. A path also needs air above, which the shovel checks only afterwards.
     */
    public static boolean changesBlock(BlockEvent.BlockToolModificationEvent event) {
        BlockState original = event.getState();
        BlockState result = event.getFinalState();
        if (result == original) result = original.getBlock().getToolModifiedState(original, event.getContext(), event.getItemAbility(), true);
        if (result == null || result == original) return false;
        return event.getItemAbility() != ItemAbilities.SHOVEL_FLATTEN || event.getLevel().getBlockState(event.getPos().above()).isAir();
    }

    @Override
    public String name() {
        return actionName;
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

package com.ccr4ft3r.actionsofstamina.actions.minecraft.till;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.level.BlockEvent;

public class TillAction extends Action {

    public TillAction(ActionType type) {
        super(type);
    }

    public static boolean charges(ItemAbility ability) {
        return ability == ItemAbilities.HOE_TILL || ability == ItemAbilities.SHOVEL_FLATTEN || ability == ItemAbilities.AXE_STRIP
                || ability == ItemAbilities.AXE_SCRAPE || ability == ItemAbilities.AXE_WAX_OFF;
    }

    public static boolean changesBlock(BlockEvent.BlockToolModificationEvent event) {
        BlockState original = event.getState();
        BlockState result = event.getFinalState();
        if (result == original) result = original.getBlock().getToolModifiedState(original, event.getContext(), event.getItemAbility(), true);
        if (result == null || result == original) return false;
        return event.getItemAbility() != ItemAbilities.SHOVEL_FLATTEN || event.getLevel().getBlockState(event.getPos().above()).isAir();
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}

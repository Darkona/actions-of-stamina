package com.ccr4ft3r.actionsofstamina.actions.minecraft.shield;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Holding up a shield: any item that blocks attacks (the {@code minecraft:blocks_attacks} component), modded shields included. */
public class ShieldAction extends Action {

    public ShieldAction(ActionType type) {
        super(type);
    }

    /** Whether using the item holds it up to block attacks. */
    public static boolean blocks(ItemStack stack) {
        return stack.has(DataComponents.BLOCKS_ATTACKS);
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    /** Out of stamina with the shield up: it comes down, or blocking would go on for free. */
    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        if (player.isUsingItem() && blocks(player.getUseItem())) player.stopUsingItem();
    }

}

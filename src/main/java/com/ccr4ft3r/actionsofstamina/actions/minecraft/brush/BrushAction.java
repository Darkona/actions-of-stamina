package com.ccr4ft3r.actionsofstamina.actions.minecraft.brush;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public class BrushAction extends Action {

    public BrushAction(ActionType type) {
        super(type);
    }

    public static boolean brushes(ItemStack stack) {
        return stack.getUseAnimation() == UseAnim.BRUSH;
    }

    public static boolean isBrushing(Player player) {
        return player.isUsingItem() && brushes(player.getUseItem());
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        if (isBrushing(player)) player.stopUsingItem();
    }
}

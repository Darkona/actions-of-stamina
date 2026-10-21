package com.ccr4ft3r.actionsofstamina.actions.minecraft.draw;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public class DrawAction extends Action {

    public DrawAction(ActionType type) {
        super(type);
    }

    public static boolean draws(ItemStack stack) {
        UseAnim anim = stack.getUseAnimation();
        return (anim == UseAnim.BOW || anim == UseAnim.SPEAR || anim == UseAnim.CROSSBOW) && !CrossbowItem.isCharged(stack);
    }

    public static boolean isDrawing(Player player) {
        if (!player.isUsingItem()) return false;
        UseAnim anim = player.getUseItem().getUseAnimation();
        return anim == UseAnim.BOW || anim == UseAnim.SPEAR || anim == UseAnim.CROSSBOW;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        if (isDrawing(player)) player.stopUsingItem();
    }
}

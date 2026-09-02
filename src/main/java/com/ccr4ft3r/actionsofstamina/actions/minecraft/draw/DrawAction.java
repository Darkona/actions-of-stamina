package com.ccr4ft3r.actionsofstamina.actions.minecraft.draw;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

/**
 * Drawing a bow, loading a crossbow or aiming a trident: using any item whose use animation is {@code BOW},
 * {@code CROSSBOW} or {@code SPEAR}, modded ones included. Firing a loaded crossbow is free.
 */
public class DrawAction extends Action {

    public DrawAction(ActionType type) {
        super(type);
    }

    /** Whether the item is drawn, loaded or aimed while used (a loaded crossbow fires instead). */
    public static boolean draws(ItemStack stack) {
        UseAnim anim = stack.getUseAnimation();
        return (anim == UseAnim.BOW || anim == UseAnim.SPEAR || anim == UseAnim.CROSSBOW) && !CrossbowItem.isCharged(stack);
    }

    /** Whether the player is drawing right now. */
    public static boolean isDrawing(Player player) {
        if (!player.isUsingItem()) return false;
        UseAnim anim = player.getUseItem().getUseAnimation();
        return anim == UseAnim.BOW || anim == UseAnim.SPEAR || anim == UseAnim.CROSSBOW;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    /** Out of stamina mid-draw: the draw is dropped without a shot (starting again is refused by the right click). */
    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        if (isDrawing(player)) player.stopUsingItem();
    }
}

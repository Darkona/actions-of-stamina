package com.ccr4ft3r.actionsofstamina.actions.minecraft.brush;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

/** Brushing: using any item whose use animation is {@code BRUSH}, modded brushes included. */
public class BrushAction extends Action {

    public static final String actionName = "brush_action";
    public static final ResourceLocation SOURCE = ActionsOfStamina.id("brush");

    public BrushAction() {
        super(SOURCE, AoSServerConfig.BRUSH);
    }

    /** Whether using the item brushes. */
    public static boolean brushes(ItemStack stack) {
        return stack.getUseAnimation() == UseAnim.BRUSH;
    }

    /** Whether the player is brushing right now. */
    public static boolean isBrushing(Player player) {
        return player.isUsingItem() && brushes(player.getUseItem());
    }

    @Override
    public String name() {
        return actionName;
    }

    @Override
    public int id() {
        return BRUSH;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    /** Out of stamina mid-brush: brushing stops (starting again is refused by the right click). */
    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        if (isBrushing(player)) player.stopUsingItem();
    }
}

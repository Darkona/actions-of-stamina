package com.ccr4ft3r.actionsofstamina.actions.minecraft.fish;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolActions;

/**
 * Fishing: casting a rod and reeling it in, each charged on use. Rods are found by their cast ability, so modded rods
 * count too. A cast or reel the player can't afford doesn't happen.
 */
public class FishAction extends Action {

    public static final String actionName = "fish_action";
    public static final ResourceLocation SOURCE = ActionsOfStamina.id("fish");

    public FishAction() {
        super(SOURCE, AoSServerConfig.FISH);
    }

    /** Whether using the item casts or reels in a fishing line. */
    public static boolean isRod(ItemStack stack) {
        return stack.canPerformAction(ToolActions.FISHING_ROD_CAST);
    }

    @Override
    public String name() {
        return actionName;
    }

    @Override
    public int id() {
        return FISH;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}

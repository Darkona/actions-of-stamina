package com.ccr4ft3r.actionsofstamina.actions.minecraft.fish;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;

/**
 * Fishing: casting a rod and reeling it in, each charged on use. Modded rods built on the fishing rod count too. A cast or reel the player can't afford doesn't happen.
 */
public class FishAction extends Action {

    public static final ResourceLocation SOURCE = ActionsOfStamina.id("fish");

    public FishAction() {
        super(SOURCE, AoSServerConfig.FISH);
    }

    /**
     * Whether using the item casts or reels in a fishing line. Forge 40 has no rod-cast tool action: modded rods count
     * when they are fishing rods to the game (built on its fishing rod).
     */
    public static boolean isRod(ItemStack stack) {
        return stack.getItem() instanceof FishingRodItem;
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

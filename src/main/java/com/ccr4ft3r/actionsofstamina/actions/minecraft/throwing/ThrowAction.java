package com.ccr4ft3r.actionsofstamina.actions.minecraft.throwing;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * Throwing an item from {@link #THROWABLES}: snowballs, eggs, ender pearls and splash or lingering potions when used,
 * the trident when released. A throw the player can't afford doesn't happen.
 */
public class ThrowAction extends Action {

    public static final ResourceLocation SOURCE = ActionsOfStamina.id("throw");
    /** Items whose throw costs stamina; the mod's own tag file holds the vanilla ones, datapacks add others. */
    public static final TagKey<Item> THROWABLES = ItemTags.create(ActionsOfStamina.id("throwables"));

    public ThrowAction() {
        super(SOURCE, AoSServerConfig.THROW);
    }

    /** Whether using the item throws it at once (a trident is only thrown when released). */
    public static boolean throwsOnUse(ItemStack stack, Player player) {
        return stack.is(THROWABLES) && stack.getUseDuration(player) <= 0;
    }

    /**
     * Whether releasing the item with {@code remainingTicks} of use left throws it. A trident needs a full aim and no Riptide (a
     * Riptide launch isn't a throw); other throwables used over time throw on every release.
     */
    public static boolean throwsOnRelease(ItemStack stack, Player player, int remainingTicks) {
        if (!stack.is(THROWABLES)) return false;
        if (!(stack.getItem() instanceof TridentItem)) return true;
        return stack.getUseDuration(player) - remainingTicks >= TridentItem.THROW_THRESHOLD_TIME && EnchantmentHelper.getTridentSpinAttackStrength(stack, player) <= 0;
    }

    @Override
    public int id() {
        return THROW;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}

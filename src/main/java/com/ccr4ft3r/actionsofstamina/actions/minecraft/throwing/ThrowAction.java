package com.ccr4ft3r.actionsofstamina.actions.minecraft.throwing;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class ThrowAction extends Action {

    public static final TagKey<Item> THROWABLES = ItemTags.create(ActionsOfStamina.id("throwables"));

    public ThrowAction(ActionType type) {
        super(type);
    }

    public static boolean throwsOnUse(ItemStack stack, Player player) {
        return stack.is(THROWABLES) && stack.getUseDuration(player) <= 0;
    }

    public static boolean throwsOnRelease(ItemStack stack, Player player, int remainingTicks) {
        if (!stack.is(THROWABLES)) return false;
        if (!(stack.getItem() instanceof TridentItem)) return true;
        return stack.getUseDuration(player) - remainingTicks >= TridentItem.THROW_THRESHOLD_TIME && EnchantmentHelper.getTridentSpinAttackStrength(stack, player) <= 0;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}

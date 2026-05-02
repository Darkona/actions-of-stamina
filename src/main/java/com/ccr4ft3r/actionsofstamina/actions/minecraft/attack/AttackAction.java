package com.ccr4ft3r.actionsofstamina.actions.minecraft.attack;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;


public class AttackAction extends Action {

    public static final String actionName = "attack_action";
    public static final ResourceLocation SOURCE = ActionsOfStamina.id("attack");

    public AttackAction() {
        super(SOURCE, AoSCommonConfig.ATTACK);
    }


    @Override
    public String name() {
        return actionName;
    }

    @Override
    public int id() {
        return ATTACK;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }

    /** A weapon is anything whose (component-driven) attribute modifiers add attack damage in the main hand. */
    private static boolean isWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;
        List<ItemAttributeModifiers.Entry> entries = stack.getAttributeModifiers().modifiers();
        for (int i = 0, n = entries.size(); i < n; i++) {
            ItemAttributeModifiers.Entry entry = entries.get(i);
            if (entry.attribute() == Attributes.ATTACK_DAMAGE && entry.slot().test(EquipmentSlot.MAINHAND)) return true;
        }
        return false;
    }

    @Override
    public boolean perform(Player player) {
        charged = false;
        if (PlayerActions.isNotExhaustable(player)) return true;
        // Not a weapon and non-weapons don't cost: the attack goes ahead for free, it isn't cancelled.
        if (!isWeapon(player.getItemInHand(InteractionHand.MAIN_HAND)) && !AoSCommonConfig.ALSO_FOR_NON_WEAPONS.get()) return true;
        return super.perform(player);
    }

}

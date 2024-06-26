package com.ccr4ft3r.actionsofstamina.actions.minecraft.attack;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;


public class AttackAction extends Action {

    public static final String actionName = "attack_action";
    public int cooldown;
    private int timesPerformed;
    private int lastPerformed;
    private int cost;
    private int minCost;
    private int timesPerformedToExhaust;


    public AttackAction() {
        super(AoSCommonConfig.ATTACKING_COST.get(),
                AoSCommonConfig.ATTACKING_MINIMUM_COST.get(),
                AoSCommonConfig.ATTACKING_COOLDOWN.get(),
                0,
                false,
                AoSCommonConfig.ATTACKING_TIMES_PERFORMED_TO_EXHAUST.get()
        );
    }

    public AttackAction(CompoundTag tag) {
        super(tag);
    }

    @Override
    public String name() {
        return actionName;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }

    private boolean isWeapon(ItemStack itemstack) {
        Multimap<Attribute, AttributeModifier> modifiers = itemstack.getItem().getAttributeModifiers(EquipmentSlot.MAINHAND, itemstack);
        Iterator<AttributeModifier> attackDamages = modifiers.get(Attributes.ATTACK_DAMAGE).iterator();
        return attackDamages.hasNext();
    }

    @Override
    public boolean perform(Player player) {
        if (PlayerActions.isNotExhaustable(player)) return true;
        if (!isWeapon(player.getItemInHand(InteractionHand.MAIN_HAND)) && !AoSCommonConfig.ALSO_FOR_NON_WEAPONS.get()) return false;
        return super.perform(player);
    }

}

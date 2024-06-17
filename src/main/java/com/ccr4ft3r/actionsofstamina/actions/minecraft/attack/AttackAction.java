package com.ccr4ft3r.actionsofstamina.actions.minecraft.attack;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.capability.IActionCapability;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.darkona.feathers.api.FeathersAPI;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;


public class AttackAction implements Action {

    public static final String actionName = "attack_action";
    public static final ResourceLocation name = new ResourceLocation(ActionsOfStamina.MOD_ID, actionName);
    public int cooldown;
    private int timesPerformed;
    private int lastPerformed;
    private int cost;
    private int minCost;
    private int timesPerformedToExhaust;

    public AttackAction() {
        this.timesPerformed = 0;
        this.lastPerformed = 0;
        this.cost = AoSCommonConfig.ATTACKING_COST.get();
        this.minCost = AoSCommonConfig.ATTACKING_MINIMUM_COST.get();
        this.timesPerformedToExhaust = AoSCommonConfig.ATTACKING_TIMES_PERFORMED_TO_EXHAUST.get();
        this.cooldown = AoSCommonConfig.ATTACKING_COOLDOWN.get();
    }

    @Override
    public String getName() {
        return actionName;
    }

    @Override
    public boolean canPerform(Player player) {
        return !IActionCapability.cannotBeExhausted(player) && FeathersAPI.canSpendFeathers(player, minCost);
    }

    @Override
    public double getFeathersPerSecond() {
        return 0;
    }

    @Override
    public boolean isRegenInhibitor() {
        return false;
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public int getMinCost() {
        return minCost;
    }

    @Override
    public int getTimesPerformedToExhaust() {
        return timesPerformedToExhaust;
    }

    @Override
    public boolean isPerforming() {
        return false;
    }

    @Override
    public void setPerforming(boolean performing) {

    }

    @Override
    public void beginPerforming(Player player) {

    }

    @Override
    public void finishPerforming(Player player) {

    }

    @Override
    public boolean perform(Player player) {

        ItemStack itemstack = player.getItemInHand(InteractionHand.MAIN_HAND);
        Multimap<Attribute, AttributeModifier> modifiers = itemstack.getItem().getAttributeModifiers(EquipmentSlot.MAINHAND, itemstack);
        Iterator<AttributeModifier> attackDamages = modifiers.get(Attributes.ATTACK_DAMAGE).iterator();

        if (!attackDamages.hasNext() && !AoSCommonConfig.ALSO_FOR_NON_WEAPONS.get()) {
            return false;
        }

        timesPerformed++;
        boolean performed = true;
        if (timesPerformed >= timesPerformedToExhaust) {
            timesPerformed = 0;
            if (AoSCommonConfig.ENABLE_DEBUGGING.get())
                LogUtils.getLogger().info("Spending {} feathers for player '{}'", cost, player.getScoreboardName());
            performed = player.level().isClientSide ? FeathersAPI.canSpendFeathers(player, cost) : FeathersAPI.spendFeathers(player, cost, cooldown);

        }
        if (performed) {
            lastPerformed = player.tickCount;
        }

        return performed;
    }

    @Override
    public int timesPerformed() {
        return timesPerformed;
    }

    @Override
    public int getLastPerformed() {
        return lastPerformed;
    }

    @Override
    public void tick(Player player, IActionCapability capability) {

    }

    @Override
    public CompoundTag saveNBTData() {
        var tag = new CompoundTag();
        tag.putInt("cost", cost);
        tag.putInt("minCost", minCost);
        tag.putInt("timesPerformedToExhaust", timesPerformedToExhaust);
        tag.putInt("timesPerformed", timesPerformed);
        tag.putLong("lastPerformed", lastPerformed);
        return tag;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        cost = nbt.getInt("cost");
        minCost = nbt.getInt("minCost");
        timesPerformedToExhaust = nbt.getInt("timesPerformedToExhaust");
        timesPerformed = nbt.getInt("timesPerformed");
        lastPerformed = nbt.getInt("lastPerformed");
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public int getStaminaCostPerTick() {
        return 0;
    }

    @Override
    public void setActionState(boolean isSprinting) {

    }
}

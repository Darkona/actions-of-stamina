package com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.darkona.feathers.attributes.FeathersAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;


public class SprintAction implements Action {

    public static final ResourceLocation name = new ResourceLocation(ActionsOfStamina.MOD_ID, "sprint_action");
    public static final String SPRINT_MODIFIER_UUID = "022d3d49-baa6-4461-ac77-1681f6e63ec2";
    private static final AttributeModifier sprintModifier = new AttributeModifier(SPRINT_MODIFIER_UUID, 0, AttributeModifier.Operation.ADDITION);

    private final int cost;
    private final int minCost;
    private final int cooldown;
    private boolean performing = false;

    public SprintAction() {
        this.cost = AoSCommonConfig.SPRINTING_COST.get();
        this.minCost = AoSCommonConfig.SPRINTING_MINIMUM_COST.get();
        this.cooldown = AoSCommonConfig.SPRINTING_COOLDOWN.get();
    }

    @Override
    public ResourceLocation getName() {
        return name;
    }

    @Override
    public double getFeathersPerSecond() {
        return 0;
    }

    @Override
    public boolean isRegenInhibitor() {
        return AoSCommonConfig.INHIBIT_REGEN_WHEN_SPRINTING.get();
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
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public int getTimesPerformedToExhaust() {
        return 0;
    }

    @Override
    public boolean wasPerforming() {
        return performing;
    }

    @Override
    public boolean isPerforming() {
        return performing;
    }

    @Override
    public void setPerforming(boolean performing) {
        this.performing = performing;
    }

    @Override
    public void atStart(Player player) {

        if (player.getAttributes().hasAttribute(FeathersAttributes.FEATHERS_PER_SECOND.get())) {
            var attr = player.getAttribute(FeathersAttributes.FEATHERS_PER_SECOND.get());
            if (attr != null) {
                var currentValue = attr.getBaseValue();
                attr.addPermanentModifier(new AttributeModifier(SPRINT_MODIFIER_UUID, -currentValue - 50, AttributeModifier.Operation.ADDITION));
            }
        }

    }

    @Override
    public void atFinish(Player player) {

        if (player.getAttributes().hasAttribute(FeathersAttributes.FEATHERS_PER_SECOND.get())) {
            var attr = player.getAttribute(FeathersAttributes.FEATHERS_PER_SECOND.get());
            if (attr != null) {
                attr.removeModifier(sprintModifier);
            }
        }

    }

    @Override
    public boolean perform(Player player) {
        return false;
    }

    @Override
    public int timesPerformed() {
        return 0;
    }

    @Override
    public long getLastPerformed() {
        return 0;
    }

    @Override
    public void tick() {

    }

    @Override
    public CompoundTag saveNBTData() {
        return null;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {

    }
}

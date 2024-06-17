package com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.capability.IActionCapability;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.darkona.feathers.api.FeathersAPI;
import com.darkona.feathers.capability.FeathersCapabilities;
import com.darkona.feathers.util.Calculations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import static com.ccr4ft3r.actionsofstamina.capability.IActionCapability.cannotBeExhausted;


public class SprintAction implements Action {

    public static final String actionName = "sprint_action";
    public static final ResourceLocation name = new ResourceLocation(ActionsOfStamina.MOD_ID, actionName);


    private final int cost;
    private final int minCost;
    private final int cooldown;
    private final int staminaPerTick;
    private final boolean regenInhibitor;

    private boolean performing = false;
    private boolean dirty = false;
    private boolean actionState = false;

    public SprintAction() {
        this.cost = AoSCommonConfig.SPRINTING_COST.get();
        this.minCost = AoSCommonConfig.SPRINTING_MINIMUM_COST.get();
        this.cooldown = AoSCommonConfig.SPRINTING_COOLDOWN.get();
        this.staminaPerTick = Calculations.calculateStaminaPerTick(AoSCommonConfig.SPRINTING_FEATHERS_PER_SECOND.get());
        this.regenInhibitor = AoSCommonConfig.INHIBIT_REGEN_WHEN_SPRINTING.get();
    }

    @Override
    public String getName() {
        return actionName;
    }

    @Override
    public boolean canPerform(Player player) {
        if (cannotBeExhausted(player)) return true;
        if (performing) {
            return player.getCapability(FeathersCapabilities.PLAYER_FEATHERS).map(f -> f.getAvailableStamina() >= staminaPerTick).orElse(false);
        } else {
            return FeathersAPI.canSpendFeathers(player, minCost);
        }
    }

    @Override
    public double getFeathersPerSecond() {
        return 0;
    }

    @Override
    public boolean isRegenInhibitor() {
        return regenInhibitor;
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
    public int getStaminaCostPerTick() {
        return staminaPerTick;
    }

    @Override
    public void setActionState(boolean state) {
        this.actionState = state;
    }

    @Override
    public int getTimesPerformedToExhaust() {
        return 0;
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
    public boolean perform(Player player) {
        return false;
    }

    @Override
    public int timesPerformed() {
        return 0;
    }

    @Override
    public int getLastPerformed() {
        return 0;
    }


    @Override
    public void tick(Player player, IActionCapability a) {

        if (actionState && !performing && canPerform(player)) {
            beginPerforming(player);
        } else if (!actionState && performing) {
            finishPerforming(player);
        }

    }

    @Override
    public void beginPerforming(Player player) {
        if (regenInhibitor) FeathersAPI.disableCooldown(player);
        performing = true;
        if (AoSCommonConfig.ENABLE_DEBUGGING.get()) {
            ActionsOfStamina.logger.info("{}: Begin sprinting. Sprint performing: {}.", player.level().isClientSide ? "Client" : "Server", performing);
        }
        FeathersAPI.spendFeathers(player, 0, 0);
    }

    @Override
    public void finishPerforming(Player player) {

        FeathersAPI.enableCooldown(player);
        performing = false;
        if (AoSCommonConfig.ENABLE_DEBUGGING.get()) {
            ActionsOfStamina.logger.info("{}: End sprinting. Sprint performing: {}", player.level().isClientSide ? "Client" : "Server", performing);
        }
        FeathersAPI.spendFeathers(player, 0, cooldown);
    }

    @Override
    public CompoundTag saveNBTData() {
        var nbt = new CompoundTag();
        nbt.putBoolean("performing", performing);
        nbt.putBoolean("actionState", actionState);
        return nbt;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        performing = nbt.getBoolean("performing");
        actionState = nbt.getBoolean("actionState");
    }
}

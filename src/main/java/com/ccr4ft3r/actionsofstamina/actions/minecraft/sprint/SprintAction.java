package com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.ccr4ft3r.actionsofstamina.network.ActionStatePacket;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import com.darkona.feathers.api.FeathersAPI;
import com.darkona.feathers.api.StaminaAPI;
import com.darkona.feathers.util.Calculations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

import java.util.Objects;
import java.util.UUID;


public class SprintAction implements Action {

    public static final String actionName = "sprint_action";
    private static final UUID SPEED_MODIFIER_SPRINTING_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");

    private final int cost;
    private final int minCost;
    private final int cooldown;
    private final int staminaPerTick;
    private final boolean regenInhibitor;

    private boolean wasSprinting = false;
    private boolean performing = false;
    private boolean actionState = false;
    private String debugInfo;

    public SprintAction() {
        this.cost = AoSCommonConfig.SPRINTING_COST.get();
        this.minCost = AoSCommonConfig.SPRINTING_MINIMUM_COST.get();
        this.cooldown = AoSCommonConfig.SPRINTING_COOLDOWN.get();
        this.staminaPerTick = Calculations.calculateStaminaPerTick(AoSCommonConfig.SPRINTING_FEATHERS_PER_SECOND.get());
        this.regenInhibitor = AoSCommonConfig.INHIBIT_REGEN_WHEN_SPRINTING.get();
    }

    public boolean wasPerforming() {
        return wasSprinting;
    }

    @Override
    public String getName() {
        return actionName;
    }

    @Override
    public String getDebugString() {
        return debugInfo;
    }

    @Override
    public boolean canPerform(Player player) {
        if (PlayerActions.cannotBeExhausted(player)) return true;
        if (performing) {
            return StaminaAPI.canUseStamina(player, staminaPerTick);
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
    public void tick(Player player, PlayerActions a, TickEvent.Phase phase) {
        boolean second = player.tickCount % 10 == 0;

        if (phase == TickEvent.Phase.END) {

            // if (second) ActionsOfStamina.sideLog(player, "SprintAction tick 1: wasSprinting: {}, ActionState: {}, ", wasSprinting, performing);
            boolean shouldSprint = actionState && StaminaAPI.useStamina(player, staminaPerTick);

            //if (second) ActionsOfStamina.sideLog(player, "SprintAction tick 2: shouldSprint: {}", shouldSprint);

            if (wasPerforming() && !shouldSprint) {
                finishPerforming(player);
            } else if (!wasPerforming() && shouldSprint) {
                beginPerforming(player);
            }

            if (!performing) {
                player.setSprinting(false);
                Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).removeModifier(SPEED_MODIFIER_SPRINTING_UUID);
            }

            debugInfo = String.format("%s: Performing: %s, ActionState: %s, ShouldSprint: %s", actionName, performing, actionState, shouldSprint);
            /*if (second) {
                ActionsOfStamina.sideLog(player, "SprintAction::Tick -> Performing: {}, playerSprinting: {}", performing, player.isSprinting());
            }*/
            wasSprinting = performing;

        }

    }

    @Override
    public void beginPerforming(Player player) {
        ActionsOfStamina.sideLog(player, "CrawlAction::beginPerforming");
        if (regenInhibitor) FeathersAPI.disableCooldown(player);
        performing = true;
        if (player.level().isClientSide) {
            PacketHandler.sendToServer(new ActionStatePacket(actionName, actionState));
        } else {
            FeathersAPI.spendFeathers(player, 0, 0);
        }

    }

    @Override
    public void finishPerforming(Player player) {
        ActionsOfStamina.sideLog(player, "CrawlAction::finishPerforming");
        if (regenInhibitor) FeathersAPI.enableCooldown(player);
        performing = false;

        if (player.level().isClientSide) {
            PacketHandler.sendToServer(new ActionStatePacket(actionName, actionState));
        } else {
            FeathersAPI.spendFeathers(player, 0, cooldown);
        }

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

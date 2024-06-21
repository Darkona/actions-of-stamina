package com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra;

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
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

public class ElytraAction implements Action {

    public static final String actionName = "elytra_action";

    private final int cost;
    private final int minCost;
    private final int cooldown;
    private final int staminaPerTick;
    private final boolean regenInhibitor;
    private final double feathersPerSecond;
    private boolean wasFlying = false;
    private boolean performing = false;
    private boolean actionState = false;
    private String debugInfo;

    public ElytraAction() {
        this.cost = AoSCommonConfig.FLYING_COST.get();
        this.minCost = AoSCommonConfig.FLYING_MINIMUM_COST.get();
        this.cooldown = AoSCommonConfig.FLYING_COOLDOWN.get();
        this.staminaPerTick = Calculations.calculateStaminaPerTick(AoSCommonConfig.FLYING_FEATHERS_PER_SECOND.get());
        this.regenInhibitor = AoSCommonConfig.INHIBIT_REGEN_WHEN_FLYING.get();
        this.feathersPerSecond = AoSCommonConfig.FLYING_FEATHERS_PER_SECOND.get();
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
    public boolean wasPerforming() {
        return wasFlying;
    }

    @Override
    public double getFeathersPerSecond() {
        return feathersPerSecond;
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
    public boolean perform(Player p) {
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
    public void tick(Player player, PlayerActions capability, TickEvent.Phase phase) {


        if (phase == TickEvent.Phase.END) {

            var shouldFly = actionState && StaminaAPI.useStamina(player, staminaPerTick);

            if (wasPerforming() && !shouldFly) {
                finishPerforming(player);
            } else if (!wasPerforming() && shouldFly) {
                beginPerforming(player);
            }

            debugInfo = String.format("%s: Performing: %s, ActionState: %s, ShouldFly: %s", actionName, performing, actionState, shouldFly);
            wasFlying = performing;
        }
    }

    @Override
    public void beginPerforming(Player player) {
        ActionsOfStamina.sideLog(player, "ElytraAction::beginPerforming");
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
        ActionsOfStamina.sideLog(player, "ElytraAction::finishPerforming");
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
}

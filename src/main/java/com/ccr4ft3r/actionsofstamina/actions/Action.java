package com.ccr4ft3r.actionsofstamina.actions;


import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.darkona.feathers.api.FeathersAPI;
import com.darkona.feathers.api.StaminaAPI;
import com.darkona.feathers.util.Calculations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public abstract class Action {

    protected final int cost;
    protected final int minCost;
    protected final int cooldown;
    protected final double featherCostPerSecond;
    protected final int staminaCostPerTick;
    protected final boolean regenInhibitor;
    protected boolean wasPerforming = false;
    protected boolean actionState = false;
    protected boolean prevActionState = false;
    protected int timesPerformed = 0;
    protected int timesPerformedToExhaust = 0;

    protected String debugInfo;

    public abstract String name();

    public Action(int cost, int minCost, int cooldown, double featherCostPerSecond, boolean regenInhibitor, int timesPerformedToExhaust) {
        this.cost = cost;
        this.minCost = minCost;
        this.cooldown = cooldown;
        this.featherCostPerSecond = featherCostPerSecond;
        this.staminaCostPerTick = Calculations.calculateStaminaPerTick(featherCostPerSecond);
        this.regenInhibitor = regenInhibitor;
        this.timesPerformedToExhaust = timesPerformedToExhaust;
    }

    public String debugString() {
        return debugInfo;
    }

    public boolean isInhibitingCooldown() {
        return actionState;
    }

    public boolean canPerform(Player player) {
        return PlayerActions.isNotExhaustable(player) ||
                (wasPerforming && StaminaAPI.canUseStamina(player, staminaCostPerTick)) ||
                (!wasPerforming && FeathersAPI.canSpendFeathers(player, minCost));
    }

    private boolean useStamina(Player player) {
        return StaminaAPI.useStamina(player, staminaCostPerTick);
    }

    private boolean canBeginPerforming(Player player) {
        return FeathersAPI.canSpendFeathers(player, minCost);
    }

    public void tick(Player p, PlayerActions a) {

        boolean performing = wasPerforming;

        if(PlayerActions.isNotExhaustable(p)){
            wasPerforming = actionState;
            return;
        }


        if (actionState) {
            var allowed = useStamina(p);

            if (!wasPerforming && canBeginPerforming(p) && allowed) {
                beginPerforming(p, a);
                performing = true;
            }

            if (wasPerforming && !allowed) {
                finishPerforming(p, a);
                performing = false;
            }

            if (performing) {
                performingEffects(p, a);
            } else {
                notPerformingEffects(p, a);
            }
        } else {

            if (wasPerforming) {
                finishPerforming(p, a);
                performing = false;
            }

        }

        boolean changeDetected = wasPerforming != performing || actionState != prevActionState;

        if (AoSCommonConfig.ENABLE_DEBUGGING.get() && (debugInfo == null || changeDetected))
            debugInfo = String.format("%s: WasPerforming: %s, Performing: %s, ActionState: %s, Allow: %s, Inhibiting regen: %s", name(), wasPerforming, performing, actionState, canPerform(p), a.isInhibitRegen());

        prevActionState = actionState;
        wasPerforming = performing;

    }

    protected abstract void performingEffects(Player p, PlayerActions a);

    protected abstract void notPerformingEffects(Player player, PlayerActions a);

    protected void beginPerforming(Player p, PlayerActions a) {
        ActionsOfStamina.sideLog(p, name() + "::beginPerforming");
        FeathersAPI.spendFeathers(p, cost, cooldown);
    }

    protected void finishPerforming(Player p, PlayerActions a) {
        ActionsOfStamina.sideLog(p, name() + "::finishPerforming");
        FeathersAPI.spendFeathers(p, 0, 0);
    }

    public boolean perform(Player player) {
        if (PlayerActions.isNotExhaustable(player)) return true;
        boolean allow = canPerform(player);
        if (!allow) {
            ActionsOfStamina.log("{}::Allowed = false, cost= {}", name(), cost);
            return false;
        }

        ActionsOfStamina.sideLog(player, name() + "::Perform");
        if (++timesPerformed == timesPerformedToExhaust) {
            timesPerformed = 0;
            allow = FeathersAPI.spendFeathers(player, cost, cooldown);
            ActionsOfStamina.log("{}::Allowed = {}, cost= {}", name(), allow, cost);
            return allow;
        }
        return true;
    }

    public CompoundTag saveNBTData() {
        var nbt = new CompoundTag();
        nbt.putBoolean("performing", wasPerforming);
        nbt.putBoolean("actionState", actionState);
        nbt.putDouble("featherCostPerSecond", featherCostPerSecond);
        nbt.putInt("cost", cost);
        nbt.putInt("minCost", minCost);
        nbt.putInt("cooldown", cooldown);
        nbt.putInt("staminaCostPerTick", staminaCostPerTick);
        nbt.putBoolean("regenInhibitor", regenInhibitor);
        nbt.putInt("timesPerformed", timesPerformed);
        nbt.putInt("timesPerformedToExhaust", timesPerformedToExhaust);
        return nbt;
    }

    public Action(CompoundTag nbt) {
        actionState = nbt.getBoolean("actionState");
        featherCostPerSecond = nbt.getDouble("featherCostPerSecond");
        cost = nbt.getInt("cost");
        minCost = nbt.getInt("minCost");
        cooldown = nbt.getInt("cooldown");
        staminaCostPerTick = nbt.getInt("staminaCostPerTick");
        regenInhibitor = nbt.getBoolean("regenInhibitor");
        timesPerformed = nbt.getInt("timesPerformed");
        timesPerformedToExhaust = nbt.getInt("timesPerformedToExhaust");
        wasPerforming = nbt.getBoolean("performing");
    }

    public boolean isPerforming() {
        return wasPerforming;
    }

    public boolean isRegenInhibitor() {
        return regenInhibitor;
    }

    public void setActionState(boolean state) {
        actionState = state;
    }


}

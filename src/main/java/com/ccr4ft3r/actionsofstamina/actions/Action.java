package com.ccr4ft3r.actionsofstamina.actions;


import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.world.entity.player.Player;

public class Action {

    protected final ActionType type;
    protected final int cost;
    protected final int minCost;
    protected final int finishCost;
    private final int beginCost;
    protected final int cooldown;
    protected final double staminaPerTick;
    protected final int tickCost;
    protected final boolean regenInhibitor;
    protected boolean wasPerforming = false;
    protected boolean actionState = false;
    protected boolean prevActionState = false;
    protected int timesPerformed = 0;
    protected final int timesPerformedToExhaust;
    private boolean blockingRegen;

    protected String debugInfo;

    public Action(ActionType type) {
        ActionCostConfig config = type.config();
        this.type = type;
        this.cost = config.cost();
        this.minCost = config.minStamina();
        this.finishCost = config.finishCost();
        this.cooldown = config.regenDelay();
        this.staminaPerTick = config.perTick();
        this.tickCost = (int) Math.ceil(staminaPerTick);
        this.regenInhibitor = config.blocksRegen();
        this.timesPerformedToExhaust = config.timesPerformedToExhaust();
        this.beginCost = minCost > 0 ? minCost : tickCost > 0 ? tickCost : finishCost;
    }

    public String name() {
        return type.id().toString();
    }

    public ActionType type() {
        return type;
    }

    public final int id() {
        return type.index();
    }

    public String debugString() {
        return debugInfo;
    }

    public boolean canPerform(Player player) {
        return PlayerActions.isExempt(player) || canAfford(player, wasPerforming ? tickCost : beginCost);
    }

    private boolean canAfford(Player player, int stamina) {
        return StaminaBackends.of(player).canSpend(player, type.id(), stamina);
    }

    private boolean drain(Player player, StaminaBackend backend) {
        blockingRegen = regenInhibitor && !backend.keepsRegenWhileActing(player);
        return backend.drain(player, type.id(), drainPerTick(player), blockingRegen);
    }

    protected double drainPerTick(Player player) {
        return staminaPerTick;
    }

    public void tick(Player p, PlayerActions a) {
        if (!actionState && !wasPerforming && !prevActionState && (debugInfo != null || !ActionsOfStamina.debugging())) return;

        boolean performing = wasPerforming;

        if (PlayerActions.isExempt(p)) {
            if (wasPerforming) finishPerforming(p, a);
            if (wasPerforming || prevActionState) cleanUp(p);
            wasPerforming = false;
            prevActionState = false;
            return;
        }

        if (actionState) {
            StaminaBackend backend = StaminaBackends.of(p);
            if (!wasPerforming) {
                if (backend.canSpend(p, type.id(), beginCost) && drain(p, backend)) {
                    beginPerforming(p, a);
                    performing = true;
                }
            } else if (!drain(p, backend)) {
                finishPerforming(p, a);
                performing = false;
            }

            if (performing) {
                performingEffects(p, a);
            } else {
                notPerformingEffects(p, a);
            }
        } else if (wasPerforming) {
            finishPerforming(p, a);
            performing = false;
        } else if (prevActionState) {
            cleanUp(p);
        }

        boolean changeDetected = wasPerforming != performing || actionState != prevActionState;

        if (ActionsOfStamina.debugging() && (debugInfo == null || changeDetected))
            debugInfo = String.format("%s: WasPerforming: %s, Performing: %s, ActionState: %s, Allow: %s, Inhibiting regen: %s", name(), wasPerforming, performing, actionState, canPerform(p), performing && blockingRegen);

        prevActionState = actionState;
        wasPerforming = performing;

    }

    protected void performingEffects(Player p, PlayerActions a) {
    }

    protected void notPerformingEffects(Player player, PlayerActions a) {
    }

    public void cleanUp(Player player) {
    }

    protected void beginPerforming(Player p, PlayerActions a) {
        if (ActionsOfStamina.debugging()) ActionsOfStamina.sideLog(p, "{}::beginPerforming", name());
        StaminaBackend backend = StaminaBackends.of(p);
        if (cost > 0) backend.spend(p, type.id(), cost, cooldown);
        else if (cooldown > 0 && staminaPerTick > 0) backend.blockRegen(p, type.id(), cooldown);
    }

    protected void finishPerforming(Player p, PlayerActions a) {
        if (ActionsOfStamina.debugging()) ActionsOfStamina.sideLog(p, "{}::finishPerforming", name());
        StaminaBackend backend = StaminaBackends.of(p);
        backend.stopDrain(p, type.id());
        if (finishCost > 0 && !PlayerActions.isExempt(p)) backend.spend(p, type.id(), finishCost, cooldown);
        else if (cooldown > 0 && staminaPerTick > 0 && !backend.keepsRegenWhileActing(p)) backend.blockRegen(p, type.id(), cooldown);
    }

    public boolean canBegin(Player player) {
        return PlayerActions.isExempt(player) || canAfford(player, beginCost);
    }

    public boolean drains() {
        return staminaPerTick > 0;
    }

    public boolean canContinue(Player player) {
        return PlayerActions.isExempt(player) || canAfford(player, tickCost);
    }

    public void begin(Player player) {
        beginPerforming(player, PlayerActions.get(player));
    }

    public boolean continueTick(Player player) {
        return drain(player, StaminaBackends.of(player));
    }

    public void end(Player player) {
        finishPerforming(player, PlayerActions.get(player));
    }

    public boolean canPay(Player player, int stamina) {
        return PlayerActions.isExempt(player) || canAfford(player, stamina);
    }

    public boolean pay(Player player, int stamina) {
        return PlayerActions.isExempt(player) || StaminaBackends.of(player).spend(player, type.id(), stamina, cooldown);
    }

    public boolean perform(Player player) {
        if (PlayerActions.isExempt(player)) return true;
        boolean allow = canPerform(player);
        if (!allow) {
            if (ActionsOfStamina.debugging()) ActionsOfStamina.log("{}::Allowed = false, cost= {}", name(), cost);
            return false;
        }

        if (ActionsOfStamina.debugging()) ActionsOfStamina.sideLog(player, "{}::Perform", name());
        if (++timesPerformed >= timesPerformedToExhaust) {
            allow = charge(player);
            if (allow) timesPerformed = 0;
            else timesPerformed--;
            if (ActionsOfStamina.debugging()) ActionsOfStamina.log("{}::Allowed = {}, cost= {}", name(), allow, cost);
            return allow;
        }
        return true;
    }

    public boolean charge(Player player) {
        return StaminaBackends.of(player).spend(player, type.id(), cost, cooldown);
    }

    public boolean isPerforming() {
        return wasPerforming;
    }

    public void setActionState(boolean state) {
        actionState = state;
    }
}

package com.ccr4ft3r.actionsofstamina.actions;


import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * One stamina-costing player action, spending through the active {@link StaminaBackend} under its own
 * {@link #source}.
 * <p>
 * Continuous actions (sprint, swim, elytra, shield, crawl, draw, paraglide, wall cling, glide, crank, climb, row, brush) run a drain refreshed every tick while
 * performing; one-off actions (attack, jump, throw, mine, build, riptide, fish, till) {@link #perform} a spend. All amounts are kept in stamina (1/1000
 * feather), read from the config once, in the constructor (actions are rebuilt whenever the player joins a level).
 * <p>
 * Runs on both sides: on the client the backend only checks (Green Feathers also predicts), the server is
 * authoritative.
 */
public abstract class Action {

    // Fixed slots in PlayerActions' action array.
    public static final int ATTACK = 0;
    public static final int SPRINT = 1;
    public static final int JUMP = 2;
    public static final int CRAWL = 3;
    public static final int ELYTRA = 4;
    public static final int SHIELD = 5;
    public static final int SWIM = 6;
    public static final int PARAGLIDE = 7;
    public static final int WALL_CLING = 8;
    public static final int GLIDE = 9;
    public static final int DRAW = 10;
    public static final int THROW = 11;
    public static final int MINE = 12;
    public static final int BUILD = 13;
    public static final int CRANK = 14;
    public static final int CLIMB = 15;
    public static final int ROW = 16;
    public static final int RIPTIDE = 17;
    public static final int FISH = 18;
    public static final int TILL = 19;
    public static final int BRUSH = 20;
    public static final int COUNT = 21;

    protected final ResourceLocation source;
    /** One-off cost, in stamina: per {@link #perform}, or when a continuous action begins. */
    protected final int cost;
    /** Stamina that must be affordable to perform or begin the action. */
    protected final int minCost;
    /** Ticks without regeneration after spending (and after a continuous action ends). */
    protected final int cooldown;
    protected final double staminaPerTick;
    /** Stamina one drain tick may take at most: what must stay affordable to keep performing. */
    protected final int tickCost;
    protected final boolean regenInhibitor;
    protected boolean wasPerforming = false;
    protected boolean actionState = false;
    protected boolean prevActionState = false;
    protected int timesPerformed = 0;
    protected final int timesPerformedToExhaust;
    /** Whether the last {@link #perform} actually charged {@link #cost}. */
    protected boolean charged;
    private boolean blockingRegen;

    protected String debugInfo;

    public abstract String name();

    /** Slot in {@link PlayerActions#getActions()}; one of the constants above. */
    public abstract int id();

    public Action(ResourceLocation source, ActionCostConfig config) {
        this.source = source;
        this.cost = config.cost();
        this.minCost = config.minStamina();
        this.cooldown = config.regenDelay();
        this.staminaPerTick = config.perTick();
        this.tickCost = (int) Math.ceil(staminaPerTick);
        this.regenInhibitor = config.blocksRegen();
        this.timesPerformedToExhaust = config.timesToCharge();
    }

    public ResourceLocation source() {
        return source;
    }

    public String debugString() {
        return debugInfo;
    }

    public boolean canPerform(Player player) {
        return PlayerActions.isNotExhaustable(player) || canAfford(player, wasPerforming ? tickCost : minCost);
    }

    private boolean canAfford(Player player, int stamina) {
        return StaminaBackends.of(player).canSpend(player, source, stamina);
    }

    /** Refreshes this action's drain for one more tick; false when it can't go on (the drain then stops itself). */
    private boolean drain(Player player, StaminaBackend backend) {
        // Energized players (Green Feathers) keep regenerating, as with the old regen inhibitor.
        blockingRegen = regenInhibitor && !backend.keepsRegenWhileActing(player);
        return backend.drain(player, source, drainPerTick(player), blockingRegen);
    }

    /**
     * What the drain takes this tick. An action may lower it for a while (0: still going on, and still pausing
     * regeneration, but free).
     */
    protected double drainPerTick(Player player) {
        return staminaPerTick;
    }

    public void tick(Player p, PlayerActions a) {
        // Idle (not flagged, not performing, nothing to undo): most actions most of the time, and nothing below changes.
        if (!actionState && !wasPerforming && !prevActionState && (debugInfo != null || !ActionsOfStamina.debugging())) return;

        boolean performing = wasPerforming;

        // Creative or spectator: the action ends here, so coming back to survival mid-action begins it properly
        // (start gate and cost) and its effects don't outlive it.
        if (PlayerActions.isNotExhaustable(p)) {
            if (wasPerforming) finishPerforming(p, a);
            if (wasPerforming || prevActionState) cleanUp(p);
            wasPerforming = false;
            prevActionState = false;
            return;
        }

        if (actionState) {
            StaminaBackend backend = StaminaBackends.of(p);
            if (!wasPerforming) {
                if (backend.canSpend(p, source, minCost) && drain(p, backend)) {
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
            // Ended without ever being paid for: only its effects to undo.
            cleanUp(p);
        }

        boolean changeDetected = wasPerforming != performing || actionState != prevActionState;

        if (AoSServerConfig.ENABLE_DEBUGGING.getAsBoolean() && (debugInfo == null || changeDetected))
            debugInfo = String.format("%s: WasPerforming: %s, Performing: %s, ActionState: %s, Allow: %s, Inhibiting regen: %s", name(), wasPerforming, performing, actionState, canPerform(p), performing && blockingRegen);

        prevActionState = actionState;
        wasPerforming = performing;

    }

    protected abstract void performingEffects(Player p, PlayerActions a);

    protected abstract void notPerformingEffects(Player player, PlayerActions a);

    /** Undoes whatever the action leaves on the player (modifiers): it ended, the player became exempt, or the actions are rebuilt. */
    public void cleanUp(Player player) {
    }

    protected void beginPerforming(Player p, PlayerActions a) {
        if (ActionsOfStamina.debugging()) ActionsOfStamina.sideLog(p, "{}::beginPerforming", name());
        StaminaBackend backend = StaminaBackends.of(p);
        if (cost > 0) backend.spend(p, source, cost, cooldown);
        else if (cooldown > 0) backend.blockRegen(p, source, cooldown);
    }

    /**
     * Stops the drain right away (it would otherwise run until its timeout) and delays regeneration by the
     * cooldown.
     */
    protected void finishPerforming(Player p, PlayerActions a) {
        if (ActionsOfStamina.debugging()) ActionsOfStamina.sideLog(p, "{}::finishPerforming", name());
        StaminaBackend backend = StaminaBackends.of(p);
        backend.stopDrain(p, source);
        if (cooldown > 0 && !backend.keepsRegenWhileActing(p)) backend.blockRegen(p, source, cooldown);
    }

    /** One-off use: charges {@link #cost} every {@code timesPerformedToExhaust} uses. */
    public boolean perform(Player player) {
        charged = false;
        if (PlayerActions.isNotExhaustable(player)) return true;
        boolean allow = canPerform(player);
        if (!allow) {
            if (ActionsOfStamina.debugging()) ActionsOfStamina.log("{}::Allowed = false, cost= {}", name(), cost);
            return false;
        }

        if (ActionsOfStamina.debugging()) ActionsOfStamina.sideLog(player, "{}::Perform", name());
        if (++timesPerformed >= timesPerformedToExhaust) {
            timesPerformed = 0;
            allow = charge(player);
            charged = allow;
            if (ActionsOfStamina.debugging()) ActionsOfStamina.log("{}::Allowed = {}, cost= {}", name(), allow, cost);
            return allow;
        }
        return true;
    }

    /** Spends {@link #cost} once with this action's regen delay. */
    public boolean charge(Player player) {
        return StaminaBackends.of(player).spend(player, source, cost, cooldown);
    }

    /** Whether the last {@link #perform} charged the cost (client side: only checked, the server must charge). */
    public boolean hasJustCharged() {
        return charged;
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

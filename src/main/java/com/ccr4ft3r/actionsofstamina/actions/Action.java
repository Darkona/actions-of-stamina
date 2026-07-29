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
 * One stamina-costing player action of an {@link ActionType}, spending through the active {@link StaminaBackend}
 * under its type's id (its {@link #source}). Every action, Actions of Stamina's own and other mods' alike, is registered
 * in {@link ActionTypes} and built per player from its type.
 * <p>
 * Continuous actions (sprint, swim, elytra, shield, crawl, draw, paraglide, wall cling, glide, crank, climb, row, brush)
 * run a drain refreshed every tick while performing, and may charge a finish cost when they end; one-off actions
 * (attack, jump, throw, mine, build, riptide, fish, till, rolls, skills) {@link #perform} a spend. A continuous action
 * follows the state {@link #setActionState} gives it every tick; one whose start, ticks and end come from another mod's
 * events drives itself with {@link #canBegin}, {@link #begin}, {@link #canContinue}, {@link #continueTick} and
 * {@link #end} instead. All amounts are kept in stamina (1/1000 feather), read from the config once, in the constructor
 * (actions are rebuilt whenever the player joins a level).
 * <p>
 * Runs on both sides: on the client the backend only checks (Green Feathers also predicts), the server is
 * authoritative.
 */
public class Action {

    protected final ActionType type;
    protected final ResourceLocation source;
    /** One-off cost, in stamina: per {@link #perform}, or when a continuous action begins. */
    protected final int cost;
    /** Stamina that must be affordable to perform or begin the action. */
    protected final int minCost;
    /** Charged when a continuous action ends. */
    protected final int finishCost;
    /** What must be affordable to begin: the stamina to begin, else one drain tick, else the finish cost. */
    private final int beginCost;
    /** Ticks without regeneration after spending (and after a continuous action with a drain ends). */
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

    /** Costs from the type's config section. */
    public Action(ActionType type) {
        ActionCostConfig config = type.config();
        this.type = type;
        this.source = type.id();
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

    /** For debug output: the action's id, which is also its stamina source. */
    public String name() {
        return source.toString();
    }

    public ActionType type() {
        return type;
    }

    /** Slot in {@link PlayerActions#getActions()}: its type's {@link ActionType#index()}. */
    public final int id() {
        return type.index();
    }

    public ResourceLocation source() {
        return source;
    }

    public String debugString() {
        return debugInfo;
    }

    public boolean canPerform(Player player) {
        return PlayerActions.isExempt(player) || canAfford(player, wasPerforming ? tickCost : beginCost);
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
                if (backend.canSpend(p, source, beginCost) && drain(p, backend)) {
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

    /** Every tick the action goes on and is paid for. */
    protected void performingEffects(Player p, PlayerActions a) {
    }

    /** Every tick the state asks for the action but the stamina can't pay for it: stop it here. */
    protected void notPerformingEffects(Player player, PlayerActions a) {
    }

    /** Undoes whatever the action leaves on the player (modifiers): it ended, the player became exempt, or the actions are rebuilt. */
    public void cleanUp(Player player) {
    }

    /**
     * Charges the start cost with the cooldown; a free start of an action with a drain pauses regeneration for the
     * cooldown instead (one without a drain leaves regeneration alone until its end, if that costs).
     */
    protected void beginPerforming(Player p, PlayerActions a) {
        if (ActionsOfStamina.debugging()) ActionsOfStamina.sideLog(p, "{}::beginPerforming", name());
        StaminaBackend backend = StaminaBackends.of(p);
        if (cost > 0) backend.spend(p, source, cost, cooldown);
        else if (cooldown > 0 && staminaPerTick > 0) backend.blockRegen(p, source, cooldown);
    }

    /**
     * Stops the drain right away (it would otherwise run until its timeout), charges the finish cost if any (never to
     * an exempt player) and delays regeneration by the cooldown after a drain (an action without one already paused
     * it when it began).
     */
    protected void finishPerforming(Player p, PlayerActions a) {
        if (ActionsOfStamina.debugging()) ActionsOfStamina.sideLog(p, "{}::finishPerforming", name());
        StaminaBackend backend = StaminaBackends.of(p);
        backend.stopDrain(p, source);
        if (finishCost > 0 && !PlayerActions.isExempt(p)) backend.spend(p, source, finishCost, cooldown);
        else if (cooldown > 0 && staminaPerTick > 0 && !backend.keepsRegenWhileActing(p)) backend.blockRegen(p, source, cooldown);
    }

    /** Event-driven continuous action: whether it may begin now (what the state-driven tick checks first). */
    public boolean canBegin(Player player) {
        return PlayerActions.isExempt(player) || canAfford(player, beginCost);
    }

    /** Whether this action drains stamina while it lasts ({@code per_second} above 0). */
    public boolean drains() {
        return staminaPerTick > 0;
    }

    /** Event-driven continuous action: whether one more drain tick is affordable. */
    public boolean canContinue(Player player) {
        return PlayerActions.isExempt(player) || canAfford(player, tickCost);
    }

    /** Event-driven continuous action: it began; charges the start cost (or pauses regeneration for the cooldown). */
    public void begin(Player player) {
        beginPerforming(player, PlayerActions.get(player));
    }

    /** Event-driven continuous action: one more tick of it; false when the drain can't go on. */
    public boolean continueTick(Player player) {
        return drain(player, StaminaBackends.of(player));
    }

    /** Event-driven continuous action: it ended; stops the drain and charges the finish cost. */
    public void end(Player player) {
        finishPerforming(player, PlayerActions.get(player));
    }

    /**
     * A use whose cost the caller works out (a swing with its weapon's multipliers): whether {@code stamina} is
     * affordable now.
     */
    public boolean canPay(Player player, int stamina) {
        return PlayerActions.isExempt(player) || canAfford(player, stamina);
    }

    /** A use whose cost the caller works out: spends {@code stamina} with this action's regen delay. */
    public boolean pay(Player player, int stamina) {
        return PlayerActions.isExempt(player) || StaminaBackends.of(player).spend(player, source, stamina, cooldown);
    }

    /** One-off use: charges {@link #cost} every {@code timesPerformedToExhaust} uses. */
    public boolean perform(Player player) {
        charged = false;
        if (PlayerActions.isExempt(player)) return true;
        boolean allow = canPerform(player);
        if (!allow) {
            if (ActionsOfStamina.debugging()) ActionsOfStamina.log("{}::Allowed = false, cost= {}", name(), cost);
            return false;
        }

        if (ActionsOfStamina.debugging()) ActionsOfStamina.sideLog(player, "{}::Perform", name());
        if (++timesPerformed >= timesPerformedToExhaust) {
            allow = charge(player);
            // A charge that doesn't go through (a Green Feathers cost modifier can raise it past min_stamina) keeps
            // the count where it was, so the next use tries again instead of waiting for a whole new count.
            if (allow) timesPerformed = 0;
            else timesPerformed--;
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

    /** Whether the last {@link #perform} charged the cost (client side: only checked or predicted, the server charges). */
    @SuppressWarnings("unused") // for addons
    public boolean hasJustCharged() {
        return charged;
    }

    public boolean isPerforming() {
        return wasPerforming;
    }

    @SuppressWarnings("unused") // for addons
    public boolean isRegenInhibitor() {
        return regenInhibitor;
    }

    public void setActionState(boolean state) {
        actionState = state;
    }
}

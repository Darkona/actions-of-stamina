package com.ccr4ft3r.actionsofstamina.data;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.compatibility.curios.CuriosCompat;
import com.ccr4ft3r.actionsofstamina.util.ActionFlags;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Per-player action state, a transient field of every player ({@link AosPlayerData}).
 * <p>
 * Common code only: the local-player movement detection that feeds {@link #applyClientState} lives in
 * {@code ClientActionTracker}. Actions are kept in a fixed array indexed by {@link Action#id()} so the
 * per-tick loop allocates nothing.
 */
public class PlayerActions {

    private final Action[] actions = new Action[Action.COUNT];

    /** Last movement-state flags applied (client: last ones sent; server: last ones received). */
    private short stateFlags;
    /** Set when {@link #stateFlags} changed and the action states must be re-applied on the next tick. */
    private boolean changed;

    /** Whether stamina wings are worn in a curio slot; cached, see {@link CuriosCompat}. */
    private boolean curioWings;

    private double lastX = Double.NaN;
    private double lastZ = Double.NaN;

    public PlayerActions() {
    }

    public static PlayerActions get(Player player) {
        return ((AosPlayerData) player).actionsofstamina$actions();
    }

    public static boolean isNotExhaustable(@Nullable Player player) {
        return player == null || player.isCreative() || player.isSpectator() || player instanceof FakePlayer;
    }

    /**
     * Client side: record freshly computed movement-state flags. Returns {@code true} when they differ from the
     * previous ones (the caller then sends them to the server).
     */
    public boolean applyClientState(int flags) {
        if (flags == stateFlags) return false;
        stateFlags = (short) flags;
        changed = true;
        return true;
    }

    /** Server side: movement-state flags received from the owning client. */
    public void processFlags(short flags) {
        stateFlags = flags;
        changed = true;
    }

    public void tick(Player player) {
        if (changed) {
            int f = stateFlags;
            setActionState(Action.SPRINT, ActionFlags.has(f, ActionFlags.SPRINTING));
            setActionState(Action.CRAWL, ActionFlags.has(f, ActionFlags.CRAWLING));
            setActionState(Action.ELYTRA, ActionFlags.has(f, ActionFlags.ELYTRA));
            setActionState(Action.SWIM, ActionFlags.has(f, ActionFlags.SWIMMING));
            setActionState(Action.SHIELD, ActionFlags.has(f, ActionFlags.HOLDING_SHIELD));
            setActionState(Action.DRAW, ActionFlags.has(f, ActionFlags.DRAWING));
            setActionState(Action.PARAGLIDE, ActionFlags.has(f, ActionFlags.PARAGLIDING));
            setActionState(Action.WALL_CLING, ActionFlags.has(f, ActionFlags.WALL_CLINGING));
            setActionState(Action.CLIMB, ActionFlags.has(f, ActionFlags.CLIMBING));
            setActionState(Action.ROW, ActionFlags.has(f, ActionFlags.ROWING));
            changed = false;
        }
        // Each action spends and pauses regeneration through the stamina backend under its own source.
        for (Action action : actions) {
            if (action != null) action.tick(player, this);
        }

        lastX = player.getX();
        lastZ = player.getZ();
    }


    public boolean wearsCurioWings() {
        return curioWings;
    }

    /** Looks the player's curios up again (allocates: on a change, on join, or at an interval, never every tick). */
    public void refreshCurioWings(Player player) {
        curioWings = CuriosCompat.wearsStaminaWings(player);
    }

    public short getStateFlags() {
        return stateFlags;
    }

    public double getLastX() {
        return lastX;
    }

    public double getLastZ() {
        return lastZ;
    }

    /** Enabled actions, indexed by {@link Action#id()}; empty slots are {@code null}. Do not modify. */
    public Action[] getActions() {
        return actions;
    }

    /** Drops every action before they are rebuilt from the config; their drains time out by themselves. */
    public void clearActions(Player player) {
        for (Action action : actions) {
            if (action != null) action.cleanUp(player);
        }
        Arrays.fill(actions, null);
    }

    public void addEnabledAction(Action action) {
        if (actions[action.id()] == null) actions[action.id()] = action;
    }

    @Nullable
    public Action getAction(int actionId) {
        return actions[actionId];
    }

    public void setActionState(int actionId, boolean state) {
        Action action = actions[actionId];
        if (action != null) action.setActionState(state);
    }
}

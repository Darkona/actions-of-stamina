package com.ccr4ft3r.actionsofstamina.data;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.util.ActionFlags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Per-player action state, stored as a transient NeoForge data attachment ({@link AosAttachments#PLAYER_ACTIONS}).
 * <p>
 * Common code only: the local-player movement detection that feeds {@link #applyClientState} lives in
 * {@code ClientActionTracker}. Actions are kept in a fixed array indexed by {@link Action#id()} so the
 * per-tick loop allocates nothing.
 */
public class PlayerActions {

    private final Action[] actions = new Action[Action.COUNT];

    /** Last movement-state byte applied (client: last one sent; server: last one received). */
    private byte stateFlags;
    /** Set when {@link #stateFlags} changed and the action states must be re-applied on the next tick. */
    private boolean changed;

    private boolean moveKeyPressed;
    private boolean jumping;
    private double lastX = Double.NaN;
    private double lastZ = Double.NaN;

    public PlayerActions() {
    }

    public static PlayerActions get(Player player) {
        return player.getData(AosAttachments.PLAYER_ACTIONS);
    }

    public static boolean isNotExhaustable(@Nullable Player player) {
        return player == null || player.isCreative() || player.isSpectator() || player instanceof FakePlayer;
    }

    /**
     * Client side: record a freshly computed movement-state byte. Returns {@code true} when it differs from the
     * previous one (the caller then sends it to the server).
     */
    public boolean applyClientState(byte flags) {
        if (flags == stateFlags) return false;
        stateFlags = flags;
        changed = true;
        return true;
    }

    /** Server side: movement-state byte received from the owning client. */
    public void processFlags(byte flags) {
        stateFlags = flags;
        changed = true;
    }

    public void tick(Player player) {
        if (changed) {
            byte f = stateFlags;
            setActionState(Action.SPRINT, ActionFlags.has(f, ActionFlags.SPRINTING));
            setActionState(Action.CRAWL, ActionFlags.has(f, ActionFlags.CRAWLING));
            setActionState(Action.ELYTRA, ActionFlags.has(f, ActionFlags.ELYTRA));
            setActionState(Action.SWIM, ActionFlags.has(f, ActionFlags.SWIMMING));
            setActionState(Action.SHIELD, ActionFlags.has(f, ActionFlags.HOLDING_SHIELD));
            setActionState(Action.PARAGLIDE, ActionFlags.has(f, ActionFlags.PARAGLIDING));
            changed = false;
        }

        // Each action spends and pauses regeneration through the stamina backend under its own source.
        for (Action action : actions) {
            if (action != null) action.tick(player, this);
        }

        lastX = player.getX();
        lastZ = player.getZ();
    }

    public boolean isMoving() {
        return ActionFlags.has(stateFlags, ActionFlags.MOVING);
    }

    public byte getStateFlags() {
        return stateFlags;
    }

    public double getLastX() {
        return lastX;
    }

    public double getLastZ() {
        return lastZ;
    }

    public boolean isMoveKeyPressed() {
        return moveKeyPressed;
    }

    public void setMoveKeyPressed(boolean moveKeyPressed) {
        this.moveKeyPressed = moveKeyPressed;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public boolean isJumping() {
        return jumping;
    }

    /** Enabled actions, indexed by {@link Action#id()}; empty slots are {@code null}. Do not modify. */
    public Action[] getActions() {
        return actions;
    }

    /** Drops every action before they are rebuilt from the config; their drains time out by themselves. */
    public void clearActions() {
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

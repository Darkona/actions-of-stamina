package com.ccr4ft3r.actionsofstamina.data;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.compatibility.curios.CuriosCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.util.ActionFlags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Per-player action state, stored as a transient NeoForge data attachment ({@link AosAttachments#PLAYER_ACTIONS}).
 * <p>
 * Common code only: the local-player movement detection that feeds {@link #applyClientState} lives in
 * {@code ClientActionTracker}. Actions are kept in an array of one slot per registered action type
 * ({@link ActionTypes}), indexed by {@link Action#id()}, so the per-tick loop allocates nothing and looks nothing up.
 */
public class PlayerActions {

    private final Action[] actions = new Action[ActionTypes.count()];
    /** The same actions, packed at the front for the tick loop; {@link #tickingCount} of them. */
    private final Action[] ticking = new Action[actions.length];
    private int tickingCount;

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
        return player.getData(AosAttachments.PLAYER_ACTIONS);
    }

    /** Whether no action ever costs {@code player} anything: no player, creative, spectator or a fake player. */
    public static boolean isExempt(@Nullable Player player) {
        return player == null || player.isCreative() || player.isSpectator() || player instanceof FakePlayer;
    }

    /**
     * Whether the player may perform or begin action {@code actionId} now. Never refused for an exempt player or an
     * action the config leaves off.
     */
    public static boolean canPerform(Player player, int actionId) {
        if (isExempt(player)) return true;
        Action action = get(player).getAction(actionId);
        return action == null || action.canPerform(player);
    }

    /** {@link #canPerform(Player, int)} by type. */
    public static boolean canPerform(Player player, ActionType type) {
        return canPerform(player, type.index());
    }

    /**
     * Performs one-off action {@code actionId}, charging it when it is due; false when the player can't afford it.
     * Never refused for an exempt player or an action the config leaves off.
     */
    public static boolean perform(Player player, int actionId) {
        if (isExempt(player)) return true;
        Action action = get(player).getAction(actionId);
        return action == null || action.perform(player);
    }

    /** {@link #perform(Player, int)} by type. */
    public static boolean perform(Player player, ActionType type) {
        return perform(player, type.index());
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
            setActionState(VanillaActions.SPRINT, ActionFlags.has(f, ActionFlags.SPRINTING));
            setActionState(VanillaActions.CRAWL, ActionFlags.has(f, ActionFlags.CRAWLING));
            setActionState(VanillaActions.ELYTRA, ActionFlags.has(f, ActionFlags.ELYTRA));
            setActionState(VanillaActions.SWIM, ActionFlags.has(f, ActionFlags.SWIMMING));
            setActionState(VanillaActions.SHIELD, ActionFlags.has(f, ActionFlags.HOLDING_SHIELD));
            setActionState(VanillaActions.DRAW, ActionFlags.has(f, ActionFlags.DRAWING));
            setActionState(WallJumpCompat.WALL_CLING, ActionFlags.has(f, ActionFlags.WALL_CLINGING));
            setActionState(VanillaActions.CLIMB, ActionFlags.has(f, ActionFlags.CLIMBING));
            setActionState(VanillaActions.ROW, ActionFlags.has(f, ActionFlags.ROWING));
            setActionState(VanillaActions.BRUSH, ActionFlags.has(f, ActionFlags.BRUSHING));
            changed = false;
        }
        // Each action spends and pauses regeneration through the stamina backend under its own source.
        Action[] ticking = this.ticking;
        for (int i = 0, n = tickingCount; i < n; i++) ticking[i].tick(player, this);

        // Only the client's movement detection reads them (ClientActionTracker).
        if (player.level().isClientSide()) {
            lastX = player.getX();
            lastZ = player.getZ();
        }
    }


    public boolean wearsCurioWings() {
        return curioWings;
    }

    /** Looks the player's curios up again (allocates: on a change, on join, or at an interval, never every tick). */
    public void refreshCurioWings(Player player) {
        curioWings = CuriosCompat.wearsStaminaWings(player);
    }

    @SuppressWarnings("unused") // for addons
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
        for (int i = 0; i < tickingCount; i++) ticking[i].cleanUp(player);
        Arrays.fill(actions, null);
        Arrays.fill(ticking, null);
        tickingCount = 0;
    }

    public void addEnabledAction(Action action) {
        int slot = action.id();
        if (actions[slot] != null) return;
        actions[slot] = action;
        ticking[tickingCount++] = action;
    }

    /** The action in slot {@code actionId}, or null when the config leaves it off or no type has that slot. */
    @Nullable
    public Action getAction(int actionId) {
        return actionId >= 0 && actionId < actions.length ? actions[actionId] : null;
    }

    /** The player's action of {@code type}, or null when the config leaves it off. */
    @Nullable
    public Action getAction(ActionType type) {
        return actions[type.index()];
    }

    public void setActionState(ActionType type, boolean state) {
        Action action = actions[type.index()];
        if (action != null) action.setActionState(state);
    }
}

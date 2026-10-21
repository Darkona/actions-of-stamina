package com.ccr4ft3r.actionsofstamina.data;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.compatibility.curios.CuriosCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.util.ActionFlags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class PlayerActions {

    private final Action[] actions = new Action[ActionTypes.count()];
    private final Action[] ticking = new Action[actions.length];
    private int tickingCount;

    private short stateFlags;
    private boolean changed;

    private boolean curioWings;
    @Nullable
    private Object vesselListenerTarget;

    private double lastX = Double.NaN;
    private double lastZ = Double.NaN;

    public PlayerActions() {
    }

    public static PlayerActions get(Player player) {
        return player.getData(AosAttachments.PLAYER_ACTIONS);
    }

    public static boolean isExempt(@Nullable Player player) {
        return player == null || player.isCreative() || player.isSpectator() || player instanceof FakePlayer;
    }

    public static boolean canPerform(Player player, int actionId) {
        if (isExempt(player)) return true;
        Action action = get(player).getAction(actionId);
        return action == null || action.canPerform(player);
    }

    public static boolean canPerform(Player player, ActionType type) {
        return canPerform(player, type.index());
    }

    public static boolean perform(Player player, int actionId) {
        if (isExempt(player)) return true;
        Action action = get(player).getAction(actionId);
        return action == null || action.perform(player);
    }

    public static boolean perform(Player player, ActionType type) {
        return perform(player, type.index());
    }

    public boolean applyClientState(int flags) {
        if (flags == stateFlags) return false;
        stateFlags = (short) flags;
        changed = true;
        return true;
    }

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
            setActionState(ParagliderCompat.PARAGLIDE, ActionFlags.has(f, ActionFlags.PARAGLIDING));
            setActionState(WallJumpCompat.WALL_CLING, ActionFlags.has(f, ActionFlags.WALL_CLINGING));
            setActionState(VanillaActions.CLIMB, ActionFlags.has(f, ActionFlags.CLIMBING));
            setActionState(VanillaActions.ROW, ActionFlags.has(f, ActionFlags.ROWING));
            setActionState(VanillaActions.BRUSH, ActionFlags.has(f, ActionFlags.BRUSHING));
            changed = false;
        }
        Action glide = getAction(GlidersCompat.GLIDE);
        if (glide != null) glide.setActionState(!player.onGround() && GlidersCompat.isGliding(player));

        Action[] ticking = this.ticking;
        for (int i = 0, n = tickingCount; i < n; i++) ticking[i].tick(player, this);

        if (player.level().isClientSide()) {
            lastX = player.getX();
            lastZ = player.getZ();
        }
    }


    public boolean wearsCurioWings() {
        return curioWings;
    }

    public void refreshCurioWings(Player player) {
        curioWings = CuriosCompat.wearsStaminaWings(player);
    }

    @Nullable
    public Object vesselListenerTarget() {
        return vesselListenerTarget;
    }

    public void setVesselListenerTarget(@Nullable Object target) {
        vesselListenerTarget = target;
    }

    @SuppressWarnings("unused")
    public short getStateFlags() {
        return stateFlags;
    }

    public double getLastX() {
        return lastX;
    }

    public double getLastZ() {
        return lastZ;
    }

    public Action[] getActions() {
        return actions;
    }

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

    @Nullable
    public Action getAction(int actionId) {
        return actionId >= 0 && actionId < actions.length ? actions[actionId] : null;
    }

    @Nullable
    public Action getAction(ActionType type) {
        return actions[type.index()];
    }

    public void setActionState(ActionType type, boolean state) {
        Action action = actions[type.index()];
        if (action != null) action.setActionState(state);
    }
}

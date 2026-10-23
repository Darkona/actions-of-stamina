package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import java.util.Arrays;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class PlayerActions {

    private static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerActions>> ATTACHMENT =
            ActionsOfStamina.ATTACHMENT_TYPES.register("player_actions", () -> AttachmentType.builder(PlayerActions::new).build());

    private final Action[] actions = new Action[ActionType.count()];
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
        return player.getData(ATTACHMENT);
    }

    public static boolean isExempt(@Nullable Player player) {
        return player == null || player.isCreative() || player.isSpectator() || player instanceof FakePlayer;
    }

    @Nullable
    public static Action action(@Nullable Player player, ActionType type) {
        return isExempt(player) ? null : get(player).getAction(type);
    }

    @Nullable
    public static <A extends Action> A action(Player player, ActionType type, Class<A> kind) {
        Action action = action(player, type);
        return kind.isInstance(action) ? kind.cast(action) : null;
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
            for (ActionType type : ActionType.flagged()) setActionState(type, (f & type.stateBit()) != 0);
            changed = false;
        }
        for (ActionType type : ActionType.sensed()) {
            Action sensed = actions[type.index()];
            if (sensed != null) sensed.setActionState(type.senses(player));
        }

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

    public void setCurioWings(boolean wearing) {
        curioWings = wearing;
    }

    @Nullable
    public Object vesselListenerTarget() {
        return vesselListenerTarget;
    }

    public void setVesselListenerTarget(@Nullable Object target) {
        vesselListenerTarget = target;
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

    public static void addEnabledActions(PlayerActions a) {
        for (ActionType type : ActionType.all()) {
            if (!type.enabled()) continue;
            Action action = type.create();
            if (action.id() != type.index()) {
                ActionsOfStamina.logger.error("Action {} reports slot {} but its type has slot {}: left out", type, action.id(), type.index());
                continue;
            }
            a.addEnabledAction(action);
        }
    }
}

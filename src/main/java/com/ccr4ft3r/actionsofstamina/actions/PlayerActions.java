package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.compatibility.curios.CuriosCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import java.util.Arrays;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class PlayerActions {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ActionsOfStamina.MOD_ID);
    private static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerActions>> ATTACHMENT =
            ATTACHMENT_TYPES.register("player_actions", () -> AttachmentType.builder(PlayerActions::new).build());

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

    public static final class ActionFlags {

        public static final int SPRINTING = 1 << 1;
        public static final int CRAWLING = 1 << 2;
        public static final int SWIMMING = 1 << 3;
        public static final int ELYTRA = 1 << 4;
        public static final int HOLDING_SHIELD = 1 << 5;
        public static final int PARAGLIDING = 1 << 7;
        public static final int WALL_CLINGING = 1 << 8;
        public static final int DRAWING = 1 << 9;
        public static final int CLIMBING = 1 << 10;
        public static final int ROWING = 1 << 11;
        public static final int BRUSHING = 1 << 12;

        private ActionFlags() {
        }

        public static boolean has(int flags, int bit) {
            return (flags & bit) != 0;
        }

        public static int with(int flags, int bit, boolean state) {
            return state ? flags | bit : flags & ~bit;
        }
    }
}

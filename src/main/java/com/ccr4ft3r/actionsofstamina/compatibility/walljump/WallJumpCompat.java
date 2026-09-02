package com.ccr4ft3r.actionsofstamina.compatibility.walljump;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.network.ActionPerformedPacket;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

/**
 * Wall-Jump TXF compatibility, safe to load without it: the only calls into the mod are the client mixins
 * ({@code WallJumpLogicMixin}, {@code WallJumpDoubleJumpMixin}) and {@link WallJumpClientBridge}.
 * <p>
 * Wall-Jump TXF decides every move on the local player. So the client refuses a wall jump, a double jump or a grip
 * the stamina can't pay for, and lets go of a wall when the grip can't be paid any more. The server charges: a wall
 * or double jump is a one-off action the client reports with an {@link ActionPerformedPacket}, and the wall cling is a
 * continuous action ({@link WallClingAction}) driven by the client's movement-state flags, like sprinting.
 */
public final class WallJumpCompat {

    public static final String MOD_ID = "walljump";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);

    public static final ActionType WALL_JUMP = ActionTypes.register(ActionsOfStamina.id("walljump/wall_jump"), WallJumpConfig.WALL_JUMP,
            () -> isActive() && WallJumpConfig.WALL_JUMP.enabled(), Action::new);
    public static final ActionType DOUBLE_JUMP = ActionTypes.register(ActionsOfStamina.id("walljump/double_jump"), WallJumpConfig.DOUBLE_JUMP,
            () -> isActive() && WallJumpConfig.DOUBLE_JUMP.enabled(), Action::new);
    public static final ActionType WALL_CLING = ActionTypes.register(ActionsOfStamina.id("walljump/wall_cling"), WallJumpConfig.WALL_CLING,
            () -> isActive() && WallJumpConfig.WALL_CLING.enabled(), WallClingAction::new);

    private WallJumpCompat() {
    }

    /** Mod construction: registers the action types above (set when this class loads). */
    public static void registerActions() {
    }

    public static boolean isActive() {
        return LOADED && WallJumpConfig.ENABLED.get();
    }

    /** Client, from the mixin: whether the local player may wall jump now; if so the server is told to perform it. */
    public static boolean tryWallJump(Player player) {
        return tryJump(player, WALL_JUMP);
    }

    /**
     * Client thread only: a paid double jump is about to call {@code jumpFromGround}, which must not charge (or refuse)
     * it again as a normal jump.
     */
    private static boolean doubleJumping;

    /** Client, from the mixin: whether the local player may double jump now; if so the server is told to perform it. */
    public static boolean tryDoubleJump(Player player) {
        boolean allowed = tryJump(player, DOUBLE_JUMP);
        doubleJumping = allowed;
        return allowed;
    }

    /**
     * From the vanilla jump check: true, once, for the jump a double jump performs, so it isn't charged twice.
     */
    public static boolean consumeDoubleJump(Player player) {
        if (!doubleJumping || !player.level.isClientSide()) return false;
        doubleJumping = false;
        return true;
    }

    /** From the mixin, after the double jump: never leave the flag set for a later jump. */
    public static void endDoubleJump() {
        doubleJumping = false;
    }

    /**
     * The client only checks: the move already happens there, and the server performs it, as one it saw itself, when
     * the {@link ActionPerformedPacket} arrives.
     */
    private static boolean tryJump(Player player, ActionType move) {
        if (!isActive() || PlayerActions.isExempt(player)) return true;
        ActionCostConfig costs = move.config();
        if (!costs.enabled() || costs.cost() <= 0) return true;
        if (!PlayerActions.canPerform(player, move)) return false;
        PacketHandler.sendToServer(new ActionPerformedPacket((byte) move.index()));
        return true;
    }

    /**
     * Client, from the mixin: whether the local player may grab a wall, or keep holding it once clinging (the
     * cling action then only needs one more drain tick).
     */
    public static boolean canCling(Player player) {
        return !isActive() || PlayerActions.canPerform(player, WALL_CLING);
    }

    /** Client only: whether the local player is clinging to (or sliding down) a wall. */
    public static boolean isClinging(Player player) {
        return isActive() && WallJumpClientBridge.isClinging();
    }
}

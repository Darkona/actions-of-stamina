package com.ccr4ft3r.actionsofstamina.compatibility.walljump;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

/**
 * Wall-Jump TXF compatibility, safe to load without it: the only calls into the mod are the client mixins
 * ({@code WallJumpLogicMixin}, {@code WallJumpDoubleJumpMixin}) and {@link WallJumpClientBridge}.
 * <p>
 * Wall-Jump TXF decides every move on the local player. So the client refuses a wall jump, a double jump or a grip
 * the stamina can't pay for, and lets go of a wall when the grip can't be paid any more. The server charges: a wall
 * or double jump arrives as a {@link WallJumpChargePacket}, and the wall cling is a continuous action
 * ({@link WallClingAction}) driven by the client's movement-state flags, like sprinting.
 */
public final class WallJumpCompat {

    public static final String MOD_ID = "walljump";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);

    public static final byte WALL_JUMP = 0;
    public static final byte DOUBLE_JUMP = 1;

    static final ResourceLocation WALL_JUMP_SOURCE = ActionsOfStamina.id("walljump/wall_jump");
    static final ResourceLocation DOUBLE_JUMP_SOURCE = ActionsOfStamina.id("walljump/double_jump");

    private WallJumpCompat() {
    }

    public static boolean isActive() {
        return LOADED && WallJumpConfig.ENABLED.get();
    }

    private static ActionCostConfig costsOf(byte move) {
        return move == WALL_JUMP ? WallJumpConfig.WALL_JUMP : WallJumpConfig.DOUBLE_JUMP;
    }

    private static ResourceLocation sourceOf(byte move) {
        return move == WALL_JUMP ? WALL_JUMP_SOURCE : DOUBLE_JUMP_SOURCE;
    }

    /** Client, from the mixin: whether the local player may wall jump now; if so the server is asked to charge it. */
    public static boolean tryWallJump(Player player) {
        return tryJump(player, WALL_JUMP);
    }

    /**
     * Client thread only: a paid double jump is about to call {@code jumpFromGround}, which must not charge (or refuse)
     * it again as a normal jump.
     */
    private static boolean doubleJumping;

    /** Client, from the mixin: whether the local player may double jump now; if so the server is asked to charge it. */
    public static boolean tryDoubleJump(Player player) {
        boolean allowed = tryJump(player, DOUBLE_JUMP);
        doubleJumping = allowed;
        return allowed;
    }

    /**
     * From the vanilla jump check: true, once, for the jump a double jump performs, so it isn't charged twice.
     */
    public static boolean consumeDoubleJump(Player player) {
        if (!doubleJumping || !player.level().isClientSide()) return false;
        doubleJumping = false;
        return true;
    }

    /** From the mixin, after the double jump: never leave the flag set for a later jump. */
    public static void endDoubleJump() {
        doubleJumping = false;
    }

    private static boolean tryJump(Player player, byte move) {
        if (!isActive() || PlayerActions.isExempt(player)) return true;
        ActionCostConfig costs = costsOf(move);
        int cost = costs.cost();
        if (!costs.enabled() || cost <= 0) return true;
        if (!StaminaBackends.of(player).canSpend(player, sourceOf(move), cost)) return false;
        PacketHandler.sendToServer(new WallJumpChargePacket(move));
        return true;
    }

    /**
     * Server: charges a wall jump or a double jump the client just performed. It already happened, so it can only be
     * charged, not stopped.
     */
    public static void charge(ServerPlayer player, byte move) {
        if (move != WALL_JUMP && move != DOUBLE_JUMP || !isActive()) return;
        ActionCostConfig costs = costsOf(move);
        int cost = costs.cost();
        if (costs.enabled() && cost > 0) StaminaBackends.server().spend(player, sourceOf(move), cost, costs.regenDelay());
    }

    /**
     * Client, from the mixin: whether the local player may grab a wall, or keep holding it once clinging (the
     * cling action then only needs one more drain tick).
     */
    public static boolean canCling(Player player) {
        return !isActive() || PlayerActions.canPerform(player, Action.WALL_CLING);
    }

    /** Client only: whether the local player is clinging to (or sliding down) a wall. */
    public static boolean isClinging(Player player) {
        return isActive() && WallJumpClientBridge.isClinging();
    }
}

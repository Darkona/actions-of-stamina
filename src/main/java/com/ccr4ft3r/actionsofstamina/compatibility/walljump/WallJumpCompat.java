package com.ccr4ft3r.actionsofstamina.compatibility.walljump;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.network.Packets.ActionPerformedPacket;
import dev.darkona.jdlib.api.compat.Bridges;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public final class WallJumpCompat {

    public static final String MOD_ID = "walljump";
    public static final boolean LOADED = Bridges.isLoaded(MOD_ID);

    public static final ActionType WALL_JUMP = ActionType.register(ActionsOfStamina.id("walljump/wall_jump"), WallJumpConfig.WALL_JUMP,
            () -> isActive() && WallJumpConfig.WALL_JUMP.enabled(), Action::new);
    public static final ActionType DOUBLE_JUMP = ActionType.register(ActionsOfStamina.id("walljump/double_jump"), WallJumpConfig.DOUBLE_JUMP,
            () -> isActive() && WallJumpConfig.DOUBLE_JUMP.enabled(), Action::new);
    public static final ActionType WALL_CLING = ActionType.register(ActionsOfStamina.id("walljump/wall_cling"), WallJumpConfig.WALL_CLING,
            () -> isActive() && WallJumpConfig.WALL_CLING.enabled(), Action::new);

    private WallJumpCompat() {
    }

    public static void registerActions() {
    }

    public static boolean isActive() {
        return LOADED && WallJumpConfig.ENABLED.getAsBoolean();
    }

    public static boolean tryWallJump(Player player) {
        return tryJump(player, WALL_JUMP);
    }

    private static boolean doubleJumping;

    public static boolean tryDoubleJump(Player player) {
        boolean allowed = tryJump(player, DOUBLE_JUMP);
        doubleJumping = allowed;
        return allowed;
    }

    public static boolean consumeDoubleJump(Player player) {
        if (!doubleJumping || !player.level().isClientSide()) return false;
        doubleJumping = false;
        return true;
    }

    public static void endDoubleJump() {
        doubleJumping = false;
    }

    private static boolean tryJump(Player player, ActionType move) {
        if (!isActive() || PlayerActions.isExempt(player)) return true;
        ActionCostConfig costs = move.config();
        if (!costs.enabled() || costs.cost() <= 0) return true;
        if (!PlayerActions.canPerform(player, move)) return false;
        PacketDistributor.sendToServer(new ActionPerformedPacket((byte) move.index()));
        return true;
    }

    public static boolean canCling(Player player) {
        return !isActive() || PlayerActions.canPerform(player, WALL_CLING);
    }

    public static boolean isClinging(Player player) {
        return isActive() && WallJumpClientBridge.isClinging();
    }
}

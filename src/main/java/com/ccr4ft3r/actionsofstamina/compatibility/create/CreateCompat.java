package com.ccr4ft3r.actionsofstamina.compatibility.create;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

/**
 * Create compatibility, safe to load without it: calls into the mod go through {@link CreateBridge}.
 * <p>
 * A player turns a hand crank by holding the use key on it: every few ticks Create's {@code useItemOn} turns it again,
 * on both sides. Mixins there (and on the valve handle's click) report each turn here, which keeps the continuous
 * {@link CrankAction} going, and refuse turns the player can't afford.
 */
public final class CreateCompat {

    public static final String MOD_ID = "create";
    public static final boolean LOADED = ModList.get().isLoaded(MOD_ID);

    private CreateCompat() {
    }

    public static boolean isActive() {
        return LOADED && CreateConfig.CRANK.enabled();
    }

    /** Both sides, from the hand crank mixin: whether the player may turn the crank at {@code pos} now. */
    public static boolean turnCrank(Player player, Level level, BlockPos pos) {
        if (PlayerActions.isNotExhaustable(player)) return true;
        if (!(PlayerActions.get(player).getAction(Action.CRANK) instanceof CrankAction crank)) return true;
        return crank.turn(player, level.getBlockEntity(pos));
    }

    /** Both sides, from the valve handle mixin: whether the player may turn a valve handle now. */
    public static boolean mayTurnValve(Player player) {
        CrankAction crank = valveAction(player);
        return crank == null || crank.canPerform(player);
    }

    /** Both sides, from the valve handle mixin: the handle turned. */
    public static void valveTurned(Player player) {
        CrankAction crank = valveAction(player);
        if (crank != null) crank.turn(player, null);
    }

    @Nullable
    private static CrankAction valveAction(Player player) {
        if (PlayerActions.isNotExhaustable(player) || !CreateConfig.VALVE_HANDLES.getAsBoolean()) return null;
        return PlayerActions.get(player).getAction(Action.CRANK) instanceof CrankAction crank ? crank : null;
    }

    /** The player ran out while turning: the crank stops now rather than coasting through Create's own delay. */
    static void stop(BlockEntity crank) {
        CreateBridge.stop(crank);
    }
}

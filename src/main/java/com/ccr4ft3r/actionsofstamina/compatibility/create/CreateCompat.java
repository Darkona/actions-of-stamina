package com.ccr4ft3r.actionsofstamina.compatibility.create;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.PlayerActions;
import dev.darkona.jdlib.api.compat.Bridges;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class CreateCompat {

    public static final String MOD_ID = "create";
    public static final boolean LOADED = Bridges.isLoaded(MOD_ID);

    private CreateCompat() {
    }

    public static final ActionType CRANK = ActionType.register(ActionsOfStamina.id("create/crank"), CreateConfig.CRANK,
            CreateCompat::isActive, CrankAction::new);

    public static void registerActions() {
    }

    public static boolean isActive() {
        return LOADED && CreateConfig.CRANK.enabled();
    }

    public static boolean turnCrank(Player player, Level level, BlockPos pos) {
        if (PlayerActions.isExempt(player)) return true;
        if (!(PlayerActions.get(player).getAction(CRANK) instanceof CrankAction crank)) return true;
        return crank.turn(player, level.getBlockEntity(pos));
    }

    public static boolean mayTurnValve(Player player) {
        CrankAction crank = valveAction(player);
        return crank == null || crank.canPerform(player);
    }

    public static void valveTurned(Player player) {
        CrankAction crank = valveAction(player);
        if (crank != null) crank.turn(player, null);
    }

    @Nullable
    private static CrankAction valveAction(Player player) {
        if (PlayerActions.isExempt(player) || !CreateConfig.VALVE_HANDLES.getAsBoolean()) return null;
        return PlayerActions.get(player).getAction(CRANK) instanceof CrankAction crank ? crank : null;
    }
}

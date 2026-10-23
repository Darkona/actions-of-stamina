package com.ccr4ft3r.actionsofstamina.compatibility.create;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.PlayerActions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class CrankAction extends Action {

    static final int TURN_WINDOW = 10;
    private static final int NEVER = Integer.MIN_VALUE / 2;

    private int lastTurnTick = NEVER;
    @Nullable
    private BlockEntity crank;

    public CrankAction(ActionType type) {
        super(type);
    }

    boolean turn(Player player, @Nullable BlockEntity turned) {
        if (!canPerform(player)) return false;
        lastTurnTick = player.tickCount;
        crank = turned;
        return true;
    }

    @Override
    public void tick(Player p, PlayerActions a) {
        setActionState(p.tickCount - lastTurnTick <= TURN_WINDOW);
        super.tick(p, a);
    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        if (crank != null) CreateCompat.stop(crank);
        crank = null;
        lastTurnTick = NEVER;
    }

    @Override
    public void cleanUp(Player player) {
        crank = null;
        lastTurnTick = NEVER;
    }
}

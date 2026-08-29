package com.ccr4ft3r.actionsofstamina.compatibility.create;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Turning a Create hand crank (or valve handle): drains while the player keeps turning it. Each side records the turns
 * it sees itself (Create turns the crank on both), so no state goes over the network.
 */
public class CrankAction extends Action {

    public static final ResourceLocation SOURCE = ActionsOfStamina.id("create/crank");
    /**
     * Ticks a turn keeps the action going: a held use key turns the crank again every 4 ticks, and Create keeps it
     * turning 10 ticks after the last turn.
     */
    static final int TURN_WINDOW = 10;
    private static final int NEVER = Integer.MIN_VALUE / 2;

    private int lastTurnTick = NEVER;
    /** The hand crank last turned, stopped when the stamina runs out; null for a valve handle. */
    @Nullable
    private BlockEntity crank;

    public CrankAction() {
        super(SOURCE, CreateConfig.CRANK);
    }

    @Override
    public int id() {
        return CRANK;
    }

    /** A turn the player can't afford is refused; otherwise it keeps the action going. */
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
    protected void performingEffects(Player p, PlayerActions a) {
    }

    /** Out of stamina mid-turn: the crank stops, and so does the action until the next turn it can pay for. */
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

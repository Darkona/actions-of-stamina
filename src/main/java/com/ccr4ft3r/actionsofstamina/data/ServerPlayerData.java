package com.ccr4ft3r.actionsofstamina.data;

import com.ccr4ft3r.actionsofstamina.config.ActionType;
import com.ccr4ft3r.actionsofstamina.config.AoSAction;
import com.elenai.feathers.api.FeathersHelper;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ccr4ft3r.actionsofstamina.config.AoSAction.*;
import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;
import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.*;

public class ServerPlayerData {

    private Vec3 lastPosition;
    private boolean isMoving;
    private boolean startedMoving;

    private final Map<AoSAction, AtomicBoolean> stateByAction = Maps.newConcurrentMap();
    private final Map<AoSAction, AtomicDouble> amountByAction = Maps.newConcurrentMap();

    ServerPlayerData() {
        for (AoSAction action : AoSAction.values()) {
            stateByAction.put(action, new AtomicBoolean());
            amountByAction.put(action, new AtomicDouble());
        }
    }

    public Vec3 getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Vec3 lastPosition) {
        this.lastPosition = lastPosition;
    }

    public boolean isMoving() {
        if (startedMoving) {
            this.startedMoving = false;
            return true;
        }
        return isMoving;
    }

    public void setMoving(boolean moving) {
        if (moving && !isMoving)
            startedMoving = true;
        isMoving = moving;
    }

    public void set(AoSAction action, boolean newState, double increment, ServerPlayer player) {
        AtomicBoolean currentState = stateByAction.get(action);
        checkStarting(action, currentState.get(), newState, player);
        currentState.set(newState);
        if (currentState.get()) {
            amountByAction.get(action).addAndGet(increment);
            exhaust(player, action);
        }

        if (ActionType.TIME_ACTIONS.contains(action) && action.getStopper() != null) {
            stopIfExhausted(player, action, () -> action.getStopper().accept(player));
        }
    }

    public void set(AoSAction action, double amount, ServerPlayer player) {
        set(action, true, amount, player);
    }

    public void set(AoSAction action, boolean newState, ServerPlayer player) {
        set(action, newState, 1d, player);
    }

    private void checkStarting(AoSAction action, boolean currentValue, boolean newValue, ServerPlayer player) {
        if (!currentValue && newValue) {
            if (action.getType() == ActionType.TICKS)
                FeathersHelper.spendFeathers(player, getProfile().initialCostsByAction.get(action).get());
            exhaust(player, ATTACKING);
        }
    }

    public boolean is(AoSAction action) {
        return stateByAction.get(action).get();
    }

    public double get(AoSAction action) {
        return amountByAction.get(action).get();
    }

    public void reset(AoSAction action) {
        amountByAction.get(action).set(0);
    }
}
package com.ccr4ft3r.actionsofstamina.data;

import com.ccr4ft3r.actionsofstamina.config.ActionType;
import com.ccr4ft3r.actionsofstamina.config.AoSAction;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.getProfile;
import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.stopIfExhausted;
import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.exhaust;

public class ServerPlayerData {

    private Vec3 lastPosition;
    private boolean isMoving;
    private boolean startedMoving;

    private final Map<AoSAction, AtomicBoolean> stateByAction = Maps.newConcurrentMap();
    private final Map<AoSAction, AtomicDouble> amountByAction = Maps.newConcurrentMap();
    private final Map<AoSAction, AtomicLong> tickByAction = Maps.newConcurrentMap();

    ServerPlayerData() {
        for (AoSAction action : AoSAction.values()) {
            stateByAction.put(action, new AtomicBoolean());
            amountByAction.put(action, new AtomicDouble());
            tickByAction.put(action, new AtomicLong());
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
        boolean wasStarted = checkStarting(action, currentState.get(), newState, player);
        currentState.set(newState);
        if (currentState.get()) {
            if (tickByAction.get(action).get() >= player.tickCount) {
                return;
            }
            long factor = 1;
            if (action.getType() == ActionType.TICKS && !wasStarted)
                factor = player.tickCount - tickByAction.get(action).get();
            tickByAction.get(action).set(player.tickCount);
            amountByAction.get(action).addAndGet(increment * factor);
            exhaust(player, action);
        }

        if (ActionType.TIME_ACTIONS.contains(action) && action.getStopper() != null) {
            stopIfExhausted(player, action, () -> action.getStopper().accept(player));
        }
    }

    public void update(ServerPlayer player) {
        ActionType.TIME_ACTIONS.forEach(action -> {
            if (is(action) && tickByAction.get(action).get() + 20 < player.tickCount)
                set(action, false, player);
        });
    }

    public void set(AoSAction action, double amount, ServerPlayer player) {
        set(action, true, amount, player);
    }

    public void set(AoSAction action, boolean newState, ServerPlayer player) {
        set(action, newState, 1d, player);
    }

    private boolean checkStarting(AoSAction action, boolean currentValue, boolean newValue, Player player) {
        if (!currentValue && newValue) {
            if (action.getType() == ActionType.TICKS)
               //return FeathersAPI.spendFeathers(player, getProfile().initialCostsByAction.get(action).get(), 0);
                return true;

        }
        return false;
    }

    public boolean is(AoSAction action) {
        return stateByAction.get(action).get();
    }

    public double get(AoSAction action) {
        return amountByAction.get(action).get();
    }

    public void reset(AoSAction action) {
        amountByAction.get(action).set(amountByAction.get(action).get() - getProfile().delayByAction.get(action).get());
    }
}
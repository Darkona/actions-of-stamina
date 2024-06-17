package com.ccr4ft3r.actionsofstamina.capability;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class PlayerActions implements IActionCapability {

    private final Map<String, Action> enabledActions = new HashMap<>();
    private final Map<String, Boolean> actionStates = new HashMap<>();

    private Vec3 lastPosition;
    private boolean clientMoving;
    private boolean clientJumping;

    public PlayerActions() {
    }


    @Override
    public Map<String, Boolean> getActionStates() {
        return actionStates;
    }

    public boolean isClientMoving() {
        return clientMoving;
    }

    @Override
    public void setClientMoving(boolean moving) {
        this.clientMoving = moving;
    }

    @Override
    public Vec3 getLastPosition() {
        return lastPosition;
    }

    @Override
    public void setLastPosition(Vec3 lastPosition) {
        this.lastPosition = lastPosition;
    }

    @Override
    public Map<String, Action> getEnabledActions() {
        return enabledActions;
    }

    @Override
    public void addEnabledAction(Action action) {
        if (!enabledActions.containsKey(action.getName())) {
            enabledActions.put(action.getName(), action);
        }
    }

    @Override
    public Optional<Action> getAction(String actionId) {
        return Optional.ofNullable(enabledActions.get(actionId));
    }

    @Override
    public CompoundTag saveNBTData() {
        var tag = new CompoundTag();

        var actionsTag = new CompoundTag();
        this.enabledActions.forEach((actionName, action) -> {
            if (action.saveNBTData() != null) {
                actionsTag.put(actionName, action.saveNBTData());
            }
        });
        tag.put("enabledActions", actionsTag);

        var actionStatesTag = new CompoundTag();
        this.actionStates.forEach(actionStatesTag::putBoolean);
        tag.put("actionStates", actionStatesTag);
        return tag;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        var actionsTag = nbt.getCompound("enabledActions");
        actionsTag.getAllKeys().forEach(actionName -> {
            var action = ActionProvider.getInstance().getActionByName(actionName);
            action.loadNBTData(actionsTag.getCompound(actionName));
            this.enabledActions.put(actionName, action);
        });

        var actionStatesTag = nbt.getCompound("actionStates");
        actionStatesTag.getAllKeys().forEach(actionId -> {
            this.actionStates.put(actionId, actionStatesTag.getBoolean(actionId));
        });

    }

    @Override
    public void copyFrom(IActionCapability oldStore) {
        this.enabledActions.putAll(oldStore.getEnabledActions());
        this.actionStates.putAll(oldStore.getActionStates());
    }

    @Override
    public void setClientJumping(boolean b) {
        clientJumping = b;
    }
}

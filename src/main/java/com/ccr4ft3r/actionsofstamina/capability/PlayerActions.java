package com.ccr4ft3r.actionsofstamina.capability;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.FakePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AutoRegisterCapability
public class PlayerActions  {

    private final Map<String, Action> enabledActions = new HashMap<>();
    private final Map<String, Boolean> actionStates = new HashMap<>();

    private Vec3 lastPosition;
    private boolean clientMoving;
    private boolean clientJumping;

    public PlayerActions() {
    }

    public static boolean cannotBeExhausted(Player player){
        return player.isCreative() || player.isSpectator() || player instanceof FakePlayer;
    }

    public Map<String, Boolean> getActionStates() {
        return actionStates;
    }

    public boolean isClientMoving() {
        return clientMoving;
    }

    
    public void setClientMoving(boolean moving) {
        this.clientMoving = moving;
    }

    
    public Vec3 getLastPosition() {
        return lastPosition;
    }

    
    public void setLastPosition(Vec3 lastPosition) {
        this.lastPosition = lastPosition;
    }

    
    public Map<String, Action> getEnabledActions() {
        return enabledActions;
    }

    
    public void addEnabledAction(Action action) {
        if (!enabledActions.containsKey(action.getName())) {
            enabledActions.put(action.getName(), action);
        }
    }

    
    public Optional<Action> getAction(String actionId) {
        return Optional.ofNullable(enabledActions.get(actionId));
    }

    
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

    
    public void copyFrom(PlayerActions oldStore) {
        this.enabledActions.putAll(oldStore.getEnabledActions());
        this.actionStates.putAll(oldStore.getActionStates());
    }

    
    public void setClientJumping(boolean b) {
        clientJumping = b;
    }
}

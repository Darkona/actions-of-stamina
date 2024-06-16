package com.ccr4ft3r.actionsofstamina;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionProvider;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class PlayerActions implements IActionCapability {

    private final Map<ResourceLocation, Action> enabledActions = new HashMap<>();
    private boolean moving;
    private boolean sprinting;
    private boolean jumping;
    private boolean climbing;
    private boolean sneaking;
    private boolean flying;
    private boolean swimming;
    private boolean crawling;
    private Vec3 lastPosition;

    public PlayerActions() {

        addEnabledAction(new AttackAction());
    }

    @Override
    public boolean isMoving() {
        return false;
    }

    @Override
    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    @Override
    public boolean isSprinting() {
        return sprinting;
    }

    @Override
    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    @Override
    public boolean isJumping() {
        return jumping;
    }

    @Override
    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    @Override
    public boolean isClimbing() {
        return climbing;
    }

    @Override
    public void setClimbing(boolean climbing) {
        this.climbing = climbing;
    }

    @Override
    public boolean isSneaking() {
        return sneaking;
    }

    @Override
    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }

    @Override
    public boolean isFlying() {
        return flying;
    }

    @Override
    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    @Override
    public boolean isSwimming() {
        return swimming;
    }

    @Override
    public void setSwimming(boolean swimming) {
        this.swimming = swimming;
    }

    @Override
    public boolean isCrawling() {
        return crawling;
    }

    @Override
    public void setCrawling(boolean crawling) {
        this.crawling = crawling;
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
    public Map<ResourceLocation, Action> getEnabledActions() {
        return enabledActions;
    }

    @Override
    public void update(Player player) {

    }

    @Override
    public void addEnabledAction(Action action) {
        if (!enabledActions.containsKey(action.getName())) {
            enabledActions.put(action.getName(), action);
        }

    }

    @Override
    public Optional<Action> getAction(ResourceLocation actionId) {
        return Optional.ofNullable(enabledActions.get(actionId));
    }

    @Override
    public CompoundTag saveNBTData() {
        var tag = new CompoundTag();

        var actionsTag = new CompoundTag();
        this.enabledActions.forEach((actionName, action) -> actionsTag.put(actionName.getPath(), action.saveNBTData()));
        tag.put("enabledActions", actionsTag);

        tag.putBoolean("moving", this.moving);
        if (this.lastPosition != null) {
            tag.putDouble("lastPositionX", lastPosition.x);
            tag.putDouble("lastPositionY", lastPosition.y);
            tag.putDouble("lastPositionZ", lastPosition.z);
        }
        tag.putBoolean("sprinting", this.sprinting);
        tag.putBoolean("jumping", this.jumping);
        tag.putBoolean("climbing", this.climbing);
        tag.putBoolean("sneaking", this.sneaking);
        tag.putBoolean("flying", this.flying);
        tag.putBoolean("swimming", this.swimming);
        tag.putBoolean("crawling", this.crawling);
        return tag;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        var actionsTag = nbt.getCompound("enabledActions");
        actionsTag.getAllKeys().forEach(actionName -> {
            var action = ActionProvider.getInstance().getActionByName(actionName);
            action.loadNBTData(actionsTag.getCompound(actionName));
            this.enabledActions.put(new ResourceLocation(actionName), action);
        });
        this.moving = nbt.getBoolean("moving");
        if (nbt.contains("lastPositionX")) {
            this.lastPosition = new Vec3(nbt.getDouble("lastPositionX"), nbt.getDouble("lastPositionY"), nbt.getDouble("lastPositionZ"));
        }
        this.sprinting = nbt.getBoolean("sprinting");
        this.jumping = nbt.getBoolean("jumping");
        this.climbing = nbt.getBoolean("climbing");
        this.sneaking = nbt.getBoolean("sneaking");
        this.flying = nbt.getBoolean("flying");
        this.swimming = nbt.getBoolean("swimming");
        this.crawling = nbt.getBoolean("crawling");
    }

    @Override
    public void copyFrom(IActionCapability oldStore) {
        this.enabledActions.putAll(oldStore.getEnabledActions());
        this.moving = oldStore.isMoving();
        this.lastPosition = oldStore.getLastPosition();
    }
}

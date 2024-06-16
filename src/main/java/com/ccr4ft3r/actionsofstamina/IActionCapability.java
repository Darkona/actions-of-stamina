package com.ccr4ft3r.actionsofstamina;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Optional;

public interface IActionCapability {

    boolean isMoving();

    void setMoving(boolean moving);

    boolean isSprinting();

    void setSprinting(boolean sprinting);

    boolean isJumping();

    void setJumping(boolean jumping);

    boolean isClimbing();

    void setClimbing(boolean climbing);

    boolean isSneaking();

    void setSneaking(boolean sneaking);

    boolean isFlying();

    void setFlying(boolean flying);

    boolean isSwimming();

    void setSwimming(boolean swimming);

    boolean isCrawling();

    void setCrawling(boolean crawling);

    Vec3 getLastPosition();

    void setLastPosition(Vec3 lastPosition);

    Map<ResourceLocation, Action> getEnabledActions();

    void update(Player player);

    void addEnabledAction(Action action);

    Optional<Action> getAction(ResourceLocation actionId);

    CompoundTag saveNBTData();

    void loadNBTData(CompoundTag nbt);

    void copyFrom(IActionCapability oldStore);
}

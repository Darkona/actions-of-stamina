package com.ccr4ft3r.actionsofstamina.capability;

import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionProvider;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.crawl.CrawlAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.shield.ShieldAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.swim.SwimAction;
import com.ccr4ft3r.actionsofstamina.network.ActionStatePacket;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.FakePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AutoRegisterCapability
public class PlayerActions {

    private final Map<String, Action> enabledActions = new HashMap<>();
    private final Map<String, Boolean> actionStates = new HashMap<>();


    private Vec3 lastPosition;
    private boolean moving;

    private boolean crawling;
    private boolean climbing;
    private boolean onVehicle;
    private boolean swimming;
    private boolean sprinting;
    private boolean sneaking;
    private boolean flying;
    private boolean jumping;
    private boolean usingShield;

    public PlayerActions() {
    }

    public static boolean cannotBeExhausted(Player player) {
        return player == null || player.isCreative() || player.isSpectator() || player instanceof FakePlayer;
    }

    public void update(Player player) {
        if (player == null) {
            return;
        }
        /*boolean isMoving =
        boolean isCrawling = isMoving && !player.isInLava() && !player.isInWater() && player.getPose() == Pose.SWIMMING;
        boolean isClimbing = player.onClimbable() && isMoving;
        boolean isOnVehicle = player.getVehicle() != null;
        boolean isSwimming = player.isInWater() && !isOnVehicle && !isClimbing && isMoving;
        boolean isSprinting = player.isSprinting() && isMoving&& !isOnVehicle && player.onGround();
        boolean isSneaking = player.isCrouching() && !isClimbing && isMoving;
        boolean isFlying = player.getPose() == Pose.FALL_FLYING || player.getAbilities().flying;*/

        var moving = player.position().x != lastPosition.x || player.position().z != lastPosition.z;
        var crawling = player.onGround() && player.getPose() == Pose.SWIMMING;
        var climbing = player.onClimbable() && moving;
        var onVehicle = player.getVehicle() != null;
        var swimming = player.isSwimming();
        var sprinting = player.isSprinting() && moving && !onVehicle && player.onGround();
        var sneaking = player.isShiftKeyDown();
        var flying = player.isFallFlying() || player.getAbilities().flying;
        var usingShield = player.getUseItem().getItem() == Items.SHIELD;

        if (player.level().isClientSide) {

            if (moving != this.moving) {
                this.moving = moving;
                PacketHandler.sendToServer(new ActionStatePacket("moving", this.moving));
            }

            if (sprinting != this.sprinting) {
                setActionState(SprintAction.actionName, sprinting);
                this.sprinting = getAction(SprintAction.actionName)
                        .map(a -> a.canPerform(player)).orElse(false);
            }

            if (crawling != this.crawling) {
                this.crawling = crawling;
                setActionState(CrawlAction.actionName, crawling);
            }

            if (flying != this.flying) {
                this.flying = flying;
                setActionState(ElytraAction.actionName, flying);
            }

            if (swimming != this.swimming) {
                setActionState(SwimAction.actionName, swimming);
                this.swimming = getAction(SwimAction.actionName).map(Action::isPerforming).orElse(false);
            }

            if (usingShield != this.usingShield) {
                this.usingShield = usingShield;
                setActionState(ShieldAction.actionName, usingShield);
            }
        }

       /* if (player.tickCount % 20 == 0)
            ActionsOfStamina.sideLog(player,
                    "PlayerEventHandler::playerTickEvent ->  moving: {}, crawling: {}, climbing: {}, onVehicle: {}, swimming: {}, sprinting: {}, sneaking: {}, flying: {}",
                    moving, crawling, climbing, onVehicle, swimming, sprinting, sneaking, flying);*/

        setLastPosition(player.position());
    }

    public Map<String, Boolean> getActionStates() {
        return actionStates;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
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


    public boolean isSprinting() {
        return sprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public boolean isCrawling() {
        return crawling;
    }

    public void setCrawling(boolean crawling) {
        this.crawling = crawling;
    }

    public boolean isClimbing() {
        return climbing;
    }

    public void setClimbing(boolean climbing) {
        this.climbing = climbing;
    }

    public boolean isOnVehicle() {
        return onVehicle;
    }

    public void setOnVehicle(boolean onVehicle) {
        this.onVehicle = onVehicle;
    }

    public boolean isSwimming() {
        return swimming;
    }

    public void setSwimming(boolean swimming) {
        this.swimming = swimming;
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public boolean isPerforming(String actionId) {
        return actionStates.getOrDefault(actionId, false);
    }

    public void setActionState(String actionId, boolean state) {
        getAction(actionId).ifPresent(a -> a.setActionState(state));
        PacketHandler.sendToServer(new ActionStatePacket(actionId, state));
    }

    public void toggleActionState(String actionId) {
        setActionState(actionId, !isPerforming(actionId));
    }
}

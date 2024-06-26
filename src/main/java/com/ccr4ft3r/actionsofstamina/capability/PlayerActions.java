package com.ccr4ft3r.actionsofstamina.capability;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionProvider;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.crawl.CrawlAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.shield.ShieldAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint.SprintAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.swim.SwimAction;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParaglideAction;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.ccr4ft3r.actionsofstamina.network.ActionStatePacket;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import com.ccr4ft3r.actionsofstamina.util.ByteFlag;
import com.darkona.feathers.api.FeathersAPI;
import com.darkona.feathers.effect.FeathersEffects;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.FakePlayer;
import tictim.paraglider.api.movement.ParagliderPlayerStates;
import tictim.paraglider.forge.capability.PlayerMovementProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@AutoRegisterCapability
public class PlayerActions {

    private final Map<String, Action> enabledActions = new HashMap<>();
    private final Map<String, Boolean> actionStates = new HashMap<>();


    private Vec3 lastPosition;

    private boolean moveKeyPressed;

    public void setMoveKeyPressed(boolean moveKeyPressed) {
        this.moveKeyPressed = moveKeyPressed;
    }

    private boolean moving;
    private boolean crawling;
    private boolean swimming;
    private boolean sprinting;
    private boolean elytra;
    private boolean jumping;
    private boolean usingShield;
    private boolean paragliding;

    private boolean changed;
    private boolean inhibitRegen = false;

    public enum A {
        MOVING,
        SPRINTING,
        CRAWLING,
        SWIMMING,
        ELYTRA,
        HOLDING_SHIELD,
        CLIMBING,
        PARAGLIDING
    }

    public PlayerActions() {
    }

    public static boolean isNotExhaustable(Player player) {
        return player == null || player.isCreative() || player.isSpectator() || player instanceof FakePlayer;
    }

    private void updateClient(LocalPlayer player) {
        if (lastPosition == null) lastPosition = player.position();
        var moving = moveKeyPressed && player.position().x != lastPosition.x || player.position().z != lastPosition.z;
        var crawling = player.onGround() && player.getPose() == Pose.SWIMMING && moving && !player.isInWater() && !player.isInLava();
        var climbing = player.onClimbable() && moving;
        var onVehicle = player.getVehicle() != null;
        var swimming = player.isSwimming() && player.getPose() == Pose.SWIMMING && (player.isInWater() || player.isInLava()) && !climbing && !onVehicle;
        var sprinting = player.isSprinting() && moving && !onVehicle && player.onGround();
        var sneaking = player.isCrouching() && !climbing && moving;
        var flying = player.getPose() == Pose.FALL_FLYING && player.isFallFlying() || player.getAbilities().flying;
        var usingShield = player.getUseItem().getItem() == Items.SHIELD;

        ByteFlag flags = new ByteFlag();

        if (moving != this.moving) {
            this.moving = moving;
            flags.set(A.MOVING, moving);
            changed = true;
        }

        if (AoSCommonConfig.SPRINTING_ENABLED.get() && sprinting != this.sprinting) {
            this.sprinting = sprinting;
            flags.set(A.SPRINTING, moving);
            changed = true;
        }

        if (AoSCommonConfig.CRAWLING_ENABLED.get() && crawling != this.crawling) {
            this.crawling = crawling;
            flags.set(A.CRAWLING, crawling);
            changed = true;
        }

        if (AoSCommonConfig.FLYING_ENABLED.get() && flying != this.elytra) {
            this.elytra = flying;
            flags.set(A.ELYTRA, flying);
            changed = true;
        }

        if (AoSCommonConfig.SWIMMING_ENABLED.get() && swimming != this.swimming) {
            this.swimming = swimming;
            flags.set(A.SWIMMING, swimming);
            changed = true;
        }

        if (AoSCommonConfig.HOLD_SHIELD_ENABLED.get() && usingShield != this.usingShield) {
            this.usingShield = usingShield;
            flags.set(A.HOLDING_SHIELD, usingShield);
            changed = true;
        }

        if (ActionsOfStamina.PARAGLIDER && ParagliderConfig.PARAGLIDING_ENABLED.get()) {
            boolean paragliding = player.getCapability(PlayerMovementProvider.PLAYER_MOVEMENT)
                                        .map(p -> p.state().flags().contains(ParagliderPlayerStates.PARAGLIDING))

                                        .orElse(false);

            if (paragliding != this.paragliding) {
                this.paragliding = paragliding;
                flags.set(A.PARAGLIDING, paragliding);
                changed = true;
            }
        }

        if (changed) {
            ActionsOfStamina.sideLog(player, "Change detected! Moving: {}, Sprinting: {}, Crawling: {}, Flying: {}, Swimming: {}, Shield: {}",
                    moving, sprinting, crawling, flying, swimming, usingShield);
            PacketHandler.sendToServer(new ActionStatePacket(flags.getFlag()));
        }
    }

    public void tick(Player player) {
        if (player == null) return;

        if (player instanceof LocalPlayer localPlayer)
            updateClient(localPlayer);

        if (changed) {
            setActionState(SprintAction.actionName, sprinting);
            setActionState(CrawlAction.actionName, crawling);
            setActionState(ElytraAction.actionName, elytra);
            setActionState(SwimAction.actionName, swimming);
            setActionState(ShieldAction.actionName, usingShield);
            setActionState(ParaglideAction.actionName, paragliding);
            changed = false;
        }

        var doRegen = new AtomicBoolean(false);
        var doCooldown = new AtomicBoolean(true);

        enabledActions.forEach((actionName, action) -> {

            if (action != null) {
                action.tick(player, this);
                if (action.isRegenInhibitor() && action.isPerforming()) {
                    doRegen.set(true);
                }
                if (action.isInhibitingCooldown()) {
                    doCooldown.set(false);
                }

            } else {
                ActionsOfStamina.sideLog(player, "Action was null {}", actionName);
            }
        });
        this.setInhibitRegen(doRegen.get());

        if(!player.hasEffect(FeathersEffects.ENERGIZED.get())){
            if (doCooldown.get()) {
                FeathersAPI.enableCooldown(player);
            } else {
                FeathersAPI.disableCooldown(player);

            }
        }


        setLastPosition(player.position());
    }

    public void processFlags(ByteFlag flagByte) {
        this.moving = flagByte.get(A.MOVING);
        this.sprinting = flagByte.get(A.SPRINTING);
        this.crawling = flagByte.get(A.CRAWLING);
        this.elytra = flagByte.get(A.ELYTRA);
        this.swimming = flagByte.get(A.SWIMMING);
        this.usingShield = flagByte.get(A.HOLDING_SHIELD);
        changed = true;
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

    public void setLastPosition(Vec3 lastPosition) {
        this.lastPosition = lastPosition;
    }

    public Map<String, Action> getEnabledActions() {
        return enabledActions;
    }

    public void addEnabledAction(Action action) {
        if (!enabledActions.containsKey(action.name())) {
            enabledActions.put(action.name(), action);
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
            this.enabledActions.put(actionName, ActionProvider.getInstance().getActionByName(actionName, actionsTag.getCompound(actionName)));
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

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public boolean isJumping() {
        return jumping;
    }

    public boolean isPerforming(String actionId) {
        return actionStates.getOrDefault(actionId, false);
    }

    public void setActionState(String actionId, boolean state) {
        getAction(actionId).ifPresent(a -> a.setActionState(state));
    }

    public void setInhibitRegen(boolean state) {
        this.inhibitRegen = state;
    }

    public boolean isInhibitRegen() {
        return inhibitRegen;
    }
}

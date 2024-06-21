package com.ccr4ft3r.actionsofstamina.actions.minecraft.crawl;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.ccr4ft3r.actionsofstamina.network.ActionStatePacket;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import com.darkona.feathers.api.FeathersAPI;
import com.darkona.feathers.api.StaminaAPI;
import com.darkona.feathers.util.Calculations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

import java.util.UUID;

public class CrawlAction implements Action {

    public static final String actionName = "crawl_action";
    public static final ResourceLocation name = new ResourceLocation(ActionsOfStamina.MOD_ID, actionName);
    private static final UUID CRAWL_SPEED_MODIFIER = UUID.fromString("f6975f3a-1834-4a1b-a7ed-d8519df974f8");

    private final int cost;
    private final int minCost;
    private final int cooldown;
    private final int staminaPerTick;
    private final boolean regenInhibitor;
    private final AttributeModifier crawlSpeedModifier = new AttributeModifier(CRAWL_SPEED_MODIFIER, "Crawling Speed Modifier", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private boolean performing = false;
    private boolean actionState = false;
    private String debugInfo;
    private final double feathersPerSecond;
    private boolean wasCrawling = false;

    public CrawlAction() {
        this.cost = AoSCommonConfig.CRAWLING_COST.get();
        this.minCost = AoSCommonConfig.CRAWLING_MINIMUM_COST.get();
        this.cooldown = AoSCommonConfig.CRAWLING_COOLDOWN.get();
        this.staminaPerTick = Calculations.calculateStaminaPerTick(AoSCommonConfig.CRAWLING_FEATHERS_PER_SECOND.get());
        this.feathersPerSecond = AoSCommonConfig.CRAWLING_FEATHERS_PER_SECOND.get();
        this.regenInhibitor = AoSCommonConfig.INHIBIT_REGEN_WHEN_CRAWLING.get();
    }

    @Override
    public String getName() {
        return actionName;
    }

    @Override
    public String getDebugString() {
        return debugInfo;
    }

    @Override
    public boolean canPerform(Player player) {
        if (PlayerActions.cannotBeExhausted(player)) return true;

        if (performing) {
            return StaminaAPI.canUseStamina(player, staminaPerTick);
        } else {
            return FeathersAPI.canSpendFeathers(player, minCost);
        }

    }

    @Override
    public boolean wasPerforming() {
        return wasCrawling;
    }

    @Override
    public double getFeathersPerSecond() {
        return feathersPerSecond;
    }

    @Override
    public boolean isRegenInhibitor() {
        return regenInhibitor;
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public int getMinCost() {
        return minCost;
    }

    @Override
    public int getTimesPerformedToExhaust() {
        return 0;
    }

    @Override
    public boolean isPerforming() {
        return performing;
    }

    @Override
    public void setPerforming(boolean performing) {
        this.performing = performing;
    }

    @Override
    public boolean perform(Player p) {
        return false;
    }

    @Override
    public int timesPerformed() {
        return 0;
    }

    @Override
    public int getLastPerformed() {
        return 0;
    }

    @Override
    public void tick(Player player, PlayerActions capability, TickEvent.Phase phase) {

        if (phase == TickEvent.Phase.END) {
            boolean perform = capability.isMoving() && actionState && StaminaAPI.useStamina(player, staminaPerTick);
            if (wasPerforming() && !perform) {
                finishPerforming(player);
            } else if (!wasPerforming() && perform) {
                beginPerforming(player);
            }

            var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attr != null) {
                if (performing && !canPerform(player)) {
                    if (!attr.hasModifier(crawlSpeedModifier))
                        attr.addPermanentModifier(crawlSpeedModifier);
                } else if (!performing && canPerform(player)) {
                    if (attr.hasModifier(crawlSpeedModifier))
                        attr.removeModifier(crawlSpeedModifier);
                }
            }
            debugInfo = String.format("%s: Performing: %s, ActionState: %s, PlayerCrawling: %s", actionName, performing, actionState, capability.isCrawling());
            wasCrawling = performing;
        }
    }

    @Override
    public void beginPerforming(Player player) {
        ActionsOfStamina.sideLog(player, "SprintAction::beginPerforming");
        if (regenInhibitor) FeathersAPI.disableCooldown(player);
        performing = true;
        if (player.level().isClientSide) {
            PacketHandler.sendToServer(new ActionStatePacket(actionName, actionState));
        } else {
            FeathersAPI.spendFeathers(player, 0, 0);
        }
    }

    @Override
    public void finishPerforming(Player player) {
        ActionsOfStamina.sideLog(player, "SprintAction::finishPerforming");
        if (regenInhibitor) FeathersAPI.enableCooldown(player);
        performing = false;
        if (player.level().isClientSide) {
            PacketHandler.sendToServer(new ActionStatePacket(actionName, actionState));
        } else {
            FeathersAPI.spendFeathers(player, 0, cooldown);
        }
    }

    @Override
    public CompoundTag saveNBTData() {
        var nbt = new CompoundTag();
        nbt.putBoolean("performing", performing);
        nbt.putBoolean("actionState", actionState);
        return nbt;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        performing = nbt.getBoolean("performing");
        actionState = nbt.getBoolean("actionState");
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public int getStaminaCostPerTick() {
        return staminaPerTick;
    }

    @Override
    public void setActionState(boolean state) {
        actionState = state;
    }
}

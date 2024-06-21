package com.ccr4ft3r.actionsofstamina.actions.minecraft.jump;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.darkona.feathers.api.FeathersAPI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

public class JumpAction implements Action {

    public static final String actionName = "jump_action";
    public static final ResourceLocation name = new ResourceLocation(ActionsOfStamina.MOD_ID, actionName);

    private final int minCost;
    private final int cost;
    private final int cooldown;
    private final int timesPerformedToExhaust;
    private int timesPerformed;
    private int lastPerformed;

    public JumpAction() {
        minCost = AoSCommonConfig.JUMPING_COST.get();
        cost = AoSCommonConfig.JUMPING_MINIMUM_COST.get();
        cooldown = AoSCommonConfig.JUMPING_COOLDOWN.get();
        timesPerformedToExhaust = AoSCommonConfig.JUMPING_TIMES_PERFORMED_TO_EXHAUST.get();
    }

    @Override
    public String getName() {
        return actionName;
    }

    @Override
    public String getDebugString() {
        return String.format("%s: Performed %d times", actionName, timesPerformed);
    }

    @Override
    public boolean canPerform(Player p) {
        return !PlayerActions.cannotBeExhausted(p) && FeathersAPI.canSpendFeathers(p, minCost);
    }

    @Override
    public boolean wasPerforming() {
        return false;
    }

    @Override
    public double getFeathersPerSecond() {
        return 0;
    }

    @Override
    public boolean isRegenInhibitor() {
        return false;
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public int getMinCost() {
        return 0;
    }

    @Override
    public int getTimesPerformedToExhaust() {
        return 0;
    }

    @Override
    public boolean isPerforming() {
        return false;
    }

    @Override
    public void setPerforming(boolean performing) {

    }

    @Override
    public void beginPerforming(Player p) {

    }

    @Override
    public void finishPerforming(Player p) {

    }

    @Override
    public boolean perform(Player player) {

        ActionsOfStamina.sideLog(player, "Performing jump action for player");
        if (!canPerform(player)) return false;
        if (++timesPerformed == timesPerformedToExhaust) {
            timesPerformed = 0;
            boolean allow = canPerform(player);
            if (!player.level().isClientSide) {
                allow = FeathersAPI.spendFeathers(player, cost, cooldown);
            }
            if (allow) lastPerformed = player.tickCount;
            ActionsOfStamina.log("Jump action allowed = {}, cost= {}", allow, cost);
            return allow;
        }
        return true;
    }

    @Override
    public int timesPerformed() {
        return timesPerformed;
    }

    @Override
    public int getLastPerformed() {
        return lastPerformed;
    }

    @Override
    public void tick(Player p, PlayerActions capability, TickEvent.Phase phase) {

    }

    @Override
    public CompoundTag saveNBTData() {
        return new CompoundTag();
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {

    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public int getStaminaCostPerTick() {
        return 0;
    }

    @Override
    public void setActionState(boolean state) {

    }
}

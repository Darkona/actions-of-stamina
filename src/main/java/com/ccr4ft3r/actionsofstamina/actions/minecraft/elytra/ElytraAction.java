package com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Fireworks;

public class ElytraAction extends Action {

    public static final TagKey<Item> STAMINA_WINGS = ItemTags.create(ActionsOfStamina.id("stamina_wings"));

    private static final int ROCKET_LIFETIME_SPREAD = 11;

    private final boolean rocketBoostCosts;
    private int boostEndTick;

    public ElytraAction(ActionType type) {
        super(type);
        this.rocketBoostCosts = AoSServerConfig.ROCKET_BOOST_COSTS.getAsBoolean();
    }

    public static boolean wearsStaminaWings(Player player) {
        return player.getItemBySlot(EquipmentSlot.CHEST).is(STAMINA_WINGS) || PlayerActions.get(player).wearsCurioWings();
    }

    public void boost(Player player, ItemStack rocket) {
        if (rocketBoostCosts) return;
        Fireworks fireworks = rocket.get(DataComponents.FIREWORKS);
        int flight = fireworks == null ? 0 : fireworks.flightDuration();
        boostEndTick = player.tickCount + 10 * (1 + flight) + ROCKET_LIFETIME_SPREAD;
    }

    @Override
    protected double drainPerTick(Player player) {
        return player.tickCount < boostEndTick ? 0 : staminaPerTick;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        if (player.isFallFlying() && wearsStaminaWings(player)) player.stopFallFlying();
    }
}

package com.ccr4ft3r.actionsofstamina.content.actions;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class ElytraAction extends Action {

    public static final TagKey<Item> STAMINA_WINGS = ItemTags.create(ActionsOfStamina.id("stamina_wings"));

    private static final int ROCKET_LIFETIME_SPREAD = 11;

    private final boolean rocketBoostCosts;
    private int boostEndTick;

    public ElytraAction(ActionType type) {
        super(type);
        this.rocketBoostCosts = ActionsConfig.ROCKET_BOOST_COSTS.get();
    }

    public static boolean wearsStaminaWings(Player player) {
        return player.getItemBySlot(EquipmentSlot.CHEST).is(STAMINA_WINGS) || PlayerActions.get(player).wearsCurioWings();
    }

    public void boost(Player player, ItemStack rocket) {
        if (rocketBoostCosts) return;
        CompoundTag fireworks = rocket.getTagElement("Fireworks");
        int flight = fireworks == null ? 0 : fireworks.getByte("Flight");
        boostEndTick = player.tickCount + 10 * (1 + flight) + ROCKET_LIFETIME_SPREAD;
    }

    @Override
    public void onUseItem(Player player, PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().getItem() instanceof FireworkRocketItem && player.isFallFlying()) boost(player, event.getItemStack());
    }

    @Override
    protected double drainPerTick(Player player) {
        return player.tickCount < boostEndTick ? 0 : staminaPerTick;
    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        if (player.isFallFlying() && wearsStaminaWings(player)) player.stopFallFlying();
    }
}

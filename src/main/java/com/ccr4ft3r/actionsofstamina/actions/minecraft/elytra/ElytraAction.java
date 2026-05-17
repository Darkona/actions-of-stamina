package com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Fireworks;

/**
 * Gliding with wings from {@link #STAMINA_WINGS} worn in the chest slot or, with Curios, in a curio slot. Wings outside the tag (mechanical or
 * propelled ones from other mods) fly for free unless a datapack adds them.
 */
public class ElytraAction extends Action {

    public static final String actionName = "elytra_action";
    public static final ResourceLocation SOURCE = ActionsOfStamina.id("elytra");
    /** Wings that cost stamina to fly with; the mod's own tag file holds the elytra. */
    public static final TagKey<Item> STAMINA_WINGS = ItemTags.create(ActionsOfStamina.id("stamina_wings"));

    /** A boosting rocket lives {@code 10 * (1 + flight duration)} ticks plus up to this many more. */
    private static final int ROCKET_LIFETIME_SPREAD = 11;

    private final boolean rocketBoostCosts;
    /** {@code player.tickCount} at which the last rocket boost ends. */
    private int boostEndTick;

    public ElytraAction() {
        super(SOURCE, AoSServerConfig.ELYTRA);
        this.rocketBoostCosts = AoSServerConfig.ROCKET_BOOST_COSTS.getAsBoolean();
    }

    /** Whether the player wears wings that cost stamina, in the chest slot or in a curio slot (cached). */
    public static boolean wearsStaminaWings(Player player) {
        return player.getItemBySlot(EquipmentSlot.CHEST).is(STAMINA_WINGS) || PlayerActions.get(player).wearsCurioWings();
    }

    /**
     * A firework rocket used while fall-flying (both sides): flight is free while it boosts, unless
     * {@code rocket_boost_costs}. The rocket's own lifetime is random, so the longest one is assumed.
     */
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
    public String name() {
        return actionName;
    }

    @Override
    public int id() {
        return ELYTRA;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    /** Out of stamina in the air: the wings fold (starting again is refused by {@code PlayerMixin}). */
    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {
        if (player.isFallFlying() && wearsStaminaWings(player)) player.stopFallFlying();
    }
}

package com.ccr4ft3r.actionsofstamina.compat.feathers;

import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.darkona.feathersoffatigue.api.DrainOptions;
import com.darkona.feathersoffatigue.api.FeathersAPI;
import com.darkona.feathersoffatigue.api.SpendOptions;
import com.darkona.feathersoffatigue.api.registry.FeathersAttributes;
import com.darkona.feathersoffatigue.api.registry.FeathersMobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public final class FeathersBackend implements StaminaBackend {

    private static final SpendOptions SIMULATED = SpendOptions.DEFAULT.simulated();
    private static final DrainOptions DRAIN_BLOCKING_REGEN = DrainOptions.DEFAULT;
    private static final DrainOptions DRAIN_KEEPING_REGEN = DrainOptions.DEFAULT.keepingRegen();
    private static final int CACHED_DELAYS = 256;
    private static final SpendOptions[] SPEND_BY_DELAY = new SpendOptions[CACHED_DELAYS];

    private FeathersBackend() {
    }

    public static StaminaBackend create() {
        return new FeathersBackend();
    }

    public static void takeOverPlayerActions() {
        FeathersAPI.takeOverPlayerActions(ActionsOfStamina.MOD_ID);
    }

    private static SpendOptions spendOptions(int regenDelayTicks) {
        if (regenDelayTicks < 0 || regenDelayTicks >= CACHED_DELAYS) return SpendOptions.DEFAULT.withRegenDelay(regenDelayTicks);
        SpendOptions options = SPEND_BY_DELAY[regenDelayTicks];
        if (options == null) SPEND_BY_DELAY[regenDelayTicks] = options = SpendOptions.DEFAULT.withRegenDelay(regenDelayTicks);
        return options;
    }

    @Override
    public Kind kind() {
        return Kind.FEATHERS;
    }

    @Override
    public boolean canSpend(Player player, ResourceLocation source, int stamina) {
        return FeathersAPI.spend(player, source, stamina, SIMULATED).allowed();
    }

    @Override
    public boolean spend(Player player, ResourceLocation source, int stamina, int regenDelayTicks) {
        return FeathersAPI.spend(player, source, stamina, spendOptions(regenDelayTicks)).allowed();
    }

    @Override
    public boolean drain(Player player, ResourceLocation source, double staminaPerTick, boolean blocksRegen) {
        return FeathersAPI.startDrain(player, source, staminaPerTick, blocksRegen ? DRAIN_BLOCKING_REGEN : DRAIN_KEEPING_REGEN).allowed();
    }

    @Override
    public void stopDrain(Player player, ResourceLocation source) {
        FeathersAPI.stopDrain(player, source);
    }

    @Override
    public void blockRegen(Player player, ResourceLocation source, int ticks) {
        FeathersAPI.blockRegen(player, source, ticks);
    }

    @Override
    public boolean keepsRegenWhileActing(Player player) {
        return player.hasEffect(FeathersMobEffects.ENERGIZED);
    }

    @Override
    public void setMaxBonus(Player player, ResourceLocation source, int stamina) {
        AttributeInstance maxFeathers = player.getAttribute(FeathersAttributes.MAX_FEATHERS);
        if (maxFeathers == null) return;
        if (stamina <= 0) {
            maxFeathers.removeModifier(source);
            return;
        }
        double feathers = stamina / (double) StaminaBackend.PER_FEATHER;
        AttributeModifier current = maxFeathers.getModifier(source);
        if (current != null && current.amount() == feathers) return;
        maxFeathers.addOrUpdateTransientModifier(new AttributeModifier(source, feathers, AttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public int stamina(Player player) {
        return FeathersAPI.get(player).stamina();
    }

    @Override
    public int maxStamina(Player player) {
        return FeathersAPI.get(player).maxStamina();
    }

    @Override
    public int availableStamina(Player player) {
        return FeathersAPI.get(player).availableStamina();
    }

    @Override
    public boolean exhausted(Player player) {
        return FeathersAPI.get(player).exhausted();
    }
}

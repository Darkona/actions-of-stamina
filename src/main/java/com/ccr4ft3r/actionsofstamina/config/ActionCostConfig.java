package com.ccr4ft3r.actionsofstamina.config;

import com.ccr4ft3r.actionsofstamina.stamina.StaminaUnits;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

/**
 * The config subsection of one stamina-costing action: a toggle plus whichever costs apply to it. Costs are in
 * feathers (a 20-feather bar by default), delays in ticks. Getters return stamina units and read cached config
 * values, so they are cheap enough for per-tick use.
 */
public final class ActionCostConfig {

    public static final double MAX_FEATHERS = 1000.0;

    private final ForgeConfigSpec.BooleanValue enabled;
    @Nullable private final ForgeConfigSpec.DoubleValue cost;
    @Nullable private final ForgeConfigSpec.DoubleValue minStamina;
    @Nullable private final ForgeConfigSpec.DoubleValue perSecond;
    @Nullable private final ForgeConfigSpec.DoubleValue finishCost;
    @Nullable private final ForgeConfigSpec.IntValue regenDelay;
    @Nullable private final ForgeConfigSpec.BooleanValue blocksRegen;
    @Nullable private final ForgeConfigSpec.IntValue timesToCharge;

    private ActionCostConfig(Builder b) {
        this.enabled = b.enabled;
        this.cost = b.cost;
        this.minStamina = b.minStamina;
        this.perSecond = b.perSecond;
        this.finishCost = b.finishCost;
        this.regenDelay = b.regenDelay;
        this.blocksRegen = b.blocksRegen;
        this.timesToCharge = b.timesToCharge;
    }

    /** Pushes {@code path}; define the fields in order, then {@link Builder#build()} pops it. */
    public static Builder builder(ForgeConfigSpec.Builder spec, String path, String description, boolean enabledByDefault) {
        return new Builder(spec, path, description, enabledByDefault);
    }

    public boolean enabled() {
        return enabled.get();
    }

    /** One-off cost: per use, or when a continuous action starts. */
    public int cost() {
        return cost == null ? 0 : StaminaUnits.ofFeathers(cost.get());
    }

    /** Stamina needed to begin; at least the one-off cost. */
    public int minStamina() {
        return Math.max(cost(), minStamina == null ? 0 : StaminaUnits.ofFeathers(minStamina.get()));
    }

    public double perTick() {
        return perSecond == null ? 0 : StaminaUnits.perTick(perSecond.get());
    }

    public double perSecondFeathers() {
        return perSecond == null ? 0 : perSecond.get();
    }

    public int finishCost() {
        return finishCost == null ? 0 : StaminaUnits.ofFeathers(finishCost.get());
    }

    public int regenDelay() {
        return regenDelay == null ? 0 : regenDelay.get();
    }

    public boolean blocksRegen() {
        return blocksRegen != null && blocksRegen.get();
    }

    public int timesToCharge() {
        return timesToCharge == null ? 1 : timesToCharge.get();
    }

    /** Whether this action costs anything at all. */
    public boolean costsAnything() {
        return cost() > 0 || perTick() > 0 || finishCost() > 0;
    }

    public static final class Builder {
        private final ForgeConfigSpec.Builder spec;
        private final ForgeConfigSpec.BooleanValue enabled;
        private ForgeConfigSpec.DoubleValue cost;
        private ForgeConfigSpec.DoubleValue minStamina;
        private ForgeConfigSpec.DoubleValue perSecond;
        private ForgeConfigSpec.DoubleValue finishCost;
        private ForgeConfigSpec.IntValue regenDelay;
        private ForgeConfigSpec.BooleanValue blocksRegen;
        private ForgeConfigSpec.IntValue timesToCharge;

        private Builder(ForgeConfigSpec.Builder spec, String path, String description, boolean enabledByDefault) {
            this.spec = spec;
            spec.comment(description).push(path);
            this.enabled = spec.comment("Whether this action costs stamina.").define("enabled", enabledByDefault);
        }

        public Builder cost(double feathers, String comment) {
            cost = spec.comment(comment + " (feathers)").defineInRange("cost", feathers, 0.0, MAX_FEATHERS);
            return this;
        }

        public Builder minStamina(double feathers) {
            minStamina = spec.comment("Feathers needed to begin (at least the cost)")
                    .defineInRange("min_stamina", feathers, 0.0, MAX_FEATHERS);
            return this;
        }

        public Builder perSecond(double feathers) {
            perSecond = spec.comment("Feathers drained per second while it lasts")
                    .defineInRange("per_second", feathers, 0.0, MAX_FEATHERS);
            return this;
        }

        public Builder finishCost(double feathers) {
            finishCost = spec.comment("Feathers charged when it ends")
                    .defineInRange("finish_cost", feathers, 0.0, MAX_FEATHERS);
            return this;
        }

        public Builder regenDelay(int ticks) {
            regenDelay = spec.comment("Ticks without regeneration after it (20 ticks = 1 second)")
                    .defineInRange("regen_delay", ticks, 0, 1200);
            return this;
        }

        public Builder blocksRegen(boolean blocks) {
            blocksRegen = spec.comment("Whether regeneration pauses while it lasts")
                    .define("blocks_regen", blocks);
            return this;
        }

        public Builder timesToCharge(int times) {
            timesToCharge = spec.comment("Charge the cost once every this many uses")
                    .defineInRange("times_to_charge", times, 1, 100);
            return this;
        }

        /** Room for action-specific options before {@link #build()}. */
        public ForgeConfigSpec.Builder spec() {
            return spec;
        }

        public ActionCostConfig build() {
            spec.pop();
            return new ActionCostConfig(this);
        }
    }
}

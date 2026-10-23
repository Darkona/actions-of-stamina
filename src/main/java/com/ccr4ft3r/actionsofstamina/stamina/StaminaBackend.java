package com.ccr4ft3r.actionsofstamina.stamina;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface StaminaBackend {

    int PER_FEATHER = 1000;

    static int ofFeathers(double feathers) {
        return (int) Math.round(feathers * PER_FEATHER);
    }

    static double perTick(double feathersPerSecond) {
        return feathersPerSecond * PER_FEATHER / 20.0;
    }

    enum BackendMode {
        AUTO,
        FEATHERS,
        INTERNAL
    }

    enum Kind {
        FEATHERS,
        INTERNAL;

        private static final Kind[] VALUES = values();

        public static Kind byId(int id) {
            return id >= 0 && id < VALUES.length ? VALUES[id] : INTERNAL;
        }
    }

    Kind kind();

    boolean canSpend(Player player, ResourceLocation source, int stamina);

    boolean spend(Player player, ResourceLocation source, int stamina, int regenDelayTicks);

    boolean drain(Player player, ResourceLocation source, double staminaPerTick, boolean blocksRegen);

    void stopDrain(Player player, ResourceLocation source);

    void blockRegen(Player player, ResourceLocation source, int ticks);

    boolean keepsRegenWhileActing(Player player);

    void setMaxBonus(Player player, ResourceLocation source, int stamina);

    int stamina(Player player);

    int maxStamina(Player player);

    int availableStamina(Player player);

    boolean exhausted(Player player);
}

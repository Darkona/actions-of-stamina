package com.ccr4ft3r.actionsofstamina.stamina;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Where AoS takes stamina from: Green Feathers when it is installed ({@link Kind#FEATHERS}), otherwise AoS's own
 * light stamina bar ({@link Kind#INTERNAL}). Pick one with {@link StaminaBackends#of(Player)}.
 * <p>
 * <b>Units.</b> Every amount is stamina, a thousandth of a feather ({@link StaminaUnits}).
 * <p>
 * <b>Sides.</b> The server is authoritative. On the client, spends and drains of the local player are only checked
 * (and spends predicted) against the last synced values; other players are never charged client side.
 * <p>
 * <b>Sources.</b> Every cost names its source, one constant {@link ResourceLocation} per action: a drain is refreshed
 * and stopped by the same source, and regeneration blocks of different sources don't undo each other.
 * <p>
 * Creative and spectator players are exempt: every check and spend succeeds without cost.
 */
public interface StaminaBackend {

    enum Kind {
        FEATHERS,
        INTERNAL;

        private static final Kind[] VALUES = values();

        public static Kind byId(int id) {
            return id >= 0 && id < VALUES.length ? VALUES[id] : INTERNAL;
        }
    }

    Kind kind();

    /** Whether {@code stamina} could be spent now, without spending it. */
    boolean canSpend(Player player, ResourceLocation source, int stamina);

    /**
     * Spends {@code stamina} once, all or nothing, then pauses regeneration for {@code regenDelayTicks}.
     *
     * @return whether the action may go ahead
     */
    boolean spend(Player player, ResourceLocation source, int stamina, int regenDelayTicks);

    /**
     * Starts or refreshes a continuous drain and pays this tick's share; call it every tick while the activity
     * lasts, it stops by itself a few ticks after the last call. Fractions carry over between ticks.
     *
     * @return false when the drain can't be paid any more (it then stops)
     */
    boolean drain(Player player, ResourceLocation source, double staminaPerTick, boolean blocksRegen);

    void stopDrain(Player player, ResourceLocation source);

    /** Pauses regeneration for {@code ticks}, e.g. after a continuous action ends. */
    void blockRegen(Player player, ResourceLocation source, int ticks);

    /** Whether an effect (Green Feathers' Energized) lets the player keep regenerating while acting. */
    boolean keepsRegenWhileActing(Player player);

    int stamina(Player player);

    int maxStamina(Player player);

    /** Stamina that can be spent right now. */
    int availableStamina(Player player);

    /** Spent everything and must recover part of the bar before acting again. */
    boolean exhausted(Player player);
}

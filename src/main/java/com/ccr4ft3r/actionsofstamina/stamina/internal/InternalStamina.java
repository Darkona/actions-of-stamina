package com.ccr4ft3r.actionsofstamina.stamina.internal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

/**
 * One player's internal stamina, a field of every player ({@code AosPlayerData}). On the server it is the real bar;
 * on the client it holds what the server last synced (plus local spend predictions). Only the stamina and the
 * exhaustion flag are saved; drains, delays and carries are transient.
 */
public final class InternalStamina {

    /** Key of the saved state in the player's data. */
    public static final String SAVE_KEY = "actionsofstamina:internal_stamina";

    /** Not set yet: the first tick fills the bar. */
    static final int UNSET = -1;

    int stamina;
    /** Server: current max. Client: max as last synced. */
    int maxStamina;
    boolean exhausted;
    /** Server: extra max stamina on top of the configured bar (Paragliders' vessels), transient. */
    int bonusMax;
    int regenDelay;
    double regenCarry;

    /** Active continuous drains, a tiny linear array: allocation free after the first use of each source. */
    Drain[] drains = new Drain[4];
    int drainCount;

    // Last values sent to the client; UNSET forces a sync.
    int syncedBucket = UNSET;
    int syncedMax = UNSET;
    boolean syncedExhausted;

    public InternalStamina() {
        stamina = UNSET;
    }

    /** Saved state; drains and delays start fresh after a load. */
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("stamina", stamina);
        tag.putBoolean("exhausted", exhausted);
        return tag;
    }

    public void load(CompoundTag tag) {
        stamina = tag.contains("stamina") ? tag.getInt("stamina") : UNSET;
        exhausted = tag.getBoolean("exhausted");
    }

    /** Clone of a player that didn't die (returning from the End): the new player keeps the bar. */
    public void copyFrom(InternalStamina other) {
        stamina = other.stamina;
        exhausted = other.exhausted;
    }

    @SuppressWarnings("unused") // for addons
    public int stamina() {
        return Math.max(stamina, 0);
    }

    @SuppressWarnings("unused") // for addons
    public int maxStamina() {
        return maxStamina;
    }

    public boolean exhausted() {
        return exhausted;
    }

    @SuppressWarnings("unused") // for addons
    public int regenDelay() {
        return regenDelay;
    }

    /** Forces a sync on the next tick, e.g. after joining a level. */
    public void markForSync() {
        syncedBucket = UNSET;
    }

    Drain drain(ResourceLocation source) {
        for (int i = 0; i < drainCount; i++) {
            if (drains[i].source.equals(source)) return drains[i];
        }
        if (drainCount == drains.length) {
            Drain[] grown = new Drain[drains.length * 2];
            System.arraycopy(drains, 0, grown, 0, drainCount);
            drains = grown;
        }
        return drains[drainCount++] = new Drain(source);
    }

    /** A continuous drain; kept (inactive) after it stops so restarting the same source allocates nothing. */
    static final class Drain {
        final ResourceLocation source;
        boolean active;
        boolean blocksRegen;
        double carry;
        int lastRefreshTick;

        Drain(ResourceLocation source) {
            this.source = source;
        }
    }
}

package com.ccr4ft3r.actionsofstamina.content.stamina;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public final class InternalStamina {

    public static final String SAVE_KEY = "actionsofstamina:internal_stamina";

    static final int UNSET = -1;

    int stamina;
    int maxStamina;
    boolean exhausted;
    int bonusMax;
    int regenDelay;

    final SourcedDrains<ResourceLocation> drains = new SourcedDrains<>();
    final FractionalCarry regenCarry = new FractionalCarry();
    final ShownFields<Shown> shown = new ShownFields<>(Shown.class);

    public InternalStamina() {
        stamina = UNSET;
    }

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

    public void copyFrom(InternalStamina other) {
        stamina = other.stamina;
        exhausted = other.exhausted;
    }

    @SuppressWarnings("unused")
    public int stamina() {
        return Math.max(stamina, 0);
    }

    @SuppressWarnings("unused")
    public int maxStamina() {
        return maxStamina;
    }

    public boolean exhausted() {
        return exhausted;
    }

    public void receive(int stamina, int maxStamina, boolean exhausted, int regenDelay) {
        this.stamina = stamina;
        this.maxStamina = maxStamina;
        this.exhausted = exhausted;
        this.regenDelay = regenDelay;
    }

    @SuppressWarnings("unused")
    public int regenDelay() {
        return regenDelay;
    }

    public void markForSync() {
        shown.resendNext();
    }

    enum Shown { MAX, EXHAUSTED }
}

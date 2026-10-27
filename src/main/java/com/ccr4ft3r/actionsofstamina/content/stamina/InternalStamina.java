package com.ccr4ft3r.actionsofstamina.content.stamina;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.darkona.jdlib.core.stat.FractionalCarry;
import dev.darkona.jdlib.core.stat.ShownFields;
import dev.darkona.jdlib.core.stat.SourcedDrains;
import net.minecraft.resources.ResourceLocation;

public final class InternalStamina {

    public static final Codec<InternalStamina> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("stamina").forGetter(s -> s.stamina),
            Codec.BOOL.fieldOf("exhausted").forGetter(s -> s.exhausted)
    ).apply(i, InternalStamina::new));

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
        this(UNSET, false);
    }

    private InternalStamina(int stamina, boolean exhausted) {
        this.stamina = stamina;
        this.exhausted = exhausted;
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

    public void receive(int stamina, int maxStamina, boolean exhausted) {
        this.stamina = stamina;
        this.maxStamina = maxStamina;
        this.exhausted = exhausted;
    }

    @SuppressWarnings("unused")
    public int regenDelay() {
        return regenDelay;
    }

    public void markForSync() {
        shown.resendNext();
    }

    enum Shown { BUCKET, MAX, EXHAUSTED }
}

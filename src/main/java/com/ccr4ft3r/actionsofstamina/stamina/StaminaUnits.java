package com.ccr4ft3r.actionsofstamina.stamina;

/**
 * AoS counts stamina in thousandths of a feather on every backend (the same unit Feathers of Fatigue uses), so costs can
 * be fractions of a feather and slow rates keep their precision. Configs are written in feathers and converted here.
 */
public final class StaminaUnits {

    public static final int PER_FEATHER = 1000;

    private StaminaUnits() {
    }

    /** Stamina in {@code feathers} feathers, rounded to the nearest unit. */
    public static int ofFeathers(double feathers) {
        return (int) Math.round(feathers * PER_FEATHER);
    }

    /** Stamina per tick for a rate in feathers per second. */
    public static double perTick(double feathersPerSecond) {
        return feathersPerSecond * PER_FEATHER / 20.0;
    }
}

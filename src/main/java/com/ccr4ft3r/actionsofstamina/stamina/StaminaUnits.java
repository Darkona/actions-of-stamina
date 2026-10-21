package com.ccr4ft3r.actionsofstamina.stamina;

public final class StaminaUnits {

    public static final int PER_FEATHER = 1000;

    private StaminaUnits() {
    }

    public static int ofFeathers(double feathers) {
        return (int) Math.round(feathers * PER_FEATHER);
    }

    public static double perTick(double feathersPerSecond) {
        return feathersPerSecond * PER_FEATHER / 20.0;
    }
}

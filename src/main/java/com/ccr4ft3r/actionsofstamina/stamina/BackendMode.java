package com.ccr4ft3r.actionsofstamina.stamina;

/** The {@code backend} config choice. */
public enum BackendMode {
    /** Green Feathers when it is installed, otherwise AoS's internal stamina. */
    AUTO,
    /** Green Feathers; falls back to the internal stamina (with a warning) when it isn't installed. */
    FEATHERS,
    /** AoS's internal stamina, even when Green Feathers is installed. */
    INTERNAL
}

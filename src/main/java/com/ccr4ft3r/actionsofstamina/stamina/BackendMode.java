package com.ccr4ft3r.actionsofstamina.stamina;

/** The {@code backend} config choice. */
public enum BackendMode {
    /** Feathers of Fatigue when it is installed, otherwise AoS's internal stamina. */
    AUTO,
    /** Feathers of Fatigue; falls back to the internal stamina (with a warning) when it isn't installed. */
    FEATHERS,
    /** AoS's internal stamina, even when Feathers of Fatigue is installed. */
    INTERNAL
}

package com.ccr4ft3r.actionsofstamina.actions.minecraft.attack;

/** The {@code [vanilla.attack] exhausted_mode} config choice: what an attack the player can't afford does. */
public enum ExhaustedAttackMode {
    /** It doesn't happen: no swing, no hit. */
    CANCEL,
    /** It lands, weakened: less attack damage and a slower attack speed while the stamina is short. */
    WEAKEN
}

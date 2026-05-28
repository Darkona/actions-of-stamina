package com.ccr4ft3r.actionsofstamina.data;

import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalStamina;

/**
 * AoS's per-player data, held in fields {@code PlayerMixin} adds to every player: reading it is a plain field access,
 * cheap enough for every tick. {@link PlayerActions} is transient, rebuilt from the config whenever the player joins a
 * level; {@link InternalStamina} is saved with the player (starts full after death, kept on other clones).
 */
public interface AosPlayerData {

    PlayerActions actionsofstamina$actions();

    InternalStamina actionsofstamina$internalStamina();
}

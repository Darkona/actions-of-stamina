package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.handlers.ParCoolStaminaHandler;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.world.entity.player.Player;

/**
 * AoS's stand-in for ParCool's own stamina handler; only reached through {@link ParcoolCompat} when ParCool is loaded.
 * <p>
 * ParCool 3 keeps a stamina handler only for the local player (other players and the server see the read-only copy it
 * syncs), so this lives on the client. While the compat is enabled it shows the AoS backend's stamina and swallows
 * ParCool's own spends and regeneration: AoS charges ParCool's actions through their events instead
 * ({@link ParcoolBridge}), so nothing is charged twice, and ParCool's own HUD stays hidden. With the compat disabled it
 * is ParCool's own handler.
 */
public final class ParcoolStamina implements IParCoolStaminaHandler {

    private final ParCoolStaminaHandler fallback = new ParCoolStaminaHandler();

    /**
     * From the mixin on ParCool's local stamina: ParCool's own handler becomes AoS's; the others (hunger, infinite)
     * stay.
     */
    public static IParCoolStaminaHandler replace(IParCoolStaminaHandler handler) {
        return handler.getClass() == ParCoolStaminaHandler.class ? new ParcoolStamina() : handler;
    }

    private static boolean active() {
        return ParcoolCompat.isActive();
    }

    /** The backend's stamina as ParCool's record; {@code current} itself while nothing changed, so it isn't synced again. */
    private static ReadonlyStamina mirror(Player player, ReadonlyStamina current) {
        StaminaBackend backend = StaminaBackends.of(player);
        int value = backend.stamina(player);
        int max = backend.maxStamina(player);
        boolean exhausted = backend.exhausted(player);
        if (current.value() == value && current.max() == max && current.isExhausted() == exhausted) return current;
        return new ReadonlyStamina(exhausted, value, max);
    }

    @Override
    public ReadonlyStamina initializeStamina(Player player, ReadonlyStamina current) {
        return active() ? mirror(player, current) : fallback.initializeStamina(player, current);
    }

    @Override
    public ReadonlyStamina consume(Player player, ReadonlyStamina current, int value) {
        return active() ? mirror(player, current) : fallback.consume(player, current, value);
    }

    @Override
    public ReadonlyStamina recover(Player player, ReadonlyStamina current, int value) {
        return active() ? mirror(player, current) : fallback.recover(player, current, value);
    }

    @Override
    public ReadonlyStamina onTick(Player player, ReadonlyStamina current) {
        return active() ? mirror(player, current) : fallback.onTick(player, current);
    }

    /** ParCool's HUD would show AoS's stamina a second time: AoS's bar or Feathers of Fatigue's HUD already do. */
    @Override
    public boolean shouldShowHUD(Player player) {
        return !active() && fallback.shouldShowHUD(player);
    }
}

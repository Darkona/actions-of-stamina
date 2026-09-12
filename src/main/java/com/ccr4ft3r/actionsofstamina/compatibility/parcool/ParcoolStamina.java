package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.stamina.ParCoolStamina;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.world.entity.player.Player;

/**
 * AoS's stand-in for ParCool's own stamina; only reached through {@link ParcoolCompat} when ParCool is loaded.
 * <p>
 * ParCool 3 keeps a stamina instance only for the local player (other players and the server see a read-only copy it
 * syncs), so this lives on the client. While the compat is enabled it shows the AoS backend's stamina and swallows
 * ParCool's own spends and regeneration: AoS charges ParCool's actions through their events instead
 * ({@link ParcoolBridge}), so nothing is charged twice. ParCool's own HUD stays hidden ({@code ParcoolClientBridge}).
 * With the compat disabled it is ParCool's own stamina. It stays a {@link ParCoolStamina}, so ParCool still sees the
 * Default type.
 */
public final class ParcoolStamina extends ParCoolStamina {

    private final Player owner;

    public ParcoolStamina(Player owner) {
        super(owner);
        this.owner = owner;
    }

    /**
     * From the mixin on ParCool's stamina capability: ParCool's own stamina becomes AoS's; the others (hunger,
     * Elenai's Feathers) stay.
     */
    public static IStamina replace(IStamina stamina, Player player) {
        return stamina.getClass() == ParCoolStamina.class ? new ParcoolStamina(player) : stamina;
    }

    /** False while ParCool's super constructor runs (the owner isn't set yet): ParCool's own behaviour then. */
    private boolean active() {
        return owner != null && ParcoolCompat.isActive();
    }

    private StaminaBackend backend() {
        return StaminaBackends.of(owner);
    }

    @Override
    public int getActualMaxStamina() {
        return active() ? backend().maxStamina(owner) : super.getActualMaxStamina();
    }

    @Override
    public int get() {
        return active() ? backend().stamina(owner) : super.get();
    }

    @Override
    public int getOldValue() {
        return active() ? get() : super.getOldValue();
    }

    @Override
    public boolean isExhausted() {
        return active() ? backend().exhausted(owner) : super.isExhausted();
    }

    @Override
    public boolean isImposingExhaustionPenalty() {
        return isExhausted();
    }

    @Override
    public void setExhaustion(boolean value) {
        if (!active()) super.setExhaustion(value);
    }

    @Override
    public void set(int value) {
        if (!active()) super.set(value);
    }

    @Override
    public void consume(int value) {
        if (!active()) super.consume(value);
    }

    @Override
    public void recover(int value) {
        if (!active()) super.recover(value);
    }

    @Override
    public void tick() {
        if (!active()) super.tick();
    }

    @Override
    public void updateOldValue() {
        if (!active()) super.updateOldValue();
    }

    /** Whether ParCool's own HUD would show AoS's stamina (it is hidden then: AoS or Feathers of Fatigue draw theirs). */
    boolean standsIn() {
        return active();
    }
}

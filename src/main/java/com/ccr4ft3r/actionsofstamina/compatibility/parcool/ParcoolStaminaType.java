package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.api.stamina.RegisterParCoolStaminaTypeEvent;
import com.alrex.parcool.api.stamina.StaminaTypeEntry;
import com.alrex.parcool.common.stamina.impl.ParCoolStamina;
import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * AoS's ParCool stamina type ({@link ParcoolCompat#STAMINA_TYPE}); only reached through {@link ParcoolCompat} when
 * ParCool is loaded.
 * <p>
 * ParCool posts {@link RegisterParCoolStaminaTypeEvent} on its own mod bus from inside its constructor and freezes
 * the registry right after. Forge 47 constructs mods in parallel, so no listener AoS adds from its own constructor
 * is sure to be there in time: the entry is added by a mixin on the event itself ({@code ParcoolStaminaTypeEventMixin})
 * as it is created, before ParCool posts it.
 */
public final class ParcoolStaminaType {

    private ParcoolStaminaType() {
    }

    /** From the mixin, while ParCool is being constructed: adds AoS's stamina type to the event's registry. */
    public static void register(RegisterParCoolStaminaTypeEvent event) {
        event.register(new StaminaTypeEntry<>(ParcoolCompat.STAMINA_TYPE, ActionsOfStamina.MOD_ID, AoSParcoolStamina::new));
    }

    static boolean isRegistered() {
        return ParCool.getStaminaTypeRegistry().isRegistered(ParcoolCompat.STAMINA_TYPE);
    }

    /**
     * ParCool keeps a stamina instance only for the local player (other players and the server see a read-only copy
     * it syncs), so this lives on the client. While the compat is enabled it shows the AoS backend's stamina and
     * swallows ParCool's own spends and regeneration: AoS charges ParCool's actions through their events instead
     * ({@link ParcoolBridge}), so nothing is charged twice. ParCool's own HUD stays hidden. With the compat disabled
     * it is ParCool's default stamina.
     */
    public static final class AoSParcoolStamina extends AbstractLocalStamina {

        private final ParCoolStamina fallback;
        /** Last state ParCool synced to the server: it only needs a packet when one of these changed. */
        private int lastValue = Integer.MIN_VALUE;
        private int lastMax = Integer.MIN_VALUE;
        private boolean lastExhausted;

        public AoSParcoolStamina(Player owner, @Nullable AbstractLocalStamina previous) {
            super(owner);
            this.fallback = new ParCoolStamina(owner, previous);
        }

        private StaminaBackend backend() {
            return StaminaBackends.of(owner);
        }

        @Override
        public double max() {
            return ParcoolCompat.isActive() ? backend().maxStamina(owner) : fallback.max();
        }

        @Override
        public double value() {
            return ParcoolCompat.isActive() ? backend().stamina(owner) : fallback.value();
        }

        @Override
        public boolean isExhausted() {
            return ParcoolCompat.isActive() ? backend().exhausted(owner) : fallback.isExhausted();
        }

        /** AoS exempts creative and spectator players; ParCool's Inexhaustible effect doesn't make AoS costs free. */
        @Override
        public boolean isInfinite() {
            return ParcoolCompat.isActive() ? owner.isCreative() || owner.isSpectator() : fallback.isInfinite();
        }

        @Override
        public boolean imposePenalty() {
            return isExhausted();
        }

        @Override
        public void setValue(double value) {
            if (!ParcoolCompat.isActive()) fallback.setValue(value);
        }

        @Override
        public void consume(double value) {
            if (!ParcoolCompat.isActive()) fallback.consume(value);
        }

        @Override
        public void recover(double value) {
            if (!ParcoolCompat.isActive()) fallback.recover(value);
        }

        @Override
        public void tick() {
            if (!ParcoolCompat.isActive()) fallback.tick();
            int value = (int) value();
            int max = (int) max();
            boolean exhausted = isExhausted();
            if (value != lastValue || max != lastMax || exhausted != lastExhausted) {
                lastValue = value;
                lastMax = max;
                lastExhausted = exhausted;
                setDirty();
            }
        }

        @Override
        public boolean showHud() {
            return !ParcoolCompat.isActive() && fallback.showHud();
        }
    }
}

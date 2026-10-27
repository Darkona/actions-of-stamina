package com.ccr4ft3r.actionsofstamina.compat.parcool;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.api.stamina.RegisterParCoolStaminaTypeEvent;
import com.alrex.parcool.api.stamina.StaminaTypeEntry;
import com.alrex.parcool.common.stamina.impl.ParCoolStamina;
import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

final class ParcoolStaminaType {

    private ParcoolStaminaType() {
    }

    static void register() {
        IEventBus parcoolBus = ModList.get().getModContainerById(ParcoolCompat.MOD_ID).map(ModContainer::getEventBus).orElse(null);
        if (parcoolBus == null) {
            ActionsOfStamina.logger.warn("ParCool has no mod bus: AoS's ParCool stamina type isn't registered");
            return;
        }
        parcoolBus.addListener(RegisterParCoolStaminaTypeEvent.class, ParcoolStaminaType::onRegister);
    }

    private static void onRegister(RegisterParCoolStaminaTypeEvent event) {
        event.register(new StaminaTypeEntry<>(ParcoolCompat.STAMINA_TYPE, ActionsOfStamina.MOD_ID, AoSParcoolStamina::new));
    }

    static boolean isRegistered() {
        return ParCool.getStaminaTypeRegistry().isRegistered(ParcoolCompat.STAMINA_TYPE);
    }

    public static final class AoSParcoolStamina extends AbstractLocalStamina {

        private final ParCoolStamina fallback;
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

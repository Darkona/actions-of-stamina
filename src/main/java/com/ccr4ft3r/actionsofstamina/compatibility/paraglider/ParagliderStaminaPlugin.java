package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import com.darkona.feathers.api.StaminaAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.api.Copy;
import tictim.paraglider.api.Serde;
import tictim.paraglider.api.movement.Movement;
import tictim.paraglider.api.plugin.ParagliderPlugin;
import tictim.paraglider.api.stamina.Stamina;
import tictim.paraglider.api.stamina.StaminaFactory;
import tictim.paraglider.api.stamina.StaminaPlugin;
import tictim.paraglider.api.vessel.VesselContainer;
import tictim.paraglider.impl.stamina.BotWStamina;
import tictim.paraglider.impl.stamina.ServerBotWStamina;

@ParagliderPlugin
public class ParagliderStaminaPlugin implements StaminaPlugin {

    @Override
    public StaminaFactory getStaminaFactory() {
        return new FeathersParagliderStaminaFactory();
    }

    public static class FeathersParagliderStaminaFactory implements StaminaFactory {
        @Override
        @NotNull
        public Stamina createServerInstance(@NotNull ServerPlayer player) {
            return new ServerFeathersParagliderStamina<>(new ServerBotWStamina(VesselContainer.get(player)), player);
        }

        @Override
        @NotNull
        public Stamina createRemoteInstance(@NotNull Player player) {
            return new FeathersParagliderStamina<>(new BotWStamina(VesselContainer.get(player)));
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        @NotNull
        public Stamina createLocalClientInstance(@NotNull LocalPlayer player) {
            return new FeathersParagliderStamina<>(new BotWStamina(VesselContainer.get(player)));
        }
    }

    public static class FeathersParagliderStamina<T extends Stamina & Copy & Serde> implements Stamina, Copy, Serde {
        public final T fallback;

        private Player player() {
            return Minecraft.getInstance().player;
        }

        public FeathersParagliderStamina(T fallback) {
            this.fallback = fallback;
        }

        @Override
        public int stamina() {
            if (ParagliderConfig.PARAGLIDING_ENABLED.get()) {
                return StaminaAPI.getAvailableStamina(player());
            } else {
                return fallback.stamina();
            }
        }

        @Override
        public void setStamina(int i) {
            if (!ParagliderConfig.PARAGLIDING_ENABLED.get()) {
                fallback.setStamina(i);
            }
        }

        @Override
        public int maxStamina() {
            if (ParagliderConfig.PARAGLIDING_ENABLED.get()) {
                return StaminaAPI.getMaxStamina(player());
            } else {
                return fallback.maxStamina();
            }
        }

        @Override
        public boolean isDepleted() {

            if (ParagliderConfig.PARAGLIDING_ENABLED.get()) {
                return stamina() <= 0 || fallback.isDepleted();
            } else {
                return fallback.isDepleted();
            }
        }

        @Override
        public void setDepleted(boolean b) {
            fallback.setDepleted(b);
        }

        @Override
        public void update(@NotNull Movement movement) {
            int oldStamina = fallback.stamina();
            if (!ParagliderConfig.PARAGLIDING_ENABLED.get()) {
                fallback.update(movement);
            }
        }

        @Override
        public int giveStamina(int i, boolean simulate) {
            if (!ParagliderConfig.PARAGLIDING_ENABLED.get()) {
                return fallback.giveStamina(i, simulate);
            }

            return 0;
        }

        @Override
        public int takeStamina(int i, boolean simulate, boolean ignoreDepletion) {
            if (!ParagliderConfig.PARAGLIDING_ENABLED.get()) {
                return fallback.takeStamina(i, simulate, ignoreDepletion);
            }

            return 0;
        }

        /**
         * @return Whether the default stamina wheel should be rendered. Always false if
         * the setting in {@link com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderConfig} is
         * enabled.
         */
        @Override
        public boolean renderStaminaWheel() {
            return !ParagliderConfig.PARAGLIDING_ENABLED.get();
        }

        @Override
        public void copyFrom(@NotNull Object from) {
            if (!ParagliderConfig.PARAGLIDING_ENABLED.get()) {
                fallback.copyFrom(from);
            }
        }

        @Override
        public void read(@NotNull CompoundTag tag) {
            if (!ParagliderConfig.PARAGLIDING_ENABLED.get()) {
                fallback.read(tag);
            }
        }

        @Override
        @NotNull
        public CompoundTag write() {
            if (!ParagliderConfig.PARAGLIDING_ENABLED.get()) {
                return fallback.write();
            }
            return new CompoundTag();
        }
    }

    public static class ServerFeathersParagliderStamina<T extends Stamina & Copy & Serde> extends FeathersParagliderStamina<T> {
        public final ServerPlayer player;

        public ServerFeathersParagliderStamina(T fallback, ServerPlayer player) {
            super(fallback);
            this.player = player;
        }

        @Override
        public int stamina() {
            if (ParagliderConfig.PARAGLIDING_ENABLED.get()) {
                return StaminaAPI.getAvailableStamina(player);
            } else {
                return fallback.stamina();
            }
        }

        @Override
        public int maxStamina() {
            if (ParagliderConfig.PARAGLIDING_ENABLED.get()) {
                return StaminaAPI.getMaxStamina(player);
            } else {
                return fallback.maxStamina();
            }
        }

        @Override
        public boolean isDepleted() {
            if (ParagliderConfig.PARAGLIDING_ENABLED.get()) {
                return stamina() <= 0 || fallback.isDepleted();
            } else {
                return fallback.isDepleted();
            }
        }
    }
}
package com.ccr4ft3r.actionsofstamina.plugins;

import com.ccr4ft3r.actionsofstamina.config.AoSAction;
import com.ccr4ft3r.actionsofstamina.config.OptionalConfig;
import com.ccr4ft3r.actionsofstamina.config.ProfileConfig;
import com.ccr4ft3r.actionsofstamina.util.PlayerUtil;
import com.elenai.feathers.api.FeathersHelper;

import com.elenai.feathers.client.ClientFeathersData;
import com.simibubi.create.content.redstone.displayLink.source.PercentOrProgressBarDisplaySource;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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

import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.getProfile;

@ParagliderPlugin
public class ParagliderStaminaPlugin implements StaminaPlugin {
    @Override public StaminaFactory getStaminaFactory() {
        return new FeathersParagliderStaminaFactory();
    }

    public static class FeathersParagliderStaminaFactory implements StaminaFactory{
        @Override @NotNull public Stamina createServerInstance(@NotNull ServerPlayer player){
            return new ServerFeathersParagliderStamina(new ServerBotWStamina(VesselContainer.get(player)), player);
        }

        @Override @NotNull public Stamina createRemoteInstance(@NotNull Player player){
            return new FeathersParagliderStamina(new BotWStamina(VesselContainer.get(player)));
        }

        @Override @NotNull public Stamina createLocalClientInstance(@NotNull LocalPlayer player){
            return new FeathersParagliderStamina(new BotWStamina(VesselContainer.get(player)));
        }
    }

    public static class FeathersParagliderStamina<T extends Stamina & Copy & Serde> implements Stamina, Copy, Serde  {
        public final T fallback;
        public FeathersParagliderStamina(T fallback) {
            this.fallback = fallback;
        }

        protected boolean canParaglide(int feathers, int endurance, int weight) {
            return feathers + endurance > weight + getProfile().minByAction.get(AoSAction.PARAGLIDING).get();
        }

        @Override
        public int stamina() {
            if (OptionalConfig.CONFIG_DATA.enableParagliderStaminaProvider.get()) {
                return Math.max(FeathersHelper.getFeathers() + FeathersHelper.getEndurance()- ClientFeathersData.getWeight(), 0);
            } else {
                return fallback.stamina();
            }
        }

        @Override
        public void setStamina(int i) {
            if (!OptionalConfig.CONFIG_DATA.enableParagliderStaminaProvider.get()) {
                fallback.setStamina(i);
            }
        }

        @Override
        public int maxStamina() {
            if (OptionalConfig.CONFIG_DATA.enableParagliderStaminaProvider.get()) {
                return FeathersHelper.getMaxFeathers();
            } else {
                return fallback.maxStamina();
            }
        }

        @Override
        public boolean isDepleted() {
            if (OptionalConfig.CONFIG_DATA.enableParagliderStaminaProvider.get()) {
                return !canParaglide(
                        FeathersHelper.getFeathers(),
                        FeathersHelper.getEndurance(),
                        ClientFeathersData.getWeight()
                ) || fallback.isDepleted();
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
            fallback.update(movement);
            if (OptionalConfig.CONFIG_DATA.enableParagliderStaminaProvider.get()) {
                // AoS will already stop the paragliding if we run out of stamina
                fallback.setStamina(oldStamina);
            }
        }

        @Override
        public int giveStamina(int i, boolean simulate) {
            if (!OptionalConfig.CONFIG_DATA.enableParagliderStaminaProvider.get()) {
                return fallback.giveStamina(i, simulate);
            }

            return 0;
        }

        @Override
        public int takeStamina(int i, boolean simulate, boolean ignoreDepletion) {
            if (!OptionalConfig.CONFIG_DATA.enableParagliderStaminaProvider.get()) {
                return fallback.takeStamina(i, simulate, ignoreDepletion);
            }

            return 0;
        }

        /**
         * @return Whether the default stamina wheel should be rendered. Always false if
         * the setting in {@link com.ccr4ft3r.actionsofstamina.config.OptionalConfig} is
         * enabled.
         */
        @Override
        public boolean renderStaminaWheel() {
            return !OptionalConfig.CONFIG_DATA.enableParagliderStaminaProvider.get();
        }

        @Override public void copyFrom(@NotNull Object from){
            if (!OptionalConfig.CONFIG_DATA.enableParagliderStaminaProvider.get()) {
                fallback.copyFrom(from);
            }
        }

        @Override public void read(@NotNull CompoundTag tag) {
            if (!OptionalConfig.CONFIG_DATA.enableParagliderStaminaProvider.get()) {
                fallback.read(tag);
            }
        }

        @Override @NotNull public CompoundTag write() {
            if (!OptionalConfig.CONFIG_DATA.enableParagliderStaminaProvider.get()) {
                return fallback.write();
            }
            CompoundTag tag = new CompoundTag();
            tag.putInt("stamina", this.stamina());
            tag.putBoolean("depleted", this.isDepleted());
            return tag;
        }
    }

    public static class ServerFeathersParagliderStamina<T extends Stamina & Copy & Serde> extends FeathersParagliderStamina<T>  {
        public final ServerPlayer player;
        public ServerFeathersParagliderStamina(T fallback, ServerPlayer player) {
            super(fallback);
            this.player = player;
        }

        @Override
        public int stamina() {
            if (OptionalConfig.CONFIG_DATA.enableParagliderStaminaProvider.get()) {
                return Math.max(FeathersHelper.getFeathers(player) + FeathersHelper.getEndurance(player) - FeathersHelper.getPlayerWeight(player), 0);
            } else {
                return fallback.stamina();
            }
        }

        @Override
        public int maxStamina() {
            if (OptionalConfig.CONFIG_DATA.enableParagliderStaminaProvider.get()) {
                return FeathersHelper.getMaxFeathers(player);
            } else {
                return fallback.maxStamina();
            }
        }

        @Override
        public boolean isDepleted() {
            if (OptionalConfig.CONFIG_DATA.enableParagliderStaminaProvider.get()) {
                return !canParaglide(
                        FeathersHelper.getFeathers(player) ,
                        FeathersHelper.getEndurance(player),
                        FeathersHelper.getPlayerWeight(player)
                ) || fallback.isDepleted();
            } else {
                return fallback.isDepleted();
            }
        }
    }
}

package com.ccr4ft3r.actionsofstamina;

import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(ActionsOfStamina.MOD_ID)
public class ActionsOfStamina {

    public static final String MOD_ID = "actionsofstamina";

    public ActionsOfStamina() {
        registerConfigs();
        PacketHandler.registerMessages();
        addCompatibilitiesListener();

    }

    private static void registerConfigs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AoSCommonConfig.SPEC, MOD_ID + "/actions_of_stamina.toml");
       /*
       if (ModList.get().isLoaded(ModConstants.PARAGLIDER_MOD_ID)) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OptionalConfig.CONFIG, ModConstants.MOD_ID + "-5-optional.toml");
       }
       */
    }

    private static void addCompatibilitiesListener() {
       /*
       if (ModList.get().isLoaded(ModConstants.PARAGLIDER_MOD_ID)) {
           // MinecraftForge.EVENT_BUS.addListener(CompatibilityHandler::onParagliding);
       }
       */
    }
}
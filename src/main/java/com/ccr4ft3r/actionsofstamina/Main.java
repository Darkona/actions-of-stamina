package com.ccr4ft3r.actionsofstamina;

import com.ccr4ft3r.actionsofstamina.config.MainConfig;
import com.ccr4ft3r.actionsofstamina.config.ProfileConfig;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(ModConstants.MOD_ID)
public class Main {

    public Main() {
        registerConfigs();
        PacketHandler.registerMessages();
    }

    private static void registerConfigs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MainConfig.CONFIG, ModConstants.MOD_ID + "-0-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ProfileConfig.CONFIG_SLUGGISH, ModConstants.MOD_ID + "/1-sluggish-profile.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ProfileConfig.CONFIG_EXHAUSTED, ModConstants.MOD_ID + "/2-exhausted-profile.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ProfileConfig.CONFIG_BREATHLESS, ModConstants.MOD_ID + "/3-breathless-profile.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ProfileConfig.CONFIG_CUSTOM, ModConstants.MOD_ID + "/4-custom-profile.toml");
    }


}
package com.ccr4ft3r.actionsofstamina;

import com.ccr4ft3r.actionsofstamina.actions.AosFeathersPlugin;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import com.darkona.feathers.FeathersManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ActionsOfStamina.MOD_ID)
public class ActionsOfStamina {

    public static final String MOD_ID = "actionsofstamina";
    public static final String PARCOOL_MOD_ID = "parcool";
    public static final String PARAGLIDER_MOD_ID = "paraglider";
    public static final Logger logger = LogManager.getLogger(MOD_ID);

    public static boolean PARCOOL = false;
    public static boolean PARAGLIDER = false;

    public ActionsOfStamina() {
        registerConfigs();
        PacketHandler.registerMessages();
        FeathersManager.registerPlugin(AosFeathersPlugin.getInstance());
        addCompatibilitiesListeners();
    }

    private static void registerConfigs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AoSCommonConfig.SPEC, MOD_ID + "/AoS_configuration.toml");

        if (ModList.get().isLoaded(PARAGLIDER_MOD_ID)) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ParagliderConfig.SPEC, MOD_ID + "/AoS_paraglider_compat.toml");
            PARAGLIDER = true;
        }
        if (ModList.get().isLoaded(PARCOOL_MOD_ID)) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ParcoolConfig.SPEC, MOD_ID + "/AoS_parcool_compat.toml");
            PARCOOL = true;
        }

    }

    public static void log(String message, Object... args) {
        if (AoSCommonConfig.ENABLE_DEBUGGING.get())
            logger.info(message, args);
    }

    public static void sideLog(Player p, String message, Object... args) {
        if (getSide(p).equals("Client"))
            log("\u001B[0;94mCLIENT -> " + message + "\u001B[0m", args);
        else
            log("\u001B[0;91mSERVER -> " + message + "\u001B[0m", args);
    }

    public static String getSide(Entity player) {
        return player.level().isClientSide ? "Client" : "Server";
    }

    private static void addCompatibilitiesListeners() {
//
//        if (PARAGLIDER) {
//            MinecraftForge.EVENT_BUS.addListener(ParagliderHandler::onParagliding);
//        }

    }
}
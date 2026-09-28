package com.ccr4ft3r.actionsofstamina;

import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.config.AoSClientConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.ccr4ft3r.actionsofstamina.data.AosAttachments;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Actions of Stamina: how actions cost stamina, for vanilla actions and other mods' actions. The stamina itself
 * comes from Green Feathers when it is installed, or from AoS's small internal bar otherwise
 * ({@code StaminaBackends}).
 */
@Mod(ActionsOfStamina.MOD_ID)
public class ActionsOfStamina {

    public static final String MOD_ID = "actionsofstamina";
    public static final Logger logger = LogManager.getLogger(MOD_ID);

    public ActionsOfStamina(IEventBus modBus, ModContainer container) {
        // First: builds the whole common spec, compat sections included, before any compat class is touched.
        container.registerConfig(ModConfig.Type.COMMON, AoSCommonConfig.SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, AoSClientConfig.SPEC);
        AosAttachments.ATTACHMENT_TYPES.register(modBus);
        modBus.addListener(PacketHandler::register);
        modBus.addListener(ActionsOfStamina::commonSetup);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        // The compat hooks are plain listener lists or event-bus registrations: not thread safe, so on the main thread.
        event.enqueueWork(() -> {
            ParcoolCompat.init();
            CombatRollCompat.init();
            EpicFightCompat.init();
        });
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void log(String message, Object... args) {
        if (AoSCommonConfig.ENABLE_DEBUGGING.getAsBoolean())
            logger.info(message, args);
    }

    public static void sideLog(Player p, String message, Object... args) {
        if (!AoSCommonConfig.ENABLE_DEBUGGING.getAsBoolean()) return;
        if (p.level().isClientSide())
            logger.info("\u001B[0;94mCLIENT -> " + message + "\u001B[0m", args);
        else
            logger.info("\u001B[0;91mSERVER -> " + message + "\u001B[0m", args);
    }

    public static String getSide(Entity player) {
        return player.level().isClientSide() ? "Client" : "Server";
    }
}

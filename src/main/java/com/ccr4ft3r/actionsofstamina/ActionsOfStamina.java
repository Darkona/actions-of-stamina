package com.ccr4ft3r.actionsofstamina;

import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.create.CreateCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.curios.CuriosCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.config.AoSClientConfig;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.AosAttachments;
import com.ccr4ft3r.actionsofstamina.network.BackendSyncTask;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Actions of Stamina: how actions cost stamina, for vanilla actions and other mods' actions. The stamina itself
 * comes from Feathers of Fatigue when it is installed, or from AoS's small internal bar otherwise
 * ({@code StaminaBackends}).
 */
@Mod(ActionsOfStamina.MOD_ID)
public class ActionsOfStamina {

    public static final String MOD_ID = "actionsofstamina";
    public static final Logger logger = LogManager.getLogger(MOD_ID);

    public ActionsOfStamina(IEventBus modBus, ModContainer container) {
        // First: builds the whole spec, compat sections included, before any compat class is touched. A server config:
        // the server's costs are sent to every client, so no client decides its own.
        container.registerConfig(ModConfig.Type.SERVER, AoSServerConfig.SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, AoSClientConfig.SPEC);
        // Every action type, Minecraft's and each compat's, whether or not that mod is installed: the indices then don't
        // depend on which mods are there.
        VanillaActions.register();
        ParcoolCompat.registerActions();
        ParagliderCompat.registerActions();
        BetterCombatCompat.registerActions();
        CombatRollCompat.registerActions();
        EpicFightCompat.registerActions();
        WallJumpCompat.registerActions();
        GlidersCompat.registerActions();
        CreateCompat.registerActions();
        AosAttachments.ATTACHMENT_TYPES.register(modBus);
        modBus.addListener(PacketHandler::register);
        modBus.addListener(BackendSyncTask::register);
        modBus.addListener(ActionsOfStamina::commonSetup);
        // Other mods register their action types while constructed or in common setup; the slots are fixed after that.
        modBus.addListener(FMLLoadCompleteEvent.class, event -> ActionTypes.freeze());
        // Parsed config lists and the caches built from them follow config and datapack reloads.
        modBus.addListener(ModConfigEvent.Loading.class, BetterCombatCompat::onConfigLoad);
        modBus.addListener(ModConfigEvent.Reloading.class, BetterCombatCompat::onConfigLoad);
        NeoForge.EVENT_BUS.addListener(BetterCombatCompat::onTagsUpdated);
        // ParCool registers stamina types on its own mod bus while it is constructed (AoS is ordered before it).
        ParcoolCompat.registerStaminaType();
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        // The compat hooks are plain listener lists or event-bus registrations: not thread safe, so on the main thread.
        event.enqueueWork(() -> {
            ParcoolCompat.init();
            CombatRollCompat.init();
            EpicFightCompat.init();
            CuriosCompat.init();
        });
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    /** Call sites that pass arguments check this first: the varargs array and the boxing cost even when off. */
    public static boolean debugging() {
        return AoSServerConfig.ENABLE_DEBUGGING.getAsBoolean();
    }

    public static void log(String message, Object... args) {
        if (AoSServerConfig.ENABLE_DEBUGGING.getAsBoolean())
            logger.info(message, args);
    }

    public static void sideLog(Player p, String message, Object... args) {
        if (!AoSServerConfig.ENABLE_DEBUGGING.getAsBoolean()) return;
        if (p.level().isClientSide())
            logger.info("\u001B[0;94mCLIENT -> " + message + "\u001B[0m", args);
        else
            logger.info("\u001B[0;91mSERVER -> " + message + "\u001B[0m", args);
    }

    @SuppressWarnings("unused") // for addons
    public static String getSide(Entity player) {
        return player.level().isClientSide() ? "Client" : "Server";
    }
}

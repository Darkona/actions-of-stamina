package com.ccr4ft3r.actionsofstamina;

import com.ccr4ft3r.actionsofstamina.compat.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compat.combatroll.CombatRollCompat;
import com.ccr4ft3r.actionsofstamina.compat.create.CreateCompat;
import com.ccr4ft3r.actionsofstamina.compat.curios.CuriosCompat;
import com.ccr4ft3r.actionsofstamina.compat.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.compat.gliders.GlidersCompat;
import com.ccr4ft3r.actionsofstamina.compat.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.compat.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.compat.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import com.ccr4ft3r.actionsofstamina.content.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.content.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.index.ActionsAttachmentTypes;
import com.ccr4ft3r.actionsofstamina.network.ActionsPackets;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ActionsOfStamina.MOD_ID)
public class ActionsOfStamina {

    public static final String MOD_ID = "actionsofstamina";
    public static final Logger logger = LogManager.getLogger(MOD_ID);

    private static final List<Class<?>> REGISTERING_CLASSES = List.of(VanillaActions.class, ParcoolCompat.class, ParagliderCompat.class,
            BetterCombatCompat.class, CombatRollCompat.class, EpicFightCompat.class, WallJumpCompat.class, GlidersCompat.class,
            CreateCompat.class);

    public ActionsOfStamina(IEventBus modBus, ModContainer container) {
        ActionsConfig.register(container);
        REGISTERING_CLASSES.forEach(ActionsOfStamina::initialize);
        ActionsAttachmentTypes.register(modBus);
        ActionsPackets.register(modBus);
        modBus.addListener(ActionsOfStamina::commonSetup);
        StaminaBackends.takeOverPlayerActions();
        modBus.addListener(FMLLoadCompleteEvent.class, event -> ActionType.freeze());
        modBus.addListener(ModConfigEvent.Loading.class, BetterCombatCompat::onConfigLoad);
        modBus.addListener(ModConfigEvent.Reloading.class, BetterCombatCompat::onConfigLoad);
        NeoForge.EVENT_BUS.addListener(BetterCombatCompat::onTagsUpdated);
        ParcoolCompat.registerStaminaType();
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ParcoolCompat.init();
            CombatRollCompat.init();
            EpicFightCompat.init();
            CuriosCompat.init();
        });
    }

    private static void initialize(Class<?> registering) {
        try {
            Class.forName(registering.getName(), true, registering.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static boolean debugging() {
        return ActionsConfig.ENABLE_DEBUGGING.getAsBoolean();
    }

    public static void log(String message, Object... args) {
        if (debugging()) logger.info(message, args);
    }

    public static void sideLog(Player p, String message, Object... args) {
        if (!debugging()) return;
        if (p.level().isClientSide())
            logger.info("\u001B[0;94mCLIENT -> " + message + "\u001B[0m", args);
        else
            logger.info("\u001B[0;91mSERVER -> " + message + "\u001B[0m", args);
    }
}

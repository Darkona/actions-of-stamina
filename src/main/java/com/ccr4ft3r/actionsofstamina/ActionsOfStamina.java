package com.ccr4ft3r.actionsofstamina;

import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.stamina.client.InternalStaminaHud;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.create.CreateCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.curios.CuriosCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import com.ccr4ft3r.actionsofstamina.network.ActionsPackets;
import com.ccr4ft3r.actionsofstamina.stamina.InternalStamina;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ActionsOfStamina.MOD_ID)
public class ActionsOfStamina {

    public static final String MOD_ID = "actionsofstamina";
    public static final Logger logger = LogManager.getLogger(MOD_ID);

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);

    private static final List<Class<?>> REGISTERING_CLASSES = List.of(VanillaActions.class, ParcoolCompat.class, ParagliderCompat.class,
            BetterCombatCompat.class, CombatRollCompat.class, EpicFightCompat.class, WallJumpCompat.class, GlidersCompat.class,
            CreateCompat.class, PlayerActions.class, InternalStamina.class);

    public ActionsOfStamina(IEventBus modBus, ModContainer container) {
        ActionsConfig.register(container);
        REGISTERING_CLASSES.forEach(ActionsOfStamina::initialize);
        ATTACHMENT_TYPES.register(modBus);
        ActionsPackets.register(modBus);
        modBus.addListener(ActionsOfStamina::commonSetup);
        StaminaBackends.takeOverPlayerActions();
        modBus.addListener(FMLLoadCompleteEvent.class, event -> ActionType.freeze());
        modBus.addListener(ModConfigEvent.Loading.class, BetterCombatCompat::onConfigLoad);
        modBus.addListener(ModConfigEvent.Reloading.class, BetterCombatCompat::onConfigLoad);
        NeoForge.EVENT_BUS.addListener(BetterCombatCompat::onTagsUpdated);
        ParcoolCompat.registerStaminaType();
        if (FMLEnvironment.dist == Dist.CLIENT) InternalStaminaHud.register();
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

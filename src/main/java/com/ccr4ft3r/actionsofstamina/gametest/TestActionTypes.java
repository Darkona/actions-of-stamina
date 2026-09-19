package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * An action type registered the way another mod would, for {@link ActionRegistryTests}; development runs only. It is
 * continuous and costs only when it ends: its cost section (a finish cost, nothing else) is in a server config of its
 * own, {@code actionsofstamina-test-server.toml}, registered only in development runs. Players only get it while a
 * test turns it on.
 */
@EventBusSubscriber(modid = ActionsOfStamina.MOD_ID)
final class TestActionTypes {

    static final Identifier FINISHING_ID = ActionsOfStamina.id("test/finishing");
    static ActionType finishing;
    static boolean enabled;

    private static ModConfigSpec spec;
    static ActionCostConfig finishingCosts;

    private TestActionTypes() {
    }

    @SubscribeEvent
    static void register(FMLCommonSetupEvent event) {
        if (FMLEnvironment.isProduction()) return;
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        finishingCosts = ActionCostConfig.builder(b, "finishing", "Test action that costs only when it ends", true)
                .finishCost(1.0)
                .build();
        spec = b.build();
        ModList.get().getModContainerById(ActionsOfStamina.MOD_ID)
                .ifPresent(container -> container.registerConfig(ModConfig.Type.SERVER, spec, "actionsofstamina-test-server.toml"));
        finishing = ActionTypes.register(FINISHING_ID, finishingCosts, () -> enabled, Action::new);
    }
}

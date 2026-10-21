package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * An action type registered the way another mod would, for {@link ActionRegistryTests}; development runs only. It is
 * continuous and costs only when it ends: it borrows ParCool's {@code charge_jump} section (a finish cost, nothing else),
 * which is in the server config whether ParCool is installed or not. Players only get it while a test turns it on.
 */
@EventBusSubscriber(modid = ActionsOfStamina.MOD_ID)
final class TestActionTypes {

    static final ResourceLocation FINISHING_ID = ActionsOfStamina.id("test/finishing");
    static ActionType finishing;
    static boolean enabled;

    private TestActionTypes() {
    }

    @SubscribeEvent
    static void register(FMLCommonSetupEvent event) {
        if (!FMLEnvironment.production) {
            finishing = ActionTypes.register(FINISHING_ID, ParcoolConfig.byName("charge_jump").costs(), () -> enabled, Action::new);
        }
    }
}

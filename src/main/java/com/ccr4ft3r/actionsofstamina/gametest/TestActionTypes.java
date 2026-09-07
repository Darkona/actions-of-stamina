package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.ActionTypes;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * An action type registered the way another mod would, for {@link ActionRegistryTests}; development runs only. It is
 * continuous: it borrows ParCool's {@code charge_jump} section (a start cost and a finish cost, whichever the section has),
 * which is in the server config whether ParCool is installed or not. Players only get it while a test turns it on.
 */
@Mod.EventBusSubscriber(modid = ActionsOfStamina.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestActionTypes {

    static final ResourceLocation FINISHING_ID = ActionsOfStamina.id("test/finishing");
    static ActionType finishing;
    static boolean enabled;

    private TestActionTypes() {
    }

    @SubscribeEvent
    public static void register(FMLCommonSetupEvent event) {
        if (!FMLEnvironment.production) {
            finishing = ActionTypes.register(FINISHING_ID, ParcoolConfig.byName("charge_jump").costs(), () -> enabled, Action::new);
        }
    }
}

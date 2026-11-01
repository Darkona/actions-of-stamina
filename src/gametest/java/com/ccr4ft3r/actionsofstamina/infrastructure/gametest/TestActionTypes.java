package com.ccr4ft3r.actionsofstamina.infrastructure.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.content.actions.Action;
import com.ccr4ft3r.actionsofstamina.content.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.config.compat.ParcoolConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

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
            finishing = ActionType.register(FINISHING_ID, ParcoolConfig.byName("charge_jump").costs(), () -> enabled, Action::new);
        }
    }
}

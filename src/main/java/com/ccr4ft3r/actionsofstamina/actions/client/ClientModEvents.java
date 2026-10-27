package com.ccr4ft3r.actionsofstamina.actions.client;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import dev.darkona.jdlib.api.debug.DebugLines;
import dev.darkona.jdlib.api.hud.JDDebugOverlay;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = ActionsOfStamina.MOD_ID, value = Dist.CLIENT)
public final class ClientModEvents {

    private static final String DEBUG_TITLE = "Actions of Stamina";
    private static final int DEBUG_TOP = 11;

    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(BetterCombatCompat::initClient);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        JDDebugOverlay.register(event, ActionsOfStamina.id("debug"), new JDDebugOverlay.Spec(JDDebugOverlay.Side.RIGHT, DEBUG_TOP,
                DEBUG_TITLE, ActionsOfStamina::debugging, ClientModEvents::describeActions));
    }

    private static void describeActions(LocalPlayer player, DebugLines lines) {
        for (Action action : PlayerActions.get(player).getActions()) {
            if (action != null) lines.add(action.debugString());
        }
    }
}

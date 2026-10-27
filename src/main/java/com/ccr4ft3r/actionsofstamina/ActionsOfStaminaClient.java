package com.ccr4ft3r.actionsofstamina;

import com.ccr4ft3r.actionsofstamina.content.actions.Action;
import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.content.stamina.InternalStaminaHud;
import com.ccr4ft3r.actionsofstamina.compat.bettercombat.BetterCombatCompat;
import dev.darkona.jdlib.api.debug.DebugLines;
import dev.darkona.jdlib.api.hud.JDDebugOverlay;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@Mod(value = ActionsOfStamina.MOD_ID, dist = Dist.CLIENT)
public final class ActionsOfStaminaClient {

    private static final String DEBUG_TITLE = "Actions of Stamina";
    private static final int DEBUG_TOP = 11;

    public ActionsOfStaminaClient(IEventBus modBus) {
        modBus.addListener(ActionsOfStaminaClient::clientSetup);
        modBus.addListener(ActionsOfStaminaClient::registerGuiLayers);
        InternalStaminaHud.register();
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(BetterCombatCompat::initClient);
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        JDDebugOverlay.register(event, ActionsOfStamina.id("debug"), new JDDebugOverlay.Spec(JDDebugOverlay.Side.RIGHT, DEBUG_TOP,
                DEBUG_TITLE, ActionsOfStamina::debugging, ActionsOfStaminaClient::describeActions));
    }

    private static void describeActions(LocalPlayer player, DebugLines lines) {
        for (Action action : PlayerActions.get(player).getActions()) {
            if (action != null) lines.add(action.debugString());
        }
    }
}

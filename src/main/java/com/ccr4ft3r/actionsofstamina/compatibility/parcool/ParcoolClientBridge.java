package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import com.alrex.parcool.api.client.gui.ParCoolHUDEvent;
import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

/** Direct ParCool client calls; only reached through {@link ParcoolCompat} on the client. */
final class ParcoolClientBridge {

    private ParcoolClientBridge() {
    }

    static void register() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ParCoolHUDEvent.RenderEvent.class, ParcoolClientBridge::onRenderHud);
    }

    /** ParCool's stamina HUD would show AoS's stamina a second time: AoS's bar or Feathers of Fatigue's HUD already do. */
    private static void onRenderHud(ParCoolHUDEvent.RenderEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && IStamina.get(player) instanceof ParcoolStamina stamina && stamina.standsIn()) event.setCanceled(true);
    }
}

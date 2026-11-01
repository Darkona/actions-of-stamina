package com.ccr4ft3r.actionsofstamina;

import com.ccr4ft3r.actionsofstamina.compat.bettercombat.BetterCombatClientBridge;
import com.ccr4ft3r.actionsofstamina.compat.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.content.actions.Action;
import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.content.stamina.client.InternalStaminaHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ActionsOfStamina.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ActionsOfStaminaClient {

    private static final String DEBUG_TITLE = "Actions of Stamina";
    private static final int DEBUG_TOP = 11;
    private static final int DEBUG_ROW = 10;
    private static final int DEBUG_MARGIN = 5;
    private static final int DEBUG_COLOR = 0xFFFFFF;

    private static final IGuiOverlay DEBUG_OVERLAY = (gui, graphics, partialTick, width, height) -> renderDebug(graphics);

    private ActionsOfStaminaClient() {
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            if (BetterCombatCompat.LOADED) BetterCombatClientBridge.register();
        });
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "internal_stamina", InternalStaminaHud.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), "debug", DEBUG_OVERLAY);
    }

    private static void renderDebug(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui || !ActionsOfStamina.debugging()) return;
        Font font = mc.font;
        int right = graphics.guiWidth() - DEBUG_MARGIN;
        int y = DEBUG_TOP;
        graphics.drawString(font, DEBUG_TITLE, right - font.width(DEBUG_TITLE), y, DEBUG_COLOR);
        for (Action action : PlayerActions.get(player).getActions()) {
            String line = action == null ? null : action.debugString();
            if (line != null) graphics.drawString(font, line, right - font.width(line), y += DEBUG_ROW, DEBUG_COLOR);
        }
    }
}

package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import dev.darkona.jdlib.api.compat.Bridges;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class ParagliderCompat {

    public static final String MOD_ID = "paraglider";
    public static final boolean LOADED = Bridges.isLoaded(MOD_ID);
    public static final ResourceLocation VESSELS = ActionsOfStamina.id("paragliders/stamina_vessels");

    private ParagliderCompat() {
    }

    public static final ActionType PARAGLIDE = ActionType.register(ActionsOfStamina.id("paraglide"), MOD_ID, ParagliderConfig.PARAGLIDE).flag(1 << 7);

    public static boolean isParagliding(Player player) {
        return PARAGLIDE.enabled() && ParagliderBridge.isParagliding(player);
    }

    public static void onJoin(ServerPlayer player) {
        if (!LOADED) return;
        applyVessels(player, PARAGLIDE.enabled() ? ParagliderBridge.listenAndCountVessels(player) : 0);
    }

    static void applyVessels(ServerPlayer player, int vessels) {
        int feathers = PARAGLIDE.enabled() ? vessels * ParagliderConfig.FEATHERS_PER_VESSEL.getAsInt() : 0;
        StaminaBackends.server().setMaxBonus(player, VESSELS, StaminaBackends.ofFeathers(feathers));
    }
}

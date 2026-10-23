package com.ccr4ft3r.actionsofstamina.compatibility.paraglider;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
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

    public static final ActionType PARAGLIDE = ActionType.register(ActionsOfStamina.id("paraglide"), ParagliderConfig.PARAGLIDE,
            ParagliderCompat::isActive, Action::new);

    public static boolean isActive() {
        return LOADED && ParagliderConfig.PARAGLIDE.enabled();
    }

    public static boolean isParagliding(Player player) {
        return isActive() && ParagliderBridge.isParagliding(player);
    }

    public static void onJoin(ServerPlayer player) {
        if (!LOADED) return;
        applyVessels(player, isActive() ? ParagliderBridge.listenAndCountVessels(player) : 0);
    }

    static void applyVessels(ServerPlayer player, int vessels) {
        int feathers = isActive() ? vessels * ParagliderConfig.FEATHERS_PER_VESSEL.getAsInt() : 0;
        StaminaBackends.server().setMaxBonus(player, VESSELS, feathers * StaminaBackends.PER_FEATHER);
    }
}

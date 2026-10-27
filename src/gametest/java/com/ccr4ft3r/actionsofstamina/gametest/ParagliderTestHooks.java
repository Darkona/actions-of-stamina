package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.compat.paraglider.ParagliderStaminaPlugin;
import net.minecraft.server.level.ServerPlayer;
import tictim.paraglider.api.stamina.Stamina;

final class ParagliderTestHooks {

    private ParagliderTestHooks() {
    }

    static boolean usesAoSStamina(ServerPlayer player) {
        return Stamina.get(player) instanceof ParagliderStaminaPlugin.AoSParagliderStamina;
    }

    static double stamina(ServerPlayer player) {
        return Stamina.get(player).stamina();
    }
}

package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderStaminaPlugin;
import net.minecraft.server.level.ServerPlayer;
import tictim.paraglider.api.stamina.Stamina;

/** Paragliders calls for {@link CompatTests}; loaded only when Paragliders is. */
final class ParagliderTestHooks {

    private ParagliderTestHooks() {
    }

    static boolean usesAoSStamina(ServerPlayer player) {
        return Stamina.get(player) instanceof ParagliderStaminaPlugin.AoSParagliderStamina;
    }

    static int stamina(ServerPlayer player) {
        return Stamina.get(player).stamina();
    }
}

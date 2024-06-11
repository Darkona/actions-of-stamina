package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.config.AoSAction;
import net.minecraft.world.entity.player.Player;

public class ActionProvider {

    public static ActionProvider INSTANCE;

    private ActionProvider() {

    }

    public static ActionProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionProvider();
        }
        return INSTANCE;
    }

    public void getAction(Player player, AoSAction action) {

    }


}

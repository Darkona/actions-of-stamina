package com.ccr4ft3r.actionsofstamina.compat;

import net.minecraftforge.fml.ModList;

public final class Bridges {

    private Bridges() {
    }

    public static boolean isLoaded(String modId) {
        ModList mods = ModList.get();
        return mods != null && mods.isLoaded(modId);
    }
}

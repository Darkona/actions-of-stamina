package com.ccr4ft3r.actionsofstamina.actions;

import com.darkona.feathers.api.ICapabilityPlugin;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

public class AosFeathersPlugin implements ICapabilityPlugin {

    private static AosFeathersPlugin INSTANCE;

    private AosFeathersPlugin() {

    }

    public static ICapabilityPlugin getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AosFeathersPlugin();
        }
        return INSTANCE;
    }

    @Override
    public void onPlayerJoin(EntityJoinLevelEvent entityJoinLevelEvent) {

    }

    @Override
    public void onPlayerTickBefore(TickEvent.PlayerTickEvent event) {

    }

    @Override
    public void onPlayerTickAfter(TickEvent.PlayerTickEvent playerTickEvent) {

    }

    @Override
    public void attachDeltaModifiers() {

    }

    @Override
    public void attackUsageModifiers() {

    }
}

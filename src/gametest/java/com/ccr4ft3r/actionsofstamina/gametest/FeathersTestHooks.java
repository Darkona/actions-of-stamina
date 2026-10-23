package com.ccr4ft3r.actionsofstamina.gametest;

import com.darkona.feathersoffatigue.api.FeathersAPI;
import com.darkona.feathersoffatigue.api.registry.FeathersAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

final class FeathersTestHooks {

    private FeathersTestHooks() {
    }

    static Set<String> playerActionOwners() {
        return FeathersAPI.getPlayerActionOwners();
    }

    static boolean hasMaxFeathersModifier(ServerPlayer player, ResourceLocation id) {
        return player.getAttribute(FeathersAttributes.MAX_FEATHERS).hasModifier(id);
    }
}

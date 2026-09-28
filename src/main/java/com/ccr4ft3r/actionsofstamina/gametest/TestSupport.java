package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalBackend;
import com.mojang.authlib.GameProfile;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

import java.util.UUID;

/**
 * Players for stamina tests. Vanilla's mock server player reports itself as creative, which is exempt by design, so
 * these are NeoForge fake players in survival. The server doesn't tick them: tests tick them.
 */
final class TestSupport {

    private TestSupport() {
    }

    static ServerPlayer player(GameTestHelper helper) {
        ServerPlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "aos-test"));
        player.moveTo(helper.absoluteVec(Vec3.ZERO));
        return player;
    }

    /** Runs the internal stamina's server tick, as the player tick event would. */
    static void tick(ServerPlayer player, int ticks) {
        for (int i = 0; i < ticks; i++) {
            InternalBackend.INSTANCE.tick(player);
            player.tickCount++;
        }
    }
}

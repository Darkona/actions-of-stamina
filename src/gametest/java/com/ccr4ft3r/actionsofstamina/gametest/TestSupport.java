package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.ActionProvider;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalBackend;
import dev.darkona.jdlib.testkit.TestPlayers;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;


final class TestSupport {

    private static final ResourceLocation TEST = ActionsOfStamina.id("test");

    private TestSupport() {
    }

    static ServerPlayer player(GameTestHelper helper) {
        return TestPlayers.survival(helper, "aos-test");
    }

    static ServerPlayer survivalPlayer(GameTestHelper helper) {
        ServerPlayer player = TestPlayers.silent(helper, new Vec3(0.5, 3, 0.5));
        ActionProvider.addEnabledActions(PlayerActions.get(player));
        return player;
    }

    static void exhaust(StaminaBackend backend, ServerPlayer player) {
        int spends = 0;
        while (spends < 10000 && backend.spend(player, TEST, 50, 0)) spends++;
    }

    static void tickEvent(ServerPlayer player, int ticks) {
        for (int i = 0; i < ticks; i++) {
            NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(player));
            player.tickCount++;
        }
    }

    static void tick(ServerPlayer player, int ticks) {
        for (int i = 0; i < ticks; i++) {
            InternalBackend.INSTANCE.tick(player);
            player.tickCount++;
        }
    }
}

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


/**
 * Players for stamina tests. Vanilla's mock server player reports itself as creative, which is exempt by design, so
 * these are NeoForge fake players in survival or, where AoS's actions must run (they skip fake players), plain
 * survival server players. The server doesn't tick either: tests tick them.
 */
final class TestSupport {

    private static final ResourceLocation TEST = ActionsOfStamina.id("test");

    private TestSupport() {
    }

    static ServerPlayer player(GameTestHelper helper) {
        return TestPlayers.survival(helper, "aos-test");
    }

    /**
     * A survival server player that isn't a fake player, so AoS's actions run for it (built here as on joining a
     * level). Its connection swallows every packet. It isn't added to the level.
     */
    static ServerPlayer survivalPlayer(GameTestHelper helper) {
        ServerPlayer player = TestPlayers.silent(helper, new Vec3(0.5, 3, 0.5));
        ActionProvider.addEnabledActions(PlayerActions.get(player));
        return player;
    }

    /** Spends in small steps until nothing more can be spent (Feathers of Fatigue: through strain too). */
    static void exhaust(StaminaBackend backend, ServerPlayer player) {
        int spends = 0;
        while (spends < 10000 && backend.spend(player, TEST, 50, 0)) spends++;
    }

    /**
     * Posts the real server player tick event: AoS's actions and internal stamina, Feathers of Fatigue's drains and
     * regeneration, and every other mod's player tick.
     */
    static void tickEvent(ServerPlayer player, int ticks) {
        for (int i = 0; i < ticks; i++) {
            NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(player));
            player.tickCount++;
        }
    }

    /** Runs the internal stamina's server tick, as the player tick event would. */
    static void tick(ServerPlayer player, int ticks) {
        for (int i = 0; i < ticks; i++) {
            InternalBackend.INSTANCE.tick(player);
            player.tickCount++;
        }
    }
}

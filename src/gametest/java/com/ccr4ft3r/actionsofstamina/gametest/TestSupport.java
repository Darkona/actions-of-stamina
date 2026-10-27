package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.content.actions.Action;
import com.ccr4ft3r.actionsofstamina.content.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.content.stamina.InternalBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import dev.darkona.jdlib.testkit.TestPlayers;
import java.lang.reflect.Field;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
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
        PlayerActions.addEnabledActions(PlayerActions.get(player));
        return player;
    }

    static ServerPlayer survivalPlayerWith(GameTestHelper helper, ActionType... enabled) {
        return survivalPlayerSwitched(helper, true, enabled);
    }

    static ServerPlayer survivalPlayerWithout(GameTestHelper helper, ActionType... disabled) {
        return survivalPlayerSwitched(helper, false, disabled);
    }

    private static ServerPlayer survivalPlayerSwitched(GameTestHelper helper, boolean on, ActionType[] types) {
        boolean[] before = new boolean[types.length];
        for (int i = 0; i < types.length; i++) {
            before[i] = types[i].config().enabled();
            switchEnabled(types[i].config(), on);
        }
        try {
            return survivalPlayer(helper);
        } finally {
            for (int i = 0; i < types.length; i++) switchEnabled(types[i].config(), before[i]);
        }
    }

    static void switchEnabled(ActionCostConfig config, boolean on) {
        try {
            Field field = ActionCostConfig.class.getDeclaredField("enabled");
            field.setAccessible(true);
            ((ModConfigSpec.BooleanValue) field.get(config)).set(on);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    static void only(ServerPlayer player, Action... actions) {
        PlayerActions held = PlayerActions.get(player);
        held.clearActions(player);
        for (Action action : actions) held.addEnabledAction(action);
    }

    static int stamina(ServerPlayer player) {
        return StaminaBackends.server().stamina(player);
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

package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.stamina.BackendSyncPacket;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend.BackendMode;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;


@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class BackendSelectionTests {

    private static final ResourceLocation TEST = ActionsOfStamina.id("test");

    @GameTest(template = "empty")
    public static void configChoiceResolves(GameTestHelper helper) {
        helper.assertValueEqual(StaminaBackends.resolve(BackendMode.AUTO, true), StaminaBackend.Kind.FEATHERS, "auto with Feathers of Fatigue");
        helper.assertValueEqual(StaminaBackends.resolve(BackendMode.AUTO, false), StaminaBackend.Kind.INTERNAL, "auto without Feathers of Fatigue");
        helper.assertValueEqual(StaminaBackends.resolve(BackendMode.INTERNAL, true), StaminaBackend.Kind.INTERNAL, "internal with Feathers of Fatigue");
        helper.assertValueEqual(StaminaBackends.resolve(BackendMode.FEATHERS, false), StaminaBackend.Kind.INTERNAL, "feathers without Feathers of Fatigue");
        helper.assertValueEqual(StaminaBackends.resolve(BackendMode.FEATHERS, true), StaminaBackend.Kind.FEATHERS, "feathers with Feathers of Fatigue");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void activeBackendFollowsTheInstall(GameTestHelper helper) {
        StaminaBackend.Kind expected = StaminaBackends.FEATHERS_LOADED ? StaminaBackend.Kind.FEATHERS : StaminaBackend.Kind.INTERNAL;
        helper.assertValueEqual(StaminaBackends.server().kind(), expected, "server backend");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void feathersBackendSpendsFeathers(GameTestHelper helper) {
        if (!StaminaBackends.FEATHERS_LOADED) {
            helper.succeed();
            return;
        }
        StaminaBackend feathers = StaminaBackends.byKind(StaminaBackend.Kind.FEATHERS);
        ServerPlayer player = TestSupport.player(helper);
        int before = feathers.stamina(player);
        helper.assertTrue(feathers.spend(player, TEST, StaminaBackend.ofFeathers(2), 0), "spend 2 feathers");
        helper.assertValueEqual(feathers.stamina(player), before - StaminaBackend.ofFeathers(2), "feathers after spending 2");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void feathersLeavesPlayerActionsToAos(GameTestHelper helper) {
        if (!StaminaBackends.FEATHERS_LOADED) {
            helper.succeed();
            return;
        }
        helper.assertTrue(FeathersTestHooks.playerActionOwners().contains(ActionsOfStamina.MOD_ID), "AoS took over player actions");
        helper.succeed();
    }

    private static ServerConfigurationPacketListener listener(boolean channel, List<Object> finished) {
        return (ServerConfigurationPacketListener) Proxy.newProxyInstance(BackendSelectionTests.class.getClassLoader(),
                new Class<?>[]{ServerConfigurationPacketListener.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "hasChannel" -> {
                            return channel;
                        }
                        case "finishCurrentTask" -> finished.add(args[0]);
                        case "hashCode" -> {
                            return System.identityHashCode(proxy);
                        }
                        case "equals" -> {
                            return proxy == args[0];
                        }
                        default -> {
                        }
                    }
                    Class<?> type = method.getReturnType();
                    return type == boolean.class ? Boolean.FALSE : type == int.class ? 0 : null;
                });
    }

    @GameTest(template = "empty")
    public static void backendIsSentWhileConfiguring(GameTestHelper helper) {
        List<Object> finished = new ArrayList<>();
        RegisterConfigurationTasksEvent event = new RegisterConfigurationTasksEvent(listener(true, finished));
        BackendSyncPacket.Task.register(event);
        Queue<ConfigurationTask> tasks = event.getConfigurationTasks();
        helper.assertValueEqual(tasks.size(), 1, "configuration tasks");
        List<CustomPacketPayload> sent = new ArrayList<>();
        ((BackendSyncPacket.Task) tasks.peek()).run(sent::add);
        helper.assertValueEqual(sent.size(), 1, "payloads sent");
        helper.assertTrue(sent.get(0) instanceof BackendSyncPacket packet && packet.kind() == StaminaBackends.server().kind().ordinal(),
                "the payload names the server's backend");
        helper.assertValueEqual(finished, List.<Object>of(BackendSyncPacket.Task.TYPE), "finished tasks");

        RegisterConfigurationTasksEvent deaf = new RegisterConfigurationTasksEvent(listener(false, finished));
        BackendSyncPacket.Task.register(deaf);
        helper.assertTrue(deaf.getConfigurationTasks().isEmpty(), "no task for a client without the channel");
        helper.succeed();
    }
}

package com.ccr4ft3r.actionsofstamina.infrastructure.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.content.actions.Action;
import com.ccr4ft3r.actionsofstamina.content.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.content.stamina.InternalBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.TickEvent;
import org.jetbrains.annotations.Nullable;


final class TestSupport {

    private static final ResourceLocation TEST = ActionsOfStamina.id("test");

    private TestSupport() {
    }

    static ServerPlayer player(GameTestHelper helper) {
        ServerPlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "aos-test"));
        player.moveTo(helper.absoluteVec(Vec3.ZERO));
        return player;
    }

    static ServerPlayer survivalPlayer(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerPlayer player = new ServerPlayer(level.getServer(), level, new GameProfile(UUID.randomUUID(), "aos-test-player"));
        new SilentListener(level.getServer(), player);
        player.moveTo(helper.absoluteVec(new Vec3(0.5, 3, 0.5)));
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
            ((ForgeConfigSpec.BooleanValue) field.get(config)).set(on);
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
            MinecraftForge.EVENT_BUS.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.START, player));
            MinecraftForge.EVENT_BUS.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.END, player));
            player.tickCount++;
        }
    }

    static void tick(ServerPlayer player, int ticks) {
        for (int i = 0; i < ticks; i++) {
            InternalBackend.INSTANCE.tick(player);
            player.tickCount++;
        }
    }

    static <N> void assertValueEqual(GameTestHelper helper, N actual, N expected, String name) {
        if (!Objects.equals(actual, expected)) {
            throw new GameTestAssertException("Expected " + name + " to be " + expected + ", but was " + actual);
        }
    }

    private static final class SilentListener extends ServerGamePacketListenerImpl {

        SilentListener(MinecraftServer server, ServerPlayer player) {
            super(server, embeddedConnection(), player);
        }

        private static Connection embeddedConnection() {
            Connection connection = new Connection(PacketFlow.SERVERBOUND);
            new EmbeddedChannel(connection);
            return connection;
        }

        @Override
        public void send(Packet<?> packet) {
        }

        @Override
        public void send(Packet<?> packet, @Nullable PacketSendListener listener) {
        }
    }
}

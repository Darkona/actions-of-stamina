package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.actions.ActionProvider;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalBackend;
import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Players for stamina tests. Vanilla's mock server player reports itself as creative, which is exempt by design, so
 * these are Forge fake players in survival or, where AoS's actions must run (they skip fake players), plain
 * survival server players. The server doesn't tick either: tests tick them.
 */
final class TestSupport {

    private TestSupport() {
    }

    static ServerPlayer player(GameTestHelper helper) {
        ServerPlayer player = new TestPlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "aos-test"));
        player.moveTo(helper.absoluteVec(Vec3.ZERO));
        return player;
    }

    /**
     * Forge 40's FakePlayer reports 0, 0, 0 as its position wherever it is, and a 1.18.2 ServerPlayer moves through its
     * connection, which a FakePlayer's ignores: tests that place the player need both to be real.
     */
    private static final class TestPlayer extends FakePlayer {

        TestPlayer(ServerLevel level, GameProfile profile) {
            super(level, profile);
        }

        @Override
        public void moveTo(double x, double y, double z) {
            moveTo(x, y, z, getYRot(), getXRot());
        }

        @Override
        public Vec3 position() {
            return new Vec3(getX(), getY(), getZ());
        }

        @Override
        public BlockPos blockPosition() {
            return new BlockPos(getBlockX(), getBlockY(), getBlockZ());
        }
    }

    /**
     * A survival server player that isn't a fake player, so AoS's actions run for it (built here as on joining a
     * level). Its connection swallows every packet. It isn't added to the level.
     */
    static ServerPlayer survivalPlayer(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerPlayer player = new ServerPlayer(level.getServer(), level, new GameProfile(UUID.randomUUID(), "aos-test-player"));
        new SilentListener(level.getServer(), player);
        player.moveTo(helper.absoluteVec(new Vec3(0.5, 3, 0.5)));
        ActionProvider.addEnabledActions(PlayerActions.get(player));
        return player;
    }

    /**
     * Sets itself as the player's connection (on an embedded channel, which other mods' networking may inspect) and
     * sends nothing anywhere.
     */
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
        public void send(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> listener) {
        }
    }

    /**
     * Posts the real server player tick event: AoS's actions and internal stamina, Green Feathers' drains and
     * regeneration, and every other mod's player tick.
     */
    static void tickEvent(ServerPlayer player, int ticks) {
        for (int i = 0; i < ticks; i++) {
            MinecraftForge.EVENT_BUS.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.START, player));
            MinecraftForge.EVENT_BUS.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.END, player));
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

    /** What later versions' {@code GameTestHelper.assertTrue} does. */
    static void assertTrue(GameTestHelper helper, boolean condition, String message) {
        if (!condition) throw new GameTestAssertException(message);
    }

    /** What later versions' {@code GameTestHelper.assertFalse} does. */
    static void assertFalse(GameTestHelper helper, boolean condition, String message) {
        if (condition) throw new GameTestAssertException(message);
    }

    /** What later versions' {@code GameTestHelper.assertValueEqual} does. */
    static <N> void assertValueEqual(GameTestHelper helper, N actual, N expected, String name) {
        if (!Objects.equals(actual, expected)) {
            throw new GameTestAssertException("Expected " + name + " to be " + expected + ", but was " + actual);
        }
    }
}

package com.ccr4ft3r.actionsofstamina.gametest;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.impl.BreakfallReady;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.provider.StaminaProvider;
import com.alrex.parcool.common.capability.stamina.HungerStamina;
import com.alrex.parcool.common.capability.stamina.ParCoolStamina;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolStamina;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/** ParCool calls for {@link CompatTests}; loaded only when ParCool is. */
final class ParcoolTestHooks {

    private ParcoolTestHooks() {
    }

    static boolean dodgeStartCancelled(ServerPlayer player) {
        return startCancelled(player, Dodge.class);
    }

    static boolean breakfallReadyCancelled(ServerPlayer player) {
        return startCancelled(player, BreakfallReady.class);
    }

    /** The roll a breakfall lands as, which ParCool asks to start after the landing. */
    static boolean breakfallRollCancelled(ServerPlayer player) {
        return startCancelled(player, Roll.class);
    }

    private static boolean startCancelled(ServerPlayer player, Class<? extends Action> action) {
        Parkourability parkourability = Parkourability.get(player);
        return MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStart(player, parkourability.get(action)));
    }

    /** What the server posts when the client's start packet for a dodge arrives. */
    static void postDodgeStarted(ServerPlayer player) {
        Parkourability parkourability = Parkourability.get(player);
        MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(player, parkourability.get(Dodge.class)));
    }

    /** Mixin merges the injector handler into the target class, under a name that keeps ours. */
    static boolean providerMixinApplied() {
        for (Method method : StaminaProvider.class.getDeclaredMethods()) {
            if (method.getName().contains("actionsofstamina$aosStamina")) return true;
        }
        return false;
    }

    /** ParCool's own stamina is replaced by AoS's, its hunger stamina is kept; null when so, else what's wrong. */
    @Nullable
    static String checkReplacement(ServerPlayer player) {
        IStamina own = ParcoolStamina.replace(new ParCoolStamina(player), player);
        if (!(own instanceof ParcoolStamina)) return "ParCool's own stamina kept: " + own.getClass().getName();
        IStamina hunger = ParcoolStamina.replace(new HungerStamina(player), player);
        if (!(hunger instanceof HungerStamina)) return "hunger stamina replaced: " + hunger.getClass().getName();
        return null;
    }

    /**
     * Builds AoS's ParCool stamina for {@code player} (ParCool builds it for the local player; for a server player it
     * reads the server backend) and checks it mirrors {@code backend} and swallows ParCool's own spends; null when it
     * does, else what's wrong.
     */
    @Nullable
    static String checkStaminaMirrorsBackend(ServerPlayer player, StaminaBackend backend) {
        IStamina stamina = new ParcoolStamina(player);
        int before = backend.stamina(player);
        if (stamina.get() != before) return "value " + stamina.get() + " != backend " + before;
        if (stamina.getActualMaxStamina() != backend.maxStamina(player)) return "max " + stamina.getActualMaxStamina() + " != backend " + backend.maxStamina(player);
        if (stamina.isExhausted() != backend.exhausted(player)) return "exhausted " + stamina.isExhausted() + " != backend " + backend.exhausted(player);
        if (stamina.isImposingExhaustionPenalty() != backend.exhausted(player)) return "penalty " + stamina.isImposingExhaustionPenalty() + " while exhausted " + backend.exhausted(player);
        stamina.consume(500);
        stamina.set(0);
        stamina.recover(500);
        stamina.tick();
        if (backend.stamina(player) != before) return "ParCool's own spends reached the backend: " + before + " -> " + backend.stamina(player);
        return null;
    }
}

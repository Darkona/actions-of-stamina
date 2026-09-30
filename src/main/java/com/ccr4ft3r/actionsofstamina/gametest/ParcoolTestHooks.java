package com.ccr4ft3r.actionsofstamina.gametest;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.impl.BreakfallReady;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.handlers.HungerStaminaHandler;
import com.alrex.parcool.common.stamina.handlers.ParCoolStaminaHandler;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolStamina;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

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
        return NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStart(player, parkourability.get(action))).isCanceled();
    }

    /** What the server posts when the client's start packet for a dodge arrives. */
    static void postDodgeStarted(ServerPlayer player) {
        Parkourability parkourability = Parkourability.get(player);
        NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(player, parkourability.get(Dodge.class)));
    }

    /** ParCool's own stamina handler is replaced by AoS's, its hunger handler is kept; null when so, else what's wrong. */
    @Nullable
    static String checkReplacement() {
        IParCoolStaminaHandler own = ParcoolStamina.replace(new ParCoolStaminaHandler());
        if (!(own instanceof ParcoolStamina)) return "ParCool's own stamina kept: " + own.getClass().getName();
        IParCoolStaminaHandler hunger = ParcoolStamina.replace(new HungerStaminaHandler());
        if (!(hunger instanceof HungerStaminaHandler)) return "hunger stamina replaced: " + hunger.getClass().getName();
        return null;
    }

    /**
     * Runs AoS's ParCool stamina handler for {@code player} (ParCool runs it for the local player; for a server player
     * it reads the server backend) and checks it mirrors {@code backend} and swallows ParCool's own spends; null when it
     * does, else what's wrong.
     */
    @Nullable
    static String checkStaminaMirrorsBackend(ServerPlayer player, StaminaBackend backend) {
        IParCoolStaminaHandler handler = new ParcoolStamina();
        int before = backend.stamina(player);
        ReadonlyStamina stamina = handler.initializeStamina(player, ReadonlyStamina.createDefault());
        String problem = mirrors(stamina, player, backend, before);
        if (problem != null) return problem;
        if (handler.shouldShowHUD(player)) return "ParCool's HUD shows";
        stamina = handler.consume(player, stamina, 500);
        stamina = handler.recover(player, stamina, 500);
        stamina = handler.onTick(player, stamina);
        if (backend.stamina(player) != before) return "ParCool's own spends reached the backend: " + before + " -> " + backend.stamina(player);
        return mirrors(stamina, player, backend, before);
    }

    @Nullable
    private static String mirrors(ReadonlyStamina stamina, ServerPlayer player, StaminaBackend backend, int value) {
        if (stamina.value() != value) return "value " + stamina.value() + " != backend " + value;
        if (stamina.max() != backend.maxStamina(player)) return "max " + stamina.max() + " != backend " + backend.maxStamina(player);
        if (stamina.isExhausted() != backend.exhausted(player)) return "exhausted " + stamina.isExhausted() + " != backend " + backend.exhausted(player);
        return null;
    }
}

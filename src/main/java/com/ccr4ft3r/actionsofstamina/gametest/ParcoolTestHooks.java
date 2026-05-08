package com.ccr4ft3r.actionsofstamina.gametest;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ParCoolActionEvent;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

/** ParCool calls for {@link CompatTests}; loaded only when ParCool is. */
final class ParcoolTestHooks {

    private ParcoolTestHooks() {
    }

    static boolean dodgeStartCancelled(ServerPlayer player) {
        Parkourability parkourability = Parkourability.get(player);
        return NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStart(player, parkourability.get(ParCoolActions.DODGE))).isCanceled();
    }

    /** What the server posts when the client's start packet for a dodge arrives. */
    static void postDodgeStarted(ServerPlayer player) {
        Parkourability parkourability = Parkourability.get(player);
        NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(player, parkourability.get(ParCoolActions.DODGE)));
    }

    static boolean staminaTypeRegistered() {
        return ParCool.getStaminaTypeRegistry().isRegistered(ParcoolCompat.STAMINA_TYPE);
    }

    /** {@code stamina_type}'s default, as written into a new ParCool server config. */
    static String defaultStaminaType() {
        return ParCool.getConfig().server().staminaType.getDefault();
    }

    /** The stamina type ParCool picks for a player now. */
    static ResourceLocation staminaType() {
        return ParCool.getConfig().server().getStaminaTypeID();
    }

    /** What ParCool creates for the local player; built here for a server player, which reads the server backend. */
    static AbstractLocalStamina newStamina(ServerPlayer player) {
        return ParCool.getStaminaTypeRegistry().getProvider(ParcoolCompat.STAMINA_TYPE).newInstance(player, null);
    }

    /**
     * Builds AoS's ParCool stamina for {@code player} and checks it mirrors {@code backend} and swallows ParCool's
     * own spends; null when it does, else what's wrong.
     */
    @Nullable
    static String checkStaminaMirrorsBackend(ServerPlayer player, StaminaBackend backend) {
        AbstractLocalStamina stamina = newStamina(player);
        int before = backend.stamina(player);
        if ((int) stamina.value() != before) return "value " + stamina.value() + " != backend " + before;
        if ((int) stamina.max() != backend.maxStamina(player)) return "max " + stamina.max() + " != backend " + backend.maxStamina(player);
        if (stamina.isExhausted() != backend.exhausted(player)) return "exhausted " + stamina.isExhausted() + " != backend " + backend.exhausted(player);
        if (stamina.imposePenalty() != backend.exhausted(player)) return "penalty " + stamina.imposePenalty() + " while exhausted " + backend.exhausted(player);
        if (stamina.isInfinite()) return "infinite for a survival player";
        if (stamina.showHud()) return "ParCool's HUD shows";
        stamina.consume(500);
        stamina.setValue(0);
        stamina.recover(500);
        if (backend.stamina(player) != before) return "ParCool's own spends reached the backend: " + before + " -> " + backend.stamina(player);
        return null;
    }
}

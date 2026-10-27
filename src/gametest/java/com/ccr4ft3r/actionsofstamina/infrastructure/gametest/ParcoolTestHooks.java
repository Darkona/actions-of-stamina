package com.ccr4ft3r.actionsofstamina.infrastructure.gametest;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ParCoolActionEvent;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import com.ccr4ft3r.actionsofstamina.compat.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackend;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

final class ParcoolTestHooks {

    private ParcoolTestHooks() {
    }

    static boolean dodgeStartCancelled(ServerPlayer player) {
        Parkourability parkourability = Parkourability.get(player);
        return NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStart(player, parkourability.get(ParCoolActions.DODGE))).isCanceled();
    }

    static void postDodgeStarted(ServerPlayer player) {
        Parkourability parkourability = Parkourability.get(player);
        NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(player, parkourability.get(ParCoolActions.DODGE)));
    }

    static boolean staminaTypeRegistered() {
        return ParCool.getStaminaTypeRegistry().isRegistered(ParcoolCompat.STAMINA_TYPE);
    }

    static String defaultStaminaType() {
        return ParCool.getConfig().server().staminaType.getDefault();
    }

    static ResourceLocation staminaType() {
        return ParCool.getConfig().server().getStaminaTypeID();
    }

    static AbstractLocalStamina newStamina(ServerPlayer player) {
        return ParCool.getStaminaTypeRegistry().getProvider(ParcoolCompat.STAMINA_TYPE).newInstance(player, null);
    }

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

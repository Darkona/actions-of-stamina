package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import net.bettercombat.network.Packets;
import net.bettercombat.network.ServerNetwork;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Better Combat (applied only when it is loaded, see {@code CompatMixinPlugin}): charges each swing when its attack
 * request reaches the server (main thread), and drops a swing that can't be paid.
 */
@Mixin(ServerNetwork.class)
public abstract class BetterCombatServerNetworkMixin {

    @Inject(method = "handleAttackRequest", at = @At("HEAD"), cancellable = true)
    private static void actionsofstamina$chargeSwing(Packets.C2S_AttackRequest request, MinecraftServer server,
                                                     ServerPlayer player, ServerGamePacketListenerImpl handler,
                                                     CallbackInfo ci) {
        if (!BetterCombatCompat.chargeSwing(player, request.comboCount())) ci.cancel();
    }
}

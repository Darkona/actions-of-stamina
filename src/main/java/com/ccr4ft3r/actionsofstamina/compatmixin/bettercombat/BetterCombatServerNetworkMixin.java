package com.ccr4ft3r.actionsofstamina.compatmixin.bettercombat;

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

@Mixin(ServerNetwork.class)
public abstract class BetterCombatServerNetworkMixin {

    @Inject(method = "handleAttackRequest", at = @At("HEAD"), cancellable = true)
    private static void actionsofstamina$chargeSwing(Packets.C2S_AttackRequest request, MinecraftServer server,
                                                     ServerPlayer player, ServerGamePacketListenerImpl handler,
                                                     CallbackInfo ci) {
        int combo = request.comboCount();
        if (server.isSameThread()) {
            if (!BetterCombatCompat.chargeSwing(player, combo, false)) ci.cancel();
        } else if (!BetterCombatCompat.chargeSwing(player, combo, true)) {
            ci.cancel();
        } else {
            server.execute(() -> BetterCombatCompat.chargeSwing(player, combo, false));
        }
    }
}

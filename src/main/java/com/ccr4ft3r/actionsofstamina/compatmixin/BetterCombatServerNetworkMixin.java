package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.network.Packets;
import net.bettercombat.network.ServerNetwork;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Better Combat (applied only when it is loaded, see {@code CompatMixinPlugin}): charges each swing when its attack
 * request runs on the server thread, and drops a swing that can't be paid.
 * <p>
 * Better Combat 1.7 receives the request on the network thread and schedules the swing on the server thread as a
 * lambda of {@code ServerNetwork.initializeHandlers}: the only method of the class with this descriptor, matched by it
 * rather than by the lambda's generated name.
 */
@Mixin(value = ServerNetwork.class, remap = false)
public abstract class BetterCombatServerNetworkMixin {

    @Inject(method = "*(Lnet/minecraft/server/level/ServerPlayer;Lnet/bettercombat/network/Packets$C2S_AttackRequest;"
            + "Lnet/bettercombat/api/WeaponAttributes;Lnet/bettercombat/api/WeaponAttributes$Attack;Lnet/bettercombat/api/AttackHand;"
            + "Lnet/minecraft/server/level/ServerLevel;ZLnet/minecraft/server/network/ServerGamePacketListenerImpl;)V",
            at = @At("HEAD"), cancellable = true)
    private static void actionsofstamina$chargeSwing(ServerPlayer player, Packets.C2S_AttackRequest request, WeaponAttributes attributes,
                                                     WeaponAttributes.Attack attack, AttackHand hand, ServerLevel level, boolean vanillaPacket,
                                                     ServerGamePacketListenerImpl handler, CallbackInfo ci) {
        if (!BetterCombatCompat.chargeSwing(player, request.comboCount(), false)) ci.cancel();
    }
}

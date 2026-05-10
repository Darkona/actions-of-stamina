package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.network.ActionChargePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ActionsOfStamina.MOD_ID, value = Dist.CLIENT)
public final class ClientGameEvents {

    private ClientGameEvents() {
    }

    /** Client-side prediction tick, local player only (remote players' state is the server's business). */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;
        PlayerActions actions = PlayerActions.get(player);
        ClientActionTracker.update(player, actions);
        actions.tick(player);
    }

    /** Cancels the attack (and swing) when the player can't afford it. */
    @SubscribeEvent
    public static void onPlayerAttemptAttack(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (PlayerActions.isNotExhaustable(player)) return;

        HitResult hitResult = mc.hitResult;
        boolean isEntityHit = hitResult != null && hitResult.getType() == HitResult.Type.ENTITY;
        boolean isMissHit = hitResult != null && hitResult.getType() == HitResult.Type.MISS;

        Action attack = PlayerActions.get(player).getAction(Action.ATTACK);
        if (attack == null) return;
        // Better Combat swings these weapons itself and its compat charges each swing.
        if (BetterCombatCompat.handlesAttacksWith(player.getMainHandItem())) return;
        // Same for Epic Fight's battle mode: its basic attack combo is charged by the Epic Fight compat.
        if (EpicFightCompat.inBattleMode(player)) return;
        // Only swings at an entity cost, and at air unless only_for_hits: mining fires this every tick a block is hit.
        if (!isEntityHit && !(isMissHit && !AoSServerConfig.ONLY_FOR_HITS.get())) return;
        if (attack.perform(player)) {
            // The client spend above was only a prediction. Hits are charged by the server itself (AttackEntityEvent);
            // a swing at air never reaches it, so that one is asked for.
            if (attack.hasJustCharged() && !isEntityHit) PacketDistributor.sendToServer(new ActionChargePacket((byte) Action.ATTACK));
        } else {
            event.setCanceled(true);
            event.setSwingHand(false);
            ActionsOfStamina.log("Attack and swing cancelled");
        }
    }

    /** Back to this client's own backend choice until the next server says otherwise. */
    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        StaminaBackends.clearClient();
    }
}

package com.ccr4ft3r.actionsofstamina.core.events.client;

import com.ccr4ft3r.actionsofstamina.content.actions.client.ClientActionTracker;
import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.content.actions.AttackAction;
import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.content.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.compat.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compat.curios.CuriosCompat;
import com.ccr4ft3r.actionsofstamina.compat.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import com.ccr4ft3r.actionsofstamina.network.ActionPerformedPacket;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
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
public final class ClientEvents {

    private ClientEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;
        PlayerActions actions = PlayerActions.get(player);
        if (CuriosCompat.LOADED && player.tickCount % CuriosCompat.CLIENT_REFRESH_INTERVAL == 0) CuriosCompat.refreshWings(player);
        ClientActionTracker.update(player, actions);
        actions.tick(player);
    }

    @SubscribeEvent
    public static void onPlayerAttemptAttack(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        HitResult hitResult = mc.hitResult;
        boolean isEntityHit = hitResult != null && hitResult.getType() == HitResult.Type.ENTITY;
        boolean isMissHit = hitResult != null && hitResult.getType() == HitResult.Type.MISS;

        AttackAction attack = PlayerActions.action(player, VanillaActions.ATTACK, AttackAction.class);
        if (attack == null) return;
        if (BetterCombatCompat.handlesAttacksWith(player.getMainHandItem())) return;
        if (EpicFightCompat.inBattleMode(player)) return;
        if (!isEntityHit && !(isMissHit && !ActionsConfig.ONLY_FOR_HITS.get())) return;
        if (isEntityHit ? attack.performHit(player) : attack.perform(player)) {
            if (!isEntityHit) PacketDistributor.sendToServer(ActionPerformedPacket.of(VanillaActions.ATTACK));
        } else if (!attack.weakens()) {
            event.setCanceled(true);
            event.setSwingHand(false);
            ActionsOfStamina.log("Attack and swing cancelled");
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        StaminaBackends.clearClient();
    }
}

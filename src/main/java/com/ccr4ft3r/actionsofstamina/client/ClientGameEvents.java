package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.curios.CuriosCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.network.ActionPerformedPacket;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
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

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;
        PlayerActions actions = PlayerActions.get(player);
        if (CuriosCompat.LOADED && player.tickCount % CuriosCompat.CLIENT_REFRESH_INTERVAL == 0) actions.refreshCurioWings(player);
        ClientActionTracker.update(player, actions);
        actions.tick(player);
    }

    @SubscribeEvent
    public static void onPlayerAttemptAttack(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (PlayerActions.isExempt(player)) return;

        HitResult hitResult = mc.hitResult;
        boolean isEntityHit = hitResult != null && hitResult.getType() == HitResult.Type.ENTITY;
        boolean isMissHit = hitResult != null && hitResult.getType() == HitResult.Type.MISS;

        if (!(PlayerActions.get(player).getAction(VanillaActions.ATTACK) instanceof AttackAction attack)) return;
        if (BetterCombatCompat.handlesAttacksWith(player.getMainHandItem())) return;
        if (EpicFightCompat.inBattleMode(player)) return;
        if (!isEntityHit && !(isMissHit && !AoSServerConfig.ONLY_FOR_HITS.get())) return;
        if (isEntityHit ? attack.performHit(player) : attack.perform(player)) {
            if (!isEntityHit) PacketDistributor.sendToServer(new ActionPerformedPacket((byte) VanillaActions.ATTACK.index()));
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

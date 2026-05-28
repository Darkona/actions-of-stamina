package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.curios.CuriosCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.network.ActionChargePacket;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ActionsOfStamina.MOD_ID, value = Dist.CLIENT)
public final class ClientGameEvents {

    private ClientGameEvents() {
    }

    /** Client-side prediction tick, local player only (remote players' state is the server's business). */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof LocalPlayer player)) return;
        PlayerActions actions = PlayerActions.get(player);
        // Curios only reports curio changes on the server: the client looks at an interval (curio wings).
        if (CuriosCompat.LOADED && player.tickCount % CuriosCompat.CLIENT_REFRESH_INTERVAL == 0) actions.refreshCurioWings(player);
        ClientActionTracker.update(player, actions);
        actions.tick(player);
    }

    /** Cancels the attack (and swing) when the player can't afford it, unless it lands weakened instead (WEAKEN). */
    @SubscribeEvent
    public static void onPlayerAttemptAttack(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (PlayerActions.isNotExhaustable(player)) return;

        HitResult hitResult = mc.hitResult;
        boolean isEntityHit = hitResult != null && hitResult.getType() == HitResult.Type.ENTITY;
        boolean isMissHit = hitResult != null && hitResult.getType() == HitResult.Type.MISS;

        if (!(PlayerActions.get(player).getAction(Action.ATTACK) instanceof AttackAction attack)) return;
        // Better Combat swings these weapons itself and its compat charges each swing.
        if (BetterCombatCompat.handlesAttacksWith(player.getMainHandItem())) return;
        // Same for Epic Fight's battle mode: its basic attack combo is charged by the Epic Fight compat.
        if (EpicFightCompat.inBattleMode(player)) return;
        // Only swings at an entity cost, and at air unless only_for_hits: mining fires this every tick a block is hit.
        if (!isEntityHit && !(isMissHit && !AoSServerConfig.ONLY_FOR_HITS.get())) return;
        if (isEntityHit ? attack.performHit(player) : attack.perform(player)) {
            // The client spend above was only a prediction. Hits are charged by the server itself (AttackEntityEvent);
            // a swing at air never reaches it, so that one is asked for.
            if (attack.hasJustCharged() && !isEntityHit) PacketHandler.sendToServer(new ActionChargePacket((byte) Action.ATTACK));
        } else if (!attack.weakens()) {
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

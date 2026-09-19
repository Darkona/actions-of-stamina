package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.curios.CuriosCompat;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.network.ActionPerformedPacket;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(modid = ActionsOfStamina.MOD_ID, value = Dist.CLIENT)
public final class ClientGameEvents {

    private ClientGameEvents() {
    }

    /** Client-side prediction tick, local player only (remote players' state is the server's business). */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;
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
        if (PlayerActions.isExempt(player)) return;

        HitResult hitResult = mc.hitResult;
        boolean isEntityHit = hitResult != null && hitResult.getType() == HitResult.Type.ENTITY;
        boolean isMissHit = hitResult != null && hitResult.getType() == HitResult.Type.MISS;

        if (!(PlayerActions.get(player).getAction(VanillaActions.ATTACK) instanceof AttackAction attack)) return;
        // Better Combat swings these weapons itself and its compat charges each swing.
        if (BetterCombatCompat.handlesAttacksWith(player.getMainHandItem())) return;
        // A spear's stab is charged by the server when it lands (PiercingWeaponMixin), whatever this client aims at: here
        // it is only refused, unless it lands weakened instead.
        if (player.getMainHandItem().has(DataComponents.PIERCING_WEAPON)) {
            if (!attack.canPerform(player) && !attack.weakens()) {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
            return;
        }
        // Only swings at an entity cost, and at air unless only_for_hits: mining fires this every tick a block is hit.
        if (!isEntityHit && !(isMissHit && !AoSServerConfig.ONLY_FOR_HITS.get())) return;
        // A hit on an entity may be a mace smash, charged at its own cost; a swing at air never is.
        if (isEntityHit ? attack.performHit(player) : attack.perform(player)) {
            // The client spend above was only a prediction. Hits are counted and charged by the server itself
            // (AttackEntityEvent); a swing at air never reaches it, so the server is told to count that one, and its
            // own count decides when the charge is due.
            if (!isEntityHit) ClientPacketDistributor.sendToServer(new ActionPerformedPacket((byte) VanillaActions.ATTACK.index()));
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

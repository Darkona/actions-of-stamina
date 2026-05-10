package com.ccr4ft3r.actionsofstamina.events;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionProvider;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.network.BackendSyncPacket;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalBackend;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Common (both-sides) game-bus handlers. The client-side counterpart (local player tick, key input, attack key)
 * is {@code ClientGameEvents}.
 */
@EventBusSubscriber(modid = ActionsOfStamina.MOD_ID)
public final class PlayerEventHandler {

    private PlayerEventHandler() {
    }

    @SubscribeEvent
    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        StaminaBackends.onServerStarting();
    }

    /** Server-side action tick, then the internal stamina's own tick. The local player's client tick is elsewhere. */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerTickEvent(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        PlayerActions.get(player).tick(player);
        if (StaminaBackends.server().kind() == StaminaBackend.Kind.INTERNAL) InternalBackend.INSTANCE.tick(player);
    }

    /**
     * Hits on an entity are charged here, on the server: a client can't skip paying by not asking. A hit that can't
     * be paid for doesn't land (the client already dropped the swing if it knew).
     */
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || PlayerActions.isNotExhaustable(player)) return;
        Action attack = PlayerActions.get(player).getAction(Action.ATTACK);
        if (attack == null) return;
        // Better Combat's swings and Epic Fight's battle-mode combo are charged by their compats.
        if (BetterCombatCompat.handlesAttacksWith(player.getMainHandItem()) || EpicFightCompat.inBattleMode(player)) return;
        if (!attack.perform(player)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void shieldUsage(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getItemStack().getItem() instanceof ShieldItem)) return;
        Player player = event.getEntity();
        if (PlayerActions.isNotExhaustable(player)) return;
        Action shield = PlayerActions.get(player).getAction(Action.SHIELD);
        if (shield != null && !shield.canPerform(player)) {
            event.setCanceled(true);
        }
    }

    /** Login, respawn and dimension change: rebuild the actions from the current config and resync the bar. */
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        PlayerActions actions = PlayerActions.get(player);
        actions.clearActions(player);
        ActionProvider.addEnabledActions(actions);
        if (player instanceof ServerPlayer) InternalBackend.data(player).markForSync();
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new BackendSyncPacket(StaminaBackends.server().kind()));
        }
    }
}

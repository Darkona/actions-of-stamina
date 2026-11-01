package com.ccr4ft3r.actionsofstamina.core.events;

import com.ccr4ft3r.actionsofstamina.content.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.content.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.content.actions.Action;
import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.compat.curios.CuriosCompat;
import com.ccr4ft3r.actionsofstamina.compat.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.compat.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.content.stamina.InternalBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.network.ActionsPackets;
import com.ccr4ft3r.actionsofstamina.network.BackendSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = ActionsOfStamina.MOD_ID)
public final class CommonEvents {

    private CommonEvents() {
    }

    @FunctionalInterface
    private interface Hook<E> {
        void run(Action action, Player player, E event);
    }

    private static <E extends Event> void dispatch(@Nullable Player player, ActionType type, E event, Hook<E> hook) {
        if (event.isCancelable() && event.isCanceled()) return;
        Action action = PlayerActions.action(player, type);
        if (action != null) hook.run(action, player, event);
    }

    @SubscribeEvent
    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        StaminaBackends.onServerStarting();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) return;
        PlayerActions.get(player).tick(player);
        if (StaminaBackends.server().kind() == StaminaBackend.Kind.INTERNAL) InternalBackend.INSTANCE.tick(player);
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) dispatch(player, VanillaActions.ATTACK, event, Action::onAttack);
    }

    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        dispatch(player, VanillaActions.SHIELD, event, Action::onUseItem);
        dispatch(player, VanillaActions.DRAW, event, Action::onUseItem);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onUseItemLate(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        dispatch(player, VanillaActions.THROW, event, Action::onUseItem);
        dispatch(player, VanillaActions.FISH, event, Action::onUseItem);
        dispatch(player, VanillaActions.ELYTRA, event, Action::onUseItem);
    }

    @SubscribeEvent
    public static void onUseBlock(PlayerInteractEvent.RightClickBlock event) {
        dispatch(event.getEntity(), VanillaActions.BRUSH, event, Action::onUseBlock);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onStopUsing(LivingEntityUseItemEvent.Stop event) {
        if (!(event.getEntity() instanceof Player player)) return;
        dispatch(player, VanillaActions.THROW, event, Action::onStopUsing);
        dispatch(player, VanillaActions.RIPTIDE, event, Action::onStopUsing);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        dispatch(event.getPlayer(), VanillaActions.TILL, event, Action::onToolModification);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (!event.getPlayer().level().isClientSide()) dispatch(event.getPlayer(), VanillaActions.MINE, event, Action::onBlockBroken);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) dispatch(player, VanillaActions.BUILD, event, Action::onBlockPlaced);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        dispatch(event.getEntity(), VanillaActions.MINE, event, Action::onBreakSpeed);
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide() && !player.isLocalPlayer()) return;
        PlayerActions actions = PlayerActions.get(player);
        actions.clearActions(player);
        PlayerActions.addEnabledActions(actions);
        CuriosCompat.refreshWings(player);
        EpicFightCompat.listen(player);
        if (player instanceof ServerPlayer serverPlayer) {
            InternalBackend.data(player).markForSync();
            ParagliderCompat.onJoin(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) return;
        InternalBackend.data(event.getEntity()).copyFrom(InternalBackend.data(event.getOriginal()));
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ActionsPackets.sendToPlayer(player, new BackendSyncPacket(StaminaBackends.server().kind()));
        }
    }
}

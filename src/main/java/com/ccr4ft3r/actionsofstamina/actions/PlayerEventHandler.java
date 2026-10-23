package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.compatibility.curios.CuriosCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.stamina.InternalBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = ActionsOfStamina.MOD_ID)
public final class PlayerEventHandler {

    private PlayerEventHandler() {
    }

    @FunctionalInterface
    private interface Hook<E> {
        void run(Action action, Player player, E event);
    }

    private static <E> void dispatch(@Nullable Player player, ActionType type, E event, Hook<E> hook) {
        Action action = PlayerActions.action(player, type);
        if (action != null) hook.run(action, player, event);
    }

    @SubscribeEvent
    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        StaminaBackends.onServerStarting();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerTickEvent(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
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
        if (player instanceof ServerPlayer serverPlayer) {
            InternalBackend.data(player).markForSync();
            ParagliderCompat.onJoin(serverPlayer);
        }
    }
}

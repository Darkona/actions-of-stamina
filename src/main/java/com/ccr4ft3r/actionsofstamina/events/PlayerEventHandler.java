package com.ccr4ft3r.actionsofstamina.events;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionProvider;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.brush.BrushAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.draw.DrawAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.fish.FishAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.mine.MineAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.riptide.RiptideAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.throwing.ThrowAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.till.TillAction;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.network.BackendSyncPacket;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalBackend;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
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
     * be paid for doesn't land (the client already dropped the swing if it knew), or with {@code exhausted_mode =
     * WEAKEN} lands weakened (the damage is worked out after this event).
     */
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || PlayerActions.isNotExhaustable(player)) return;
        if (!(PlayerActions.get(player).getAction(Action.ATTACK) instanceof AttackAction attack)) return;
        // Better Combat's swings and Epic Fight's battle-mode combo are charged by their compats.
        if (BetterCombatCompat.handlesAttacksWith(player.getMainHandItem()) || EpicFightCompat.inBattleMode(player)) return;
        if (attack.performHit(player)) return;
        if (attack.weakens()) attack.weaken(player);
        else event.setCanceled(true);
    }

    /** Raising a shield the player can't afford is refused; modded shields are found by their shield-block ability. */
    @SubscribeEvent
    public static void shieldUsage(PlayerInteractEvent.RightClickItem event) {
        if (!event.getItemStack().canPerformAction(ItemAbilities.SHIELD_BLOCK)) return;
        Player player = event.getEntity();
        if (PlayerActions.isNotExhaustable(player)) return;
        Action shield = PlayerActions.get(player).getAction(Action.SHIELD);
        if (shield != null && !shield.canPerform(player)) {
            event.setCanceled(true);
        }
    }

    /**
     * Drawing a bow, loading a crossbow or aiming a trident the player can't afford is refused (both sides), which also
     * keeps a held use button from starting the draw again right after it ran out.
     */
    @SubscribeEvent
    public static void drawUsage(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (PlayerActions.isNotExhaustable(player) || !DrawAction.draws(event.getItemStack())) return;
        Action draw = PlayerActions.get(player).getAction(Action.DRAW);
        if (draw != null && !draw.canPerform(player)) {
            event.setCanceled(true);
        }
    }

    /**
     * Brushing the player can't afford is refused (both sides; a brush is used on a block, not in the air), which also
     * keeps a held use button from starting it again right after it ran out. Only the item use is refused: the block
     * can still be used.
     */
    @SubscribeEvent
    public static void brushUsage(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (PlayerActions.isNotExhaustable(player) || !BrushAction.brushes(event.getItemStack())) return;
        Action brush = PlayerActions.get(player).getAction(Action.BRUSH);
        if (brush != null && !brush.canPerform(player)) {
            event.setUseItem(TriState.FALSE);
        }
    }

    /** A snowball, egg, ender pearl or throwable potion thrown on use (both sides); one the player can't afford isn't thrown. */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void throwOnUse(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (PlayerActions.isNotExhaustable(player) || !ThrowAction.throwsOnUse(event.getItemStack(), player)) return;
        Action throwing = PlayerActions.get(player).getAction(Action.THROW);
        if (throwing != null && !throwing.perform(player)) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    /**
     * A trident released after aiming (both sides; the event comes before the item's release): charged as a throw, and
     * one the player can't afford isn't thrown (cancelling skips the release, the aim just ends).
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void throwOnRelease(LivingEntityUseItemEvent.Stop event) {
        if (!(event.getEntity() instanceof Player player) || PlayerActions.isNotExhaustable(player)) return;
        ItemStack stack = event.getItem();
        if (!ThrowAction.throwsOnRelease(stack, player, event.getDuration())) return;
        Action throwing = PlayerActions.get(player).getAction(Action.THROW);
        if (throwing != null && !throwing.perform(player)) {
            event.setCanceled(true);
        }
    }

    /**
     * A Riptide trident released after aiming (both sides; the event comes before the item's release): charged as a
     * launch, and one the player can't afford doesn't happen (cancelling skips the release, the aim just ends).
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void riptideOnRelease(LivingEntityUseItemEvent.Stop event) {
        if (!(event.getEntity() instanceof Player player) || PlayerActions.isNotExhaustable(player)) return;
        if (!RiptideAction.launchesOnRelease(event.getItem(), player, event.getDuration())) return;
        Action riptide = PlayerActions.get(player).getAction(Action.RIPTIDE);
        if (riptide != null && !riptide.perform(player)) {
            event.setCanceled(true);
        }
    }

    /**
     * Casting a fishing rod or reeling it in (both sides; any item with the rod's cast ability): charged on use, and one
     * the player can't afford doesn't happen (the line stays where it is).
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void fishOnUse(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (PlayerActions.isNotExhaustable(player) || !FishAction.isRod(event.getItemStack())) return;
        Action fish = PlayerActions.get(player).getAction(Action.FISH);
        if (fish != null && !fish.perform(player)) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    /**
     * Tilling, making a path, stripping a log, scraping or unwaxing copper (after every other mod had its say; a
     * simulated check is never charged nor refused). The server charges it and refuses it without the stamina; the
     * client only refuses it, so it doesn't show a change the server won't make.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void toolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.isSimulated() || !TillAction.charges(event.getItemAbility())) return;
        Player player = event.getPlayer();
        if (PlayerActions.isNotExhaustable(player)) return;
        Action till = PlayerActions.get(player).getAction(Action.TILL);
        if (till == null || !TillAction.changesBlock(event)) return;
        if (player.level().isClientSide() ? !till.canPerform(player) : !till.perform(player)) event.setCanceled(true);
    }

    /** Server: a block broken by a player (after every other mod had its say: a cancelled break isn't charged). */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void blockBroken(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide() || PlayerActions.isNotExhaustable(player)) return;
        if (PlayerActions.get(player).getAction(Action.MINE) instanceof MineAction mine) {
            mine.mined(player, event.getState().getDestroySpeed(event.getLevel(), event.getPos()));
        }
    }

    /**
     * Server: a block (or a multi-block, such as a bed or a door, once) placed by a player; never refused. NeoForge
     * posts this event for every block any item changes on use (a hoe tilling, an axe stripping, bone meal, flint and
     * steel), so only a block placed from the item held for it counts.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void blockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || PlayerActions.isNotExhaustable(player)) return;
        Action build = PlayerActions.get(player).getAction(Action.BUILD);
        if (build == null) return;
        Item placed = event.getPlacedBlock().getBlock().asItem();
        if (placed == Items.AIR || !player.getMainHandItem().is(placed) && !player.getOffhandItem().is(placed)) return;
        build.perform(player);
    }

    /**
     * {@code block_when_exhausted}: mining slows down while the player can't afford it. Both sides, every tick of
     * mining: the client's break progress must match the server's.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void breakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (PlayerActions.isNotExhaustable(player)) return;
        if (PlayerActions.get(player).getAction(Action.MINE) instanceof MineAction mine) {
            float multiplier = mine.breakSpeedMultiplier(player);
            if (multiplier < 1.0f) event.setNewSpeed(event.getNewSpeed() * multiplier);
        }
    }

    /**
     * A firework rocket used while fall-flying boosts the flight (both sides, as the item itself only boosts then).
     * Recorded here, with the rocket's flight duration, rather than by looking for rocket entities every tick.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void rocketBoost(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getItemStack().getItem() instanceof FireworkRocketItem)) return;
        Player player = event.getEntity();
        if (!player.isFallFlying() || PlayerActions.isNotExhaustable(player)) return;
        if (PlayerActions.get(player).getAction(Action.ELYTRA) instanceof ElytraAction elytra) elytra.boost(player, event.getItemStack());
    }

    /** Login, respawn and dimension change: rebuild the actions from the current config and resync the bar. */
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        PlayerActions actions = PlayerActions.get(player);
        actions.clearActions(player);
        ActionProvider.addEnabledActions(actions);
        actions.refreshCurioWings(player);
        if (player instanceof ServerPlayer serverPlayer) {
            InternalBackend.data(player).markForSync();
            ParagliderCompat.onJoin(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new BackendSyncPacket(StaminaBackends.server().kind()));
        }
    }
}

package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions.BrushAction;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions.DrawAction;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.stamina.InternalBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ActionsOfStamina.MOD_ID)
public final class PlayerEventHandler {

    private static final TagKey<Item> THROWABLES = ItemTags.create(ActionsOfStamina.id("throwables"));

    private PlayerEventHandler() {
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
        if (!(event.getEntity() instanceof ServerPlayer player) || PlayerActions.isExempt(player)) return;
        if (!(PlayerActions.get(player).getAction(VanillaActions.ATTACK) instanceof AttackAction attack)) return;
        if (BetterCombatCompat.handlesAttacksWith(player.getMainHandItem()) || EpicFightCompat.inBattleMode(player)) return;
        if (attack.weakens()) attack.refreshWeakened(player);
        if (attack.performHit(player)) return;
        if (attack.weakens()) attack.weaken(player);
        else event.setCanceled(true);
    }

    @SubscribeEvent
    public static void shieldUsage(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().canPerformAction(ItemAbilities.SHIELD_BLOCK) && !PlayerActions.canPerform(event.getEntity(), VanillaActions.SHIELD)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void drawUsage(PlayerInteractEvent.RightClickItem event) {
        if (DrawAction.draws(event.getItemStack()) && !PlayerActions.canPerform(event.getEntity(), VanillaActions.DRAW)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void brushUsage(PlayerInteractEvent.RightClickBlock event) {
        if (BrushAction.brushes(event.getItemStack()) && !PlayerActions.canPerform(event.getEntity(), VanillaActions.BRUSH)) {
            event.setUseItem(TriState.FALSE);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void throwOnUse(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (throwsOnUse(event.getItemStack(), player) && !PlayerActions.perform(player, VanillaActions.THROW)) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void throwOnRelease(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof Player player && throwsOnRelease(event.getItem(), player, event.getDuration())
                && !PlayerActions.perform(player, VanillaActions.THROW)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void riptideOnRelease(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof Player player && launchesRiptide(event.getItem(), player, event.getDuration())
                && !PlayerActions.perform(player, VanillaActions.RIPTIDE)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void fishOnUse(PlayerInteractEvent.RightClickItem event) {
        if (isRod(event.getItemStack()) && !PlayerActions.perform(event.getEntity(), VanillaActions.FISH)) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void toolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.isSimulated() || !tillCharges(event.getItemAbility())) return;
        Player player = event.getPlayer();
        if (PlayerActions.isExempt(player)) return;
        Action till = PlayerActions.get(player).getAction(VanillaActions.TILL);
        if (till == null || !tillChangesBlock(event)) return;
        if (player.level().isClientSide() ? !till.canPerform(player) : !till.perform(player)) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void blockBroken(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide() || PlayerActions.isExempt(player)) return;
        if (PlayerActions.get(player).getAction(VanillaActions.MINE) instanceof MineAction mine) {
            mine.mined(player, event.getState().getDestroySpeed(event.getLevel(), event.getPos()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void blockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || PlayerActions.isExempt(player)) return;
        Action build = PlayerActions.get(player).getAction(VanillaActions.BUILD);
        if (build == null) return;
        Item placed = event.getPlacedBlock().getBlock().asItem();
        if (placed == Items.AIR || !player.getMainHandItem().is(placed) && !player.getOffhandItem().is(placed)) return;
        build.perform(player);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void breakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (PlayerActions.isExempt(player)) return;
        if (PlayerActions.get(player).getAction(VanillaActions.MINE) instanceof MineAction mine) {
            float multiplier = mine.breakSpeedMultiplier(player);
            if (multiplier < 1.0f) event.setNewSpeed(event.getNewSpeed() * multiplier);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void rocketBoost(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getItemStack().getItem() instanceof FireworkRocketItem)) return;
        Player player = event.getEntity();
        if (!player.isFallFlying() || PlayerActions.isExempt(player)) return;
        if (PlayerActions.get(player).getAction(VanillaActions.ELYTRA) instanceof ElytraAction elytra) elytra.boost(player, event.getItemStack());
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide() && !player.isLocalPlayer()) return;
        PlayerActions actions = PlayerActions.get(player);
        actions.clearActions(player);
        PlayerActions.addEnabledActions(actions);
        actions.refreshCurioWings(player);
        if (player instanceof ServerPlayer serverPlayer) {
            InternalBackend.data(player).markForSync();
            ParagliderCompat.onJoin(serverPlayer);
        }
    }

    private static boolean throwsOnUse(ItemStack stack, Player player) {
        return stack.is(THROWABLES) && stack.getUseDuration(player) <= 0;
    }

    private static boolean throwsOnRelease(ItemStack stack, Player player, int remainingTicks) {
        if (!stack.is(THROWABLES)) return false;
        if (!(stack.getItem() instanceof TridentItem)) return true;
        return stack.getUseDuration(player) - remainingTicks >= TridentItem.THROW_THRESHOLD_TIME && EnchantmentHelper.getTridentSpinAttackStrength(stack, player) <= 0;
    }

    private static boolean launchesRiptide(ItemStack stack, Player player, int remainingTicks) {
        if (!(stack.getItem() instanceof TridentItem)) return false;
        return stack.getUseDuration(player) - remainingTicks >= TridentItem.THROW_THRESHOLD_TIME
                && EnchantmentHelper.getTridentSpinAttackStrength(stack, player) > 0 && player.isInWaterOrRain()
                && stack.getDamageValue() < stack.getMaxDamage() - 1;
    }

    private static boolean isRod(ItemStack stack) {
        return stack.canPerformAction(ItemAbilities.FISHING_ROD_CAST);
    }

    private static boolean tillCharges(ItemAbility ability) {
        return ability == ItemAbilities.HOE_TILL || ability == ItemAbilities.SHOVEL_FLATTEN || ability == ItemAbilities.AXE_STRIP
                || ability == ItemAbilities.AXE_SCRAPE || ability == ItemAbilities.AXE_WAX_OFF;
    }

    private static boolean tillChangesBlock(BlockEvent.BlockToolModificationEvent event) {
        BlockState original = event.getState();
        BlockState result = event.getFinalState();
        if (result == original) result = original.getBlock().getToolModifiedState(original, event.getContext(), event.getItemAbility(), true);
        if (result == null || result == original) return false;
        return event.getItemAbility() != ItemAbilities.SHOVEL_FLATTEN || event.getLevel().getBlockState(event.getPos().above()).isAir();
    }
}

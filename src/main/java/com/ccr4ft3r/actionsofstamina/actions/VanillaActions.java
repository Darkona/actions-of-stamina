package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;
import static com.ccr4ft3r.actionsofstamina.ActionsOfStamina.id;

public final class VanillaActions {

    public static final ActionType ATTACK = ActionType.register(id("attack"), ActionsConfig.ATTACK, AttackAction::new);
    public static final ActionType JUMP = ActionType.register(id("jump"), ActionsConfig.JUMP);
    public static final ActionType SPRINT = ActionType.register(id("sprint"), ActionsConfig.SPRINT, SprintAction::new).flag(1 << 1);
    public static final ActionType SWIM = ActionType.register(id("swim"), ActionsConfig.SWIM, SwimAction::new).flag(1 << 3);
    public static final ActionType ELYTRA = ActionType.register(id("elytra"), ActionsConfig.ELYTRA, ElytraAction::new).flag(1 << 4);
    public static final ActionType CRAWL = ActionType.register(id("crawl"), ActionsConfig.CRAWL, CrawlAction::new).flag(1 << 2);
    public static final ActionType SHIELD = ActionType.register(id("shield"), ActionsConfig.SHIELD, ShieldAction::new).flag(1 << 5);
    public static final ActionType DRAW = ActionType.register(id("draw"), ActionsConfig.DRAW, DrawAction::new).flag(1 << 9);
    public static final ActionType THROW = ActionType.register(id("throw"), ActionsConfig.THROW, ThrowAction::new);
    public static final ActionType MINE = ActionType.register(id("mine"), ActionsConfig.MINE, MineAction::new);
    public static final ActionType BUILD = ActionType.register(id("build"), ActionsConfig.BUILD, BuildAction::new);
    public static final ActionType CLIMB = ActionType.register(id("climb"), ActionsConfig.CLIMB).flag(1 << 10);
    public static final ActionType ROW = ActionType.register(id("row"), ActionsConfig.ROW).flag(1 << 11);
    public static final ActionType RIPTIDE = ActionType.register(id("riptide"), ActionsConfig.RIPTIDE, RiptideAction::new);
    public static final ActionType FISH = ActionType.register(id("fish"), ActionsConfig.FISH, FishAction::new);
    public static final ActionType TILL = ActionType.register(id("till"), ActionsConfig.TILL, TillAction::new);
    public static final ActionType BRUSH = ActionType.register(id("brush"), ActionsConfig.BRUSH, BrushAction::new).flag(1 << 12);

    public static final TagKey<EntityType<?>> ROWED_BOATS = TagKey.create(Registries.ENTITY_TYPE, ActionsOfStamina.id("rowed_boats"));

    private VanillaActions() {
    }

    public static boolean mayClimbUp(Player player) {
        return player.level().isClientSide() && !player.isLocalPlayer() || PlayerActions.canPerform(player, CLIMB);
    }

    public static boolean drivesRowedBoat(Player player, @Nullable Entity vehicle) {
        return vehicle instanceof Boat boat && vehicle.getControllingPassenger() == player && isRowed(boat);
    }

    public static boolean isRowed(Boat boat) {
        return boat.getType().is(ROWED_BOATS);
    }

    public static boolean mayRow(Player player) {
        return PlayerActions.canPerform(player, ROW);
    }

    public static class SprintAction extends Action {

        public SprintAction(ActionType type) {
            super(type);
        }

        @Override
        public void notPerformingEffects(Player p, PlayerActions a) {
            p.setSprinting(false);
        }
    }

    public static class SwimAction extends Action {

        public SwimAction(ActionType type) {
            super(type);
        }

        @Override
        public void notPerformingEffects(Player player, PlayerActions a) {
            player.setSprinting(false);
            player.setSwimming(false);
        }
    }

    public static class ShieldAction extends Action {

        public ShieldAction(ActionType type) {
            super(type);
        }

        @Override
        public void onUseItem(Player player, PlayerInteractEvent.RightClickItem event) {
            if (event.getItemStack().canPerformAction(ItemAbilities.SHIELD_BLOCK) && !canPerform(player)) event.setCanceled(true);
        }

        @Override
        public void notPerformingEffects(Player player, PlayerActions a) {
            if (player.isUsingItem() && player.getUseItem().canPerformAction(ItemAbilities.SHIELD_BLOCK)) player.stopUsingItem();
        }

    }

    public static class BrushAction extends Action {

        public BrushAction(ActionType type) {
            super(type);
        }

        public static boolean brushes(ItemStack stack) {
            return stack.getUseAnimation() == UseAnim.BRUSH;
        }

        public static boolean isBrushing(Player player) {
            return player.isUsingItem() && brushes(player.getUseItem());
        }

        @Override
        public void onUseBlock(Player player, PlayerInteractEvent.RightClickBlock event) {
            if (brushes(event.getItemStack()) && !canPerform(player)) event.setUseItem(TriState.FALSE);
        }

        @Override
        public void notPerformingEffects(Player player, PlayerActions a) {
            if (isBrushing(player)) player.stopUsingItem();
        }
    }

    public static class DrawAction extends Action {

        public DrawAction(ActionType type) {
            super(type);
        }

        private static boolean isDrawAnim(UseAnim anim) {
            return anim == UseAnim.BOW || anim == UseAnim.SPEAR || anim == UseAnim.CROSSBOW;
        }

        public static boolean draws(ItemStack stack) {
            return isDrawAnim(stack.getUseAnimation()) && !CrossbowItem.isCharged(stack);
        }

        public static boolean isDrawing(Player player) {
            return player.isUsingItem() && isDrawAnim(player.getUseItem().getUseAnimation());
        }

        @Override
        public void onUseItem(Player player, PlayerInteractEvent.RightClickItem event) {
            if (draws(event.getItemStack()) && !canPerform(player)) event.setCanceled(true);
        }

        @Override
        public void notPerformingEffects(Player player, PlayerActions a) {
            if (isDrawing(player)) player.stopUsingItem();
        }
    }

    public static class ThrowAction extends Action {

        private static final TagKey<Item> THROWABLES = ItemTags.create(ActionsOfStamina.id("throwables"));

        public ThrowAction(ActionType type) {
            super(type);
        }

        @Override
        public void onUseItem(Player player, PlayerInteractEvent.RightClickItem event) {
            ItemStack stack = event.getItemStack();
            if (stack.is(THROWABLES) && stack.getUseDuration(player) <= 0 && !perform(player)) deny(event);
        }

        @Override
        public void onStopUsing(Player player, LivingEntityUseItemEvent.Stop event) {
            if (throwsOnRelease(event.getItem(), player, event.getDuration()) && !perform(player)) event.setCanceled(true);
        }

        private static boolean throwsOnRelease(ItemStack stack, Player player, int remainingTicks) {
            if (!stack.is(THROWABLES)) return false;
            if (!(stack.getItem() instanceof TridentItem)) return true;
            return stack.getUseDuration(player) - remainingTicks >= TridentItem.THROW_THRESHOLD_TIME && EnchantmentHelper.getTridentSpinAttackStrength(stack, player) <= 0;
        }
    }

    public static class RiptideAction extends Action {

        public RiptideAction(ActionType type) {
            super(type);
        }

        @Override
        public void onStopUsing(Player player, LivingEntityUseItemEvent.Stop event) {
            if (launchesRiptide(event.getItem(), player, event.getDuration()) && !perform(player)) event.setCanceled(true);
        }

        private static boolean launchesRiptide(ItemStack stack, Player player, int remainingTicks) {
            if (!(stack.getItem() instanceof TridentItem)) return false;
            return stack.getUseDuration(player) - remainingTicks >= TridentItem.THROW_THRESHOLD_TIME
                    && EnchantmentHelper.getTridentSpinAttackStrength(stack, player) > 0 && player.isInWaterOrRain()
                    && stack.getDamageValue() < stack.getMaxDamage() - 1;
        }
    }

    public static class FishAction extends Action {

        public FishAction(ActionType type) {
            super(type);
        }

        @Override
        public void onUseItem(Player player, PlayerInteractEvent.RightClickItem event) {
            if (event.getItemStack().canPerformAction(ItemAbilities.FISHING_ROD_CAST) && !perform(player)) deny(event);
        }
    }

    public static class TillAction extends Action {

        public TillAction(ActionType type) {
            super(type);
        }

        @Override
        public void onToolModification(Player player, BlockEvent.BlockToolModificationEvent event) {
            if (event.isSimulated() || !tillCharges(event.getItemAbility()) || !tillChangesBlock(event)) return;
            if (player.level().isClientSide() ? !canPerform(player) : !perform(player)) event.setCanceled(true);
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

    public static class BuildAction extends Action {

        public BuildAction(ActionType type) {
            super(type);
        }

        @Override
        public void onBlockPlaced(Player player, BlockEvent.EntityPlaceEvent event) {
            Item placed = event.getPlacedBlock().getBlock().asItem();
            if (placed == Items.AIR || !player.getMainHandItem().is(placed) && !player.getOffhandItem().is(placed)) return;
            perform(player);
        }
    }

    private static void deny(PlayerInteractEvent.RightClickItem event) {
        event.setCancellationResult(InteractionResult.FAIL);
        event.setCanceled(true);
    }
}

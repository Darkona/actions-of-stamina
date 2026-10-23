package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;
import static com.ccr4ft3r.actionsofstamina.ActionsOfStamina.id;

public final class VanillaActions {

    public static final ActionType ATTACK = ActionType.register(id("attack"), AoSServerConfig.ATTACK, AttackAction::new);
    public static final ActionType JUMP = ActionType.register(id("jump"), AoSServerConfig.JUMP, Action::new);
    public static final ActionType SPRINT = ActionType.register(id("sprint"), AoSServerConfig.SPRINT, SprintAction::new);
    public static final ActionType SWIM = ActionType.register(id("swim"), AoSServerConfig.SWIM, SwimAction::new);
    public static final ActionType ELYTRA = ActionType.register(id("elytra"), AoSServerConfig.ELYTRA, ElytraAction::new);
    public static final ActionType CRAWL = ActionType.register(id("crawl"), AoSServerConfig.CRAWL, CrawlAction::new);
    public static final ActionType SHIELD = ActionType.register(id("shield"), AoSServerConfig.SHIELD, ShieldAction::new);
    public static final ActionType DRAW = ActionType.register(id("draw"), AoSServerConfig.DRAW, DrawAction::new);
    public static final ActionType THROW = ActionType.register(id("throw"), AoSServerConfig.THROW, Action::new);
    public static final ActionType MINE = ActionType.register(id("mine"), AoSServerConfig.MINE, MineAction::new);
    public static final ActionType BUILD = ActionType.register(id("build"), AoSServerConfig.BUILD, Action::new);
    public static final ActionType CLIMB = ActionType.register(id("climb"), AoSServerConfig.CLIMB, Action::new);
    public static final ActionType ROW = ActionType.register(id("row"), AoSServerConfig.ROW, Action::new);
    public static final ActionType RIPTIDE = ActionType.register(id("riptide"), AoSServerConfig.RIPTIDE, Action::new);
    public static final ActionType FISH = ActionType.register(id("fish"), AoSServerConfig.FISH, Action::new);
    public static final ActionType TILL = ActionType.register(id("till"), AoSServerConfig.TILL, Action::new);
    public static final ActionType BRUSH = ActionType.register(id("brush"), AoSServerConfig.BRUSH, BrushAction::new);

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
        public void notPerformingEffects(Player player, PlayerActions a) {
            if (isDrawing(player)) player.stopUsingItem();
        }
    }
}

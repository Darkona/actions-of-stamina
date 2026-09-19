package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.network.ActionPerformedPacket;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.exhaust;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayer;

/**
 * The attack's charge once every few attacks keeps one count on the server for hits and misses alike: hits through the
 * attack itself, misses through the packet the client sends for a swing at the air. On whichever backend is active;
 * default attack config (1 feather every 3 attacks). Also which attacks WEAKEN weakens, with and without
 * {@code weaken_non_weapons}.
 */
public class AttackChargeTests {

    /** The player's attack action, with a sword in hand so the attacks count. */
    private static AttackAction attack(ServerPlayer player) {
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
        return (AttackAction) PlayerActions.get(player).getAction(VanillaActions.ATTACK);
    }

    @GameTest(template = "empty")
    public static void missesCountTowardsTheCharge(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        AttackAction attack = attack(player);
        helper.assertTrue(attack != null, "attack action exists");
        StaminaBackend backend = StaminaBackends.server();
        int cost = AoSServerConfig.ATTACK.cost();
        int start = backend.stamina(player);
        ActionPerformedPacket.apply(player, VanillaActions.ATTACK.index());
        ActionPerformedPacket.apply(player, VanillaActions.ATTACK.index());
        helper.assertValueEqual(backend.stamina(player), start, "two misses charge nothing yet");
        attack.performHit(player);
        helper.assertValueEqual(backend.stamina(player), start - cost, "the third attack, a hit, is charged");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void aChargedMissStartsANewCount(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        AttackAction attack = attack(player);
        helper.assertTrue(attack != null, "attack action exists");
        StaminaBackend backend = StaminaBackends.server();
        int cost = AoSServerConfig.ATTACK.cost();
        int start = backend.stamina(player);
        attack.performHit(player);
        attack.performHit(player);
        ActionPerformedPacket.apply(player, VanillaActions.ATTACK.index());
        helper.assertValueEqual(backend.stamina(player), start - cost, "the third attack, a miss, is charged");
        attack.performHit(player);
        helper.assertValueEqual(backend.stamina(player), start - cost, "the fourth attack, a hit, starts a new count");
        helper.succeed();
    }

    /**
     * Whether an exhausted player with {@code stack} in hand ends up weakened after the periodic check, with WEAKEN on and
     * {@code weaken_non_weapons} as given. The action takes the switches directly: writing them to the config would race
     * with the reload the file watcher does after every save.
     */
    private static boolean weakenedWith(GameTestHelper helper, ItemStack stack, boolean weakenNonWeapons) {
        ServerPlayer player = survivalPlayer(helper);
        exhaust(StaminaBackends.server(), player);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        player.tickCount = 10;
        new AttackAction(VanillaActions.ATTACK, true, weakenNonWeapons).tick(player, PlayerActions.get(player));
        return player.getAttribute(Attributes.ATTACK_DAMAGE).hasModifier(AttackAction.WEAKEN_ID);
    }

    @GameTest(template = "empty")
    public static void weakenReachesBareHandsByDefault(GameTestHelper helper) {
        helper.assertTrue(weakenedWith(helper, ItemStack.EMPTY, true), "bare hands are weakened (weaken_non_weapons on)");
        helper.assertTrue(weakenedWith(helper, new ItemStack(Items.IRON_SWORD), true), "a sword is weakened (weaken_non_weapons on)");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void weakenSparesFreeAttacksWhenOff(GameTestHelper helper) {
        helper.assertFalse(weakenedWith(helper, ItemStack.EMPTY, false), "bare hands are not weakened (weaken_non_weapons off)");
        helper.assertTrue(weakenedWith(helper, new ItemStack(Items.IRON_SWORD), false), "a sword is still weakened (weaken_non_weapons off)");
        helper.succeed();
    }

    /** A zombie three blocks in front of the player's eyes, within a spear's reach, that stays where it is. */
    private static Zombie stabTarget(GameTestHelper helper, ServerPlayer player) {
        Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new Vec3(0.5, 3, 3.5));
        zombie.setNoGravity(true);
        player.setYRot(0);
        player.setXRot(0);
        return zombie;
    }

    /** One stab of the spear in hand, as the server runs it for the stab packet. */
    private static void stab(ServerPlayer player) {
        player.getMainHandItem().get(DataComponents.PIERCING_WEAPON).attack(player, EquipmentSlot.MAINHAND);
    }

    @GameTest(template = "empty")
    public static void spearStabsAreChargedAsAttacks(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SPEAR));
        // Better Combat swings spears itself, charged by its compat (CompatTests).
        if (BetterCombatCompat.handlesAttacksWith(player.getMainHandItem())) {
            helper.succeed();
            return;
        }
        StaminaBackend backend = StaminaBackends.server();
        int cost = AoSServerConfig.ATTACK.cost();
        int start = backend.stamina(player);
        stab(player);
        helper.assertValueEqual(backend.stamina(player), start, "a stab at the air is free (only_for_hits)");

        Zombie zombie = stabTarget(helper, player);
        for (int i = 0; i < 3; i++) {
            zombie.invulnerableTime = 0;
            stab(player);
        }
        helper.assertValueEqual(backend.stamina(player), start - cost, "the third stab that lands is charged");
        helper.assertTrue(zombie.getHealth() < zombie.getMaxHealth(), "the stabs hurt the target");

        exhaust(backend, player);
        zombie.setHealth(zombie.getMaxHealth());
        zombie.invulnerableTime = 0;
        stab(player);
        helper.assertValueEqual(zombie.getHealth(), zombie.getMaxHealth(), "a stab that can't be paid hits nothing");
        zombie.discard();
        helper.succeed();
    }
}

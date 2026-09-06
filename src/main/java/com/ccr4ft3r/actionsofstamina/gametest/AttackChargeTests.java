package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.network.ActionPerformedPacket;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.assertFalse;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.assertTrue;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.assertValueEqual;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.exhaust;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayer;

/**
 * The attack's charge once every few attacks keeps one count on the server for hits and misses alike: hits through the
 * attack itself, misses through the packet the client sends for a swing at the air. On whichever backend is active;
 * default attack config (1 feather every 3 attacks). Also which attacks WEAKEN weakens, with and without
 * {@code weaken_non_weapons}.
 */
@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
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
        assertTrue(helper, attack != null, "attack action exists");
        StaminaBackend backend = StaminaBackends.server();
        int cost = AoSServerConfig.ATTACK.cost();
        int start = backend.stamina(player);
        ActionPerformedPacket.apply(player, VanillaActions.ATTACK.index());
        ActionPerformedPacket.apply(player, VanillaActions.ATTACK.index());
        assertValueEqual(helper, backend.stamina(player), start, "two misses charge nothing yet");
        attack.performHit(player);
        assertValueEqual(helper, backend.stamina(player), start - cost, "the third attack, a hit, is charged");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void aChargedMissStartsANewCount(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        AttackAction attack = attack(player);
        assertTrue(helper, attack != null, "attack action exists");
        StaminaBackend backend = StaminaBackends.server();
        int cost = AoSServerConfig.ATTACK.cost();
        int start = backend.stamina(player);
        attack.performHit(player);
        attack.performHit(player);
        ActionPerformedPacket.apply(player, VanillaActions.ATTACK.index());
        assertValueEqual(helper, backend.stamina(player), start - cost, "the third attack, a miss, is charged");
        attack.performHit(player);
        assertValueEqual(helper, backend.stamina(player), start - cost, "the fourth attack, a hit, starts a new count");
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
        return player.getAttribute(Attributes.ATTACK_DAMAGE).getModifier(AttackAction.WEAKEN_ID) != null;
    }

    @GameTest(template = "empty")
    public static void weakenReachesBareHandsByDefault(GameTestHelper helper) {
        assertTrue(helper, weakenedWith(helper, ItemStack.EMPTY, true), "bare hands are weakened (weaken_non_weapons on)");
        assertTrue(helper, weakenedWith(helper, new ItemStack(Items.IRON_SWORD), true), "a sword is weakened (weaken_non_weapons on)");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void weakenSparesFreeAttacksWhenOff(GameTestHelper helper) {
        assertFalse(helper, weakenedWith(helper, ItemStack.EMPTY, false), "bare hands are not weakened (weaken_non_weapons off)");
        assertTrue(helper, weakenedWith(helper, new ItemStack(Items.IRON_SWORD), false), "a sword is still weakened (weaken_non_weapons off)");
        helper.succeed();
    }
}

package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.assertValueEqual;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayer;

/**
 * The attack's charge once every few attacks keeps one count on the server for hits and misses alike: hits through the
 * attack itself, misses through the packet the client sends for a swing at the air. On whichever backend is active;
 * default attack config (1 feather every 3 attacks).
 */
@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class AttackChargeTests {

    /** The player's attack action, with a sword in hand so the attacks count. */
    private static AttackAction attack(ServerPlayer player) {
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
        return (AttackAction) PlayerActions.get(player).getAction(Action.ATTACK);
    }

    @GameTest(template = "empty")
    public static void missesCountTowardsTheCharge(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        AttackAction attack = attack(player);
        helper.assertTrue(attack != null, "attack action exists");
        StaminaBackend backend = StaminaBackends.server();
        int cost = AoSServerConfig.ATTACK.cost();
        int start = backend.stamina(player);
        ActionPerformedPacket.apply(player, Action.ATTACK);
        ActionPerformedPacket.apply(player, Action.ATTACK);
        assertValueEqual(helper, backend.stamina(player), start, "two misses charge nothing yet");
        attack.performHit(player);
        assertValueEqual(helper, backend.stamina(player), start - cost, "the third attack, a hit, is charged");
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
        ActionPerformedPacket.apply(player, Action.ATTACK);
        assertValueEqual(helper, backend.stamina(player), start - cost, "the third attack, a miss, is charged");
        attack.performHit(player);
        assertValueEqual(helper, backend.stamina(player), start - cost, "the fourth attack, a hit, starts a new count");
        helper.succeed();
    }
}

package com.ccr4ft3r.actionsofstamina.infrastructure.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.content.actions.AttackAction;
import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.content.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import static com.ccr4ft3r.actionsofstamina.infrastructure.gametest.TestSupport.exhaust;
import static com.ccr4ft3r.actionsofstamina.infrastructure.gametest.TestSupport.survivalPlayer;

@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class AttackChargeTests {

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
        int cost = ActionsConfig.ATTACK.cost();
        int start = backend.stamina(player);
        PlayerActions.perform(player, VanillaActions.ATTACK);
        PlayerActions.perform(player, VanillaActions.ATTACK);
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
        int cost = ActionsConfig.ATTACK.cost();
        int start = backend.stamina(player);
        attack.performHit(player);
        attack.performHit(player);
        PlayerActions.perform(player, VanillaActions.ATTACK);
        helper.assertValueEqual(backend.stamina(player), start - cost, "the third attack, a miss, is charged");
        attack.performHit(player);
        helper.assertValueEqual(backend.stamina(player), start - cost, "the fourth attack, a hit, starts a new count");
        helper.succeed();
    }

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
}

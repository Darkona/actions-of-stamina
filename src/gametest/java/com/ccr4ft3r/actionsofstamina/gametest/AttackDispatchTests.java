package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.content.actions.AttackAction;
import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.content.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.config.compat.BetterCombatConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.AfterBatch;
import net.minecraft.gametest.framework.BeforeBatch;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.exhaust;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.only;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.stamina;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayer;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.survivalPlayerWithout;
import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.switchEnabled;

@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class AttackDispatchTests {

    private static final String VANILLA_MELEE = "vanilla-melee";
    private static boolean swingsWereOn;

    private static Entity target(GameTestHelper helper) {
        return helper.spawn(EntityType.ARMOR_STAND, new BlockPos(2, 2, 2));
    }

    private static boolean weakened(ServerPlayer player) {
        return player.getAttribute(Attributes.ATTACK_DAMAGE).hasModifier(AttackAction.WEAKEN_ID);
    }

    private static ServerPlayer meleePlayer(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        if (EpicFightCompat.LOADED) EpicFightTestHooks.battleMode(player, false);
        return player;
    }

    private static ServerPlayer exhaustedWith(GameTestHelper helper, boolean weakens) {
        ServerPlayer player = meleePlayer(helper);
        only(player, new AttackAction(VanillaActions.ATTACK, weakens, true));
        exhaust(StaminaBackends.server(), player);
        return player;
    }

    @BeforeBatch(batch = VANILLA_MELEE)
    public static void leaveSwingsToVanilla(ServerLevel level) {
        swingsWereOn = BetterCombatConfig.SWING.enabled();
        switchEnabled(BetterCombatConfig.SWING, false);
    }

    @AfterBatch(batch = VANILLA_MELEE)
    public static void restoreSwings(ServerLevel level) {
        switchEnabled(BetterCombatConfig.SWING, swingsWereOn);
    }

    @GameTest(template = "empty", batch = VANILLA_MELEE)
    public static void everyThirdSwordAttackIsChargedOnce(GameTestHelper helper) {
        ServerPlayer player = meleePlayer(helper);
        Entity target = target(helper);
        int times = ActionsConfig.ATTACK.timesPerformedToExhaust();
        int start = stamina(player);
        for (int i = 1; i < times; i++) {
            helper.assertFalse(EventPosts.attack(player, new ItemStack(Items.IRON_SWORD), target).isCanceled(), "attack " + i);
            helper.assertValueEqual(stamina(player), start, "attack " + i + " is not due yet");
        }
        helper.assertFalse(EventPosts.attack(player, new ItemStack(Items.IRON_SWORD), target).isCanceled(), "the due attack goes ahead");
        helper.assertValueEqual(stamina(player), start - ActionsConfig.ATTACK.cost(), "the due attack is charged once");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = VANILLA_MELEE)
    public static void anAttackWithoutStaminaIsCancelledInCancelMode(GameTestHelper helper) {
        ServerPlayer player = exhaustedWith(helper, false);
        helper.assertTrue(EventPosts.attack(player, new ItemStack(Items.IRON_SWORD), target(helper)).isCanceled(), "the attack is cancelled");
        helper.assertFalse(weakened(player), "and nothing is weakened");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = VANILLA_MELEE)
    public static void anAttackWithoutStaminaLandsWeakenedInWeakenMode(GameTestHelper helper) {
        ServerPlayer player = exhaustedWith(helper, true);
        helper.assertFalse(EventPosts.attack(player, new ItemStack(Items.IRON_SWORD), target(helper)).isCanceled(), "the attack goes ahead");
        helper.assertTrue(weakened(player), "weakened");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = VANILLA_MELEE)
    public static void bareHandsAreFreeUnlessConfiguredToCost(GameTestHelper helper) {
        ServerPlayer player = exhaustedWith(helper, false);
        boolean costs = ActionsConfig.ALSO_FOR_NON_WEAPONS.getAsBoolean();
        helper.assertValueEqual(EventPosts.attack(player, ItemStack.EMPTY, target(helper)).isCanceled(), costs, "bare hands cancelled only when they cost");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = VANILLA_MELEE)
    public static void exemptPlayersAreNeverRefusedOrWeakened(GameTestHelper helper) {
        Entity target = target(helper);
        for (GameType mode : new GameType[]{GameType.CREATIVE, GameType.SPECTATOR}) {
            ServerPlayer player = exhaustedWith(helper, false);
            player.setGameMode(mode);
            int start = stamina(player);
            helper.assertFalse(EventPosts.attack(player, new ItemStack(Items.IRON_SWORD), target).isCanceled(), mode + " attack");
            helper.assertValueEqual(stamina(player), start, mode + " pays nothing");
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = VANILLA_MELEE)
    public static void aDisabledAttackActionIsNeverRefused(GameTestHelper helper) {
        ServerPlayer player = survivalPlayerWithout(helper, VanillaActions.ATTACK);
        exhaust(StaminaBackends.server(), player);
        helper.assertTrue(PlayerActions.get(player).getAction(VanillaActions.ATTACK) == null, "no attack action");
        helper.assertFalse(EventPosts.attack(player, new ItemStack(Items.IRON_SWORD), target(helper)).isCanceled(), "the attack goes ahead");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = VANILLA_MELEE)
    public static void aMaceSmashIsChargedOnTheFirstEvent(GameTestHelper helper) {
        ServerPlayer player = meleePlayer(helper);
        player.fallDistance = 5.0F;
        int smash = (int) Math.round(ActionsConfig.ATTACK.cost() * ActionsConfig.MACE_SMASH_MULTIPLIER.getAsDouble());
        int start = stamina(player);
        helper.assertFalse(EventPosts.attack(player, new ItemStack(Items.MACE), target(helper)).isCanceled(), "the smash goes ahead");
        helper.assertValueEqual(stamina(player), start - smash, "the smash is charged on the spot");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void betterCombatSwordsAreLeftToTheCompat(GameTestHelper helper) {
        if (!BetterCombatCompat.LOADED) {
            helper.succeed();
            return;
        }
        ServerPlayer player = exhaustedWith(helper, false);
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        helper.assertTrue(BetterCombatCompat.handlesAttacksWith(sword), "Better Combat handles swords");
        helper.assertFalse(EventPosts.attack(player, sword, target(helper)).isCanceled(), "the vanilla attack cost stands aside");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = VANILLA_MELEE)
    public static void epicFightBattleModeAttacksAreLeftToTheCompat(GameTestHelper helper) {
        if (!EpicFightCompat.LOADED) {
            helper.succeed();
            return;
        }
        ServerPlayer player = exhaustedWith(helper, false);
        EpicFightTestHooks.battleMode(player, false);
        helper.assertTrue(EventPosts.attack(player, new ItemStack(Items.IRON_SWORD), target(helper)).isCanceled(), "vanilla mode: the attack is cancelled");
        EpicFightTestHooks.battleMode(player, true);
        helper.assertFalse(EventPosts.attack(player, new ItemStack(Items.IRON_SWORD), target(helper)).isCanceled(), "battle mode: the vanilla attack cost stands aside");
        helper.succeed();
    }
}

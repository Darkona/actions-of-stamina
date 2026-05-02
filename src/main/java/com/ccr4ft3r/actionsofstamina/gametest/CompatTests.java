package com.ccr4ft3r.actionsofstamina.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaUnits;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import static com.ccr4ft3r.actionsofstamina.gametest.TestSupport.player;

/**
 * Compat hooks against the real mods; each test passes trivially when its mod isn't installed (run them with
 * {@code -PwithCompat}). Foreign classes are only touched through {@code *TestHooks}, loaded only when the mod is.
 */
@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class CompatTests {

    private static final ResourceLocation TEST = ActionsOfStamina.id("test");

    /** Spends in small steps until nothing more can be spent (Green Feathers: through Strain too). */
    private static String exhaust(StaminaBackend backend, ServerPlayer player) {
        int spends = 0;
        while (spends < 10000 && backend.spend(player, TEST, 50, 0)) spends++;
        return " (after " + spends + " spends: stamina " + backend.stamina(player) + ", available "
                + backend.availableStamina(player) + ", exhausted " + backend.exhausted(player) + ")";
    }

    @GameTest(template = "empty")
    public static void parcoolActionsAreChargedAndBlocked(GameTestHelper helper) {
        if (!ParcoolCompat.LOADED) {
            helper.succeed();
            return;
        }
        ServerPlayer player = player(helper);
        StaminaBackend backend = StaminaBackends.server();
        int before = backend.stamina(player);
        helper.assertFalse(ParcoolTestHooks.dodgeStartCancelled(player), "dodge may start with a full bar");
        ParcoolTestHooks.postDodgeStarted(player);
        helper.assertValueEqual(backend.stamina(player), before - StaminaUnits.ofFeathers(0.5), "stamina after a dodge");

        String state = exhaust(backend, player);
        helper.assertTrue(ParcoolTestHooks.dodgeStartCancelled(player), "dodge can't start when exhausted" + state);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void betterCombatSwingsAreCharged(GameTestHelper helper) {
        if (!BetterCombatCompat.LOADED) {
            helper.succeed();
            return;
        }
        helper.assertTrue(BetterCombatTestHooks.serverMixinApplied(), "mixin into Better Combat's ServerNetwork applied");
        ServerPlayer player = player(helper);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_SWORD));
        helper.assertTrue(BetterCombatCompat.handlesAttacksWith(player.getMainHandItem()), "a sword has Better Combat attacks");
        StaminaBackend backend = StaminaBackends.server();
        int before = backend.stamina(player);
        helper.assertTrue(BetterCombatCompat.chargeSwing(player, 0), "first swing paid");
        helper.assertValueEqual(backend.stamina(player), before - BetterCombatConfig.SWING.cost(), "stamina after one swing");

        String state = exhaust(backend, player);
        helper.assertFalse(BetterCombatCompat.chargeSwing(player, 0), "a swing is dropped when it can't be paid" + state);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void paraglidersReadsAoSStamina(GameTestHelper helper) {
        if (!ParagliderCompat.LOADED) {
            helper.succeed();
            return;
        }
        ServerPlayer player = player(helper);
        StaminaBackend backend = StaminaBackends.server();
        backend.spend(player, TEST, StaminaUnits.ofFeathers(3), 0);
        helper.assertTrue(ParagliderTestHooks.usesAoSStamina(player), "Paragliders' stamina is AoS's");
        helper.assertValueEqual((int) ParagliderTestHooks.stamina(player), backend.availableStamina(player), "Paragliders reads the backend");
        helper.succeed();
    }
}

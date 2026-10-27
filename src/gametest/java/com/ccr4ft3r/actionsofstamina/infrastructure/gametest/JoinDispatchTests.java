package com.ccr4ft3r.actionsofstamina.infrastructure.gametest;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.content.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.compat.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.content.stamina.StaminaBackends;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import static com.ccr4ft3r.actionsofstamina.infrastructure.gametest.TestSupport.survivalPlayer;
import static com.ccr4ft3r.actionsofstamina.infrastructure.gametest.TestSupport.switchEnabled;

@GameTestHolder(ActionsOfStamina.MOD_ID)
@PrefixGameTestTemplate(false)
public class JoinDispatchTests {

    private static void join(ServerPlayer player) {
        NeoForge.EVENT_BUS.post(new EntityJoinLevelEvent(player, player.level()));
    }

    @GameTest(template = "empty")
    public static void joiningRebuildsThePlayersActions(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        PlayerActions actions = PlayerActions.get(player);
        actions.clearActions(player);
        helper.assertTrue(actions.getAction(VanillaActions.SHIELD) == null, "no actions before the join");
        join(player);
        helper.assertTrue(actions.getAction(VanillaActions.SHIELD) != null, "the enabled actions are back after the join");
        helper.assertTrue(actions.getAction(VanillaActions.MINE) == null, "a disabled action stays out");
        helper.succeed();
    }

    private static boolean hasVesselBonus(ServerPlayer player) {
        StaminaBackend backend = StaminaBackends.server();
        if (backend.kind() == StaminaBackend.Kind.FEATHERS) return FeathersTestHooks.hasMaxFeathersModifier(player, ParagliderCompat.VESSELS);
        return backend.maxStamina(player) > StaminaBackend.ofFeathers(ActionsConfig.INTERNAL_MAX_FEATHERS.getAsInt());
    }

    private static void assertJoinResetsTheVesselBonus(GameTestHelper helper, boolean glidingEnabled) {
        ServerPlayer player = survivalPlayer(helper);
        StaminaBackends.server().setMaxBonus(player, ParagliderCompat.VESSELS, StaminaBackend.ofFeathers(4));
        helper.assertTrue(hasVesselBonus(player), "the vessel bonus is set");
        boolean before = ParagliderCompat.PARAGLIDE.config().enabled();
        switchEnabled(ParagliderCompat.PARAGLIDE.config(), glidingEnabled);
        try {
            join(player);
        } finally {
            switchEnabled(ParagliderCompat.PARAGLIDE.config(), before);
        }
        helper.assertFalse(hasVesselBonus(player), "the join refreshes the vessel bonus (gliding enabled: " + glidingEnabled + ")");
    }

    @GameTest(template = "empty")
    public static void paragliderJoinHookRunsWhileGlidingIsDisabled(GameTestHelper helper) {
        if (!ParagliderCompat.LOADED) {
            helper.succeed();
            return;
        }
        assertJoinResetsTheVesselBonus(helper, false);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void paragliderJoinHookRunsWhileGlidingIsEnabled(GameTestHelper helper) {
        if (!ParagliderCompat.LOADED) {
            helper.succeed();
            return;
        }
        assertJoinResetsTheVesselBonus(helper, true);
        helper.succeed();
    }
}

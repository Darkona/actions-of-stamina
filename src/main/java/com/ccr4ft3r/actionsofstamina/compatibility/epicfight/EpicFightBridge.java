package com.ccr4ft3r.actionsofstamina.compatibility.epicfight;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.modules.HoldableSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.eventlistener.BasicAttackEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;

import java.util.UUID;

/** Direct Epic Fight calls; only reached through {@link EpicFightCompat} when Epic Fight is loaded. */
final class EpicFightBridge {

    private static final ResourceLocation DODGE = ActionsOfStamina.id("epicfight/dodge");
    private static final ResourceLocation GUARD = ActionsOfStamina.id("epicfight/guard");
    private static final ResourceLocation INNATE = ActionsOfStamina.id("epicfight/innate");
    private static final ResourceLocation MOVER = ActionsOfStamina.id("epicfight/mover");
    private static final ResourceLocation BASIC_ATTACK = ActionsOfStamina.id("epicfight/basic_attack");

    /** AoS's key in each player's Epic Fight event listeners: adding it again replaces the old listener. */
    private static final UUID LISTENER = UUID.fromString("3c6f2d7e-4b1a-4e8f-a5d2-9e0b7c1f4a62");

    // Server thread only: the holdable skill AoS charged in the last consume event, to tell the repeat of a hold start.
    private static int paidPlayer = -1;
    @Nullable private static Skill paidSkill;
    private static long paidTick = Long.MIN_VALUE;

    private EpicFightBridge() {
    }

    /**
     * Epic Fight 20.x has no global skill events: each player patch keeps its own listeners, so they are added to every
     * new player entity when it joins a level (both sides; the basic attack event only fires on the server).
     */
    static void listen(Player player) {
        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        if (patch == null) return;
        PlayerEventListener listeners = patch.getEventListener();
        listeners.removeListener(PlayerEventListener.EventType.SKILL_CONSUME_EVENT, LISTENER);
        listeners.addEventListener(PlayerEventListener.EventType.SKILL_CONSUME_EVENT, LISTENER, EpicFightBridge::onConsume);
        if (!player.level().isClientSide()) {
            listeners.removeListener(PlayerEventListener.EventType.BASIC_ATTACK_EVENT, LISTENER);
            listeners.addEventListener(PlayerEventListener.EventType.BASIC_ATTACK_EVENT, LISTENER, EpicFightBridge::onBasicAttack);
        }
    }

    static boolean isEpicFightMode(Player player) {
        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        return patch != null && patch.isEpicFightMode();
    }

    /** Server: each swing of the basic attack combo. A swing that can't be paid for doesn't happen. */
    private static void onBasicAttack(BasicAttackEvent event) {
        if (!EpicFightCompat.isActive() || !EpicFightConfig.BASIC_ATTACK.enabled()) return;
        Player player = event.getPlayerPatch().getOriginal();
        if (player.isCreative() || player.isSpectator()) return;
        ActionCostConfig costs = EpicFightConfig.BASIC_ATTACK;
        if (!StaminaBackends.of(player).spend(player, BASIC_ATTACK, costs.cost(), costs.regenDelay())) event.setCanceled(true);
    }

    @Nullable
    private static ActionCostConfig costsOf(SkillCategory category) {
        if (category == SkillCategories.DODGE) return EpicFightConfig.DODGE;
        if (category == SkillCategories.GUARD) return EpicFightConfig.GUARD;
        if (category == SkillCategories.WEAPON_INNATE) return EpicFightConfig.INNATE;
        if (category == SkillCategories.MOVER) return EpicFightConfig.MOVER;
        return null;
    }

    private static ResourceLocation sourceOf(SkillCategory category) {
        if (category == SkillCategories.DODGE) return DODGE;
        if (category == SkillCategories.GUARD) return GUARD;
        if (category == SkillCategories.WEAPON_INNATE) return INNATE;
        return MOVER;
    }

    private static void onConsume(SkillConsumeEvent event) {
        if (!EpicFightCompat.isActive() || event.getResourceType() != Skill.Resource.STAMINA) return;
        SkillCategory category = event.getSkill().getCategory();
        ActionCostConfig costs = costsOf(category);
        if (costs == null || !costs.enabled()) return;
        Player player = event.getPlayerPatch().getOriginal();
        if (player.isCreative() || player.isSpectator()) return;

        ResourceLocation source = sourceOf(category);
        int cost = costs.cost();
        StaminaBackend backend = StaminaBackends.of(player);
        boolean paid;
        if (player.level().isClientSide()) {
            paid = backend.canSpend(player, source, cost);
        } else {
            // Starting to hold a skill (guard, charged skills), Epic Fight posts the event twice in a row on the
            // server: once from the resource check, then again to consume. The second one is already paid.
            long tick = player.level().getGameTime();
            boolean repeat = paidSkill == event.getSkill() && paidPlayer == player.getId() && paidTick == tick;
            paidSkill = null;
            if (repeat) {
                paid = true;
            } else if ((paid = backend.spend(player, source, cost, costs.regenDelay())) && event.getSkill() instanceof HoldableSkill) {
                paidPlayer = player.getId();
                paidSkill = event.getSkill();
                paidTick = tick;
            }
        }
        if (paid) event.setResourceType(Skill.Resource.NONE);
        else event.setCanceled(true);
    }
}

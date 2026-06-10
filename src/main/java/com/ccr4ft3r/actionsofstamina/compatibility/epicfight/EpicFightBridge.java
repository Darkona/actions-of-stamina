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

    private EpicFightBridge() {
    }

    /**
     * Epic Fight 18.x has no global skill events: each player patch keeps its own listeners, so they are added to every
     * new player entity when it joins a level (both sides; the basic attack event only fires on the server).
     */
    static void listen(Player player) {
        PlayerPatch<?> patch = EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
        if (patch == null) return;
        PlayerEventListener listeners = patch.getEventListener();
        listeners.removeListener(PlayerEventListener.EventType.SKILL_CONSUME_EVENT, LISTENER);
        listeners.addEventListener(PlayerEventListener.EventType.SKILL_CONSUME_EVENT, LISTENER, EpicFightBridge::onConsume);
        if (!player.level.isClientSide()) {
            listeners.removeListener(PlayerEventListener.EventType.BASIC_ATTACK_EVENT, LISTENER);
            listeners.addEventListener(PlayerEventListener.EventType.BASIC_ATTACK_EVENT, LISTENER, EpicFightBridge::onBasicAttack);
        }
    }

    static boolean isEpicFightMode(Player player) {
        PlayerPatch<?> patch = EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
        return patch != null && patch.isBattleMode();
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
        // Epic Fight 18 posts the event twice for a skill: to check it can run (shouldConsume false, both sides), then
        // on the server to pay for it (shouldConsume true). Only the second spends.
        boolean paid = player.level.isClientSide() || !event.shouldConsume()
                ? backend.canSpend(player, source, cost)
                : backend.spend(player, source, cost, costs.regenDelay());
        if (paid) event.setResourceType(Skill.Resource.NONE);
        else event.setCanceled(true);
    }
}

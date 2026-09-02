package com.ccr4ft3r.actionsofstamina.compatibility.epicfight;

import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
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
        if (!PlayerActions.perform(player, EpicFightCompat.BASIC_ATTACK)) event.setCanceled(true);
    }

    /** The action a skill of this category is, or null for a category AoS leaves to Epic Fight's own stamina. */
    @Nullable
    private static ActionType typeOf(SkillCategory category) {
        if (category == SkillCategories.DODGE) return EpicFightCompat.DODGE;
        if (category == SkillCategories.GUARD) return EpicFightCompat.GUARD;
        if (category == SkillCategories.WEAPON_INNATE) return EpicFightCompat.INNATE;
        if (category == SkillCategories.MOVER) return EpicFightCompat.MOVER;
        return null;
    }

    private static void onConsume(SkillConsumeEvent event) {
        if (!EpicFightCompat.isActive() || event.getResourceType() != Skill.Resource.STAMINA) return;
        ActionType type = typeOf(event.getSkill().getCategory());
        if (type == null || !type.config().enabled()) return;
        Player player = event.getPlayerPatch().getOriginal();
        if (player.isCreative() || player.isSpectator()) return;

        // Epic Fight 18 posts the event twice for a skill: to check it can run (shouldConsume false, both sides), then
        // on the server to pay for it (shouldConsume true). Only the second spends.
        boolean paid = player.level.isClientSide() || !event.shouldConsume()
                ? PlayerActions.canPerform(player, type)
                : PlayerActions.perform(player, type);
        if (paid) event.setResourceType(Skill.Resource.NONE);
        else event.setCanceled(true);
    }
}

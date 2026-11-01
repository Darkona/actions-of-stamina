package com.ccr4ft3r.actionsofstamina.compat.epicfight;

import com.ccr4ft3r.actionsofstamina.config.compat.EpicFightConfig;
import com.ccr4ft3r.actionsofstamina.content.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
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

final class EpicFightBridge {

    private static final UUID LISTENER = UUID.fromString("3c6f2d7e-4b1a-4e8f-a5d2-9e0b7c1f4a62");

    private static int paidPlayer = -1;
    @Nullable private static Skill paidSkill;
    private static long paidTick = Long.MIN_VALUE;

    private EpicFightBridge() {
    }

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

    private static void onBasicAttack(BasicAttackEvent event) {
        if (!EpicFightCompat.isActive() || !EpicFightConfig.BASIC_ATTACK.enabled()) return;
        Player player = event.getPlayerPatch().getOriginal();
        if (player.isCreative() || player.isSpectator()) return;
        if (!PlayerActions.perform(player, EpicFightCompat.BASIC_ATTACK)) event.setCanceled(true);
    }

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

        boolean paid;
        if (player.level().isClientSide()) {
            paid = PlayerActions.canPerform(player, type);
        } else {
            long tick = player.level().getGameTime();
            boolean repeat = paidSkill == event.getSkill() && paidPlayer == player.getId() && paidTick == tick;
            paidSkill = null;
            if (repeat) {
                paid = true;
            } else if ((paid = PlayerActions.perform(player, type)) && event.getSkill() instanceof HoldableSkill) {
                paidPlayer = player.getId();
                paidSkill = event.getSkill();
                paidTick = tick;
            }
        }
        if (paid) event.setResourceType(Skill.Resource.NONE);
        else event.setCanceled(true);
    }
}

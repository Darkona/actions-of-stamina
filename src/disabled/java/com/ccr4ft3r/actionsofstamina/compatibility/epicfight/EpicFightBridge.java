package com.ccr4ft3r.actionsofstamina.compatibility.epicfight;

import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.player.ComboAttackEvent;
import yesman.epicfight.api.event.types.player.SkillConsumeEvent;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.modules.HoldableSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

/** Direct Epic Fight calls; only reached through {@link EpicFightCompat} when Epic Fight is loaded. */
final class EpicFightBridge {

    // Server thread only: the holdable skill AoS charged in the last consume event, to tell the repeat of a hold start.
    private static int paidPlayer = -1;
    @Nullable private static Skill paidSkill;
    private static long paidTick = Long.MIN_VALUE;

    private EpicFightBridge() {
    }

    static void register() {
        EpicFightEventHooks.Player.CONSUME_SKILL.registerEvent(EpicFightBridge::onConsume);
        EpicFightEventHooks.Player.COMBO_ATTACK.registerEvent(EpicFightBridge::onComboAttack);
    }

    static boolean isEpicFightMode(Player player) {
        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        return patch != null && patch.isEpicFightMode();
    }

    /**
     * Server: each swing of the basic attack combo. A swing that can't be paid for doesn't happen.
     */
    private static void onComboAttack(ComboAttackEvent event) {
        if (!EpicFightCompat.isActive() || !EpicFightConfig.BASIC_ATTACK.enabled()) return;
        Player player = event.getPlayerPatch().getOriginal();
        if (player.isCreative() || player.isSpectator()) return;
        if (!PlayerActions.perform(player, EpicFightCompat.BASIC_ATTACK)) event.cancel();
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
        if (!(event.getEntityPatch().getOriginal() instanceof Player player)) return;
        if (player.isCreative() || player.isSpectator()) return;

        boolean paid;
        if (player.level().isClientSide()) {
            paid = PlayerActions.canPerform(player, type);
        } else {
            // Starting to hold a skill (guard, charged skills), Epic Fight posts the event twice in a row on the
            // server: once from the resource check, then again to consume. The second one is already paid.
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
        else event.cancel();
    }
}

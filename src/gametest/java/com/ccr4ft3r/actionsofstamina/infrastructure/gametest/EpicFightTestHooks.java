package com.ccr4ft3r.actionsofstamina.infrastructure.gametest;

import com.ccr4ft3r.actionsofstamina.compat.epicfight.EpicFightCompat;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.BasicAttackEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;

final class EpicFightTestHooks {

    static final int CATEGORIES = 4;
    static final String[] CATEGORY_NAMES = {"dodge", "guard", "innate", "mover"};
    private static final SkillCategories[] CATEGORY = {SkillCategories.DODGE, SkillCategories.GUARD,
            SkillCategories.WEAPON_INNATE, SkillCategories.MOVER};

    private EpicFightTestHooks() {
    }

    static boolean hasPatch(ServerPlayer player) {
        if (!(EpicFightCapabilities.getPlayerPatch(player) instanceof ServerPlayerPatch)) return false;
        EpicFightCompat.listen(player);
        return true;
    }

    @Nullable
    private static Skill firstSkillOf(SkillCategories category) {
        for (Skill skill : SkillManager.getSkills(skill -> skill.getCategory() == category)) {
            return skill;
        }
        return null;
    }

    @Nullable
    static String skillName(int category) {
        Skill skill = firstSkillOf(CATEGORY[category]);
        return skill == null ? null : skill.getRegistryName().toString();
    }

    record Consume(boolean canceled, boolean switchedToNone) {
    }

    static Consume consumeSkill(ServerPlayer player, int category) {
        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        Skill skill = firstSkillOf(CATEGORY[category]);
        SkillConsumeEvent event = new SkillConsumeEvent(patch, skill, Skill.Resource.STAMINA, 1.0F, null);
        patch.getEventListener().triggerEvents(PlayerEventListener.EventType.SKILL_CONSUME_EVENT, event);
        return new Consume(event.isCanceled(), event.getResourceType() == Skill.Resource.NONE);
    }

    static boolean comboAttackCanceled(ServerPlayer player) {
        ServerPlayerPatch patch = (ServerPlayerPatch) EpicFightCapabilities.getPlayerPatch(player);
        BasicAttackEvent event = new BasicAttackEvent(patch);
        patch.getEventListener().triggerEvents(PlayerEventListener.EventType.BASIC_ATTACK_EVENT, event);
        return event.isCanceled();
    }

    static void battleMode(ServerPlayer player, boolean battle) {
        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        if (battle) patch.toEpicFightMode(false);
        else patch.toVanillaMode(false);
    }
}

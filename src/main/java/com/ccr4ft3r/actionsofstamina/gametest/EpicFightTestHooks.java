package com.ccr4ft3r.actionsofstamina.gametest;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.player.ComboAttackEvent;
import yesman.epicfight.api.event.types.player.SkillConsumeEvent;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/** Epic Fight calls for {@link CompatTests}; loaded only when Epic Fight is. */
final class EpicFightTestHooks {

    /** The categories AoS charges, in the order of {@link #CATEGORY_NAMES}. */
    static final int CATEGORIES = 4;
    static final String[] CATEGORY_NAMES = {"dodge", "guard", "innate", "mover"};
    private static final SkillCategories[] CATEGORY = {SkillCategories.DODGE, SkillCategories.GUARD,
            SkillCategories.WEAPON_INNATE, SkillCategories.MOVER};

    private EpicFightTestHooks() {
    }

    static boolean hasPatch(ServerPlayer player) {
        return EpicFightCapabilities.getPlayerPatch(player) instanceof ServerPlayerPatch;
    }

    @Nullable
    private static Skill firstSkillOf(SkillCategories category) {
        for (Skill skill : EpicFightRegistries.SKILL) {
            if (skill.getCategory() == category) return skill;
        }
        return null;
    }

    @Nullable
    static String skillName(int category) {
        Skill skill = firstSkillOf(CATEGORY[category]);
        return skill == null ? null : skill.getRegistryName().toString();
    }

    /** Result of posting a skill's stamina consumption the way Epic Fight does before a skill runs. */
    record Consume(boolean canceled, boolean switchedToNone) {
    }

    static Consume consumeSkill(ServerPlayer player, int category) {
        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        Skill skill = firstSkillOf(CATEGORY[category]);
        SkillConsumeEvent event = new SkillConsumeEvent(patch, skill, Skill.Resource.STAMINA, 1.0F, null);
        EpicFightEventHooks.Player.CONSUME_SKILL.post(event);
        return new Consume(event.isCanceled(), event.getResourceType() == Skill.Resource.NONE);
    }

    /** Posts one basic-attack swing; true when it was cancelled. */
    static boolean comboAttackCanceled(ServerPlayer player) {
        ServerPlayerPatch patch = (ServerPlayerPatch) EpicFightCapabilities.getPlayerPatch(player);
        return EpicFightEventHooks.Player.COMBO_ATTACK.post(new ComboAttackEvent(patch)).isCanceled();
    }

    static void battleMode(ServerPlayer player, boolean battle) {
        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        if (battle) patch.toEpicFightMode(false);
        else patch.toVanillaMode(false);
    }
}

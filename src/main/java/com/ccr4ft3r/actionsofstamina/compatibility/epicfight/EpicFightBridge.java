package com.ccr4ft3r.actionsofstamina.compatibility.epicfight;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.player.ComboAttackEvent;
import yesman.epicfight.api.event.types.player.SkillConsumeEvent;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

/** Direct Epic Fight calls; only reached through {@link EpicFightCompat} when Epic Fight is loaded. */
final class EpicFightBridge {

    private static final ResourceLocation DODGE = ActionsOfStamina.id("epicfight/dodge");
    private static final ResourceLocation GUARD = ActionsOfStamina.id("epicfight/guard");
    private static final ResourceLocation INNATE = ActionsOfStamina.id("epicfight/innate");
    private static final ResourceLocation MOVER = ActionsOfStamina.id("epicfight/mover");
    private static final ResourceLocation BASIC_ATTACK = ActionsOfStamina.id("epicfight/basic_attack");

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
        ActionCostConfig costs = EpicFightConfig.BASIC_ATTACK;
        if (!StaminaBackends.of(player).spend(player, BASIC_ATTACK, costs.cost(), costs.regenDelay())) event.cancel();
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
        if (!(event.getEntityPatch().getOriginal() instanceof Player player)) return;
        if (player.isCreative() || player.isSpectator()) return;

        ResourceLocation source = sourceOf(category);
        int cost = costs.cost();
        StaminaBackend backend = StaminaBackends.of(player);
        boolean paid = player.level().isClientSide()
                ? backend.canSpend(player, source, cost)
                : backend.spend(player, source, cost, costs.regenDelay());
        if (paid) event.setResourceType(Skill.Resource.NONE);
        else event.cancel();
    }
}

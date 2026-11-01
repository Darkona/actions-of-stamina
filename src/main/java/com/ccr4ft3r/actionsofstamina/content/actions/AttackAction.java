package com.ccr4ft3r.actionsofstamina.content.actions;

import com.ccr4ft3r.actionsofstamina.compat.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compat.epicfight.EpicFightCompat;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import java.util.UUID;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class AttackAction extends Action {

    public static final UUID WEAKEN_ID = UUID.fromString("5b0f3c52-8a1e-4f6b-9d0c-2f4e7a1c6d31");
    private static final String WEAKEN_NAME = "actionsofstamina:exhausted_attack";
    private static final int WEAKEN_CHECK_INTERVAL = 10;

    private final boolean weakens;
    private final boolean weakensFreeAttacks;
    private final AttributeModifier damageModifier;
    private final AttributeModifier speedModifier;

    public AttackAction(ActionType type) {
        this(type, ActionsConfig.EXHAUSTED_MODE.get() == ExhaustedAttackMode.WEAKEN,
                ActionsConfig.WEAKEN_NON_WEAPONS.get() || ActionsConfig.ALSO_FOR_NON_WEAPONS.get());
    }

    public AttackAction(ActionType type, boolean weakens, boolean weakensFreeAttacks) {
        super(type);
        this.weakens = weakens;
        this.weakensFreeAttacks = weakensFreeAttacks;
        this.damageModifier = new AttributeModifier(WEAKEN_ID, WEAKEN_NAME, ActionsConfig.WEAKEN_DAMAGE.get() - 1.0, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.speedModifier = new AttributeModifier(WEAKEN_ID, WEAKEN_NAME, ActionsConfig.WEAKEN_SPEED.get() - 1.0, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    public boolean weakens() {
        return weakens;
    }

    @Override
    public void onAttack(Player player, AttackEntityEvent event) {
        if (BetterCombatCompat.handlesAttacksWith(player.getMainHandItem()) || EpicFightCompat.inBattleMode(player)) return;
        if (weakens) refreshWeakened(player);
        if (performHit(player)) return;
        if (weakens) weaken(player);
        else event.setCanceled(true);
    }

    @Override
    public void tick(Player p, PlayerActions a) {
        super.tick(p, a);
        if (!weakens || p.level().isClientSide() || p.tickCount % WEAKEN_CHECK_INTERVAL != 0) return;
        refreshWeakened(p);
    }

    public void refreshWeakened(Player player) {
        setWeakened(player, !canPerform(player) && (weakensFreeAttacks || isWeapon(player.getMainHandItem())));
    }

    public void weaken(Player player) {
        setWeakened(player, true);
    }

    private void setWeakened(Player player, boolean weakened) {
        AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance speed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (damage == null || speed == null || (damage.getModifier(WEAKEN_ID) != null) == weakened) return;
        if (weakened) {
            damage.addTransientModifier(damageModifier);
            speed.removeModifier(WEAKEN_ID);
            speed.addTransientModifier(speedModifier);
        } else {
            damage.removeModifier(WEAKEN_ID);
            speed.removeModifier(WEAKEN_ID);
        }
    }

    @Override
    public void cleanUp(Player player) {
        AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) damage.removeModifier(WEAKEN_ID);
        AttributeInstance speed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (speed != null) speed.removeModifier(WEAKEN_ID);
    }

    private static boolean isWeapon(ItemStack stack) {
        return !stack.isEmpty() && stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE);
    }

    public boolean performHit(Player player) {
        return perform(player);
    }

    @Override
    public boolean perform(Player player) {
        if (PlayerActions.isExempt(player)) return true;
        if (!isWeapon(player.getItemInHand(InteractionHand.MAIN_HAND)) && !ActionsConfig.ALSO_FOR_NON_WEAPONS.get()) return true;
        return super.perform(player);
    }

    public enum ExhaustedAttackMode {
        CANCEL,
        WEAKEN
    }
}

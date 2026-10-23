package com.ccr4ft3r.actionsofstamina.actions;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class AttackAction extends Action {

    public static final ResourceLocation WEAKEN_ID = ActionsOfStamina.id("exhausted_attack");
    private static final int WEAKEN_CHECK_INTERVAL = 10;

    private final boolean weakens;
    private final boolean weakensFreeAttacks;
    private final int smashCost;
    private final AttributeModifier damageModifier;
    private final AttributeModifier speedModifier;

    public AttackAction(ActionType type) {
        this(type, AoSServerConfig.EXHAUSTED_MODE.get() == ExhaustedAttackMode.WEAKEN,
                AoSServerConfig.WEAKEN_NON_WEAPONS.getAsBoolean() || AoSServerConfig.ALSO_FOR_NON_WEAPONS.getAsBoolean());
    }

    public AttackAction(ActionType type, boolean weakens, boolean weakensFreeAttacks) {
        super(type);
        this.weakens = weakens;
        this.weakensFreeAttacks = weakensFreeAttacks;
        this.smashCost = (int) Math.round(cost * AoSServerConfig.MACE_SMASH_MULTIPLIER.getAsDouble());
        this.damageModifier = new AttributeModifier(WEAKEN_ID, AoSServerConfig.WEAKEN_DAMAGE.get() - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.speedModifier = new AttributeModifier(WEAKEN_ID, AoSServerConfig.WEAKEN_SPEED.get() - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    public boolean weakens() {
        return weakens;
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
        if (damage == null || speed == null || damage.hasModifier(WEAKEN_ID) == weakened) return;
        if (weakened) {
            damage.addOrUpdateTransientModifier(damageModifier);
            speed.addOrUpdateTransientModifier(speedModifier);
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
        if (stack.isEmpty()) return false;
        List<ItemAttributeModifiers.Entry> entries = stack.getAttributeModifiers().modifiers();
        for (int i = 0, n = entries.size(); i < n; i++) {
            ItemAttributeModifiers.Entry entry = entries.get(i);
            if (entry.attribute() == Attributes.ATTACK_DAMAGE && entry.slot().test(EquipmentSlot.MAINHAND)) return true;
        }
        return false;
    }

    public boolean performHit(Player player) {
        if (PlayerActions.isExempt(player) || !(player.getMainHandItem().getItem() instanceof MaceItem) || !MaceItem.canSmashAttack(player)) {
            return perform(player);
        }
        if (smashCost <= 0) return true;
        boolean charged = StaminaBackends.of(player).spend(player, type.id(), smashCost, cooldown);
        if (ActionsOfStamina.debugging()) ActionsOfStamina.log("{}::Smash allowed = {}, cost= {}", name(), charged, smashCost);
        return charged;
    }

    @Override
    public boolean perform(Player player) {
        if (PlayerActions.isExempt(player)) return true;
        if (!isWeapon(player.getItemInHand(InteractionHand.MAIN_HAND)) && !AoSServerConfig.ALSO_FOR_NON_WEAPONS.get()) return true;
        return super.perform(player);
    }

    public enum ExhaustedAttackMode {
        CANCEL,
        WEAKEN
    }
}

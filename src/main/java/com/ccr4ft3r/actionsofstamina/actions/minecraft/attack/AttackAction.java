package com.ccr4ft3r.actionsofstamina.actions.minecraft.attack;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;


/**
 * Attacking, charged once every few attacks. An attack the player can't afford is cancelled, or with
 * {@code exhausted_mode = WEAKEN} lands weakened: transient attack damage and attack speed modifiers stay on the
 * player while the stamina is short.
 */
public class AttackAction extends Action {

    public static final String actionName = "attack_action";
    public static final ResourceLocation SOURCE = ActionsOfStamina.id("attack");
    /** Id of both WEAKEN modifiers (one per attribute). */
    public static final ResourceLocation WEAKEN_ID = ActionsOfStamina.id("exhausted_attack");
    /** Ticks between checks of whether the WEAKEN modifiers still belong on the player. */
    private static final int WEAKEN_CHECK_INTERVAL = 10;

    private final boolean weakens;
    private final AttributeModifier damageModifier;
    private final AttributeModifier speedModifier;

    public AttackAction() {
        super(SOURCE, AoSServerConfig.ATTACK);
        this.weakens = AoSServerConfig.EXHAUSTED_MODE.get() == ExhaustedAttackMode.WEAKEN;
        this.damageModifier = new AttributeModifier(WEAKEN_ID, AoSServerConfig.WEAKEN_DAMAGE.get() - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.speedModifier = new AttributeModifier(WEAKEN_ID, AoSServerConfig.WEAKEN_SPEED.get() - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    /** Whether an attack the player can't afford lands weakened instead of being cancelled. */
    public boolean weakens() {
        return weakens;
    }

    /**
     * Server, WEAKEN only: every {@link #WEAKEN_CHECK_INTERVAL} ticks, puts the modifiers on or takes them off when
     * the stamina went short or came back. Attributes sync to the client by themselves (attack speed drives its
     * cooldown indicator).
     */
    @Override
    public void tick(Player p, PlayerActions a) {
        super.tick(p, a);
        if (!weakens || p.level().isClientSide() || p.tickCount % WEAKEN_CHECK_INTERVAL != 0) return;
        setWeakened(p, !canPerform(p)); // Creative and spectator players can always perform.
    }

    /** Server: an attack the player can't afford lands weakened right away, without waiting for the next check. */
    public void weaken(Player player) {
        setWeakened(player, true);
    }

    /** Only acts on a change; the damage modifier's presence is the current state. */
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

    /** The actions are being rebuilt: no WEAKEN modifier outlives this action (the new one checks again). */
    @Override
    public void cleanUp(Player player) {
        AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) damage.removeModifier(WEAKEN_ID);
        AttributeInstance speed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (speed != null) speed.removeModifier(WEAKEN_ID);
    }


    @Override
    public String name() {
        return actionName;
    }

    @Override
    public int id() {
        return ATTACK;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }

    /** A weapon is anything whose (component-driven) attribute modifiers add attack damage in the main hand. */
    private static boolean isWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;
        List<ItemAttributeModifiers.Entry> entries = stack.getAttributeModifiers().modifiers();
        for (int i = 0, n = entries.size(); i < n; i++) {
            ItemAttributeModifiers.Entry entry = entries.get(i);
            if (entry.attribute() == Attributes.ATTACK_DAMAGE && entry.slot().test(EquipmentSlot.MAINHAND)) return true;
        }
        return false;
    }

    @Override
    public boolean perform(Player player) {
        charged = false;
        if (PlayerActions.isNotExhaustable(player)) return true;
        // Not a weapon and non-weapons don't cost: the attack goes ahead for free, it isn't cancelled.
        if (!isWeapon(player.getItemInHand(InteractionHand.MAIN_HAND)) && !AoSServerConfig.ALSO_FOR_NON_WEAPONS.get()) return true;
        return super.perform(player);
    }

}

package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.attack.AttackAction;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.PiercingWeapon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A spear's stab, server side: it reaches every entity along its line without the attack event, so it is charged here,
 * once per stab, as an attack. A stab the player can't afford hits nothing (or lands weakened with WEAKEN); a stab
 * that hits nothing counts as a miss.
 */
@Mixin(PiercingWeapon.class)
public abstract class PiercingWeaponMixin {

    @Inject(method = "attack", at = @At("HEAD"))
    private void actionsofstamina$stabStarts(LivingEntity attacker, EquipmentSlot hand, CallbackInfo ci) {
        // Before the stab reads the attack damage, so WEAKEN's modifiers are already where they belong.
        AttackAction.stabStarts(attacker);
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;stabAttack(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/Entity;FZZZ)Z"))
    private boolean actionsofstamina$stabNeedsStamina(LivingEntity attacker, EquipmentSlot hand, Entity target, float damage,
                                                       boolean dealsDamage, boolean dealsKnockback, boolean dismounts,
                                                       Operation<Boolean> original, @Share("stab") LocalIntRef stab) {
        if (stab.get() == AttackAction.STAB_UNCHARGED) stab.set(AttackAction.chargeStab(attacker));
        if (stab.get() == AttackAction.STAB_REFUSED) return false;
        return original.call(attacker, hand, target, damage, dealsDamage, dealsKnockback, dismounts);
    }

    @Inject(method = "attack", at = @At("RETURN"))
    private void actionsofstamina$stabEnds(LivingEntity attacker, EquipmentSlot hand, CallbackInfo ci, @Share("stab") LocalIntRef stab) {
        if (stab.get() == AttackAction.STAB_UNCHARGED) AttackAction.stabMissed(attacker);
    }
}

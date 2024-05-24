package com.ccr4ft3r.actionsofstamina.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow protected boolean jumping;

    @Shadow
    public abstract void setJumping(boolean p_21314_);

    @Shadow
    public abstract void setSprinting(boolean p_108751_);

}

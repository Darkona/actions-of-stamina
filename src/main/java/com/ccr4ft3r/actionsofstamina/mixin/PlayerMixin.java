package com.ccr4ft3r.actionsofstamina.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntityMixin {

    @Shadow
    public abstract <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing);

    @Inject(method = "jumpFromGround", at = @At(value = "HEAD"), cancellable = true)
    public void stopJumping(CallbackInfo ci) {
       /* if (shouldStop((Player) (Object) this, AoSAction.JUMPING)){
            jumping = false;
            ci.cancel();
        }*/
    }



}

package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.jump.JumpAction;
import com.ccr4ft3r.actionsofstamina.capability.AosCapabilityProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Player.class)
public abstract class PlayerMixin   {


    @Shadow
    public abstract <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing);

    @Inject(method = "jumpFromGround", at = @At(value = "HEAD"), cancellable = true)
    public void stopJumping(CallbackInfo ci) {

        getCapability(AosCapabilityProvider.PLAYER_ACTIONS, null)
                 .ifPresent(a -> a.getAction(JumpAction.actionName)
                                 .ifPresent(w -> {
                                     if(!w.perform((Player)(Object)this)) {
                                         ci.cancel();
                                     }
                                 }));

    }
}

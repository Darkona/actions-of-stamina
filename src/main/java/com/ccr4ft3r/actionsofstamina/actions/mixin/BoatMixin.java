package com.ccr4ft3r.actionsofstamina.actions.mixin;

import com.ccr4ft3r.actionsofstamina.content.actions.VanillaActions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Boat.class)
public abstract class BoatMixin {

    @Inject(method = "controlBoat", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$rowWithStamina(CallbackInfo ci) {
        Boat self = (Boat) (Object) this;
        if (self.getControllingPassenger() instanceof Player player && VanillaActions.isRowed(self) && !VanillaActions.mayRow(player)) {
            self.setPaddleState(false, false);
            ci.cancel();
        }
    }
}

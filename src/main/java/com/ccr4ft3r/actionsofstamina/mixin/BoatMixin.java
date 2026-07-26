package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.row.RowAction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Boat.class)
public abstract class BoatMixin {

    /**
     * A driver without the stamina to row: the paddles stop and the paddling input is ignored, so the boat drifts
     * (it neither speeds up nor turns). Boats are moved by their driver's client, the only side that runs this; the
     * paddle state it leaves is what that client reports to the server.
     */
    @Inject(method = "controlBoat", at = @At("HEAD"), cancellable = true)
    private void actionsofstamina$rowWithStamina(CallbackInfo ci) {
        Boat self = (Boat) (Object) this;
        if (self.getControllingPassenger() instanceof Player player && RowAction.isRowed(self) && !RowAction.mayRow(player)) {
            self.setPaddleState(false, false);
            ci.cancel();
        }
    }
}

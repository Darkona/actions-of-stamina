package com.ccr4ft3r.actionsofstamina.mixin;

import com.ccr4ft3r.actionsofstamina.config.ActionType;
import com.ccr4ft3r.actionsofstamina.data.ServerData;
import com.ccr4ft3r.actionsofstamina.data.ServerPlayerData;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;

@Mixin(targets = "com.elenai.feathers.event.CommonEvents")
public class FeathersCommonEventsMixin {

    @Inject(method = "regenerateFeathers", at = @At("HEAD"), cancellable = true, remap = false)
    private static void regenerateFeathersHeadInjected(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        ServerPlayerData playerData = ServerData.getPlayerData(event.player);
        if (ActionType.TIME_ACTIONS.stream().anyMatch((action) -> playerData.is(action) && getProfile().regenerationByAction.get(action).get()))
            ci.cancel();
    }
}
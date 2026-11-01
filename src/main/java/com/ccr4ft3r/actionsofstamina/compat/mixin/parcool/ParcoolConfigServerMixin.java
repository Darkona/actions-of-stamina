package com.ccr4ft3r.actionsofstamina.compat.mixin.parcool;

import com.alrex.parcool.config.ParCoolConfig;
import com.ccr4ft3r.actionsofstamina.compat.parcool.ParcoolCompat;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ParCoolConfig.Server.class, remap = false)
public abstract class ParcoolConfigServerMixin {

    @ModifyArg(method = "<init>", require = 0, index = 1, at = @At(value = "INVOKE",
            target = "Lnet/minecraftforge/common/ForgeConfigSpec$Builder;define(Ljava/lang/String;Ljava/lang/Object;)Lnet/minecraftforge/common/ForgeConfigSpec$ConfigValue;"))
    private Object actionsofstamina$defaultStaminaType(String path, Object defaultValue) {
        return "stamina_type".equals(path) ? ParcoolCompat.STAMINA_TYPE.toString() : defaultValue;
    }

    @ModifyReturnValue(method = "getStaminaTypeID", at = @At("RETURN"))
    private ResourceLocation actionsofstamina$staminaType(ResourceLocation original) {
        return ParcoolCompat.effectiveStaminaType(original);
    }
}

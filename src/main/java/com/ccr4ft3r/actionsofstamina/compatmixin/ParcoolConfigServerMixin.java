package com.ccr4ft3r.actionsofstamina.compatmixin;

import com.alrex.parcool.config.ParCoolConfig;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolCompat;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * ParCool's server config (applied only when ParCool is loaded): AoS's stamina type is the default
 * {@code stamina_type} of a newly written config, and while AoS's ParCool compat is enabled it also stands in for
 * ParCool's own {@code parcool:parcool}, which every existing config holds and which would charge each action twice.
 */
@Mixin(value = ParCoolConfig.Server.class, remap = false)
public abstract class ParcoolConfigServerMixin {

    /** The only {@code define(String, Object)} in the constructor; the path is checked anyway. Optional: cosmetic. */
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

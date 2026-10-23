package com.ccr4ft3r.actionsofstamina.compatibility;

import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.create.CreateCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import dev.darkona.jdlib.api.mixin.JDMixinPlugin;
import java.util.Map;
import net.neoforged.fml.loading.LoadingModList;

public final class CompatMixinPlugin extends JDMixinPlugin {

    private static final Map<String, String> MOD_BY_MIXIN_PREFIX = Map.of(
            "BetterCombat", BetterCombatCompat.MOD_ID,
            "CombatRoll", CombatRollCompat.MOD_ID,
            "Create", CreateCompat.MOD_ID,
            "Gliders", GlidersCompat.MOD_ID,
            "Parcool", ParcoolCompat.MOD_ID,
            "WallJump", WallJumpCompat.MOD_ID);

    private LoadingModList mods;

    public static String modOf(String mixinClassName) {
        String simpleName = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        for (Map.Entry<String, String> entry : MOD_BY_MIXIN_PREFIX.entrySet()) {
            if (simpleName.startsWith(entry.getKey())) return entry.getValue();
        }
        throw new IllegalStateException("Compat mixin " + mixinClassName + " does not start with the name of a compat");
    }

    @Override
    public void onLoad(String mixinPackage) {
        mods = LoadingModList.get();
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return mods != null && mods.getModFileById(modOf(mixinClassName)) != null;
    }
}

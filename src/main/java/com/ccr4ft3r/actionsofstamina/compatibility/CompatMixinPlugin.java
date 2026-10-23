package com.ccr4ft3r.actionsofstamina.compatibility;

import dev.darkona.jdlib.api.compat.JDMixinPlugin;
import net.neoforged.fml.loading.LoadingModList;
import org.jetbrains.annotations.Nullable;

public final class CompatMixinPlugin extends JDMixinPlugin {

    private static final String ROOT_PACKAGE = "mixin";

    private LoadingModList mods;

    @Nullable
    public static String modOf(String mixinClassName) {
        int end = mixinClassName.lastIndexOf('.');
        int start = mixinClassName.lastIndexOf('.', end - 1);
        if (end < 0 || start < 0) return null;
        String owner = mixinClassName.substring(start + 1, end);
        return owner.equals(ROOT_PACKAGE) ? null : owner;
    }

    @Override
    public void onLoad(String mixinPackage) {
        mods = LoadingModList.get();
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String mod = modOf(mixinClassName);
        return mod != null && mods != null && mods.getModFileById(mod) != null;
    }
}

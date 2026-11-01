package com.ccr4ft3r.actionsofstamina.compat;

import java.util.List;
import java.util.Set;
import net.minecraftforge.fml.loading.LoadingModList;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public final class CompatMixinPlugin implements IMixinConfigPlugin {

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
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String mod = modOf(mixinClassName);
        return mod != null && mods != null && mods.getModFileById(mod) != null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

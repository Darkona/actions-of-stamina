package com.ccr4ft3r.actionsofstamina.compatibility;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Mixin plugin of {@code actionsofstamina.compat.mixins.json}: a mixin into another mod applies only when that mod
 * is installed. Runs before the mod list exists, so it asks the loading mod list.
 */
public final class CompatMixinPlugin implements IMixinConfigPlugin {

    private static final String PACKAGE = "com.ccr4ft3r.actionsofstamina.compatmixin.";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String mixin = mixinClassName.startsWith(PACKAGE) ? mixinClassName.substring(PACKAGE.length()) : mixinClassName;
        if (mixin.startsWith("BetterCombat")) return isLoaded("bettercombat");
        if (mixin.startsWith("CombatRoll")) return isLoaded("combat_roll");
        return false;
    }

    private static boolean isLoaded(String modId) {
        LoadingModList mods = LoadingModList.get();
        return mods != null && mods.getModFileById(modId) != null;
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

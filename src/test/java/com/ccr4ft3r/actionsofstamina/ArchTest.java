package com.ccr4ft3r.actionsofstamina;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    private static final String MOD = "com.ccr4ft3r.actionsofstamina";

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(MOD);

    private static final DescribedPredicate<JavaClass> PLATFORM = resideInAnyPackage("java..", "javax..", "jdk..", "net.minecraft..",
            "com.mojang..", "net.neoforged..", "it.unimi.dsi.fastutil..", "org.jetbrains.annotations..", "org.jspecify..", "org.joml..",
            "org.slf4j..", "org.apache.logging..", "com.google..", "io.netty..", "dev.darkona.jdlib..", "org.spongepowered..",
            "com.llamalad7..");

    private static final DescribedPredicate<JavaClass> USES_CLIENT = DescribedPredicate.describe("use net.minecraft.client",
            javaClass -> javaClass.getAccessesFromSelf().stream()
                    .anyMatch(access -> access.getTargetOwner().getPackageName().startsWith("net.minecraft.client")));

    @Test
    void deferredRegistersLiveInIndexOrCompat() {
        noClasses().that().resideOutsideOfPackages(MOD + ".index..", MOD + ".compat..")
                .should().dependOnClassesThat().haveFullyQualifiedName("net.neoforged.neoforge.registries.DeferredRegister")
                .check(CLASSES);
    }

    @Test
    void configSpecsLiveInConfig() {
        noClasses().that().resideOutsideOfPackage(MOD + ".config..")
                .should().dependOnClassesThat().haveFullyQualifiedName("net.neoforged.neoforge.common.ModConfigSpec")
                .orShould().dependOnClassesThat().haveFullyQualifiedName("net.neoforged.neoforge.common.ModConfigSpec$Builder")
                .check(CLASSES);
    }

    @Test
    void payloadsLiveInNetwork() {
        noClasses().that().resideOutsideOfPackage(MOD + ".network..")
                .should().implement("net.minecraft.network.protocol.common.custom.CustomPacketPayload")
                .check(CLASSES);
    }

    @Test
    void theClientCodeOfAFeatureLivesInItsClientSubpackage() {
        classes().that().resideInAnyPackage(MOD + ".content..", MOD + ".core..").and(USES_CLIENT)
                .should().resideInAPackage("..client..")
                .because("HUDs, trackers of the client and client event handlers go in the client subpackage of the feature, next to what they draw")
                .check(CLASSES);
    }

    @Test
    void serverCodeNeverNamesClientCode() {
        noClasses().that(DescribedPredicate.not(USES_CLIENT)).and().doNotHaveSimpleName("ActionsOfStaminaClient")
                .should().dependOnClassesThat(USES_CLIENT.and(resideInAnyPackage(MOD + "..")))
                .check(CLASSES);
    }

    @Test
    void onlyCompatNamesOtherMods() {
        classes().that().resideOutsideOfPackage(MOD + ".compat..")
                .should().onlyDependOnClassesThat(PLATFORM.or(resideInAnyPackage(MOD + "..")))
                .check(CLASSES);
    }

    @Test
    void mixinPackagesHoldOnlyMixins() {
        classes().that().resideInAPackage("..mixin..").and().areTopLevelClasses()
                .should().beAnnotatedWith("org.spongepowered.asm.mixin.Mixin")
                .check(CLASSES);
    }
}

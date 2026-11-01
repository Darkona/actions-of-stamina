package com.ccr4ft3r.actionsofstamina;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MixinConfigsTest {

    private static final String MOD = "com.ccr4ft3r.actionsofstamina";
    private static final List<String> CONFIGS = List.of("actionsofstamina.mixins.json", "actionsofstamina.compat.mixins.json");
    private static final List<String> SECTIONS = List.of("mixins", "client", "server");

    private static JsonObject config(String name) throws IOException {
        try (InputStream in = MixinConfigsTest.class.getClassLoader().getResourceAsStream(name)) {
            assertNotNull(in, name + " is on the classpath");
            return JsonParser.parseString(new String(in.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    @Test
    void everyMixinIsInExactlyOneConfig() throws IOException {
        TreeSet<String> listed = new TreeSet<>();
        for (String name : CONFIGS) {
            JsonObject json = config(name);
            String pkg = json.get("package").getAsString();
            for (String section : SECTIONS) {
                JsonArray entries = json.getAsJsonArray(section);
                if (entries == null) continue;
                entries.forEach(entry -> assertTrue(listed.add(pkg + "." + entry.getAsString()), entry + " is listed twice"));
            }
        }
        TreeSet<String> mixins = new TreeSet<>();
        for (JavaClass c : new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages(MOD)) {
            if (c.isAnnotatedWith("org.spongepowered.asm.mixin.Mixin")) mixins.add(c.getName());
        }
        assertEquals(mixins, listed);
    }

    @Test
    void theJarManifestNamesEveryConfigAndEachHasTheRefmap() throws IOException {
        String buildScript = Files.readString(Path.of("build.gradle"));
        for (String name : CONFIGS) {
            assertTrue(buildScript.contains(name.replace("actionsofstamina", "${mod_id}")), name + " is in the build script");
            assertEquals("actionsofstamina.refmap.json", config(name).get("refmap").getAsString(), name);
        }
    }
}

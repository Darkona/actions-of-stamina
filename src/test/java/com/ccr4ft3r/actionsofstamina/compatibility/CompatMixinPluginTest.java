package com.ccr4ft3r.actionsofstamina.compatibility;

import com.ccr4ft3r.actionsofstamina.compatibility.bettercombat.BetterCombatCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.combatroll.CombatRollCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.create.CreateCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.gliders.GlidersCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompatMixinPluginTest {

    private static final String CONFIG = "/actionsofstamina.compat.mixins.json";
    private static final String PACKAGE = "com.ccr4ft3r.actionsofstamina.compatibility.mixin.";
    private static final Set<String> COMPAT_MODS = Set.of(BetterCombatCompat.MOD_ID, CombatRollCompat.MOD_ID, CreateCompat.MOD_ID,
            GlidersCompat.MOD_ID, ParcoolCompat.MOD_ID, WallJumpCompat.MOD_ID);

    @Test
    void everyListedMixinIsInThePackageOfACompatMod() throws IOException {
        JsonObject config;
        try (InputStream in = getClass().getResourceAsStream(CONFIG); Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            config = JsonParser.parseReader(reader).getAsJsonObject();
        }
        for (String section : List.of("mixins", "client", "server")) {
            JsonArray entries = config.getAsJsonArray(section);
            if (entries == null) continue;
            for (JsonElement entry : entries) {
                String mod = CompatMixinPlugin.modOf(PACKAGE + entry.getAsString());
                assertTrue(mod != null && COMPAT_MODS.contains(mod), entry.getAsString() + " is not in the package of a compat mod: " + mod);
            }
        }
    }

    @Test
    void theModIsThePackageOfTheMixin() {
        assertEquals("walljump", CompatMixinPlugin.modOf(PACKAGE + "walljump.WallJumpLogicMixin"));
        assertEquals("combat_roll", CompatMixinPlugin.modOf(PACKAGE + "combat_roll.CombatRollManagerMixin"));
        assertEquals("vc_gliders", CompatMixinPlugin.modOf(PACKAGE + "vc_gliders.GlidersToggleGlideMixin"));
    }

    @Test
    void aMixinOutsideEveryModPackageHasNoOwner() {
        assertNull(CompatMixinPlugin.modOf(PACKAGE + "StrayMixin"));
        assertNull(CompatMixinPlugin.modOf("StrayMixin"));
    }
}

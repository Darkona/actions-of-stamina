package com.ccr4ft3r.actionsofstamina.compatibility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompatMixinPluginTest {

    private static final String PACKAGE = "com.ccr4ft3r.actionsofstamina.compatibility.mixin.";

    @Test
    void everyListedMixinBelongsToOneCompat() throws IOException {
        String json;
        try (InputStream in = getClass().getResourceAsStream("/actionsofstamina.compat.mixins.json")) {
            json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        Matcher mixins = Pattern.compile("\"([A-Za-z]+Mixin)\"").matcher(json);
        int count = 0;
        while (mixins.find()) {
            CompatMixinPlugin.modOf(PACKAGE + mixins.group(1));
            count++;
        }
        assertEquals(8, count);
    }

    @Test
    void theModIsTheOneTheMixinNameStartsWith() {
        assertEquals("walljump", CompatMixinPlugin.modOf(PACKAGE + "WallJumpLogicMixin"));
        assertEquals("combat_roll", CompatMixinPlugin.modOf(PACKAGE + "CombatRollManagerMixin"));
        assertEquals("vc_gliders", CompatMixinPlugin.modOf(PACKAGE + "GlidersToggleGlideMixin"));
    }

    @Test
    void aMixinOfNoCompatIsRejected() {
        assertThrows(IllegalStateException.class, () -> CompatMixinPlugin.modOf(PACKAGE + "StrayMixin"));
    }
}

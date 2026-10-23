package com.ccr4ft3r.actionsofstamina;

import dev.darkona.jdlib.testkit.MixinConfigCheck;
import java.util.List;
import org.junit.jupiter.api.Test;

class MixinConfigsTest {

    private static final MixinConfigCheck CHECK = new MixinConfigCheck("actionsofstamina", "com.ccr4ft3r.actionsofstamina",
            List.of("actionsofstamina.mixins.json", "actionsofstamina.compat.mixins.json"));

    @Test
    void mixinConfigsMatchTheClassesAndTheModsToml() {
        CHECK.assertAll();
    }
}

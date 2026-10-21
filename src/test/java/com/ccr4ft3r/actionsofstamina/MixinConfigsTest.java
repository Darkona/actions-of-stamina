package com.ccr4ft3r.actionsofstamina;

import dev.darkona.jdlib.testkit.MixinConfigCheck;
import org.junit.jupiter.api.Test;

import java.util.List;

class MixinConfigsTest {

    private static final MixinConfigCheck CHECK = new MixinConfigCheck("actionsofstamina", "com.ccr4ft3r.actionsofstamina",
            List.of("actionsofstamina.mixins.json",
                    "actionsofstamina.compat.bettercombat.mixins.json",
                    "actionsofstamina.compat.combatroll.mixins.json",
                    "actionsofstamina.compat.parcool.mixins.json",
                    "actionsofstamina.compat.walljump.mixins.json",
                    "actionsofstamina.compat.gliders.mixins.json",
                    "actionsofstamina.compat.create.mixins.json"));

    @Test
    void mixinConfigsMatchTheClassesAndTheModsToml() {
        CHECK.assertAll();
    }
}

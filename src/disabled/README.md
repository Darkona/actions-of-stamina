# Disabled compats

Compats with mods that have no NeoForge build for this Minecraft version: Epic Fight, Gliders and Create. Their code is kept here as it is for Minecraft 1.21.1, so it is not lost.

Nothing under `src/disabled` is compiled or packaged: it is not a Gradle source set. The parts that lived in shared files are here too:

- `java/.../compatmixin/`: their mixins. Their entries in `actionsofstamina.compat.mixins.json` were `CreateHandCrankBlockMixin`, `CreateValveHandleBlockMixin` and `GlidersToggleGlideMixin`, all in `mixins` (both sides), and `CompatMixinPlugin` applied them for the mod ids `create` and `vc_gliders`.
- `java/.../gametest/DisabledCompatTests.java`: their tests from `CompatTests`, with their `*TestHooks`.
- `resources/META-INF/neoforge.mods.toml.disabled`: their optional dependencies.
- Calls from shared code, each removed from its file:
  - `ActionsOfStamina`: `EpicFightCompat.registerActions()`, `GlidersCompat.registerActions()`, `CreateCompat.registerActions()` in the constructor, and `EpicFightCompat.init()` in common setup.
  - `AoSServerConfig`: `EpicFightConfig.init()`, `GlidersConfig.init()` and `CreateConfig.init()`, in that place among the other compat sections.
  - `PlayerEventHandler.onAttackEntity` and `ClientGameEvents.onPlayerAttemptAttack`: the vanilla attack cost stands aside in Epic Fight's battle mode (`EpicFightCompat.inBattleMode(player)`).
  - `PlayerActions.tick`: the glide action follows `!player.onGround() && GlidersCompat.isGliding(player)` every tick.
  - `ActionRegistryTests`: the id `create/crank` is checked.

Enabling one again is a port of its own: move its files back under `src/main`, update it to the mod's API for this Minecraft version, put back its calls, mixins and tests, add its dependency to `build.gradle`, `gradle.properties` and `neoforge.mods.toml`, and run the GameTests with `-PwithCompat`.

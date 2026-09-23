# Disabled compats

Compats with mods that have no NeoForge build for this Minecraft version: Paragliders (kept as it is for Minecraft 26.2), and ParCool, Epic Fight, Gliders and Create (kept as they are for Minecraft 1.21.1), so they are not lost.

Nothing under `src/disabled` is compiled or packaged: it is not a Gradle source set. The parts that lived in shared files are here too:

- `java/.../compatmixin/`: their mixins. Their entries in `actionsofstamina.compat.mixins.json` were `ParcoolConfigServerMixin`, `CreateHandCrankBlockMixin`, `CreateValveHandleBlockMixin` and `GlidersToggleGlideMixin`, all in `mixins` (both sides), and `CompatMixinPlugin` applied them for the mod ids `parcool`, `create` and `vc_gliders`.
- `java/.../compatibility/paraglider/`: Paragliders' compat, whose `ParagliderStaminaPlugin` Paragliders finds by its own annotation scan (no mixin).
- `java/.../gametest/DisabledCompatTests.java`: their tests from `CompatTests`, with their `*TestHooks`.
- `resources/META-INF/neoforge.mods.toml.disabled`: their optional dependencies. AoS was ordered `BEFORE` ParCool, which registers its stamina types on its own mod bus while it is constructed.
- Calls from shared code, each removed from its file:
  - `ActionsOfStamina`: `ParcoolCompat.registerActions()`, `EpicFightCompat.registerActions()`, `GlidersCompat.registerActions()`, `CreateCompat.registerActions()` in the constructor, `ParcoolCompat.registerStaminaType()` at its end, and `ParcoolCompat.init()` and `EpicFightCompat.init()` in common setup.
  - `AoSServerConfig`: `ParcoolConfig.init()`, `EpicFightConfig.init()`, `GlidersConfig.init()` and `CreateConfig.init()`, in that place among the other compat sections.
  - `PlayerEventHandler.onAttackEntity` and `ClientGameEvents.onPlayerAttemptAttack`: the vanilla attack cost stands aside in Epic Fight's battle mode (`EpicFightCompat.inBattleMode(player)`).
  - `ClientActionTracker.update`: ParCool's own sprint, swim and crawl are not charged as AoS's (`ParcoolCompat.ownsSprint`, `ownsSwim`, `ownsCrawl`).
  - `PlayerActions.tick`: the glide action follows `!player.onGround() && GlidersCompat.isGliding(player)` every tick.
  - `ActionRegistryTests`: the ids `create/crank` and `parcool/dodge` are checked, and `paraglide` is the compat action looked up by id.
  - Paragliders: `ParagliderCompat.registerActions()` in the `ActionsOfStamina` constructor and `ParagliderConfig.init()` first among the compat sections of `AoSServerConfig`; in `ClientActionTracker.update`, the `ActionFlags.PARAGLIDING` flag from `ParagliderCompat.isParagliding(player)`; in `PlayerActions.tick`, `setActionState(ParagliderCompat.PARAGLIDE, ...)` from that flag, and the `vesselListenerTarget` field with its getter and setter, which `ParagliderBridge` uses; in `PlayerEventHandler.onPlayerJoin`, `ParagliderCompat.onJoin(serverPlayer)` for server players. Its Gradle properties were `paragliders_version=FTwrXq5l` (26.2.1) and `paragliders_range=[26.2,26.3)`.

Enabling one again is a port of its own: move its files back under `src/main`, update it to the mod's API for this Minecraft version, put back its calls, mixins and tests, add its dependency to `build.gradle`, `gradle.properties` and `neoforge.mods.toml`, and run the GameTests with `-PwithCompat`.

# Actions of Stamina

*Minecraft 1.19.2 · Forge 43*

Actions of Stamina (AoS) makes player actions cost stamina. Sprinting, jumping, attacking, swimming, crawling, flying with an elytra and raising a shield all cost stamina, and so do the actions of several popular movement and combat mods. If you don't have enough stamina, the action doesn't happen: you can't start sprinting, a jump is cancelled, a swing is dropped, a roll isn't available.

AoS only decides **how actions cost stamina**. The stamina bar comes from [Feathers of Fatigue](https://github.com/Darkona/feathers-of-fatigue) if it is installed, or from AoS's own simple bar if it isn't.

Supported out of the box, each with its own switch in the config: ParCool, Paragliders, Better Combat, Combat Roll, Epic Fight, Wall-Jump TXF, Gliders and Create.

This is the Minecraft 1.19.2 (Forge) version. It has the same actions, config options and backends as the 1.21.1 version, except for what 1.19.2 doesn't have: see "Not in this version" in the [changelog](CHANGELOG.md).

## Documentation

The [wiki](https://github.com/Darkona/actions-of-stamina/wiki) has the details:

- [Stamina Backends](https://github.com/Darkona/actions-of-stamina/wiki/Stamina-Backends): Feathers of Fatigue's feathers, or AoS's own bar.
- [Vanilla Actions](https://github.com/Darkona/actions-of-stamina/wiki/Vanilla-Actions): what each action costs.
- [Compatibility](https://github.com/Darkona/actions-of-stamina/wiki/Compatibility): what each supported mod's actions cost, and how.
- [Configuration](https://github.com/Darkona/actions-of-stamina/wiki/Configuration): every option.

## For developers

- Java 17. The build uses ModDevGradle's legacy Forge mode.
- Feathers of Fatigue comes from the local Maven repository: publish Feathers of Fatigue's `1.19.2` branch first (`./gradlew publishToMavenLocal` there, version `1.19.2-2.0.0`).
- `./gradlew build` builds the mod.
- `./gradlew runGameTestServer` runs the GameTests: the internal backend (spend, drain, regeneration, exhaustion), backend selection, and the compats against the real mods: each compat's charges, and its refusals when the stamina runs out. A compat's tests pass without doing anything when its mod isn't installed.
- `-PwithoutFeathers` runs without Feathers of Fatigue.
- `-PwithCompat` adds ParCool, Paragliders, Better Combat, Combat Roll, Epic Fight, Wall-Jump TXF, Gliders, Create, Curios and the libraries they need to the dev runs.
- `scripts/client-boot-check.sh` starts a dedicated server of its own on a copy of a superflat world, joins it with a headless client and saves a screenshot to `build/` (`GRADLE_ARGS="-PwithCompat"` for the compat mods). Minecraft 1.19.2 can't open a singleplayer world from the command line, so the check uses a server. Its runs are `aosBootServer` and `aosBoot`.

AoS compiles against the other mods only (`compileOnly`) and bundles none of their code; it bundles MixinExtras, which Forge 43 doesn't ship. Every call into another mod goes through a bridge class that loads only when that mod is present. Mixins into other mods live in `actionsofstamina.compat.mixins.json`, which applies them only when their mod is loaded. The Modrinth dependencies are pinned by Modrinth version id, with the version number next to each one in `gradle.properties`. Paragliders for 1.19.2 is only on CurseForge: it comes from CurseMaven by file id.

Forge 43 constructs mods in parallel, so AoS doesn't depend on the load order: its ParCool stamina type joins ParCool's registry through a mixin on ParCool's registration event. Paragliders 1.7 has no stamina plugin API, so AoS's mixins on its player movement make it read its stamina from AoS. Gliders 1.1 carries its networking library (PalladiumCore) inside its jar; the build extracts it to compile the Gliders compat.

## Credits

Originally created by CCr4ft3r, with thanks to muraokun for the idea and ElenaiDev for the feather icon. Ported to 1.21.1 and back to 1.20.1 and 1.19.2, and extended, by Darkona.

## License

See [LICENSE](LICENSE).

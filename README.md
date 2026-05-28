# Actions of Stamina

*Minecraft 1.20.1 · Forge 47*

Actions of Stamina (AoS) makes player actions cost stamina. Sprinting, jumping, attacking, swimming, crawling, flying with an elytra and raising a shield all cost stamina, and so do the actions of several popular movement and combat mods. If you don't have enough stamina, the action doesn't happen: you can't start sprinting, a jump is cancelled, a swing is dropped, a roll isn't available.

AoS only decides **how actions cost stamina**. The stamina bar comes from [Green Feathers](https://github.com/Darkona/Green-Feathers) if it is installed, or from AoS's own simple bar if it isn't.

Supported out of the box, each with its own switch in the config: ParCool, Paragliders, Better Combat, Combat Roll, Epic Fight, Wall-Jump TXF, Gliders and Create.

This is the Minecraft 1.20.1 (Forge) version. It has the same actions, config options and backends as the 1.21.1 version, except for what 1.20.1 doesn't have: see "Not in this version" in the [changelog](CHANGELOG.md).

## Documentation

The [wiki](https://github.com/Darkona/actions-of-stamina/wiki) has the details:

- [Stamina Backends](https://github.com/Darkona/actions-of-stamina/wiki/Stamina-Backends): Green Feathers' feathers, or AoS's own bar.
- [Vanilla Actions](https://github.com/Darkona/actions-of-stamina/wiki/Vanilla-Actions): what each action costs.
- [Compatibility](https://github.com/Darkona/actions-of-stamina/wiki/Compatibility): what each supported mod's actions cost, and how.
- [Configuration](https://github.com/Darkona/actions-of-stamina/wiki/Configuration): every option.

## For developers

- Java 17. The build uses ModDevGradle's legacy Forge mode.
- Green Feathers comes from the local Maven repository: publish Green Feathers' `1.20.1` branch first (`./gradlew publishToMavenLocal` there, version `1.20.1-2.0.0`).
- `./gradlew build` builds the mod.
- `./gradlew runGameTestServer` runs the GameTests: the internal backend (spend, drain, regeneration, exhaustion), backend selection, and the compats against the real mods: each compat's charges, and its refusals when the stamina runs out. A compat's tests pass without doing anything when its mod isn't installed.
- `-PwithoutFeathers` runs without Green Feathers.
- `-PwithCompat` adds ParCool, Paragliders, Better Combat, Combat Roll, Epic Fight, Wall-Jump TXF, Gliders, Create and the libraries they need to the dev runs.
- `-PwithCurios` adds Curios. With `-PwithCompat` it leaves Epic Fight out: ParCool 4.0.0.5, Curios and Epic Fight 20.14.17 together crash the client on joining a world (see build.gradle).
- `scripts/client-boot-check.sh` boots a headless client into a copy of a superflat world and saves a screenshot to `build/` (`GRADLE_ARGS="-PwithCompat"` for the compat mods).
- `xvfb-run -a ./gradlew runBootCheck` starts a headless client straight into `run/saves/aosboot`.

AoS compiles against the other mods only (`compileOnly`) and bundles none of their code; it bundles MixinExtras, which Forge 47 doesn't ship. Every call into another mod goes through a bridge class that loads only when that mod is present. Mixins into other mods live in `actionsofstamina.compat.mixins.json`, which applies them only when their mod is loaded. The Modrinth dependencies are pinned by Modrinth version id, with the version number next to each one in `gradle.properties`.

Forge 47 constructs mods in parallel, so AoS doesn't depend on the load order: its ParCool stamina type joins ParCool's registry through a mixin on ParCool's registration event. Gliders 1.2 carries its networking library (PalladiumCore) inside its jar; the build extracts it to compile the Gliders compat.

## Credits

Originally created by CCr4ft3r, with thanks to muraokun for the idea and ElenaiDev for the feather icon. Ported to 1.21.1 and back to 1.20.1, and extended, by Darkona.

## License

See [LICENSE](LICENSE).

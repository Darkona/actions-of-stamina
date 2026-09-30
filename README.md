# Actions of Stamina

*Minecraft 26.3 · NeoForge 26.3.0.36-beta*

Actions of Stamina (AoS) makes player actions cost stamina. Sprinting, jumping, attacking, swimming, crawling, elytra flight and a raised shield all cost stamina, and so do the actions of several popular movement and combat mods. Without enough stamina, the action does not happen: you cannot start to sprint, the jump is cancelled, the swing is dropped, the roll is not available.

AoS only decides **how actions cost stamina**. The stamina bar comes from [Feathers of Fatigue](https://github.com/Darkona/feathers-of-fatigue) when it is installed. Without it, AoS uses its own simple bar.

Better Combat, Combat Roll and Wall-Jump TXF work out of the box, each with its own switch in the config. With Curios, wings in a curio slot count too. The versions of AoS for older Minecraft versions also support Paragliders (26.2 and older) and ParCool, Epic Fight, Gliders and Create (1.21.1 and older). These mods have no build for Minecraft 26.3 (see [Minecraft Versions](https://github.com/Darkona/actions-of-stamina/wiki/Minecraft-Versions)).

| ![Mid-roll](wiki/images/combatroll.png) | ![A Better Combat swing](wiki/images/bettercombat.png) |
|---|---|
| A Combat Roll roll | A Better Combat swing |

## Documentation

The [wiki](https://github.com/Darkona/actions-of-stamina/wiki) has the details:

- [Stamina Backends](https://github.com/Darkona/actions-of-stamina/wiki/Stamina-Backends): the feathers of Feathers of Fatigue, or the own bar of AoS.
- [Vanilla Actions](https://github.com/Darkona/actions-of-stamina/wiki/Vanilla-Actions): the cost of each action.
- [Compatibility](https://github.com/Darkona/actions-of-stamina/wiki/Compatibility): the cost of the actions of each supported mod, and how AoS charges them.
- [Configuration](https://github.com/Darkona/actions-of-stamina/wiki/Configuration): all the options.

## For developers

- `./gradlew build` builds the mod.
- `./gradlew runGameTestServer` runs the GameTests. They cover the internal backend (spend, drain, regeneration, exhaustion), the backend selection, and each compat against the real mod: its charges, and its refusals when the stamina runs out. The tests of a compat pass and do nothing when its mod is not installed.
- `-PwithoutFeathers` runs without Feathers of Fatigue.
- `-PwithCompat` adds Better Combat, Combat Roll, Wall-Jump TXF, Curios and their libraries to the dev runs.
- `scripts/client-boot-check.sh` starts a headless client in a copy of a superflat test world and saves a screenshot to `build/`. Use `GRADLE_ARGS="-PwithCompat"` to add the compat mods.
- `xvfb-run -a ./gradlew runBootCheck` starts a headless client directly in `run/saves/aosboot`.

AoS compiles against the other mods only (`compileOnly`) and bundles none of their code. Each call into another mod goes through a bridge class, and that class loads only when the mod is present. The mixins into other mods are in `actionsofstamina.compat.mixins.json`, which applies each one only when its mod is loaded. When the Modrinth maven would resolve a version number to the file of another loader, the build pins that dependency by its Modrinth version id.

The compats of mods without a build for this Minecraft version (Paragliders, ParCool, Epic Fight, Gliders, Create) wait in `src/disabled`. Gradle does not compile that folder. Its README tells how to bring a compat back.

## Credits

CCr4ft3r made the original mod. Thanks to muraokun for the idea and to ElenaiDev for the feather icon. Darkona ported it to 1.21.1 and 26.x and extended it.

## License

See [LICENSE](LICENSE).

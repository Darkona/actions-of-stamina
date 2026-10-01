# Actions of Stamina

*Minecraft 26.1 · NeoForge 26.1.2.109 or later*

Actions of Stamina (AoS) makes player actions cost stamina. Sprinting, jumping, attacking, swimming, crawling, elytra flight and a raised shield all cost stamina, and so do the actions of several popular movement and combat mods. Without enough stamina, the action does not happen: you cannot start to sprint, the jump is cancelled, the swing is dropped, the roll is not available.

AoS only decides **how actions cost stamina**. The stamina bar comes from [Feathers of Fatigue](https://github.com/Darkona/feathers-of-fatigue) for Minecraft 26.1 when it is installed. Without it, AoS uses its own simple bar.

| ![Paragliding](wiki/images/paraglider.png) | ![A Better Combat swing](wiki/images/bettercombat.png) |
|---|---|
| Paragliding | A Better Combat swing |

## Supported mods

Each one is optional and has its own section in the server config, with an `enabled` switch.

| Mod | Versions | What costs stamina |
|---|---|---|
| Paragliders | 26.1.2 up to 26.2 | Paragliding. Paragliders reads its stamina from AoS, and Stamina Vessels make the stamina bar larger. |
| Better Combat | 3.2 up to 4 | Each weapon swing, with multipliers by weapon category. |
| Combat Roll | 3.0 up to 4 | Each roll. |
| Wall-Jump TXF | 26.1.2-1.3 up to 26.2 | Wall jumps, double jumps and wall clinging. |
| Curios | 15.0.0 up to 16 | Wings in a curio slot cost like wings in the chest slot. |

## This version

The [wiki](https://github.com/Darkona/actions-of-stamina/wiki) describes the newest version of AoS. On Minecraft 26.1:

- **Paragliders:** this version has it, and the newest version does not.
- **Farming tools:** a tool counts by the NeoForge item abilities for tilling, making paths, stripping, scraping and unwaxing.
- **No ParCool, Epic Fight, Gliders or Create:** these mods have no build for Minecraft 26.1.

[Minecraft Versions](https://github.com/Darkona/actions-of-stamina/wiki/Minecraft-Versions#261-neoforge-2612) has the full list and the mod versions that AoS was tested with.

## Documentation

- [Stamina Backends](https://github.com/Darkona/actions-of-stamina/wiki/Stamina-Backends): the feathers of Feathers of Fatigue, or the own bar of AoS.
- [Vanilla Actions](https://github.com/Darkona/actions-of-stamina/wiki/Vanilla-Actions): the cost of each action.
- [Compatibility](https://github.com/Darkona/actions-of-stamina/wiki/Compatibility): the cost of the actions of each supported mod, and how AoS charges them.
- [Configuration](https://github.com/Darkona/actions-of-stamina/wiki/Configuration): all the options.
- [Addon API](https://github.com/Darkona/actions-of-stamina/wiki/Addon-API): for mod developers who want to add stamina-costing actions of their own.

## For developers

- `./gradlew build` builds the mod.
- `./gradlew runGameTestServer` runs the GameTests. They cover the internal backend (spend, drain, regeneration, exhaustion), the backend selection, and each compat against the real mod: its charges, and its refusals when the stamina runs out. The tests of a compat pass and do nothing when its mod is not installed.
- `-PwithoutFeathers` runs without Feathers of Fatigue.
- `-PwithCompat` adds Paragliders, Better Combat, Combat Roll, Wall-Jump TXF, Curios and their libraries to the dev runs.
- `scripts/client-boot-check.sh` starts a headless client in a copy of a superflat test world and saves a screenshot to `build/`. Use `GRADLE_ARGS="-PwithCompat"` to add the compat mods.
- `xvfb-run -a ./gradlew runBootCheck` starts a headless client directly in `run/saves/aosboot`.

AoS compiles against the other mods only (`compileOnly`) and bundles none of their code. Each call into another mod goes through a bridge class, and that class loads only when the mod is present. The mixins into other mods are in `actionsofstamina.compat.mixins.json`, which applies each one only when its mod is loaded. When the Modrinth maven would resolve a version number to the file of another loader, the build pins that dependency by its Modrinth version id.

The compats of mods without a build for this Minecraft version (ParCool, Epic Fight, Gliders, Create) wait in `src/disabled`. Gradle does not compile that folder. Its README tells how to bring a compat back.

## Credits

CCr4ft3r made the original mod. Thanks to muraokun for the idea and to ElenaiDev for the feather icon. Darkona ported it to 1.21.1 and 26.x and extended it.

## License

See [LICENSE](LICENSE).

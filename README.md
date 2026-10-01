# Actions of Stamina

*Minecraft 1.20.1 · Forge 47 or later*

Actions of Stamina (AoS) makes player actions cost stamina. Sprinting, jumping, attacking, swimming, crawling, elytra flight and a raised shield all cost stamina, and so do the actions of several popular movement and combat mods. Without enough stamina, the action does not happen: you cannot start to sprint, the jump is cancelled, the swing is dropped, the roll is not available.

AoS only decides **how actions cost stamina**. The stamina bar comes from [Feathers of Fatigue](https://github.com/Darkona/feathers-of-fatigue) for Minecraft 1.20.1 when it is installed. Without it, AoS uses its own simple bar.

## Supported mods

Each one is optional and has its own section in the server config, with an `enabled` switch.

| Mod | Versions | What costs stamina |
|---|---|---|
| ParCool | 4.0 or later | All parkour actions. AoS adds its own ParCool stamina type, so ParCool does not charge an action twice. |
| Paragliders | 20.1 or later | Paragliding. Paragliders reads its stamina from AoS, and Stamina Vessels make the stamina bar larger. |
| Better Combat | 1.9 or later | Each weapon swing, with multipliers by weapon category. |
| Combat Roll | 1.3 or later | Each roll. |
| Epic Fight | 20.14 or later | Dodge, guard, weapon innate and mover skills, and the swings of the battle mode. |
| Wall-Jump TXF | 1.20.1-1.3 or later | Wall jumps, double jumps and wall clinging. |
| Gliders | 1.2 or later | Gliding with a deployed glider. |
| Create | 6.0 or later | Hand cranks and valve handles that you turn. |
| Curios | 5.0 or later | Wings in a curio slot cost like wings in the chest slot. |

ParCool 4.0.0.5 needs Forge 47.4.23 or later.

**Known issue, not in AoS:** ParCool 4.0.0.5, Curios 5.14.1 and Epic Fight 20.14.17 together crash the game when you join a world. Any two of the three work.

## This version

The [wiki](https://github.com/Darkona/actions-of-stamina/wiki) describes the newest version of AoS. On Minecraft 1.20.1, there is no mace smash and no wind charge, because Minecraft 1.20.1 does not have them. [Minecraft Versions](https://github.com/Darkona/actions-of-stamina/wiki/Minecraft-Versions#1201-forge-47) has the full list and the mod versions that AoS was tested with.

## Documentation

- [Stamina Backends](https://github.com/Darkona/actions-of-stamina/wiki/Stamina-Backends): the feathers of Feathers of Fatigue, or the own bar of AoS.
- [Vanilla Actions](https://github.com/Darkona/actions-of-stamina/wiki/Vanilla-Actions): the cost of each action.
- [Compatibility](https://github.com/Darkona/actions-of-stamina/wiki/Compatibility): the cost of the actions of each supported mod, and how AoS charges them.
- [Configuration](https://github.com/Darkona/actions-of-stamina/wiki/Configuration): all the options.
- [Addon API](https://github.com/Darkona/actions-of-stamina/wiki/Addon-API): for mod developers who want to add stamina-costing actions of their own. On Forge, ids are `ResourceLocation`, the config section is built on a `ForgeConfigSpec.Builder`, and `ActionPerformedPacket` goes to the server through `PacketHandler.sendToServer`.

## For developers

- Java 17. The build uses the legacy Forge mode of ModDevGradle.
- Feathers of Fatigue comes from the local Maven repository. Publish the `1.20.1` branch of Feathers of Fatigue first (`./gradlew publishToMavenLocal` there, version `1.20.1-2.0.0`).
- `./gradlew build` builds the mod.
- `./gradlew runGameTestServer` runs the GameTests. They cover the internal backend (spend, drain, regeneration, exhaustion), the backend selection, and each compat against the real mod: its charges, and its refusals when the stamina runs out. The tests of a compat pass and do nothing when its mod is not installed.
- `-PwithoutFeathers` runs without Feathers of Fatigue.
- `-PwithCompat` adds ParCool, Paragliders, Better Combat, Combat Roll, Epic Fight, Wall-Jump TXF, Gliders, Create and their libraries to the dev runs.
- `-PwithCurios` adds Curios. With `-PwithCompat`, it leaves Epic Fight out, because of the crash above.
- `scripts/client-boot-check.sh` starts a headless client in a copy of a superflat test world and saves a screenshot to `build/`. Use `GRADLE_ARGS="-PwithCompat"` to add the compat mods.
- `xvfb-run -a ./gradlew runBootCheck` starts a headless client directly in `run/saves/aosboot`.

AoS compiles against the other mods only (`compileOnly`) and bundles none of their code. It bundles MixinExtras, because Forge 47 does not ship it. Each call into another mod goes through a bridge class, and that class loads only when the mod is present. The mixins into other mods are in `actionsofstamina.compat.mixins.json`, which applies each one only when its mod is loaded. The build pins the Modrinth dependencies by Modrinth version id, with the version number next to each one in `gradle.properties`.

Forge 47 constructs mods in parallel. So that AoS does not depend on the load order, its ParCool stamina type joins the registry of ParCool through a mixin on the registration event of ParCool. Gliders 1.2 carries its networking library (PalladiumCore) inside its jar. The build extracts it to compile the Gliders compat.

## Credits

CCr4ft3r made the original mod. Thanks to muraokun for the idea and to ElenaiDev for the feather icon. Darkona ported it to 1.21.1, extended it, and brought that version back to 1.20.1.

## License

See [LICENSE](LICENSE).

# Actions of Stamina

*Minecraft 1.18.2 · Forge 40.2.4 or later*

Actions of Stamina (AoS) makes player actions cost stamina. Sprinting, jumping, attacking, swimming, crawling, elytra flight and a raised shield all cost stamina, and so do the actions of several popular movement and combat mods. Without enough stamina, the action does not happen: you cannot start to sprint, the jump is cancelled, the swing is dropped, the roll is not available.

AoS only decides **how actions cost stamina**. The stamina bar comes from [Feathers of Fatigue](https://github.com/Darkona/feathers-of-fatigue) for Minecraft 1.18.2 when it is installed. Without it, AoS uses its own simple bar. Elenai's Feathers, which version 0.5 needed, is not supported any more.

## Supported mods

Each one is optional and has its own section in the server config, with an `enabled` switch.

| Mod | Versions | What costs stamina |
|---|---|---|
| ParCool | 3.4 or a later 3.x | All parkour actions. The stamina of AoS replaces the own stamina of ParCool. |
| Paragliders | 1.6 or later | Paragliding. Paragliders reads its stamina from AoS, and Stamina Vessels make the stamina bar larger. |
| Better Combat | 1.6 or later | Each weapon swing, with multipliers by weapon category. |
| Combat Roll | 1.1 or later | Each roll. |
| Epic Fight | 18.5 or later | Dodge, weapon innate and mover skills, and the swings of the battle mode. |
| Wall-Jump TXF | 1.18.2-1.3 or later | Wall jumps, double jumps and wall clinging. |
| Create | 0.5.1 or later | Hand cranks and valve handles that you turn. |
| Curios | 1.18.2-5.0 or later | Wings in a curio slot cost like wings in the chest slot. |

Paragliders for 1.18.2 is only on CurseForge.

## This version

The [wiki](https://github.com/Darkona/actions-of-stamina/wiki) describes the newest version of AoS. On Minecraft 1.18.2:

- **No brushing, mace smash, wind charges or chest boats:** Minecraft 1.18.2 does not have them.
- **No Gliders:** Gliders has no build for 1.18.2.
- **ParCool 3:** it has no grapple or castaway. A player who selects the Hunger stamina in the client options of ParCool pays both food and stamina.
- **Epic Fight guard:** Epic Fight 18 takes the stamina of a blocked hit from its own bar, so `[epicfight.guard]` does nothing.
- **Better Combat:** version 1.6 has no `lance` category.

[Minecraft Versions](https://github.com/Darkona/actions-of-stamina/wiki/Minecraft-Versions#1182-forge-40) has the full list and the mod versions that AoS was tested with.

## Documentation

- [Stamina Backends](https://github.com/Darkona/actions-of-stamina/wiki/Stamina-Backends): the feathers of Feathers of Fatigue, or the own bar of AoS.
- [Vanilla Actions](https://github.com/Darkona/actions-of-stamina/wiki/Vanilla-Actions): the cost of each action.
- [Compatibility](https://github.com/Darkona/actions-of-stamina/wiki/Compatibility): the cost of the actions of each supported mod, and how AoS charges them.
- [Configuration](https://github.com/Darkona/actions-of-stamina/wiki/Configuration): all the options.
- [Addon API](https://github.com/Darkona/actions-of-stamina/wiki/Addon-API): for mod developers who want to add stamina-costing actions of their own. On Forge, ids are `ResourceLocation`, the config section is built on a `ForgeConfigSpec.Builder`, and `ActionPerformedPacket` goes to the server through `PacketHandler.sendToServer`.

## For developers

- Java 17. The build uses the legacy Forge mode of ModDevGradle.
- Feathers of Fatigue comes from the local Maven repository. Publish the `1.18.2` branch of Feathers of Fatigue first (`./gradlew publishToMavenLocal` there, version `1.18.2-2.0.0`).
- `./gradlew build` builds the mod.
- `./gradlew runGameTestServer` runs the GameTests. They cover the internal backend (spend, drain, regeneration, exhaustion), the backend selection, and each compat against the real mod: its charges, and its refusals when the stamina runs out. The tests of a compat pass and do nothing when its mod is not installed.
- `-PwithoutFeathers` runs without Feathers of Fatigue.
- `-PwithCompat` adds ParCool, Paragliders, Better Combat, Combat Roll, Epic Fight, Wall-Jump TXF, Create, Curios and their libraries to the dev runs.
- `scripts/client-boot-check.sh` starts a dedicated server on a copy of a superflat world, joins it with a headless client and saves a screenshot to `build/`. Use `GRADLE_ARGS="-PwithCompat"` to add the compat mods. Minecraft 1.18.2 cannot open a singleplayer world from the command line, so the check uses a server. Its runs are `aosBootServer` and `aosBoot`.

AoS compiles against the other mods only (`compileOnly`) and bundles none of their code. It bundles MixinExtras, because Forge 40 does not ship it. Forge applies mixins from bundled libraries since 40.2.3. Each call into another mod goes through a bridge class, and that class loads only when the mod is present. The mixins into other mods are in `actionsofstamina.compat.mixins.json`, which applies each one only when its mod is loaded. The build pins the Modrinth dependencies by Modrinth version id, with the version number next to each one in `gradle.properties`. Paragliders comes from CurseMaven by file id.

ParCool 3 has no stamina type registry. The stamina of the local player comes from the client option `used_stamina` of ParCool, and a mixin on the stamina capability of ParCool puts the stamina of AoS in place of the own (Default) stamina of ParCool. Paragliders 1.6 has no stamina plugin API, so the mixins of AoS on its player movement make it read its stamina from AoS.

## Credits

CCr4ft3r made the original mod. Thanks to muraokun for the idea and to ElenaiDev for the feather icon. Darkona ported it to 1.21.1, extended it, and brought that version back to 1.20.1, 1.19.2 and 1.18.2.

## License

See [LICENSE](LICENSE).

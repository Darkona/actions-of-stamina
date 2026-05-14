# Actions of Stamina

*Minecraft 1.21.1 · NeoForge 21.1*

Actions of Stamina (AoS) makes player actions cost stamina. Sprinting, jumping, attacking, swimming, crawling,
flying with an elytra and raising a shield all cost stamina, and so do the actions of several popular movement
and combat mods. If you don't have enough stamina, the action doesn't happen: you can't start sprinting, a jump
is cancelled, a swing is dropped, a roll isn't available.

AoS only decides **how actions cost stamina**. The stamina bar comes from
[Green Feathers](https://github.com/Darkona/Green-Feathers) if it is installed, or from AoS's own simple bar if it
isn't.

Supported out of the box, each with its own switch in the config: ParCool, Paragliders, Better Combat, Combat Roll,
Epic Fight, Wall-Jump TXF and Gliders.

## Documentation

The [wiki](https://github.com/Darkona/actions-of-stamina/wiki) has the details:

- [Stamina Backends](https://github.com/Darkona/actions-of-stamina/wiki/Stamina-Backends): Green Feathers' feathers, or AoS's own bar.
- [Vanilla Actions](https://github.com/Darkona/actions-of-stamina/wiki/Vanilla-Actions): what each action costs.
- [Compatibility](https://github.com/Darkona/actions-of-stamina/wiki/Compatibility): what each supported mod's actions cost, and how.
- [Configuration](https://github.com/Darkona/actions-of-stamina/wiki/Configuration): every option.

## For developers

- `./gradlew build` builds the mod.
- `./gradlew runGameTestServer` runs the GameTests: the internal backend (spend, drain, regeneration,
  exhaustion), backend selection, and the compats against the real mods: each compat's charges, and its refusals
  when the stamina runs out. A compat's tests pass without doing anything when its mod isn't installed.
- `-PwithoutFeathers` runs without Green Feathers.
- `-PwithCompat` adds ParCool, Paragliders, Better Combat, Combat Roll, Epic Fight, Wall-Jump TXF, Gliders and the
  libraries they need to the dev runs.
- `scripts/client-boot-check.sh` boots a headless client into a copy of `run/world` and saves a screenshot to
  `build/` (`GRADLE_ARGS="-PwithCompat"` for the compat mods).
- `xvfb-run -a ./gradlew runBootCheck` starts a headless client straight into `run/saves/aosboot`.

AoS compiles against the other mods only (`compileOnly`) and bundles none of their code. Every call into another
mod goes through a bridge class that loads only when that mod is present. Mixins into other mods live in
`actionsofstamina.compat.mixins.json`, which applies them only when their mod is loaded. The Modrinth dependencies
are pinned by Modrinth version id where Modrinth's maven would resolve a version number to another loader's file.

AoS is ordered before ParCool: ParCool registers stamina types on its own mod bus while it is being constructed,
so AoS adds its listener there from its own constructor.

## Credits

Originally created by CCr4ft3r, with thanks to muraokun for the idea and ElenaiDev for the feather icon. Ported to
1.21.1 and extended by Darkona.

## License

See [LICENSE](LICENSE).

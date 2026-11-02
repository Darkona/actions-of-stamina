# Minecraft Versions

This wiki describes Actions of Stamina for **Minecraft 1.21.1 (NeoForge)**. The mod also exists for Minecraft 1.20.1 (Forge), with the same actions, config options and stamina backends (Feathers of Fatigue or the own bar) where the game allows it. This page lists what is different on 1.20.1.

| Minecraft | Loader | Differences |
|---|---|---|
| 1.21.1 | NeoForge 21.1.252 or later | None: this whole wiki |
| 1.20.1 | Forge 47.4.23 or later | [1.20.1](#1201-forge-47) |

Each build is named `<minecraft>-<mod>`, for example `1.21.1-1.0.1`, and the [releases](https://github.com/Darkona/actions-of-stamina/releases) have one jar for each Minecraft version.

Each version works with the [Feathers of Fatigue](https://github.com/Darkona/feathers-of-fatigue) for the same Minecraft version (`1.21.1-1.1.0` or later on 1.21.1, `1.20.1-1.1.0` or later on 1.20.1). Through its API, AoS turns off the basic exertion of Feathers of Fatigue (see [Stamina Backends](Stamina-Backends)).

**Tested with, on 1.21.1:** ParCool 4.0.0.5, Paragliders 21.1.5, Better Combat 2.4.0, Combat Roll 2.0.6, Epic Fight 21.17.3.1, Wall-Jump TXF 1.21.1-1.3.8, Gliders 1.1.8, Create 6.0.10, Curios 9.5.1.

## 1.20.1 (Forge 47)

**Missing, compared with 1.21.1:**

- **Mace smash:** 1.20.1 has no mace, so the `[vanilla.attack] mace_smash_multiplier` option does not exist.
- **Wind charges:** 1.20.1 has no wind charge.

**Different:**

- **[Addon API](Addon-API):** the same classes and calls. The config section is built on a `ForgeConfigSpec.Builder`, and `ActionPerformedPacket` goes to the server through `PacketHandler.sendToServer`.

**Known issue, not in Actions of Stamina:** ParCool 4.0.0.5, Curios 5.14.1 and Epic Fight 20.14.17 together crash the game when you join a world. Any two of the three work. The bug is between those three mods.

**Tested with:** ParCool 4.0.0.5, Paragliders 20.1.3, Better Combat 1.9.0, Combat Roll 1.3.3, Epic Fight 20.14.17, Wall-Jump TXF 1.3.8, Gliders 1.2.0, Create 6.0.8, Curios 5.14.1. ParCool needs Forge 47.4.23 or later.

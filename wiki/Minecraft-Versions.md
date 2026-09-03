# Minecraft Versions

This wiki describes the newest version of Actions of Stamina, for **Minecraft 1.21.1 (NeoForge)**. The same mod is also made for older Minecraft versions, with the same actions, config options and stamina backends (Green Feathers or its own bar) wherever the game allows it. This page lists only what is different in each of them.

| Minecraft | Loader | Differences |
|---|---|---|
| 1.21.1 | NeoForge 21.1 | None: this whole wiki |
| 1.20.1 | Forge 47 | [1.20.1](#1201-forge-47) |
| 1.19.2 | Forge 43 | [1.19.2](#1192-forge-43) |
| 1.18.2 | Forge 40.2.4 or later | [1.18.2](#1182-forge-40) |

Each version works with the [Green Feathers](https://github.com/Darkona/green-feathers) made for the same Minecraft version. The 0.5 versions for 1.18.2 and 1.19.2 needed Elenai's Feathers; from 0.6 on it is no longer supported.

## 1.20.1 (Forge 47)

**Missing:**

- **Mace smash:** 1.20.1 has no mace, so there is no `[vanilla.attack] mace_smash_multiplier` option.
- **Wind charges:** 1.20.1 has no wind charge.

**Different:**

- **[Addon API](Addon-API):** the same classes and calls. The config section is built on a `ForgeConfigSpec.Builder`, and `ActionPerformedPacket` goes to the server through `PacketHandler.sendToServer`.

**Known issue, not in Actions of Stamina:** ParCool 4.0.0.5, Curios 5.14.1 and Epic Fight 20.14.17 installed together crash the game on joining a world. Any two of the three work. This is a bug between those three mods, not in Actions of Stamina.

**Tested with:** ParCool 4.0.0.5, Paragliders 20.1.3, Better Combat 1.9.0, Combat Roll 1.3.3, Epic Fight 20.14.17, Wall-Jump TXF 1.3.8, Gliders 1.2.0, Create 6.0.8, Curios 5.14.1. ParCool needs Forge 47.4.23 or later.

## 1.19.2 (Forge 43)

**Missing:** everything missing in 1.20.1, and:

- **Brushing:** 1.19.2 has no brush (archaeology came in 1.20), so there is no `[vanilla.brush]` section.
- **Bamboo rafts:** they don't exist; the `actionsofstamina:rowed_boats` tag holds boats and chest boats.
- **Epic Fight guard:** Epic Fight 19 takes a blocked hit's stamina from its own bar without telling other mods, so `[epicfight.guard]` does nothing.

**Different:**

- **Paragliders:** Paragliders 1.7 has no stamina API. Actions of Stamina hooks into it differently, with the same result: Paragliders reads its stamina from Actions of Stamina and neither regenerates nor drains its own.
- ParCool, Curios and Epic Fight work together here.
- **[Addon API](Addon-API):** as in 1.20.1.

**Tested with:** ParCool 4.0.0.5, Paragliders 1.7.0.5, Better Combat 1.7.1, Combat Roll 1.1.5, Epic Fight 19.5.26, Wall-Jump TXF 1.3.8, Gliders 1.1.4, Create 0.5.1, Curios 5.1.6.

## 1.18.2 (Forge 40)

Needs Forge 40.2.4 or later.

**Missing:** everything missing in 1.19.2, and:

- **Chest boats:** they don't exist either; the `actionsofstamina:rowed_boats` tag holds the boat only.
- **Gliders:** it has no 1.18.2 build, so there is no `[gliders]` section.
- **ParCool grapple and castaway:** ParCool 3 doesn't have them.
- **Better Combat `lance`:** Better Combat 1.6 has no lance category; the other 27 are the same.
- **Fishing rods by tool action:** a modded rod counts only when it is built on the vanilla fishing rod.

**Different:**

- **ParCool 3:** it has no stamina types, so Actions of Stamina's stamina replaces ParCool's own, and ParCool's stamina bar is hidden. A player who picks ParCool's Hunger stamina in ParCool's client options keeps it, and then pays both food (ParCool) and stamina (Actions of Stamina). The costs follow ParCool 3's own: `[parcool]` has `jump_from_bar` and no `grapple` or `castaway`.
- **Paragliders:** works as in 1.19.2 (Paragliders 1.6).
- **[Addon API](Addon-API):** as in 1.20.1.

**Tested with:** ParCool 3.4.3.3, Paragliders 1.6.0.6, Better Combat 1.6.2, Combat Roll 1.1.5, Epic Fight 18.5.26, Wall-Jump TXF 1.3.8, Create 0.5.1, Curios 5.0.9.

# Minecraft Versions

This wiki describes the newest version of Actions of Stamina, for **Minecraft 26.3 (NeoForge)**. The mod also exists for older Minecraft versions, with the same actions, config options and stamina backends (Feathers of Fatigue or the own bar) where the game allows it. This page lists only the differences of each version.

| Minecraft | Loader | Differences |
|---|---|---|
| 26.3 | NeoForge 26.3.0.36-beta | None: this whole wiki |
| 26.2 | NeoForge 26.2.0.88 or later | [26.2](#262-neoforge-262) |
| 26.1 | NeoForge 26.1.2.109 or later | [26.1](#261-neoforge-2612) |
| 1.21.11 | NeoForge 21.11.42 or later | [1.21.11](#12111-neoforge-2111) |
| 1.21.1 | NeoForge 21.1 | [1.21.1](#1211-neoforge-211) |
| 1.20.1 | Forge 47 | [1.20.1](#1201-forge-47) |
| 1.19.2 | Forge 43 | [1.19.2](#1192-forge-43) |
| 1.18.2 | Forge 40.2.4 or later | [1.18.2](#1182-forge-40) |

Each version works with the [Feathers of Fatigue](https://github.com/Darkona/feathers-of-fatigue) for the same Minecraft version. The 0.5 versions for 1.18.2 and 1.19.2 needed Elenai's Feathers. From 0.6 on, the mod does not support it.

## 26.3 (NeoForge 26.3.0.36-beta)

Needs exactly NeoForge 26.3.0.36-beta. NeoForge 26.3 is in beta, and 26.3.0.37-beta changes the config types. A mod built for one side of that change does not load on the other.

**Missing:**

- **Paragliders:** it has no build for Minecraft 26.3, so its compat and config section are not in this version.
- **ParCool, Epic Fight, Gliders, Create:** none of them has a build for Minecraft 26.x either.

**Tested with:** Better Combat 3.2.2, Combat Roll 3.0.1, Wall-Jump TXF 26.3-1.3.8, Curios 17.0.0-beta.2 (the only Curios builds for 26.3 are betas), Feathers of Fatigue 26.3-1.0.0-beta.1.

## 26.2 (NeoForge 26.2)

**Has, on top of 26.3:** Paragliders, with its `[paragliders]` section in the server config (see [Configuration](Configuration#sections-only-on-older-minecraft-versions) and [Compatibility](Compatibility#paragliders)).

**Different:**

- **Farming tools:** a tool counts by the NeoForge item abilities for tilling, making paths, stripping, scraping and unwaxing. On 26.3, the mod looks at the block transformer of the item.

**Known issue, not in Actions of Stamina:** Paragliders 26.2.1 cannot read its own loot modifiers. Elder guardians therefore drop no Spirit Orbs, and the wither drops no Stamina Vessel.

**Tested with:** Paragliders 26.2.1, Better Combat 3.2.2, Combat Roll 3.0.1, Wall-Jump TXF 26.2-1.3.8, Curios 16.0.0, Feathers of Fatigue 26.2-1.0.0.

## 26.1 (NeoForge 26.1.2)

The same as 26.2.

**Tested with:** Paragliders 26.1.2, Better Combat 3.2.2, Combat Roll 3.0.1, Wall-Jump TXF 26.1.2-1.3.8, Curios 15.0.0, Feathers of Fatigue 26.1-1.0.0.

## 1.21.11 (NeoForge 21.11)

The same as 26.1, and:

**Has, on top of 26.x:** ParCool 3, with its `[parcool]` section in the server config (see [Configuration](Configuration#sections-only-on-older-minecraft-versions) and [Compatibility](Compatibility#on-older-minecraft-versions)). It works as on 1.18.2, which has ParCool 3 too. The own stamina of ParCool (its client option `used_stamina = PARCOOL`, or the `forced_stamina` of the server) shows the stamina of Actions of Stamina and charges nothing. The stamina bar of ParCool is hidden. A player who selects `used_stamina = HUNGER` pays both food (ParCool) and stamina (Actions of Stamina). `[parcool]` has `jump_from_bar` and no `grapple` or `castaway`.

**Missing:** Epic Fight, Gliders and Create have no build for Minecraft 1.21.11.

**Tested with:** ParCool 3.4.3.3, Paragliders 21.11.0-beta.6, Better Combat 3.1.0, Combat Roll 3.0.1, Wall-Jump TXF 1.21.11-1.3.8, Curios 14.0.0, Feathers of Fatigue 1.21.11-1.0.0.

## 1.21.1 (NeoForge 21.1)

**Has, on top of 26.x:** ParCool (version 4, with the own ParCool stamina type of Actions of Stamina), Epic Fight, Gliders and Create, with their sections in the server config (see [Configuration](Configuration#sections-only-on-older-minecraft-versions) and [Compatibility](Compatibility#on-older-minecraft-versions)).

**Different:**

- **Spears:** 1.21.1 has no spears. The draw action counts the spear use animation, which on 1.21.1 belongs to the trident.
- **Shields:** a modded shield counts when it has the NeoForge `shield_block` item ability. On 26.x, the mod looks for the `minecraft:blocks_attacks` component.
- **Boats:** the `actionsofstamina:rowed_boats` tag holds `minecraft:boat` and `minecraft:chest_boat`. These two cover all wood types and the bamboo raft.
- **Eggs:** only the white egg exists.
- **[Addon API](Addon-API):** ids are `ResourceLocation`.

**Tested with:** ParCool 4.0.0.5, Paragliders 21.1.5, Better Combat 2.4.0, Combat Roll 2.0.6, Epic Fight 21.17.3.1, Wall-Jump TXF 1.21.1-1.3.8, Gliders 1.1.8, Create 6.0.10, Curios 9.5.1.

## 1.20.1 (Forge 47)

**Missing, compared with 1.21.1:**

- **Mace smash:** 1.20.1 has no mace, so the `[vanilla.attack] mace_smash_multiplier` option does not exist.
- **Wind charges:** 1.20.1 has no wind charge.

**Different:**

- **[Addon API](Addon-API):** the same classes and calls. The config section is built on a `ForgeConfigSpec.Builder`, and `ActionPerformedPacket` goes to the server through `PacketHandler.sendToServer`.

**Known issue, not in Actions of Stamina:** ParCool 4.0.0.5, Curios 5.14.1 and Epic Fight 20.14.17 together crash the game when you join a world. Any two of the three work. The bug is between those three mods.

**Tested with:** ParCool 4.0.0.5, Paragliders 20.1.3, Better Combat 1.9.0, Combat Roll 1.3.3, Epic Fight 20.14.17, Wall-Jump TXF 1.3.8, Gliders 1.2.0, Create 6.0.8, Curios 5.14.1. ParCool needs Forge 47.4.23 or later.

## 1.19.2 (Forge 43)

**Missing:** all that is missing in 1.20.1, and:

- **Brushing:** 1.19.2 has no brush (archaeology came in 1.20), so the `[vanilla.brush]` section does not exist.
- **Bamboo rafts:** they do not exist. The `actionsofstamina:rowed_boats` tag holds boats and chest boats.
- **Epic Fight guard:** Epic Fight 19 takes the stamina of a blocked hit from its own bar and does not tell other mods. `[epicfight.guard]` therefore does nothing.

**Different:**

- **Paragliders:** Paragliders 1.7 has no stamina API. Actions of Stamina connects to it in a different way, with the same result: Paragliders reads its stamina from Actions of Stamina, and its own stamina does not regenerate or drain.
- ParCool, Curios and Epic Fight work together on this version.
- **[Addon API](Addon-API):** as in 1.20.1.

**Tested with:** ParCool 4.0.0.5, Paragliders 1.7.0.5, Better Combat 1.7.1, Combat Roll 1.1.5, Epic Fight 19.5.26, Wall-Jump TXF 1.3.8, Gliders 1.1.4, Create 0.5.1, Curios 5.1.6.

## 1.18.2 (Forge 40)

Needs Forge 40.2.4 or later.

**Missing:** all that is missing in 1.19.2, and:

- **Chest boats:** they do not exist either. The `actionsofstamina:rowed_boats` tag holds only the boat.
- **Gliders:** it has no 1.18.2 build, so the `[gliders]` section does not exist.
- **ParCool grapple and castaway:** ParCool 3 does not have them.
- **Better Combat `lance`:** Better Combat 1.6 has no lance category. The other 27 categories are the same.
- **Fishing rods by tool action:** a modded rod counts only when it is built on the vanilla fishing rod.

**Different:**

- **ParCool 3:** it has no stamina types. The stamina of Actions of Stamina replaces the own stamina of ParCool, and the stamina bar of ParCool is hidden. A player who selects the Hunger stamina in the client options of ParCool keeps it, and then pays both food (ParCool) and stamina (Actions of Stamina). The costs follow the costs of ParCool 3: `[parcool]` has `jump_from_bar` and no `grapple` or `castaway`.
- **Paragliders:** works as in 1.19.2 (Paragliders 1.6).
- **[Addon API](Addon-API):** as in 1.20.1.

**Tested with:** ParCool 3.4.3.3, Paragliders 1.6.0.6, Better Combat 1.6.2, Combat Roll 1.1.5, Epic Fight 18.5.26, Wall-Jump TXF 1.3.8, Create 0.5.1, Curios 5.0.9.

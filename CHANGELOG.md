# Changelog

All notable changes to Actions of Stamina are in this file. The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and each change has its date.

Each Minecraft version has its own build, named `<minecraft>-<mod>`: 1.21.1-1.0.0, 26.1-1.0.0 (relabeled 26.1.2-1.0.1), 26.2-1.0.0 and 26.3-1.0.0-beta.1 are on NeoForge. An entry that starts with a Minecraft version applies only from that version on. All of them were released on 2026-10-01. The builds 1.21.11-1.0.0 (NeoForge) and 1.20.1-1.0.0, 1.19.2-1.0.0 and 1.18.2-1.0.0 (Forge) were released on the same day, and their changes are in the changelog of their own branch. Later the same day, 1.0.1 followed on each Minecraft version (26.1.2-1.0.2 on 26.1.2, 26.3-1.0.0-beta.2 on 26.3).

Planned: Paragliders on Minecraft 26.3, and ParCool, Epic Fight, Gliders and Create on Minecraft 26.x, when they publish a build for it.

## [1.0.0-beta.2] - 2026-10-01

### Changed
- 2026-10-01: With Feathers of Fatigue installed, AoS turns off the basic exertion of Feathers of Fatigue (its own sprint and jump costs) through the Feathers of Fatigue API, so a player never pays twice for the same action. Before, Feathers of Fatigue looked for AoS itself; the result is the same. AoS now needs Feathers of Fatigue 26.3-1.0.0-beta.2 or later. The same change is in the 1.0.1 builds of the other Minecraft versions (26.1.2-1.0.2 on 26.1.2), each with the Feathers of Fatigue 1.0.1 of the same Minecraft version or later (26.1.2-1.0.2 on 26.1.2).
- 2026-10-01: **Minecraft 26.1.2:** 26.1.2-1.0.2 requires NeoForge 26.1.2.109 or later (it was 26.1.2.112), like Feathers of Fatigue and Droplets of Thirst 26.1.2-1.0.2: KubeJS 8.0.6 only loads on NeoForge 26.1.2.109.

## [1.0.0-beta.1] - 2026-10-01

### Added
- 2026-09-23: Minecraft 26.3 version (26.3-1.0.0-beta.1), on NeoForge 26.3.0.36-beta exactly. NeoForge 26.3.0.37-beta changes the config types, and a mod built for one side of that change does not load on the other. It has the same actions, config and Addon API as 26.2-1.0.0, except for the 26.3 entries in this file.
- 2026-09-23: **Minecraft 26.3:** tested with Better Combat 3.2.2, Combat Roll 3.0.1, Wall-Jump TXF 26.3-1.3.8, Curios 17.0.0-beta.2 (the only Curios builds for 26.3 are betas) and Feathers of Fatigue 26.3-1.0.0-beta.1.
- 2026-09-20: Minecraft 26.2 version (26.2-1.0.0), on NeoForge 26.2.0.88 or later. It has the same actions, config and Addon API as 26.1-1.0.0.
- 2026-09-20: **Minecraft 26.2:** tested with Paragliders 26.2.1, Better Combat 3.2.2, Combat Roll 3.0.1, Wall-Jump TXF 26.2-1.3.8, Curios 16.0.0 and Feathers of Fatigue 26.2-1.0.0. Paragliders 26.2.1 cannot read its own loot modifiers on Minecraft 26.2 and logs an error at load, so elder guardians drop no Spirit Orbs and the wither no Stamina Vessel. Vessels from goddess statues still add max stamina.
- 2026-09-19: Minecraft 26.1.2 version (26.1-1.0.0), on NeoForge 26.1.2.112 or later. It has the same actions, config and Addon API as 1.21.1-1.0.0, except for the 26.1 entries in this file.
- 2026-09-19: **Minecraft 26.1, spears:** a spear stab is an attack. It costs once per stab, however many creatures it pierces, and counts towards `times_performed_to_exhaust` with the other attacks. A stab that reaches nothing is a swing at the air (`only_for_hits`). Without the stamina, a stab hits nothing, or lands weakened with `exhausted_mode = WEAKEN`. To hold a spear ready to charge is free.
- 2026-09-19: **Minecraft 26.1:** tested with Paragliders 26.1.2, Better Combat 3.2.2, Combat Roll 3.0.1, Wall-Jump TXF 26.1.2-1.3.8, Curios 15.0.0 and Feathers of Fatigue 26.1-1.0.0.
- 2026-09-19: **Minecraft 26.1:** each compat accepts its mod from the tested version up to the next major version. A mod outside that range stops the game at load with a message.
- 2026-07-05: **Addon API:** other mods can add their own actions, with an `ActionCostConfig` section, an `Action` subclass and a type registered in `ActionTypes`. A continuous action can charge a `finish_cost` when it ends. See the Addon API page of the wiki.
- 2026-07-02: **Attack:** girl mode (`weaken_non_weapons`, on by default). While the stamina is short, WEAKEN also weakens bare-handed and other non-weapon attacks, even when they are free.
- 2026-05-19: **Throwables (throw):** wind charges cost stamina to throw.
- 2026-05-19: **Climbing:** ladders, vines, scaffolding and anything else climbable cost stamina, off by default. Out of stamina, you cannot go up: you hold on or slide down slowly.
- 2026-05-19: **Rowing:** only boats in the `actionsofstamina:rowed_boats` entity tag cost stamina (vanilla boats, chest boats and rafts), so sail or motor boats from other mods do not. Off by default. Out of stamina, the paddles stop and the boat drifts.
- 2026-05-19: **Riptide:** a trident launch costs stamina when you release it, off by default. A launch you cannot afford does not happen.
- 2026-05-19: **Fishing:** each cast and each reel of a rod costs stamina, off by default. Modded rods count (by the cast ability of the rod). A cast or reel you cannot afford does not happen.
- 2026-05-19: **Farming tools:** tilling, making paths, stripping logs and scraping or unwaxing copper cost stamina once every few blocks, off by default. Modded tools count. A block you cannot afford stays as it is.
- 2026-05-19: **Brushing:** brushing drains stamina, off by default. Out of stamina, brushing stops.
- 2026-05-19: **Mace smash:** the falling mace attack has a charge of its own, at the attack cost times `mace_smash_multiplier` (2 by default).
- 2026-05-17: **Attack:** `exhausted_mode = CANCEL | WEAKEN`. An attack you cannot afford is cancelled, or lands with less damage and attack speed (`weaken_damage`, `weaken_speed`).
- 2026-05-17: **Wings:** only wings in the `actionsofstamina:stamina_wings` item tag cost stamina (the elytra by default), in the chest slot or a Curios slot. Mechanical or powered wings from other mods fly free unless a modpack tags them. Out of stamina, the wings fold and do not open. A rocket boost is free unless `rocket_boost_costs` is set.
- 2026-05-17: **Shields:** a raised shield drains stamina, shields from other mods included.
- 2026-05-17: **Bows, crossbows and tridents (draw):** drawing drains stamina. Out of stamina, the draw drops without a shot.
- 2026-05-17: **Throwables (throw):** snowballs, eggs, ender pearls, throwable potions and tridents cost stamina to throw (item tag `actionsofstamina:throwables`).
- 2026-05-17: **Mining and building:** to break and place blocks costs stamina, off by default. Mining can scale with block hardness, and can get slower in place of a stop when you are out of stamina (`block_when_exhausted`).
- 2026-05-17: **Paragliders:** Stamina Vessels add maximum stamina (`feathers_per_vessel`).
- 2026-05-17: **Better Combat:** a multiplier per weapon category (`category_multipliers`): daggers cost less, claymores and hammers cost more.
- 2026-05-17: **Create:** a hand crank or a valve handle drains stamina while you turn it.
- 2026-05-17: **Curios:** wings in a Curios slot count.
- 2026-05-08: **ParCool:** ParCool reads its stamina from Actions of Stamina.
- 2026-05-08: **Wall-Jump TXF:** wall jumps and clinging cost stamina.
- 2026-05-08: **Gliders:** gliding drains stamina.
- 2026-05-01: Actions spend the stamina of Feathers of Fatigue when it is installed. Without it, they spend the own stamina bar of Actions of Stamina.
- 2026-05-01: Each action has its own config section: cost per use, number of uses that each charge covers (`times_performed_to_exhaust`), drain per second, stamina needed to start, regeneration pause, and an on/off switch.
- 2026-05-01: Sprinting and swimming drain stamina while they last. Jumping and crawling cost stamina.
- 2026-05-01: **ParCool:** parkour actions cost stamina.
- 2026-05-01: **Paragliders:** paragliding costs stamina.
- 2026-05-01: **Better Combat:** swings cost stamina, with multipliers for two-handed weapons, off-hand swings and combo finishers.
- 2026-05-01: **Combat Roll:** each roll costs stamina.
- 2026-05-01: **Epic Fight:** skills cost stamina.
- 2026-05-01: Weapons that get their damage from the modifiers of other mods (such as modular weapons) count as weapons.

### Changed
- 2026-10-01: **Minecraft 26.1.2:** the build is now labeled 26.1.2-1.0.1. The jar, the Maven artifact (`com.ccr4ft3r:actions-of-stamina:26.1.2-1.0.1`) and the branch say 26.1.2 in place of 26.1. It has the same actions, config and Addon API as 26.1-1.0.0, and was tested with Feathers of Fatigue 26.1.2-1.0.1.
- 2026-10-01: The mod list spells the name Actions of Stamina (older versions said Actions Of Stamina). The mod id `actionsofstamina` does not change.
- 2026-09-23: **Minecraft 26.3, farming tools:** Minecraft 26.3 changes blocks with tools through the block transformer of the item (the `minecraft:block_transformer` component), not through item abilities. Any item whose block transformer changes the clicked block counts, data map transformers of other mods too. The vanilla hoe, shovel and axe count as before.
- 2026-09-19: **Minecraft 26.1, shields:** a modded shield counts when it blocks attacks (the `minecraft:blocks_attacks` component), as Minecraft 26.1 defines a shield.
- 2026-09-19: **Minecraft 26.1, draw:** counts the bow, crossbow and trident use animations. The spear animation is not a draw.
- 2026-09-19: **Minecraft 26.1, rowing:** the `actionsofstamina:rowed_boats` tag holds `#minecraft:boat` and all chest boats and chest rafts, because each wood type is its own boat in Minecraft 26.1.
- 2026-09-19: **Minecraft 26.1, throw:** the `actionsofstamina:throwables` tag holds `#minecraft:eggs`, so blue and brown eggs cost like white ones.
- 2026-09-19: **Minecraft 26.1, Riptide and trident throws:** they follow the rules of the trident. There is no launch while you ride, and no throw or launch from a trident that is about to break.
- 2026-09-19: **Minecraft 26.1, Addon API:** the same API, with the Minecraft renames: `ResourceLocation` is now `Identifier` in all signatures. The Maven artifact is `com.ccr4ft3r:actions-of-stamina:26.1-1.0.0`.
- 2026-09-10: The optional dependency is now Feathers of Fatigue, the new name of Green Feathers. The mod id is `feathers_of_fatigue` (it was `greenfeathers`) and the API package is `com.darkona.feathersoffatigue.api`. The `backend` option and its `FEATHERS` value, which names the mechanic, do not change. Packs and servers must install Feathers of Fatigue in place of Green Feathers.
- 2026-07-29: **Addon API:** each action is a type registered in `ActionTypes`: the Minecraft actions, the actions of each supported mod, and the actions of other mods. The actions of other mods only need an `ActionCostConfig` section and, for effects, an `Action` subclass. The same code gates, drains and charges all of them.
- 2026-07-29: Fake players (machines that act as players) never pay stamina, for the actions of the supported mods too.
- 2026-06-29: Sprinting and swimming do not start without the stamina to begin, as sprinting does not start without food. They stop when the stamina runs out.
- 2026-06-29: **Attack:** with `only_for_hits = false`, swings at the air count towards `times_performed_to_exhaust` like hits, in the one count that the server keeps.
- 2026-05-10: The config is a server config, and the server syncs it to clients.
- 2026-05-10: **Attack:** the server charges attacks, so a client cannot skip the payment.

### Removed
- 2026-09-23: **Minecraft 26.3:** no Paragliders compat, because Paragliders has no build for Minecraft 26.3. Its compat and `[paragliders]` section wait in `src/disabled`. ParCool, Epic Fight, Gliders and Create have no build for Minecraft 26.3 either.
- 2026-09-20: **Minecraft 26.2:** no ParCool, Epic Fight, Gliders or Create compat, because none of these mods has a build for Minecraft 26.2. Their compats and config sections wait in `src/disabled`.
- 2026-09-19: **Minecraft 26.1:** no ParCool, Epic Fight, Gliders or Create compat, because none of these mods has a build for Minecraft 26.1. Their compats and config sections wait in `src/disabled`.

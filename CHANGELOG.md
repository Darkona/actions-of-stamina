# Changelog

Changes by feature, newest version first.

## 26.2-1.0.1 (NeoForge), 2026-10-01

Same as 26.2-1.0.0 below, except for what this section lists.

### Feathers of Fatigue

- AoS turns off the basic exertion of Feathers of Fatigue (its own sprint and jump costs) through the Feathers of Fatigue API, so a player never pays twice for the same action. The result is the same as before.
- Needs Feathers of Fatigue 26.2-1.0.1 or later, the first with that API (the accepted range is now `[26.2-1.0.1,26.2-2)`). Tested with Feathers of Fatigue 26.2-1.0.1.

## 26.2-1.0.0 (NeoForge), 2026-10-01

Actions of Stamina for Minecraft 26.2, on NeoForge 26.2.0.88 or later. Same actions, config and Addon API as 26.1-1.0.0 below.

### Compatibility

- Tested with Paragliders 26.2.1, Better Combat 3.2.2, Combat Roll 3.0.1, Wall-Jump TXF 26.2-1.3.8, Curios 16.0.0 and Feathers of Fatigue 26.2-1.0.0.
- Paragliders 26.2.1 can't read its own loot modifiers on Minecraft 26.2 (it logs an error at load), so elder guardians drop no Spirit Orbs and the wither no Stamina Vessel. Vessels from goddess statues still add max stamina.

### Not in this version

- **ParCool, Epic Fight, Gliders, Create:** none of them has a build for Minecraft 26.2. Their compats and config sections are left out (the code waits in `src/disabled`).

## 26.1.2-1.0.1 (NeoForge), 2026-10-01

The Minecraft 26.1.2 build is now labeled 26.1.2: the jar, the Maven artifact (`com.ccr4ft3r:actions-of-stamina:26.1.2-1.0.1`) and the branch say 26.1.2 instead of 26.1. Same actions, config and Addon API as 26.1-1.0.0. Tested with Feathers of Fatigue 26.1.2-1.0.1.

## 26.1-1.0.0 (NeoForge), 2026-10-01

Actions of Stamina for Minecraft 26.1.2, on NeoForge 26.1.2.112 or later. Same actions, config and Addon API as 1.21.1-1.0.0 below, except for what this section lists.

### Vanilla actions

- **Spears:** a spear's stab is an attack. It is charged once per stab, however many creatures it pierces, and counts towards `times_performed_to_exhaust` with the other attacks; a stab that reaches nothing is a swing at the air (`only_for_hits`). Without the stamina, a stab hits nothing, or lands weakened with `exhausted_mode = WEAKEN`. Holding a spear ready to charge is free.
- **Shields:** a modded shield counts when it blocks attacks (the `minecraft:blocks_attacks` component), the way Minecraft 26.1 defines a shield.
- **Draw:** counts the bow, crossbow and trident use animations; the spear's is not a draw.
- **Rowing:** the `actionsofstamina:rowed_boats` tag holds `#minecraft:boat` and every chest boat and chest raft, since each wood type is its own boat in Minecraft 26.1.
- **Throw:** the `actionsofstamina:throwables` tag holds `#minecraft:eggs`, so blue and brown eggs cost like white ones.
- **Riptide and trident throws:** follow the trident's own rules: no launch while riding, and no throw or launch from a trident about to break.

### Compatibility

- Tested with Paragliders 26.1.2, Better Combat 3.2.2, Combat Roll 3.0.1, Wall-Jump TXF 26.1.2-1.3.8, Curios 15.0.0 and Feathers of Fatigue 26.1-1.0.0.
- Each compat accepts its mod from the version tested up to the next major version; a mod outside that range stops the game at load with a message.

### For mod developers

- The Addon API is the same, with Minecraft's renames: `ResourceLocation` is now `Identifier` in every signature. The Maven artifact is `com.ccr4ft3r:actions-of-stamina:26.1-1.0.0`.

### Not in this version

- **ParCool, Epic Fight, Gliders, Create:** none of them has a build for Minecraft 26.1. Their compats and config sections are left out (the code waits in `src/disabled`).

## 1.21.1-1.0.0 (NeoForge), 2026-10-01

The mod list spells the name Actions of Stamina (older versions said Actions Of Stamina). The mod id `actionsofstamina` is unchanged.

### Stamina backends

- Actions spend Feathers of Fatigue when it is installed, or Actions of Stamina's own stamina bar without it.
- The optional dependency is now Feathers of Fatigue, the new name of Green Feathers: the mod id is `feathers_of_fatigue` (it was `greenfeathers`) and the API package is `com.darkona.feathersoffatigue.api`. The `backend` option and its `FEATHERS` value, which names the mechanic, are unchanged. Packs and servers must install Feathers of Fatigue in place of Green Feathers.
- Every action has its own config section: cost per use, how many uses each charge covers (`times_performed_to_exhaust`), drain per second, stamina needed to start, regeneration pause, and an on/off switch. All in the server config, synced to clients.

### Vanilla actions

- **Attack:** charged on the server, so a client can't skip paying. `exhausted_mode = CANCEL | WEAKEN`: an attack you can't afford is cancelled, or lands with less damage and attack speed (`weaken_damage`, `weaken_speed`). With `only_for_hits = false`, swings at the air count towards `times_performed_to_exhaust` like hits, in the one count the server keeps. Girl mode (`weaken_non_weapons`, on by default): WEAKEN also weakens bare-handed and other non-weapon attacks while the stamina is short, even when they are free.
- **Wings:** only wings in the `actionsofstamina:stamina_wings` item tag cost stamina (the elytra by default), worn in the chest or a Curios slot. Mechanical or powered wings from other mods fly free unless a modpack tags them. Out of stamina, the wings fold and won't open. A rocket boost is free unless `rocket_boost_costs`.
- **Shields:** raising a shield drains stamina, including shields from other mods.
- **Bows, crossbows and tridents (draw):** drawing drains stamina; out of stamina the draw drops without a shot.
- **Throwables (throw):** snowballs, eggs, ender pearls, throwable potions, wind charges and tridents cost stamina to throw (item tag `actionsofstamina:throwables`).
- **Mining and building:** breaking and placing blocks cost stamina, off by default. Mining can scale with block hardness, and can get slower instead of stopping when you're out (`block_when_exhausted`).
- **Climbing** ladders, vines, scaffolding and anything else climbable, off by default. Out of stamina you can't go up: you hold on or slide down slowly.
- **Rowing:** only boats in the `actionsofstamina:rowed_boats` entity tag cost stamina (vanilla boats, chest boats and rafts), so sail or motor boats from other mods don't. Off by default. Out of stamina the paddles stop and the boat drifts.
- **Riptide:** a trident launch costs stamina when released, off by default. A launch you can't afford doesn't happen.
- **Fishing:** casting a rod and reeling it in each cost stamina, off by default. Modded rods count (by the rod's cast ability). A cast or reel you can't afford doesn't happen.
- **Farming tools:** tilling, making paths, stripping logs and scraping or unwaxing copper cost stamina once every few blocks, off by default. Modded tools count. A block you can't afford stays as it is.
- **Brushing:** brushing drains stamina, off by default. Out of stamina, brushing stops.
- **Mace smash:** the falling mace attack is charged on its own, at the attack cost times `mace_smash_multiplier` (2 by default).
- **Sprinting and swimming:** drain while they last. Without the stamina to begin they don't start, as sprinting doesn't without food, and they stop when the stamina runs out.
- Jumping and crawling.

### Compatibility

- **ParCool:** parkour actions cost stamina, and ParCool reads its stamina from Actions of Stamina.
- **Paragliders:** paragliding costs stamina. Stamina Vessels add maximum stamina (`feathers_per_vessel`).
- **Better Combat:** swings cost stamina, with multipliers for two-handed, off-hand and combo finishers, and per weapon category (`category_multipliers`: daggers cheaper, claymores and hammers dearer).
- **Combat Roll:** each roll costs stamina.
- **Epic Fight:** skills cost stamina.
- **Wall-Jump TXF:** wall jumps and clinging cost stamina.
- **Gliders:** gliding drains stamina.
- **Create:** turning a hand crank or a valve handle drains stamina.
- **Curios:** wings in a Curios slot count.
- Weapons whose damage comes from other mods' modifiers (such as modular weapons) count as weapons.

### Addon API

- Every action is a type registered in `ActionTypes`: Minecraft's, each supported mod's, and other mods' own, which only need an `ActionCostConfig` section and, for effects, an `Action` subclass. They are gated, drained and charged by the same code, and a continuous action can charge a `finish_cost` when it ends. See the wiki's Addon API page.
- Fake players (machines acting as players) never pay stamina, for the supported mods' actions too.

## Planned

### Compatibility

- Paragliders on Minecraft 26.3, and ParCool, Epic Fight, Gliders and Create on Minecraft 26.x, once they publish a build for it.

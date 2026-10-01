# Changelog

Changes by feature, newest version first.

## 1.20.1-1.0.1 (Forge 47), 2026-10-01

Same as 1.20.1-1.0.0 below, except for what this section lists.

### Feathers of Fatigue

- AoS turns off the basic exertion of Feathers of Fatigue (its own sprint and jump costs) through the Feathers of Fatigue API, so a player never pays twice for the same action. The result is the same as before.
- Needs Feathers of Fatigue 1.20.1-1.0.1 or later, the first with that API (the accepted range is now `[1.20.1-1.0.1,1.20.1-2)`). Works with Feathers of Fatigue 1.20.1-1.0.1.

## 1.20.1-1.0.0 (Forge 47), 2026-10-01

The 1.21.1 version of Actions of Stamina, ported to Forge 1.20.1 with the same actions, config options and stamina backends. It replaces the earlier 0.6.0 code of this branch, which needed Green Feathers 1.3.0. Needs Forge 47.4.23 or later. The mod list spells the name Actions of Stamina (older versions said Actions Of Stamina). The mod id `actionsofstamina` is unchanged.

### Stamina backends

- Actions spend Feathers of Fatigue (1.20.1-1.0.0 or later) when it is installed, or Actions of Stamina's own stamina bar without it.
- The optional dependency is now Feathers of Fatigue, the new name of Green Feathers: the mod id is `feathers_of_fatigue` (it was `greenfeathers`) and the API package is `com.darkona.feathersoffatigue.api`. The `backend` option and its `FEATHERS` value, which names the mechanic, are unchanged. Packs and servers must install Feathers of Fatigue in place of Green Feathers.
- Every action has its own config section: cost per use, how many uses each charge covers (`times_performed_to_exhaust`), drain per second, stamina needed to start, regeneration pause, and an on/off switch. All in the server config, synced to clients. Option names are the same as in 1.21.1.

### Vanilla actions

- **Attack:** charged on the server, so a client can't skip paying. `exhausted_mode = CANCEL | WEAKEN`: an attack you can't afford is cancelled, or lands with less damage and attack speed (`weaken_damage`, `weaken_speed`). With `only_for_hits = false`, swings at the air count towards `times_performed_to_exhaust` like hits, in the one count the server keeps. Girl mode (`weaken_non_weapons`, on by default): WEAKEN also weakens bare-handed and other non-weapon attacks while the stamina is short, even when they are free.
- **Wings:** only wings in the `actionsofstamina:stamina_wings` item tag cost stamina (the elytra by default), worn in the chest or a Curios slot. Mechanical or powered wings from other mods fly free unless a modpack tags them. Out of stamina, the wings fold and won't open. A rocket boost is free unless `rocket_boost_costs`.
- **Shields:** raising a shield drains stamina, including shields from other mods (found by Forge's shield-block tool action).
- **Bows, crossbows and tridents (draw):** drawing drains stamina; out of stamina the draw drops without a shot.
- **Throwables (throw):** snowballs, eggs, ender pearls, throwable potions and tridents cost stamina to throw (item tag `actionsofstamina:throwables`).
- **Mining and building:** breaking and placing blocks cost stamina, off by default. Mining can scale with block hardness, and can get slower instead of stopping when you're out (`block_when_exhausted`).
- **Climbing** ladders, vines, scaffolding and anything else climbable, off by default. Out of stamina you can't go up: you hold on or slide down slowly.
- **Rowing:** only boats in the `actionsofstamina:rowed_boats` entity tag cost stamina (vanilla boats, chest boats and rafts), so sail or motor boats from other mods don't. Off by default. Out of stamina the paddles stop and the boat drifts.
- **Riptide:** a trident launch costs stamina when released, off by default. A launch you can't afford doesn't happen.
- **Fishing:** casting a rod and reeling it in each cost stamina, off by default. Modded rods count (by the rod's cast tool action). A cast or reel you can't afford doesn't happen.
- **Farming tools:** tilling, making paths, stripping logs and scraping or unwaxing copper cost stamina once every few blocks, off by default. Modded tools count. A block you can't afford stays as it is.
- **Brushing:** brushing drains stamina, off by default. Out of stamina, brushing stops.
- **Sprinting and swimming:** drain while they last. Without the stamina to begin they don't start, as sprinting doesn't without food, and they stop when the stamina runs out.
- Jumping and crawling.

### Compatibility

Each one only does something when its mod is installed, and can be turned off in the config. Works with these 1.20.1 Forge builds:

- **ParCool 4.0.0.5:** parkour actions cost stamina, and ParCool reads its stamina from Actions of Stamina. This ParCool build needs Forge 47.4.23 or later.
- **Paragliders 20.1.3:** paragliding costs stamina. Stamina Vessels add maximum stamina (`feathers_per_vessel`).
- **Better Combat 1.9.0:** swings cost stamina, with multipliers for two-handed, off-hand and combo finishers, and per weapon category (`category_multipliers`: daggers cheaper, claymores and hammers dearer).
- **Combat Roll 1.3.3:** each roll costs stamina.
- **Epic Fight 20.14.17:** skills (dodge, guard, weapon innate, mover) and the basic attack combo in battle mode cost stamina.
- **Wall-Jump TXF 1.3.8:** wall jumps, double jumps and clinging cost stamina.
- **Gliders 1.2.0:** gliding drains stamina.
- **Create 6.0.8:** turning a hand crank or a valve handle drains stamina.
- **Curios 5.14.1:** wings in a Curios slot count.
- Weapons whose damage comes from other mods' modifiers (such as modular weapons) count as weapons.
- Known issue, not in Actions of Stamina: ParCool 4.0.0.5, Curios 5.14.1 and Epic Fight 20.14.17 installed together crash the game when it first reads the attributes of ParCool's Traceur Gloves (on joining a world, for the creative search). Any two of the three work.

### Not in this version

- **Mace smash:** Minecraft 1.20.1 has no mace, so there is no `[vanilla.attack] mace_smash_multiplier` option.
- **Wind charges:** Minecraft 1.20.1 has no wind charge, so it isn't in the `actionsofstamina:throwables` tag.

### Addon API

- Every action is a type registered in `ActionTypes`: Minecraft's, each supported mod's, and other mods' own, which only need an `ActionCostConfig` section and, for effects, an `Action` subclass. They are gated, drained and charged by the same code, and a continuous action can charge a `finish_cost` when it ends. See the wiki's Addon API page.
- Fake players (machines acting as players) never pay stamina, for the supported mods' actions too.

## Planned

### Compatibility

- Paragliders on Minecraft 26.3, and ParCool, Epic Fight, Gliders and Create on Minecraft 26.x, once they publish a build for it.

# Changelog

Changes by feature, newest version first.

## 0.6.0 (Minecraft 1.21.1, NeoForge), unreleased

### Stamina backends

- Actions spend Green Feathers when it is installed, or Actions of Stamina's own stamina bar without it.
- Every action has its own config section: cost per use, drain per second, stamina needed to start, regeneration pause, and an on/off switch. All in the server config, synced to clients.

### Vanilla actions

- **Attack:** charged on the server, so a client can't skip paying. `exhausted_mode = CANCEL | WEAKEN`: an attack you can't afford is cancelled, or lands with less damage and attack speed (`weaken_damage`, `weaken_speed`). With `only_for_hits = false`, swings at the air count towards `times_to_charge` like hits, in the one count the server keeps. Girl mode (`weaken_non_weapons`, on by default): WEAKEN also weakens bare-handed and other non-weapon attacks while the stamina is short, even when they are free.
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

- Other mods can add their own actions: an `ActionCostConfig` section, an `Action` subclass and a type registered in `ActionTypes`. Addon actions are gated, drained and charged like the built-in ones, and a continuous action can charge a `finish_cost` when it ends. See the wiki's Addon API page.

## Planned

### Ports

- Ports to Minecraft 1.20.1 (Forge 47), 1.19.2 (Forge 43) and 1.18.2 (Forge 40), on Green Feathers or the own stamina bar, each with the latest stable versions of the supported mods.
- The wiki gets a section per Minecraft version where the versions differ.

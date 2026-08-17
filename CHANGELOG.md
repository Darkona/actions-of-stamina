# Changelog

Changes by feature, newest version first.

## 0.6.0 (Minecraft 1.18.2, Forge 40), unreleased

The 1.21.1 version of Actions of Stamina, ported to Forge 1.18.2 (through the 1.20.1 and 1.19.2 ports) with the same actions, config options and stamina backends. It replaces the 0.5.0 code of this branch, which needed Elenai's Feathers: Elenai's Feathers is no longer supported. Needs Forge 40.2.3 or later.

### Stamina backends

- Actions spend Green Feathers (2.0.0 for 1.18.2) when it is installed, or Actions of Stamina's own stamina bar without it.
- Every action has its own config section: cost per use, drain per second, stamina needed to start, regeneration pause, and an on/off switch. All in the server config, synced to clients. Option names are the same as in 1.21.1.

### Vanilla actions

- **Attack:** charged on the server, so a client can't skip paying. `exhausted_mode = CANCEL | WEAKEN`: an attack you can't afford is cancelled, or lands with less damage and attack speed (`weaken_damage`, `weaken_speed`). With `only_for_hits = false`, swings at the air count towards `times_to_charge` like hits, in the one count the server keeps.
- **Wings:** only wings in the `actionsofstamina:stamina_wings` item tag cost stamina (the elytra by default), worn in the chest or a Curios slot. Mechanical or powered wings from other mods fly free unless a modpack tags them. Out of stamina, the wings fold and won't open. A rocket boost is free unless `rocket_boost_costs`.
- **Shields:** raising a shield drains stamina, including shields from other mods (found by Forge's shield-block tool action).
- **Bows, crossbows and tridents (draw):** drawing drains stamina; out of stamina the draw drops without a shot.
- **Throwables (throw):** snowballs, eggs, ender pearls, throwable potions and tridents cost stamina to throw (item tag `actionsofstamina:throwables`).
- **Mining and building:** breaking and placing blocks cost stamina, off by default. Mining can scale with block hardness, and can get slower instead of stopping when you're out (`block_when_exhausted`).
- **Climbing** ladders, vines, scaffolding and anything else climbable, off by default. Out of stamina you can't go up: you hold on or slide down slowly.
- **Rowing:** only boats in the `actionsofstamina:rowed_boats` entity tag cost stamina (the vanilla boat), so sail or motor boats from other mods don't. Off by default. Out of stamina the paddles stop and the boat drifts.
- **Riptide:** a trident launch costs stamina when released, off by default. A launch you can't afford doesn't happen.
- **Fishing:** casting a rod and reeling it in each cost stamina, off by default. Modded rods count when they are built on the vanilla fishing rod. A cast or reel you can't afford doesn't happen.
- **Farming tools:** tilling, making paths, stripping logs and scraping or unwaxing copper cost stamina once every few blocks, off by default. Modded tools count. A block you can't afford stays as it is.
- **Sprinting and swimming:** drain while they last. Without the stamina to begin they don't start, as sprinting doesn't without food, and they stop when the stamina runs out.
- Jumping and crawling.

### Compatibility

Each one only does something when its mod is installed, and can be turned off in the config. Tested with these 1.18.2 Forge builds:

- **ParCool 3.4.3.3:** parkour actions cost stamina (`[parcool]`, one section per action). ParCool's own stamina (its client option `used_stamina = Default`) shows AoS's stamina and charges nothing, and its stamina HUD is hidden, so each action is charged once. ParCool 3 has no grapple or castaway, so there are no sections for them; it has a jump from a bar (`jump_from_bar`). Defaults are ParCool 3's own costs. A breakfall is charged when it lands (as a roll or a tap), and can't be readied without its cost.
- **Paragliders 1.6.0.6:** paragliding costs stamina, and Paragliders reads its stamina from Actions of Stamina (its stamina wheel is hidden). Stamina Vessels add maximum stamina (`feathers_per_vessel`).
- **Better Combat 1.6.2:** swings cost stamina, with multipliers for two-handed, off-hand and combo finishers, and per weapon category (`category_multipliers`: daggers cheaper, claymores and hammers dearer).
- **Combat Roll 1.1.5:** each roll costs stamina.
- **Epic Fight 18.5.26:** skills (dodge, weapon innate, mover) and the basic attack combo in battle mode cost stamina.
- **Wall-Jump TXF 1.3.8:** wall jumps, double jumps and clinging cost stamina.
- **Create 0.5.1.i:** turning a hand crank or a valve handle drains stamina.
- **Curios 5.0.9.2:** wings in a Curios slot count.
- Weapons whose damage comes from other mods' modifiers (such as modular weapons) count as weapons.
- ParCool, Curios and Epic Fight work together on 1.18.2 (on 1.20.1 the three together crash the game).

### Not in this version

- **Brushing:** Minecraft 1.18.2 has no brush (archaeology came in 1.20), so there is no `[vanilla.brush]` section.
- **Mace smash:** Minecraft 1.18.2 has no mace, so there is no `[vanilla.attack] mace_smash_multiplier` option.
- **Wind charges:** Minecraft 1.18.2 has no wind charge, so it isn't in the `actionsofstamina:throwables` tag.
- **Chest boats and bamboo rafts:** Minecraft 1.18.2 has neither (chest boats came in 1.19), so the `actionsofstamina:rowed_boats` tag holds the boat only.
- **Gliders:** Gliders has no 1.18.2 build, so there is no `[gliders]` section.
- **ParCool stamina type:** ParCool 3 has no stamina type registry or server `stamina_type` option (ParCool 4 has both). AoS's stamina stands in for ParCool's own through a mixin instead; a player who picks ParCool's Hunger stamina keeps it, and then ParCool also charges food.
- **ParCool grapple and castaway:** ParCool 3 doesn't have them.
- **Better Combat `lance`:** Better Combat 1.6 has no lance category; the other 27 categories are the same.
- **Fishing rods by tool action:** Forge 40 has no rod-cast tool action, so a modded rod counts only when it is built on the vanilla fishing rod.
- **Epic Fight guard:** Epic Fight 18 takes a blocked hit's stamina straight from its own bar, without a skill event, so guarding keeps Epic Fight's stamina and `[epicfight.guard]` does nothing.
- **Paragliders stamina plugin:** Paragliders 1.6 has no stamina API. Actions of Stamina changes its player movement instead (mixins), with the same result: Paragliders reads its stamina from Actions of Stamina, and neither regenerates nor drains its own.

## Planned

### Ports

- The wiki gets a section per Minecraft version where the versions differ.

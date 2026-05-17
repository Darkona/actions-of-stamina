# Changelog

Changes by feature, newest version first.

## 0.6.0 (Minecraft 1.21.1, NeoForge), unreleased

### Stamina backends

- Actions spend Green Feathers when it is installed, or Actions of Stamina's own stamina bar without it.
- Every action has its own config section: cost per use, drain per second, stamina needed to start, regeneration pause, and an on/off switch. All in the server config, synced to clients.

### Vanilla actions

- **Attack:** charged on the server, so a client can't skip paying. `exhausted_mode = CANCEL | WEAKEN`: an attack you can't afford is cancelled, or lands with less damage and attack speed (`weaken_damage`, `weaken_speed`).
- **Wings:** only wings in the `actionsofstamina:stamina_wings` item tag cost stamina (the elytra by default), worn in the chest or a Curios slot. Mechanical or powered wings from other mods fly free unless a modpack tags them. Out of stamina, the wings fold and won't open. A rocket boost is free unless `rocket_boost_costs`.
- **Shields:** raising a shield drains stamina, including shields from other mods.
- **Bows, crossbows and tridents (draw):** drawing drains stamina; out of stamina the draw drops without a shot.
- **Throwables (throw):** snowballs, eggs, ender pearls, throwable potions and tridents cost stamina to throw (item tag `actionsofstamina:throwables`).
- **Mining and building:** breaking and placing blocks cost stamina, off by default. Mining can scale with block hardness, and can get slower instead of stopping when you're out (`block_when_exhausted`).
- Sprinting, jumping, crawling and swimming.

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

## Planned

### Before the ports

- **Climbing** ladders, vines and scaffolding (off by default).
- **Rowing** boats: only boats in an entity tag cost stamina (vanilla boats and rafts), so sail or motor boats from other mods don't (off by default).
- **Riptide** trident launches (off by default).
- **Mace smash:** the falling mace attack costs more.
- **Wind charges** count as throwables.
- **Fishing:** casting and reeling (off by default).
- **Farming tools:** tilling, making paths and stripping logs (off by default).
- **Brushing** in archaeology (off by default).

### Ports

- Ports to Minecraft 1.20.1 (Forge 47), 1.19.2 (Forge 43) and 1.18.2 (Forge 40), on Green Feathers or the own stamina bar, each with the latest stable versions of the supported mods.
- The wiki gets a section per Minecraft version where the versions differ.

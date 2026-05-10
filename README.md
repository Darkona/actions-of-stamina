# Actions of Stamina

*Minecraft 1.21.1 · NeoForge 21.1*

Actions of Stamina (AoS) makes player actions cost stamina. Sprinting, jumping, attacking, swimming, crawling,
flying with an elytra and raising a shield all cost stamina, and so do the actions of several popular movement
and combat mods. If you don't have enough stamina, the action doesn't happen: you can't start sprinting, a jump
is cancelled, a swing is dropped, a roll isn't available.

AoS only decides **how actions cost stamina**. The stamina bar comes from
[Green Feathers](https://github.com/Darkona/Green-Feathers) if it is installed, or from AoS's own simple bar if it
isn't.

## Stamina backends

| Backend | When | What you get |
|---|---|---|
| **Green Feathers** | Green Feathers 2.0+ is installed (and `backend` is `AUTO` or `FEATHERS`) | Green Feathers' feathers, regeneration, strain, exhaustion, effects, armor weight, climate and HUD. AoS spends under its own sources (`actionsofstamina:sprint`, `actionsofstamina:parcool/dodge`, ...). |
| **Internal** | Green Feathers isn't installed, or `backend = INTERNAL` | A small, light bar: a maximum, regeneration after a short delay, and exhaustion (once you run out, you must regain part of the bar before you can act again). It is drawn as a thin bar above the food bar. |

The server picks the backend when it starts and tells each client which one it uses. Green Feathers is an
**optional** dependency: AoS works without it.

Creative and spectator players never spend stamina.

## Vanilla actions

| Action | How it costs | Default |
|---|---|---|
| Sprinting | Drain while sprinting; blocks regeneration | 0.25 feathers/s, 2 needed to start |
| Swimming (fast swim) | Drain while swimming | 0.5 feathers/s |
| Elytra flying | Drain while gliding | 0.05 feathers/s |
| Crawling | Drain while moving in the crawl pose on land; you move slower when you can't pay | 0.1 feathers/s |
| Holding up a shield | Cost to raise it, then a drain | 1 feather, then 0.2/s |
| Jumping | Charged once every few jumps | 1 feather every 4 jumps |
| Attacking | Charged once every few attacks | 1 feather every 3 attacks |

Attacks with non-weapons (bare hands, tools without attack damage) are free unless `also_for_non_weapons = true`.
By default only attacks that hit an entity are charged (`only_for_hits`).

## Mod compatibility

Every compatibility is optional and only active when that mod is installed. Each has its own section in the
config, with an `enabled` switch.

### ParCool (4.x)

All 24 ParCool actions (fast run, wall run, vault, dodge, hang on, climb up, slide, dive, breakfall, charge
jump, ...) cost stamina. Each action has its own start cost, per-second drain, finish cost and regeneration
delay. The defaults are ParCool's own costs, scaled to a 20-feather bar.

AoS charges on the server when ParCool starts, ticks and finishes an action. It stops an action from starting or
continuing when the stamina is short. While ParCool's fast run, fast swim or crawl is active, AoS's own sprint,
swim and crawl costs stay out, so they aren't charged twice.

AoS registers its own ParCool stamina type, `actionsofstamina:stamina`. It shows ParCool the AoS stamina (so
ParCool knows when you are exhausted), ignores ParCool's own costs and regeneration (AoS charges the actions, so
nothing is charged twice), and hides ParCool's stamina HUD. There is nothing to set up: it is the default
`stamina_type` of a new ParCool server config, and while `parcool.enabled = true` it also replaces ParCool's own
`parcool:parcool`, which existing configs hold. `parcool:none` works too. Any other type (`parcool:hunger`, Epic
Fight's) charges the actions a second time, and AoS logs a warning at server start. With `parcool.enabled = false`,
AoS's stamina type behaves exactly like ParCool's own.

### Paragliders (21.1.x)

Paragliding drains AoS stamina, and Paragliders reads its stamina from AoS. Its stamina wheel is hidden, and its
own stamina logic is turned off. Because of that, Paragliders never charges running or swimming. AoS's sprint and
swim actions charge them instead. With `paragliders.enabled = false`, Paragliders keeps its own stamina wheel.

### Better Combat (2.4.x)

Every Better Combat weapon swing costs stamina. The server charges each swing when Better Combat's attack request
arrives, and drops a swing it can't pay for. The client cancels a swing you can't afford as soon as its upswing
starts. You can tune the cost with multipliers for two-handed weapons, off-hand swings and the last swing of a
combo. Weapons that Better Combat swings use this cost instead of the vanilla attack cost.

### Combat Roll (2.0.x)

Each roll costs stamina. A roll isn't available on the client while you can't pay for it.

### Epic Fight (21.17.x)

Epic Fight skills in the dodge, guard, weapon innate and mover categories spend AoS stamina instead of Epic
Fight stamina. Guards are charged once per blocked hit. A skill you can't pay for fails, the same as it would
without Epic Fight stamina. Skills in other categories still use Epic Fight's own stamina.

In Epic Fight's battle mode, each swing of its basic attack combo costs stamina, and the vanilla attack cost stands
aside so a swing is only charged once. Swings you can't pay for don't happen.

### Wall-Jump TXF (1.21.1-1.3.x)

Wall jumps and double jumps cost stamina, and clinging to a wall (and sliding down it afterwards) drains it. A jump
you can't pay for doesn't happen, you can't grab a wall without the stamina to begin, and you let go of the wall
when you can't pay any more. Wall-Jump TXF decides these moves on the client, so the client refuses them and the
server charges them.

### Gliders (1.1.x)

Gliding with a deployed glider drains stamina, since you hold on to it. You can't deploy a glider without the stamina
to begin, and the glider folds when the stamina runs out.

## Configuration

`config/actionsofstamina-server.toml`, a server config: a server sends its own to every player who joins, so every
client charges the same costs (modpacks set their defaults in `defaultconfigs/`). Costs are in feathers, and a
feather is half a HUD icon. The defaults are tuned for a 20-feather bar. Delays are in ticks (20 ticks = 1 second).

```toml
[general]
backend = "AUTO"            # AUTO | FEATHERS | INTERNAL (read when the server starts)
debugging = false

[internal]                  # only used by the internal backend
enabled = true              # false: every action is free without Green Feathers
max_feathers = 20
regen_per_second = 0.5
regen_delay = 30            # minimum pause after any spend
exhaustion_recovery = 0.3   # share of the bar to regain after running out

[vanilla.sprint]            # likewise attack, jump, swim, elytra, crawl, shield
enabled = true
cost = 0.0                  # one-off cost (per use, or to start)
min_stamina = 2.0           # needed to begin
per_second = 0.25           # drain while it lasts
regen_delay = 40            # pause after it ends
blocks_regen = true         # no regeneration while it lasts
# attack and jump also have: times_to_charge; attack also: also_for_non_weapons, only_for_hits

[parcool]
enabled = true
  [parcool.fast_run]        # one subsection per ParCool action
  enabled = true
  cost = 0.0
  per_second = 0.4
  finish_cost = 0.0
  regen_delay = 20
  blocks_regen = true

[paragliders]
enabled = true
cost = 0.0
min_stamina = 1.0
per_second = 0.1
regen_delay = 20
blocks_regen = true

[bettercombat]
enabled = true
cost = 0.4                  # per swing
regen_delay = 40
two_handed_multiplier = 1.5
off_hand_multiplier = 0.75
combo_finisher_multiplier = 1.25
block_when_short = true

[combat_roll]
enabled = true
cost = 1.5
regen_delay = 30

[epicfight]
enabled = true
  [epicfight.dodge]         # likewise guard (1.0), innate (3.0), mover (2.0), basic_attack (1.0)
  enabled = true
  cost = 2.0
  regen_delay = 30

[walljump]
enabled = true
  [walljump.wall_jump]      # likewise double_jump (1.5)
  enabled = true
  cost = 0.5
  regen_delay = 20
  [walljump.wall_cling]
  enabled = true
  cost = 0.0
  min_stamina = 0.5
  per_second = 0.4
  regen_delay = 20
  blocks_regen = true

[gliders]
enabled = true
cost = 0.0                  # to deploy
min_stamina = 1.0
per_second = 0.1
regen_delay = 20
blocks_regen = true
```

The actions are rebuilt from the config whenever a player joins a level, so most edits apply on the next
respawn or dimension change. Changing `backend` needs a server restart.

`config/actionsofstamina-client.toml` controls the internal stamina bar: `hud.enabled`, `hud.x_offset` and
`hud.y_offset`.

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

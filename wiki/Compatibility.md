# Compatibility

Each compatibility is optional and works only when its mod is installed. Each one has its own section in the config, with an `enabled` switch.

## Better Combat

Each Better Combat weapon swing costs stamina. The server charges each swing when the attack request of Better Combat arrives, and drops a swing it cannot pay for. The client cancels a swing you cannot afford when its upswing starts. Multipliers tune the cost for two-handed weapons, off-hand swings and the last swing of a combo. Weapons that Better Combat swings use this cost in place of the vanilla attack cost.

The Better Combat category of the weapon also multiplies the cost of each swing (`category_multipliers`, entries like `"claymore=1.2"`). By default, daggers, fists, claws, sickles, rapiers and spears cost less. Axes, claymores, hammers, maces and anchors cost more. A category that is not in the list costs the base amount. The category multiplier stacks with the two-handed, off-hand and combo-finisher multipliers. The mod calculates it once per weapon type, and again when the config or the datapacks reload.

![A Better Combat swing](images/bettercombat.png)

## Combat Roll

Each roll costs stamina. While you cannot pay for a roll, the client does not make it available.

![Mid-roll](images/combatroll.png)

## Wall-Jump TXF

Wall jumps and double jumps cost stamina. To cling to a wall, and to slide down it after, drains stamina. A jump you cannot pay for does not happen. You cannot grab a wall without the stamina to begin, and you let go of the wall when you cannot pay any more. Wall-Jump TXF decides these moves on the client, so the client refuses them and the server charges them.

## Other mods' wings, shields and weapons

Wings from other mods cost stamina only when a datapack adds them to the item tag `actionsofstamina:stamina_wings`. The tag holds the vanilla elytra. Mechanical or propelled wings are free by default, because the tag is for wings that you flap or glide with yourself. Tagged wings cost when you wear them in the chest slot or, with Curios installed, in any curio slot (for mods that give the elytra its own slot).

Shields from other mods cost like the vanilla shield when they block like one. That is the `minecraft:blocks_attacks` component, which all items that block attacks have. They count in either hand.

Bows, crossbows and tridents from other mods cost like the vanilla ones when they use the same animation (a bow you draw, a crossbow you load, a trident you aim). Spears do not draw. To hold one ready is free, and its stabs cost like any attack. Throwables from other mods cost only when a datapack adds them to the item tag `actionsofstamina:throwables`.

With `vanilla.attack.also_for_non_weapons = false`, an item is a weapon when it adds attack damage in the main hand. The source of that damage does not matter: the attribute modifiers of the item, modifiers calculated for that stack, or modifiers that other mods add through the attribute modifier event of NeoForge. Modular weapons that build their damage from their parts (as the weapons of Tetra do) count as weapons.

## Curios

Stamina wings (the item tag `actionsofstamina:stamina_wings`) in a curio slot cost stamina like wings in the chest slot. The server sees when you put them on or take them off. The client checks once a second.

## Versions

Actions of Stamina calls these mods directly. It accepts the versions it was tested with, up to the next major version. With a version outside that range, the game stops at load and names the mod and the range, in place of a crash later in the middle of play.

| Mod | Accepted versions (26.3) |
|---|---|
| Better Combat | 3.2 up to 4 |
| Combat Roll | 3.0 up to 4 |
| Wall-Jump TXF | 26.3-1.3 up to 26.4 |
| Curios | 17.0.0-beta.2 up to 18 |
| Feathers of Fatigue | 26.3-1.0.0-beta.2 up to 26.3-2 |

## On older Minecraft versions

These mods have no build for Minecraft 26.3. Their compats exist only in Actions of Stamina for older versions: Paragliders on Minecraft 26.2 and older, ParCool on Minecraft 1.21.11 and older, and Epic Fight, Gliders and Create on Minecraft 1.21.1 and older. [Minecraft Versions](Minecraft-Versions) lists what each version supports and the versions it was tested with.

### Paragliders

Paragliding drains AoS stamina, and Paragliders reads its stamina from AoS. AoS hides the stamina wheel of Paragliders and turns off its stamina logic. Because of that, Paragliders never charges running or swimming. The sprint and swim actions of AoS charge them. With `paragliders.enabled = false`, Paragliders keeps its own stamina wheel.

Stamina Vessels (traded for Spirit Orbs at a goddess statue) make the AoS bar larger, as they make the wheel of Paragliders larger. Each vessel adds `feathers_per_vessel` max feathers (2 by default, 0 turns this off). With Feathers of Fatigue, this is a `max_feathers` attribute modifier, `actionsofstamina:paragliders/stamina_vessels`. With the internal stamina, the bar itself grows. AoS sets it when you join, respawn or change dimension, and when the number of vessels changes. With `paragliders.enabled = false`, the vessels count only for the own wheel of Paragliders.

![Paragliding: the drain shows in the feathers](images/paraglider.png)

### ParCool

All 24 ParCool actions cost stamina (fast run, wall run, vault, dodge, hang on, climb up, slide, dive, breakfall, charge jump, ...). Each action has its own start cost, drain per second, finish cost and regeneration delay. The defaults are the costs of ParCool, scaled to a 20-feather bar.

AoS charges on the server when ParCool starts, ticks and ends an action. It stops an action from starting or continuing when the stamina is short. While the fast run, fast swim or crawl of ParCool is active, the sprint, swim and crawl costs of AoS do not apply, so the mod does not charge them twice.

AoS registers its own ParCool stamina type, `actionsofstamina:stamina`. This type shows the AoS stamina to ParCool, so ParCool knows when you are exhausted. It ignores the costs and regeneration of ParCool, because AoS charges the actions and nothing is charged twice. It also hides the stamina HUD of ParCool. There is nothing to set up. It is the default `stamina_type` of a new ParCool server config. While `parcool.enabled = true`, it also replaces `parcool:parcool`, the type that existing configs hold. `parcool:none` works too. Any other type (`parcool:hunger`, the type of Epic Fight) charges the actions a second time, and AoS logs a warning at server start. With `parcool.enabled = false`, the stamina type of AoS works exactly like the type of ParCool.

![Sprinting with ParCool: fast run, charged in feathers](images/sprint-parcool.png)

![A ParCool jump mid-run](images/jump-parcool.png)

### Epic Fight

Epic Fight skills in the dodge, guard, weapon innate and mover categories spend AoS stamina in place of Epic Fight stamina. A guard costs once per blocked hit. A skill you cannot pay for fails, as it would without Epic Fight stamina. Skills in other categories still use the stamina of Epic Fight.

In the battle mode of Epic Fight, each swing of its basic attack combo costs stamina. The vanilla attack cost does not apply then, so the mod charges a swing only once. Swings you cannot pay for do not happen.

### Gliders

Gliding with a deployed glider drains stamina, because you hold on to it. You cannot deploy a glider without the stamina to begin. The glider folds when the stamina runs out.

### Create

A hand crank drains stamina while you keep it turning (you hold the use key on it). A valve handle does the same (`create.crank.valve_handles`). A crank you cannot afford does not turn. A crank you turn stops when the stamina runs out. The hunger cost of Create for cranking still applies.

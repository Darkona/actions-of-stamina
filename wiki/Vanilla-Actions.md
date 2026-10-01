# Vanilla Actions

| Action | How it costs | Default |
|---|---|---|
| Sprinting | Drain while you sprint. Stops regeneration | 0.25 feathers/s, 2 needed to start |
| Swimming (fast swim) | Drain while you swim | 0.5 feathers/s |
| Elytra flying | Drain while you glide. Free while a firework rocket boosts you | 0.05 feathers/s, 2 needed to start |
| Crawling | Drain while you move in the crawl pose on land. You move slower when you cannot pay | 0.1 feathers/s |
| Holding up a shield | A cost to raise it, then a drain | 1 feather, then 0.2/s |
| Jumping | One charge every few jumps | 1 feather every 4 jumps |
| Attacking, spear stabs included | One charge every few attacks | 1 feather every 3 attacks |
| Drawing a bow, loading a crossbow, aiming a trident | A cost to start, then a drain while drawn | 0.5 feathers, then 0.5/s |
| Throwing (snowball, egg, ender pearl, splash or lingering potion, wind charge, trident) | One charge per throw | 0.5 feathers |
| Mining (off by default) | One charge every few blocks, scaled by the hardness of the block | 0.1 feathers × hardness every 4 blocks |
| Building (off by default) | One charge every few blocks placed | 0.1 feathers every 4 blocks |
| Climbing (off by default) | Drain while you go up a ladder, vines, scaffolding or anything climbable | 0.3 feathers/s, 1 needed to start |
| Rowing (off by default) | Drain while you paddle or turn a boat that you drive | 0.15 feathers/s, 1 needed to start |
| Riptide launch (off by default) | One charge per launch | 1 feather |
| Fishing (off by default) | One charge per cast and one per reel | 0.25 feathers |
| Tilling, making paths, stripping logs, scraping copper (off by default) | One charge every few blocks | 0.25 feathers every 2 blocks |
| Brushing (off by default) | Drain while you brush | 0.2 feathers/s |
| Mace smash | A charge of its own, at a multiple of the attack cost | 2 feathers |

Sprinting and swimming need the stamina to begin (`min_stamina`), as sprinting needs food. Without it, the sprint key does nothing until the stamina comes back. Both stop when the stamina runs out.

Attacks with non-weapons (bare hands, tools without attack damage) are free unless `also_for_non_weapons = true`. By default, only attacks that hit an entity count (`only_for_hits`). With this option off, swings at the air count towards the charge like hits.

Without the stamina for an attack, the mod cancels it: no swing, no hit (`exhausted_mode = "CANCEL"`). With `exhausted_mode = "WEAKEN"`, the attack lands, but weakened. While your stamina is short, your attack damage and attack speed fall to `weaken_damage` and `weaken_speed` of their usual values (half, by default). They come back when you have the stamina to attack again.

**Girl mode** (`weaken_non_weapons`, on by default): WEAKEN also weakens attacks with non-weapons, such as bare hands, while your stamina is short. This applies even though those attacks cost nothing. Turn it off and only the attacks that cost stamina get weaker. An empty hand or a tool without attack damage then hits at full strength.

A spear stab is an attack. The mod charges it once per stab, however many creatures it pierces, and counts it with the other attacks. A stab that reaches nothing is a swing at the air. Without the stamina for it, a stab hits nothing, or lands weakened with `WEAKEN`. To hold a spear ready to charge at a target is free.

A mace smash is a mace hit while you fall. It does not count with the other attacks. The mod charges it alone, at once, at the `cost` of the attack times `mace_smash_multiplier` (2 by default, so 2 feathers). Without the stamina for it, the smash is cancelled or lands weakened, as `exhausted_mode` says. A swing at the air is never a smash.

## Bows, crossbows and throwables

The draw action counts each item used with the bow, crossbow or trident animation: a bow you draw, a crossbow you load, a trident you aim, and modded items with the same animations. To fire a loaded crossbow is free. You cannot start to draw without the stamina to begin. When the stamina runs out mid-draw, the draw drops without a shot.

Throws cost for the items in the item tag `actionsofstamina:throwables`: snowballs, eggs (`#minecraft:eggs`: white, blue and brown), ender pearls, splash and lingering potions, wind charges, and the trident. Datapacks can add other items. The mod charges a snowball, egg, pearl, potion or wind charge when you throw it. It charges a trident when you release it after a full aim (a Riptide launch is not a throw). A throw you cannot afford does not happen.

A Riptide launch is an action of its own, off by default (`[vanilla.riptide]`, `enabled = true` to turn it on). The mod charges it when you release the trident and the trident would launch you: a full aim, in water or rain, while you ride nothing. A launch you cannot afford does not happen. The aim only ends.

## Mining and building

Both are off by default (`[vanilla.mine]` and `[vanilla.build]`, `enabled = true` to turn them on). The mod charges them once every `times_performed_to_exhaust` blocks, for all the blocks since the last charge. With `scale_with_hardness = true` (the default), each broken block adds the cost times its hardness: dirt 0.5, stone 1.5, obsidian 50, up to `max_hardness_multiplier`. Blocks that break instantly are free.

The mod never cancels either one. Without the stamina, blocks break and place for free. With `block_when_exhausted = true`, mining slows to `exhausted_break_speed` of its usual speed (0.3 by default) while you cannot afford it (`min_stamina`). It stays slow until the stamina comes back.

## Climbing

Off by default (`[vanilla.climb]`, `enabled = true` to turn it on). To go up anything that the game lets you climb costs stamina: ladders, vines, scaffolding, twisting vines and modded ladders. To go down, or to hold on, is free.

When the stamina runs out, you cannot go up. You stay on the climbable block: sneak to hold on, or slide down slowly, as on any ladder. You go up again when you have the stamina to start (`min_stamina`). Ladders under water still lift you.

## Rowing

Off by default (`[vanilla.row]`, `enabled = true` to turn it on). Only boats in the entity tag `actionsofstamina:rowed_boats` cost stamina, and only for the player who drives. The tag of the mod holds all vanilla boats and rafts (`#minecraft:boat`) and all chest boats and chest rafts. Sail or motor boats from other mods move for free unless a datapack adds them to the tag.

The drain runs while you paddle forward or back, or turn. When the stamina runs out, the paddles stop and the boat drifts. It does not speed up or turn until you have the stamina to start again.

## Fishing

Off by default (`[vanilla.fish]`, `enabled = true` to turn it on). The mod charges each cast of a rod and each reel. Any item with the cast ability of the fishing rod counts, so modded rods cost too. A cast or reel you cannot afford does not happen. The line stays where it is until you have the stamina.

## Farming tools

Off by default (`[vanilla.till]`, `enabled = true` to turn it on). Work on a block with a tool costs stamina: tilling with a hoe, a path with a shovel, a stripped log, scraped or unwaxed copper with an axe. Any item whose block transformer changes the block counts, so modded tools and blocks count too. The block transformer is the `minecraft:block_transformer` component, or one that a data map adds. To douse a campfire is free. The mod charges once every `times_performed_to_exhaust` blocks. A block you cannot afford stays as it is.

## Brushing

Off by default (`[vanilla.brush]`, `enabled = true` to turn it on). Brushing drains stamina while it lasts. Any item used with the brush animation counts. You cannot start without the stamina to begin (`min_stamina`). When the stamina runs out, brushing stops.

## Wings

Only wings in the item tag `actionsofstamina:stamina_wings` cost stamina, worn in the chest slot or, with Curios, in a curio slot. The tag of the mod holds the vanilla elytra. Mechanical or propelled wings from other mods fly for free unless a datapack adds them to the tag. When the stamina runs out in the air, the wings fold and you fall. You cannot open them again until you have enough stamina to start.

While a firework rocket boosts you, the flight does not drain (`rocket_boost_costs = false`). Set it to `true` to keep the drain during the boost.

| ![Flying with an elytra](images/elytra.png) | ![Holding up a shield](images/shield.png) |
|---|---|
| Elytra flight drains slowly | A raised shield costs to raise, then drains |

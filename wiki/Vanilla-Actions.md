# Vanilla Actions

| Action | How it costs | Default |
|---|---|---|
| Sprinting | Drain while sprinting; blocks regeneration | 0.25 feathers/s, 2 needed to start |
| Swimming (fast swim) | Drain while swimming | 0.5 feathers/s |
| Elytra flying | Drain while gliding, free while a firework rocket boosts you | 0.05 feathers/s, 2 needed to start |
| Crawling | Drain while moving in the crawl pose on land; you move slower when you can't pay | 0.1 feathers/s |
| Holding up a shield | Cost to raise it, then a drain | 1 feather, then 0.2/s |
| Jumping | Charged once every few jumps | 1 feather every 4 jumps |
| Attacking | Charged once every few attacks | 1 feather every 3 attacks |
| Drawing a bow, loading a crossbow, aiming a trident | Cost to start, then a drain while drawn | 0.5 feathers, then 0.5/s |
| Throwing (snowball, egg, ender pearl, splash or lingering potion, trident) | Charged per throw | 0.5 feathers |
| Mining (off by default) | Charged once every few blocks, scaled by the block's hardness | 0.1 feathers × hardness every 4 blocks |
| Building (off by default) | Charged once every few blocks placed | 0.1 feathers every 4 blocks |

Attacks with non-weapons (bare hands, tools without attack damage) are free unless `also_for_non_weapons = true`.
By default only attacks that hit an entity are charged (`only_for_hits`).

Without the stamina for an attack, the attack is cancelled: no swing, no hit (`exhausted_mode = "CANCEL"`). With `exhausted_mode = "WEAKEN"` it lands anyway, but weakened: while your stamina is short, your attack damage and attack speed drop to `weaken_damage` and `weaken_speed` of their normal values (half, by default). They come back as soon as you have the stamina to attack again.

## Bows, crossbows and throwables

Drawing counts any item used with the bow, crossbow or spear animation: drawing a bow, loading a crossbow, aiming a trident, and modded items that use the same animations. Firing a loaded crossbow is free. You can't start drawing without the stamina to begin, and when the stamina runs out mid-draw the draw is dropped without a shot.

Throws cost for the items in the item tag `actionsofstamina:throwables`: snowballs, eggs, ender pearls, splash and lingering potions, and the trident. Datapacks can add others. A snowball, egg, pearl or potion is charged when thrown; a trident when you release it after a full aim (a Riptide launch isn't a throw). A throw you can't afford doesn't happen.

## Mining and building

Both are off by default (`[vanilla.mine]` and `[vanilla.build]`, `enabled = true` to turn them on). They are charged once every `times_to_charge` blocks, for all the blocks since the last charge. With `scale_with_hardness = true` (the default) each broken block adds the cost times its hardness: dirt 0.5, stone 1.5, obsidian 50, capped at `max_hardness_multiplier`; blocks that break instantly are free.

Neither is ever cancelled: without the stamina, blocks break and place for free. With `block_when_exhausted = true`, mining slows down to `exhausted_break_speed` of its normal speed (0.3 by default) while you can't afford it (`min_stamina`), until the stamina is back.

## Wings

Only wings in the item tag `actionsofstamina:stamina_wings`, worn in the chest slot (or, with Curios, in a curio slot), cost stamina. The mod's tag holds the vanilla elytra; mechanical or propelled wings from other mods fly for free unless a datapack adds them to the tag. When the stamina runs out in the air, the wings fold and you fall; you can't open them again until you have enough stamina to start.

While a firework rocket boosts you, the flight doesn't drain (`rocket_boost_costs = false`). Set it to `true` to keep draining during the boost.

| ![Flying with an elytra](images/elytra.png) | ![Holding up a shield](images/shield.png) |
|---|---|
| Elytra flight drains slowly | A raised shield costs to raise, then drains |

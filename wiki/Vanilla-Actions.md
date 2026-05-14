# Vanilla Actions

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

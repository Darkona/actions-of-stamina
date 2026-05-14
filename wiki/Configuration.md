# Configuration

`config/actionsofstamina-server.toml`, a server config: a server sends its own to every player who joins, so every
client charges the same costs (modpacks set their defaults in `defaultconfigs/`). Costs are in feathers, and a
feather is half a HUD icon. The defaults are tuned for a 20-feather bar. Delays are in ticks (20 ticks = 1 second).

The actions are rebuilt from the config whenever a player joins a level, so most edits apply on the next
respawn or dimension change. Changing `backend` needs a server restart.

`config/actionsofstamina-client.toml` controls the internal stamina bar: `hud.enabled`, `hud.x_offset` and
`hud.y_offset`.

## Default `actionsofstamina-server.toml`

```toml
[general]
	#Where stamina comes from:
	# AUTO     - Green Feathers when it is installed, otherwise AoS's internal stamina
	# FEATHERS - Green Feathers (falls back to the internal stamina if it isn't installed)
	# INTERNAL - AoS's internal stamina, even with Green Feathers installed
	#Allowed Values: AUTO, FEATHERS, INTERNAL
	backend = "AUTO"
	#Log every action change and show the action debug HUD
	debugging = false

#AoS's own stamina bar, used only when the backend is the internal one (no Green Feathers)
[internal]
	#Whether the internal stamina exists at all; if false, every action is free without Green Feathers
	enabled = true
	#Size of the bar, in feathers
	# Default: 20
	# Range: 1 ~ 1000
	max_feathers = 20
	#Feathers regenerated per second
	# Default: 0.5
	# Range: 0.0 ~ 100.0
	regen_per_second = 0.5
	#Minimum ticks without regeneration after any spend (actions may ask for longer)
	# Default: 30
	# Range: 0 ~ 1200
	regen_delay = 30
	#After running out, the share of the bar to regain before acting again (0-1)
	# Default: 0.3
	# Range: 0.0 ~ 1.0
	exhaustion_recovery = 0.3

#Vanilla actions
[vanilla]

	#Attacking
	[vanilla.attack]
		#Whether this action costs stamina.
		enabled = true
		#Cost of an attack (feathers)
		# Default: 1.0
		# Range: 0.0 ~ 1000.0
		cost = 1.0
		#Feathers needed to begin (at least the cost)
		# Default: 1.0
		# Range: 0.0 ~ 1000.0
		min_stamina = 1.0
		#Charge the cost once every this many uses
		# Default: 3
		# Range: 1 ~ 100
		times_to_charge = 3
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 70
		# Range: 0 ~ 1200
		regen_delay = 70
		#Whether attacks with non-weapons (bare hands, tools without attack damage) cost too
		also_for_non_weapons = false
		#Whether only attacks that hit an entity cost (misses stay free)
		only_for_hits = true

	#Jumping
	[vanilla.jump]
		#Whether this action costs stamina.
		enabled = true
		#Cost of a jump (feathers)
		# Default: 1.0
		# Range: 0.0 ~ 1000.0
		cost = 1.0
		#Feathers needed to begin (at least the cost)
		# Default: 1.0
		# Range: 0.0 ~ 1000.0
		min_stamina = 1.0
		#Charge the cost once every this many uses
		# Default: 4
		# Range: 1 ~ 100
		times_to_charge = 4
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 40
		# Range: 0 ~ 1200
		regen_delay = 40

	#Sprinting
	[vanilla.sprint]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers needed to begin (at least the cost)
		# Default: 2.0
		# Range: 0.0 ~ 1000.0
		min_stamina = 2.0
		#Feathers drained per second while it lasts
		# Default: 0.25
		# Range: 0.0 ~ 1000.0
		per_second = 0.25
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 40
		# Range: 0 ~ 1200
		regen_delay = 40
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#Swimming (the fast, sprint-swimming pose)
	[vanilla.swim]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers needed to begin (at least the cost)
		# Default: 2.0
		# Range: 0.0 ~ 1000.0
		min_stamina = 2.0
		#Feathers drained per second while it lasts
		# Default: 0.5
		# Range: 0.0 ~ 1000.0
		per_second = 0.5
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 80
		# Range: 0 ~ 1200
		regen_delay = 80
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#Elytra flying
	[vanilla.elytra]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers needed to begin (at least the cost)
		# Default: 2.0
		# Range: 0.0 ~ 1000.0
		min_stamina = 2.0
		#Feathers drained per second while it lasts
		# Default: 0.05
		# Range: 0.0 ~ 1000.0
		per_second = 0.05
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#Crawling (moving in the swimming pose on land)
	[vanilla.crawl]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers needed to begin (at least the cost)
		# Default: 1.0
		# Range: 0.0 ~ 1000.0
		min_stamina = 1.0
		#Feathers drained per second while it lasts
		# Default: 0.1
		# Range: 0.0 ~ 1000.0
		per_second = 0.1
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 40
		# Range: 0 ~ 1200
		regen_delay = 40
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#Holding up a shield
	[vanilla.shield]
		#Whether this action costs stamina.
		enabled = true
		#Cost to raise it (feathers)
		# Default: 1.0
		# Range: 0.0 ~ 1000.0
		cost = 1.0
		#Feathers needed to begin (at least the cost)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		min_stamina = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.2
		# Range: 0.0 ~ 1000.0
		per_second = 0.2
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

#ParCool (parkour) actions. AoS charges them server side and blocks starting or continuing them
# when stamina runs short. ParCool's stamina is AoS's own stamina type, "actionsofstamina:stamina",
# which replaces ParCool's default "parcool:parcool" while this is enabled.
[parcool]
	#Whether ParCool actions cost stamina (only when ParCool is installed)
	enabled = true

	#ParCool fast_run
	[parcool.fast_run]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.4
		# Range: 0.0 ~ 1000.0
		per_second = 0.4
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool fast_swim
	[parcool.fast_swim]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.4
		# Range: 0.0 ~ 1000.0
		per_second = 0.4
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool horizontal_wall_run
	[parcool.horizontal_wall_run]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.6
		# Range: 0.0 ~ 1000.0
		per_second = 0.6
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool hang_on
	[parcool.hang_on]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.6
		# Range: 0.0 ~ 1000.0
		per_second = 0.6
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool hang_down
	[parcool.hang_down]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		per_second = 0.0
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool pole_climb
	[parcool.pole_climb]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.2
		# Range: 0.0 ~ 1000.0
		per_second = 0.2
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool slide_down
	[parcool.slide_down]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.2
		# Range: 0.0 ~ 1000.0
		per_second = 0.2
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool ride_zipline
	[parcool.ride_zipline]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.4
		# Range: 0.0 ~ 1000.0
		per_second = 0.4
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool crawl
	[parcool.crawl]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		per_second = 0.0
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool slide
	[parcool.slide]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		per_second = 0.0
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool dive
	[parcool.dive]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		per_second = 0.0
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool skydive
	[parcool.skydive]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		per_second = 0.0
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool hide_in_block
	[parcool.hide_in_block]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		per_second = 0.0
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool grapple
	[parcool.grapple]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		per_second = 0.0
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool vault
	[parcool.vault]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.5
		# Range: 0.0 ~ 1000.0
		cost = 0.5
		#Feathers drained per second while it lasts
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		per_second = 0.0
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool climb_up
	[parcool.climb_up]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.5
		# Range: 0.0 ~ 1000.0
		cost = 0.5
		#Feathers drained per second while it lasts
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		per_second = 0.0
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool castaway
	[parcool.castaway]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.2
		# Range: 0.0 ~ 1000.0
		cost = 0.2
		#Feathers drained per second while it lasts
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		per_second = 0.0
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool dodge
	[parcool.dodge]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.5
		# Range: 0.0 ~ 1000.0
		cost = 0.5
		#Feathers drained per second while it lasts
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		per_second = 0.0
		#Feathers charged when it ends
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool charge_jump
	[parcool.charge_jump]
		#Whether this action costs stamina.
		enabled = true
		#Cost to start (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers drained per second while it lasts
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		per_second = 0.0
		#Feathers charged when it ends
		# Default: 0.5
		# Range: 0.0 ~ 1000.0
		finish_cost = 0.5
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

	#ParCool wall_jump
	[parcool.wall_jump]
		#Whether this action costs stamina.
		enabled = true
		#Cost of the action (feathers)
		# Default: 0.5
		# Range: 0.0 ~ 1000.0
		cost = 0.5
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20

	#ParCool wall_run
	[parcool.wall_run]
		#Whether this action costs stamina.
		enabled = true
		#Cost of the action (feathers)
		# Default: 0.5
		# Range: 0.0 ~ 1000.0
		cost = 0.5
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20

	#ParCool long_jump
	[parcool.long_jump]
		#Whether this action costs stamina.
		enabled = true
		#Cost of the action (feathers)
		# Default: 0.5
		# Range: 0.0 ~ 1000.0
		cost = 0.5
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20

	#ParCool trick_jump
	[parcool.trick_jump]
		#Whether this action costs stamina.
		enabled = true
		#Cost of the action (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20

	#ParCool breakfall
	[parcool.breakfall]
		#Whether this action costs stamina.
		enabled = true
		#Cost of the action (feathers)
		# Default: 0.5
		# Range: 0.0 ~ 1000.0
		cost = 0.5
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20

#Paragliders: paragliding costs AoS stamina and Paragliders reads its stamina from AoS (only when Paragliders is installed)
[paragliders]
	#Whether this action costs stamina.
	enabled = true
	#Cost to open the paraglider (feathers)
	# Default: 0.0
	# Range: 0.0 ~ 1000.0
	cost = 0.0
	#Feathers needed to begin (at least the cost)
	# Default: 1.0
	# Range: 0.0 ~ 1000.0
	min_stamina = 1.0
	#Feathers drained per second while it lasts
	# Default: 0.1
	# Range: 0.0 ~ 1000.0
	per_second = 0.1
	#Ticks without regeneration after it (20 ticks = 1 second)
	# Default: 20
	# Range: 0 ~ 1200
	regen_delay = 20
	#Whether regeneration pauses while it lasts
	blocks_regen = true

#Better Combat: weapon swings cost stamina (only when Better Combat is installed). Swings of weapons with Better Combat attributes replace the vanilla attack cost.
[bettercombat]
	#Whether this action costs stamina.
	enabled = true
	#Cost of one swing (feathers)
	# Default: 0.4
	# Range: 0.0 ~ 1000.0
	cost = 0.4
	#Ticks without regeneration after it (20 ticks = 1 second)
	# Default: 40
	# Range: 0 ~ 1200
	regen_delay = 40
	#Cost multiplier for two-handed weapons
	# Default: 1.5
	# Range: 0.0 ~ 10.0
	two_handed_multiplier = 1.5
	#Cost multiplier for off-hand swings while dual wielding
	# Default: 0.75
	# Range: 0.0 ~ 10.0
	off_hand_multiplier = 0.75
	#Cost multiplier for the last swing of a weapon's combo
	# Default: 1.25
	# Range: 0.0 ~ 10.0
	combo_finisher_multiplier = 1.25
	#Whether swings are stopped when the stamina can't pay for them (otherwise they are just free)
	block_when_short = true

#Combat Roll: each roll costs stamina (only when Combat Roll is installed)
[combat_roll]
	#Whether this action costs stamina.
	enabled = true
	#Cost of a roll (feathers)
	# Default: 1.5
	# Range: 0.0 ~ 1000.0
	cost = 1.5
	#Ticks without regeneration after it (20 ticks = 1 second)
	# Default: 30
	# Range: 0 ~ 1200
	regen_delay = 30

#Epic Fight skills (only when Epic Fight is installed). A skill that can't be paid for fails, as it would
# without Epic Fight stamina.
[epicfight]
	#Whether Epic Fight skills cost AoS stamina
	enabled = true

	#Dodge skills (step, roll)
	[epicfight.dodge]
		#Whether this action costs stamina.
		enabled = true
		#Cost of one use (feathers)
		# Default: 2.0
		# Range: 0.0 ~ 1000.0
		cost = 2.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 30
		# Range: 0 ~ 1200
		regen_delay = 30

	#Guard skills, charged per blocked hit
	[epicfight.guard]
		#Whether this action costs stamina.
		enabled = true
		#Cost of one use (feathers)
		# Default: 1.0
		# Range: 0.0 ~ 1000.0
		cost = 1.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 30
		# Range: 0 ~ 1200
		regen_delay = 30

	#Weapon innate skills that use stamina
	[epicfight.innate]
		#Whether this action costs stamina.
		enabled = true
		#Cost of one use (feathers)
		# Default: 3.0
		# Range: 0.0 ~ 1000.0
		cost = 3.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 30
		# Range: 0 ~ 1200
		regen_delay = 30

	#Mover skills (e.g. double jump, demolition leap)
	[epicfight.mover]
		#Whether this action costs stamina.
		enabled = true
		#Cost of one use (feathers)
		# Default: 2.0
		# Range: 0.0 ~ 1000.0
		cost = 2.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 30
		# Range: 0 ~ 1200
		regen_delay = 30

	#Each swing of Epic Fight's basic attack combo in its battle mode. While in battle mode this replaces AoS's vanilla attack cost, so a swing is only charged once
	[epicfight.basic_attack]
		#Whether this action costs stamina.
		enabled = true
		#Cost of one use (feathers)
		# Default: 1.0
		# Range: 0.0 ~ 1000.0
		cost = 1.0
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 30
		# Range: 0 ~ 1200
		regen_delay = 30

#Wall-Jump TXF (only when it is installed). A jump or a grip that can't be paid for doesn't happen,
# and a player clinging to a wall lets go when the stamina runs out.
[walljump]
	#Whether Wall-Jump TXF's moves cost stamina
	enabled = true

	#Jumping off a wall
	[walljump.wall_jump]
		#Whether this action costs stamina.
		enabled = true
		#Cost of a wall jump (feathers)
		# Default: 0.5
		# Range: 0.0 ~ 1000.0
		cost = 0.5
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20

	#Jumping again in mid-air
	[walljump.double_jump]
		#Whether this action costs stamina.
		enabled = true
		#Cost of a double jump (on its own: it doesn't also pay for a normal jump) (feathers)
		# Default: 1.5
		# Range: 0.0 ~ 1000.0
		cost = 1.5
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20

	#Clinging to a wall, and sliding down it afterwards
	[walljump.wall_cling]
		#Whether this action costs stamina.
		enabled = true
		#Cost to grab the wall (feathers)
		# Default: 0.0
		# Range: 0.0 ~ 1000.0
		cost = 0.0
		#Feathers needed to begin (at least the cost)
		# Default: 0.5
		# Range: 0.0 ~ 1000.0
		min_stamina = 0.5
		#Feathers drained per second while it lasts
		# Default: 0.4
		# Range: 0.0 ~ 1000.0
		per_second = 0.4
		#Ticks without regeneration after it (20 ticks = 1 second)
		# Default: 20
		# Range: 0 ~ 1200
		regen_delay = 20
		#Whether regeneration pauses while it lasts
		blocks_regen = true

#Gliders: gliding drains stamina (you hold on to the glider). A glider can't be deployed without the stamina to begin, and closes when the stamina runs out (only when Gliders is installed)
[gliders]
	#Whether this action costs stamina.
	enabled = true
	#Cost to deploy the glider (feathers)
	# Default: 0.0
	# Range: 0.0 ~ 1000.0
	cost = 0.0
	#Feathers needed to begin (at least the cost)
	# Default: 1.0
	# Range: 0.0 ~ 1000.0
	min_stamina = 1.0
	#Feathers drained per second while it lasts
	# Default: 0.1
	# Range: 0.0 ~ 1000.0
	per_second = 0.1
	#Ticks without regeneration after it (20 ticks = 1 second)
	# Default: 20
	# Range: 0 ~ 1200
	regen_delay = 20
	#Whether regeneration pauses while it lasts
	blocks_regen = true
```

## Default `actionsofstamina-client.toml`

```toml
#The internal stamina bar, drawn above the food bar when Green Feathers isn't the backend
[hud]
	enabled = true
	# Default: 0
	# Range: -1000 ~ 1000
	x_offset = 0
	# Default: 0
	# Range: -1000 ~ 1000
	y_offset = 0
```

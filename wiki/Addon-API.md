# Addon API

*For mod developers.* Another mod can add its own stamina-costing actions to Actions of Stamina. They get a config section like the built-in ones, spend through whichever stamina backend is active (Feathers of Fatigue or the own bar), and are gated, drained and charged by the same code as sprinting or attacking. Actions of Stamina's own actions, the vanilla ones and those of every supported mod, are registered the same way.

Everything below is in `com.ccr4ft3r.actionsofstamina` (Minecraft 26.2, NeoForge). On Minecraft 1.21.1, ids are `ResourceLocation` where this page says `Identifier`.

## Asking whether a player can do something

`PlayerActions.canPerform(player, type)` says whether the player may perform or begin an action now. `PlayerActions.perform(player, type)` performs a one-off action: it counts the use, charges the cost when it is due (`times_performed_to_exhaust`) and returns whether the action may go ahead. Neither ever refuses a creative or spectator player, a fake player, or an action the config turns off.

The types of Minecraft's actions are in `VanillaActions` (`VanillaActions.ATTACK`, `VanillaActions.JUMP`, `VanillaActions.SPRINT` and so on); each supported mod's are in its compat class (`WallJumpCompat.WALL_JUMP`, `CombatRollCompat.ROLL`...).

## Adding an action

An action has a config section and a type registered in `ActionTypes`, and optionally an `Action` subclass for effects of its own.

The config section is an `ActionCostConfig`, built on your own config spec. Register that spec as a **server** config, so the server's costs reach every client:

```java
public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
public static final ActionCostConfig DASH_COSTS = ActionCostConfig.builder(BUILDER, "dash", "Dashing", true)
        .cost(1.0, "Cost of a dash")
        .minStamina(1.0)
        .regenDelay(40)
        .build();
public static final ModConfigSpec SPEC = BUILDER.build();
```

The builder offers `cost`, `minStamina`, `perSecond`, `finishCost`, `regenDelay`, `blocksRegen` and `timesPerformedToExhaust`, in feathers and ticks; define only the ones your action uses, in that order. `spec()` gives the spec builder for options of your own before `build()`.

The type is registered while your mod is constructed, or in `FMLCommonSetupEvent`:

```java
public static final ActionType DASH = ActionTypes.register(Identifier.fromNamespaceAndPath("mymod", "dash"), DASH_COSTS, Action::new);

public MyMod(IEventBus modBus, ModContainer container) {
    container.registerConfig(ModConfig.Type.SERVER, SPEC);
}
```

The id is also the stamina source the action spends under, so Feathers of Fatigue's cost modifiers and debug output see it by that name. Players get the action while its config section is enabled; the four-argument `register` takes a condition of its own instead (for instance, the section's switch and whether another mod is installed). The last argument builds the action for one player from its type: `Action::new` for an action that only costs, or the constructor of your own subclass. Players get a fresh set of actions every time they join a level (login, respawn, dimension change), so a config change applies from the next one.

Registration closes when loading completes. Then every type is numbered in the order of its id, so a client and a server with the same mods agree on every number. Registering later, or the same id twice, throws.

## One-off actions

A one-off action (a dash, a throw) is performed from your own code, on the side that decides it. Call `PlayerActions.perform(player, DASH)` and cancel the action when it returns false. The server is the one that charges: on the client, `perform` only checks. If only the client sees the action happen, check with `canPerform` there and send `new ActionPerformedPacket((byte) DASH.index())` to the server, which performs it as if it had seen it itself.

A use whose cost changes each time (a swing multiplied by its weapon) asks the player's action directly: `PlayerActions.get(player).getAction(type)` gives it (null when the config turns it off), and `canPay(player, stamina)` and `pay(player, stamina)` check and spend an amount you work out, with the action's regen delay.

## Continuous actions

A continuous action (gliding, channelling) drains stamina while it lasts. There are two ways to drive one.

**By state.** Tell Actions of Stamina every tick whether the action is going on, on both sides, with `PlayerActions.get(player).setActionState(type, doing)`, from state both sides know (synced data, an item component). The action then runs by itself:

- It begins when the player has the stamina to begin (`min_stamina`, else one drain tick, else the finish cost), and charges `cost` at the start.
- It drains `per_second` while it lasts, and pauses regeneration if `blocks_regen`.
- It ends when the state goes off or the stamina runs out. It then charges `finish_cost`, or pauses regeneration for `regen_delay` after a drain.

A subclass of `Action` can add effects: `performingEffects` runs every tick the action is going on and paid for, and `notPerformingEffects` runs every tick the state is on but the stamina can't pay (stop the action there: fold the glider, drop the channel). Override `cleanUp` to remove anything the action leaves on the player, such as attribute modifiers: it runs when the action ends for an exempt player and when the actions are rebuilt.

**By events.** When another mod already tells you when the action starts, ticks and ends (the ParCool compat of Minecraft 1.21.1 works this way), call the player's action at each point instead of setting its state: `canBegin(player)` before it starts (refuse the start when false), `begin(player)` when it starts, `canContinue(player)` and `continueTick(player)` on each tick while `drains()` is true, and `end(player)` when it ends. Do this on the server; on the deciding client, only the `canBegin` and `canContinue` checks.

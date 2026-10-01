# Addon API

*For mod developers.* Another mod can add its own stamina-costing actions to Actions of Stamina. Each one gets a config section like the built-in actions and spends through the active stamina backend (Feathers of Fatigue or the own bar). The same code that gates, drains and charges sprinting or attacking does it for these actions too. The own actions of Actions of Stamina, the vanilla ones and those of each supported mod, use the same registration.

All the classes on this page are in `com.ccr4ft3r.actionsofstamina` (Minecraft 26.3, NeoForge). On Minecraft 1.21.1, ids are `ResourceLocation` where this page says `Identifier`.

## Asking whether a player can do something

`PlayerActions.canPerform(player, type)` tells whether the player can perform or begin an action now. `PlayerActions.perform(player, type)` performs a one-off action. It counts the use, charges the cost when it is due (`times_performed_to_exhaust`) and returns whether the action can go ahead. Neither call ever refuses a creative or spectator player, a fake player, or an action that the config turns off.

The types of the Minecraft actions are in `VanillaActions` (`VanillaActions.ATTACK`, `VanillaActions.JUMP`, `VanillaActions.SPRINT` and the others). The types of each supported mod are in its compat class (`WallJumpCompat.WALL_JUMP`, `CombatRollCompat.ROLL`...).

## Adding an action

An action has a config section and a type registered in `ActionTypes`, and optionally an `Action` subclass for effects of its own.

The config section is an `ActionCostConfig`, built on your own config spec. Register that spec as a **server** config, so that the costs of the server reach all clients:

```java
public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
public static final ActionCostConfig DASH_COSTS = ActionCostConfig.builder(BUILDER, "dash", "Dashing", true)
        .cost(1.0, "Cost of a dash")
        .minStamina(1.0)
        .regenDelay(40)
        .build();
public static final ModConfigSpec SPEC = BUILDER.build();
```

The builder has `cost`, `minStamina`, `perSecond`, `finishCost`, `regenDelay`, `blocksRegen` and `timesPerformedToExhaust`, in feathers and ticks. Define only the ones that your action uses, in that order. Before `build()`, `spec()` gives the spec builder for options of your own.

Register the type while your mod is constructed, or in `FMLCommonSetupEvent`:

```java
public static final ActionType DASH = ActionTypes.register(Identifier.fromNamespaceAndPath("mymod", "dash"), DASH_COSTS, Action::new);

public MyMod(IEventBus modBus, ModContainer container) {
    container.registerConfig(ModConfig.Type.SERVER, SPEC);
}
```

The id is also the stamina source that the action spends under, so the cost modifiers and debug output of Feathers of Fatigue show it by that name. Players get the action while its config section is enabled. The four-argument `register` takes a condition of its own in place of that (for example, the switch of the section and whether another mod is installed). The last argument builds the action for one player from its type: `Action::new` for an action that only costs, or the constructor of your own subclass. Players get a new set of actions each time they join a level (login, respawn, dimension change), so a config change applies from the next join.

Registration closes when loading completes. Then each type gets a number in the order of its id, so a client and a server with the same mods agree on all numbers. A later registration, or the same id twice, throws an exception.

## One-off actions

Your own code performs a one-off action (a dash, a throw), on the side that decides it. Call `PlayerActions.perform(player, DASH)` and cancel the action when it returns false. Only the server charges. On the client, `perform` only checks. If only the client sees the action, check with `canPerform` there and send `new ActionPerformedPacket((byte) DASH.index())` to the server. The server then performs it as if it saw it itself.

For a use whose cost changes each time (a swing multiplied by its weapon), ask the action of the player directly. `PlayerActions.get(player).getAction(type)` gives it (null when the config turns it off). `canPay(player, stamina)` and `pay(player, stamina)` check and spend an amount that you calculate, with the regen delay of the action.

## Continuous actions

A continuous action (gliding, channelling) drains stamina while it lasts. You can drive one in two ways.

**By state.** On each tick, on both sides, tell Actions of Stamina whether the action is in progress, with `PlayerActions.get(player).setActionState(type, doing)`. Use state that both sides know (synced data, an item component). The action then runs by itself:

- It begins when the player has the stamina to begin (`min_stamina`, else one drain tick, else the finish cost), and charges `cost` at the start.
- It drains `per_second` while it lasts, and pauses regeneration if `blocks_regen` is set.
- It ends when the state goes off or the stamina runs out. It then charges `finish_cost`, or pauses regeneration for `regen_delay` after a drain.

A subclass of `Action` can add effects. `performingEffects` runs on each tick that the action is in progress and paid for. `notPerformingEffects` runs on each tick that the state is on but the stamina cannot pay. Stop the action there: fold the glider, drop the channel. Override `cleanUp` to remove anything that the action leaves on the player, such as attribute modifiers. It runs when the action ends for an exempt player and when the mod builds the actions again.

**By events.** Another mod can already tell you when the action starts, ticks and ends (the ParCool compat of Minecraft 1.21.1 works this way). In that case, call the action of the player at each point and do not set its state:

1. Before it starts, call `canBegin(player)`. When it returns false, refuse the start.
2. When it starts, call `begin(player)`.
3. On each tick while `drains()` is true, call `canContinue(player)` and `continueTick(player)`.
4. When it ends, call `end(player)`.

Do this on the server. On the client that decides, do only the `canBegin` and `canContinue` checks.

# Addon API

*For mod developers.* Another mod can add its own stamina-costing actions to Actions of Stamina: they get a config section like the built-in ones, spend through whichever stamina backend is active (Green Feathers or the own bar), and are gated, drained and charged by the same code as sprinting or attacking.

Everything below is in `com.ccr4ft3r.actionsofstamina` (Minecraft 1.21.1, NeoForge).

## Asking whether a player can do something

`PlayerActions.canPerform(player, actionId)` says whether the player may perform or begin an action now. `PlayerActions.perform(player, actionId)` performs a one-off action: it counts the use, charges the cost when it is due (`times_to_charge`) and returns whether the action may go ahead. Neither ever refuses a creative or spectator player, a fake player, or an action the config turns off.

The ids of the built-in actions are the constants in `Action` (`Action.ATTACK`, `Action.JUMP`, `Action.SPRINT` and so on). An addon's own action uses the index of its type, below.

## Adding an action

An action has three parts: a config section, an `Action` subclass, and a type registered in `ActionTypes`.

The config section is an `ActionCostConfig`, built on your own config spec. Register that spec as a **server** config, so the server's costs reach every client:

```java
public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
public static final ActionCostConfig DASH = ActionCostConfig.builder(BUILDER, "dash", "Dashing", true)
        .cost(1.0, "Cost of a dash")
        .minStamina(1.0)
        .regenDelay(40)
        .build();
public static final ModConfigSpec SPEC = BUILDER.build();
```

The builder offers `cost`, `minStamina`, `perSecond`, `finishCost`, `regenDelay`, `blocksRegen` and `timesToCharge`, in feathers and ticks; define only the ones your action uses, in that order. `spec()` gives the spec builder for options of your own before `build()`.

The action extends `Action`. Its `id()` returns its type's index:

```java
public class DashAction extends Action {
    public DashAction() {
        super(MyMod.DASH_TYPE.id(), MyMod.DASH);
    }

    @Override
    public String name() {
        return "dash_action";
    }

    @Override
    public int id() {
        return MyMod.DASH_TYPE.index();
    }

    @Override
    protected void performingEffects(Player player, PlayerActions actions) {
    }

    @Override
    protected void notPerformingEffects(Player player, PlayerActions actions) {
    }
}
```

The type is registered while your mod is constructed, or in `FMLCommonSetupEvent`:

```java
public MyMod(IEventBus modBus, ModContainer container) {
    container.registerConfig(ModConfig.Type.SERVER, SPEC);
    DASH_TYPE = ActionTypes.register(ResourceLocation.fromNamespaceAndPath("mymod", "dash"), DASH::enabled, DashAction::new);
}
```

The id is also the stamina source the action spends under, so Green Feathers' cost modifiers and debug output see it by that name. The second argument decides whether players get the action (usually the config's `enabled`), and the third builds one for a player. Players get a fresh set of actions every time they join a level (login, respawn, dimension change), so a config change applies from the next one.

Registration closes when loading completes. After that, `ActionType.index()` is known: the built-in actions keep their slots, and addon types follow them, sorted by id, so a client and a server with the same mods agree on every index. Registering later, or the same id twice, throws.

## One-off and continuous actions

A **one-off** action (a dash, a throw) is performed from your own code, on the side that decides it. Call `PlayerActions.perform(player, DASH_TYPE.index())` and cancel the action when it returns false. The server is the one that charges: on the client, `perform` only checks. If only the client sees the action happen, send `new ActionPerformedPacket((byte) DASH_TYPE.index())` to the server, which performs it there as if it had seen it itself.

A **continuous** action (gliding, channelling) drains stamina while it lasts. Tell Actions of Stamina every tick whether it is going on, on both sides, with `PlayerActions.get(player).setActionState(index, doing)`, from state both sides know (synced data, an item component). The action then runs by itself:

- It begins when the player has the stamina to begin (`min_stamina`, or the finish cost when that is the only cost), and charges `cost` at the start.
- It drains `per_second` while it lasts, and pauses regeneration if `blocks_regen`.
- It ends when the state goes off or the stamina runs out. It then charges `finish_cost` and pauses regeneration for `regen_delay`.

`performingEffects` runs every tick the action is going on and paid for. `notPerformingEffects` runs every tick the state is on but the stamina can't pay: stop the action there (fold the glider, drop the channel). Override `cleanUp` to remove anything the action leaves on the player, such as attribute modifiers: it runs when the action ends for an exempt player and when the actions are rebuilt.

package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.ContinuableAction;
import com.alrex.parcool.api.action.ParCoolActionEvent;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import org.jetbrains.annotations.Nullable;

/** Direct ParCool calls; only reached through {@link ParcoolCompat} when ParCool is loaded. */
final class ParcoolBridge {

    /** ParCool's stamina type that charges nothing: fine next to AoS, it just doesn't show AoS's stamina to ParCool. */
    private static final ResourceLocation PARCOOL_NONE_STAMINA = new ResourceLocation(ParcoolCompat.MOD_ID, "none");

    /** A ParCool action AoS charges: its own source and config. */
    private record Costed(ResourceLocation source, ActionCostConfig costs) {
    }

    /** Keyed by ParCool's singleton action entries; filled once at setup, read-only afterwards (both sides). */
    private static final Reference2ObjectOpenHashMap<ActionEntry<?>, Costed> COSTS = new Reference2ObjectOpenHashMap<>();

    private ParcoolBridge() {
    }

    static void register() {
        add(ParCoolActions.FAST_RUN);
        add(ParCoolActions.FAST_SWIM);
        add(ParCoolActions.HORIZONTAL_WALL_RUN);
        add(ParCoolActions.HANG_ON);
        add(ParCoolActions.HANG_DOWN);
        add(ParCoolActions.POLE_CLIMB);
        add(ParCoolActions.SLIDE_DOWN);
        add(ParCoolActions.RIDE_ZIPLINE);
        add(ParCoolActions.CRAWL);
        add(ParCoolActions.SLIDE);
        add(ParCoolActions.DIVE);
        add(ParCoolActions.SKYDIVE);
        add(ParCoolActions.HIDE_IN_BLOCK);
        add(ParCoolActions.GRAPPLE);
        add(ParCoolActions.VAULT);
        add(ParCoolActions.CLIMB_UP);
        add(ParCoolActions.CASTAWAY);
        add(ParCoolActions.DODGE);
        add(ParCoolActions.CHARGE_JUMP);
        add(ParCoolActions.WALL_JUMP);
        add(ParCoolActions.WALL_RUN);
        add(ParCoolActions.LONG_JUMP);
        add(ParCoolActions.TRICK_JUMP);
        add(ParCoolActions.BREAKFALL);

        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ParCoolActionEvent.TryToStart.class, ParcoolBridge::onTryToStart);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ParCoolActionEvent.TryToContinue.class, ParcoolBridge::onTryToContinue);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ParCoolActionEvent.Start.Post.class, ParcoolBridge::onStart);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ParCoolActionEvent.Tick.Post.class, ParcoolBridge::onTick);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ParCoolActionEvent.Finish.Post.class, ParcoolBridge::onFinish);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ServerStartedEvent.class, ParcoolBridge::onServerStarted);
    }

    private static void add(ActionEntry<?> entry) {
        String name = entry.id().getPath();
        ParcoolConfig.Entry config = ParcoolConfig.byName(name);
        if (config == null) {
            ActionsOfStamina.logger.warn("No AoS config for ParCool action {}", entry.id());
            return;
        }
        COSTS.put(entry, new Costed(ActionsOfStamina.id("parcool/" + name), config.costs()));
    }

    @Nullable
    private static Costed costed(ParCoolActionEvent event) {
        if (!ParcoolConfig.ENABLED.get()) return null;
        Costed costed = COSTS.get(event.getAction().getEntry());
        return costed != null && costed.costs.enabled() ? costed : null;
    }

    /** What must be affordable to start: the start cost, else one drain tick, else the finish cost. */
    private static int startRequirement(ActionCostConfig costs) {
        int cost = costs.cost();
        if (cost > 0) return cost;
        double perTick = costs.perTick();
        if (perTick > 0) return (int) Math.ceil(perTick);
        return costs.finishCost();
    }

    /** On the deciding side (the local client, or the server for server-triggered actions). */
    private static void onTryToStart(ParCoolActionEvent.TryToStart event) {
        Costed costed = costed(event);
        if (costed == null || !costed.costs.costsAnything()) return;
        Player player = event.getPlayer();
        if (!StaminaBackends.of(player).canSpend(player, costed.source, startRequirement(costed.costs))) {
            event.setCanceled(true);
        }
    }

    private static void onTryToContinue(ParCoolActionEvent.TryToContinue event) {
        Costed costed = costed(event);
        if (costed == null) return;
        double perTick = costed.costs.perTick();
        if (perTick <= 0) return;
        Player player = event.getPlayer();
        if (!StaminaBackends.of(player).canSpend(player, costed.source, (int) Math.ceil(perTick))) {
            event.setCanceled(true);
        }
    }

    private static void onStart(ParCoolActionEvent.Start.Post event) {
        Player player = event.getPlayer();
        if (player.level.isClientSide()) return;
        Costed costed = costed(event);
        if (costed == null) return;
        int cost = costed.costs.cost();
        if (cost > 0) StaminaBackends.server().spend(player, costed.source, cost, costed.costs.regenDelay());
    }

    /** Fires for every ParCool action of every player every tick: the cheap checks come first. */
    private static void onTick(ParCoolActionEvent.Tick.Post event) {
        Player player = event.getPlayer();
        if (player.level.isClientSide()) return;
        if (!(event.getAction() instanceof ContinuableAction action) || !action.isDoing()) return;
        Costed costed = costed(event);
        if (costed == null) return;
        double perTick = costed.costs.perTick();
        if (perTick <= 0) return;
        StaminaBackend backend = StaminaBackends.server();
        backend.drain(player, costed.source, perTick, costed.costs.blocksRegen() && !backend.keepsRegenWhileActing(player));
    }

    private static void onFinish(ParCoolActionEvent.Finish.Post event) {
        Player player = event.getPlayer();
        if (player.level.isClientSide()) return;
        Costed costed = costed(event);
        if (costed == null) return;
        StaminaBackend backend = StaminaBackends.server();
        backend.stopDrain(player, costed.source);
        int finish = costed.costs.finishCost();
        int delay = costed.costs.regenDelay();
        if (finish > 0) backend.spend(player, costed.source, finish, delay);
        else if (delay > 0 && costed.costs.perTick() > 0 && !backend.keepsRegenWhileActing(player)) {
            backend.blockRegen(player, costed.source, delay);
        }
    }

    /** Only a stamina type other than AoS's own, ParCool's own (replaced by AoS's) or none charges twice. */
    private static void onServerStarted(ServerStartedEvent event) {
        if (!ParcoolConfig.ENABLED.get()) return;
        if (!ParcoolStaminaType.isRegistered()) {
            ActionsOfStamina.logger.warn("AoS's ParCool stamina type {} isn't registered (did the ParCool mixin apply?)",
                    ParcoolCompat.STAMINA_TYPE);
        }
        ResourceLocation type = ParCool.getConfig().server().getStaminaTypeID();
        if (!ParcoolCompat.STAMINA_TYPE.equals(type) && !PARCOOL_NONE_STAMINA.equals(type)) {
            ActionsOfStamina.logger.warn("ParCool's stamina_type is {}: ParCool actions are charged by both {} and AoS."
                    + " Set it to \"{}\" in ParCool's server config.", type, type, ParcoolCompat.STAMINA_TYPE);
        }
    }

    static boolean isFastRunning(Player player) {
        return isDoing(player, ParCoolActions.FAST_RUN);
    }

    static boolean isFastSwimming(Player player) {
        return isDoing(player, ParCoolActions.FAST_SWIM);
    }

    static boolean isCrawling(Player player) {
        return isDoing(player, ParCoolActions.CRAWL);
    }

    private static boolean isDoing(Player player, ActionEntry<? extends ContinuableAction> entry) {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return false;
        ContinuableAction action = parkourability.get(entry);
        return action != null && action.isDoing();
    }
}

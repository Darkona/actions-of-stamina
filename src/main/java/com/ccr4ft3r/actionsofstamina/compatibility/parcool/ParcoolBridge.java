package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.ContinuableAction;
import com.alrex.parcool.api.action.ParCoolActionEvent;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
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

    /** Keyed by ParCool's singleton action entries; filled once at setup, read-only afterwards (both sides). */
    private static final Reference2ObjectOpenHashMap<ActionEntry<?>, ActionType> TYPES = new Reference2ObjectOpenHashMap<>();

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
        ActionType type = ParcoolCompat.typeOf(entry.id().getPath());
        if (type == null) {
            ActionsOfStamina.logger.warn("No AoS config for ParCool action {}", entry.id());
            return;
        }
        TYPES.put(entry, type);
    }

    /**
     * The player's AoS action for the ParCool action of {@code event}, or null when AoS doesn't charge it (the section
     * is off, or the action costs nothing). ParCool drives it: the start, each tick and the end come from its events.
     */
    @Nullable
    private static Action actionOf(ParCoolActionEvent event, Player player) {
        if (!ParcoolConfig.ENABLED.get()) return null;
        ActionType type = TYPES.get(event.getAction().getEntry());
        return type != null && type.config().enabled() ? PlayerActions.get(player).getAction(type) : null;
    }

    /** On the deciding side (the local client, or the server for server-triggered actions). */
    private static void onTryToStart(ParCoolActionEvent.TryToStart event) {
        Player player = event.getPlayer();
        Action action = actionOf(event, player);
        if (action != null && !action.canBegin(player)) event.setCanceled(true);
    }

    private static void onTryToContinue(ParCoolActionEvent.TryToContinue event) {
        Player player = event.getPlayer();
        Action action = actionOf(event, player);
        if (action != null && action.drains() && !action.canContinue(player)) event.setCanceled(true);
    }

    private static void onStart(ParCoolActionEvent.Start.Post event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide()) return;
        Action action = actionOf(event, player);
        if (action != null) action.begin(player);
    }

    /** Fires for every ParCool action of every player every tick: the cheap checks come first. */
    private static void onTick(ParCoolActionEvent.Tick.Post event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide()) return;
        if (!(event.getAction() instanceof ContinuableAction parcoolAction) || !parcoolAction.isDoing()) return;
        Action action = actionOf(event, player);
        if (action != null && action.drains()) action.continueTick(player);
    }

    private static void onFinish(ParCoolActionEvent.Finish.Post event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide()) return;
        Action action = actionOf(event, player);
        if (action != null) action.end(player);
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

package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.impl.BreakfallReady;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.ClimbPoles;
import com.alrex.parcool.common.action.impl.ClimbUp;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.action.impl.FastSwim;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.action.impl.HangDown;
import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.action.impl.HorizontalWallRun;
import com.alrex.parcool.common.action.impl.JumpFromBar;
import com.alrex.parcool.common.action.impl.RideZipline;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.action.impl.SkyDive;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.action.impl.Tap;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.action.impl.VerticalWallRun;
import com.alrex.parcool.common.action.impl.WallJump;
import com.alrex.parcool.common.action.impl.WallSlide;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.config.ActionCostConfig;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackends;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

/** Direct ParCool calls; only reached through {@link ParcoolCompat} when ParCool is loaded. */
final class ParcoolBridge {

    /** A ParCool action AoS charges: its own source and config. */
    private record Costed(ResourceLocation source, ActionCostConfig costs) {
    }

    /** Keyed by ParCool's action classes (one instance of each per player); filled once at setup, read-only afterwards. */
    private static final Reference2ObjectOpenHashMap<Class<? extends Action>, Costed> COSTS = new Reference2ObjectOpenHashMap<>();

    /** Readying a breakfall must afford it (see {@link #onTryToStart}); set with the other costs. */
    @Nullable
    private static Costed breakfall;

    /** Client thread only: the start data of a refused start that is only asked so the action drops its input. */
    private static final ByteBuffer DISCARDED_START = ByteBuffer.allocate(128);

    private ParcoolBridge() {
    }

    /** ParCool 3 actions under the names ParCool 4 gives them, so the config matches other versions where it can. */
    static void register() {
        add(FastRun.class, "fast_run");
        add(FastSwim.class, "fast_swim");
        add(HorizontalWallRun.class, "horizontal_wall_run");
        add(ClingToCliff.class, "hang_on");
        add(HangDown.class, "hang_down");
        add(ClimbPoles.class, "pole_climb");
        add(WallSlide.class, "slide_down");
        add(RideZipline.class, "ride_zipline");
        add(Crawl.class, "crawl");
        add(Slide.class, "slide");
        add(Dive.class, "dive");
        add(SkyDive.class, "skydive");
        add(HideInBlock.class, "hide_in_block");
        add(Vault.class, "vault");
        add(ClimbUp.class, "climb_up");
        add(JumpFromBar.class, "jump_from_bar");
        add(Dodge.class, "dodge");
        add(ChargeJump.class, "charge_jump");
        add(WallJump.class, "wall_jump");
        add(VerticalWallRun.class, "wall_run");
        add(CatLeap.class, "long_jump");
        add(Flipping.class, "trick_jump");
        // A breakfall lands as a roll or a tap.
        add(Roll.class, "breakfall");
        add(Tap.class, "breakfall");
        breakfall = COSTS.get(Roll.class);

        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ParCoolActionEvent.TryToStart.class, ParcoolBridge::onTryToStart);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ParCoolActionEvent.TryToContinue.class, ParcoolBridge::onTryToContinue);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ParCoolActionEvent.Start.Post.class, ParcoolBridge::onStart);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ParCoolActionEvent.Tick.Post.class, ParcoolBridge::onTick);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ParCoolActionEvent.Finish.Post.class, ParcoolBridge::onFinish);
    }

    private static void add(Class<? extends Action> action, String name) {
        ParcoolConfig.Entry config = ParcoolConfig.byName(name);
        if (config == null) {
            ActionsOfStamina.logger.warn("No AoS config for ParCool action {}", name);
            return;
        }
        COSTS.put(action, new Costed(ActionsOfStamina.id("parcool/" + name), config.costs()));
    }

    @Nullable
    private static Costed costed(ParCoolActionEvent event) {
        return enabled(COSTS.get(event.getAction().getClass()));
    }

    @Nullable
    private static Costed enabled(@Nullable Costed costed) {
        return costed != null && ParcoolConfig.ENABLED.get() && costed.costs.enabled() ? costed : null;
    }

    /** What must be affordable to start: the start cost, else one drain tick, else the finish cost. */
    private static int startRequirement(ActionCostConfig costs) {
        int cost = costs.cost();
        if (cost > 0) return cost;
        double perTick = costs.perTick();
        if (perTick > 0) return (int) Math.ceil(perTick);
        return costs.finishCost();
    }

    /**
     * On the deciding side: ParCool 3 decides its actions on the local client. The event comes before the action's own
     * start check, which is where some actions take in a one-off input: ParCool asks again every tick, so a refused
     * start would stay pending and happen whenever the stamina comes back.
     * <ul>
     * <li>A breakfall lands as a roll or a tap that ParCool keeps asking to start until it may, after the landing
     * already happened: those are never refused (only charged), and readying the breakfall is refused instead.</li>
     * <li>A trick jump or a dive remembers the last jump until its start check looks at it: when refused, the check
     * still runs (its answer ignored) so that jump is dropped.</li>
     * </ul>
     */
    private static void onTryToStart(ParCoolActionEvent.TryToStart event) {
        Action action = event.getAction();
        Class<? extends Action> type = action.getClass();
        if (type == Roll.class || type == Tap.class) return;
        Costed costed = type == BreakfallReady.class ? enabled(breakfall) : costed(event);
        if (costed == null || !costed.costs.costsAnything()) return;
        Player player = event.getPlayer();
        if (!StaminaBackends.of(player).canSpend(player, costed.source, startRequirement(costed.costs))) {
            event.setCanceled(true);
            if (type == Flipping.class || type == Dive.class) dropPendingJump(player, action);
        }
    }

    private static void dropPendingJump(Player player, Action action) {
        Parkourability parkourability = Parkourability.get(player);
        IStamina stamina = IStamina.get(player);
        if (parkourability == null || stamina == null) return;
        DISCARDED_START.clear();
        action.canStart(player, parkourability, stamina, DISCARDED_START);
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
        if (!event.getAction().isDoing()) return;
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

    static boolean isFastRunning(Player player) {
        return isDoing(player, FastRun.class);
    }

    static boolean isFastSwimming(Player player) {
        return isDoing(player, FastSwim.class);
    }

    static boolean isCrawling(Player player) {
        return isDoing(player, Crawl.class);
    }

    private static boolean isDoing(Player player, Class<? extends Action> type) {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return false;
        Action action = parkourability.get(type);
        return action != null && action.isDoing();
    }
}

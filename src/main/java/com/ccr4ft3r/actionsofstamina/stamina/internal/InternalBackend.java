package com.ccr4ft3r.actionsofstamina.stamina.internal;

import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.AosPlayerData;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaBackend;
import com.ccr4ft3r.actionsofstamina.stamina.StaminaUnits;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;

/**
 * AoS's own stamina, used when Feathers of Fatigue isn't installed (or {@code backend = internal}). Deliberately light:
 * a bar with a max, regeneration after a delay, exhaustion with a recovery threshold, and per-source continuous
 * drains. Server authoritative; the owning client gets {@link InternalStaminaPacket} only when something visible
 * changed. The client side only checks against those values, it never predicts.
 */
public final class InternalBackend implements StaminaBackend {

    public static final InternalBackend INSTANCE = new InternalBackend();

    /** A drain stops by itself this many ticks after its last refresh. */
    static final int DRAIN_TIMEOUT = 5;
    /** Stamina step the client is synced at: a quarter feather, about one HUD pixel. */
    static final int SYNC_STEP = 250;

    private InternalBackend() {
    }

    public static InternalStamina data(Player player) {
        return ((AosPlayerData) player).actionsofstamina$internalStamina();
    }

    public static boolean enabled() {
        return AoSServerConfig.INTERNAL_ENABLED.get();
    }

    static int configMaxStamina() {
        return AoSServerConfig.INTERNAL_MAX_FEATHERS.get() * StaminaUnits.PER_FEATHER;
    }

    /** Server: the bar's size, the configured one plus the transient bonus. */
    private static int serverMax(InternalStamina s) {
        return configMaxStamina() + s.bonusMax;
    }

    /** Creative and spectator players, a disabled internal stamina, and other players seen from a client. */
    private static boolean exempt(Player player) {
        return player.isCreative() || player.isSpectator() || !enabled()
                || player.level.isClientSide() && !player.isLocalPlayer();
    }

    /** Current stamina; an unset bar (new player, or a client before the first sync) counts as full. */
    private static int current(InternalStamina s) {
        if (s.stamina != InternalStamina.UNSET) return s.stamina;
        return s.maxStamina > 0 ? s.maxStamina : serverMax(s);
    }

    @Override
    public Kind kind() {
        return Kind.INTERNAL;
    }

    @Override
    public boolean canSpend(Player player, ResourceLocation source, int stamina) {
        if (exempt(player)) return true;
        InternalStamina s = data(player);
        return !s.exhausted && stamina <= current(s);
    }

    @Override
    public boolean spend(Player player, ResourceLocation source, int stamina, int regenDelayTicks) {
        if (exempt(player)) return true;
        InternalStamina s = data(player);
        int current = current(s);
        if (s.exhausted || stamina > current) return false;
        if (player.level.isClientSide()) return true;

        s.stamina = current - stamina;
        if (stamina > 0 || regenDelayTicks > 0) {
            int delay = Math.max(regenDelayTicks, AoSServerConfig.INTERNAL_REGEN_DELAY.get());
            if (delay > s.regenDelay) s.regenDelay = delay;
        }
        if (stamina > 0 && s.stamina == 0) s.exhausted = true;
        // Exact value to the client after a spend: it checks one-off actions (jumps, rolls) against it, and a value
        // a sync step too high lets it start one the server then refuses.
        if (stamina > 0) s.markForSync();
        return true;
    }

    @Override
    public boolean drain(Player player, ResourceLocation source, double staminaPerTick, boolean blocksRegen) {
        if (exempt(player)) return true;
        InternalStamina s = data(player);
        if (player.level.isClientSide()) return !s.exhausted && current(s) > 0;
        if (s.exhausted) {
            stopDrain(player, source);
            return false;
        }

        int current = current(s);
        InternalStamina.Drain drain = s.drain(source);
        if (!drain.active) {
            drain.active = true;
            drain.carry = 0;
        }
        drain.blocksRegen = blocksRegen;
        drain.lastRefreshTick = player.tickCount;
        drain.carry += staminaPerTick;
        int whole = (int) drain.carry;
        if (whole <= 0) {
            s.stamina = current;
            return true;
        }
        if (whole > current) {
            s.stamina = 0;
            s.exhausted = true;
            drain.active = false;
            return false;
        }
        drain.carry -= whole;
        s.stamina = current - whole;
        if (s.stamina == 0) s.exhausted = true;
        return true;
    }

    @Override
    public void stopDrain(Player player, ResourceLocation source) {
        if (player.level.isClientSide()) return;
        InternalStamina s = data(player);
        for (int i = 0; i < s.drainCount; i++) {
            InternalStamina.Drain drain = s.drains[i];
            if (drain.source.equals(source)) {
                drain.active = false;
                return;
            }
        }
    }

    @Override
    public void blockRegen(Player player, ResourceLocation source, int ticks) {
        if (player.level.isClientSide() || exempt(player)) return;
        InternalStamina s = data(player);
        if (ticks > s.regenDelay) s.regenDelay = ticks;
    }

    @Override
    public boolean keepsRegenWhileActing(Player player) {
        return false;
    }

    /** One bonus at a time (vessels are its only source): the bar grows now, or shrinks and clamps on the next tick. */
    @Override
    public void setMaxBonus(Player player, ResourceLocation source, int stamina) {
        if (player.level.isClientSide()) return;
        data(player).bonusMax = Math.max(0, stamina);
    }

    @Override
    public int stamina(Player player) {
        return current(data(player));
    }

    @Override
    public int maxStamina(Player player) {
        InternalStamina s = data(player);
        return player.level.isClientSide() ? s.maxStamina > 0 ? s.maxStamina : configMaxStamina() : serverMax(s);
    }

    @Override
    public int availableStamina(Player player) {
        return stamina(player);
    }

    @Override
    public boolean exhausted(Player player) {
        return data(player).exhausted;
    }

    /**
     * Server side, once per player tick: times out forgotten drains, regenerates, ends exhaustion and syncs the
     * owning client when something visible changed.
     */
    public void tick(ServerPlayer player) {
        InternalStamina s = data(player);
        int max = serverMax(s);
        s.maxStamina = max;
        int stamina = current(s);
        if (stamina > max) stamina = max;

        boolean drainBlocksRegen = false;
        int now = player.tickCount;
        for (int i = 0; i < s.drainCount; i++) {
            InternalStamina.Drain drain = s.drains[i];
            if (!drain.active) continue;
            if (now - drain.lastRefreshTick > DRAIN_TIMEOUT) drain.active = false;
            else if (drain.blocksRegen) drainBlocksRegen = true;
        }

        if (s.regenDelay > 0) {
            s.regenDelay--;
        } else if (!drainBlocksRegen && stamina < max) {
            s.regenCarry += StaminaUnits.perTick(AoSServerConfig.INTERNAL_REGEN_PER_SECOND.get());
            int whole = (int) s.regenCarry;
            if (whole > 0) {
                s.regenCarry -= whole;
                stamina = Math.min(max, stamina + whole);
            }
        }
        if (stamina >= max) s.regenCarry = 0;
        s.stamina = stamina;

        if (s.exhausted && stamina >= AoSServerConfig.INTERNAL_RECOVERY.get() * max) s.exhausted = false;

        sync(player, s);
    }

    private static void sync(ServerPlayer player, InternalStamina s) {
        if (player instanceof FakePlayer) return;
        int bucket = s.stamina >= s.maxStamina ? Integer.MAX_VALUE : s.stamina / SYNC_STEP;
        if (bucket == s.syncedBucket && s.maxStamina == s.syncedMax && s.exhausted == s.syncedExhausted) return;
        s.syncedBucket = bucket;
        s.syncedMax = s.maxStamina;
        s.syncedExhausted = s.exhausted;
        PacketHandler.sendToPlayer(player, new InternalStaminaPacket(s.stamina, s.maxStamina, s.exhausted));
    }
}

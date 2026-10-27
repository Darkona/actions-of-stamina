package com.ccr4ft3r.actionsofstamina.content.stamina;

import com.ccr4ft3r.actionsofstamina.network.InternalStaminaPacket;
import com.ccr4ft3r.actionsofstamina.config.ActionsConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class InternalBackend implements StaminaBackend {

    public static final InternalBackend INSTANCE = new InternalBackend();

    static final int DRAIN_TIMEOUT = 5;
    static final int SYNC_STEP = 250;

    private InternalBackend() {
    }

    public static InternalStamina data(Player player) {
        return player.getData(InternalStamina.ATTACHMENT);
    }

    public static boolean enabled() {
        return ActionsConfig.INTERNAL_ENABLED.getAsBoolean();
    }

    static int configMaxStamina() {
        return StaminaBackend.ofFeathers(ActionsConfig.INTERNAL_MAX_FEATHERS.getAsInt());
    }

    private static int serverMax(InternalStamina s) {
        return configMaxStamina() + s.bonusMax;
    }

    private static boolean exempt(Player player) {
        return player.isCreative() || player.isSpectator() || !enabled()
                || player.level().isClientSide() && !player.isLocalPlayer();
    }

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
        if (player.level().isClientSide()) return true;

        s.stamina = current - stamina;
        if (stamina > 0 || regenDelayTicks > 0) {
            int delay = Math.max(regenDelayTicks, ActionsConfig.INTERNAL_REGEN_DELAY.getAsInt());
            if (delay > s.regenDelay) s.regenDelay = delay;
        }
        if (stamina > 0 && s.stamina == 0) s.exhausted = true;
        if (stamina > 0) s.markForSync();
        return true;
    }

    @Override
    public boolean drain(Player player, ResourceLocation source, double staminaPerTick, boolean blocksRegen) {
        if (exempt(player)) return true;
        InternalStamina s = data(player);
        if (player.level().isClientSide()) return !s.exhausted && current(s) > 0;
        if (s.exhausted) {
            stopDrain(player, source);
            return false;
        }

        int current = current(s);
        s.drains.refresh(source, staminaPerTick, DRAIN_TIMEOUT, false, blocksRegen, player.tickCount);
        int whole = s.drains.advance(s.drains.indexOf(source));
        if (whole <= 0) {
            s.stamina = current;
            return true;
        }
        if (whole > current) {
            s.stamina = 0;
            s.exhausted = true;
            s.drains.remove(source);
            return false;
        }
        s.stamina = current - whole;
        if (s.stamina == 0) s.exhausted = true;
        return true;
    }

    @Override
    public void stopDrain(Player player, ResourceLocation source) {
        if (player.level().isClientSide()) return;
        data(player).drains.remove(source);
    }

    @Override
    public void blockRegen(Player player, ResourceLocation source, int ticks) {
        if (player.level().isClientSide() || exempt(player)) return;
        InternalStamina s = data(player);
        if (ticks > s.regenDelay) s.regenDelay = ticks;
    }

    @Override
    public boolean keepsRegenWhileActing(Player player) {
        return false;
    }

    @Override
    public void setMaxBonus(Player player, ResourceLocation source, int stamina) {
        if (player.level().isClientSide()) return;
        data(player).bonusMax = Math.max(0, stamina);
    }

    @Override
    public int stamina(Player player) {
        return current(data(player));
    }

    @Override
    public int maxStamina(Player player) {
        InternalStamina s = data(player);
        return player.level().isClientSide() ? s.maxStamina > 0 ? s.maxStamina : configMaxStamina() : serverMax(s);
    }

    @Override
    public int availableStamina(Player player) {
        return stamina(player);
    }

    @Override
    public boolean exhausted(Player player) {
        return data(player).exhausted;
    }

    public void tick(ServerPlayer player) {
        InternalStamina s = data(player);
        int max = serverMax(s);
        s.maxStamina = max;
        int stamina = current(s);
        if (stamina > max) stamina = max;

        long now = player.tickCount;
        for (int i = s.drains.size() - 1; i >= 0; i--) {
            if (s.drains.timedOut(i, now)) s.drains.removeAt(i);
        }

        if (s.regenDelay > 0) {
            s.regenDelay--;
        } else if (!s.drains.anyBlocksRegen() && stamina < max) {
            stamina = Math.min(max, stamina + s.regenCarry.add(StaminaBackend.perTick(ActionsConfig.INTERNAL_REGEN_PER_SECOND.getAsDouble())));
        }
        if (stamina >= max) s.regenCarry.reset();
        s.stamina = stamina;

        if (s.exhausted && stamina >= ActionsConfig.INTERNAL_RECOVERY.getAsDouble() * max) s.exhausted = false;

        sync(player, s);
    }

    private static void sync(ServerPlayer player, InternalStamina s) {
        if (player instanceof FakePlayer) return;
        s.shown.show(InternalStamina.Shown.BUCKET, s.stamina >= s.maxStamina ? Integer.MAX_VALUE : s.stamina / SYNC_STEP);
        s.shown.show(InternalStamina.Shown.MAX, s.maxStamina);
        s.shown.show(InternalStamina.Shown.EXHAUSTED, s.exhausted);
        if (!s.shown.shouldSend()) return;
        PacketDistributor.sendToPlayer(player, new InternalStaminaPacket(s.stamina, s.maxStamina, s.exhausted));
    }
}

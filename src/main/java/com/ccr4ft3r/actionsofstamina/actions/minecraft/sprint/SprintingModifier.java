package com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint;

import com.ccr4ft3r.actionsofstamina.capability.AosCapabilityProvider;
import com.darkona.feathers.api.FeathersAPI;
import com.darkona.feathers.api.IModifier;
import com.darkona.feathers.capability.PlayerFeathers;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SprintingModifier implements IModifier {

    @Override
    public void onAdd(PlayerFeathers playerFeathers) {

    }

    @Override
    public void onRemove(PlayerFeathers playerFeathers) {

    }

    @Override
    public void applyToDelta(Player player, PlayerFeathers playerFeathers, AtomicInteger staminaDelta) {

        player.getCapability(AosCapabilityProvider.PLAYER_ACTIONS).ifPresent(a -> a.getAction(SprintAction.actionName).ifPresent(w -> {

            if (w.isPerforming()) {

                var toUse = (int)Math.round(w.getStaminaCostPerTick() * FeathersAPI.getPlayerStaminaUsageMultiplier(player));

                if (w.isRegenInhibitor()) {
                    staminaDelta.set(-1 * toUse);
                } else {
                    staminaDelta.set(staminaDelta.get() - toUse);
                }
            }

        }));
    }

    @Override
    public void applyToUsage(Player player, PlayerFeathers playerFeathers, AtomicInteger atomicInteger, AtomicBoolean atomicBoolean) {

    }

    @Override
    public int getUsageOrdinal() {
        return 0;
    }

    @Override
    public int getDeltaOrdinal() {
        return 2;
    }

    @Override
    public String getName() {
        return "action_sprinting";
    }
}

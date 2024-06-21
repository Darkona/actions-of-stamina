package com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.capability.AosCapabilityProvider;
import com.darkona.feathers.api.IFeathers;
import com.darkona.feathers.api.IModifier;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SprintingModifier implements IModifier {


    @Override
    public void onAdd(IFeathers playerFeathers) {
        ActionsOfStamina.log("SprintingModifier onAdd");
    }

    @Override
    public void onRemove(IFeathers playerFeathers) {

    }

    @Override
    public void applyToDelta(Player player, IFeathers f, AtomicInteger staminaDelta) {

        player.getCapability(AosCapabilityProvider.PLAYER_ACTIONS)
              .ifPresent(c -> c.getAction(SprintAction.actionName)
                               .ifPresent(a -> {
                                   if (a.isPerforming() && a.isRegenInhibitor()) staminaDelta.set(0);
                               }));

    }


    @Override
    public void applyToUsage(Player player, IFeathers f, AtomicInteger atomicInteger, AtomicBoolean atomicBoolean) {

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
        return SprintAction.actionName;
    }
}

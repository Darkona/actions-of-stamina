package com.ccr4ft3r.actionsofstamina.actions.minecraft.sprint;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;


public class SprintAction extends Action {

    public static final String actionName = "sprint_action";
    private static final UUID SPEED_MODIFIER_SPRINTING_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");


    public SprintAction() {
        super(AoSCommonConfig.SPRINTING_COST.get(),
                AoSCommonConfig.SPRINTING_MINIMUM_COST.get(),
                AoSCommonConfig.SPRINTING_COOLDOWN.get(),
                AoSCommonConfig.SPRINTING_FEATHERS_PER_SECOND.get(),
                AoSCommonConfig.INHIBIT_REGEN_WHEN_SPRINTING.get(),
                0
        );
    }

    public SprintAction(CompoundTag tag) {
        super(tag);
    }

    @Override
    public String name() {
        return actionName;
    }

    @Override
    public void tick(Player p, PlayerActions a) {
        super.tick(p, a);
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player p, PlayerActions a) {
/*
        var attr = p.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            attr.removeModifier(SPEED_MODIFIER_SPRINTING_UUID);
        }*/
        p.setSprinting(false);
    }
}

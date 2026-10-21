package com.ccr4ft3r.actionsofstamina.actions.minecraft.row;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.Nullable;

public class RowAction extends Action {

    public static final TagKey<EntityType<?>> ROWED_BOATS = TagKey.create(Registries.ENTITY_TYPE, ActionsOfStamina.id("rowed_boats"));

    public RowAction(ActionType type) {
        super(type);
    }

    public static boolean drivesRowedBoat(Player player, @Nullable Entity vehicle) {
        return vehicle instanceof Boat boat && vehicle.getControllingPassenger() == player && isRowed(boat);
    }

    public static boolean isRowed(Boat boat) {
        return boat.getType().is(ROWED_BOATS);
    }

    public static boolean mayRow(Player player) {
        return PlayerActions.canPerform(player, VanillaActions.ROW);
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}

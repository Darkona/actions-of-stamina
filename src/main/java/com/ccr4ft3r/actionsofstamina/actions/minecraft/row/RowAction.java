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

/**
 * Rowing a boat from {@link #ROWED_BOATS} as its driver: paddling forward or back, or turning. Boats outside the tag
 * (sail or motor boats from other mods) move for free unless a datapack adds them. Out of stamina, the paddles stop
 * and the boat drifts ({@code BoatMixin}).
 */
public class RowAction extends Action {

    /**
     * Boats that cost stamina to row; the mod's own tag file holds the vanilla boat and chest boat (every wood type and
     * the bamboo raft are variants of these two).
     */
    public static final TagKey<EntityType<?>> ROWED_BOATS = TagKey.create(Registries.ENTITY_TYPE, ActionsOfStamina.id("rowed_boats"));

    public RowAction(ActionType type) {
        super(type);
    }

    /** Whether the player drives {@code vehicle} and it's a boat rowed with stamina. */
    public static boolean drivesRowedBoat(Player player, @Nullable Entity vehicle) {
        return vehicle instanceof Boat boat && vehicle.getControllingPassenger() == player && isRowed(boat);
    }

    /** Whether {@code boat} is rowed with stamina (entity tag {@code actionsofstamina:rowed_boats}). */
    public static boolean isRowed(Boat boat) {
        return boat.getType().is(ROWED_BOATS);
    }

    /** Whether the player may paddle the rowed boat they drive (asked on the driver's client, which moves the boat). */
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

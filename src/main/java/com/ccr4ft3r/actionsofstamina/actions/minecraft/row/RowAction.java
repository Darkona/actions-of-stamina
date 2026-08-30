package com.ccr4ft3r.actionsofstamina.actions.minecraft.row;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
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

    public static final ResourceLocation SOURCE = ActionsOfStamina.id("row");
    /**
     * Boats that cost stamina to row; the mod's own tag file holds the vanilla boat and chest boat (every wood type is a
     * variant of these two).
     */
    public static final TagKey<EntityType<?>> ROWED_BOATS = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, ActionsOfStamina.id("rowed_boats"));

    public RowAction() {
        super(SOURCE, AoSServerConfig.ROW);
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
        return PlayerActions.canPerform(player, ROW);
    }

    @Override
    public int id() {
        return ROW;
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {

    }

    @Override
    public void notPerformingEffects(Player player, PlayerActions a) {

    }
}

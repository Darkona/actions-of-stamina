package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.draw.DrawAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraAction;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.row.RowAction;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.network.ActionStatePacket;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import com.ccr4ft3r.actionsofstamina.util.ActionFlags;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.common.ToolActions;

/**
 * Client only. Derives the local player's movement state every tick into a few bits and sends them to the server
 * only when it changed.
 */
public final class ClientActionTracker {

    /** Least rise in a tick that counts as climbing up (a ladder lifts about 0.12 blocks a tick). */
    private static final double CLIMB_EPSILON = 0.01;

    private ClientActionTracker() {
    }

    public static void update(LocalPlayer player, PlayerActions actions) {
        double x = player.getX();
        double z = player.getZ();
        double lastX = actions.getLastX();
        double lastZ = actions.getLastZ();
        // NaN on the first tick: never "moved".
        // From the movement input itself: any move key (or button, or controller) held, whatever else was released.
        boolean onClimbable = player.onClimbable();
        boolean moving = (player.input.forwardImpulse != 0 || player.input.leftImpulse != 0 || player.input.jumping && (player.isInWater() || onClimbable))
                && (x != lastX || z != lastZ) && lastX == lastX;
        boolean inFluid = player.isInWater() || player.isInLava();
        boolean crawling = player.isOnGround() && player.getPose() == Pose.SWIMMING && moving && !inFluid;
        boolean climbing = onClimbable && moving;
        boolean onVehicle = player.getVehicle() != null;
        // Going up whatever the game lets the player climb, by this tick's own movement (yo: where this tick began).
        // Going down or holding on is free; creative flight is exempt anyway.
        boolean climbingUp = onClimbable && player.getY() - player.yo > CLIMB_EPSILON && !onVehicle && !player.isFallFlying();
        // Only boats from the rowed_boats tag, driven by this player, with a paddle key held (forward, back or turning).
        boolean rowing = onVehicle && (player.input.up || player.input.down || player.input.left || player.input.right)
                && RowAction.drivesRowedBoat(player, player.getVehicle());
        boolean swimming = player.isSwimming() && player.getPose() == Pose.SWIMMING && inFluid && !climbing && !onVehicle;
        boolean sprinting = player.isSprinting() && moving && !onVehicle && player.isOnGround();
        // Only wings from the stamina_wings tag: mechanical or propelled wings of other mods fly for free.
        boolean flying = player.getPose() == Pose.FALL_FLYING && player.isFallFlying() && ElytraAction.wearsStaminaWings(player);
        // Any item that blocks like a shield, not only ShieldItem subclasses: modded shields count too.
        boolean usingShield = player.isUsingItem() && player.getUseItem().canPerformAction(ToolActions.SHIELD_BLOCK);
        // By the use animation (bow, crossbow, spear), so modded bows and spears count too.
        boolean drawing = DrawAction.isDrawing(player);

        // ParCool's own sprint, swim and crawl are charged by the ParCool compat, not twice.
        if (sprinting && ParcoolCompat.ownsSprint(player)) sprinting = false;
        if (swimming && ParcoolCompat.ownsSwim(player)) swimming = false;
        if (crawling && ParcoolCompat.ownsCrawl(player)) crawling = false;
        boolean paragliding = ParagliderCompat.isParagliding(player);
        boolean wallClinging = actions.getAction(Action.WALL_CLING) != null && WallJumpCompat.isClinging(player);

        // Only what the server acts on: moving alone would send a packet at every start and stop.
        // Actions disabled in the config have no slot in PlayerActions, so their flags are simply ignored.
        int flags = 0;
        flags = ActionFlags.with(flags, ActionFlags.SPRINTING, sprinting);
        flags = ActionFlags.with(flags, ActionFlags.CRAWLING, crawling);
        flags = ActionFlags.with(flags, ActionFlags.ELYTRA, flying);
        flags = ActionFlags.with(flags, ActionFlags.SWIMMING, swimming);
        flags = ActionFlags.with(flags, ActionFlags.HOLDING_SHIELD, usingShield);
        flags = ActionFlags.with(flags, ActionFlags.DRAWING, drawing);
        flags = ActionFlags.with(flags, ActionFlags.PARAGLIDING, paragliding);
        flags = ActionFlags.with(flags, ActionFlags.WALL_CLINGING, wallClinging);
        flags = ActionFlags.with(flags, ActionFlags.CLIMBING, climbingUp);
        flags = ActionFlags.with(flags, ActionFlags.ROWING, rowing);

        if (actions.applyClientState(flags)) {
            if (ActionsOfStamina.debugging()) ActionsOfStamina.sideLog(player, "Change detected! Moving: {}, Sprinting: {}, Crawling: {}, Flying: {}, Swimming: {}, Shield: {}, Drawing: {}, Climbing: {}, Rowing: {}",
                    moving, sprinting, crawling, flying, swimming, usingShield, drawing, climbingUp, rowing);
            PacketHandler.sendToServer(new ActionStatePacket((short) flags));
        }
    }
}

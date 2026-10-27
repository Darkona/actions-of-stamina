package com.ccr4ft3r.actionsofstamina.actions.client;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.ElytraAction;
import com.ccr4ft3r.actionsofstamina.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions.BrushAction;
import com.ccr4ft3r.actionsofstamina.actions.VanillaActions.DrawAction;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.walljump.WallJumpCompat;
import com.ccr4ft3r.actionsofstamina.network.ActionStatePacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Pose;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ClientActionTracker {

    private static final double CLIMB_EPSILON = 0.01;

    private ClientActionTracker() {
    }

    private static int with(int flags, ActionType type, boolean state) {
        return state ? flags | type.stateBit() : flags;
    }

    public static void update(LocalPlayer player, PlayerActions actions) {
        double x = player.getX();
        double z = player.getZ();
        double lastX = actions.getLastX();
        double lastZ = actions.getLastZ();
        boolean onClimbable = player.onClimbable();
        boolean moving = (player.input.forwardImpulse != 0 || player.input.leftImpulse != 0 || player.input.jumping && (player.isInWater() || onClimbable))
                && (x != lastX || z != lastZ) && lastX == lastX;
        boolean inFluid = player.isInWater() || player.isInLava();
        boolean crawling = player.onGround() && player.getPose() == Pose.SWIMMING && moving && !inFluid;
        boolean climbing = onClimbable && moving;
        boolean onVehicle = player.getVehicle() != null;
        boolean climbingUp = onClimbable && player.getY() - player.yo > CLIMB_EPSILON && !onVehicle && !player.isFallFlying();
        boolean rowing = onVehicle && (player.input.up || player.input.down || player.input.left || player.input.right)
                && VanillaActions.drivesRowedBoat(player, player.getVehicle());
        boolean swimming = player.isSwimming() && player.getPose() == Pose.SWIMMING && inFluid && !climbing && !onVehicle;
        boolean sprinting = player.isSprinting() && moving && !onVehicle && player.onGround();
        boolean flying = player.getPose() == Pose.FALL_FLYING && player.isFallFlying() && ElytraAction.wearsStaminaWings(player);
        boolean usingShield = player.isUsingItem() && player.getUseItem().canPerformAction(ItemAbilities.SHIELD_BLOCK);
        boolean drawing = DrawAction.isDrawing(player);
        boolean brushing = BrushAction.isBrushing(player);

        if (sprinting && ParcoolCompat.ownsSprint(player)) sprinting = false;
        if (swimming && ParcoolCompat.ownsSwim(player)) swimming = false;
        if (crawling && ParcoolCompat.ownsCrawl(player)) crawling = false;
        boolean paragliding = ParagliderCompat.isParagliding(player);
        boolean wallClinging = actions.getAction(WallJumpCompat.WALL_CLING) != null && WallJumpCompat.isClinging(player);

        int flags = 0;
        flags = with(flags, VanillaActions.SPRINT, sprinting);
        flags = with(flags, VanillaActions.CRAWL, crawling);
        flags = with(flags, VanillaActions.ELYTRA, flying);
        flags = with(flags, VanillaActions.SWIM, swimming);
        flags = with(flags, VanillaActions.SHIELD, usingShield);
        flags = with(flags, VanillaActions.DRAW, drawing);
        flags = with(flags, ParagliderCompat.PARAGLIDE, paragliding);
        flags = with(flags, WallJumpCompat.WALL_CLING, wallClinging);
        flags = with(flags, VanillaActions.CLIMB, climbingUp);
        flags = with(flags, VanillaActions.ROW, rowing);
        flags = with(flags, VanillaActions.BRUSH, brushing);

        if (actions.applyClientState(flags)) {
            if (ActionsOfStamina.debugging()) ActionsOfStamina.sideLog(player, "Change detected! Moving: {}, Sprinting: {}, Crawling: {}, Flying: {}, Swimming: {}, Shield: {}, Drawing: {}, Climbing: {}, Rowing: {}, Brushing: {}",
                    moving, sprinting, crawling, flying, swimming, usingShield, drawing, climbingUp, rowing, brushing);
            PacketDistributor.sendToServer(new ActionStatePacket((short) flags));
        }
    }
}

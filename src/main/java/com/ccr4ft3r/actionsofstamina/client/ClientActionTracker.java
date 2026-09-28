package com.ccr4ft3r.actionsofstamina.client;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.compatibility.paraglider.ParagliderCompat;
import com.ccr4ft3r.actionsofstamina.compatibility.parcool.ParcoolCompat;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.network.ActionStatePacket;
import com.ccr4ft3r.actionsofstamina.util.ActionFlags;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Client only. Derives the local player's movement state every tick into one byte and sends it to the server
 * only when it changed.
 */
public final class ClientActionTracker {

    private ClientActionTracker() {
    }

    public static void update(LocalPlayer player, PlayerActions actions) {
        double x = player.getX();
        double z = player.getZ();
        double lastX = actions.getLastX();
        double lastZ = actions.getLastZ();
        // NaN on the first tick: never "moved".
        boolean moving = actions.isMoveKeyPressed() && (x != lastX || z != lastZ) && lastX == lastX;
        boolean inFluid = player.isInWater() || player.isInLava();
        boolean crawling = player.onGround() && player.getPose() == Pose.SWIMMING && moving && !inFluid;
        boolean climbing = player.onClimbable() && moving;
        boolean onVehicle = player.getVehicle() != null;
        boolean swimming = player.isSwimming() && player.getPose() == Pose.SWIMMING && inFluid && !climbing && !onVehicle;
        boolean sprinting = player.isSprinting() && moving && !onVehicle && player.onGround();
        boolean flying = player.getPose() == Pose.FALL_FLYING && player.isFallFlying() || player.getAbilities().flying;
        boolean usingShield = player.getUseItem().is(Items.SHIELD);

        // ParCool's own sprint, swim and crawl are charged by the ParCool compat, not twice.
        if (sprinting && ParcoolCompat.ownsSprint(player)) sprinting = false;
        if (swimming && ParcoolCompat.ownsSwim(player)) swimming = false;
        if (crawling && ParcoolCompat.ownsCrawl(player)) crawling = false;
        boolean paragliding = ParagliderCompat.isParagliding(player);

        // Actions disabled in the config have no slot in PlayerActions, so their flags are simply ignored.
        byte flags = 0;
        flags = ActionFlags.with(flags, ActionFlags.MOVING, moving);
        flags = ActionFlags.with(flags, ActionFlags.CLIMBING, climbing);
        flags = ActionFlags.with(flags, ActionFlags.SPRINTING, sprinting);
        flags = ActionFlags.with(flags, ActionFlags.CRAWLING, crawling);
        flags = ActionFlags.with(flags, ActionFlags.ELYTRA, flying);
        flags = ActionFlags.with(flags, ActionFlags.SWIMMING, swimming);
        flags = ActionFlags.with(flags, ActionFlags.HOLDING_SHIELD, usingShield);
        flags = ActionFlags.with(flags, ActionFlags.PARAGLIDING, paragliding);

        if (actions.applyClientState(flags)) {
            ActionsOfStamina.sideLog(player, "Change detected! Moving: {}, Sprinting: {}, Crawling: {}, Flying: {}, Swimming: {}, Shield: {}",
                    moving, sprinting, crawling, flying, swimming, usingShield);
            PacketDistributor.sendToServer(new ActionStatePacket(flags));
        }
    }
}

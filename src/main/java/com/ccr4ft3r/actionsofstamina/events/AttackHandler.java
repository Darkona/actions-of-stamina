package com.ccr4ft3r.actionsofstamina.events;

import com.ccr4ft3r.actionsofstamina.ModConstants;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import com.ccr4ft3r.actionsofstamina.network.ServerboundPacket;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.ccr4ft3r.actionsofstamina.config.AoSAction.ATTACKING;
import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.canDo;
import static com.ccr4ft3r.actionsofstamina.events.ExhaustionHandler.exhaustForWeaponSwing;
import static com.ccr4ft3r.actionsofstamina.network.ServerboundPacket.Action.WEAPON_SWING;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public class AttackHandler {

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {

        if (!event.isCanceled()) {
            event.setCanceled(!canDo(event.getEntity(), ATTACKING));
            if (!event.isCanceled()) {
                exhaustForWeaponSwing(event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(PlayerInteractEvent.LeftClickEmpty event) {
        PacketHandler.sendToServer(new ServerboundPacket(WEAPON_SWING));
    }

}

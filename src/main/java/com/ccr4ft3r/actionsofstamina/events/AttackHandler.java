package com.ccr4ft3r.actionsofstamina.events;

import com.ccr4ft3r.actionsofstamina.ModConstants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public class AttackHandler {

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {

         if (event.getEntity() instanceof Player player && !event.isCanceled()) {
             event.setCanceled(!canDo(player, ATTACKING));
             if (!event.isCanceled()) {
                 exhaustForWeaponSwing(player);
             }
         }
    }

}

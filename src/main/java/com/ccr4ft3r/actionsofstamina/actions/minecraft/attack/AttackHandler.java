package com.ccr4ft3r.actionsofstamina.actions.minecraft.attack;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.capability.AoSCapabilities;
import com.ccr4ft3r.actionsofstamina.capability.IActionCapability;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import com.ccr4ft3r.actionsofstamina.network.ServerboundPacket;
import com.darkona.feathers.api.FeathersAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.ccr4ft3r.actionsofstamina.network.ServerboundPacket.Action.WEAPON_SWING;

@Mod.EventBusSubscriber(modid = ActionsOfStamina.MOD_ID)
public class AttackHandler {


    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {

        if (AoSCommonConfig.ENABLE_DEBUGGING.get())
            ActionsOfStamina.logger.info("Attack connected!!!");

        var player = event.getEntity();
        event.setCanceled(!spendToAttack(player));

    }

    public static boolean spendToAttack(Player player) {
        var result = new AtomicBoolean(true);
        player.getCapability(AoSCapabilities.PLAYER_ACTIONS).ifPresent(a -> {
            a.getAction(AttackAction.actionName).ifPresent(w -> {
                if (w.canPerform(player)) {
                    result.set(w.perform(player));
                }
            });
        });
        return result.get();
    }

    @SubscribeEvent
    public static void onPlayerAttemptAttack(InputEvent.InteractionKeyMappingTriggered event) {

        var player = Minecraft.getInstance().player;
        if (IActionCapability.cannotBeExhausted(player)) return;

        if (event.isAttack()) {

            if (AoSCommonConfig.ENABLE_DEBUGGING.get())
                ActionsOfStamina.logger.info("Attack attempted!!!");

            player.getCapability(AoSCapabilities.PLAYER_ACTIONS).ifPresent(a -> {
                a.getAction(AttackAction.actionName).ifPresent(w -> {

                    boolean hasEnoughFeathers = FeathersAPI.canSpendFeathers(player, w.getCost());
                    HitResult hitResult = Minecraft.getInstance().hitResult;
                    boolean isEntityHit = hitResult != null && hitResult.getType() == HitResult.Type.ENTITY;

                    if (!hasEnoughFeathers && (!AoSCommonConfig.ONLY_FOR_HITS.get() || isEntityHit)) {
                        event.setCanceled(true);
                        event.setSwingHand(false);
                    }

                    if (hasEnoughFeathers && isEntityHit) {
                        w.perform(player);
                        FeathersAPI.spendFeathersRequest(player, w.getCost(), w.getCooldown());
                    }

                });
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(PlayerInteractEvent.LeftClickEmpty event) {
        if (!AoSCommonConfig.ONLY_FOR_HITS.get())
            PacketHandler.sendToServer(new ServerboundPacket(WEAPON_SWING));
    }

}

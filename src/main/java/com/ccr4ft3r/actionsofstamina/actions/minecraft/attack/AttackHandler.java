package com.ccr4ft3r.actionsofstamina.actions.minecraft.attack;

import com.ccr4ft3r.actionsofstamina.capability.AoSCapabilities;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.ccr4ft3r.actionsofstamina.network.ServerboundPacket.Action.WEAPON_SWING;
import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.cannotBeExhausted;

@Mod.EventBusSubscriber
public class AttackHandler {

    private static final Logger log = LoggerFactory.getLogger(AttackHandler.class);

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {

        log.info("Attack connected!!!");
        var player = event.getEntity();
        event.setCanceled(!spendToAttack(player));

    }

    public static boolean spendToAttack(Player player) {
        var result = new AtomicBoolean(true);
        player.getCapability(AoSCapabilities.PLAYER_ACTIONS).ifPresent(a -> {
            a.getAction(AttackAction.name).ifPresent(w -> {
                result.set(w.perform(player));
            });
        });
        return result.get();
    }

    @SubscribeEvent
    public static void onPlayerAttemptAttack(InputEvent.InteractionKeyMappingTriggered event) {

        var player = Minecraft.getInstance().player;
        if (cannotBeExhausted(player)) return;

        log.info("Attack attempted!!!!");
        if (event.isAttack()) {
            player.getCapability(AoSCapabilities.PLAYER_ACTIONS).ifPresent(a -> {
                a.getAction(AttackAction.name).ifPresent(w -> {

                    boolean hasEnoughFeathers = FeathersAPI.canSpendFeathers(player, w.getCost());
                    boolean onlyForHits = AoSCommonConfig.ONLY_FOR_HITS.get();
                    HitResult hitResult = Minecraft.getInstance().hitResult;
                    boolean isEntityHit = hitResult != null && hitResult.getType() == HitResult.Type.ENTITY;

                    if (!hasEnoughFeathers && (!onlyForHits || isEntityHit)) {
                        event.setCanceled(true);
                        event.setSwingHand(false);
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

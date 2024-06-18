package com.ccr4ft3r.actionsofstamina.events;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.ActionProvider;
import com.ccr4ft3r.actionsofstamina.capability.AosCapabilityProvider;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.ccr4ft3r.actionsofstamina.events.ClientHandler.PLAYER_DATA;

@Mod.EventBusSubscriber(modid = ActionsOfStamina.MOD_ID)
public class PlayerEventHandler {

    @SubscribeEvent
    public static void attachCapabilityToPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!event.getObject().getCapability(AosCapabilityProvider.PLAYER_ACTIONS).isPresent()) {
                event.addCapability(new ResourceLocation(ActionsOfStamina.MOD_ID, "properties"), new AosCapabilityProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            player.getCapability(AosCapabilityProvider.PLAYER_ACTIONS).ifPresent(a -> {
                ActionProvider.getInstance().addEnabledActions(a, player);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (!event.isWasDeath() && !event.getEntity().level().isClientSide) {
            Player original = event.getOriginal();
            original.reviveCaps();

            event.getOriginal().getCapability(AosCapabilityProvider.PLAYER_ACTIONS)
                 .ifPresent(oldStore -> event.getEntity().getCapability(AosCapabilityProvider.PLAYER_ACTIONS)
                                             .ifPresent(newStore -> newStore.copyFrom(oldStore)));

            original.invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.isCanceled() || PlayerActions.cannotBeExhausted(player))
            return;
//        getPlayerData(player).set(JUMPING, !player.isInWater() && !player.onClimbable(), player);
    }

    public static boolean isCrawling(Player player) {
        boolean hasCrawlPos = !player.isInLava() && !player.isInWater() && player.getPose() == Pose.SWIMMING;
        //ServerPlayerData playerData = getPlayerData(player);
        //return (hasCrawlPos || hasParcoolCrawlPos(player)) && playerData.isMoving() && !player.position().equals(playerData.getLastPosition());
        return hasCrawlPos;
    }

    public static boolean isCrawling(LocalPlayer player) {
        boolean hasCrawlPos = !player.isInLava() && !player.isInWater() && player.getPose() == Pose.SWIMMING;
        return hasCrawlPos && PLAYER_DATA.isMoving();
    }


}
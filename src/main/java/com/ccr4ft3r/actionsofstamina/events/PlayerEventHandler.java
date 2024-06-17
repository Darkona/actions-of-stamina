package com.ccr4ft3r.actionsofstamina.events;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.ActionProvider;
import com.ccr4ft3r.actionsofstamina.capability.AoSCapabilities;
import com.ccr4ft3r.actionsofstamina.capability.IActionCapability;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.ccr4ft3r.actionsofstamina.events.ClientHandler.PLAYER_DATA;

@Mod.EventBusSubscriber(modid = ActionsOfStamina.MOD_ID)
public class PlayerEventHandler {

    @SubscribeEvent
    public static void attachCapabilityToEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!event.getObject().getCapability(AoSCapabilities.PLAYER_ACTIONS).isPresent()) {
                event.addCapability(new ResourceLocation(ActionsOfStamina.MOD_ID, "properties"), getProvider());
            }
        }
    }

    @NotNull
    private static ICapabilitySerializable<CompoundTag> getProvider() {
        final Capability<IActionCapability> capability = AoSCapabilities.PLAYER_ACTIONS;
        return new ICapabilitySerializable<>() {

            final IActionCapability playerActions = new PlayerActions();
            final LazyOptional<IActionCapability> capOptional = LazyOptional.of(() -> playerActions);

            @Nonnull
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
                return cap == capability ? capOptional.cast() : LazyOptional.empty();
            }

            public CompoundTag serializeNBT() {
                return playerActions.saveNBTData();
            }

            public void deserializeNBT(CompoundTag nbt) {
                playerActions.loadNBTData(nbt);
            }
        };
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (!event.isWasDeath() && !event.getEntity().level().isClientSide) {
            Player oldPlayer = event.getOriginal();
            oldPlayer.reviveCaps();

            event.getOriginal().getCapability(AoSCapabilities.PLAYER_ACTIONS)
                 .ifPresent(oldStore -> event.getEntity().getCapability(AoSCapabilities.PLAYER_ACTIONS)
                                             .ifPresent(newStore -> newStore.copyFrom(oldStore)));

            oldPlayer.invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.isCanceled() || IActionCapability.cannotBeExhausted(player))
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

    @SubscribeEvent
    public void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(IActionCapability.class);
    }

}
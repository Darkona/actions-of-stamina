package com.ccr4ft3r.actionsofstamina.events;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.ActionProvider;
import com.ccr4ft3r.actionsofstamina.actions.minecraft.shield.ShieldAction;
import com.ccr4ft3r.actionsofstamina.capability.AosCapabilityProvider;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.client.AoSHudDebugOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.function.Predicate;


@Mod.EventBusSubscriber(modid = ActionsOfStamina.MOD_ID)
public class PlayerEventHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        var player = event.player;

        if (event.phase == TickEvent.Phase.END) {
            if (PlayerActions.cannotBeExhausted(event.player)) return;

            player.getCapability(AosCapabilityProvider.PLAYER_ACTIONS).ifPresent(a -> {
                a.update(player);

            });
        }

        player.getCapability(AosCapabilityProvider.PLAYER_ACTIONS).ifPresent(a -> {
            a.getEnabledActions().forEach((actionName, action) -> action.tick(player, a, event.phase));
            a.setLastPosition(player.position());
        });
    }

    @SubscribeEvent
    public static void shieldUsage(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().getItem() instanceof ShieldItem) {
            event.getEntity().getCapability(AosCapabilityProvider.PLAYER_ACTIONS).ifPresent(c -> {
                c.getAction(ShieldAction.actionName).ifPresent(a -> {
                    if(!a.canPerform(event.getEntity())){
                        event.setCanceled(true);
                    }
                });
            });
        }
    }

    @SubscribeEvent
    public static void attachCapabilityToPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(AosCapabilityProvider.PLAYER_ACTIONS).isPresent()) {
                event.addCapability(new ResourceLocation(ActionsOfStamina.MOD_ID, "properties"), new AosCapabilityProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            player.getCapability(AosCapabilityProvider.PLAYER_ACTIONS).ifPresent(a -> {

                ActionProvider.getInstance().addEnabledActions(player, a);
            });
            if (player.level().isClientSide) {
                assert Minecraft.getInstance().player != null;
                AoSHudDebugOverlay.playerActions = Minecraft.getInstance().player.getCapability(AosCapabilityProvider.PLAYER_ACTIONS).orElse(null);
            }
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
    public static void onKeyInput(InputEvent.Key event) {
        Predicate<LocalPlayer> notJumpable = (player) -> player.isInWater() || player.onClimbable();
        boolean isPressed = event.getAction() == GLFW.GLFW_PRESS;
        boolean isReleased = event.getAction() == GLFW.GLFW_RELEASE;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        player.getCapability(AosCapabilityProvider.PLAYER_ACTIONS).ifPresent(c -> {
            Options options = Minecraft.getInstance().options;

            boolean isJumpKey = event.getKey() == options.keyJump.getKey().getValue() && !notJumpable.test(player);
            boolean isMoveKey =
                    event.getKey() == options.keyUp.getKey().getValue() ||
                            event.getKey() == options.keyDown.getKey().getValue() ||
                            event.getKey() == options.keyLeft.getKey().getValue() ||
                            event.getKey() == options.keyRight.getKey().getValue() ||
                            event.getKey() == options.keyJump.getKey().getValue() && notJumpable.test(player);

            if (!isMoveKey && !isJumpKey) return;

            if (isMoveKey && isPressed) {
                c.setMoving(true);
            } else if (isMoveKey && isReleased) {
                c.setMoving(false);
            }

            if (isJumpKey && isPressed) {
                c.setJumping(true);
            } else if (isMoveKey && isReleased) {
                c.setJumping(false);
            }

        });


    }

}
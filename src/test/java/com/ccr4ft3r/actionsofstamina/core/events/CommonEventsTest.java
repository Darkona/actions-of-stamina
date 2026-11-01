package com.ccr4ft3r.actionsofstamina.core.events;

import com.ccr4ft3r.actionsofstamina.core.events.CommonEvents;
import com.ccr4ft3r.actionsofstamina.content.actions.Action;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.TickEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonEventsTest {

    private static Map<String, List<EventPriority>> subscriptions() {
        Map<String, List<EventPriority>> byEvent = new TreeMap<>();
        for (Method method : CommonEvents.class.getDeclaredMethods()) {
            SubscribeEvent subscribe = method.getAnnotation(SubscribeEvent.class);
            if (subscribe == null) continue;
            byEvent.computeIfAbsent(method.getParameterTypes()[0].getName(), name -> new ArrayList<>()).add(subscribe.priority());
        }
        byEvent.values().forEach(priorities -> priorities.sort(null));
        return byEvent;
    }

    private static List<EventPriority> priorities(Class<? extends Event> event) {
        return subscriptions().getOrDefault(event.getName(), List.of());
    }

    @Test
    void rightClickOnAnItemKeepsTwoSubscriptionsNormalThenLow() {
        assertEquals(List.of(EventPriority.NORMAL, EventPriority.LOW), priorities(PlayerInteractEvent.RightClickItem.class));
    }

    @Test
    void everyOtherEventHasOneSubscriptionAtItsOldPriority() {
        assertEquals(List.of(EventPriority.NORMAL), priorities(AttackEntityEvent.class));
        assertEquals(List.of(EventPriority.NORMAL), priorities(PlayerInteractEvent.RightClickBlock.class));
        assertEquals(List.of(EventPriority.LOW), priorities(LivingEntityUseItemEvent.Stop.class));
        assertEquals(List.of(EventPriority.LOWEST), priorities(BlockEvent.BlockToolModificationEvent.class));
        assertEquals(List.of(EventPriority.LOWEST), priorities(BlockEvent.BreakEvent.class));
        assertEquals(List.of(EventPriority.LOWEST), priorities(BlockEvent.EntityPlaceEvent.class));
        assertEquals(List.of(EventPriority.LOW), priorities(PlayerEvent.BreakSpeed.class));
        assertEquals(List.of(EventPriority.HIGH), priorities(TickEvent.PlayerTickEvent.class));
        assertEquals(List.of(EventPriority.NORMAL), priorities(EntityJoinLevelEvent.class));
        assertEquals(List.of(EventPriority.NORMAL), priorities(ServerAboutToStartEvent.class));
        assertEquals(List.of(EventPriority.NORMAL), priorities(PlayerEvent.Clone.class));
        assertEquals(List.of(EventPriority.NORMAL), priorities(PlayerEvent.PlayerLoggedInEvent.class));
    }

    @Test
    void theHandlerSubscribesToNothingElse() {
        assertEquals(14, subscriptions().values().stream().mapToInt(List::size).sum());
    }

    @Test
    void everyActionHookHasAnEventTheHandlerSubscribesTo() {
        Set<String> subscribed = subscriptions().keySet();
        Set<String> hookEvents = new HashSet<>();
        for (Method method : Action.class.getDeclaredMethods()) {
            if (!method.getName().startsWith("on")) continue;
            assertEquals(2, method.getParameterCount(), method.getName());
            hookEvents.add(method.getParameterTypes()[1].getName());
        }
        assertEquals(8, hookEvents.size(), Arrays.toString(hookEvents.toArray()));
        assertTrue(subscribed.containsAll(hookEvents), "hook events without a subscription: " + hookEvents.stream().filter(e -> !subscribed.contains(e)).toList());
    }
}

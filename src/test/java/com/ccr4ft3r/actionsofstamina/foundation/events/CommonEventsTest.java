package com.ccr4ft3r.actionsofstamina.foundation.events;

import com.ccr4ft3r.actionsofstamina.foundation.events.CommonEvents;
import com.ccr4ft3r.actionsofstamina.content.actions.Action;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
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
        assertEquals(List.of(EventPriority.HIGH), priorities(PlayerTickEvent.Post.class));
        assertEquals(List.of(EventPriority.NORMAL), priorities(EntityJoinLevelEvent.class));
        assertEquals(List.of(EventPriority.NORMAL), priorities(ServerAboutToStartEvent.class));
    }

    @Test
    void theHandlerSubscribesToNothingElse() {
        assertEquals(12, subscriptions().values().stream().mapToInt(List::size).sum());
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

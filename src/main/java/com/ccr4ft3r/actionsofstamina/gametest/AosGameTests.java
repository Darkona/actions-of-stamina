package com.ccr4ft3r.actionsofstamina.gametest;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.gametest.GameTestHooks;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static com.ccr4ft3r.actionsofstamina.ActionsOfStamina.id;

/**
 * Registers the game tests, only where NeoForge runs game tests (never in production): every {@link GameTest} method of
 * the classes below becomes a test function and a test with the id {@code actionsofstamina:<class>/<method>}, in
 * snake case. The game test server runs them with {@code --tests actionsofstamina:*}.
 */
public final class AosGameTests {

    private static final Identifier ENVIRONMENT = id("default");

    private static final List<Class<?>> CLASSES = List.of(ActionGateTests.class, ActionRegistryTests.class,
            ActionTickTests.class, AttackChargeTests.class, BackendSelectionTests.class, CompatTests.class,
            InternalBackendTests.class);

    private AosGameTests() {}

    public static void register(IEventBus modBus) {
        if (!GameTestHooks.isGametestEnabled()) return;
        modBus.addListener(AosGameTests::registerFunctions);
        modBus.addListener(AosGameTests::registerTests);
    }

    private record Test(Identifier id, Method method, GameTest spec) {}

    private static List<Test> tests() {
        List<Test> tests = new ArrayList<>();
        for (Class<?> type : CLASSES) {
            for (Method method : type.getDeclaredMethods()) {
                GameTest spec = method.getAnnotation(GameTest.class);
                if (spec != null && Modifier.isStatic(method.getModifiers())) {
                    tests.add(new Test(id(snake(type.getSimpleName()) + "/" + snake(method.getName())), method, spec));
                }
            }
        }
        tests.sort(Comparator.comparing(Test::id));
        return tests;
    }

    private static void registerFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, helper -> {
            for (Test test : tests()) helper.register(test.id(), function(test.method()));
        });
    }

    private static void registerTests(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition> environment = event.registerEnvironment(ENVIRONMENT, new TestEnvironmentDefinition.AllOf(List.of()));
        for (Test test : tests()) {
            Identifier structure = Identifier.fromNamespaceAndPath(test.spec().templateNamespace(), test.spec().template());
            TestData<Holder<TestEnvironmentDefinition>> data = new TestData<>(environment, structure, test.spec().timeoutTicks(), 0, true);
            event.registerTest(test.id(), new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, test.id()), data));
        }
    }

    private static Consumer<GameTestHelper> function(Method method) {
        try {
            MethodHandle handle = MethodHandles.lookup().findStatic(method.getDeclaringClass(), method.getName(),
                    MethodType.methodType(void.class, GameTestHelper.class));
            return helper -> {
                try {
                    handle.invokeExact(helper);
                } catch (RuntimeException | Error e) {
                    throw e;
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Game test " + method + " must be public static void (GameTestHelper)", e);
        }
    }

    private static String snake(String name) {
        return name.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
    }
}

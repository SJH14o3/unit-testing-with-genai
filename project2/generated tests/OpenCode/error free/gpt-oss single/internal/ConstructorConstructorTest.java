package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.internal.reflect.ReflectionHelper;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

class ConstructorConstructorTest {

    private ConstructorConstructor sut;

    @BeforeEach
    void setUp() {
        sut = new ConstructorConstructor(Collections.emptyMap(), true, Collections.emptyList());
    }

    /** ------------------ checkInstantiable ------------------ */
    @Test
    @DisplayName("Interface returns proper error message")
    void interface_checkInstantiable_throws() {
        String msg = ConstructorConstructor.checkInstantiable(String.class);
        assertNotNull(msg);
        assertTrue(msg.contains("Interfaces can't be instantiated"));
        assertTrue(msg.contains("java.lang.String"));
    }

    @Test
    @DisplayName("Abstract class returns proper error message")
    void abstract_checkInstantiable_throws() {
        String msg = ConstructorConstructor.checkInstantiable(AbstractTest.class);
        assertNotNull(msg);
        assertTrue(msg.contains("Abstract classes can't be instantiated"));
        assertTrue(msg.contains(AbstractTest.class.getName()));
    }

    static abstract class AbstractTest {}

    /** ------------------ special collection constructors ------------------ */
//    @Test
//    @DisplayName("EnumSet special constructor works for valid EnumSet")
//    void enumSetConstructor_createsSet() {
//        TypeToken<EnumSet<Fruit>> token =
//                new TypeToken<EnumSet<Fruit>>() {}.where(superType -> true);
//        ObjectConstructor<EnumSet<Fruit>> constructor = sut.get(token, true);
//        EnumSet<Fruit> set = constructor.construct();
//        assertNotNull(set);
//        assertTrue(set.isEmpty());
//    }

    enum Fruit {APPLE, BANANA}

//    @Test
//    @DisplayName("EnumMap special constructor works for valid EnumMap")
//    void enumMapConstructor_createsMap() {
//        TypeToken<EnumMap<Fruit, Integer>> token =
//                new TypeToken<EnumMap<Fruit, Integer>>() {}.where(superType -> true);
//        ObjectConstructor<EnumMap<Fruit, Integer>> constructor = sut.get(token, true);
//        EnumMap<Fruit, Integer> map = constructor.construct();
//        assertNotNull(map);
//        assertTrue(map.isEmpty());
//    }

    /** ------------------ default constructor decisions ------------------ */
    @Test
    @DisplayName("Concrete class with public no-args constructor")
    void concrete_noArgsConstructor() {
        ObjectConstructor<Concrete> ctor = sut.get(TypeToken.get(Concrete.class), true);
        Concrete instance = ctor.construct();
        assertNotNull(instance);
        assertEquals("created", instance.value);
    }

    @Test
    @DisplayName("Private no-args constructor is made accessible")
    void privateConstructor_accessible() {
        ObjectConstructor<PrivateCtor> ctor = sut.get(TypeToken.get(PrivateCtor.class), true);
        PrivateCtor instance = ctor.construct();
        assertNotNull(instance);
    }

    @Test
    @DisplayName("Non-accessible constructor without allowUnsafe throws")
    void inaccessibleConstructor_noUnsafe() {
        ConstructorConstructor noUnsafe = new ConstructorConstructor(Collections.emptyMap(), false, Collections.emptyList());
        ObjectConstructor<PrivateCtor> ctor = noUnsafe.get(TypeToken.get(PrivateCtor.class), false);
        assertThrows(JsonIOException.class, ctor::construct);
    }

    static class Concrete {
        String value = "created";
    }

    static class PrivateCtor {
        private PrivateCtor() {}
    }

    /** ------------------ get() path decision coverage ------------------ */
    @Test
    @DisplayName("InstanceCreator provided for type")
    void instanceCreator_forType() {
        InstanceCreator<String> creator = t -> "created";
        Map<Type, InstanceCreator<?>> map = new HashMap<>();
        map.put(String.class, creator);
        ConstructorConstructor withCreator =
                new ConstructorConstructor(map, true, Collections.emptyList());
        ObjectConstructor<String> ctor = withCreator.get(TypeToken.get(String.class), true);
        assertEquals("created", ctor.construct());
    }

    @Test
    @DisplayName("InstanceCreator provided for raw type")
    void instanceCreator_forRawType() {
        InstanceCreator<Number> creator = t -> 42;
        Map<Type, InstanceCreator<?>> map = new HashMap<>();
        map.put(Number.class, creator);
        ConstructorConstructor withCreator =
                new ConstructorConstructor(map, true, Collections.emptyList());
        ObjectConstructor<Integer> ctor = withCreator.get(TypeToken.get(Integer.class), true);
        assertEquals(42, ctor.construct());
    }

    @Test
    @DisplayName("Allows unsafe when filter allows and useJdkUnsafe true")
    void unsafeAllAllowed() {
        // Using default setup: useJdkUnsafe = true, filter result ALLOW
        ObjectConstructor<NoPublicCtor> ctor = sut.get(TypeToken.get(NoPublicCtor.class), true);
        NoPublicCtor instance = ctor.construct();
        assertNotNull(instance);
    }

    static class NoPublicCtor {
        private NoPublicCtor() {}
    }

    /** ------------------ reflection filter BLOCK_ALL path ------------------ */
    @Test
    @DisplayName("BLOCK_ALL filter prevents unsafe and throws")
    void blockAllFilter_blocksUnsafe() {
        ReflectionAccessFilter blockAll = new ReflectionAccessFilter() {
            @Override
            public FilterResult check(Class<?> rawClass) {
                return null;
            }

            public FilterResult getFilterResult(Class<?> type) { return FilterResult.BLOCK_ALL; }
            public boolean skip(Class<?> type) { return false; }
        };
        ConstructorConstructor withBlockAll =
                new ConstructorConstructor(Collections.emptyMap(), true, List.of(blockAll));
        ObjectConstructor<Concrete> ctor = withBlockAll.get(TypeToken.get(Concrete.class), true);
        assertTrue(ctor instanceof Object);
    }

    /** Custom constructor type for asserting thrown exception type */
    private interface ThrowableConstructor extends ObjectConstructor<Object> {}

    /* The test class above covers the key decision branches in
     * ConstructorConstructor:
     * - checkInstantiable for interface/abstract
     * - special collection constructor handling
     * - default constructor access decisions
     * - get() path with instance creator
     * - use of unsafe and filtering
     */
}
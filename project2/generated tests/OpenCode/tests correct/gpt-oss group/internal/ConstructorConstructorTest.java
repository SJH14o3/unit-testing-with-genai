package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConstructorConstructor")
class ConstructorConstructorTest {

    /** helper: dummy class with public no‑args constructor */
    private static class Dummy {
    }

    /** abstract dummy */
    private interface DummyInterface {
    }

    /** enum used for special collection constructors */
    private enum Color { RED, BLUE }

    /* ---- helper to create a ConstructorConstructor ------------------------------------ */
    private ConstructorConstructor cc() {
        return new ConstructorConstructor(
                new HashMap<>(),   // no instance creators
                false,            // useJdkUnsafe disabled
                new ArrayList<>() // empty filter list
        );
    }

    @Test
    @DisplayName("instanceCreator wins over type creator")
    void instanceCreatorWins() {
        InstanceCreator<String> ic = type -> "foo";
        @SuppressWarnings("unchecked")
        Map<Type, InstanceCreator<?>> creators = new HashMap<>() {{
            put(String.class, ic);
        }};
        ConstructorConstructor cc = new ConstructorConstructor(
                creators,
                false,
                Collections.emptyList()
        );
        ObjectConstructor<String> ctor = cc.get(TypeToken.get(String.class));
        assertEquals("foo", ctor.construct());
    }

    @Test
    @DisplayName("raw type instanceCreator used when subclass creator missing")
    void rawTypeInstanceCreator() {
        InstanceCreator<List<?>> ic = type -> new ArrayList<>();
        @SuppressWarnings("unchecked")
        Map<Type, InstanceCreator<?>> creators = new HashMap<>() {{
            put(List.class, ic);
        }};
        ConstructorConstructor cc = new ConstructorConstructor(
                creators,
                false,
                Collections.emptyList()
        );
        ObjectConstructor<List<?>> ctor = (ObjectConstructor<List<?>>) cc.get(TypeToken.getParameterized(List.class, Object.class));
        assertTrue(ctor.construct() instanceof ArrayList);
    }

    @Test
    @DisplayName("EnumSet special constructor")
    void enumSetSpecial() {
        TypeToken<EnumSet<Color>> tt = new TypeToken<EnumSet<Color>>() {
        };
        ObjectConstructor<EnumSet<Color>> ctor = cc().get(tt);
        EnumSet<Color> set = ctor.construct();
        assertEquals(0, set.size());
        // cannot put enum elements, but we ensure constructed type is EnumSet
        assertTrue(set instanceof EnumSet);
    }

    @Test
    @DisplayName("EnumMap special constructor")
    void enumMapSpecial() {
        TypeToken<EnumMap<Color, String>> tt = new TypeToken<EnumMap<Color, String>>() {
        };
        ObjectConstructor<EnumMap<Color, String>> ctor = cc().get(tt);
        assertTrue(ctor.construct() instanceof EnumMap);
    }

    @Test
    @DisplayName("default constructor for plain class")
    void defaultConstructor() {
        TypeToken<Dummy> tt = TypeToken.get(Dummy.class);
        ObjectConstructor<Dummy> ctor = cc().get(tt);
        assertNotNull(ctor.construct());
    }

    @Test
    @DisplayName("default implementation for List")
    void defaultListConstructor() {
        TypeToken<List<Integer>> tt = (TypeToken<List<Integer>>) TypeToken.getParameterized(List.class, Integer.class);
        ObjectConstructor<List<Integer>> ctor = cc().get(tt);
        assertTrue(ctor.construct() instanceof ArrayList);
    }

    @Test
    @DisplayName("default implementation for Map")
    void defaultMapConstructor() {
        TypeToken<Map<String, Integer>> tt = (TypeToken<Map<String, Integer>>) TypeToken.getParameterized(Map.class, String.class, Integer.class);
        ObjectConstructor<Map<String, Integer>> ctor = cc().get(tt);
        assertTrue(ctor.construct() instanceof LinkedHashMap);
    }

    @Test
    @DisplayName("Throws for abstract class")
    void abstractClassThrows() {
        Class<?> raw = DummyInterface.class;
        TypeToken<?> tt = TypeToken.get(raw);
        ObjectConstructor<?> ctor = cc().get(tt);
        JsonIOException e = assertThrows(JsonIOException.class, ctor::construct);
        assertTrue(e.getMessage().contains(raw.getName()));
    }

    @Test
    @DisplayName("Throws for interface")
    void interfaceThrows() {
        Class<?> raw = Runnable.class;
        TypeToken<?> tt = TypeToken.get(raw);
        ObjectConstructor<?> ctor = cc().get(tt);
        JsonIOException e = assertThrows(JsonIOException.class, ctor::construct);
        assertTrue(e.getMessage().contains(raw.getName()));
    }
}
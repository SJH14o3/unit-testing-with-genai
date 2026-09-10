package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.ReflectionAccessFilter.FilterResult;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConstructorConstructorTest {

    // Test helper classes
    static class ConcreteClass {
        public ConcreteClass() {}
    }

    static abstract class AbstractClass {
        public AbstractClass() {}
    }

    interface TestInterface {}

    static class NoNoArgsConstructor {
        public NoNoArgsConstructor(String arg) {}
    }

    static class PrivateConstructor {
        private PrivateConstructor() {}
    }

    enum TestEnum {
        VALUE
    }

    @Test
    @DisplayName("checkInstantiable: return null for concrete class")
    void checkInstantiable_concrete() {
        assertNull(ConstructorConstructor.checkInstantiable(ConcreteClass.class));
    }

    @Test
    @DisplayName("checkInstantiable: return error for interface")
    void checkInstantiable_interface() {
        String message = ConstructorConstructor.checkInstantiable(TestInterface.class);
        assertNotNull(message);
        assertTrue(message.contains("Interfaces can't be instantiated"));
    }

    @Test
    @DisplayName("checkInstantiable: return error for abstract class")
    void checkInstantiable_abstract() {
        String message = ConstructorConstructor.checkInstantiable(AbstractClass.class);
        assertNotNull(message);
        assertTrue(message.contains("Abstract classes can't be instantiated"));
    }

    @Test
    @DisplayName("get: return InstanceCreatorConstructor when type match found")
    void get_instanceCreator_typeMatch() {
        Map<Type, InstanceCreator<?>> creators = new HashMap<>();
        Type type = new TypeToken<ConcreteClass>(){}.getType();
        creators.put(type, (t) -> new ConcreteClass());

        ConstructorConstructor constructor = new ConstructorConstructor(creators, false, Collections.emptyList());
        ObjectConstructor<ConcreteClass> objConstructor = constructor.get(new TypeToken<ConcreteClass>(){});
        assertNotNull(objConstructor.construct());
    }

    @Test
    @DisplayName("get: return InstanceCreatorConstructor when raw type match found")
    void get_instanceCreator_rawTypeMatch() {
        Map<Type, InstanceCreator<?>> creators = new HashMap<>();
        creators.put(ConcreteClass.class, (t) -> new ConcreteClass());

        ConstructorConstructor constructor = new ConstructorConstructor(creators, false, Collections.emptyList());
        ObjectConstructor<ConcreteClass> objConstructor = constructor.get(new TypeToken<ConcreteClass>(){});
        assertNotNull(objConstructor.construct());
    }

    @Test
    @DisplayName("get: handle EnumSet with valid parameterized type")
    void get_enumSet_valid() {
        ConstructorConstructor constructor = new ConstructorConstructor(Collections.emptyMap(), false, Collections.emptyList());
        Type type = new TypeToken<EnumSet<TestEnum>>(){}.getType();
        ObjectConstructor<EnumSet<TestEnum>> objConstructor = constructor.get(new TypeToken<EnumSet<TestEnum>>(){});

        EnumSet<TestEnum> set = objConstructor.construct();
        assertNotNull(set);
        assertTrue(set.isEmpty());
    }

    @Test
    @DisplayName("get: handle EnumMap with valid parameterized type")
    void get_enumMap_valid() {
        ConstructorConstructor constructor = new ConstructorConstructor(Collections.emptyMap(), false, Collections.emptyList());
        Type type = new TypeToken<EnumMap<TestEnum, ConcreteClass>>(){}.getType();
        ObjectConstructor<EnumMap<TestEnum, ConcreteClass>> objConstructor = constructor.get(new TypeToken<EnumMap<TestEnum, ConcreteClass>>(){});

        EnumMap<TestEnum, ConcreteClass> map = objConstructor.construct();
        assertNotNull(map);
    }

    @Test
    @DisplayName("get: handle ArrayList via default implementation")
    void get_arrayList_implementation() {
        ConstructorConstructor constructor = new ConstructorConstructor(Collections.emptyMap(), false, Collections.emptyList());
        ObjectConstructor<ArrayList<String>> objConstructor = constructor.get(new TypeToken<ArrayList<String>>(){});
        assertNotNull(objConstructor.construct());
    }

    @Test
    @DisplayName("get: handle LinkedHashMap via default implementation")
    void get_linkedHashMap_implementation() {
        ConstructorConstructor constructor = new ConstructorConstructor(Collections.emptyMap(), false, Collections.emptyList());
        ObjectConstructor<LinkedHashMap<String, String>> objConstructor = constructor.get(new TypeToken<LinkedHashMap<String, String>>(){});
        assertNotNull(objConstructor.construct());
    }

    @Test
    @DisplayName("get: throw exception when allowUnsafe is false and no creator available")
    void get_throws_when_unsafe_disabled() {
        ConstructorConstructor constructor = new ConstructorConstructor(Collections.emptyMap(), false, Collections.emptyList());
        ObjectConstructor<ConcreteClass> objConstructor = constructor.get(new TypeToken<ConcreteClass>(){}, false);
        assertThrows(JsonIOException.class, objConstructor::construct);
    }

    @Test
    @DisplayName("get: throw exception when reflect access is blocked")
    void get_throws_when_blocked() {
        List<ReflectionAccessFilter> filters = List.of((type) -> FilterResult.BLOCK_ALL);
        ConstructorConstructor constructor = new ConstructorConstructor(Collections.emptyMap(), true, filters);
        ObjectConstructor<ConcreteClass> objConstructor = constructor.get(new TypeToken<ConcreteClass>(){});
        assertThrows(JsonIOException.class, objConstructor::construct);
    }
}
## The Medium Project
This is Gson, a Java library used to convert Java Objects into an equivalent JSON and vice versa.  
Repository link: https://github.com/google/gson  
Focus is the implementation of the entire project, or at least the part of codes that are worthy to test.
Since the whole project will be tested, I won't go through how each unit is. instead, I'll give some statistics about the files that will be tested:  
```
54 files to be tested
17,600 lines in total
325.93 lines per file on average
10,100 LOC
187.04 LOC per file on average
1661 methods (very simple methods such as getters included)
30.76 methods per file on average
```

Unlike the previous project, I will also run the tests to make sure the tests were generated correctly.

## Files to be tested
Here are the files that are going to be tested. They are grouped according to role and behavior:

1. Core JSON Data Model:
    - com/google/gson/JsonArray.java
    - com/google/gson/JsonElement.java
    - com/google/gson/JsonNull.java
    - com/google/gson/JsonObject.java
    - com/google/gson/JsonPrimitive.java
    - com/google/gson/JsonParser.java

2. Streaming parser/writer state machines:
    - com/google/gson/stream/JsonWriter.java
    - com/google/gson/stream/JsonReader.java
    - com/google/gson/internal/bind/JsonTreeReader.java
    - com/google/gson/internal/bind/JsonTreeWriter.java
    - com/google/gson/JsonStreamParser.java

3. Reflection & Type System:  
    - com/google/gson/reflect/TypeToken.java:
    - com/google/gson/internal/ConstructorConstructor.java
    - com/google/gson/internal/GsonTypes.java
    - com/google/gson/internal/reflect/ReflectionHelper.java
    - com/google/gson/internal/ReflectionAccessFilterHelper.java
    - com/google/gson/ReflectionAccessFilter.java
    - com/google/gson/FieldAttributes.java

4. Exclusion, Reflection & Policy Engine:
    - com/google/gson/internal/Excluder.java
    - com/google/gson/FieldNamingPolicy.java
    - com/google/gson/LongSerializationPolicy.java
    - com/google/gson/ToNumberPolicy.java
    - com/google/gson/FormattingStyle.java
    - com/google/gson/internal/Primitives.java
    - com/google/gson/internal/JavaVersion.java

5. Adapter infrastructure:
    - com/google/gson/TypeAdapter.java
    - com/google/gson/internal/bind/ArrayTypeAdapter.java
    - com/google/gson/internal/bind/ObjectTypeAdapter.java
    - com/google/gson/internal/bind/TypeAdapterRuntimeTypeWrapper.java
    - com/google/gson/internal/bind/MapTypeAdapterFactory.java
    - com/google/gson/internal/bind/CollectionTypeAdapterFactory.java
    - com/google/gson/internal/bind/TreeTypeAdapter.java

6. Specialized/reflective adapters:
    - com/google/gson/internal/bind/ReflectiveTypeAdapterFactory.java
    - com/google/gson/internal/bind/EnumTypeAdapter.java
    - com/google/gson/internal/bind/NumberTypeAdapter.java
    - com/google/gson/internal/bind/DefaultDateTypeAdapter.java
    - com/google/gson/internal/bind/JavaTimeTypeAdapters.java
    - com/google/gson/internal/bind/JsonAdapterAnnotationTypeAdapterFactory.java
    - com/google/gson/internal/bind/JsonElementTypeAdapter.java
    - com/google/gson/internal/bind/TypeAdapters.java

7. Internal utilities/data structures:
    - com/google/gson/internal/LinkedTreeMap.java
    - com/google/gson/internal/bind/util/ISO8601Utils.java
    - com/google/gson/internal/Streams.java
    - com/google/gson/internal/LazilyParsedNumber.java
    - com/google/gson/internal/NumberLimits.java
    - com/google/gson/internal/NonNullElementWrapperList.java
    - com/google/gson/internal/UnsafeAllocator.java

8. SQL:
    - com/google/gson/internal/sql/SqlTypesSupport.java
    - com/google/gson/internal/sql/SqlDateTypeAdapter.java
    - com/google/gson/internal/sql/SqlTimestampTypeAdapter.java
    - com/google/gson/internal/sql/SqlTimeTypeAdapter.java

9. Facade/configuration and a Miscellaneous:
    - com/google/gson/GsonBuilder.java
    - com/google/gson/Gson.java
    - com/google/gson/internal/PreJava9DateFormatProvider.java

It should be mentioned that many of the files are excluded from behavioral unit testing
because they contain no meaningful executable decision logic. These files include:
- module and package information files
- annotation-only declarations
- marker interfaces
- pure data declarations and enums
- exceptions
- a bridge class
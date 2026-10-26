## پروژه متوسط
این پروژه Gson است؛ کتابخانه‌ای در جاوا که برای تبدیل اشیای جاوا به JSON معادل و برعکس استفاده می‌شود.  
لینک مخزن: https://github.com/google/gson  
تمرکز کار بر پیاده‌سازی کل پروژه است، یا دست‌کم بخشی از کدها که ارزش تست دارند.
از آنجا که کل پروژه تست خواهد شد، به بررسی تک‌تک واحدها نمی‌پردازم؛ در عوض آماری درباره فایل‌هایی که تست خواهند شد ارائه می‌دهم:  
***
۵۴ فایل برای تست
۱۷٬۶۰۰ خط در مجموع
به‌طور میانگین ۳۲۵٫۹۳ خط در هر فایل
۱۰٬۱۰۰ LOC
به‌طور میانگین ۱۸۷٫۰۴ LOC در هر فایل
۱۶۶۱ متد (متدهای بسیار ساده مانند getterها نیز شامل می‌شوند)
به‌طور میانگین ۳۰٫۷۶ متد در هر فایل
***

برخلاف پروژه پیشین، این بار تست‌ها را نیز اجرا می‌کنم تا از درستی تولید آن‌ها اطمینان حاصل شود.

## فایل‌های تحت تست
در ادامه فایل‌هایی که قرار است تست شوند آمده است. این فایل‌ها بر اساس نقش و رفتار دسته‌بندی شده‌اند:

1. مدل داده اصلی JSON:
    - com/google/gson/JsonArray.java
    - com/google/gson/JsonElement.java
    - com/google/gson/JsonNull.java
    - com/google/gson/JsonObject.java
    - com/google/gson/JsonPrimitive.java
    - com/google/gson/JsonParser.java

2. ماشین‌های حالت پارسر/نویسنده جریانی:
    - com/google/gson/stream/JsonWriter.java
    - com/google/gson/stream/JsonReader.java
    - com/google/gson/internal/bind/JsonTreeReader.java
    - com/google/gson/internal/bind/JsonTreeWriter.java
    - com/google/gson/JsonStreamParser.java

3. بازتاب (Reflection) و سیستم نوع:  
    - com/google/gson/reflect/TypeToken.java:
    - com/google/gson/internal/ConstructorConstructor.java
    - com/google/gson/internal/GsonTypes.java
    - com/google/gson/internal/reflect/ReflectionHelper.java
    - com/google/gson/internal/ReflectionAccessFilterHelper.java
    - com/google/gson/ReflectionAccessFilter.java
    - com/google/gson/FieldAttributes.java

4. موتور حذف، بازتاب و سیاست‌گذاری:
    - com/google/gson/internal/Excluder.java
    - com/google/gson/FieldNamingPolicy.java
    - com/google/gson/LongSerializationPolicy.java
    - com/google/gson/ToNumberPolicy.java
    - com/google/gson/FormattingStyle.java
    - com/google/gson/internal/Primitives.java
    - com/google/gson/internal/JavaVersion.java

5. زیرساخت آداپترها:
    - com/google/gson/TypeAdapter.java
    - com/google/gson/internal/bind/ArrayTypeAdapter.java
    - com/google/gson/internal/bind/ObjectTypeAdapter.java
    - com/google/gson/internal/bind/TypeAdapterRuntimeTypeWrapper.java
    - com/google/gson/internal/bind/MapTypeAdapterFactory.java
    - com/google/gson/internal/bind/CollectionTypeAdapterFactory.java
    - com/google/gson/internal/bind/TreeTypeAdapter.java

6. آداپترهای تخصصی/بازتابی:
    - com/google/gson/internal/bind/ReflectiveTypeAdapterFactory.java
    - com/google/gson/internal/bind/EnumTypeAdapter.java
    - com/google/gson/internal/bind/NumberTypeAdapter.java
    - com/google/gson/internal/bind/DefaultDateTypeAdapter.java
    - com/google/gson/internal/bind/JavaTimeTypeAdapters.java
    - com/google/gson/internal/bind/JsonAdapterAnnotationTypeAdapterFactory.java
    - com/google/gson/internal/bind/JsonElementTypeAdapter.java
    - com/google/gson/internal/bind/TypeAdapters.java

7. ابزارها/ساختارهای داده داخلی:
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

9. نما (Facade)/پیکربندی و متفرقه:
    - com/google/gson/GsonBuilder.java
    - com/google/gson/Gson.java
    - com/google/gson/internal/PreJava9DateFormatProvider.java

باید خاطرنشان کرد که بسیاری از فایل‌ها از تست واحد رفتاری کنار گذاشته شده‌اند
زیرا منطق تصمیم‌گیری اجرایی معناداری در خود ندارند. این فایل‌ها شامل موارد زیر هستند:
- فایل‌های اطلاعات ماژول و پکیج
- اعلان‌های صرفاً annotation
- رابط‌های marker (نشانه‌گذار)
- اعلان‌های صرفاً داده و enumها
- استثناها
- یک کلاس bridge
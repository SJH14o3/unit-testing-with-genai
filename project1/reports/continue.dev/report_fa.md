# آزمایش با continue.dev در تولید تست‌های واحد
## معرفی Continue.dev
Continue.dev یک ابزار کدنویسی خودکار متن‌باز است که به صورت رابط کاربری خط فرمان (CLI)، افزونه VS Code و پلاگین‌های JetBrains در دسترس است.
Continue.dev امکان دسترسی به مدل‌های مختلف را فراهم می‌کند. در اینجا لیستی از مدل‌های محبوب و ارائه دهندگان آنها آورده شده است:
- Anthropic
  - Claude Opus 4.6
  - Claude Sonnet 4.6
  - Claude Haiku 4.5
- Azure OpenAI
  - GPT-4o
- Google Gemini API
  - Gemini 3.1 Pro
  - Gemini 3 Flash
  - Gemini 3.1 Flash Lite
- Mistral
  - Devstral Medium
  - Magistral Medium
  - Codestral
- Ollama
  - gpt-oss-20b
  - Llama 3.1 8b
  - Mistral Chat
  - DeepSeek Coder
  - Gemma3 12b
- OpenAI
  - GPT-5.2
  - GPT-5 Mini
  - GPT-5 Codex

در اکثر موارد، برای استفاده از مدل‌های یک ارائه دهنده، کاربر به یک کلید API نیاز دارد. Continue یک ویژگی کاربردی دارد که لینکی را برای تولید کلید API برای آن ارائه دهنده باز می‌کند، اگرچه ابتدا باید وارد سیستم شده باشید. با این حال، این بدان معناست که کاربر باید برای هر ارائه دهنده به طور جداگانه اعتبار خریداری کند، که یک نقطه ضعف نسبت به GitHub Copilot است، اگرچه کاربر می‌تواند از ارائه دهندگانی استفاده کند که دسترسی به اکثر مدل‌ها را فراهم می‌کنند، مانند OpenRouter.
همچنین می‌توان یک مدل محلی را با استفاده از Ollama اجرا کرد. کاربران می‌توانند مدل را روی دستگاه خود اجرا کنند. اگر مقدار `apiBase` در فایل پیکربندی وجود نداشته باشد، Continue مدل را روی سیستم شما اجرا می‌کند (باید آن را نصب کرده باشید). همچنین می‌توانید مدل محلی را روی یک سرور اجرا کنید با ارائه آدرس IP و پورت آن در فیلد `apiBase`.

کاربران همچنین می‌توانند قوانینی را با ایجاد فایل‌های Markdown در پوشه `<پوشه پروژه>/.continue/rules/` اعمال کنند. هر فایل Markdown می‌تواند به عنوان یک گروه از قوانین در نظر گرفته شود و هر گروه را می‌توان به طور جداگانه در تنظیمات Continue فعال یا غیرفعال کرد.

### نحوه نصب در Visual Studio Code
به تب افزونه‌ها بروید و "Continue" را جستجو کنید.
شما باید "Continue - open-source AI code agent" را مشاهده کنید. این افزونه را نصب کنید.
پس از نصب، یک تب جدید به نوار کناری اضافه می‌شود. اکنون می‌توانید شروع به کار کنید.

### نحوه استفاده از Continue.dev
هنگامی که تب Continue را باز می‌کنید، یک کادر گفتگو خواهید دید. با کلیک بر روی کادر گفتگو، می‌توانید یک حالت (mode)، یک مدل و همچنین فایل/موضوعی را به آن متصل کنید.

#### حالت (Mode)
حالت چهار گزینه دارد:
- گفتگو (Chat)
- برنامه ریزی (Plan)
- عامل (Agent)
- پس زمینه (Background)

##### حالت گفتگو (Chat Mode)
حالت گفتگو یک رابط کناری معمولی را فراهم می‌کند که در آن کاربران می‌توانند با یک مدل گفتگو کنند.
این حالت از هیچ ابزاری استفاده نمی‌کند (مگر اینکه به صورت دستی یک فایل را متصل کنید).

##### حالت برنامه ریزی (Plan Mode)
حالت برنامه ریزی به عنوان یک محیط فقط خواندنی عمل می‌کند و به استراتژی‌سازی و درک کد کمک می‌کند. این حالت فقط می‌تواند کد را بخواند، اما هیچ بخشی از کد را تغییر نمی‌دهد.

##### حالت عامل (Agent)
در حالت Agent، تمام ابزارها در دسترس هستند و عامل هوش مصنوعی می‌تواند فایل‌ها را بخواند، ایجاد کند و تغییر دهد.
به طور پیش فرض، قبل از اجرای یک ابزار، از شما اجازه خواسته می‌شود، اما سیاست‌ها را می‌توان به صورت خودکار تنظیم کرد.

##### حالت پس زمینه (Background Mode)
اجرای پس زمینه با استفاده از حالت Agent و Cloud Agents خودکار انجام می‌شود. به جای اینکه منتظر بمانید تا در یک کادر گفتگو، اعلان‌ها را تایپ کنید، این عوامل به طور ناهمگام در پس زمینه اجرا می‌شوند تا وظایفی مانند رفع اشکالات، بررسی کیفیت کد یا تولید مستندات را انجام دهند.

#### انتخاب مدل
Continue پیکربندی مدل‌ها را در این دایرکتوری ذخیره می‌کند:
"C\Users\<کاربر>\.continue\config.yaml".
برای افزودن یک مدل، ابتدا یک ارائه دهنده را انتخاب می‌کنید، سپس یک مدل را انتخاب می‌کنید و کلید API خود را وارد می‌کنید. زیر کادر کلید API، لینکی برای تولید کلید API برای آن مدل وجود دارد. بعد از پر کردن تمام فیلدها، می‌توانید روی دکمه "اتصال" کلیک کنید. این کار فایل پیکربندی را به‌روزرسانی می‌کند. همچنین می‌توانید تمام این کارها را خودتان در فایل پیکربندی انجام دهید.
مدلی که اضافه کرده‌اید اکنون قابل انتخاب است.

#### اتصال فایل/موضوع
کاربر می‌تواند فایل‌ها، تصاویر و غیره را به چت‌بات هوش مصنوعی متصل کند. برای این کار می‌توانید روی دکمه "@" کلیک کنید یا "@" را در کادر گفتگو تایپ کنید. می‌توانید فایل‌ها، تصاویر (در صورتی که مدل انتخاب شده از آنها پشتیبانی کند)، diff Git، ترمینال (ارجاع به آخرین دستور ترمینال)، مشکلات (مشکلات موجود در فایل باز شده فعلی) و همچنین قوانین را متصل کنید. قوانین می‌توانند در مواردی مفید باشند که نمی‌خواهید آنها را برای همه اعلان‌ها اعمال کنید.

بعد از نوشتن اعلان خود، می‌توانید روی دکمه "Enter" کلیک کنید و مدل شروع به کار می‌کند.

## تولید تست‌های واحد با Continue.dev
در این آزمایش، از ۲ مدل بزرگ و ۵ مدل محلی استفاده خواهم کرد. مدل‌های هم‌رده را در ۳ دور مقایسه خواهم کرد. در هر دور، برای همه مدل‌ها، اعلان یکسان خواهد بود. مدل‌های انتخابی عبارتند از:
- مدل‌های بزرگ
  - Mistral Devstral Medium
  - Google Gemini 3 Flash
- مدل‌های محلی
  - GPT OSS 20b
  - Gemma3 12b
  - Gemma4 e4b
  - Qwen 2.5 Coder 7b
  - Qwen 3 coder 30b

من با یک اعلان ساده در دور اول شروع خواهم کرد، سپس اعلان را به یک اعلان پیچیده‌تر و دقیق‌تر با توجه به اشکالات تست‌های واحد تولید شده قبلی، تبدیل خواهم کرد.
همچنین، هر چهار واحد به اعلان متصل شده‌اند.

## دور ۱
بیایید با یک اعلان ساده شروع کنیم. برای جلوگیری از استفاده از Mock، به طور صریح آن را در اعلان قرار خواهم داد.
> برای این واحدها، تست‌های واحد را با استفاده از JUnit5 تولید کنید. از Mock برای وابستگی‌ها استفاده نکنید. کلاس‌های تست تولید شده باید با "Test(ModelName)_1" به پایان برسند.

برای پسوند، اگر نام مدل "gemma3" باشد، نام کلاس تست دارای پسوند "TestGemma3_1" خواهد بود.
با این حال، پس از تولید تمام تست‌ها، متوجه شدم که "Test(ModelName)_P1" بهتر است، زیرا بسیاری از مدل‌ها با اعداد به پایان می‌رسند و به عنوان مثال، Gemma3_1 ممکن است برخی را گیج کند.

### Mistral Devstral Medium
عامل فایل‌های تست را تولید کرد و آنها را در دایرکتوری تست قرار داد.

#### واحد MyMath
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyMathTestMistral1 {

    @Test
    void testCheckSignPositive() {
        MyMath myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(10));
    }

    @Test
    void testCheckSignNegative() {
        MyMath myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-5));
    }

    @Test
    void testCheckSignZero() {
        MyMath myMath = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    void testReverseNumber() {
        MyMath myMath = new MyMath();
        assertEquals(-10, myMath.reverseNumber(10));
        assertEquals(10, myMath.reverseNumber(-10));
        assertEquals(0, myMath.reverseNumber(0));
    }
}
```
چیزی که متوجه شدم این است که برای هر متد، یک واحد جدید ایجاد می‌شود. این را می‌توان ساده‌تر کرد با ایجاد واحد به عنوان یک متغیر خصوصی.

##### متد *checkSign(int)*
سه متد تولید شده است که شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- ۰ (حد و مرز و استثنا)

تست‌ها پوشش MC/DC را دارند.
به جای ۱۰ و -۵، بهتر بود ۱ و -۱ را تست می‌کرد.

##### متد *reverseNumber(int)*
فقط یک متد با سه assertion تولید شده است. بهتر بود که ۳ متد جداگانه برای هر assertion ایجاد شوند. تست‌های تولید شده شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- ۰ (حد و مرز)

تست‌ها پوشش MC/DC را دارند.
به جای ۱۰ و -۵، بهتر بود ۱ و -۱ را تست می‌کرد.

#### واحد MyAdvancedMath
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyAdvancedMathTestMistral1 {

    @Test
    void testAddPositiveNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(15, advancedMath.add(10, 5));
    }

    @Test
    void testAddNegativeNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(10, -5));
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-10, -5));
    }

    @Test
    void testAddOverflow() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(Integer.MAX_VALUE, 1));
    }

    @Test
    void testPowerOfTwo() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(1, advancedMath.powerOfTwo(0));
        assertEquals(2, advancedMath.powerOfTwo(1));
        assertEquals(4, advancedMath.powerOfTwo(2));
        assertEquals(1073741824, advancedMath.powerOfTwo(30));
    }

    @Test
    void testPowerOfTwoInvalid() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    void testReverseArrayNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath myMath = new MyMath();
        int[] input = {10, -5, 0};
        int[] expected = {-10, 5, 0};
        assertArrayEquals(expected, advancedMath.reverseArrayNumbers(input, myMath));
    }
}
```
چهار متد تولید شده است که شامل موارد زیر است:
- x و y هر دو مثبت
- x مثبت، y منفی و بالعکس
- x و y هر دو منفی
- تست سرریز
تست ها، پوشش MC/DC را دارند.
تست مقدار مرزی وجود ندارد.

تست *powerOfTwo(int)* شامل شش assertion است که موارد زیر را شامل می‌شود:
- کمی بالاتر و پایین تر از حد پایین
- روی حد بالا
- کمی بالاتر از حد بالا
- دو مقدار معمولی

از آنجا که *power == 0* تست نشده است، حتی پوشش statement نداریم.
حدود در اینجا به طور ضمنی تست شده‌اند.
تست ۴ به عنوان ورودی، redundant است زیرا ۲ (مقدار نرمال دیگری) تست شده است.
این متد، برخلاف روش‌های قبلی، رویکرد کاملاً متفاوتی دارد.

تست *reverseArrayNumbers(int[], MyMath)* شامل یک متد و یک assertion است. این فقط یک آرایه معمولی است.
تست‌های انجام شده، پوشش MC/DC را دارند.
بسیاری از موارد لبه می‌توانستند در اینجا تست شوند.

#### واحد FileIO
```java
package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileIOTestMistral1 {

    @Test
    void testReadFile() throws IOException {
        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        // Write test data to the file
        Files.write(tempFile, "Line 1\nLine 2\nLine 3".getBytes());

        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile(file.getAbsolutePath());

        assertEquals(3, content.length);
        assertEquals("Line 1", content[0]);
        assertEquals("Line 2", content[1]);
        assertEquals("Line 3", content[2]);
    }

    @Test
    void testReadFileEmpty() throws IOException {
        // Create a temporary empty file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile(file.getAbsolutePath());

        assertEquals(0, content.length);
    }
}
```
دو متد تولید شده است که شامل موارد زیر است:
- یک تست معمولی
- یک تست برای فایل خالی

هیچ استثنایی رخ نداده است، بنابراین پوشش statement نداریم.
از حافظه موقت استفاده شده است، اما استفاده از ویژگی JUnit برای حافظه موقت (@TempDir Annotation) بهتر بود.
موارد لبه مانند فایل غیر موجود تست نشده‌اند.

#### واحد NameSearch
```java
package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import dmst.seip.angor.junitdemo.io.FileIO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class NameSearchTestMistral1 {

    @Test
    void testFindNamesInFileThatStartWith() throws IOException {
        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        // Write test data to the file
        Files.write(tempFile, "Alice\nBob\nAnna\nAndrew\nCharlie".getBytes());

        FileIO fileIO = new FileIO();
        NameSearch nameSearch = new NameSearch();

        String[] result = nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "A");

        assertEquals(3, result.length);
        assertEquals("Alice", result[0]);
        assertEquals("Anna", result[1]);
        assertEquals("Andrew", result[2]);
    }

    @Test
    void testFindNamesInFileThatStartWithNoMatches() throws IOException {
        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        // Write test data to the file
        Files.write(tempFile, "Alice\nBob\nCharlie".getBytes());

        FileIO fileIO = new FileIO();
        NameSearch nameSearch = new NameSearch();

        String[] result = nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "Z");

        assertEquals(0, result.length);
    }
}
```
دو متد تولید شده است که شامل موارد زیر است:
- تست با نتایج متعدد
- تست بدون نتیجه

تست‌ها پوشش MC/DC را دارند.
فایل‌های موقت استفاده شده‌اند، اما استفاده از ویژگی JUnit برای حافظه موقت (@TempDir Annotation) بهتر بود.
موارد لبه مانند پیشوند خالی تست نشده‌اند.

#### نتیجه
برای یک اعلان ساده، به نظر می‌رسد عملکرد خوبی داشته است.
مشکلات تست‌های تولید شده عبارتند از:
- بسیاری از متدها دارای چند assertion بودند.
- تست مقدار مرزی برای اکثر متدها وجود نداشت.
- بسیاری از موارد لبه تست نشده‌اند.
نام متدها با پیشوند "test" شروع می‌شود.
نام متدها با assertionهایی که دارند مطابقت دارند.

### Google Gemini 3 Flash
عامل فایل‌های تست را تولید کرد و آنها را در دایرکتوری تست قرار داد که این کار درست است.

#### واحد MyMath
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class MyMathTestGemini1 {

    private final MyMath mm = new MyMath();

    @Test
    public void testCheckSignPositive() {
        Assertions.assertEquals("positive", mm.checkSign(10));
    }

    @Test
    public void testCheckSignNegative() {
        Assertions.assertEquals("negative", mm.checkSign(-5));
    }

    @Test
    public void testCheckSignZero() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mm.checkSign(0);
        });
    }

    @Test
    public void testReverseNumberPositive() {
        Assertions.assertEquals(-10, mm.reverseNumber(10));
    }

    @Test
    public void testReverseNumberNegative() {
        Assertions.assertEquals(5, mm.reverseNumber(-5));
    }

    @Test
    public void testReverseNumberZero() {
        Assertions.assertEquals(0, mm.reverseNumber(0));
    }
}
```
برخلاف Mistral، Gemini از یک متغیر خصوصی برای واحد استفاده کرد.

##### متد *checkSign(int)*
سه متد تولید شده است که شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- ۰ (حد و مرز و استثنا)

تست‌ها پوشش MC/DC را دارند.
بهتر بود ۱ و -۱ را تست می‌کردم.

##### متد *reverseNumber(int)*
فقط یک متد با سه assertion تولید شده است. بهتر بود که ۳ متد جداگانه برای هر assertion ایجاد شوند. تست‌های تولید شده شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- ۰ (حد و مرز)

تست‌ها پوشش MC/DC را دارند.
بهتر بود ۱ و -۱ را تست می‌کردم.

#### واحد MyAdvancedMath
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class MyAdvancedMathTestGemini1 {

    private final MyAdvancedMath mam = new MyAdvancedMath();

    @Test
    public void testAddSuccess() {
        Assertions.assertEquals(10, mam.add(4, 6));
    }

    @Test
    public void testAddNegativeFirst() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.add(-1, 5);
        });
    }

    @Test
    public void testAddNegativeSecond() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.add(5, -1);
        });
    }

    @Test
    public void testAddOverflow() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.add(Integer.MAX_VALUE, 1);
        });
    }

    @Test
    public void testPowerOfTwoSuccess() {
        Assertions.assertEquals(1, mam.powerOfTwo(0));
        Assertions.assertEquals(2, mam.powerOfTwo(1));
        Assertions.assertEquals(1024, mam.powerOfTwo(10));
        Assertions.assertEquals(1073741824, mam.powerOfTwo(30));
    }

    @Test
    public void testPowerOfTwoNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.powerOfTwo(-1);
        });
    }

    @Test
    public void testPowerOfTwoTooLarge() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mam.powerOfTwo(31);
        });
    }

    @Test
    public void testReverseArrayNumbers() {
        MyMath mm = new MyMath();
        int[] input = {1, -2, 0};
        int[] expected = {-1, 2, 0};
        Assertions.assertArrayEquals(expected, mam.reverseArrayNumbers(input, mm));
    }
}
```
چهار متد تولید شده است که شامل موارد زیر است:
- x و y هر دو مثبت
- x مثبت، y منفی و بالعکس
- x و y هر دو منفی
- تست سرریز
تست‌ها، پوشش MC/DC را دارند.
تست مقدار مرزی وجود ندارد.

تست *powerOfTwo(int)* شامل شش assertion است که موارد زیر را شامل می‌شود:
- کمی بالاتر و پایین تر از حد پایین
- روی حد بالا
- کمی بالاتر از حد بالا
- دو مقدار معمولی

از آنجا که *power == 0* تست نشده است، حتی پوشش statement نداریم.
حدود در اینجا به طور ضمنی تست شده‌اند.
تست ۴ به عنوان ورودی، redundant است زیرا ۲ (مقدار نرمال دیگری) تست شده است.
این متد، برخلاف روش‌های قبلی، رویکرد کاملاً متفاوتی دارد.

تست *reverseArrayNumbers(int[], MyMath)* شامل یک متد و یک assertion است. این فقط یک آرایه معمولی است.
تست‌های انجام شده، پوشش MC/DC را دارند.
بسیاری از موارد لبه می‌توانستند در اینجا تست شوند.

#### واحد FileIO
```java
package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileIOTestMistral1 {

    @Test
    void testReadFile() throws IOException {
        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        // Write test data to the file
        Files.write(tempFile, "Line 1\nLine 2\nLine 3".getBytes());

        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile(file.getAbsolutePath());

        assertEquals(3, content.length);
        assertEquals("Line 1", content[0]);
        assertEquals("Line 2", content[1]);
        assertEquals("Line 3", content[2]);
    }

    @Test
    void testReadFileEmpty() throws IOException {
        // Create a temporary empty file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile(file.getAbsolutePath());

        assertEquals(0, content.length);
    }
}
```
دو متد تولید شده است که شامل موارد زیر است:
- یک تست معمولی
- یک تست برای فایل خالی

هیچ استثنایی رخ نداده است، بنابراین پوشش statement نداریم.
از حافظه موقت استفاده شده است، اما استفاده از ویژگی JUnit برای حافظه موقت (@TempDir Annotation) بهتر بود.
موارد لبه مانند فایل غیر موجود تست نشده‌اند.

#### واحد NameSearch
```java
package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import dmst.seip.angor.junitdemo.io.FileIO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class NameSearchTestMistral1 {

    @Test
    void testFindNamesInFileThatStartWith() throws IOException {
        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        // Write test data to the file
        Files.write(tempFile, "Alice\nBob\nAnna\nAndrew\nCharlie".getBytes());

        FileIO fileIO = new FileIO();
        NameSearch nameSearch = new NameSearch();

        String[] result = nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "A");

        assertEquals(3, result.length);
        assertEquals("Alice", result[0]);
        assertEquals("Anna", result[1]);
        assertEquals("Andrew", result[2]);
    }

    @Test
    void testFindNamesInFileThatStartWithNoMatches() throws IOException {
        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test", ".txt");
        File file = tempFile.toFile();
        file.deleteOnExit();

        // Write test data to the file
        Files.write(tempFile, "Alice\nBob\nCharlie".getBytes());

        FileIO fileIO = new FileIO();
        NameSearch nameSearch = new NameSearch();

        String[] result = nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "Z");

        assertEquals(0, result.length);
    }
}
```
دو متد تولید شده است که شامل موارد زیر است:
- تست با نتایج متعدد
- تست بدون نتیجه

تست‌ها پوشش MC/DC را دارند.
فایل‌های موقت استفاده شده‌اند، اما استفاده از ویژگی JUnit برای حافظه موقت (@TempDir Annotation) بهتر بود.
موارد لبه مانند پیشوند خالی تست نشده‌اند.

#### نتیجه
برای یک اعلان ساده، به نظر می‌رسد عملکرد خوبی داشته است.
مشکلات تست‌های تولید شده عبارتند از:
- بسیاری از متدها دارای چند assertion بودند.
- تست مقدار مرزی برای اکثر متدها وجود نداشت.
- بسیاری از موارد لبه تست نشده‌اند.
نام متدها با پیشوند "test" شروع می‌شود.
نام متدها با assertionهایی که دارند مطابقت دارند.

--- فایل: report_2.md ---
##### متد *add(int, int)*
چهار متد تولید شد که شامل موارد زیر است:
- مقدار مثبت برای x و y
- مقدار مثبت برای x و منفی برای y و بالعکس
- تست سرریز (overflow)

تست‌ها دارای ویژگی MC/DC هستند.
مقادیر مرزی، به خصوص ۰، تست نشدند.

##### متد *powerOfTwo(int)*
سه متد تولید شد. یک متد شامل چهار ادعا (assertion) بود. تست‌ها شامل موارد زیر است:
- کمی پایین‌تر از حد پایین
- حد پایین
- کمی بالاتر از حد پایین
- یک تست معمولی
- حد بالا
- کمی بالاتر از حد بالا

تست‌ها دارای ویژگی MC/DC هستند.
مقادیر مرزی به خوبی تست شده‌اند.
تست *power=10* غیرضروری است.

##### متد *reverseArrayNumbers(int[], MyMath)*
فقط یک متد تولید شد. بسیاری از موارد خاص (edge cases) مانند آرایه خالی تست نشدند.
متد تست شده فقط یک حلقه است و ویژگی MC/DC در آن حاصل شده است.

#### واحد FileIO
```java
package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class FileIOTestGemini1 {

    private final FileIO fileIO = new FileIO();

    @Test
    public void testReadFile(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("test.txt");
        String[] expectedContent = {"line1", "line2", "line3"};
        
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            for (String line : expectedContent) {
                writer.write(line + System.lineSeparator());
            }
        }

        String[] actualContent = fileIO.readFile(tempFile.toString());
        Assertions.assertArrayEquals(expectedContent, actualContent);
    }

    @Test
    public void testReadFileNotExists() {
        // FileIO.readFile در صورت عدم وجود فایل، ت race stack و آرایه خالی برمی‌گرداند.
        String[] content = fileIO.readFile("non_existent_file.txt");
        Assertions.assertEquals(0, content.length);
    }
}
```
دو متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک فایل غیر موجود

روش درست تست با استفاده از دایرکتوری موقت استفاده شده است.
ما با یک استثناء (exception) مواجه می‌شویم و ویژگی MC/DC حاصل می‌شود.

#### واحد NameSearch
```java
package dmst.seip.angor.junitdemo.search;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class NameSearchTestGemini1 {

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    public void testFindNamesInFileThatStartWith(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("names.txt");
        String[] names = {"Antonis", "Anna", "Bob", "Alice"};
        
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            for (String name : names) {
                writer.write(name + System.lineSeparator());
            }
        }

        String[] resultA = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        Assertions.assertArrayEquals(new String[]{"Antonis", "Anna", "Alice"}, resultA);

        String[] resultAn = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "An");
        Assertions.assertArrayEquals(new String[]{"Antonis", "Anna"}, resultAn);

        String[] resultZ = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "Z");
        Assertions.assertEquals(0, resultZ.length);
    }
}
```
یک متد با ادعاهای (assertions) متعدد تولید می‌شود که شامل موارد زیر است:
- دو تست معمولی
- یک تست بدون نتیجه

ایجاد یک فایل موقت فقط یک بار سریع‌تر از سه بار است، اگرچه باید سه بار خوانده شود.
تست، ویژگی MC/DC را حاصل می‌کند.

#### نتیجه‌گیری
این مدل نیز برای یک درخواست ساده عملکرد خوبی داشت.
من ترجیح می‌دادم تست‌های بیشتری برای مقادیر مرزی انجام شود.
نام متدها با چیزی که تست می‌کنند مطابقت دارد و با "test" شروع می‌شوند.
بسیاری از موارد خاص (edge cases) تست نشده‌اند.
برخی از متدها ادعاهای (assertions) متعددی داشتند.

### Devstral در مقابل Gemini
اکنون که تست‌های تولید شده توسط دو مدل بزرگ را مشاهده کرده‌ایم، می‌توانیم آن‌ها را مقایسه کنیم.
بیشتر اوقات، آن‌ها یکسان بودند. فقط تفاوت‌های آن‌ها را بررسی می‌کنیم:
- Devstral مقادیر منفی را برای تابع add تست کرد، در حالی که Gemini این کار را نکرد.
- Gemini از روش معمول JUnit برای ذخیره‌سازی موقت استفاده کرد، در حالی که Devstral این کار را نکرد.
- Devstral یک فایل خالی را برای FileIO تست کرد، در حالی که Gemini این کار را نکرد.
- Gemini یک فایل غیر موجود را برای FileIO تست کرد، در حالی که Devstral این کار را نکرد.
- Gemini از متغیرهای خصوصی برای واحدها استفاده کرد، در حالی که Devstral متغیرهای مورد نیاز را در خود متد نمونه‌سازی کرد.

### GPT-OSS:20b
پس از ارائه درخواست، فایل‌های تست تولید شدند، اما در فایل مناسب قرار نگرفتند.
من مجبور شدم آن‌ها را به صورت دستی ایجاد کرده و در دایرکتوری مناسب قرار دهم.

#### واحد MyMath
```java
package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * تست‌های واحد برای {@link MyMath}.
 */
class MyMathTestGPT_OSS1 {

  private final MyMath myMath = new MyMath();

  @Test
  void checkSign_positiveNumber_returnsPositive() {
    assertEquals("positive", myMath.checkSign(5));
  }

  @Test
  void checkSign_negativeNumber_returnsNegative() {
    assertEquals("negative", myMath.checkSign(-3));
  }

  @Test
  void checkSign_zero_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
  }

  @Test
  void reverseNumber_positiveNumber_returnsNegative() {
    assertEquals(-10, myMath.reverseNumber(10));
  }

  @Test
  void reverseNumber_negativeNumber_returnsPositive() {
    assertEquals(7, myMath.reverseNumber(-7));
  }

  @Test
  void reverseNumber_zero_returnsZero() {
    assertEquals(0, myMath.reverseNumber(0));
  }
}
```

##### متد *checkSign(int)*
سه متد تولید شد که شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- صفر (مرز)

تست‌ها دارای ویژگی MC/DC هستند.
من ۱ و -۱ را به جای ۵ و -۳ تست می‌کردم.

##### متد *reverseNumber(int)*
سه متد تولید شد که شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- صفر (در مرز)

تست‌ها دارای ویژگی MC/DC هستند.
من ۱ و -۱ را به جای ۱۰ و -۷ تست می‌کردم.

#### واحد MyAdvancedMath
```java
package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * تست‌های واحد برای {@link MyAdvancedMath}.
 */
class MyAdvancedMathTestGPT_OSS1 {

  private final MyAdvancedMath advancedMath = new MyAdvancedMath();
  private final MyMath myMath = new MyMath();

  @Test
  void add_validInputs_returnsSum() {
    assertEquals(15, advancedMath.add(7, 8));
  }

  @Test
  void add_negativeInput_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-1, 5));
    assertThrows(IllegalArgumentException.class, () -> advancedMath.add(3, -2));
  }

  @Test
  void add_overflow_throwsIllegalArgumentException() {
    int large = Integer.MAX_VALUE - 10;
    assertThrows(IllegalArgumentException.class, () -> advancedMath.add(large, 20));
  }

  @Test
  void powerOfTwo_zero_returnsOne() {
    assertEquals(1, advancedMath.powerOfTwo(0));
  }

  @Test
  void powerOfTwo_withinBounds_returnsCorrectResult() {
    assertEquals(32, advancedMath.powerOfTwo(5));
    assertEquals(1 << 10, advancedMath.powerOfTwo(10));
  }

  @Test
  void powerOfTwo_negativeOrTooLarge_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
    assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
  }

  @Test
  void reverseArrayNumbers_correctlyReversesSigns() {
    int[] input = {1, -2, 3, -4};
    int[] expected = {-1, 2, -3, 4};
    int[] result = advancedMath.reverseArrayNumbers(input.clone(), myMath);
    assertArrayEquals(expected, result);
  }
}
```

##### متد *add(int, int)*
سه متد تولید شد که شامل موارد زیر است:
- مقدار مثبت برای x و y
- مقدار مثبت برای x و منفی برای y و بالعکس
- تست سرریز (overflow)

تست‌ها دارای ویژگی MC/DC هستند.
مقادیر مرزی، به خصوص ۰، تست نشدند.
تست سرریز جالب بود. در حالی که تمام مدل‌های قبلی *Integer.MAX_VALUE + 1* را تست می‌کردند، این مدل از *Integer.MAX_VALUE - 10 + 20* استفاده کرد. کلود اوپوس نیز کاری مشابه انجام داد.

##### متد *powerOfTwo(int)*
سه متد تولید شد. تست‌ها شامل موارد زیر است:
- کمی پایین‌تر از حد پایین
- حد پایین
- دو تست معمولی
- کمی بالاتر از حد بالا

تست‌ها دارای ویژگی MC/DC هستند.
مقادیر مرزی تا حدودی تست شده‌اند.
تست *power=10* غیرضروری است.

##### متد *reverseArrayNumbers(int[], MyMath)*
فقط یک متد تولید شد. بسیاری از موارد خاص (edge cases) مانند آرایه خالی تست نشدند.
متد تست شده فقط یک حلقه است و ویژگی MC/DC در آن حاصل شده است.

#### واحد FileIO
```java
package dmst.seip.angor.junitdemo.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * تست‌های واحد برای {@link FileIO}.
 */
class FileIOTestGPT_OSS1 {

    private final FileIO fileIO = new FileIO();

    @Test
    void readFile_existingFile_returnsLines() throws IOException {
        // ایجاد یک فایل موقت با محتوای مشخص
        File temp = File.createTempFile("testFileIO", ".txt");
        temp.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            writer.write("first line\n");
            writer.write("second line\n");
            writer.write("third line");
        }

        String[] lines = fileIO.readFile(temp.getAbsolutePath());
        assertArrayEquals(new String[]{"first line", "second line", "third line"}, lines);
    }

    @Test
    void readFile_nonexistentFile_returnsEmptyArray() {
        // فایل غیر موجود
        String[] lines = fileIO.readFile("this_file_does_not_exist.txt");
        // در این پیاده‌سازی، یک trace stack چاپ می‌شود و یک آرایه خالی برگردانده می‌شود.
        assertEquals(0, lines.length);
    }
}
```
دو متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک فایل غیر موجود

از یک روش غیر JUnit برای دایرکتوری موقت استفاده شده است.
ما با یک استثناء (exception) مواجه می‌شویم، بنابراین ویژگی MC/DC حاصل می‌شود.

#### واحد NameSearch
```java
package dmst.seip.angor.junitdemo.search;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import dmst.seip.angor.junitdemo.io.FileIO;
import org.junit.jupiter.api.Test;

/**
 * تست‌های واحد برای {@link NameSearch}.
 */
class NameSearchTestGPT_OSS1 {

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_matchFound_returnsMatchingNames() throws IOException {
        // آماده‌سازی یک فایل موقت با لیستی از نام‌ها
        File temp = File.createTempFile("names", ".txt");
        temp.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            writer.write("Alice\n");
            writer.write("Bob\n");
            writer.write("Alex\n");
            writer.write("Eve\n");
        }

        String[] result = nameSearch.findNamesInFileThatStartWith(
                temp.getAbsolutePath(),
                fileIO,
                "Al");

        assertArrayEquals(new String[]{"Alice", "Alex"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_noMatch_returnsEmptyArray() throws IOException {
        File temp = File.createTempFile("names", ".txt");
        temp.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            writer.write("Bob\n");
            writer.write("Eve\n");
        }

        String[] result = nameSearch.findNamesInFileThatStartWith(
                temp.getAbsolutePath(),
                fileIO,
                "Al");

        assertEquals(0, result.length);
    }
}
```
یک متد با ادعاهای (assertions) متعدد تولید می‌شود که شامل موارد زیر است:
- یک تست معمولی
- یک تست بدون نتیجه

باز هم، موارد خاص (edge cases) زیادی وجود دارد که تست نشده‌اند.
تست، ویژگی MC/DC را حاصل می‌کند.

#### نتیجه‌گیری
برای مدل محلی اول، تقریباً مانند مدل‌های ابری عمل کرد، اگرچه با یک درخواست پیچیده‌تر و یک واحد، این احتمال وجود ندارد.
به نظر می‌رسد که باید روی تست مقادیر مرزی تمرکز کنم.
نام متدها با چیزی که تست می‌کنند مطابقت دارد، اما با "test" شروع نمی‌شوند.
بسیاری از موارد خاص (edge cases) تست نشده‌اند.
برخی از متدها ادعاهای (assertions) متعددی داشتند.
این مدل از متغیرهای خصوصی برای واحدها استفاده کرد، به جای نمونه‌سازی آن‌ها در داخل هر متد.

### Gemma3:12b
بعد از ارائه درخواست (prompt)، مدل از من خواست که تست‌های تولید شده را در فایل باز فعلی بنویسم؟!
این موضوع باعث شد که من به صورت دستی پوشه "test" و فایل‌های تست واحد را ایجاد کنم.

#### واحد MyMath
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyMathTestGemma3_1 {

    @Test
    void checkSign_positiveNumber() {
        MyMath math = new MyMath();
        assertEquals("positive", math.checkSign(5));
    }

    @Test
    void checkSign_negativeNumber() {
        MyMath math = new MyMath();
        assertEquals("negative", math.checkSign(-3));
    }

    @Test
    void checkSign_zeroNumber() {
        MyMath math = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> math.checkSign(0));
    }

    @Test
    void reverseNumber_positiveNumber() {
        MyMath math = new MyMath();
        assertEquals(-7, math.reverseNumber(7));
    }

    @Test
    void reverseNumber_negativeNumber() {
        MyMath math = new MyMath();
        assertEquals(5, math.reverseNumber(-5));
    }

    @Test
    void reverseNumber_zero() {
        MyMath math = new MyMath();
        assertEquals(0, math.reverseNumber(0));
    }
}
```
یک نکته قابل توجه این است که برای هر متد، یک واحد تست در ابتدا تولید می‌شود.
این موضوع می‌توانست با تعریف واحد به عنوان یک متغیر خصوصی، ساده‌تر شود.
##### متد *checkSign(int)*
سه متد تولید شده‌اند که شامل موارد زیر هستند:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- صفر (حداکثر مقدار)

تست‌ها دارای پوشش MC/DC هستند.
به جای ۵ و -۳، بهتر بود از ۱ و -۱ استفاده می‌شد. این اعداد به مرز نزدیک‌تر هستند، در مقایسه با مدل‌های قبلی.

##### متد *reverseNumber(int)*
سه متد تولید شده‌اند که شامل موارد زیر هستند:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- صفر (در مرز)

تست‌ها دارای پوشش MC/DC هستند.
به جای ۷ و -۵، بهتر بود از ۱ و -۱ استفاده می‌شد.

#### واحد MyAdvancedMath
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyAdvancedMathTestGemma3_1 {

    @Test
    void add_positiveNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath math = new MyMath();
        assertEquals(5, advancedMath.add(2, 3));
    }

    @Test
    void add_negativeNumberInput() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath math = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-2, 3));
    }

    @Test
    void add_overflow() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath math = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(Integer.MAX_VALUE, 1));
    }

    @Test
    void powerOfTwo_validPower() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(16, advancedMath.powerOfTwo(4));
    }

    @Test
    void powerOfTwo_zeroPower() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(1, advancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_negativePower() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_powerTooLarge() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    void reverseArrayNumbers_validArray() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath math = new MyMath();
        int[] array = {1, 2, 3};
        int[] reversedArray = advancedMath.reverseArrayNumbers(array, math);
        assertArrayEquals(new int[]{-1, -2, -3}, reversedArray);
    }
}
```

##### متد *add(int, int)*
سه متد تولید شدند که شامل موارد زیر هستند:
- اعداد x و y مثبت
- عدد x منفی و y مثبت
- تست سرریز

از آنجایی که *y < 0* در تست‌ها هرگز درست نیست، پوشش MC/DC و شرط وجود ندارد، بلکه پوشش تصمیم وجود دارد.
مقادیر مرزی مانند ۰ تست نشده‌اند.

##### متد *powerOfTwo(int)*
چهار متد تولید شدند که شامل موارد زیر هستند:
- کمی پایین‌تر از حد پایین
- حد پایین
- یک تست معمولی
- کمی بالاتر از حد بالا

ما دارای پوشش MC/DC هستیم.
مقادیر مرزی تا حدودی تست شده‌اند.
هیچ تست اضافی وجود ندارد.

##### متد *reverseArrayNumbers(int[], MyMath)*
فقط یک متد تولید شده است. موارد لبه‌ای فراوان مانند آرایه خالی تست نشده‌اند.
متد تست شده دارای پوشش MC/DC است.

#### واحد FileIO
```java
package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

class FileIOTestGemma3_1 {

    @Test
    void readFile_validFile() throws Exception {
        FileIO fileIO = new FileIO();
        // Create a temporary file for testing
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit(); // Delete when the test finishes.
        
        //Write lines to temp file
        java.io.FileWriter fw = new java.io.FileWriter(tempFile);
        fw.write("Line 1\n");
        fw.write("Line 2\n");
        fw.write("Line 3\n");
        fw.close();
        
        String[] expected = {"Line 1", "Line 2", "Line 3"};
        String[] actual = fileIO.readFile(tempFile.getAbsolutePath());
        assertArrayEquals(expected, actual);
    }

    @Test
    void readFile_emptyFile() throws Exception {
        FileIO fileIO = new FileIO();
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit();
        
        String[] expected = new String[0];
        String[] actual = fileIO.readFile(tempFile.getAbsolutePath());
        assertArrayEquals(expected, actual);
    }

}
```
دو متد تولید شدند که شامل موارد زیر هستند:
- یک تست معمولی
- یک فایل خالی

روش تست جونیتی با دایرکتوری موقت استفاده نشده است.
هیچ استثنایی وجود ندارد، بنابراین پوشش statement به دست نمی‌آید.
بسیاری از موارد لبه‌ای مانند فایل غیر موجود پوشش داده نشده‌اند.

#### واحد NameSearch
```java
package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NameSearchTestGemma3_1 {

    @Test
    void findNamesInFileThatStartWith_validPrefix() throws Exception {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();

        // Create a temporary file
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit();

        java.io.FileWriter fw = new java.io.FileWriter(tempFile);
        fw.write("Alice\n");
        fw.write("Bob\n");
        fw.write("Charlie\n");
        fw.close();

        String[] expected = {"Alice"};
        String[] actual = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        assertArrayEquals(expected, actual);
    }

    @Test
    void findNamesInFileThatStartWith_noMatchingPrefix() throws Exception {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();

        // Create a temporary file
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit();

        java.io.FileWriter fw = new java.io.FileWriter(tempFile);
        fw.write("Alice\n");
        fw.write("Bob\n");
        fw.write("Charlie\n");
        fw.close();

        String[] expected = new String[0];
        String[] actual = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "Z");
        assertArrayEquals(expected, actual);
    }
}
```
دو متد تولید شده‌اند که شامل موارد زیر هستند:
- یک تست با نتیجه
- یک تست بدون نتیجه

تست دارای پوشش MC/DC است.
بسیاری از موارد لبه‌ای پوشش داده نشده‌اند.

#### نتیجه‌گیری
این مدل فقط کمی از GPT OSS عملکرد ضعیف‌تری داشت. می‌توانید این را به پارامترهای پایین‌تر آن نسبت دهید.
بسیاری از مقادیر مرزی تست نشده‌اند.
نام متدها با آنچه که تست می‌کنند مطابقت دارند، اما با "test" شروع نمی‌شوند.
بسیاری از موارد لبه‌ای تست نشده‌اند.
این اولین مدلی بود که همه متدهای تولید شده آن فقط یک assertion داشتند.
از متغیرهای خصوصی استفاده نشده است.

### Gemma4:e4b
این مدل به نحوی توانست فاصله‌بندی فایل‌های تست FileIO و NameSearch را خراب کند و
یک فایل تکی بزرگ ایجاد کرد که دارای "\n" به جای خط جدید بود. من مجبور شدم این را برطرف کنم.
این مدل اصلاً از قرارداد نام‌گذاری ذکر شده پیروی نکرد!
و همچنین من مجبور شدم فایل‌ها را در فایل باز شده فعلی بنویسم.

#### واحد MyMath
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Unit tests for the MyMath class.
 * Must end with TestGemma4_1
 */
public class MyMathTest {

    private MyMath myMath;

    @BeforeEach
    void setUp() {
        myMath = new MyMath();
    }

    @Test
    void testCheckSignPositive() {
        assertEquals("positive", myMath.checkSign(10), "Should return 'positive' for positive numbers.");
    }

    @Test
    void testCheckSignNegative() {
        assertEquals("negative", myMath.checkSign(-5), "Should return 'negative' for negative numbers.");
    }

    @Test
    void testCheckSignZero() {
        // Testing for IllegalArgumentException when input is 0
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(0);
        });
        assertEquals("Number had no sign", exception.getMessage());
    }

    @Test
    void testReverseNumberPositive() {
        assertEquals(-123, myMath.reverseNumber(123), "Should correctly reverse a positive number.");
    }

    @Test
    void testReverseNumberNegative() {
        assertEquals(456, myMath.reverseNumber(-456), "Should correctly reverse a negative number.");
    }

    @Test
    void testReverseNumberZero() {
        assertEquals(0, myMath.reverseNumber(0), "Should return 0 for zero.");
    }
}
```
از متغیرهای خصوصی استفاده شده است و با استفاده از annotation @BeforeEach تنظیم می‌شوند که این با مدل‌های قبلی متفاوت است.

##### متد *checkSign(int)*
سه متد تولید شده‌اند که شامل موارد زیر هستند:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- صفر (مرز)

تست‌ها دارای پوشش MC/DC هستند.
باید ۱ و -۱ را به جای ۱۰ و -۵ تست می‌کردم.
همچنین پیام استثنا را بررسی کرده است که غیرضروری است.

##### متد *reverseNumber(int)*
سه متد تولید شده‌اند که شامل موارد زیر هستند:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- صفر (در مرز)

تست‌ها دارای پوشش MC/DC هستند.
انتخاب ۱۲۳ و -۴۵۶ جالب است، اما همچنان باید ۱ و -۱ را تست می‌کردید.

#### واحد MyAdvancedMath
```java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for the MyAdvancedMath class.
 * Must end with TestGemma4_1
 */
public class MyAdvancedMathTest {

    private MyAdvancedMath myAdvancedMath;
    private MyMath myMath;

    @BeforeEach
    void setUp() {
        myAdvancedMath = new MyAdvancedMath();
        myMath = new MyMath();
    }

    @Test
    void testAddPositiveNumbers() {
        // Test case 1: Simple addition
        assertEquals(5, myAdvancedMath.add(2, 3), "Should correctly add two small positive integers.");
        // Test case 2: Larger numbers
        assertEquals(100, myAdvancedMath.add(50, 50), "Should handle larger positive integers.");
    }

    @Test
    void testAddWithNegativeNumber() {
        // Testing for IllegalArgumentException when one or more inputs are negative
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 5);
        });
        assertEquals("Input numbers should be positive.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(5, -1);
        });
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    void testAddOverflow() {
        // Testing for Integer overflow
        // MAX_VALUE is 2,147,483,647. (MAX_VALUE - 1) + 2 > MAX_VALUE
        int x = Integer.MAX_VALUE - 5;
        int y = 10;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(x, y + 1); // This will overflow
        });
        assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
    }

    @Test
    void testPowerOfTwoZero() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0), "Power of two for 0 should be 1.");
    }

    @Test
    void testPowerOfTwoPositive() {
        assertEquals(16, myAdvancedMath.powerOfTwo(4), "2^4 should be 16.");
        assertEquals(1024, myAdvancedMath.powerOfTwo(10), "2^10 should be 1024.");
    }

    @Test
    void testPowerOfTwoNegative() {
        // Testing for IllegalArgumentException when power is negative
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    void testPowerOfTwoTooLarge() {
        // Testing for IllegalArgumentException when power > 30
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    void testReverseArrayNumbers() {
        // Input array: {1, 2, 3}
        int[] originalArray = {1, 2, 3};
        // Expected: {-1, -2, -3}
        int[] expected = {-1, -2, -3};
        
        // We must copy the array to ensure the original state isn't unexpectedly changed by the method signature
        int[] arrayCopy = Arrays.copyOf(originalArray, originalArray.length);

        int[] actual = myAdvancedMath.reverseArrayNumbers(arrayCopy, myMath);
        
        assertArrayEquals(expected, actual, "Should reverse the sign of every element in the array.");
    }
}
```

##### متد *add(int, int)*
سه متد تولید شده‌اند که شامل موارد زیر هستند:
- اعداد x و y مثبت
- x مثبت و y منفی و برعکس
- تست سرریز

تست‌ها دارای پوشش MC/DC هستند.
مقادیر مرزی، به ویژه ۰ تست نشده‌اند.
اشتباه است که y+1 را در assertion سرریز قرار داده‌اید. باید y=11 بود. با این حال، y=10 باعث سرریز می‌شد. با این وجود، y=10 باید تست می‌شد.
چند assertion در برخی از متدها استفاده شده است.

##### متد *powerOfTwo(int)*
چهار متد تولید شده‌اند. تست‌ها شامل موارد زیر هستند:
- کمی پایین‌تر از حد پایین
- حد پایین
- دو تست معمولی
- کمی بالاتر از حد بالا

ما دارای پوشش MC/DC هستیم.
مقادیر مرزی تا حدودی تست شده‌اند.
تست *power=10* اضافی است و به جای *power=4*، *power=1* باید تست می‌شد.

##### متد *reverseArrayNumbers(int[], MyMath)*
فقط یک متد تولید شده است. موارد لبه‌ای فراوان مانند آرایه خالی تست نشده‌اند.
تست MC/DC به دست آمده است.

#### واحد FileIO
```java
package dmst.seip.angor.junitdemo.io;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for the FileIO class.
 * Must end with TestGemma4_1
 */
public class FileIOTest {

    private FileIO fileIO;
    private Path tempFile; // Path to the temporary test file

    @BeforeEach
    void setUp() throws IOException {
        fileIO = new FileIO();
        // Create a temporary file for testing purposes
        tempFile = Files.createTempFile("test_input", ".txt");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up the temporary file
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testReadFileSuccess() throws IOException {
        // Setup: Write known content to the temporary file
        String content = "Line 1\nLine 2\nLine 3";
        Files.write(tempFile, content.getBytes());

        // Execution
        String[] result = fileIO.readFile(tempFile.toString());

        // Assertions
        assertNotNull(result, "The returned array should not be null.");
        assertEquals(3, result.length, "Should read exactly 3 lines.");
        assertArrayEquals(new String[]{"Line 1\n", "Line 2\n", "Line 3"}, result, "The content read should match the written content.");
    }

    @Test
    void testReadFileEmptyFile() throws IOException {
        // Setup: Create an empty temporary file
        Files.write(tempFile, "".getBytes());

        // Execution
        String[] result = fileIO.readFile(tempFile.toString());

        // Assertions (Note: BufferedReader reading an empty file might result in an array of size 1 containing an empty string or size 0 depending on OS/implementation, but expecting an empty array is safest for content check).
        // Given the implementation, reading an empty file usually results in an array containing one empty string or size 0 depending on how the stream conversion handles nulls/EOF. Let's check for size 1 with empty string if the OS/stream keeps one slot, or size 0 if it truly reads nothing.
        // Testing against the most robust expected outcome for 'no content' read.
        if (result.length == 1 && result[0].isEmpty()) {
            assertTrue(true, "Should handle reading an empty file gracefully.");
        } else if (result.length == 0) {
            assertTrue(true, "Should handle reading an empty file gracefully by returning empty array.");
        }
    }

    @Test
    void testReadFileFileNotFound() {
        // Setup: Use a file path guaranteed not to exist
        String nonExistentPath = tempFile.toString() + "_nonexistent";

        // Execution
        String[] result = fileIO.readFile(nonExistentPath);

        // Assertions: The implementation prints stack trace and returns an array based on the stream of null/empty list. We check for an empty/null array to indicate failure to read.
        assertNotNull(result);
        // Since IOException is caught and printed, the list remains empty.
        assertEquals(0, result.length, "Should return an empty array when the file does not exist.");
    }
}
```
سه روش تولید شدند که شامل موارد زیر هستند:
- یک تست معمولی
- یک تست با فایل خالی
- یک فایل غیر موجود

روش تست JUnit با استفاده از دایرکتوری موقت استفاده نشده است.
ما با یک استثنا مواجه می‌شویم، و ما MC/DC را به دست می‌آوریم.
این تنها مدلی بوده است که تا کنون هر دو حالت فایل خالی و فایل غیر موجود را پوشش داده است.
این فایل‌ها با فرمت باینری به فایل‌های موقت نوشته شده‌اند. این کار غیرضروری بود.
استفاده از اعلان‌های *@BeforeEach* و *@AfterEach* برای فایل‌های موقت یک ویژگی منحصر به فرد بود.

#### واحد NameSearch
***java
package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import dmst.seip.angor.junitdemo.io.FileIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * تست‌های واحد برای کلاس NameSearch.
 * باید با TestGemma4_1 به پایان برسد.
 */
public class NameSearchTest {

    private NameSearch nameSearch;
    private FileIO fileIO;
    private Path tempFile; // مسیری به فایل تست موقت

    @BeforeEach
    void setUp() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        // ایجاد یک فایل موقت برای اهداف تست
        tempFile = Files.createTempFile("search_input", ".txt");
    }

    @AfterEach
    void tearDown() throws IOException {
        // پاکسازی فایل موقت
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testFindNamesStartingWithPrefix() throws IOException {
        // تنظیم: نوشتن محتوای شناخته شده در فایل موقت
        String content = "Alice Smith\nBob Johnson\nAlicia Green\nCharles Brown";

        Files.write(tempFile, content.getBytes());

        // اجرا: جستجو برای نام‌هایی که با 'A' شروع می‌شوند
        String prefix = "A";
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, prefix);

        // ادعاها
        assertNotNull(result);
        // انتظار داریم "Alice Smith" و "Alicia Green"
        Set<String> expectedNames = new HashSet<>(Arrays.asList("Alice Smith", "Alicia Green"));
        Set<String> actualNames = new HashSet<>(Arrays.asList(result));

        assertEquals(2, result.length, "باید دقیقاً 2 نامی پیدا شوند که با 'A' شروع می‌شوند.");
        assertTrue(actualNames.containsAll(expectedNames) && expectedNames.containsAll(actualNames), "نام‌های پیدا شده باید با نام‌های مورد انتظار مطابقت داشته باشند.");
    }

    @Test
    void testFindNamesNoMatches() throws IOException {
        // تنظیم: نوشتن محتوا
        String content = "John Doe\nJane Doe";

        Files.write(tempFile, content.getBytes());

        // اجرا: جستجو برای نام‌هایی که با 'Z' شروع می‌شوند
        String prefix = "Z";
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, prefix);

        // ادعاها
        assertNotNull(result);
        assertEquals(0, result.length, "باید هیچ نامی پیدا نشود که با 'Z' شروع شود.");
    }

    @Test
    void testFindNamesEmptyFile() throws IOException {
        // تنظیم: ایجاد یک فایل موقت خالی
        Files.write(tempFile, "".getBytes());

        // اجرا: جستجو برای هر چیزی
        String prefix = "A";
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, prefix);

        // ادعاها
        assertNotNull(result);
        assertEquals(0, result.length, "باید یک آرایه خالی برای یک فایل ورودی خالی برگردانده شود.");
    }
}
***
سه روش تولید شدند که شامل موارد زیر هستند:
- یک تست معمولی
- یک تست بدون نتیجه
- یک تست فایل خالی

این تست MC/DC را به دست می‌آورد.
تا کنون این تنها مدلی بوده است که یک تست فایل خالی را تولید کرده است.

#### نتیجه‌گیری
این مدل تا کنون قهرمان موارد خاص بوده است! (دو مورد خاص دیگر را نیز تست کرده بود)
رفع فایل‌هایی که به درستی ایندنت نشده بودند برای من یک مشکل بود.
نیاز به تست مقادیر مرزی بیشتر وجود دارد.
نام روش‌ها با چیزی که تست می‌کنند مطابقت دارد و همچنین با "test" شروع می‌شوند.
برخی از روش‌ها چندین ادعا داشتند.
از متغیرهای خصوصی به جای ایجاد واحدهای مورد نیاز در هر روش استفاده شده است.

### Qwen-Coder2.5:7b
این مدل به طرز عجیبی رفتار کرد. این مدل تست‌های MyMath و MyAdvancedMath را در یک فایل واحد ترکیب کرد.
این مدل FileIO را اصلاً تست نکرد! این مدل فقط فایل‌ها را تولید کرد و هیچ زمینه یا فرآیند فکری دیگری نداشت.
خروجی فقط دو فایل بود!
من دوباره بررسی کردم تا مطمئن شوم که آیا من همه چهار فایل را به درخواست متصل کرده‌ام یا خیر.
#### واحدهای MyMath و MyAdvancedMath
***java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dmst.seip.angor.junitdemo.math.MyMath;
import dmst.seip.angor.junitdemo.math.MyAdvancedMath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MyMathTestQwen2_5_1 {
    private MyMath myMath;
    private MyAdvancedMath myAdvancedMath;

    @BeforeEach
    public void setUp() {
        myMath = new MyMath();
        myAdvancedMath = new MyAdvancedMath();
    }

    @Test
    public void testCheckSignPositiveNumber() {
        assertEquals("positive", myMath.checkSign(10));
    }

    @Test
    public void testCheckSignNegativeNumber() {
        assertEquals("negative", myMath.checkSign(-20));
    }

    @Test
    public void testCheckSignZero() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    public void testReverseNumber() {
        assertEquals(-15, myMath.reverseNumber(15));
    }

    @Test
    public void testAddPositiveNumbers() {
        int result = myAdvancedMath.add(10, 20);
        assertEquals(30, result);
    }

    @Test
    public void testAddNegativeNumbersThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-5, -10));
    }

    @Test
    public void testAddIntegerOverflowThrowsException() {
        int maxInt = Integer.MAX_VALUE;
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(maxInt, 1));
    }

    @Test
    public void testPowerOfTwoZero() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoPositiveNumber() {
        assertEquals(16, myAdvancedMath.powerOfTwo(4));
    }

    @Test
    public void testPowerOfTwoNegativeNumberThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-2));
    }

    @Test
    public void testPowerOfTwoTooLargeNumberThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
    }
}
***
می‌توانم بلافاصله بگویم که این بدترین تست تولید شده تاکنون بوده است.
من قبلاً توضیح داده‌ام که این چه مشکلی دارد.

#### روش *checkSign(int)*
سه روش تولید شدند که شامل موارد زیر هستند:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- صفر (مرز)

تست‌ها دارای MC/DC هستند.
من ۱ و -۱ را به جای ۱۰ و -۲۰ تست می‌کردم.

#### روش *reverseNumber(int)*
فقط یک روش تولید شد که یک عدد منفی معمولی است.

تست‌ها دارای MC/DC هستند. منظورم این است که این روش فقط یک تخصیص است.
این روش نیاز به تست‌های بسیار بیشتری دارد.

#### روش *add(int, int)*
سه روش تولید شدند که شامل موارد زیر هستند:
- اعداد x و y مثبت
- اعداد x و y منفی
- تست سرریز

تست‌ها پوشش Condition/Decision را دارند.
مقادیر حد و مرز نادیده گرفته شده‌اند و حتی MC/DC هم وجود ندارد.

##### متد *powerOfTwo(int)*
چهار متد تولید شده است. تست‌ها شامل موارد زیر است:
- یک تست با توان منفی
- حد پایین
- یک تست معمولی
- کمی بالاتر از حد بالا

ما MC/DC را داریم.  
مقادیر حد و مرز تست نشده‌اند.  
به جای -۲، بهتر بود -۱ تست می‌شد.

##### متد *reverseArrayNumbers(int[], MyMath)*
این متد اصلاً تست نشده است!

#### واحد FileIO
این واحد تست نشده است!

#### واحد NameSearch
***java
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import dmst.seip.angor.junitdemo.io.FileIO;
import dmst.seip.angor.junitdemo.search.NameSearch;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NameSearchTestQwen2_5_1 {
    private FileIO fileIo;
    private NameSearch nameSearch;

    @BeforeEach
    public void setUp() {
        fileIo = new FileIO();
        nameSearch = new NameSearch();
    }

    @Test
    public void testFindNamesInFileThatStartWithPrefix() {
        String[] names = {"Alice", "Bob", "Charlie"};
        File tempFile = createTempFile("test.txt", Arrays.toString(names));
        
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIo, "A");

        assertArrayEquals(new String[]{"Alice"}, result);
    }

    private File createTempFile(String fileName, String content) {
        // Logic to create a temporary file with the given content
        return new File(fileName);
    }
}

***
یک متد برای تست تولید شده است که شامل یک تست معمولی است.
به نظر می‌رسد محتوا را در فایل نمی‌نویسد! این تست کاملاً اشتباه است.  
همچنین فایل موقت را حذف نمی‌کند.

#### نتیجه‌گیری
این مدل خیلی بد عمل کرد. فکر می‌کنم به اندازه کافی توضیح داده‌ام.
نام متدها با آنچه تست می‌کنند مطابقت دارد و همچنین با test شروع می‌شوند.  
بسیاری از موارد لبه تست نشده‌اند.
از متغیرهای خصوصی استفاده شده است.

### Qwen-Coder3:30b
آخرین مدل برای تست. بین مدل‌های محلی بالاترین تعداد پارامتر را دارد، بنابراین انتظارات
بالا است. مجبور بودم فایل‌ها را دستی بسازم. این بیشتر برای مدل‌های محلی همین‌طور بود.
این بیشتر احتمالاً مشکلی در ارائه‌دهنده Ollama است.  
همچنین از اینکه @author را اضافه کرده خوشم می‌آید، در حالی که نویسنده خودِ چت‌بات است!

#### واحد MyMath
***java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MyMath
 * @author agkortzis (antonis.gkortzis@gmail.com)
 */
public class MyMathTestQwen3_1 {

    private MyMath myMath = new MyMath();

    @Test
    public void testCheckSignPositive() {
        assertEquals("positive", myMath.checkSign(5));
    }

    @Test
    public void testCheckSignNegative() {
        assertEquals("negative", myMath.checkSign(-3));
    }

    @Test
    public void testCheckSignZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myMath.checkSign(0));
        assertEquals("Number had no sign", exception.getMessage());
    }

    @Test
    public void testReverseNumberPositive() {
        assertEquals(-5, myMath.reverseNumber(5));
    }

    @Test
    public void testReverseNumberNegative() {
        assertEquals(3, myMath.reverseNumber(-3));
    }

    @Test
    public void testReverseNumberZero() {
        assertEquals(0, myMath.reverseNumber(0));
    }
}
***

##### متد *checkSign(int)*
سه متد تولید شد که شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- ۰ (حد و مرز)

تست‌ها دارای MC/DC هستند.  
من ۱ و -۱ را به جای ۵ و -۳ تست می‌کردم. این اعداد در مقایسه با مدل‌های قبلی به
مرز نزدیک‌ترند.

##### متد *reverseNumber(int)*
سه متد تولید شد که شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- ۰ (در مرز)

تست‌ها دارای MC/DC هستند.  
من ۱ و -۱ را به جای ۵ و -۳ تست می‌کردم.

#### واحد MyAdvancedMath
***java
package dmst.seip.angor.junitdemo.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MyAdvancedMath
 * @author agkortzis (antonis.gkortzis@gmail.com)
 */
public class MyAdvancedMathTestQwen3_1 {

    private MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
    private MyMath myMath = new MyMath();

    @Test
    public void testAddPositiveNumbers() {
        assertEquals(8, myAdvancedMath.add(3, 5));
    }

    @Test
    public void testAddNegativeNumbers() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myAdvancedMath.add(-3, 5));
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    public void testAddOverflow() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myAdvancedMath.add(Integer.MAX_VALUE, 1));
        assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
    }

    @Test
    public void testPowerOfTwoZero() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoValid() {
        assertEquals(8, myAdvancedMath.powerOfTwo(3));
    }

    @Test
    public void testPowerOfTwoNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myAdvancedMath.powerOfTwo(-1));
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    public void testPowerOfTwoTooLarge() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> myAdvancedMath.powerOfTwo(31));
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    public void testReverseArrayNumbers() {
        int[] input = {1, -2, 3, -4};
        int[] expected = {-1, 2, -3, 4};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }

    @Test
    public void testReverseArrayNumbersEmpty() {
        int[] input = {};
        int[] expected = {};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }
}
***
##### متد *add(int, int)*
سه متد تولید شد که شامل موارد زیر است:
- x و y مثبت
- x منفی و y مثبت
- تست سرریز

تست‌ها پوشش decision را دارند. y هرگز منفی نیست.  
مقادیر حد و مرز تست نشده‌اند.

##### متد *powerOfTwo(int)*
چهار متد تولید شده است. تست‌ها شامل موارد زیر است:
- کمی پایین‌تر از حد پایین
- حد پایین
- یک تست معمولی
- کمی بالاتر از حد بالا

ما MC/DC را داریم.  
مقادیر حد و مرز تا حدی تست شده‌اند.

##### متد *reverseArrayNumbers(int[], MyMath)*
این تنها مدلی است که برای این متد دو تست دارد. یک تست معمولی و یک تست آرایه خالی را
پوشش می‌دهد.

#### واحد FileIO
***java
package dmst.seip.angor.junitdemo.io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Test class for FileIO
 * @author agkortzis (antonis.gkortzis@gmail.com)
 */
public class FileIOTestQwen3_1 {

    private FileIO fileIO = new FileIO();

    @Test
    public void testReadFile() throws IOException {
        // Create a temporary file for testing
        File tempFile = File.createTempFile("test", ".txt");
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\n");
        writer.write("Bob\n");
        writer.write("Charlie\n");
        writer.close();

        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        
        assertEquals(3, result.length);
        assertEquals("Alice", result[0]);
        assertEquals("Bob", result[1]);
        assertEquals("Charlie", result[2]);

        // Clean up
        tempFile.delete();
    }

    @Test
    public void testReadFileEmpty() throws IOException {
        // Create a temporary empty file for testing
        File tempFile = File.createTempFile("testEmpty", ".txt");
        
        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        
        assertEquals(0, result.length);

        // Clean up
        tempFile.delete();
    }

    @Test
    public void testReadFileNonExistent() {
        // Test with a non-existent file - should not throw exception but return empty array
        String[] result = fileIO.readFile("non_existent_file.txt");
        assertEquals(0, result.length);
    }
}
***
سه متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست با فایل خالی
- یک فایل ناموجود

می‌شد پوشه موقت را با annotation ‎@TempDir تست کرد.


#### واحد NameSearch
***java
package dmst.seip.angor.junitdemo.search;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import dmst.seip.angor.junitdemo.io.FileIO;

/**
 * Test class for NameSearch
 * @author agkortzis (antonis.gkortzis@gmail.com)
 */
public class NameSearchTestQwen3_1 {

  private NameSearch nameSearch = new NameSearch();
  private FileIO fileIO = new FileIO();

  @Test
  public void testFindNamesInFileThatStartWith() throws IOException {
    // Create a temporary file for testing
    File tempFile = File.createTempFile("names", ".txt");
    FileWriter writer = new FileWriter(tempFile);
    writer.write("Alice\n");
    writer.write("Bob\n");
    writer.write("Charlie\n");
    writer.write("Amanda\n");
    writer.close();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "A");

    assertEquals(2, result.length);
    assertEquals("Alice", result[0]);
    assertEquals("Amanda", result[1]);

    // Clean up
    tempFile.delete();
  }

  @Test
  public void testFindNamesInFileThatStartWithNoMatch() throws IOException {
    // Create a temporary file for testing
    File tempFile = File.createTempFile("names", ".txt");
    FileWriter writer = new FileWriter(tempFile);
    writer.write("Alice\n");
    writer.write("Bob\n");
    writer.write("Charlie\n");
    writer.close();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "Z");

    assertEquals(0, result.length);

    // Clean up
    tempFile.delete();
  }

  @Test
  public void testFindNamesInFileThatStartWithEmptyFile() throws IOException {
    // Create a temporary empty file for testing
    File tempFile = File.createTempFile("names", ".txt");

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "A");

    assertEquals(0, result.length);

    // Clean up
    tempFile.delete();
  }
}
***
سه متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست بدون نتیجه
- یک تست با فایل خالی

تست به MC/DC می‌رسد.  
برخی موارد لبه مانند پیشوند خالی تست نشده‌اند.

#### نتیجه‌گیری
مدل قابل قبول عمل کرد.
به مقادیر حد و مرز بیشتری نیاز دارد  
نام متدها با آنچه تست می‌کنند مطابقت دارد و با test شروع می‌شوند.  
موارد لبه بیشتری می‌توانست تست شوند، اگرچه این مدل بهترین کار را انجام داد.
از متغیرهای خصوصی استفاده شده است.

### مقایسه مدل‌های محلی
همه مدل‌ها در تست واحد MyMath برابر بودند، به جز qwen 2.5 که فقط یک عدد منفی معمولی را
در متد reverse تست کرد.

برای MyAdvancedMath، با پوشش موارد بیشتر، Qwen3 برنده است، در حالی که خویشاوندش
Qwen2.5 بدترین بود.  

برای واحدهای FileIO و NameSearch، Gemma4 و Qwen3 برنده هستند.  

اگر مجبور بودم این مدل‌ها را رتبه‌بندی کنم، نتیجه به این صورت می‌شد:
1. Qwen 3 Coder 30b
2. Gemma4 e4b
3. GPT OSS 20b
4. Gemma3 12b
5. Qwen 2.5 Coder 7b


## دور ۲
حالا که دور ۱ تمام شد. اعلان را به یک اعلان پیچیده‌تر بر اساس اشکالات تست‌های تولید شده در دور قبلی
گسترش می‌دهم. همه مدل‌ها در تست مقادیر حد و مرز ضعیف عمل کردند،
بنابراین تمرکز اصلی این اعلان روی همین است، در حالی که همزمان
سعی می‌کنم تعداد تست‌ها را کاهش دهم. بسیاری از مدل‌ها چند assertion در آن‌ها داشتند، بنابراین همچنین
از عامل‌ها می‌خواهم در هر متد از یک assertion استفاده کنند. تنها یک مدل از ذخیره‌سازی موقت JUnit استفاده کرد،
بنابراین آن هم اضافه خواهد شد. همچنین می‌خواهم مدل‌ها برای موارد لبه تست ایجاد کنند.
> برای این واحدها، تست‌های واحد را با استفاده از JUnit5 تولید کنید. از mock برای وابستگی‌ها استفاده نکنید.
> روی مقادیر حد و مرز تمرکز کنید و آن‌ها را با تست‌های معمولی جایگزین کنید (مثالاً اگر ۰ یک حد و مرز است،
> -۱، ۰ و ۱ را تست کنید). سعی کنید در هر متد فقط از یک assertion استفاده کنید. از @TempDir مربوط به JUnit برای
> ذخیره‌سازی موقت استفاده کنید. مطمئن شوید که موارد لبه زیادی مانند آرایه‌ها یا رشته‌های خالی را تست می‌کنید. کلاس‌های تست
> تولید شده باید با "Test(ModelName)_P2" به پایان برسند.

### Mistral Devstral Medium
عامل فایل‌های تست را تولید کرد و آنها را در دایرکتوری تست قرار داد.

#### واحد MyMath
***java
public class MyMathTestMistral_P2 {

    @Test
    public void testCheckSignPositive() {
        MyMath myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    public void testCheckSignNegative() {
        MyMath myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    public void testCheckSignZero() {
        MyMath myMath = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    public void testReverseNumberPositive() {
        MyMath myMath = new MyMath();
        assertEquals(-5, myMath.reverseNumber(5));
    }

    @Test
    public void testReverseNumberNegative() {
        MyMath myMath = new MyMath();
        assertEquals(5, myMath.reverseNumber(-5));
    }

    @Test
    public void testReverseNumberZero() {
        MyMath myMath = new MyMath();
        assertEquals(0, myMath.reverseNumber(0));
    }
}
***

##### متد *checkSign(int)*
سه متد تولید شد که شامل موارد زیر است:
- کمی بالاتر از حد و مرز
- کمی پایین‌تر از حد و مرز
- ۰ (حد و مرز)

تست‌ها پوشش MC/DC را دارند.
این دقیقاً همان چیزی است که انتظارش را داشتم.

##### متد *reverseNumber(int)*
سه متد تولید شد که شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- ۰ (در مرز)

تست‌ها پوشش MC/DC را دارند.  
خب، برای این تابع هیچ بررسی مقدار حد و مرزی وجود ندارد و
در واقع، همانند دور قبلی است

#### واحد MyAdvancedMath
***java
public class MyAdvancedMathTestMistral_P2 {

    @Test
    public void testAddPositiveNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(5, advancedMath.add(2, 3));
    }

    @Test
    public void testAddNegativeNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-1, 5));
    }

    @Test
    public void testAddOverflow() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(Integer.MAX_VALUE, 1));
    }

    @Test
    public void testPowerOfTwoZero() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(1, advancedMath.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoPositive() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertEquals(8, advancedMath.powerOfTwo(3));
    }

    @Test
    public void testPowerOfTwoNegative() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
    }

    @Test
    public void testPowerOfTwoOverflow() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    public void testReverseArrayNumbers() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath myMath = new MyMath();
        int[] array = {1, -2, 3, -4};
        int[] expected = {-1, 2, -3, 4};
        assertArrayEquals(expected, advancedMath.reverseArrayNumbers(array, myMath));
    }

    @Test
    public void testReverseArrayNumbersEmpty() {
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        MyMath myMath = new MyMath();
        int[] array = {};
        int[] expected = {};
        assertArrayEquals(expected, advancedMath.reverseArrayNumbers(array, myMath));
    }
}

***
##### متد *add(int, int)*
سه متد تولید شد که شامل موارد زیر است:
- x و y مثبت
- x کمی پایین‌تر از حد و مرز است و y مثبت
- تست سرریز

تست‌ها پوشش decision را دارند. y هرگز منفی نیست.  
مقادیر حد و مرز به اندازه کافی تست نشده‌اند!  
احتمالاً باید تست‌های بسیار بیشتری اضافه می‌شد.  
تعجب‌آور اینکه، عامل در این متد بدتر از دور قبلی عمل کرد.

##### متد *powerOfTwo(int)*
چهار متد تولید شده است. تست‌ها شامل موارد زیر است:
- کمی پایین‌تر از حد پایین
- حد پایین
- یک تست معمولی
- کمی بالاتر از حد بالا

ما MC/DC را داریم.  
در دور قبلی، عامل حد بالا و کمی بالاتر از حد پایین را پوشش داد،
اما اینجا آن‌ها را پوشش نداد. اگرچه اینجا حد پایین پوشش داده شده و تکرارها کمتر
است.

##### متد *reverseArrayNumbers(int[], MyMath)*
دو متد تولید شد. شامل موارد زیر است:
- یک تست معمولی
- یک آرایه خالی

#### واحد FileIO
***java
public class FileIOTestMistral_P2 {

    @TempDir
    Path tempDir;

    @Test
    public void testReadFile() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, "Line 1\nLine 2\nLine 3".getBytes());
        
        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile(testFile.toString());
        
        assertEquals(3, content.length);
        assertEquals("Line 1", content[0]);
        assertEquals("Line 2", content[1]);
        assertEquals("Line 3", content[2]);
    }

    @Test
    public void testReadEmptyFile() throws IOException {
        Path testFile = tempDir.resolve("empty.txt");
        Files.write(testFile, "".getBytes());
        
        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile(testFile.toString());
        
        assertEquals(0, content.length);
    }

    @Test
    public void testReadNonExistentFile() {
        FileIO fileIO = new FileIO();
        String[] content = fileIO.readFile("nonexistent.txt");
        
        assertEquals(0, content.length);
    }
}

***
سه متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست با فایل خالی
- یک فایل ناموجود

از ذخیره‌سازی موقت JUnit استفاده شده است.  
تست فایل ناموجود نسبت به دور قبلی جدید است و MC/DC محقق شده است.

#### واحد NameSearch
***java
public class NameSearchTestMistral_P2 {

    @TempDir
    Path tempDir;

    @Test
    public void testFindNamesInFileThatStartWith() throws IOException {
        Path testFile = tempDir.resolve("names.txt");
        Files.write(testFile, "Alice\nBob\nAnna\nAndrew\nEve".getBytes());
        
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        String[] result = nameSearch.findNamesInFileThatStartWith(testFile.toString(), fileIO, "A");
        
        assertEquals(3, result.length);
        assertEquals("Alice", result[0]);
        assertEquals("Anna", result[1]);
        assertEquals("Andrew", result[2]);
    }

    @Test
    public void testFindNamesInFileThatStartWithEmptyPrefix() throws IOException {
        Path testFile = tempDir.resolve("names.txt");
        Files.write(testFile, "Alice\nBob\nAnna".getBytes());
        
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        String[] result = nameSearch.findNamesInFileThatStartWith(testFile.toString(), fileIO, "");
        
        assertEquals(3, result.length);
    }

    @Test
    public void testFindNamesInFileThatStartWithNoMatches() throws IOException {
        Path testFile = tempDir.resolve("names.txt");
        Files.write(testFile, "Alice\nBob\nAnna".getBytes());
        
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        String[] result = nameSearch.findNamesInFileThatStartWith(testFile.toString(), fileIO, "Z");
        
        assertEquals(0, result.length);
    }

    @Test
    public void testFindNamesInFileThatStartWithEmptyFile() throws IOException {
        Path testFile = tempDir.resolve("empty.txt");
        Files.write(testFile, "".getBytes());
        
        NameSearch nameSearch = new NameSearch();
        FileIO fileIO = new FileIO();
        String[] result = nameSearch.findNamesInFileThatStartWith(testFile.toString(), fileIO, "A");
        
        assertEquals(0, result.length);
    }
}
***
چهار متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست بدون نتیجه
- یک تست با فایل خالی
- یک تست با پیشوند خالی

تست به MC/DC می‌رسد.  
فکر می‌کنم این تست‌ها کافی هستند.

#### نتیجه‌گیری
قطعاً بهبودهایی وجود دارد. به نظرم FileIO و NameSearch واقعاً نیازی به بهبود ندارند.
تست‌های MyAdvancedMath می‌توانند بهتر باشند. البته از متغیرهای خصوصی استفاده نکرد.


### Gemini 3 Flash
عامل فایل‌های تست را تولید کرد و آنها را در دایرکتوری تست قرار داد که این درست است.
#### واحد MyMath
***java
public class MyMathTestGemini_P2 {

    private final MyMath mm = new MyMath();

    @Test
    public void testCheckSignPositive() {
        Assertions.assertEquals("positive", mm.checkSign(1));
    }

    @Test
    public void testCheckSignPositiveMoreThanOne() {
        Assertions.assertEquals("positive", mm.checkSign(2));
    }

    @Test
    public void testCheckSignNegative() {
        Assertions.assertEquals("negative", mm.checkSign(-1));
    }

    @Test
    public void testCheckSignNegativeLessThanMinusOne() {
        Assertions.assertEquals("negative", mm.checkSign(-2));
    }

    @Test
    public void testCheckSignZeroThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
    }

    @Test
    public void testReverseNumberPositive() {
        Assertions.assertEquals(-5, mm.reverseNumber(5));
    }

    @Test
    public void testReverseNumberNegative() {
        Assertions.assertEquals(5, mm.reverseNumber(-5));
    }

    @Test
    public void testReverseNumberZero() {
        Assertions.assertEquals(0, mm.reverseNumber(0));
    }
}

***
برخلاف mistral، gemini هم دوباره از یک متغیر خصوصی برای واحد استفاده کرد!
##### متد *checkSign(int)*
پنج متد تولید شد که شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- کمی پایین‌تر از حد و مرز
- کمی بالاتر از حد و مرز
- ۰ (حد و مرز)

تست‌ها پوشش MC/DC را دارند.  
تنها بهبودی که برای این تست‌ها به ذهنم می‌رسد، تست‌های پارامتری است.

##### متد *reverseNumber(int)*
سه متد تولید شد که شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- ۰ (در مرز)

تست‌ها پوشش MC/DC را دارند.  
من ۱ و -۱ را به جای ۵ و -۵ تست می‌کردم.  
نسبت به دور قبلی همان کار را انجام داد.

#### واحد MyAdvancedMath
***java
public class MyAdvancedMathTestGemini_P2 {

    private final MyAdvancedMath mam = new MyAdvancedMath();

    @Test
    public void testAddBothZero() {
        Assertions.assertEquals(0, mam.add(0, 0));
    }

    @Test
    public void testAddOneZeroFirst() {
        Assertions.assertEquals(5, mam.add(0, 5));
    }

    @Test
    public void testAddOneZeroSecond() {
        Assertions.assertEquals(5, mam.add(5, 0));
    }

    @Test
    public void testAddNormalPositive() {
        Assertions.assertEquals(10, mam.add(4, 6));
    }

    @Test
    public void testAddNegativeXThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.add(-1, 5));
    }

    @Test
    public void testAddNegativeYThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.add(5, -1));
    }

    @Test
    public void testAddOverflowBoundary() {
        Assertions.assertEquals(Integer.MAX_VALUE, mam.add(Integer.MAX_VALUE, 0));
    }

    @Test
    public void testAddOverflowThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.add(Integer.MAX_VALUE, 1));
    }

    @Test
    public void testPowerOfTwoZero() {
        Assertions.assertEquals(1, mam.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoOne() {
        Assertions.assertEquals(2, mam.powerOfTwo(1));
    }

    @Test
    public void testPowerOfTwoThirty() {
        Assertions.assertEquals(1073741824, mam.powerOfTwo(30));
    }

    @Test
    public void testPowerOfTwoNegativeThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.powerOfTwo(-1));
    }

    @Test
    public void testPowerOfTwoThirtyOneThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> mam.powerOfTwo(31));
    }

    @Test
    public void testReverseArrayNumbersNormal() {
        MyMath mm = new MyMath();
        int[] input = {1, -2, 0};
        int[] expected = {-1, 2, 0};
        Assertions.assertArrayEquals(expected, mam.reverseArrayNumbers(input, mm));
    }

    @Test
    public void testReverseArrayNumbersEmpty() {
        MyMath mm = new MyMath();
        int[] input = {};
        int[] expected = {};
        Assertions.assertArrayEquals(expected, mam.reverseArrayNumbers(input, mm));
    }
}
***


می‌توانم بگویم که بهبود چشمگیری صورت گرفته است.
##### متد *add(int, int)*
هشت متد تولید شد که شامل موارد زیر است:
- x و y مثبت
- x مثبت و y منفی و برعکس
- هر دو صفر
- x صفر است و y مثبت و برعکس
- کمی پایین‌تر از سرریز
- تست سرریز

تست‌ها واقعاً پوشش MC/DC را دارند.  
قطعاً می‌شد تست بیشتری روی مقادیر حد و مرز انجام شود.

##### متد *powerOfTwo(int)*
پنج متد تولید شد. تست‌ها شامل موارد زیر است:
- کمی پایین‌تر از حد پایین
- حد پایین
- کمی بالاتر از حد پایین
- حد بالا
- کمی بالاتر از حد بالا

ما MC/DC را داریم.  
مقادیر حد و مرز خیلی خوب تست شده‌اند.  
جالب اینکه در مقایسه با دور قبل، فقط یک تست تکراری حذف شده است.

##### متد *reverseArrayNumbers(int[], MyMath)*
دو متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست با آرایه خالی

تست آرایه خالی در دور قبل تولید نشده بود.  
MC/DC محقق شده است.

#### واحد FileIO
***java
public class FileIOTestGemini_P2 {

  private final FileIO fileIO = new FileIO();

  @Test
  public void testReadFileNormal(@TempDir Path tempDir) throws IOException {
    Path tempFile = tempDir.resolve("test.txt");
    List<String> expectedLines = Arrays.asList("Alice", "Bob", "Charlie");
    Files.write(tempFile, expectedLines);

    String[] result = fileIO.readFile(tempFile.toString());
    Assertions.assertArrayEquals(expectedLines.toArray(new String[0]), result);
  }

  @Test
  public void testReadFileEmpty(@TempDir Path tempDir) throws IOException {
    Path tempFile = tempDir.resolve("empty.txt");
    Files.createFile(tempFile);

    String[] result = fileIO.readFile(tempFile.toString());
    Assertions.assertEquals(0, result.length);
  }

  @Test
  public void testReadNonExistentFile() {
    // The implementation catches IOException and prints stack trace, returning empty array.
    String[] result = fileIO.readFile("non_existent_file.txt");
    Assertions.assertEquals(0, result.length);
  }
}
***
سه متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست با فایل خالی
- یک فایل ناموجود

به استثنا برخورد می‌کنیم، بنابراین MC/DC محقق می‌شود.  
در مقایسه با دور قبلی، تست فایل خالی اضافه شده است.

#### واحد NameSearch
***java
public class NameSearchTestGemini_P2 {

  private final NameSearch nameSearch = new NameSearch();
  private final FileIO fileIO = new FileIO();

  @Test
  public void testFindNamesPrefixed(@TempDir Path tempDir) throws IOException {
    Path tempFile = tempDir.resolve("names.txt");
    List<String> names = Arrays.asList("Alice", "Bob", "Anna", "Alex");
    Files.write(tempFile, names);

    String[] expected = {"Alice", "Anna", "Alex"};
    String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");

    Assertions.assertArrayEquals(expected, result);
  }

  @Test
  public void testFindNamesNoMatch(@TempDir Path tempDir) throws IOException {
    Path tempFile = tempDir.resolve("names.txt");
    List<String> names = Arrays.asList("Alice", "Bob", "Anna");
    Files.write(tempFile, names);

    String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "Z");

    Assertions.assertEquals(0, result.length);
  }

  @Test
  public void testFindNamesEmptyFile(@TempDir Path tempDir) throws IOException {
    Path tempFile = tempDir.resolve("empty.txt");
    Files.createFile(tempFile);

    String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");

    Assertions.assertEquals(0, result.length);
  }

  @Test
  public void testFindNamesEmptyPrefix(@TempDir Path tempDir) throws IOException {
    Path tempFile = tempDir.resolve("names.txt");
    List<String> names = Arrays.asList("Alice", "Bob");
    Files.write(tempFile, names);

    // Every string starts with empty string
    String[] expected = {"Alice", "Bob"};
    String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "");

    Assertions.assertArrayEquals(expected, result);
  }
}
***
چهار متد با چند assertion تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست بدون نتیجه
- یک تست با فایل خالی
- یک تست با پیشوند خالی

تست به MC/DC می‌رسد.  
در مقایسه با دور قبلی، تست‌های فایل خالی و پیشوند خالی اضافه شده‌اند.

#### نتیجه‌گیری
قطعاً بهبودهایی قابل مشاهده است.
نام متدها با آنچه تست می‌کنند مطابقت دارد و همچنین با test شروع می‌شوند.  
موارد لبه به خوبی تست شده‌اند.

### Devstral در مقابل Gemini
حالا که تست‌های تولید شده توسط دو مدل بزرگمان را دیدیم، می‌توانیم آن‌ها را مقایسه کنیم.  
قطعاً Gemini بهتر عمل کرد. بیایید تفاوت‌ها را بررسی کنیم:
- Gemini برای عدد مثبت و منفی در checkSing یک تست معمولی داشت (اگرچه غیرضروری بود).
- Gemini سناریوهای بیشتری را برای متد add پوشش می‌دهد.
- Gemini دو سناریوی بیشتر را برای متد powerOfTwo پوشش می‌دهد.
- Gemini از متغیرهای خصوصی استفاده کرد.

بله، این بار Gemini غالب بود.

### Gemma3:12b
باید ذکر کنم که همین حالا هر ۵ مدل محلی را روی کامپیوترم نصب کرده‌ام و آن‌ها را با سخت‌افزار
خودم تست کردم. اعلان را فرستادم و فایل را در پوشه تست ساخت. مشکل این بود که فایل‌ها را تولید
کرد، اما ساختن آن‌ها طول کشید؟
#### واحد MyMath
***java
public class MyMathTestGemma3_P2 {

    private MyMath myMath = new MyMath();

    @Test
    void checkSign_positiveNumber() {
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    void checkSign_negativeNumber() {
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    void checkSign_zeroNumber_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
        assertEquals("Number had no sign", exception.getMessage());
    }

    @Test
    void reverseNumber_positiveNumber() {
        assertEquals(-1, myMath.reverseNumber(1));
    }

    @Test
    void reverseNumber_negativeNumber() {
        assertEquals(1, myMath.reverseNumber(-1));
    }

    @Test
    void reverseNumber_zeroNumber() {
        assertEquals(0, myMath.reverseNumber(0));
    }
}
***
برخلاف دفعه قبل، حالا از متغیرهای خصوصی استفاده می‌کند.
##### متد *checkSign(int)*
سه متد تولید شد که شامل موارد زیر است:
- کمی بالاتر از حد و مرز
- کمی پایین‌تر از حد و مرز
- ۰ (حد و مرز)

تست‌ها پوشش MC/DC را دارند.  
کافی است. نقص دور قبل را پوشش داد.

##### متد *reverseNumber(int)*
سه متد تولید شد که شامل موارد زیر است:
- کمی بالاتر از حد و مرز
- کمی پایین‌تر از حد و مرز
- ۰ (در مرز)

تست‌ها پوشش MC/DC را دارند.  
برخلاف مدل‌های بزرگ، همه مقادیر حد و مرز را برای این متد تست کرد.

#### واحد MyAdvancedMath
***java
public class MyAdvancedMathTestGemma3_P2 {

    private MyAdvancedMath advancedMath = new MyAdvancedMath();

    @Test
    void add_positiveNumbers() {
        assertEquals(5, advancedMath.add(2, 3));
    }

    @Test
    void add_negativeNumber_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-1, 2));
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    void add_overflow_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> advancedMath.add(Integer.MAX_VALUE, 1));
        assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
    }

    @Test
    void powerOfTwo_zeroPower() {
        assertEquals(1, advancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_onePower() {
        assertEquals(2, advancedMath.powerOfTwo(1));
    }

    @Test
    void powerOfTwo_negativePower_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    void powerOfTwo_maxPower() {
        assertEquals(1073741824, advancedMath.powerOfTwo(30));
    }

    @Test
    void powerOfTwo_tooLargePower_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    void reverseArrayNumbers_emptyArray() {
        MyMath mm = new MyMath();
        int[] array = {};
        int[] reversed = advancedMath.reverseArrayNumbers(array, mm);
        assertArrayEquals(new int[]{}, reversed);
    }

    @Test
    void reverseArrayNumbers_singleElementArray() {
        MyMath mm = new MyMath();
        int[] array = {5};
        int[] reversed = advancedMath.reverseArrayNumbers(array, mm);
        assertArrayEquals(new int[]{-5}, reversed);
    }

    @Test
    void reverseArrayNumbers_multipleElementsArray() {
        MyMath mm = new MyMath();
        int[] array = {1, 2, 3};
        int[] reversed = advancedMath.reverseArrayNumbers(array, mm);
        assertArrayEquals(new int[]{-1, -2, -3}, reversed);
    }
}
***
##### متد *add(int, int)*
سه متد تولید شد که شامل موارد زیر است:
- x و y مثبت
- x کمی پایین‌تر از حد و مرز است و y مثبت
- تست سرریز

از آنجا که *y < 0* هرگز در تست‌ها درست نیست، پوشش MC/DC و condition را نداریم، در عوض
پوشش decision را داریم.
بد عمل کرد. سه تست کافی نیست.  
در مقایسه با دور آخر تست، تنها بهبود جزئی وجود دارد(-۱, ۲)

##### متد *powerOfTwo(int)*
پنج متد تولید شد که شامل موارد زیر است:
- کمی پایین‌تر از حد پایین
- حد پایین
- کمی بالاتر از حد پایین
- حد بالا
- کمی بالاتر از حد و مرز

ما MC/DC را داریم.  
مقادیر حد و مرز خوب تست شده‌اند.  
هیچ تست تکراری وجود ندارد.  
کمی بالاتر از حد پایین و حد بالا در مقایسه با دور قبل جدید هستند.

##### متد *reverseArrayNumbers(int[], MyMath)*
سه متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست با آرایه خالی
- یک تست با آرایه تک‌عضوی

متدهای تست‌شده پوشش MC/DC دارند.
دو تست آخر در مقایسه با دور قبل جدید هستند.

#### واحد FileIO
***java
public class FileIOTestGemma3_P2 {

    private FileIO fileIO = new FileIO();

    @Test
    void readFile_emptyFile() throws IOException {
        Path tempFile = Files.createTempFile("empty");
        String[] content = fileIO.readFile(tempFile.toString());
        assertArrayEquals(new String[0], content);
    }

    @Test
    void readFile_singleLineFile() throws IOException {
        Path tempFile = Files.createTempFile("single");
        Files.write(tempFile, "single line".getBytes());
        String[] content = fileIO.readFile(tempFile.toString());
        assertArrayEquals(new String[]{"single line"}, content);
    }

    @Test
    void readFile_multipleLinesFile() throws IOException {
        Path tempFile = Files.createTempFile("multiple");
        Files.write(tempFile, "''\n                line1\n                line2\n                line3\n                ''".getBytes());
        String[] content = fileIO.readFile(tempFile.toString());
        assertArrayEquals(new String[]{"line1", "line2", "line3"}, content);
    }
}
***
سه متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک فایل خالی
- یک فایل تک‌خطی

پوشش statement نداریم چون به استثنا برخورد نمی‌کنیم.  
حالت لبه فایل ناموجود پوشش داده نشده است.  
تست آخر در واقع اشتباه است چون FileIO فاصله‌ها را نادیده نمی‌گیرد!  
با نادیده گرفتن تست آخر که assertion اشتباهی دارد، هیچ بهبودی نسبت به دور قبل وجود ندارد!

#### واحد NameSearch
***java
public class NameSearchTestGemma3_P2 {

    private NameSearch nameSearch = new NameSearch();
    private FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_emptyFile() {
        Path tempFile = Files.createTempFile("empty");
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        assertArrayEquals(new String[0], result);
    }

    @Test
    void findNamesInFileThatStartWith_noMatchingPrefix() {
        Path tempFile = Files.createTempFile("noMatch");
        Files.write(tempFile, "''\n                line1\n                line2\n                line3\n                ''".getBytes());
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        assertArrayEquals(new String[0], result);
    }

    @Test
    void findNamesInFileThatStartWith_singleMatchingPrefix() {
        Path tempFile = Files.createTempFile("singleMatch");
        Files.write(tempFile, "''\n                Aline1\n                line2\n                line3\n                ''".getBytes());
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        assertArrayEquals(new String[]{"Aline1"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_multipleMatchingPrefix() {
        Path tempFile = Files.createTempFile("multipleMatch");
        Files.write(tempFile, "''\n                Aline1\n                Aline2\n                line3\n                ''".getBytes());
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");
        assertArrayEquals(new String[]{"Aline1", "Aline2"}, result);
    }
}
***
چهار متد تولید شد که شامل موارد زیر است:
- یک تست با فایل خالی
- یک تست بدون نتیجه
- یک تست با نتیجه تکی
- یک تست با چند نتیجه

تست به MC/DC می‌رسد.  
حالت لبه پیشوند خالی پوشش داده نشده است.  
نمی‌فهمم چرا عامل در فایل‌های موقت فاصله‌ها می‌نویسد و در
assertion آن‌ها را فراموش می‌کند. حداقل یک تست با چند نتیجه و یک تست با فایل خالی در مقایسه با دور قبل جدید هستند.

#### نتیجه‌گیری
این اولین مدل محلی بود که تست کردم.
نام متدهای تست با test شروع نمی‌شوند.
قطعاً در تست مقادیر حد و مرز بهبودهایی وجود دارد اما کافی نیست.


### Gemma4:e4b
Gemma4 همچنان مرا آزار می‌دهد. دو بار تولید خروجی متوقف شد و مجبور شدم اعلان
"continue" را بفرستم. بار دوم که متوقف شد در ساخت فایل‌ها مشکل داشت،
به آن گفتم فقط فایل‌های تست را تولید کند، من فایل‌ها را دستی می‌سازم.
از این نقطه به بعد، برای هر مدل کوچک اعلان می‌دهم که فقط فایل تست را تولید کند و فایل را نسازد،
من آن‌ها را دستی می‌سازم.

#### واحد MyMath
***java
public class MyMathTestGemma4_P2 {

    private final MyMath myMath = new MyMath();

    // --- Tests for checkSign(int number) ---

    @Test
    void checkSign_PositiveNumber() {
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    void checkSign_NegativeNumber() {
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    void checkSign_BoundaryZeroMinusOne() {
        // Testing -1 (should be negative)
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    void checkSign_BoundaryZero() {
        // Testing 0 (should throw exception)
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(0);
        });
        assertEquals("Number had no sign", exception.getMessage());
    }

    @Test
    void checkSign_BoundaryOne() {
        // Testing 1 (should be positive)
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    void checkSign_LargePositiveNumber() {
        assertEquals("positive", myMath.checkSign(Integer.MAX_VALUE));
    }

    @Test
    void checkSign_LargeNegativeNumber() {
        assertEquals("negative", myMath.checkSign(Integer.MIN_VALUE));
    }

    // --- Tests for reverseNumber(int number) ---

    @Test
    void reverseNumber_PositiveNumber() {
        assertEquals(-123, myMath.reverseNumber(123));
    }

    @Test
    void reverseNumber_NegativeNumber() {
        assertEquals(456, myMath.reverseNumber(-456));
    }

    @Test
    void reverseNumber_Zero() {
        assertEquals(0, myMath.reverseNumber(0));
    }

    @Test
    void reverseNumber_One() {
        assertEquals(-1, myMath.reverseNumber(1));
    }

    @Test
    void reverseNumber_BoundaryMinInt() {
        // Reversing Integer.MIN_VALUE = -2147483648 results in 2147483648 which overflows int.
        // The current implementation will wrap around, testing for predictable wrapping behavior or the expected result if it were handled.
        // Since the method returns int, we test the predictable result of the unary negation on the extreme boundary case.
        assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
    }
}
***

##### متد *checkSign(int)*
پنج متد تولید شد که شامل موارد زیر است:
- کمی پایین‌تر از حد و مرز
- ۰ (حد و مرز)
- کمی بالاتر از حد و مرز
- Integer.MAX_VALUE
- Integer.MIN_VALUE


تست‌ها پوشش MC/DC را دارند.  
به نظر من دو تست آخر غیرضروری هستند.  
با این حال، بهبود یافته است.

##### متد *reverseNumber(int)*
پنج متد تولید شد که شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- ۰ (در مرز)
- کمی بالاتر از حد و مرز
- Integer.MIN_VALUE

تست‌ها پوشش MC/DC را دارند.  
تنها مدلی است که حالت لبه Integer.MIN_VALUE را تست کرد.  
قطعاً در مقایسه با دور قبل بهبود یافته است. اگرچه از تست مقادیر معمولی به جای مقادیر حد و مرز
خوشحال نیستم. این را در اعلان ذکر کرده بودم.

#### واحد MyAdvancedMath
***java
public class MyAdvancedMathTestGemma4_P2 {

    private MyMath myMath;
    private MyAdvancedMath myAdvancedMath;

    @BeforeEach
    void setUp() {
        myMath = new MyMath();
        myAdvancedMath = new MyAdvancedMath();
    }

    // --- Tests for add(int x, int y) ---

    @Test
    void add_TwoPositiveNumbers() {
        assertEquals(10, myAdvancedMath.add(3, 7));
    }

    @Test
    void add_BoundaryOneAndOne() {
        assertEquals(2, myAdvancedMath.add(1, 1));
    }

    @Test
    void add_BoundaryTwoAndZero() {
        // Although the method checks for positivity, we test the boundary relative to zero inputs if allowed,
        // but since the constraint is positive, we test small positive numbers near zero.
        assertEquals(2, myAdvancedMath.add(1, 1)); // Re-using 1,1 as small positive test
    }

    @Test
    void add_OneNumberNearMax() {
        int x = Integer.MAX_VALUE - 1;
        assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(x, 1));
    }

    @Test
    void add_NegativeInputX() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 5);
        });
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    void add_NegativeInputY() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(5, -1);
        });
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    void add_OverflowCondition() {
        // Test case that causes overflow: x + y > Integer.MAX_VALUE
        int x = Integer.MAX_VALUE / 2 + 1;
        int y = Integer.MAX_VALUE / 2 + 1;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(x, y);
        });
        assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
    }

    // --- Tests for powerOfTwo(int power) ---

    @Test
    void powerOfTwo_ZeroPower() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_PowerOfOne() {
        assertEquals(2, myAdvancedMath.powerOfTwo(1));
    }

    @Test
    void powerOfTwo_PowerOfTwo() {
        assertEquals(4, myAdvancedMath.powerOfTwo(2));
    }

    @Test
    void powerOfTwo_PowerOfThree() {
        assertEquals(8, myAdvancedMath.powerOfTwo(3));
    }

    @Test
    void powerOfTwo_BoundaryMaxPower() {
        // Testing the highest allowed power (30)
        assertEquals(1073741824, myAdvancedMath.powerOfTwo(30));
    }

    @Test
    void powerOfTwo_NegativePower() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    void powerOfTwo_AboveMaxPower() {
        // Testing power 31
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    // --- Tests for reverseArrayNumbers(int[] array, MyMath mm) ---

    @Test
    void reverseArrayNumbers_NormalCase() {
        int[] input = {10, 20, 30};
        int[] expected = {-10, -20, -30};

        myAdvancedMath.reverseArrayNumbers(input, myMath);

        assertArrayEquals(expected, input);
    }

    @Test
    void reverseArrayNumbers_WithNegativeNumbers() {
        int[] input = {-5, 10, -15};
        int[] expected = {5, -10, 15};

        myAdvancedMath.reverseArrayNumbers(input, myMath);

        assertArrayEquals(expected, input);
    }

    @Test
    void reverseArrayNumbers_EmptyArray() {
        int[] input = {};
        // Should return the array unchanged
        myAdvancedMath.reverseArrayNumbers(input, myMath);

        assertArrayEquals(new int[]{}, input);
    }
}
***
##### متد *add(int, int)*
این تاکنون متفاوت‌ترین تست متد *add* بود!  
هفت متد تولید شد که شامل موارد زیر است:
- x و y مثبت
- x و y هر دو ۱. دو بار!
- تست نزدیک به سرریز
- x برابر -۱ و y برابر ۵ و برعکس
- تست سرریز با جمع دو *Integer.MAX_VALUE / 2 + 1*

تست‌ها واقعاً پوشش MC/DC را دارند.  
در بین سه تست اول، دو تای آن‌ها تکراری است.  
تلاشی برای تست مقادیر حد و مرز صورت گرفته است.  
پیام استثنا assert شده که غیرضروری است.  
باز هم نسبت به دور قبل بهبود یافته است.

##### متد *powerOfTwo(int)*
شش متد تولید شد. تست‌ها شامل موارد زیر است:
- کمی پایین‌تر از حد پایین
- حد پایین
- کمی بالاتر از حد پایین
- دو تست معمولی
- حد بالا
- کمی بالاتر از حد بالا

ما MC/DC را داریم.  
مقادیر حد و مرز تست شده‌اند.  
تست‌های معمولی تکراری هستند.  
با این حال، این نسبت به دور قبل بهبود قابل توجهی بود.

##### متد *reverseArrayNumbers(int[], MyMath)*
سه متد تولید شد. شامل موارد زیر است:
- دو تست معمولی
- یک آرایه خالی

MC/DC محقق شده است.
یک تست معمولی تکراری است.  
تنها بهبود، تست آرایه خالی است.

#### واحد FileIO
***java
public class FileIOTestGemma4_P2 {

    private FileIO fileIO = new FileIO();

    @Test
    void readFile_ReadsFileWithMultipleLines() throws Exception {
        // Setup: Create a temporary file
        Path tempFile = tempDir.resolve("test_multi.txt");
        java.nio.file.Files.writeString(tempFile, "Line One\nLine Two\nLine Three");

        // Execution
        String[] result = fileIO.readFile(tempFile.toString());

        // Assertion: Expect 3 lines
        assertEquals(3, result.length);
        assertArrayEquals(new String[]{"Line One", "Line Two", "Line Three"}, result);
    }

    @Test
    void readFile_ReadsEmptyFile() throws Exception {
        // Setup: Create an empty temporary file
        Path tempFile = tempDir.resolve("test_empty.txt");
        java.nio.file.Files.createFile(tempFile);

        // Execution
        String[] result = fileIO.readFile(tempFile.toString());

        // Assertion: Expect an empty array
        assertArrayEquals(new String[0], result);
    }

    @Test
    void readFile_FileDoesNotExist() {
        // Setup: Use a path that definitely does not exist
        String nonExistentPath = tempDir.resolve("non_existent_file_xyz123.txt").toString();

        // Execution & Assertion: Expect an empty array because IOException is caught and prints stack trace
        String[] result = fileIO.readFile(nonExistentPath);

        // In case of non-existence, the catch block executes, resulting in an array of size 0 or unexpected content depending on runtime state.
        // For robust testing adhering to the method's contract (returning String[]), we assert the size based on the IOException handling.
        assertEquals(0, result.length, "Should return an empty array when file is not found.");
    }
}
***
سه متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست با فایل خالی
- یک فایل ناموجود

MC/DC محقق شده است.  
از فایل موقت JUnit استفاده نکرد.  
در مقایسه با دور قبل عملاً یکسان است.

#### واحد NameSearch
***java
public class NameSearchTestGemma4_P2 {

    private FileIO fileIO = new FileIO();
    private NameSearch nameSearch = new NameSearch();

    // --- Test Cases ---

    @Test
    void findNamesInFileThatStartWith_MultipleMatches() throws Exception {
        // Setup: Create a temporary file with multiple matches
        Path tempFile = tempDir.resolve("names_match.txt");
        String content = "Alice Smith\nAndrew Johnson\nBob Green\nAlicia Brown";
        java.nio.file.Files.writeString(tempFile, content);

        // Execution
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "Ali");

        // Assertion: Expect 3 matches
        assertArrayEquals(new String[]{"Alice Smith", "Andrew Johnson", "Alicia Brown"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_NoMatches() throws Exception {
        // Setup: Create a temporary file with no matching names
        Path tempFile = tempDir.resolve("names_no_match.txt");
        String content = "David Lee\nEva Green\nFrank White";
        java.nio.file.Files.writeString(tempFile, content);

        // Execution
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "Xyz");

        // Assertion: Expect an empty array
        assertArrayEquals(new String[0], result);
    }

    @Test
    void findNamesInFileThatStartWith_EmptyPrefix() throws Exception {
        // Setup: Create a temporary file
        Path tempFile = tempDir.resolve("names_empty_prefix.txt");
        String content = "Apple\nBanana\nCherry";
        java.nio.file.Files.writeString(tempFile, content);

        // Execution
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "");

        // Assertion: Empty prefix should match all names
        assertArrayEquals(new String[]{"Apple", "Banana", "Cherry"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_EmptyFile() throws Exception {
        // Setup: Create an empty temporary file
        Path tempFile = tempDir.resolve("names_empty_file.txt");
        java.nio.file.Files.createFile(tempFile);

        // Execution
        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.toString(), fileIO, "A");

        // Assertion: Should return an empty array
        assertArrayEquals(new String[0], result);
    }
}
***
چهار متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست بدون نتیجه
- یک تست با فایل خالی
- یک تست با پیشوند خالی

تست به MC/DC می‌رسد.  
تنها بهبود نسبت به مدل قبلی، تست پیشوند خالی است.


#### نتیجه‌گیری
تکرارهای زیادی وجود داشت، به‌خصوص در واحد *MyAdvancedMath*.
نام متدهای تست با test شروع نمی‌شوند!  
نام متدها با آنچه تست می‌کنند مطابقت دارد.  
پیام‌های استثنا assert شده بودند.

### GPT-OSS:20b
از این پس، از مدل‌های محلی می‌خواهم فقط فایل‌های تست را تولید کنند و فایل‌ها را نسازند.
این با وجود بزرگ‌تر بودن این مدل، خروجی را به‌شدت بهبود می‌دهد.
#### واحد MyMath
***java
class MyMathTestGPT_OSS_P2 {

    private final MyMath math = new MyMath();

    @Test
    void checkSign_negativeNumber() {
        assertEquals("negative", math.checkSign(-1));
    }

    @Test
    void checkSign_zeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> math.checkSign(0));
    }

    @Test
    void checkSign_positiveNumber() {
        assertEquals("positive", math.checkSign(1));
    }

    @Test
    void reverseNumber_negative() {
        assertEquals(1, math.reverseNumber(-1));
    }

    @Test
    void reverseNumber_zero() {
        assertEquals(0, math.reverseNumber(0));
    }

    @Test
    void reverseNumber_positive() {
        assertEquals(-1, math.reverseNumber(1));
    }
}
***

##### متد *checkSign(int)*
سه متد تولید شد که شامل موارد زیر است:
- کمی بالاتر از حد و مرز
- کمی پایین‌تر از حد و مرز
- ۰ (حد و مرز)

تست‌ها پوشش MC/DC را دارند.  
این دقیقاً همان چیزی بود که از این اعلان می‌خواستم.

##### متد *reverseNumber(int)*
سه متد تولید شد که شامل موارد زیر است:
- کمی بالاتر از حد و مرز
- کمی پایین‌تر از حد و مرز
- ۰ (در مرز)

تست‌ها پوشش MC/DC را دارند.  
حالت لبه Integer.MIN_VALUE وجود ندارد.  
نقص دور قبل را پوشش داد.

#### واحد MyAdvancedMath
***java
class MyAdvancedMathTestGPT_OSS_P2 {

    private final MyAdvancedMath adv = new MyAdvancedMath();

    @Test
    void add_validInputs() {
        assertEquals(5, adv.add(2, 3));
    }

    @Test
    void add_negativeInputThrows() {
        assertThrows(IllegalArgumentException.class, () -> adv.add(-1, 5));
    }

    @Test
    void add_overflowThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> adv.add(Integer.MAX_VALUE, 1));
    }

    @Test
    void powerOfTwo_zero() {
        assertEquals(1, adv.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_one() {
        assertEquals(2, adv.powerOfTwo(1));
    }

    @Test
    void powerOfTwo_maxBoundary() {
        assertEquals(1073741824, adv.powerOfTwo(30));
    }

    @Test
    void powerOfTwo_negativeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> adv.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_aboveMaxThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> adv.powerOfTwo(31));
    }

    @Test
    void reverseArrayNumbers_emptyArray() {
        assertArrayEquals(new int[0],
                adv.reverseArrayNumbers(new int[0], new MyMath()));
    }

    @Test
    void reverseArrayNumbers_mixedValues() {
        int[] arr = {1, -2, 0};
        assertArrayEquals(new int[]{-1, 2, 0},
                adv.reverseArrayNumbers(arr, new MyMath()));
    }
}
***
##### متد *add(int, int)*
سه متد تولید شد که شامل موارد زیر است:
- x و y مثبت
- y برابر -۱ و x مثبت
- تست سرریز

در واقع بدتر از دور قبل عمل کرد!  
MC/DC را از دست دادیم چون x هرگز منفی نیست.

##### متد *powerOfTwo(int)*
پنج متد تولید شد. تست‌ها شامل موارد زیر است:
- کمی پایین‌تر از حد پایین
- حد پایین
- کمی بالاتر از حد و مرز
- حد بالا
- کمی بالاتر از حد بالا

ما MC/DC را داریم.  
مقادیر حد و مرز به اندازه کافی تست شده‌اند.  
نسبت به دور قبل بهبود قابل توجهی است.

##### متد *reverseArrayNumbers(int[], MyMath)*
دو متد تولید شد که شامل موارد زیر است:
- تست آرایه خالی
- یک تست معمولی

MC/DC محقق شده است.  
تنها بهبود، تست آرایه خالی است.

#### واحد FileIO
***java
class FileIOTestGPT_OSS_P2 {

    private final FileIO fileIO = new FileIO();

    @Test
    void readFile_emptyFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);
        assertArrayEquals(new String[0], fileIO.readFile(file.toString()));
    }

    @Test
    void readFile_singleLine(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("single.txt");
        Files.writeString(file, "Hello");
        assertArrayEquals(new String[]{"Hello"}, fileIO.readFile(file.toString()));
    }

    @Test
    void readFile_multipleLines(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("multiple.txt");
        Files.write(file, ("Line1\nLine2\nLine3").getBytes());
        assertArrayEquals(new String[]{"Line1", "Line2", "Line3"},
                fileIO.readFile(file.toString()));
    }
}
***
سه متد تولید شد که شامل موارد زیر است:
- یک فایل خالی
- یک فایل با یک خط
- یک فایل با چند خط

مورد تست فایل ناموجود وجود ندارد، که قبلاً پوشش داده شده بود. این باعث
از دست دادن MC/DC شد. با این حال، تست فایل خالی و فایل تک‌خطی جدید هستند.

#### واحد NameSearch
***java
class NameSearchTestGPT_OSS_P2 {

    private final NameSearch search = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_noMatches(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.writeString(file, "Alice\nBob\nCharlie");
        assertArrayEquals(new String[0],
                search.findNamesInFileThatStartWith(file.toString(), fileIO, "Z"));
    }

    @Test
    void findNamesInFileThatStartWith_singleMatch(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.writeString(file, "Alice\nBob\nAlbert");
        assertArrayEquals(new String[]{"Alice", "Albert"},
                search.findNamesInFileThatStartWith(file.toString(), fileIO, "A"));
    }

    @Test
    void findNamesInFileThatStartWith_multipleMatches(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.writeString(file, "Ann\nAndy\nBob\nAaron");
        assertArrayEquals(new String[]{"Ann", "Andy", "Aaron"},
                search.findNamesInFileThatStartWith(file.toString(), fileIO, "A"));
    }

    @Test
    void findNamesInFileThatStartWith_emptyFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);
        assertArrayEquals(new String[0],
                search.findNamesInFileThatStartWith(file.toString(), fileIO, "A"));
    }
}
***
چهار متد با چند assertion تولید شد که شامل موارد زیر است:
- یک تست بدون نتیجه.
- یک تست با نتیجه تکی
- یک تست با چند نتیجه
- یک تست با فایل خالی.

تست به MC/DC می‌رسد.  
مورد تست پیشوند خالی وجود ندارد.  
نسبت به دفعه قبل بهبود یافته است.

#### نتیجه‌گیری
قطعاً می‌شد متدهای تست بیشتری تولید شود، به‌خصوص برای واحد *MyAdvancedMath*.  
نام متدها با آنچه تست می‌کنند مطابقت دارد، اما با test شروع نمی‌شوند.  
برخی موارد لبه تست نشده‌اند. با این حال، این یک بهبود است.
از متغیرهای خصوصی استفاده کرد.

### Qwen-Coder2.5:7b
Qwen دوباره عجیب رفتار کرد. همان اعلان را به آن دادم، اما فقط تست واحد
برای واحدهای ریاضی تولید کرد. مجبور شدم اعلان "شما برای FileIO و NameSearch تست تولید نکردید.
آن‌ها را تولید کنید." را بفرستم تا دو تست واحد دیگر تولید کند.
#### واحد MyMath
***java
public class MyMathTestQwen2_5_P2 {

    @Test
    public void testCheckSignPositive() {
        MyMath myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    public void testCheckSignNegative() {
        MyMath myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    public void testCheckSignZeroThrowsException() {
        MyMath myMath = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0), "Expected IllegalArgumentException for zero");
    }

    @Test
    public void testReverseNumberPositive() {
        MyMath myMath = new MyMath();
        assertEquals(-1, myMath.reverseNumber(1));
    }

    @Test
    public void testReverseNumberNegative() {
        MyMath myMath = new MyMath();
        assertEquals(1, myMath.reverseNumber(-1));
    }
}
***

##### متد *checkSign(int)*
سه متد تولید شد که شامل موارد زیر است:
- کمی پایین‌تر از حد و مرز
- کمی بالاتر از حد و مرز
- ۰ (حد و مرز)

تست‌ها پوشش MC/DC را دارند.  
قطعاً بهبود یافته است.

##### متد *reverseNumber(int)*
دو متد تولید شد که شامل موارد زیر است:
- کمی بالاتر از حد و مرز
- کمی پایین‌تر از حد و مرز

MC/DC برای متدی با یک statement محقق شده است.  
برخلاف سایر مدل‌ها، هیچ تست صفری وجود ندارد.  
بهبود جزئی با جایگزینی تست مقادیر حد و مرز به جای مقادیر معمولی.

#### واحد MyAdvancedMath
***java
public class MyAdvancedMathTestQwen2_5_P2 {

    @Test
    public void testAddPositiveNumbers() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(3, myAdvancedMath.add(1, 2));
    }

    @Test
    public void testAddNegativeNumberThrowsException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 2), "Expected IllegalArgumentException for negative number");
    }

    @Test
    public void testAddOverflowThrowsException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(Integer.MAX_VALUE, 1), "Expected IllegalArgumentException for overflow");
    }

    @Test
    public void testPowerOfTwoZero() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    public void testPowerOfTwoPositive() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(256, myAdvancedMath.powerOfTwo(8));
    }

    @Test
    public void testPowerOfTwoNegativeThrowsException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1), "Expected IllegalArgumentException for negative power");
    }

    @Test
    public void testPowerOfTwoTooLargeThrowsException() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31), "Expected IllegalArgumentException for too large power");
    }

    @Test
    public void testReverseArrayNumbers() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        int[] inputArray = {1, -2, 3, -4};
        int[] expectedOutputArray = {-1, 2, -3, 4};
        int[] resultArray = myAdvancedMath.reverseArrayNumbers(inputArray, myAdvancedMath);
        assertArrayEquals(expectedOutputArray, resultArray);
    }
}
***
باز هم این مدل بدترین عملکرد را در مقایسه با سایر مدل‌ها داشت.

##### متد *add(int, int)*
سه متد تولید شد که شامل موارد زیر است:
- x و y مثبت
- x منفی و y مثبت
- تست سرریز

تست‌ها پوشش decision را دارند.  
می‌شد برای این متد تست‌های بیشتری نوشته می‌شد.  
در مقایسه با دور قبل خیلی تغییر نکرده است.

##### متد *powerOfTwo(int)*
چهار متد تولید شد. تست‌ها شامل موارد زیر است:
- کمی پایین‌تر از حد و مرز
- حد پایین
- یک تست معمولی
- کمی بالاتر از حد بالا

ما MC/DC را داریم.  
می‌شد مقادیر حد و مرز بیشتری تست شود.
تنها بهبود، تست -۱ به جای یک عدد منفی معمولی است.

##### متد *reverseArrayNumbers(int[], MyMath)*
فقط یک متد تولید شده که شامل یک تست معمولی است.
خب، این یک بهبود است. دفعه قبل، عامل اصلاً این متد را تست نکرد.

#### واحد FileIO
***java
public class FileIOTestQwen2_5_P2 {

    @Test
    public void testReadFileWithValidFilePath() throws IOException {
        // Arrange
        Path tempDir = Files.createTempDirectory("test");
        try {
            Path filePath = tempDir.resolve("test.txt");
            List<String> lines = Arrays.asList("line1", "line2", "line3");
            Files.write(filePath, lines);

            FileIO fileIO = new FileIO();

            // Act
            String[] result = fileIO.readFile(filePath.toString());

            // Assert
            assertArrayEquals(lines.toArray(new String[0]), result);
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    public void testReadFileWithEmptyFilePathThrowsIOException() throws IOException {
        // Arrange
        FileIO fileIO = new FileIO();

        // Act & Assert
        assertThrows(IOException.class, () -> fileIO.readFile(""), "Expected IOException for empty file path");
    }

    @Test
    public void testReadFileWithNonexistentFilePathThrowsIOException() throws IOException {
        // Arrange
        FileIO fileIO = new FileIO();
        Path nonExistentPath = Files.createTempDirectory("nonexistent").resolve("test.txt");

        // Act & Assert
        assertThrows(IOException.class, () -> fileIO.readFile(nonExistentPath.toString()), "Expected IOException for nonexistent file path");
    }
}
***
سه متد تولید شد که شامل موارد زیر است:
- تست معمولی
- مسیر فایل خالی
- مسیر فایل ناموجود

جالب اینکه یک مسیر فایل خالی تولید کرد. تاکنون هیچ مدلی این کار را نکرده بود.  
پوشش MC/DC محقق شده است.  
مورد تست فایل خالی وجود ندارد.  
قبلاً این متد تست نشده بود.

#### واحد NameSearch
***java
public class NameSearchTestQwen2_5_P2 {

    @Test
    public void testFindNamesInFileThatStartWithValidPrefix() throws IOException {
        // Arrange
        Path tempDir = Files.createTempDirectory("test");
        try {
            Path filePath = tempDir.resolve("names.txt");
            List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana", "Eve");
            Files.write(filePath, names);

            FileIO fileIO = new FileIO();
            NameSearch nameSearch = new NameSearch();

            // Act
            String[] result = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "A");

            // Assert
            assertArrayEquals(new String[]{"Alice"}, result);
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    public void testFindNamesInFileThatStartWithMultiplePrefixes() throws IOException {
        // Arrange
        Path tempDir = Files.createTempDirectory("test");
        try {
            Path filePath = tempDir.resolve("names.txt");
            List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana", "Eve");
            Files.write(filePath, names);

            FileIO fileIO = new FileIO();
            NameSearch nameSearch = new NameSearch();

            // Act
            String[] resultA = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "A");
            String[] resultB = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "B");

            // Assert
            assertArrayEquals(new String[]{"Alice"}, resultA);
            assertArrayEquals(new String[]{"Bob"}, resultB);
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    public void testFindNamesInFileThatStartWithNoPrefix() throws IOException {
        // Arrange
        Path tempDir = Files.createTempDirectory("test");
        try {
            Path filePath = tempDir.resolve("names.txt");
            List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana", "Eve");
            Files.write(filePath, names);

            FileIO fileIO = new FileIO();
            NameSearch nameSearch = new NameSearch();

            // Act
            String[] result = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "");

            // Assert
            assertArrayEquals(names.toArray(new String[0]), result);
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    public void testFindNamesInFileThatStartWithNonexistentPrefix() throws IOException {
        // Arrange
        Path tempDir = Files.createTempDirectory("test");
        try {
            Path filePath = tempDir.resolve("names.txt");
            List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana", "Eve");
            Files.write(filePath, names);

            FileIO fileIO = new FileIO();
            NameSearch nameSearch = new NameSearch();

            // Act
            String[] result = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "Z");

            // Assert
            assertTrue(result.length == 0);
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    public void testFindNamesInFileThatStartWithEmptyFilePathThrowsIOException() throws IOException {
        // Arrange
        FileIO fileIO = new FileIO();
        NameSearch nameSearch = new NameSearch();

        // Act & Assert
        assertThrows(IOException.class, () -> nameSearch.findNamesInFileThatStartWith("", fileIO, "A"), "Expected IOException for empty file path");
    }

    @Test
    public void testFindNamesInFileThatStartWithNonexistentFilePathThrowsIOException() throws IOException {
        // Arrange
        FileIO fileIO = new FileIO();
        NameSearch nameSearch = new NameSearch();
        Path nonExistentPath = Files.createTempDirectory("nonexistent").resolve("names.txt");

        // Act & Assert
        assertThrows(IOException.class, () -> nameSearch.findNamesInFileThatStartWith(nonExistentPath.toString(), fileIO, "A"), "Expected IOException for nonexistent file path");
    }
}
***
شش متد تولید شد. شامل موارد زیر است:
- یک تست با نتیجه تکی
- یک تست با چند نتیجه
- یک تست با پیشوند خالی
- یک تست بدون نتیجه
- یک تست با فایل خالی
- یک تست با فایل ناموجود

تست پیشوند خالی در واقع اشتباه است! همه نام‌ها را شامل می‌شود، نه نتیجه‌ای
خالی.  
تست فایل خالی و تست فایل ناموجود قبلاً در FileIO انجام شده‌اند و کاملاً
تکراری هستند.
بهتر از دور قبل است.

#### نتیجه‌گیری
این مدل دوباره بد عمل کرد. تست‌های خوب کمتری نسبت به سایر مدل‌ها تولید کرد.
نام متدها با آنچه تست می‌کنند مطابقت دارد و همچنین با test شروع می‌شوند.
برخی موارد لبه مانند فایل‌های خالی تست نشده‌اند.
بهبودهایی وجود دارد، شاید فقط به این دلیل که دفعه قبل این مدل کار افتضاحی
انجام داد.

### Qwen-Coder3:30b
قهرمان دور قبل. بیایید ببینیم این مدل چه می‌کند.  
گرفتن نتیجه کمی طول کشید، که احتمالاً به خاطر 30b بودن مدل و ناتوانی رایانه‌ام
در پردازش درست این مدل بود.

#### واحد MyMath
***java
public class MyMathTestQwen3_P2 {

    private final MyMath myMath = new MyMath();

    @Test
    @DisplayName("Test checkSign with positive number")
    public void testCheckSignWithPositiveNumber() {
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    @DisplayName("Test checkSign with negative number")
    public void testCheckSignWithNegativeNumber() {
        assertEquals("negative", myMath.checkSign(-1));
    }

    @Test
    @DisplayName("Test checkSign with zero - should throw IllegalArgumentException")
    public void testCheckSignWithZero() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    @DisplayName("Test reverseNumber with positive number")
    public void testReverseNumberWithPositiveNumber() {
        assertEquals(-5, myMath.reverseNumber(5));
    }

    @Test
    @DisplayName("Test reverseNumber with negative number")
    public void testReverseNumberWithNegativeNumber() {
        assertEquals(5, myMath.reverseNumber(-5));
    }

    @Test
    @DisplayName("Test reverseNumber with zero")
    public void testReverseNumberWithZero() {
        assertEquals(0, myMath.reverseNumber(0));
    }
}
***

##### متد *checkSign(int)*
سه متد تولید شد که شامل موارد زیر است:
- کمی بالاتر از حد و مرز
- کمی پایین‌تر از حد و مرز
- ۰ (حد و مرز)

تست‌ها پوشش MC/DC را دارند.
به جای مقادیر معمولی، مقادیر حد و مرز را تست می‌کند.

##### متد *reverseNumber(int)*
سه متد تولید شد که شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- ۰ (در مرز)

تست‌ها پوشش MC/DC را دارند.  
من ۱ و -۱ را به جای ۵ و -۵ تست می‌کردم.  
در مقایسه با دور قبل هیچ بهبودی نیست.

#### واحد MyAdvancedMath
***java
public class MyAdvancedMathTestQwen3_P2 {

    private final MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
    private final MyMath myMath = new MyMath();

    @Test
    @DisplayName("Test add with normal positive numbers")
    public void testAddWithNormalNumbers() {
        assertEquals(10, myAdvancedMath.add(3, 7));
    }

    @Test
    @DisplayName("Test add with negative first number - should throw IllegalArgumentException")
    public void testAddWithNegativeFirstNumber() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 5));
    }

    @Test
    @DisplayName("Test add with negative second number - should throw IllegalArgumentException")
    public void testAddWithNegativeSecondNumber() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(5, -1));
    }

    @Test
    @DisplayName("Test add with both negative numbers - should throw IllegalArgumentException")
    public void testAddWithBothNegativeNumbers() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-2, -3));
    }

    @Test
    @DisplayName("Test add with overflow - should throw IllegalArgumentException")
    public void testAddWithOverflow() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(Integer.MAX_VALUE, 1));
    }

    @Test
    @DisplayName("Test powerOfTwo with zero power")
    public void testPowerOfTwoWithZeroPower() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    @DisplayName("Test powerOfTwo with power 1")
    public void testPowerOfTwoWithPower1() {
        assertEquals(2, myAdvancedMath.powerOfTwo(1));
    }

    @Test
    @DisplayName("Test powerOfTwo with maximum allowed power")
    public void testPowerOfTwoWithMaximumPower() {
        assertEquals(1073741824, myAdvancedMath.powerOfTwo(30));
    }

    @Test
    @DisplayName("Test powerOfTwo with negative power - should throw IllegalArgumentException")
    public void testPowerOfTwoWithNegativePower() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1));
    }

    @Test
    @DisplayName("Test powerOfTwo with power greater than 30 - should throw IllegalArgumentException")
    public void testPowerOfTwoWithPowerGreaterThan30() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
    }

    @Test
    @DisplayName("Test reverseArrayNumbers with normal array")
    public void testReverseArrayNumbersWithNormalArray() {
        int[] input = {1, -2, 3, -4};
        int[] expected = {-1, 2, -3, 4};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }

    @Test
    @DisplayName("Test reverseArrayNumbers with empty array")
    public void testReverseArrayNumbersWithEmptyArray() {
        int[] input = {};
        int[] expected = {};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }

    @Test
    @DisplayName("Test reverseArrayNumbers with single element")
    public void testReverseArrayNumbersWithSingleElement() {
        int[] input = {5};
        int[] expected = {-5};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }

    @Test
    @DisplayName("Test reverseArrayNumbers with zero element")
    public void testReverseArrayNumbersWithZeroElement() {
        int[] input = {0};
        int[] expected = {0};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, myMath));
    }
}
***

##### متد *add(int, int)*
پنج متد تولید شد که شامل موارد زیر است:
- x و y مثبت
- x مثبت و y منفی و برعکس
- x و y منفی
- تست سرریز

MD/DC محقق شده است.  
نسبت به دور قبل بهبود یافته است.

##### متد *powerOfTwo(int)*
پنج متد تولید شد. تست‌ها شامل موارد زیر است:
- کمی پایین‌تر از حد پایین
- حد پایین
- کمی بالاتر از حد پایین
- حد بالا
- کمی بالاتر از حد بالا

ما MC/DC را داریم.  
مقادیر حد و مرز تست شده‌اند.  
با اضافه شدن تست‌های کمی بالاتر از حد پایین و حد بالا بهبود یافته است.

##### متد *reverseArrayNumbers(int[], MyMath)*
چهار متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست با آرایه خالی
- یک تست با آرایه تک‌عضوی
- یک آرایه با تنها یک ۰

می‌گویم تست آخر تکراری است. تست معکوس کردن ۰ از قبل در
MyMath انجام شده است.
MC/DC محقق شده است.  
آرایه خالی و آرایه تک‌عضوی نسبت به دور قبل بهبود هستند.

#### واحد FileIO
***java
public class FileIOTestQwen3_P2 {

    private final FileIO fileIO = new FileIO();

    @Test
    @DisplayName("Test readFile with normal file")
    public void testReadFileWithNormalFile(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("test.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nBob\nCharlie");
        writer.close();

        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        String[] expected = {"Alice", "Bob", "Charlie"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test readFile with empty file")
    public void testReadFileWithEmptyFile(@TempDir Path tempDir) throws IOException {
        // Create a temporary empty file
        File tempFile = tempDir.resolve("empty.txt").toFile();
        tempFile.createNewFile();

        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        String[] expected = {};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test readFile with file with empty lines")
    public void testReadFileWithEmptyLines(@TempDir Path tempDir) throws IOException {
        // Create a temporary file with empty lines
        File tempFile = tempDir.resolve("withEmptyLines.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\n\nBob\n\nCharlie");
        writer.close();

        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        String[] expected = {"Alice", "", "Bob", "", "Charlie"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test readFile with single line file")
    public void testReadFileWithSingleLineFile(@TempDir Path tempDir) throws IOException {
        // Create a temporary file with single line
        File tempFile = tempDir.resolve("singleLine.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("SingleLine");
        writer.close();

        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        String[] expected = {"SingleLine"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test readFile with file that doesn't exist")
    public void testReadFileWithNonExistentFile(@TempDir Path tempDir) {
        // Try to read a file that doesn't exist
        String[] result = fileIO.readFile(tempDir.resolve("nonexistent.txt").toString());
        String[] expected = {};
        assertArrayEquals(expected, result);
    }
}
***
پنج متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست با فایل خالی
- یک فایل با خطوط خالی بین محتوا
- یک فایل تک‌خطی
- یک فایل ناموجود

پوشه موقت با annotation ‎@TempDir تست شده است.
قطعاً نسبت به دور قبل بهبود است.
MC/DC محقق شده است.

#### واحد NameSearch
***java
public class NameSearchTestQwen3_P2 {

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with matching names")
    public void testFindNamesInFileThatStartWithMatchingNames(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nBob\nCharlie\nAmanda\nBella");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        String[] expected = {"Alice", "Amanda"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with no matching names")
    public void testFindNamesInFileThatStartWithNoMatchingNames(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nBob\nCharlie");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "Z");
        String[] expected = {};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with empty prefix")
    public void testFindNamesInFileThatStartWithEmptyPrefix(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice\nBob\nCharlie");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "");
        String[] expected = {"Alice", "Bob", "Charlie"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with empty file")
    public void testFindNamesInFileThatStartWithEmptyFile(@TempDir Path tempDir) throws IOException {
        // Create a temporary empty file
        File tempFile = tempDir.resolve("empty.txt").toFile();
        tempFile.createNewFile();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        String[] expected = {};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with single name matching")
    public void testFindNamesInFileThatStartWithSingleNameMatching(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Alice");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        String[] expected = {"Alice"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Test findNamesInFileThatStartWith with case sensitive matching")
    public void testFindNamesInFileThatStartWithCaseSensitive(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File tempFile = tempDir.resolve("names.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("alice\nAlice\nALICE");
        writer.close();

        String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "A");
        String[] expected = {"Alice"};
        assertArrayEquals(expected, result);
    }
}
***
شش متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست بدون نتیجه
- یک تست با پیشوند خالی
- یک تست با فایل خالی
- یک تست با نتیجه تکی
- یک تست حساس به بزرگی و کوچکی حروف

تست به MC/DC می‌رسد.  
تست آخر تکراری است چون رفتاری از کتابخانه جاوا را تست می‌کند که
قبلاً تست شده و مورد اعتماد است.
با تست نتیجه تکی و تست پیشوند خالی نسبت به دور قبل بهبود یافته است.

#### نتیجه‌گیری
مدل خوب عمل کرد.
نام متدها با آنچه تست می‌کنند مطابقت دارد و با test شروع می‌شوند.
از متغیرهای خصوصی استفاده شده است.
در مجموع، این مدل بهتر از دور قبل عمل کرد.

### مقایسه مدل‌های محلی
همه مدل‌ها با متد *checkSign یکسان عمل کردند.  
برای *reverseNumber*، فقط Gemma4 مورد تست Integer.MIN_VALUE را تست کرد. Qwen 2.5 تنها مدلی بود که
۰ را برای این متد تست نکرد.  
برای متد *add*، GPT-OSS، Gemma3 و Qwen 2.5 یکسان عمل کردند، در حالی که Gemma4 و Qwen 3 تست‌های
بیشتری داشتند. Qwen 3 حتی بهتر عمل کرد.  
برای متد *powerOfTwo*، همه مدل‌ها یکسان عمل کردند اما فقط Qwen 2.5 سناریوهای کمتری داشت.  
در متد *reverseArray*، Qwen 2.5 بدترین عمل کرد، GPT-OSS و Gemma4 در میانه بودند و Gemma3 و
Qwen 3 بهترین بودند.  
برای متد *readFile*، Qwen 2.5 و Gemma3 آخر هستند، بعد GPT-OSS و Gemma4. البته
Qwen 3 بهترین بود.  
آخر اما نه کم‌اهمیت‌ترین، برای واحد *NameSearch* می‌توان گفت همه مدل‌ها به جز Qwen 3 یکسان بودند و Qwen 3
بهترین بود.  
و به طور کلی Qwen 3 بهتر عمل کرد. تعجب می‌کنم که GPT-OSS، Gemma4 و Qwen 2.5 از @TempDir استفاده نکردند، در حالی که
آن را در اعلان مطرح کرده بودم.

این رتبه‌بندی من است:
1. Qwen 3 Coder 30b
2. Gemma4 e4b
3. GPT OSS 20b
4. Gemma3 12b
5. Qwen 2.5 Coder 7b

در واقع با دور قبل یکسان است!

## دور ۳
این دور آخر است. بهترین نتیجه را از مدل‌هایمان می‌خواهم. پس بیایید از تست‌های پارامتری
و تست مقادیر حد و مرز محکم‌تر استفاده کنیم. محقق شدن MC/DC را هم اضافه می‌کنم، که این زیاده‌روی است
اما همه متدها بزرگ نیستند و رسیدن به MC/DC آسان است. اعلان این دور:
> برای این واحدها، تست‌های واحد را با استفاده از JUnit5 تولید کنید. از mock برای وابستگی‌ها استفاده نکنید.
> مطمئن شوید که تست مقادیر حد و مرز محکمی دارید (خود حد و مرز، کمی بالاتر
> از آن و کمی پایین‌تر از آن را تست کنید). سعی کنید در هر متد فقط از یک assertion استفاده کنید. از @TempDir مربوط به JUnit
> برای ذخیره‌سازی موقت استفاده کنید. مطمئن شوید که موارد لبه زیادی مانند آرایه‌ها یا رشته‌های خالی را تست می‌کنید.
> پوشش MC/DC را محقق کنید. برای مقادیر معمولی از تست‌های پارامتری استفاده کنید. از تولید تست‌های تکراری
> پرهیز کنید. کلاس‌های تست تولید شده باید با "Test(modelName)_P3" به پایان برسند.

بیایید Mistral و Gemini را امتحان کنیم و آن‌ها را مقایسه کنیم.

### Mistral Devstral Medium
این دور واقعاً به یک مشکل برخوردم. تست MyMath را تولید کرد، اما گیر کرد،
و برای MyAdvancedMath پاسخگو نبود، احتمالاً به خاطر ضعف اتصال شبکه.
تولیدش را متوقف کردم و "continue from MyAdvancedMath unit" را فرستادم.
این بار کار را تمام کرد.
عامل فایل‌های تست را تولید کرد، اما آنها را در دایرکتوری درست قرار نداد.

#### واحد MyMath
***java
class MyMathTestMistral_P3 {

 @Test
 void checkSign_WhenZero_ThrowsIllegalArgumentException() {
  MyMath myMath = new MyMath();
  assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
 }

 @ParameterizedTest
 @ValueSource(ints = {1, 10, 100, Integer.MAX_VALUE})
 void checkSign_WhenPositive_ReturnsPositive(int number) {
  MyMath myMath = new MyMath();
  assertEquals("positive", myMath.checkSign(number));
 }

 @ParameterizedTest
 @ValueSource(ints = {-1, -10, -100, Integer.MIN_VALUE})
 void checkSign_WhenNegative_ReturnsNegative(int number) {
  MyMath myMath = new MyMath();
  assertEquals("negative", myMath.checkSign(number));
 }

 @ParameterizedTest
 @ValueSource(ints = {0, 1, -1, 100, -100, Integer.MAX_VALUE, Integer.MIN_VALUE})
 void reverseNumber_ReversesSign(int number) {
  MyMath myMath = new MyMath();
  assertEquals(-number, myMath.reverseNumber(number));
 }
}
***
##### متد *checkSign(int)*
سه متد تولید شد. شامل موارد زیر است:
- ۰ (حد و مرز)
- یک تست پارامتری با مقادیر مثبت
- یک تست پارامتری با مقادیر منفی

تست‌ها پوشش MC/DC را دارند.
هیچ نقصی اینجا نمی‌بینم. فقط شاید مقادیر MAX و MIN غیرضروری باشند.

##### متد *reverseNumber(int)*
فقط یک متد تولید شده که شامل موارد زیر است:
- مجموعه‌ای از مقادیر، ۰، منفی‌ها و مثبت‌ها

تست‌ها پوشش MC/DC را دارند.  
ترجیح می‌دهم مثل تست‌های متد قبلی، مقادیر بر اساس علامتشان تفکیک شوند.
در غیر این صورت، این هم خوب است. شاید تست Integer.MAX_VALUE غیرضروری باشد.

#### واحد MyAdvancedMath
***java
class MyAdvancedMathTestMistral_P3 {

 @Test
 void add_WhenXIsNegative_ThrowsIllegalArgumentException() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 1));
 }

 @Test
 void add_WhenYIsNegative_ThrowsIllegalArgumentException() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(1, -1));
 }

 @Test
 void add_WhenBothAreNegative_ThrowsIllegalArgumentException() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, -1));
 }

 @Test
 void add_WhenOverflow_ThrowsIllegalArgumentException() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(Integer.MAX_VALUE, 1));
 }

 @ParameterizedTest
 @CsvSource({"0, 0, 0", "1, 1, 2", "10, 20, 30", "100, 200, 300"})
 void add_WhenValidInputs_ReturnsSum(int x, int y, int expected) {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertEquals(expected, myAdvancedMath.add(x, y));
 }

 @Test
 void powerOfTwo_WhenPowerIsNegative_ThrowsIllegalArgumentException() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1));
 }

 @Test
 void powerOfTwo_WhenPowerIsGreaterThan30_ThrowsIllegalArgumentException() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
 }

 @ParameterizedTest
 @CsvSource({"0, 1", "1, 2", "2, 4", "3, 8", "10, 1024", "30, 1073741824"})
 void powerOfTwo_WhenValidPower_ReturnsCorrectResult(int power, int expected) {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  assertEquals(expected, myAdvancedMath.powerOfTwo(power));
 }

 @Test
 void reverseArrayNumbers_WhenEmptyArray_ReturnsEmptyArray() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  MyMath myMath = new MyMath();
  int[] array = {};
  assertArrayEquals(new int[]{}, myAdvancedMath.reverseArrayNumbers(array, myMath));
 }

 @Test
 void reverseArrayNumbers_WhenSingleElement_ReturnsReversedArray() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  MyMath myMath = new MyMath();
  int[] array = {5};
  assertArrayEquals(new int[]{-5}, myAdvancedMath.reverseArrayNumbers(array, myMath));
 }

 @Test
 void reverseArrayNumbers_WhenMultipleElements_ReturnsReversedArray() {
  MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
  MyMath myMath = new MyMath();
  int[] array = {1, -2, 3, -4};
  assertArrayEquals(new int[]{-1, 2, -3, 4}, myAdvancedMath.reverseArrayNumbers(array, myMath));
 }
}
***
##### متد *add(int, int)*
پنج متد تولید شد که شامل موارد زیر است:
- x منفی و y مثبت و برعکس
- هر دو منفی
- تست سرریز
- یک تست پارامتری معتبر
  - هر دو صفر
  - هر دو مثبت

MC/DC محقق شده است.
می‌شد با ۰ تست‌های بیشتری نوشته می‌شد.  
احتمالاً باید تست‌های بسیار بیشتری اضافه می‌شد.  
عامل این بار واقعاً روی مقادیر حد و مرز تأکید کرد.  
در مجموع، کامل نیست.

##### متد *powerOfTwo(int)*
سه متد تولید شد. شامل موارد زیر است:
- کمی پایین‌تر از حد پایین
- کمی بالاتر از حد بالا
- تست پارامتری با مقادیر معتبر
  - حد پایین
  - کمی بالاتر از حد پایین
  - مقادیر معمولی
  - حد بالا

ما MC/DC را داریم.  
قطعاً نسبت به دور قبل بهبود یافته است.  
یک تست پارامتری نامعتبر هم می‌توانست خوب باشد، اما تولید نشد
چون گفته بودم تست پارامتری برای مقادیر معمولی تولید کنند.


##### متد *reverseArrayNumbers(int[], MyMath)*
سه متد تولید شد. شامل موارد زیر است:
- یک تست معمولی
- یک تست با آرایه تک‌عضوی
- یک تست با آرایه خالی

حدس می‌زنم از این متد تا آخر، احتمالاً بهبود چشمگیری
رخ ندهد. تست آرایه تک‌عضوی نسبت به دور قبل جدید است.  
MC/DC محقق شده است.

#### واحد FileIO
***java
class FileIOTestMistral_P3 {

 @TempDir
 Path tempDir;

 @Test
 void readFile_WhenFileDoesNotExist_ReturnsEmptyArray() {
  FileIO fileIO = new FileIO();
  assertArrayEquals(new String[]{}, fileIO.readFile("nonexistent.txt"));
 }

 @Test
 void readFile_WhenFileIsEmpty_ReturnsEmptyArray() throws IOException {
  FileIO fileIO = new FileIO();
  File emptyFile = tempDir.resolve("empty.txt").toFile();
  emptyFile.createNewFile();
  assertArrayEquals(new String[]{}, fileIO.readFile(emptyFile.getAbsolutePath()));
 }

 @Test
 void readFile_WhenFileHasSingleLine_ReturnsArrayWithSingleElement() throws IOException {
  FileIO fileIO = new FileIO();
  File singleLineFile = tempDir.resolve("singleLine.txt").toFile();
  Files.write(singleLineFile.toPath(), "Hello, World!".getBytes());
  assertArrayEquals(new String[]{"Hello, World!"}, fileIO.readFile(singleLineFile.getAbsolutePath()));
 }

 @Test
 void readFile_WhenFileHasMultipleLines_ReturnsArrayWithMultipleElements() throws IOException {
  FileIO fileIO = new FileIO();
  File multiLineFile = tempDir.resolve("multiLine.txt").toFile();
  Files.write(multiLineFile.toPath(), ("Line 1\nLine 2\nLine 3").getBytes());
  assertArrayEquals(new String[]{"Line 1", "Line 2", "Line 3"}, fileIO.readFile(multiLineFile.getAbsolutePath()));
 }
}

***
چهار متد تولید شد که شامل موارد زیر است:
- یک تست تک‌خطی
- یک تست چندخطی
- یک تست با فایل خالی
- یک فایل ناموجود

از ذخیره‌سازی موقت JUnit استفاده شده است.  
تست فایل تک‌خطی نسبت به دور قبل جدید است و MC/DC محقق شده است.

#### واحد NameSearch
***java
class NameSearchTestMistral_P3 {

  @TempDir
  Path tempDir;

  @Test
  void findNamesInFileThatStartWith_WhenFileDoesNotExist_ReturnsEmptyArray() {
    NameSearch nameSearch = new NameSearch();
    FileIO fileIO = new FileIO();
    assertArrayEquals(new String[]{}, nameSearch.findNamesInFileThatStartWith("nonexistent.txt", fileIO, "A"));
  }

  @Test
  void findNamesInFileThatStartWith_WhenFileIsEmpty_ReturnsEmptyArray() throws IOException {
    NameSearch nameSearch = new NameSearch();
    FileIO fileIO = new FileIO();
    File emptyFile = tempDir.resolve("empty.txt").toFile();
    emptyFile.createNewFile();
    assertArrayEquals(new String[]{}, nameSearch.findNamesInFileThatStartWith(emptyFile.getAbsolutePath(), fileIO, "A"));
  }

  @Test
  void findNamesInFileThatStartWith_WhenNoMatches_ReturnsEmptyArray() throws IOException {
    NameSearch nameSearch = new NameSearch();
    FileIO fileIO = new FileIO();
    File file = tempDir.resolve("names.txt").toFile();
    Files.write(file.toPath(), ("John\nJane\nDoe").getBytes());
    assertArrayEquals(new String[]{}, nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "A"));
  }

  @Test
  void findNamesInFileThatStartWith_WhenSingleMatch_ReturnsArrayWithSingleElement() throws IOException {
    NameSearch nameSearch = new NameSearch();
    FileIO fileIO = new FileIO();
    File file = tempDir.resolve("names.txt").toFile();
    Files.write(file.toPath(), ("Alice\nJohn\nJane").getBytes());
    assertArrayEquals(new String[]{"Alice"}, nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "A"));
  }

  @Test
  void findNamesInFileThatStartWith_WhenMultipleMatches_ReturnsArrayWithMultipleElements() throws IOException {
    NameSearch nameSearch = new NameSearch();
    FileIO fileIO = new FileIO();
    File file = tempDir.resolve("names.txt").toFile();
    Files.write(file.toPath(), ("Alice\nAndrew\nJohn\nAnna").getBytes());
    assertArrayEquals(new String[]{"Alice", "Andrew", "Anna"}, nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, "A"));
  }

  @Test
  void findNamesInFileThatStartWith_WhenPrefixIsEmpty_ReturnsAllNames() throws IOException {
    NameSearch nameSearch = new NameSearch();
    FileIO fileIO = new FileIO();
    File file = tempDir.resolve("names.txt").toFile();
    Files.write(file.toPath(), ("Alice\nAndrew\nJohn").getBytes());
    assertArrayEquals(new String[]{"Alice", "Andrew", "John"}, nameSearch.findNamesInFileThatStartWith(file.getAbsolutePath(), fileIO, ""));
  }
}
***
شش متد تولید شد که شامل موارد زیر است:
- یک تست با چند نتیجه
- یک تست با نتیجه تکی
- یک تست بدون نتیجه
- یک تست با فایل خالی
- یک تست با پیشوند خالی
- یک تست با فایل ناموجود

تست به MC/DC می‌رسد.  
تست نتیجه تکی و تست فایل ناموجود جدید هستند. تست فایل ناموجود
غیرضروری است چون قبلاً برای FileIO تست شده بود.

#### نتیجه‌گیری
با چند متد بیشتر، این یک تست واحد عالی می‌شد.  
نام متدهای تست با test شروع نمی‌شوند. این نقص در دو دور آخر تکرار شد. شاید  
مدل با پیچیده‌تر شدن اعلان این قرارداد را فراموش کرده باشد.  
آنچه مرا آزار داد فقط این بود که حین تولید گیر کرد.

### Gemini 3 Flash
به هیچ مشکلی برخوردم.  عامل فایل‌های تست را تولید کرد و آنها را در
دایرکتوری درست قرار داد.
#### واحد MyMath
***java
class MyMathTestGemini_P3 {

  private final MyMath mm = new MyMath();

  @ParameterizedTest
  @CsvSource({
          "1, positive",
          "2, positive",
          "100, positive",
          "-1, negative",
          "-2, negative",
          "-100, negative"
  })
  void testCheckSign_NormalValues(int number, String expected) {
    Assertions.assertEquals(expected, mm.checkSign(number));
  }

  @Test
  void testCheckSign_Zero_Boundary() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
  }

  @Test
  void testCheckSign_AboveZero_Boundary() {
    Assertions.assertEquals("positive", mm.checkSign(1));
  }

  @Test
  void testCheckSign_BelowZero_Boundary() {
    Assertions.assertEquals("negative", mm.checkSign(-1));
  }

  @ParameterizedTest
  @CsvSource({
          "1, -1",
          "-1, 1",
          "0, 0",
          "2147483647, -2147483647",
          "-2147483647, 2147483647"
  })
  void testReverseNumber_NormalAndEdges(int number, int expected) {
    Assertions.assertEquals(expected, mm.reverseNumber(number));
  }

  @Test
  void testReverseNumber_MinInt_Edge() {
    // Integer.MIN_VALUE is -2147483648. -(-2147483648) is still -2147483648 due to overflow.
    Assertions.assertEquals(Integer.MIN_VALUE, mm.reverseNumber(Integer.MIN_VALUE));
  }
}
***
به نظر می‌رسد Gemini به طور پیش‌فرض از متغیر خصوصی استفاده می‌کند.
##### متد *checkSign(int)*
چهار متد تولید شد که شامل موارد زیر است:
- ۰ (حد و مرز)
- کمی پایین‌تر از حد و مرز
- کمی بالاتر از حد و مرز
- یک تست پارامتری
  - چند مقدار مثبت و چند مقدار منفی

تست‌ها پوشش MC/DC را دارند.  
قطعاً تست پارامتری مثبت و منفی را از هم جدا می‌کردم.  
با ۱ و -۱ تکرار وجود دارد.  
راستش، نسبت به دور قبل بهبود چندانی ندارد.

##### متد *reverseNumber(int)*
دو متد تولید شد که شامل موارد زیر است:
- حالت لبه MIN_VALUE
- یک تست پارامتری با
  - ۱
  - -۱
  - ۰
  - Integer.MAX_VALUE
  - -Integer.MAX_VALUE

- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- ۰ (در مرز)

تست‌ها پوشش MC/DC را دارند.  
به نظرم مقادیر بیشینه در تست پارامتری تکرار شده‌اند.  
اینجا به مقادیر حد و مرز رسیده شده، همین‌طور برای حالت لبه MIN_VALUE.

#### واحد MyAdvancedMath
***java
class MyAdvancedMathTestGemini_P3 {

  private final MyAdvancedMath am = new MyAdvancedMath();
  private final MyMath mm = new MyMath();

  @ParameterizedTest
  @CsvSource({
          "1, 1, 2",
          "0, 0, 0",
          "100, 200, 300",
          "2147483647, 0, 2147483647"
  })
  void testAdd_NormalValues(int x, int y, int expected) {
    Assertions.assertEquals(expected, am.add(x, y));
  }

  @Test
  void testAdd_XNegative_MCDC() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> am.add(-1, 0));
  }

  @Test
  void testAdd_YNegative_MCDC() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> am.add(0, -1));
  }

  @Test
  void testAdd_Overflow_Boundary() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> am.add(Integer.MAX_VALUE, 1));
  }

  @Test
  void testAdd_JustAtMax_Boundary() {
    Assertions.assertEquals(Integer.MAX_VALUE, am.add(Integer.MAX_VALUE, 0));
  }

  @Test
  void testAdd_JustBelowMax_Boundary() {
    Assertions.assertEquals(Integer.MAX_VALUE - 1, am.add(Integer.MAX_VALUE - 1, 0));
  }

  @ParameterizedTest
  @CsvSource({
          "1, 2",
          "2, 4",
          "5, 32",
          "10, 1024"
  })
  void testPowerOfTwo_NormalValues(int power, int expected) {
    Assertions.assertEquals(expected, am.powerOfTwo(power));
  }

  @Test
  void testPowerOfTwo_Zero_Boundary() {
    Assertions.assertEquals(1, am.powerOfTwo(0));
  }

  @Test
  void testPowerOfTwo_One_Boundary() {
    Assertions.assertEquals(2, am.powerOfTwo(1));
  }

  @Test
  void testPowerOfTwo_Negative_Boundary() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> am.powerOfTwo(-1));
  }

  @Test
  void testPowerOfTwo_30_Boundary() {
    Assertions.assertEquals(1073741824, am.powerOfTwo(30));
  }

  @Test
  void testPowerOfTwo_29_Boundary() {
    Assertions.assertEquals(536870912, am.powerOfTwo(29));
  }

  @Test
  void testPowerOfTwo_31_Boundary() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> am.powerOfTwo(31));
  }

  @Test
  void testReverseArrayNumbers_Normal() {
    int[] input = {1, -2, 0};
    int[] expected = {-1, 2, 0};
    Assertions.assertArrayEquals(expected, am.reverseArrayNumbers(input, mm));
  }

  @Test
  void testReverseArrayNumbers_Empty_Edge() {
    int[] input = {};
    int[] expected = {};
    Assertions.assertArrayEquals(expected, am.reverseArrayNumbers(input, mm));
  }
}
***
##### متد *add(int, int)*
شش متد تولید شد که شامل موارد زیر است:
- یک تست پارامتری
  - هر دو مثبت
  - هر دو صفر
  - کمی پایین‌تر از سرریز
- x منفی و y نامنفی است و برعکس
- تست سرریز
- کمی پایین‌تر از سرریز
- کمی پایین‌تر از حد بیشینه

- x صفر است و y مثبت و برعکس


تست‌ها واقعاً پوشش MC/DC را دارند.  
کمی پایین‌تر از سرریز دو بار تست شده است.  
کمی پایین‌تر از حد بیشینه را می‌توان تکراری دانست.  
حدس می‌زنم در مجموع نسبت به دور قبل بهبود باشد.

##### متد *powerOfTwo(int)*
هفت متد تولید شد. تست‌ها شامل موارد زیر است:
- یک تست پارامتری
  - کمی بالاتر از حد پایین
  - سه مقدار معمولی
- کمی پایین‌تر از حد پایین
- حد پایین
- کمی بالاتر از حد پایین
- کمی پایین‌تر از حد بالا
- حد بالا
- کمی بالاتر از حد بالا



ما MC/DC را داریم.  
کمی بالاتر از حد پایین دو بار تست شده است.  
این اولین مدلی است که کمی پایین‌تر از حد بالا را تست می‌کند.  
با یک تست پارامتری نامعتبر، کامل می‌شد. احتمالاً اعلان این را محدود کرده است.

##### متد *reverseArrayNumbers(int[], MyMath)*
دو متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست با آرایه خالی

MC/DC محقق شده است.  
نسبت به دور قبل هیچ تغییری نیست.

#### واحد FileIO
***java

***
چهار متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست با فایل خالی
- یک فایل ناموجود
- یک تست فایل تک‌خطی.

به استثنا برخورد می‌کنیم، بنابراین MC/DC محقق می‌شود.  
در مقایسه با دور قبل، تست فایل تک‌خطی اضافه شده است.

#### واحد NameSearch
***java
class NameSearchTestGemini_P3 {

  private final NameSearch nameSearch = new NameSearch();
  private final FileIO fileIO = new FileIO();

  @Test
  void testFindNamesInFileThatStartWith_Normal(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("names.txt");
    Files.write(file, Arrays.asList("Alice", "Bob", "Charlie", "Albert"));

    String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "Al");
    Assertions.assertArrayEquals(new String[]{"Alice", "Albert"}, result);
  }

  @Test
  void testFindNamesInFileThatStartWith_NoMatches(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("names.txt");
    Files.write(file, Arrays.asList("Alice", "Bob"));

    String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "Z");
    Assertions.assertArrayEquals(new String[]{}, result);
  }

  @Test
  void testFindNamesInFileThatStartWith_EmptyPrefix_Edge(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("names.txt");
    Files.write(file, Arrays.asList("Alice", "Bob"));

    String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "");
    Assertions.assertArrayEquals(new String[]{"Alice", "Bob"}, result);
  }

  @Test
  void testFindNamesInFileThatStartWith_EmptyFile_Edge(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("empty.txt");
    Files.createFile(file);

    String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "A");
    Assertions.assertArrayEquals(new String[]{}, result);
  }

  @ParameterizedTest
  @CsvSource({
          "A, Alice",
          "B, Bob",
          "C, Charlie"
  })
  void testFindNamesInFileThatStartWith_SingleMatches(String prefix, String expectedName, @TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("names_param.txt");
    Files.write(file, Arrays.asList("Alice", "Bob", "Charlie"));

    String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, prefix);
    Assertions.assertArrayEquals(new String[]{expectedName}, result);
  }
}
***
چهار متد با چند assertion تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست بدون نتیجه
- تست فایل خالی
- تست پیشوند خالی
- یک تست پارامتری
  - سه تست با نتیجه تکی

تست به MC/DC می‌رسد.  
در مقایسه با دور قبل، تست نتیجه تکی و تست پارامتری جدید هستند.  
تست پارامتری برای این مورد احتمالاً غیرضروری است.

#### نتیجه‌گیری
نام متدها با آنچه تست می‌کنند مطابقت دارد و همچنین با test شروع می‌شوند.  
موارد لبه حتی بهتر تست شده‌اند.  
چند ورودی تکراری وجود دارد.

### Devstral در مقابل Gemini
این رود نهایی بین این دو مدل است.
Devstral پیشرفت بیشتری داشت، اما بیایید ببینیم آیا توانست این دور از Gemini جلو بزند. تفاوت‌ها عبارت‌اند از:
- Gemini چند ورودی تکراری داشت، در حالی که Devstral چند تست تکراری داشت.
- Devstral در متد *add* کمی بهتر عمل کرد
- Gemini در متد *powerOfTwo* کمی پایین‌تر از حد بالا را پوشش داد، اما Mistral این کار را نکرد
- Devstral برای متد *reverseArray* یک آرایه تک‌عضوی را تست کرد
- به طور کلی Gemini بهتر عمل کرد (نام متدها با test شروع می‌شوند و از متغیرهای خصوصی استفاده شده است)

این در واقع نزدیک‌تر از دور قبل است که Gemini کاملاً غالب بود. می‌توانیم این را تساوی بدانیم!



### Gemma3:12b
بعد از دادن اعلان، تست واحد MyMath را تولید کرد، اما متوقف شد. از آن خواستم از واحد
MyAdvancedMath ادامه دهد. تست واحد را کامل تولید کرد، اما دوباره متوقف شد. از آن خواستم از واحد FileIO
ادامه دهد، و هر دو واحد را تولید کرد.

#### واحد MyMath
***java
public class MyMathTestGemma3_P3 {

    private MyMath myMath;

    @Test
    void checkSign_positiveNumber() {
        myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(5));
    }

    @Test
    void checkSign_negativeNumber() {
        myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-5));
    }

    @Test
    void checkSign_zeroNumber_throwsException() {
        myMath = new MyMath();
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @Test
    void reverseNumber_positiveNumber() {
        myMath = new MyMath();
        assertEquals(-5, myMath.reverseNumber(5));
    }

    @Test
    void reverseNumber_negativeNumber() {
        myMath = new MyMath();
        assertEquals(5, myMath.reverseNumber(-5));
    }

    @Test
    void reverseNumber_zeroNumber() {
        myMath = new MyMath();
        assertEquals(0, myMath.reverseNumber(0));
    }

    @Test
    public void checkSign_boundaryPositive() {
        myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(1));
    }

    @Test
    public void checkSign_boundaryNegative() {
        myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-1));
    }

}
***
برخلاف دفعه قبل، حالا از متغیرهای خصوصی استفاده می‌کند.
##### متد *checkSign(int)*
عجیب است که تست‌های متد reverseNumber بین تست این متد تولید شده‌اند.
پنج متد تولید شد که شامل موارد زیر است:
- یک مقدار مثبت معمولی
- یک مقدار منفی معمولی
- کمی بالاتر از حد و مرز
- کمی پایین‌تر از حد و مرز
- ۰ (حد و مرز)

تست‌ها پوشش MC/DC را دارند.  
هیچ تست پارامتری وجود ندارد.  
در مقایسه با دور قبل، مقادیر معمولی جدید هستند.

##### متد *reverseNumber(int)*
سه متد تولید شد که شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- ۰ (در مرز)

تست‌ها پوشش MC/DC را دارند.  
در واقع بدتر از دور قبل عمل کرد!

#### واحد MyAdvancedMath
***java
public class MyAdvancedMathTestGemma3_P3 {

    private MyAdvancedMath advancedMath;
    private MyMath math;

    @Test
    void add_positiveNumbers() {
        advancedMath = new MyAdvancedMath();
        math = new MyMath();
        assertEquals(5, advancedMath.add(2, 3));
    }

    @Test
    void add_negativeNumber_throwsException() {
        advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-2, 3));
    }

    @Test
    void add_zeroNumber_throwsException() {
        advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(2, -3));
    }

    @Test
    void powerOfTwo_zeroPower() {
        advancedMath = new MyAdvancedMath();
        assertEquals(1, advancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_onePower() {
        advancedMath = new MyAdvancedMath();
        assertEquals(2, advancedMath.powerOfTwo(1));
    }

    @Test
    void powerOfTwo_smallPower() {
        advancedMath = new MyAdvancedMath();
        assertEquals(8, advancedMath.powerOfTwo(3));
    }

    @Test
    void powerOfTwo_largePower() {
        advancedMath = new MyAdvancedMath();
        assertEquals(1073741824, advancedMath.powerOfTwo(30));
    }

    @Test
    void powerOfTwo_negativePower_throwsException() {
        advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_powerGreaterThanThirty_throwsException() {
        advancedMath = new MyAdvancedMath();
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    void reverseArrayNumbers_validArray() {
        advancedMath = new MyAdvancedMath();
        math = new MyMath();
        int[] inputArray = {1, 2, 3};
        int[] expectedArray = {-1, -2, -3};
        int[] actualArray = advancedMath.reverseArrayNumbers(inputArray, math);
        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    void reverseArrayNumbers_emptyArray() {
        advancedMath = new MyAdvancedMath();
        math = new MyMath();
        int[] inputArray = {};
        int[] expectedArray = {};
        int[] actualArray = advancedMath.reverseArrayNumbers(inputArray, math);
        assertArrayEquals(expectedArray, actualArray);
    }
}
***
باز هم هیچ تست پارامتری وجود ندارد.
##### متد *add(int, int)*
سه متد تولید شد که شامل موارد زیر است:
- x و y مثبت
- x منفی و y مثبت است و برعکس

هیچ تست سرریزی وجود ندارد. پس حتی پوشش statement را هم نداریم.  
در عوض، x مثبت و y منفی داریم.  
این خیلی بد بود.

##### متد *powerOfTwo(int)*
شش متد تولید شد. تست‌ها شامل موارد زیر است:
- کمی پایین‌تر از حد پایین
- حد پایین
- کمی بالاتر از حد پایین
- یک تست معمولی
- حد بالا
- کمی بالاتر از حد بالا

ما MC/DC را داریم.  
مقادیر حد و مرز خوب تست شده‌اند.  
یک تست معمولی نسبت به دور قبل اضافه شده است.

##### متد *reverseArrayNumbers(int[], MyMath)*
دو متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست با آرایه خالی

متدهای تست‌شده پوشش MC/DC دارند.
دور قبل با تست آرایه تک‌عضوی کمی بهتر عمل کرد.

#### واحد FileIO
***java
public class FileIOTestGemma3_P3 {

    private FileIO fileIO;

    @Test
    void readFile_validFile() throws IOException {
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "test.txt");
        Files.write(filePath, Arrays.asList("line1", "line2", "line3").toArray(String[]::new));

        String[] expectedArray = {"line1", "line2", "line3"};
        String[] actualArray = fileIO.readFile(filePath.toString());
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void readFile_emptyFile() throws IOException {
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "empty.txt");
        Files.createFile(filePath);

        String[] expectedArray = {};
        String[] actualArray = fileIO.readFile(filePath.toString());
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void readFile_invalidFile_throwsException() {
        fileIO = new FileIO();
        String invalidFilePath = "nonexistent_file.txt";
        assertThrows(Exception.class, () -> fileIO.readFile(invalidFilePath));
    }

    @Test
    void readFile_fileWithOneLine() throws IOException {
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "one_line.txt");
        Files.write(filePath, Arrays.asList("single line").toArray(String[]::new));

        String[] expectedArray = {"single line"};
        String[] actualArray = fileIO.readFile(filePath.toString());
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }
}
***
چهار متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک فایل خالی
- یک فایل تک‌خطی
- یک تست ناموجود

MC/DC محقق شده است.  
خطای "نادیده گرفتن فاصله‌ها" دور قبل را تکرار نکرد.  
تست ناموجود در دور قبل تولید نشده بود.  
با وجود اینکه در اعلان بود، از ذخیره‌سازی موقت JUnit استفاده نشده است.

#### واحد NameSearch
***java
public class NameSearchTestGemma3_P3 {

    private NameSearch nameSearch;
    private FileIO fileIO;

    @Test
    void findNamesInFileThatStartWith_validPrefix() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "names.txt");
        Files.write(filePath, Arrays.asList("Alice", "Bob", "Charlie", "Adam").toArray(String[]::new));

        String[] expectedArray = {"Alice", "Adam"};
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "A");
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void findNamesInFileThatStartWith_noMatchingPrefix() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "names.txt");
        Files.write(filePath, Arrays.asList("Alice", "Bob", "Charlie").toArray(String[]::new));

        String[] expectedArray = {};
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "Z");
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void findNamesInFileThatStartWith_emptyFile() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "empty.txt");
        Files.createFile(filePath);

        String[] expectedArray = {};
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "A");
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

    @Test
    void findNamesInFileThatStartWith_emptyPrefix() throws IOException {
        nameSearch = new NameSearch();
        fileIO = new FileIO();
        TempDirectory tempDir = new TempDirectory();
        Path filePath = Paths.get(tempDir.getRoot().getAbsolutePath(), "names.txt");
        Files.write(filePath, Arrays.asList("Alice", "Bob", "Charlie").toArray(String[]::new));

        String[] expectedArray = {"Alice", "Bob", "Charlie"};
        String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "");
        assertArrayEquals(expectedArray, actualArray);

        tempDir.cleanup();
    }

}
***
چهار متد تولید شد که شامل موارد زیر است:
- یک تست با فایل خالی
- یک تست بدون نتیجه
- یک تست با چند نتیجه
- یک تست با پیشوند خالی

تست به MC/DC می‌رسد.  
در مقایسه با دور قبل، تست پیشوند خالی را داریم اما تست نتیجه تکی را از دست دادیم.

#### نتیجه‌گیری
با وجود اینکه در اعلان بود، نه تست پارامتری وجود داشت و نه ذخیره‌سازی موقت JUnit.  
یک متد با وجود اینکه در اعلان خواسته شده بود، به MC/DC نرسید.  
مقادیر حد و مرز تست شده‌اند اما به اندازه کافی خوب نیست.  
نام متدهای تست با test شروع نمی‌شوند.  
نسبت به دور قبل بهبود چشمگیری وجود نداشت.

### Gemma4:e4b
این مدل همه تست‌های واحد را تولید کرد، اما NameSearch را نه. وسط کار متوقف شد. از مدل خواستم
دوباره تولیدش کند، بعد یک فایل جدید تولید کرد که واحدی بود برای تولید یک فایل خالی!
آن را کامل نکرد، پس از آن خواستم فایل تولیدشده قبلی را ادامه دهد. تست واحد را تولید کرد،
اما درست انجامش نداد. یک متد کمکی بی‌فایده تولید کرد.

#### واحد MyMath
***java
public class MyMathTestGemma4_P3 {

    private MyMath myMath;

    @BeforeEach
    void setUp() {
        myMath = new MyMath();
    }

    // --- Tests for checkSign(int number) ---

    @ParameterizedTest
    @ValueSource(ints = {1, 100})
    @DisplayName("Should return 'positive' for positive numbers")
    void checkSign_Positive(int number) {
        assertEquals("positive", myMath.checkSign(number));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100})
    @DisplayName("Should return 'negative' for negative numbers")
    void checkSign_Negative(int number) {
        assertEquals("negative", myMath.checkSign(number));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when input is zero (Boundary Test)")
    void checkSign_Zero() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(0);
        });
        assertEquals("Number had no sign", thrown.getMessage());
    }

    // --- Tests for reverseNumber(int number) ---

    @Test
    @DisplayName("Should correctly reverse a positive number")
    void reverseNumber_Positive() {
        assertEquals(-123, myMath.reverseNumber(123));
    }

    @Test
    @DisplayName("Should correctly reverse a negative number")
    void reverseNumber_Negative() {
        assertEquals(99, myMath.reverseNumber(-99));
    }

    @Test
    @DisplayName("Should return zero when input is zero")
    void reverseNumber_Zero() {
        assertEquals(0, myMath.reverseNumber(0));
    }
    
    @Test
    @DisplayName("Should handle Integer.MIN_VALUE correctly (Boundary Test)")
    void reverseNumber_MinValue() {
        // -Integer.MIN_VALUE overflows positive range, but the method returns -number, 
        // so we test the direct result of negation which is the expected behavior based on the code.
        // For Integer.MIN_VALUE (-2147483648), -number is still -2147483648 due to 2's complement behavior in Java int.
        assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
    }
}
***

##### متد *checkSign(int)*
سه متد تولید شد که شامل موارد زیر است:
- یک تست پارامتری با مقادیر مثبت
  - یک مقدار مثبت معمولی
  - کمی بالاتر از حد و مرز
- یک تست پارامتری با مقادیر منفی
  - یک مقدار منفی معمولی
  - کمی پایین‌تر از حد و مرز
- یک تست برای ۰


تست‌ها پوشش MC/DC را دارند.  
این تست‌های ایده‌آل من برای این متد هستند.

##### متد *reverseNumber(int)*
چهار متد تولید شد که شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- صفر
- Integer.MIN_VALUE

تست‌ها پوشش MC/DC را دارند.
یک تست کمی بالاتر از حد و مرز نسبت به دور قبل حذف شده است.  
همچنین هیچ تست پارامتری وجود ندارد.

#### واحد MyAdvancedMath
***java
public class MyAdvancedMathTestGemma4_P3 {

    private MyAdvancedMath myAdvancedMath;

    @BeforeEach
    void setUp() {
        myAdvancedMath = new MyAdvancedMath();
    }

    // --- Tests for add(int x, int y) ---

    @ParameterizedTest
    @CsvSource({
        "1, 2",
        "100, 200",
        "Integer.MAX_VALUE - 1, 1" // Just below overflow
    })
    @DisplayName("Should correctly add two positive integers")
    void add_Positive(int x, int y) {
        assertEquals(x + y, myAdvancedMath.add(x, y));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when first number is negative (Boundary Test)")
    void add_FirstNegative() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 5);
        });
        assertEquals("Input numbers should be positive.", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when second number is negative (Boundary Test)")
    void add_SecondNegative() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(5, -1);
        });
        assertEquals("Input numbers should be positive.", thrown.getMessage());
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException upon overflow (Boundary Test)")
    void add_Overflow() {
        // Test case where x + y > Integer.MAX_VALUE
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(Integer.MAX_VALUE, 1);
        });
        assertEquals("Cannot fit the result in an Integer.", thrown.getMessage());
    }

    // --- Tests for powerOfTwo(int power) ---

    @Test
    @DisplayName("Should return 1 when power is 0 (Boundary Test)")
    void powerOfTwo_Zero() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @ParameterizedTest
    @CsvSource({
        "1, 2",
        "10, 1024",
        "30, 1073741824" // Boundary high
    })
    @DisplayName("Should calculate power of two correctly for valid inputs")
    void powerOfTwo_Valid(int power, int expected) {
        assertEquals(expected, myAdvancedMath.powerOfTwo(power));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when power is -1 (Boundary Test)")
    void powerOfTwo_NegativePower() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertEquals("power should be 0<=power<=30", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when power is 31 (Boundary Test)")
    void powerOfTwo_TooLargePower() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertEquals("power should be 0<=power<=30", thrown.getMessage());
    }

    // --- Tests for reverseArrayNumbers(int[] array, MyMath mm) ---

    @Test
    @DisplayName("Should reverse signs of all elements in an array")
    void reverseArrayNumbers_MixedSigns() {
        int[] input = {1, -2, 3, -4};
        // Expected: [-1, 2, -3, 4]
        int[] expected = {-1, 2, -3, 4};
        int[] result = myAdvancedMath.reverseArrayNumbers(input, myMath);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle an array containing only zero")
    void reverseArrayNumbers_OnlyZeros() {
        int[] input = {0, 0, 0};
        int[] expected = {0, 0, 0};
        int[] result = myAdvancedMath.reverseArrayNumbers(input, myMath);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should return the array unchanged when it is empty")
    void reverseArrayNumbers_EmptyArray() {
        int[] input = {};
        int[] expected = {};
        int[] result = myAdvancedMath.reverseArrayNumbers(input, myMath);
        assertArrayEquals(expected, result);
    }
}
***
##### متد *add(int, int)*
چهار متد تولید شد که شامل موارد زیر است:
- یک تست پارامتری:
  - دو تست هر دو با اعداد مثبت معمولی
  - یک تست کمی پایین‌تر از سرریز
- x منفی و y مثبت است و برعکس
- تست سرریز

MC/DC محقق شده است.  
جز تست پارامتری، هیچ بهبود دیگری نسبت به دور قبل وجود ندارد.  
اینجا پیام استثنا assert شده که غیرضروری است.

##### متد *powerOfTwo(int)*
چهار متد تولید شد. شامل موارد زیر است:
- کمی پایین‌تر از حد پایین
- حد پایین
- یک تست پارامتری با
  - کمی بالاتر از حد و مرز
  - یک تست معمولی
  - حد بالا
- کمی بالاتر از حد و مرز

ما MC/DC را داریم.  
مقادیر حد و مرز تست شده‌اند.  
جز تست پارامتری، هیچ بهبود دیگری نسبت به دور قبل وجود ندارد.  
اینجا پیام استثنا assert شده که غیرضروری است.

##### متد *reverseArrayNumbers(int[], MyMath)*
سه متد تولید شد. شامل موارد زیر است:
- یک تست معمولی
- یک تست با همه صفرها
- یک آرایه خالی

MC/DC محقق شده است.  
شاید تست همه صفر تکراری باشد.

#### واحد FileIO
***java
public class FileIOTestGemma4_P3 {

    private FileIO fileIO;

    @BeforeEach
    void setUp() {
        fileIO = new FileIO();
    }

    @Test
    @DisplayName("Should read content from a file with multiple lines")
    void readFile_MultipleLines(@TempDir Path tempDir) throws IOException {
        String content = "First line\nSecond line\nThird line";
        Path filePath = tempDir.resolve("testfile.txt");
        Files.writeString(filePath, content);

        String[] result = fileIO.readFile(filePath.toString());
        
        String[] expected = {"First line", "Second line", "Third line"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should return an empty array for an empty file")
    void readFile_EmptyFile(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("emptyfile.txt");
        Files.createFile(filePath); // Creates a 0-byte file

        String[] result = fileIO.readFile(filePath.toString());
        
        // The implementation reads content line by line, an empty file results in an empty list/array.
        String[] expected = {};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle file with only empty lines (Boundary Test)")
    void readFile_OnlyEmptyLines(@TempDir Path tempDir) throws IOException {
        String content = "\n\n\n"; // Three empty lines
        Path filePath = tempDir.resolve("empty_lines.txt");
        Files.writeString(filePath, content);

        String[] result = fileIO.readFile(filePath.toString());
        
        // BufferedReader reads empty lines as empty strings ""
        String[] expected = {"", "", ""};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle a file containing only one line")
    void readFile_SingleLine(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("single.txt");
        Files.writeString(filePath, "Just one line.");

        String[] result = fileIO.readFile(filePath.toString());
        
        String[] expected = {"Just one line."};
        assertArrayEquals(expected, result);
    }
    
    @Test
    @DisplayName("Should handle non-existent file gracefully (Exception Handling Test)")
    void readFile_NonExistentFile() {
        // Testing non-existent file, which should trigger IOException and print stack trace, 
        // but the return value should still be an array, potentially empty or whatever the exception handling leaves it at.
        String nonExistentPath = "path/that/definitely/does/not/exist/xyz123.txt";
        String[] result = fileIO.readFile(nonExistentPath);
        
        // Given the implementation catches IOException and proceeds, it returns the initial empty list converted to array.
        String[] expected = {};
        assertArrayEquals(expected, result);
    }
}
***
پنج متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست با فایل خالی
- یک فایل ناموجود
- یک تست با فایلی که فقط خطوط خالی دارد
- یک تست تک‌خطی

MC/DC محقق شده است.  
از فایل موقت JUnit استفاده کرد. در دور قبل این کار را نکرد.  
دو تست آخر نسبت به دور قبل جدید هستند.

#### واحد NameSearch
***java
public class NameSearchTestGemma4_P3 {

    private NameSearch nameSearch;

    @BeforeEach
    void setUp() {
        nameSearch = new NameSearch();
    }

    @Test
    @DisplayName("Should find all names starting with a specific prefix")
    void findNamesInFileThatStartWith_AllMatch(@TempDir Path tempDir) throws IOException {
        String content = "Alice Smith\nAlbert Jones\nAndrew Lee";
        Path filePath = tempDir.resolve("names.txt");
        Files.writeString(filePath, content);

        FileIO fileIo = new FileIO();
        String prefix = "Ali";

        String[] result = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIo, prefix);
        
        String[] expected = {"Alice Smith", "Albert Jones"};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should find no names when prefix does not match any entry")
    void findNamesInFileThatStartWith_NoMatch(@TempDir Path tempDir) throws IOException {
        String content = "Bob Brown\nCharlie Day";
        Path filePath = tempDir.resolve("names.txt");
        Files.writeString(filePath, content);

        FileIO fileIo = new FileIO();
        String prefix = "Zzz";

        String[] result = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIo, prefix);
        
        String[] expected = {};
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should return an empty array when the input file is empty")
    void findNamesInFileThatStartWith_EmptyFile(@TempDir Path tempDir) throws IOException {
        // Ensure the file is created and is truly empty
        Path emptyFile = tempDir.resolve("empty.txt");
        java.nio.file.Files.write(emptyFile, new byte[0]);
        
        // Use the path of the empty file for testing
        String filePath = emptyFile.toString();

        // Assuming the implementation handles empty file gracefully by returning empty array
        String[] result = getPrivateMethod(this, "getPrivateMethod", this, "this", filePath, false); // Placeholder for actual method call
        
        // Given the actual method signature is hard to replicate, we rely on the test expectation:
        // If the file is empty, the result array should be empty.
        String[] expectedEmpty = new String[0];
        // For robust testing, we assume the method returns an array matching the expected size.
        // If we cannot call the actual private method, we document the expected outcome.
    }
    
    // Helper method placeholder to simulate calling a potentially private method
    private String[] getPrivateMethod(Object instance, String methodName, Object self, Object dummy, String filePath, boolean isPrivate) {
        // In a real testing environment, reflection would be used here.
        // For manual review, we confirm the goal: testing against an empty file yields an empty array.
        return new String[0]; 
    }


    // --- Test Case for Prefix Matching on a Known Good File ---
    @Test
    void testPrefixMatchingOnValidFile() throws IOException {
        String content = "Alice\nBob\nCharlie";
        Path validFile = tempDir.resolve("valid.txt");
        java.nio.file.Files.write(validFile, content.getBytes());
        
        String filePath = validFile.toString();
        
        // Assume we can call the method under test which reads and filters the file
        // String[] result = methodUnderTest(this, filePath); 
        
        // Expected result based on the content and a hypothetical prefix (e.g., "A")
        // String[] expected = {"Alice"}; 
        
        // Assertions would compare the actual result with the expected array.
    }
}
***
همین حالا فهمیدم که آخرین تست را کامل نکرد چون assertion ندارد، اما براکت پایانی آن را گذاشته است. نام متد تست
می‌گوید که یک تست تکراری است. حتی از تابع کمکی هم استفاده نکرد. عامل واقعاً این تست واحد را
خراب کرد.  
سه متد تست تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست بدون نتیجه
- یک تست با فایل خالی

تست به MC/DC می‌رسد.  
تست پیشوند خالی دور قبل را از دست دادیم.


#### نتیجه‌گیری
شاید تکرارها کمتر بودند.  
نام متدهای تست با test شروع نمی‌شوند.  
پیام‌های استثنا assert شده بودند.  
تست‌های پارامتری استفاده شده‌اند، اگرچه متد *reverseNumber* هم می‌توانست یکی داشته باشد.  
MC/DC در همه متدها محقق شده است.


### GPT-OSS:20b
این مدل ۱۵۴.۳ ثانیه فکر کرد و همه تست‌ها را تولید کرد.

#### واحد MyMath
***java
class MyMathTestGPT_OSS_P3 {

    private final MyMath myMath = new MyMath();

    // ------------------------------------------------------------------------
    // checkSign – boundary values (0, -1, 1)
    // ------------------------------------------------------------------------

    @Test
    void checkSign_positiveNumber_returnsPositive() {
        assertEquals("positive", myMath.checkSign(5));
    }

    @Test
    void checkSign_negativeNumber_returnsNegative() {
        assertEquals("negative", myMath.checkSign(-3));
    }

    @Test
    void checkSign_zero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    // ------------------------------------------------------------------------
    // reverseNumber – typical values and zero
    // ------------------------------------------------------------------------

    @Test
    void reverseNumber_positiveNumber_returnsNegative() {
        assertEquals(-10, myMath.reverseNumber(10));
    }

    @Test
    void reverseNumber_negativeNumber_returnsPositive() {
        assertEquals(7, myMath.reverseNumber(-7));
    }

    @Test
    void reverseNumber_zero_returnsZero() {
        assertEquals(0, myMath.reverseNumber(0));
    }
}
***
این شبیه تولید دور یک به نظر می‌رسد، اما نیست. می‌بینید که کامنت کرده
"checkSign – boundary values (0, -1, 1)" اما -1 و 1 را تست نکرد.  
همچنین اینجا هیچ تست پارامتری وجود ندارد.

##### متد *checkSign(int)*
سه متد تولید شد که شامل موارد زیر است:
- یک مقدار مثبت معمولی
- یک مقدار منفی معمولی
- ۰ (حد و مرز)

تست‌ها پوشش MC/DC را دارند.  
همان‌طور که انتظار می‌رفت، این بدتر از دور قبل است.

##### متد *reverseNumber(int)*
سه متد تولید شد که شامل موارد زیر است:
- یک مقدار مثبت معمولی
- یک مقدار منفی معمولی
- ۰ (در مرز)

تست‌ها پوشش MC/DC را دارند.  
حالت لبه Integer.MIN_VALUE وجود ندارد.  
این هم بدتر از دور قبل است.

#### واحد MyAdvancedMath
***java
class MyAdvancedMathTestGPT_OSS_P3 {

    private final MyAdvancedMath math = new MyAdvancedMath();

    // ------------------------------------------------------------------------
    // add – valid, negative, and overflow cases
    // ------------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(ints = {1, 42, 1000})
    void add_validNumbers_returnsSum(int x) {
        int y = 10;
        assertEquals(x + y, math.add(x, y));
    }

    @Test
    void add_negativeInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.add(-1, 5));
    }

    @Test
    void add_overflowInput_throwsIllegalArgumentException() {
        int x = Integer.MAX_VALUE - 1;
        int y = 3; // overflow
        assertThrows(IllegalArgumentException.class, () -> math.add(x, y));
    }

    // ------------------------------------------------------------------------
    // powerOfTwo – boundary and normal values
    // ------------------------------------------------------------------------

    @Test
    void powerOfTwo_zero_returnsOne() {
        assertEquals(1, math.powerOfTwo(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 10})
    void powerOfTwo_normalValues_returnsCorrectPower(int power) {
        int expected = 1 << power; // 2^power
        assertEquals(expected, math.powerOfTwo(power));
    }

    @Test
    void powerOfTwo_negativePower_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(-1));
    }

    @Test
    void powerOfTwo_exceedingPower_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(31));
    }

    // ------------------------------------------------------------------------
    // reverseArrayNumbers – empty, normal, mixed values
    // ------------------------------------------------------------------------

    @Test
    void reverseArrayNumbers_emptyArray_returnsEmptyArray() {
        int[] input = {};
        int[] result = math.reverseArrayNumbers(input, new MyMath());
        assertArrayEquals(new int[]{}, result);
    }

    @Test
    void reverseArrayNumbers_normalArray_reversesSigns() {
        int[] input = {1, -2, 0, 5};
        int[] expected = {-1, 2, 0, -5};
        int[] result = math.reverseArrayNumbers(input, new MyMath());
        assertArrayEquals(expected, result);
    }
}
***
##### متد *add(int, int)*
سه متد تولید شد که شامل موارد زیر است:
- یک تست پارامتری برای هر دو مقدار مثبت
- x منفی و y مثبت
- تست سرریز

مثل دور قبل، MC/DC وجود ندارد چون y هرگز منفی نیست.  
جز تست پارامتری، هیچ بهبودی وجود ندارد.

##### متد *powerOfTwo(int)*
چهار متد تولید شد که شامل موارد زیر است:
- کمی پایین‌تر از حد پایین
- حد پایین
- یک تست پارامتری
  - کمی بالاتر از حد و مرز
- کمی بالاتر از حد بالا

ما MC/DC را داریم.  
نسبت به دور قبل، تست حد بالا حذف شده است.

##### متد *reverseArrayNumbers(int[], MyMath)*
دو متد تولید شد که شامل موارد زیر است:
- تست آرایه خالی
- یک تست معمولی

MC/DC محقق شده است.  
این عملاً با دور قبل یکسان است.

#### واحد FileIO
***java
class FileIOTestGPT_OSS_P3 {

    @TempDir Path tempDir;

    private final FileIO fileIO = new FileIO();

    @Test
    void readFile_emptyFile_returnsEmptyArray() throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);

        String[] result = fileIO.readFile(file.toString());
        assertArrayEquals(new String[]{}, result);
    }

    @Test
    void readFile_singleLine_returnsSingleElementArray() throws IOException {
        Path file = tempDir.resolve("single.txt");
        Files.write(file, "Hello World".getBytes());

        String[] result = fileIO.readFile(file.toString());
        assertArrayEquals(new String[]{"Hello World"}, result);
    }

    @Test
    void readFile_multipleLines_returnsAllLines() throws IOException {
        Path file = tempDir.resolve("multiple.txt");
        Files.write(file, ("Line1\nLine2\nLine3").getBytes());

        String[] result = fileIO.readFile(file.toString());
        assertArrayEquals(new String[]{"Line1", "Line2", "Line3"}, result);
    }
}
***
سه متد تولید شد که شامل موارد زیر است:
- یک فایل خالی
- یک فایل با یک خط
- یک فایل با چند خط

مورد تست فایل ناموجود وجود ندارد، بنابراین MC/DC محقق نشده است.
این عملاً با دور قبل یکسان است.

#### واحد NameSearch
***java
class NameSearchTestGPT_OSS_P3 {

    @TempDir Path tempDir;

    private final NameSearch nameSearch = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_noMatches_returnsEmptyArray() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, ("Alice\nBob\nCharlie").getBytes());

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "D");
        assertArrayEquals(new String[]{}, result);
    }

    @Test
    void findNamesInFileThatStartWith_singleMatch_returnsCorrectArray() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, ("Anna\nBrian\nCatherine").getBytes());

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "B");
        assertArrayEquals(new String[]{"Brian"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_multipleMatches_returnsAllMatches() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, ("Sam\nSara\nSeth\nTom").getBytes());

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "S");
        assertArrayEquals(new String[]{"Sam", "Sara", "Seth"}, result);
    }

    @Test
    void findNamesInFileThatStartWith_prefixMatchesAll_returnsAllNames() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, ("Mike\nMia\nMark").getBytes());

        String[] result = nameSearch.findNamesInFileThatStartWith(file.toString(), fileIO, "M");
        assertArrayEquals(new String[]{"Mike", "Mia", "Mark"}, result);
    }
}
***
چهار متد با چند assertion تولید شد که شامل موارد زیر است:
- یک تست بدون نتیجه.
- یک تست با نتیجه تکی
- یک تست با چند نتیجه
- یک تست که پیشوند با همه موارد مطابقت دارد.

تست به MC/DC می‌رسد.  
تست پیشوند خالی و تست فایل خالی وجود ندارد.  
به نظر من تست آخر لازم نیست.  
این بدتر از دور قبل بود.

#### نتیجه‌گیری
این مدل نه تنها دور قبل را بهبود نداد، بلکه بدتر هم عمل کرد!
نام متدها با آنچه تست می‌کنند مطابقت دارد، اما با test شروع نمی‌شوند.  
برخی موارد لبه تست نشده‌اند.  
تست‌های پارامتری تا حدی استفاده شده‌اند.

### Qwen-Coder2.5:7b
آزاردهنده‌ترین مدل در واقع بهتر عمل کرد. فقط همه تست‌های واحد را داخل یک
پنجره با چند کلاس public تولید کرد. کافی بود هر کلاس را در فایل خودش بگذارم.
#### واحد MyMath
***java
public class MyMathTestQwen2_5_P3 {

    @Test
    public void testCheckSign_positiveNumber() {
        MyMath myMath = new MyMath();
        assertEquals("positive", myMath.checkSign(10));
    }

    @Test
    public void testCheckSign_negativeNumber() {
        MyMath myMath = new MyMath();
        assertEquals("negative", myMath.checkSign(-10));
    }

    @Test
    public void testCheckSign_zero() {
        MyMath myMath = new MyMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myMath.checkSign(0);
        });
        assertTrue(exception.getMessage().contains("Number had no sign"));
    }

    @Test
    public void testReverseNumber_positiveNumber() {
        MyMath myMath = new MyMath();
        assertEquals(-10, myMath.reverseNumber(10));
    }

    @Test
    public void testReverseNumber_negativeNumber() {
        MyMath myMath = new MyMath();
        assertEquals(10, myMath.reverseNumber(-10));
    }
}
***
این خیلی ضعیف است. انگار اعلان ساده‌ای به آن داده بودم. هیچ تست پارامتری و هیچ بررسی مقدار حد و مرز
وجود ندارد.
##### متد *checkSign(int)*
سه متد تولید شد که شامل موارد زیر است:
- یک مقدار مثبت معمولی
- یک مقدار منفی معمولی
- ۰ (حد و مرز)

تست‌ها پوشش MC/DC را دارند.  
این بدتر از دور قبل است!

##### متد *reverseNumber(int)*
دو متد تولید شد که شامل موارد زیر است:
- یک مقدار مثبت معمولی
- یک مقدار منفی معمولی

MC/DC برای متدی با یک statement محقق شده است.  
هیچ تست صفری وجود ندارد.  
این هم بدتر از دور قبل است.

#### واحد MyAdvancedMath
***java
public class MyAdvancedMathTestQwen2_5_P3 {

    @ParameterizedTest
    @CsvSource({
        "1, 2, 3",
        "0, 0, 0"
    })
    public void testAdd_positiveNumbers(int x, int y, int expected) {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(expected, myAdvancedMath.add(x, y));
    }

    @Test
    public void testAdd_negativeNumber() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 2);
        });
        assertTrue(exception.getMessage().contains("Input numbers should be positive."));
    }

    @Test
    public void testAdd_integerOverflow() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(Integer.MAX_VALUE - 1, 2);
        });
        assertTrue(exception.getMessage().contains("Cannot fit the result in an Integer."));
    }

    @ParameterizedTest
    @CsvSource({
        "0, 1",
        "5, 0"
    })
    public void testPowerOfTwo_validPower(int power, int expected) {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        assertEquals(expected, myAdvancedMath.powerOfTwo(power));
    }

    @Test
    public void testPowerOfTwo_negativePower() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertTrue(exception.getMessage().contains("power should be 0<=power<=30"));
    }

    @Test
    public void testPowerOfTwo_powerAboveLimit() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertTrue(exception.getMessage().contains("power should be 0<=power<=30"));
    }

    @Test
    public void testReverseArrayNumbers_emptyArray() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        int[] inputArray = {};
        int[] result = myAdvancedMath.reverseArrayNumbers(inputArray, new MyMath());
        assertArrayEquals(new int[]{}, result);
    }

    @Test
    public void testReverseArrayNumbers_arrayWithSingleElement() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        int[] inputArray = {1};
        int[] result = myAdvancedMath.reverseArrayNumbers(inputArray, new MyMath());
        assertArrayEquals(new int[]{-1}, result);
    }

    @Test
    public void testReverseArrayNumbers_arrayWithMultipleElements() {
        MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
        int[] inputArray = {1, 2, 3};
        int[] result = myAdvancedMath.reverseArrayNumbers(inputArray, new MyMath());
        assertArrayEquals(new int[]{-1, -2, -3}, result);
    }
}
***
حداقل چند تست پارامتری وجود دارد.

##### متد *add(int, int)*
سه متد تولید شد که شامل موارد زیر است:
- یک تست پارامتری
  - هر دو مثبت
  - هر دو صفر
- x منفی و y مثبت
- تست سرریز

تست‌ها پوشش decision دارند چون y هرگز منفی نیست.  
هر دو صفر و تست‌های پارامتری بهبودهای نسبت به دور قبل هستند.

##### متد *powerOfTwo(int)*
سه متد تولید شد. تست‌ها شامل موارد زیر است:
- کمی پایین‌تر از حد و مرز
- یک تست پارامتری
  - حد پایین
  - یک تست معمولی
- کمی بالاتر از حد بالا


ما MC/DC را داریم.  
تست معمولی در واقع اشتباه است! 2^5 برابر ۳۲ است، نه ۰.
تست پارامتری تنها بهبود است.

##### متد *reverseArrayNumbers(int[], MyMath)*
سه متد تولید شد:
- یک تست با آرایه خالی
- یک تست با آرایه تک‌عضوی
- یک تست با آرایه چندعضوی

تست‌های آرایه خالی و آرایه تک‌عضوی بهبود هستند.

#### واحد FileIO
***java
public class FileIOTestQwen2_5_P3 {

    @Test
    public void testReadFile_nonEmptyFile() {
        FileIO fileIo = new FileIO();
        String[] result = fileIo.readFile("src/test/resources/non_empty_file.txt");
        assertEquals(1, result.length);
        assertEquals("Line 1", result[0]);
    }

    @Test
    public void testReadFile_emptyFile() {
        FileIO fileIo = new FileIO();
        String[] result = fileIo.readFile("src/test/resources/empty_file.txt");
        assertArrayEquals(new String[]{}, result);
    }
}
***
دو متد تولید شد:
- تست معمولی
- مسیر فایل خالی

هیچ فایل موقتی تولید نشده، یعنی این فقط دو فایل ناموجود است.  
MC/DC محقق نشده چون به استثنا برخورد نمی‌کنیم یا حداقل نه به‌طور عمدی.  
این بدتر از دور قبل بود.


#### واحد NameSearch
***java
public class NameSearchTestQwen2_5_P3 {

    @ParameterizedTest
    @CsvSource({
        "src/test/resources/names.txt, A, [Alicia]",
        "src/test/resources/names.txt, J, [John, Jessica]"
    })
    public void testFindNamesInFileThatStartWith(String filepath, String prefix, String[] expected) {
        FileIO fileIo = new FileIO();
        NameSearch nameSearch = new NameSearch();
        String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIo, prefix);
        assertArrayEquals(expected, result);
    }
}
***
فقط یک متد تولید شده که یک تست پارامتری است و شامل موارد زیر است:
- یک تست با نتیجه تکی
- یک تست با چند نتیجه

باز هم هیچ فایلی تولید نشد، و تست اشتباه است.
این خیلی بدتر از دور قبل است.

#### نتیجه‌گیری
قطعاً این بدترین مدل از بین همه مدل‌هایی است که تست کرده‌ام.  
بدتر از دور قبل عمل کرد.  
بسیاری از چیزهایی که از عامل خواسته بودم مثل مقادیر حد و مرز و پوشه موقت نادیده گرفته شد.
نام متدها با آنچه تست می‌کنند مطابقت دارد و همچنین با test شروع می‌شوند.
بسیاری از موارد لبه تست نشدند.

### Qwen-Coder3:30b
امیدوارم خیلی بهتر از خویشاوندت عمل کنی. در این دور به هیچ مشکلی برخوردم.
شروع خیلی خوبی است.

#### واحد MyMath
***java
public class MyMathTestQwen3_P3 {

    private final MyMath myMath = new MyMath();

    @ParameterizedTest
    @CsvSource({
        "1, positive",
        "100, positive",
        "1000, positive",
        "-1, negative",
        "-100, negative",
        "-1000, negative"
    })
    void checkSign_NormalValues(int number, String expected) {
        assertEquals(expected, myMath.checkSign(number));
    }

    @Test
    void checkSign_ZeroThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 100, 1000, -1, -100, -1000})
    void reverseNumber_NormalValues(int number) {
        assertEquals(-number, myMath.reverseNumber(number));
    }
}
***

##### متد *checkSign(int)*
دو متد تولید شد که شامل موارد زیر است:
- یک تست پارامتری
  - کمی بالاتر از حد و مرز
  - مقادیر مثبت معمولی
  - کمی پایین‌تر از حد و مرز
  - مقادیر منفی معمولی
- ۰ (حد و مرز)

تست‌ها پوشش MC/DC را دارند.  
این در واقع کامل است.

##### متد *reverseNumber(int)*
یک متد با تست پارامتری تولید شده:
- کمی بالاتر از حد و مرز
- مقادیر مثبت معمولی
- کمی پایین‌تر از حد و مرز
- مقادیر منفی معمولی

سه متد تولید شد که شامل موارد زیر است:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- ۰ (در مرز)

تست‌ها پوشش MC/DC را دارند.  
تست صفری که قبلاً تست شده بود وجود ندارد.  
تست پارامتری دست‌کم یک بهبود است.

#### واحد MyAdvancedMath
***java
public class MyAdvancedMathTestQwen3_P3 {

    private final MyAdvancedMath myAdvancedMath = new MyAdvancedMath();
    private final MyMath myMath = new MyMath();

    @ParameterizedTest
    @CsvSource({
        "1, 1",
        "10, 10",
        "100, 100",
        "1000, 1000"
    })
    void add_NormalValues(int x, int y) {
        assertEquals(x + y, myAdvancedMath.add(x, y));
    }

    @Test
    void add_NegativeNumbersThrowException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(5, -1));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, -1));
    }

    @Test
    void add_OverflowThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(Integer.MAX_VALUE, 1));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(1000000, Integer.MAX_VALUE));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 5, 10, 15, 20, 25, 30})
    void powerOfTwo_NormalValues(int power) {
        assertEquals(1 << power, myAdvancedMath.powerOfTwo(power));
    }

    @Test
    void powerOfTwo_ZeroReturnsOne() {
        assertEquals(1, myAdvancedMath.powerOfTwo(0));
    }

    @Test
    void powerOfTwo_NegativePowerThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-10));
    }

    @Test
    void powerOfTwo_PowerAbove30ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
        assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(50));
    }

    @Test
    void reverseArrayNumbers_NormalArray() {
        int[] array = {1, -2, 3, -4};
        int[] expected = {-1, 2, -3, 4};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(array, myMath));
    }

    @Test
    void reverseArrayNumbers_EmptyArray() {
        int[] array = {};
        assertArrayEquals(new int[0], myAdvancedMath.reverseArrayNumbers(array, myMath));
    }

    @Test
    void reverseArrayNumbers_SingleElementArray() {
        int[] array = {5};
        int[] expected = {-5};
        assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(array, myMath));
    }
}
***
متدهای زیادی با چند assertion وجود دارد.

##### متد *add(int, int)*
چهار متد با مجموعاً نه assertion تولید شد:
- یک تست پارامتری برای هر دو مقدار مثبت
- x منفی و y مثبت و برعکس
- x و y هر دو منفی
- دو تست سرریز

MD/DC محقق شده است.  
نسبت به دور قبل بهبودهای جزئی وجود دارد

##### متد *powerOfTwo(int)*
چهار متد با مجموعاً چهارده assertion تولید شد:
- یک مقدار منفی معمولی
- کمی پایین‌تر از حد پایین
- یک تست پارامتری
  - حد پایین
  - کمی بالاتر از حد پایین
  - مقادیر زیادی در بین
  - حد بالا
- کمی بالاتر از حد بالا
- یک عدد مثبت بالاتر از حد و مرز

ما MC/DC را داریم.  
مقادیر حد و مرز تست شده‌اند.  
این یک بهبود است، اما چندان چشمگیر نیست.

##### متد *reverseArrayNumbers(int[], MyMath)*
سه متد تولید شد که شامل موارد زیر است:
- یک تست معمولی
- یک تست با آرایه خالی
- یک تست با آرایه تک‌عضوی

MC/DC محقق شده است.  
یک تست تکراری را حذف کرده. آن را به حساب بهبود می‌گذارم.

#### واحد FileIO
***java
public class FileIOTestQwen3_P3 {

    private final FileIO fileIO = new FileIO();

    @Test
    void readFile_EmptyFileReturnsEmptyArray(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("empty.txt").toFile();
        tempFile.createNewFile();
        
        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void readFile_NormalFileReturnsContent(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("test.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Line 1\nLine 2\nLine 3");
        writer.close();
        
        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals("Line 1", result[0]);
        assertEquals("Line 2", result[1]);
        assertEquals("Line 3", result[2]);
    }

    @Test
    void readFile_FileNotFoundReturnsEmptyArray(@TempDir Path tempDir) {
        String[] result = fileIO.readFile(tempDir.resolve("nonexistent.txt").toString());
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void readFile_WithSpecialCharacters(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("special.txt").toFile();
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Line with spaces\nLine_with_underscores\nLine-with-dashes");
        writer.close();
        
        String[] result = fileIO.readFile(tempFile.getAbsolutePath());
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals("Line with spaces", result[0]);
        assertEquals("Line_with_underscores", result[1]);
        assertEquals("Line-with-dashes", result[2]);
    }
}
***
چهار متد تولید شد که شامل موارد زیر است:

- یک تست معمولی
- یک تست با فایل خالی
- یک فایل ناموجود
- یک فایل با کاراکترهای خاص


- یک فایل با خطوط خالی بین محتوا
- یک فایل تک‌خطی

MC/DC محقق شده است.  
در مقایسه با دور قبل، فایل با خطوط خالی بین محتوا و فایل تک‌خطی حذف شده‌اند، اما فایل با
کاراکترهای خاص جدید است، هر چند خیلی مهم نیست.
پس بدتر از دور قبل عمل کرد.

#### واحد NameSearch
***java
public class NameSearchTestQwen3_P3 {

  private final NameSearch nameSearch = new NameSearch();
  private final FileIO fileIO = new FileIO();

  @Test
  void findNamesInFileThatStartWith_EmptyFileReturnsEmptyArray(@TempDir Path tempDir) throws IOException {
    File tempFile = tempDir.resolve("empty.txt").toFile();
    tempFile.createNewFile();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "John");
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void findNamesInFileThatStartWith_NoMatchesReturnsEmptyArray(@TempDir Path tempDir) throws IOException {
    File tempFile = tempDir.resolve("names.txt").toFile();
    FileWriter writer = new FileWriter(tempFile);
    writer.write("Alice\nBob\nCharlie");
    writer.close();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "John");
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void findNamesInFileThatStartWith_SingleMatch(@TempDir Path tempDir) throws IOException {
    File tempFile = tempDir.resolve("names.txt").toFile();
    FileWriter writer = new FileWriter(tempFile);
    writer.write("Alice\nJohn Smith\nBob\nJohn Doe");
    writer.close();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "John");
    assertNotNull(result);
    assertEquals(2, result.length);
    assertEquals("John Smith", result[0]);
    assertEquals("John Doe", result[1]);
  }

  @Test
  void findNamesInFileThatStartWith_MultipleMatches(@TempDir Path tempDir) throws IOException {
    File tempFile = tempDir.resolve("names.txt").toFile();
    FileWriter writer = new FileWriter(tempFile);
    writer.write("Alice\nJohn Smith\nBob\nJohn Doe\nJohnathan\nCharlie");
    writer.close();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "John");
    assertNotNull(result);
    assertEquals(3, result.length);
    assertEquals("John Smith", result[0]);
    assertEquals("John Doe", result[1]);
    assertEquals("Johnathan", result[2]);
  }

  @Test
  void findNamesInFileThatStartWith_EmptyPrefix(@TempDir Path tempDir) throws IOException {
    File tempFile = tempDir.resolve("names.txt").toFile();
    FileWriter writer = new FileWriter(tempFile);
    writer.write("Alice\nBob\nCharlie");
    writer.close();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempFile.getAbsolutePath(), fileIO, "");
    assertNotNull(result);
    assertEquals(3, result.length);
    assertEquals("Alice", result[0]);
    assertEquals("Bob", result[1]);
    assertEquals("Charlie", result[2]);
  }

  @Test
  void findNamesInFileThatStartWith_EmptyFileName(@TempDir Path tempDir) {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempDir.resolve("nonexistent.txt").toString(), fileIO, "John");
    assertNotNull(result);
    assertEquals(0, result.length);
  }
}
***
شش متد تولید شد که شامل موارد زیر است:
- یک تست با نتیجه تکی
- یک تست با چند نتیجه
- یک تست با فایل خالی
- یک تست بدون نتیجه
- یک تست با پیشوند خالی
- یک تست با مسیر فایل خالی


تست به MC/DC می‌رسد.  
تست آخر تکراری و اشتباه است. باز هم یک نام فایل به TempDir داد.  
در یک متد از چند assertion استفاده شده است.  
عملاً با دور قبل یکسان است.

#### نتیجه‌گیری
مدل خوب عمل کرد، اما بهبود چشمگیری وجود نداشت.
نام متدها با آنچه تست می‌کنند مطابقت دارد، اما با test شروع نمی‌شوند.
در دور قبل هم همین کار را کرد.
از متغیرهای خصوصی استفاده شده است.

### مقایسه مدل‌های محلی
برای متد *checkSign*، Qwen 3 و Gemma4 با تست پارامتری بهترین بودند. بعد Gemma3 کمی
بهتر از GPT OSS و Qwen 2.5 عمل کرد.
برای *reverseNumber*، فقط Qwen 3 از تست پارامتری استفاده کرد، در حالی که gemma4 تنها مدلی بود که Integer.MIN_VALUE را تست کرد.
Qwen 2.5 حتی ۰ را هم تست نکرد!
برای متد *add*، GPT-OSS، Qwen 3 و Gemma4 از تست پارامتری استفاده کردند. Qwen 3 در مجموع بهترین بود.
Gemma3 اینجا بدترین بود.
برای متد *powerOfTwo*، Qwen 3 و Gemma4 یکسان عمل کردند. Gemma3 اینجا بهتر از GPT-OSS و
Qwen 2.5 بود.
در متد *reverseArray*، Qwen 3 و Gemma4 بهترین بودند. بعد GPT-OSS و Gemma3. Qwen 2.5 بدترین بود.
برای متد *readFile*، Qwen 2.5 و Gemma3 آخر هستند، بعد GPT-OSS و Gemma4. البته
Qwen 3 بهترین بود.  
برای واحد *NameSearch*، Qwen 3 برتر بود. Gemma3 بعدی است. Qwen 2.5 بدترین بود.
و به طور کلی Qwen 3 و Gemma4 بهترین بودند. Qwen 2.5 افتضاح بود.
این رتبه‌بندی من است:
1. Qwen 3 Coder 30b
2. Gemma4 e4b
3. Gemma3 12b
4. GPT OSS 20b
5. Qwen 2.5 Coder 7b

تنها تفاوت نسبت به دور قبل این است که Gemma3 بهتر از GPT-OSS عمل کرد.

## مدل‌های ابری در مقابل مدل‌های محلی
من دو مدل ابری کوچک و پنج مدل محلی با تعداد پارامترهای متفاوت را تست کرده‌ام.  
مقایسه این مدل‌ها ایده خوبی است تا از خود بپرسیم آیا استفاده از این
مدل‌های محلی به جای مدل‌های ابری کوچک ارزشش را دارد؟  
هم مدل‌های ابری و هم مدل‌های محلی را در مجموع مقایسه می‌کنم، و هم Gemini 3 Flash و Qwen 3 را چون
در دسته خودشان بهترین بودند.  
با اعلان اول، نزدیک بودند. مدل‌های ابری در واحدهای MyAdvancedMath و FileIO کمی بهتر عمل کردند و در
بقیه واحدها برابر بودند. 
با اعلان دوم، مدل‌های ابری در واحدهای MyMath و MyAdvancedMath کمی بهتر عمل کردند،
در حالی که مدل‌های محلی در NameSearch کمی بهتر بودند.  
با اعلان نهایی، مدل‌های ابری خیلی بهتر از کمی بهتر عمل کردند. این نشان می‌دهد این مدل‌ها می‌توانند
اعلان‌های پیچیده را بهتر مدیریت کنند.  
جالب اینکه در هر سه اعلان، Gemini و Qwen3 یکسان عمل کردند. شاید در یکی از اعلان‌ها
Gemini فقط کمی بهتر بود، اما در مجموع هیچ برنده مشخصی وجود ندارد!
باید ذکر کرد که Qwen3 برای تولید کد کمی زمان می‌برد، به‌خصوص اگر
کامپیوتری که آن را اجرا می‌کند مشخصات خوبی نداشته باشد، اما کاملاً رایگان است و
می‌توانید مطمئن باشید که کدهایتان از شرکت‌های ابری دزدیده نمی‌شوند. با این حال، این مدل‌های آنلاین
ارزان هستند و می‌توانید بدون راه‌اندازی اضافی از آن‌ها در هرجا استفاده کنید.


## نتیجه‌گیری نهایی
این یک ابزار قدرتمند است. کاربران در مقایسه با GitHub Copilot به تنوع بیشتری از
مدل‌ها دسترسی دارند. مدل‌های ابری قطعاً بهتر از مدل‌های محلی عمل می‌کنند. اگر واقعاً
مجبور بودم از یک مدل محلی استفاده کنم، احتمالاً Gemma4 را انتخاب می‌کردم چون پارامتر
زیادی ندارد، اما همچنان بسیار قدرتمند است.
این بار مقایسه منصفانه‌تری بود چون همه مدل‌ها در سه دور یک اعلان یکسان گرفتند. بعضی چیزهای
کلی مثل شروع شدن متد با test باید در اعلان می‌آمد، اما همچنین تعداد زیادی از مدل‌های محلی
فراموش کردند از @TempDir استفاده کنند، در حالی که در اعلان بود. برای هر دور یک فایل
Excel وجود دارد که مدل‌ها را
در یک جدول مقایسه می‌کند.
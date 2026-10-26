# آزمایش با OpenCode در تولید تست واحد
## معرفی OpenCode
OpenCode یک عامل متن‌باز است که به توسعه‌دهندگان اجازه می‌دهد کد را مستقیماً درون ترمینال، IDE یا برنامه دسکتاپ
بنویسند و اجرا کنند. توسط Anomaly Innovations توسعه یافته است.
مشابه Continue.dev، به چندین ارائه‌دهنده مدل دسترسی فراهم می‌کند.
فهرست زیر نمونه‌ای از ارائه‌دهندگان محبوب آن است:
- Anthropic
- GitHub Copilot
- OpenAI
- Google
- OpenRouter
- OpenCode Zen و Go

برای استفاده از یک ارائه‌دهنده، کاربر باید مستقیماً از وب‌سایت ارائه‌دهنده یک کلید API تهیه کند.
این فرایند معمولاً مستلزم خرید اعتبار برای هر سرویس به‌صورت جداگانه است،
که می‌تواند نامتناسب باشد.
با این حال، کاربران این انعطاف را دارند که از ارائه‌دهندگان تجمیعی مانند OpenRouter استفاده کنند.
همچنین می‌تواند یک مدل محلی را با استفاده از Ollama اجرا کند. با این حال، مدل باید از *tools* پشتیبانی کند
پلتفرم همچنین از اجرای مدل‌های محلی از طریق Ollama پشتیبانی می‌کند. نکته حیاتی این است که مدل باید ابزارهای مشخصی
را در قابلیت‌های خود ارائه دهد. این الزام استفاده از Gemma3 را با این ابزار ناممکن کرد.
کاربران می‌توانند از دستور زیر برای اجرای OpenCode در ترمینال با مدل‌های محلی استفاده کنند:
```bash
ollama launch opencode --config  
```
این دستور از کاربر می‌خواهد مدلی را انتخاب کند.  
آزمایش عامل در ترمینال نشان داد که محیط کاملاً کاربردی است.
برنامه ترمینال یک رابط کاربری ابتدایی دارد که با مجموعه‌ای از دکمه‌ها یک منوی فرمان باز می‌کند.
این منو کارهایی مانند اشتراک‌گذاری یک نشست، جابه‌جایی میان نشست‌ها یا انتخاب یک مدل را ممکن می‌سازد.
همچنین تاریخچه‌ای از فرمان‌های اخیر را نمایش می‌دهد و فهرستی از پیشنهادها برای انتخاب مدل ارائه می‌شود.
اصلی‌ترین ایراد کاربردپذیری، ناتوانی در جمع کردن (collapse) ارائه‌دهندگان است آنگاه که یک ارائه‌دهنده صدها مدل دارد؛ که نیازمند اسکرول بسیار طولانی است.

### نحوه نصب OpenCode CLI
نصب را می‌توان به چند روش انجام داد. من از Node.js با دستور زیر استفاده کردم:
```bash
npm i -g opencode-ai
```
پس از نصب، می‌توانید با اجرای دستور زیر نصب را تأیید کنید:
```bash
opencode --version
```

### نحوه استفاده از OpenCode
برای شروع کار با OpenCode، یک ترمینال در پوشه پروژه خود باز کنید و دستور زیر را اجرا کنید
```bash
opencode
```
این کار OpenCode را در ترمینال اجرا می‌کند.

#### حالت
در OpenCode دو حالت وجود دارد.
با میان‌بر *tab* می‌توانید بین آن‌ها جابه‌جا شوید:
- Build (اصلی)
- Plan


##### حالت Build
این حالت، عامل اصلی و بدون محدودیت AI است که وظایف کدنویسی شما را به‌طور فعال اجرا می‌کند.
دسترسی کامل برای خواندن، نوشتن، ویرایش فایل‌ها و اجرای فرمان‌های سیستمی دارد؛
که آن را به حالتی تبدیل می‌کند که برای پیاده‌سازی واقعی ویژگی‌ها و ایجاد تغییرات استفاده می‌شود.

##### حالت Plan
این حالت یک محیط محدود و فقط‌خواندنی است که برای تحلیل، تعیین دامنه و برنامه‌ریزی معماری
توسط AI طراحی شده، بدون آنکه خطر تغییرات ناخواسته در پایگاه کد شما وجود داشته باشد.
ابزارهای مخرب را محدود می‌کند در حالی که زمینه را بارگذاری می‌کند
تا بتوانید ویژگی‌ها را با ایمنی طرح‌ریزی کنید.

#### انتخاب مدل‌ها
کاربران ابتدا باید یک کلید API از ارائه‌دهنده مشخص تهیه کنند؛ این فرایند با انتخاب ارائه‌دهنده
و سپس وارد کردن اطلاعات احراز هویت API آغاز می‌شود.
سپس OpenCode اتصال موفق با ارائه‌دهنده را تأیید می‌کند.
همچنین می‌توانید مدل‌های محلی را در مسیر زیر پیکربندی کنید:  
"(user)\.config\opencode\opencode.jsonc"  
اما برای اعمال تغییرات در فایل json، باید OpenCode را دوباره راه‌اندازی کنید.

#### پیوند زمینه
کاربران می‌توانند با تایپ @ و سپس مسیر نسبی فایل، فایل یا تصویر را به چت‌بات AI پیوست کنند.

پس از نوشتن پرامپت خود، می‌توانید روی دکمه enter کلیک کنید
و مدل کار خود را آغاز می‌کند.
دو ویژگی قابل‌توجه OpenCode توانایی نمایش زمان صرف‌شده برای تولید خروجی
و میزان پنجره زمینه استفاده‌شده
را نشان می‌دهد.

## تولید تست‌های واحد با OpenCode
هدف این بار استفاده از دو مدل محلی GPT-OSS:20b و Gemma4:e4b با همان پرامپت Continue.dev است
تا ببینیم اگر از ابزارهای متفاوتی استفاده کنیم چقدر نتایج تغییر می‌کند. هرچند حتی با همان ابزارها هم
احتمالاً نتایج متفاوتی می‌گیریم، چون آن‌ها هوش مصنوعی هستند. آنچه هوش مصنوعی را از الگوریتم متمایز می‌کند
این است که خروجی AI ممکن است همیشه برای ورودی یکسان یکسان نباشد.
بنابراین اینجا نخستین پرامپتی است که در Continue.dev استفاده کردم:

## دور ۱
بگذارید با یک پرامپت ساده شروع کنیم. برای جلوگیری از mock، آن را صریحاً در پرامپت می‌آورم.
> برای این واحدها، با استفاده از JUnit5 تست واحد تولید کن. برای وابستگی‌ها از mock استفاده نکن. کلاس‌های
> تست تولیدشده باید به "Test(ModelName)_P1" ختم شوند

برای پسوند، اگر نام مدل gemma4 باشد، نام کلاس تست پسوند "TestGemma4_P1" خواهد داشت.

### GPT-OSS:20b
پس از ۱۹ دقیقه و ۴۱ ثانیه تولید همه فایل‌های تست را کامل کرد!
در Continue.dev این کار ۱۰ دقیقه و ۲۳ ثانیه طول کشید!
این عامل توانست تست‌ها را در پوشه درست بنویسد.

#### واحد MyMath
```java
class MyMathTestGPTOSS_P1 {

  @Test
  void testCheckSignPositive() {
    MyMath mm = new MyMath();
    assertEquals("positive", mm.checkSign(5));
  }

  @Test
  void testCheckSignNegative() {
    MyMath mm = new MyMath();
    assertEquals("negative", mm.checkSign(-3));
  }

  @Test
  void testCheckSignZero() {
    MyMath mm = new MyMath();
    assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
  }

  @Test
  void testReverseNumber() {
    MyMath mm = new MyMath();
    assertEquals(-5, mm.reverseNumber(5));
    assertEquals(0, mm.reverseNumber(0));
    assertEquals(3, mm.reverseNumber(-3));
  }
}
```

##### متد *checkSign(int)*
سه متد تولید شد که شامل می‌شوند:
- یک عدد مثبت عادی
- یک عدد منفی عادی
- صفر (مرز)

تست‌ها MC/DC دارند.  
این با خروجی تولیدشده توسط Continue.dev مطابقت دارد.

##### متد *reverseNumber(int)*
یک متد واحد با سه assertion تولید شد:
- یک عدد مثبت عادی
- یک عدد منفی عادی
- صفر (در مرز)

تست‌ها MC/DC دارند.  
تفاوت اصلی با Continue.dev این است که این رویکرد چند assertion را در یک متد واحد ادغام می‌کند.

#### واحد MyAdvancedMath
```java
class MyAdvancedMathTestGPTOSS_P1 {

  @Test
  void testAddValid() {
    MyAdvancedMath ma = new MyAdvancedMath();
    assertEquals(12, ma.add(5, 7));
  }

  @Test
  void testAddNegative() {
    MyAdvancedMath ma = new MyAdvancedMath();
    assertThrows(IllegalArgumentException.class, () -> ma.add(-1, 5));
    assertThrows(IllegalArgumentException.class, () -> ma.add(5, -3));
  }

  @Test
  void testAddOverflow() {
    MyAdvancedMath ma = new MyAdvancedMath();
    int x = Integer.MAX_VALUE - 10;
    int y = 20; // 20 > 10 so will overflow
    assertThrows(IllegalArgumentException.class, () -> ma.add(x, y));
  }

  @Test
  void testPowerOfTwoZero() {
    MyAdvancedMath ma = new MyAdvancedMath();
    assertEquals(1, ma.powerOfTwo(0));
  }

  @Test
  void testPowerOfTwoPositive() {
    MyAdvancedMath ma = new MyAdvancedMath();
    assertEquals(32, ma.powerOfTwo(5));
  }

  @Test
  void testPowerOfTwoOutOfBounds() {
    MyAdvancedMath ma = new MyAdvancedMath();
    assertThrows(IllegalArgumentException.class, () -> ma.powerOfTwo(-1));
    assertThrows(IllegalArgumentException.class, () -> ma.powerOfTwo(31));
  }

  @Test
  void testReverseArrayNumbers() {
    MyAdvancedMath ma = new MyAdvancedMath();
    MyMath mm = new MyMath();
    int[] input = new int[]{1, -2, 0, 3};
    int[] expected = new int[]{-1, 2, 0, -3};
    int[] actual = ma.reverseArrayNumbers(input.clone(), mm);
    assertArrayEquals(expected, actual);
  }
}
```
##### متد *add(int, int)*
سه متد تولید شد که شامل می‌شوند:
- x و y مثبت
- x مثبت و y منفی و برعکس
- تست سرریز

تست‌ها MC/DC دارند.  
این با تست‌های Continue.dev یکسان است.

##### متد *powerOfTwo(int)*
سه متد تولید شد. تست‌ها شامل می‌شوند:
- درست زیر مرز پایین
- مرز پایین
- یک تست عادی
- درست بالای مرز بالا

MC/DC داریم.  
Continue.dev دو تست عادی تولید کرد، در حالی که اینجا فقط یکی تولید شد.
این بهتر است، زیرا داشتن دو تست عادی به معنای افزونگی است.

##### متد *reverseArrayNumbers(int[], MyMath)*
فقط یک متد برای یک تست عادی تولید شد.  
این همان چیزی است که Continue.dev پیش‌تر تولید کرده بود.

#### واحد FileIO
```java
class FileIOTestGPTOSS_P1 {

  private File tempFile;

  @AfterEach
  void cleanUp() {
    if (tempFile != null && tempFile.exists()) {
      tempFile.delete();
    }
  }

  private void writeLines(File file, String... lines) throws IOException {
    try (FileWriter fw = new FileWriter(file)) {
      for (String line : lines) {
        fw.write(line + System.lineSeparator());
      }
    }
  }

  @Test
  void testReadFile() throws Exception {
    tempFile = File.createTempFile("fileio_test", ".txt");
    writeLines(tempFile, "first", "second", "third");

    FileIO fio = new FileIO();
    String[] result = fio.readFile(tempFile.getAbsolutePath());

    assertEquals(3, result.length);
    assertArrayEquals(new String[]{"first", "second", "third"}, result);
  }
}
```
یک متد تست واحد با یک تست عادی تولید شد.

به استثنا برخورد نمی‌کنیم، بنابراین پوشش دستور (statement coverage) نداریم.
Continue.dev حالت مرزی فایل ناموجود را پوشش داد.

#### واحد NameSearch
```java
class NameSearchTestGPTOSS_P1 {

  private File tempFile;

  @AfterEach
  void cleanUp() {
    if (tempFile != null && tempFile.exists()) {
      tempFile.delete();
    }
  }

  private void writeLines(File file, String... lines) throws Exception {
    try (FileWriter fw = new FileWriter(file)) {
      for (String line : lines) {
        fw.write(line + System.lineSeparator());
      }
    }
  }

  @Test
  void testFindNamesInFileThatStartWith() throws Exception {
    tempFile = File.createTempFile("names", ".txt");
    writeLines(tempFile, "Alice", "Alfred", "Bob", "Charlie", "Alfreda");

    FileIO fileIO = new FileIO();
    NameSearch ns = new NameSearch();
    String[] result = ns.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "Al");

    assertArrayEquals(new String[]{"Alice", "Alfred", "Alfreda"}, result);
  }

  @Test
  void testFindNamesInFileThatStartWithNoMatch() throws Exception {
    tempFile = File.createTempFile("names", ".txt");
    writeLines(tempFile, "Alice", "Bob", "Charlie");

    FileIO fileIO = new FileIO();
    NameSearch ns = new NameSearch();
    String[] result = ns.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), fileIO, "Z");

    assertArrayEquals(new String[0], result);
  }
}
```
دو متد تست تولید شد که شامل می‌شوند:
- یک تست عادی
- یک تست بدون نتیجه.

تست به MC/DC دست می‌یابد.  
این معادل کد تولیدشده توسط Continue.dev است.

#### مقایسه
در مقایسه با Continue.dev چند تفاوت جزئی وجود دارد:
- نام متدها با آنچه آزمایش می‌کنند مطابقت دارد و با test شروع نمی‌شوند.
در دفعه قبل با Continue.dev این‌طور نبود.
- متدهای بیشتری چند assertion دارند.
- از متغیرهای private برای واحدها استفاده نکرد، در حالی که Continue.dev چنین کرد.
- Continue.dev حالت مرزی فایل ناموجود را برای FileIO پوشش داد.
- OpenCode یک تست افزونه کمتر تولید کرد.
- OpenCode ۹ دقیقه و ۱۷ ثانیه بیشتر برای تولید وقت گرفت!
تقریباً دو برابر Continue!

در مجموع، تفاوت‌های اساسی‌ای وجود ندارد.

### Gemma4:e4b
عامل همه تست‌های واحد را در یک نوبت تولید کرد. در دفعه قبل با چند مشکل مواجه شدم.
تولید تست‌ها ۵ دقیقه و ۲۴ ثانیه طول کشید.
تست‌ها در پوشه درست نوشته شدند.

#### واحد MyMath
```java
public class MyMathTestGemma4_P1 {

  private final MyMath myMath = new MyMath();

  @Test
  void testCheckSignPositive() {
    assertEquals("positive", myMath.checkSign(10));
  }

  @Test
  void testCheckSignNegative() {
    assertEquals("negative", myMath.checkSign(-5));
  }

  @Test
  void testCheckSignZero() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      myMath.checkSign(0);
    });
    assertEquals("Number had no sign", thrown.getMessage());
  }

  @Test
  void testReverseNumberPositive() {
    assertEquals(-1234, myMath.reverseNumber(1234));
  }

  @Test
  void testReverseNumberNegative() {
    assertEquals(500, myMath.reverseNumber(-500));
  }

  @Test
  void testReverseNumberZero() {
    assertEquals(0, myMath.reverseNumber(0));
  }
}
```
از متغیرهای private استفاده شده است. هرچند هنگام تعریف مقداردهی می‌شوند.
پیش‌تر از یک متد با annotation ‎@BeforeEach برای مقداردهی اولیه آن‌ها استفاده می‌شد.

##### متد *checkSign(int)*
سه متد تولید شد که شامل می‌شوند:
- یک عدد مثبت عادی
- یک عدد منفی عادی
- صفر (مرز)

تست‌ها MC/DC دارند.
همچنین پیام استثنا را نیز assert کرده که غیرضروری است.  
این معادل تست‌های تولیدشده با Continue.dev است.

##### متد *reverseNumber(int)*
سه متد تولید شد که شامل می‌شوند:
- یک عدد مثبت عادی
- یک عدد منفی عادی
- صفر (در مرز)

تست‌ها MC/DC دارند.  
این بار هم از اعداد بزرگی مانند ۱۲۳۴ و -۵۰۰ استفاده کرد.  
این بسیار شبیه دفعه قبل است.

#### واحد MyAdvancedMath
```java
public class MyAdvancedMathTestGemma4_P1 {

  private final MyAdvancedMath myAdvancedMath = new MyAdvancedMath();

  @Test
  void testAddSuccessfulAddition() {
    assertEquals(15, myAdvancedMath.add(10, 5));
  }

  @Test
  void testAddWithNegativeInputX() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      myAdvancedMath.add(-1, 5);
    });
    assertEquals("Input numbers should be positive.", thrown.getMessage());
  }

  @Test
  void testAddWithNegativeInputY() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      myAdvancedMath.add(10, -5);
    });
    assertEquals("Input numbers should be positive.", thrown.getMessage());
  }

  @Test
  void testAddOverflow() {
    // Max Int value - 10 will still fit. Max Int + 1 will overflow.
    int x = Integer.MAX_VALUE - 1;
    int y = 2;
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      myAdvancedMath.add(x, y);
    });
    assertEquals("Cannot fit the result in an Integer.", thrown.getMessage());
  }

  @Test
  void testPowerOfTwoZero() {
    assertEquals(1, myAdvancedMath.powerOfTwo(0));
  }

  @Test
  void testPowerOfTwoSmall() {
    assertEquals(2, myAdvancedMath.powerOfTwo(1));
    assertEquals(16, myAdvancedMath.powerOfTwo(4));
    assertEquals(1024, myAdvancedMath.powerOfTwo(10));
  }

  @Test
  void testPowerOfTwoMax() {
    // 2^30
    assertEquals(1073741824, myAdvancedMath.powerOfTwo(30));
  }

  @Test
  void testPowerOfTwoNegativePower() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      myAdvancedMath.powerOfTwo(-1);
    });
    assertEquals("power should be 0<=power<=30", thrown.getMessage());
  }

  @Test
  void testPowerOfTwoTooLargePower() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      myAdvancedMath.powerOfTwo(31);
    });
    assertEquals("power should be 0<=power<=30", thrown.getMessage());
  }

  @Test
  void testReverseArrayNumbers() {
    // The MyMath instance used here does not use mocks as per requirement
    MyMath myMath = new MyMath();
    int[] input = {1, 2, 3};
    int[] expected = {-1, -2, -3};

    int[] result = myAdvancedMath.reverseArrayNumbers(input, myMath);

    for (int i = 0; i < 3; i++) {
      assertEquals(expected[i], result[i], "Element at index " + i + " mismatch");
    }
  }
}
```
##### متد *add(int, int)*
چهار متد تولید شد که شامل می‌شوند:
- x و y مثبت
- x مثبت و y منفی و برعکس
- تست سرریز

تست‌ها MC/DC دارند.  
تست‌ها همان تست‌های Continue.dev هستند، اما یک تست بهتر برای سرریز تولید شده
و متدی با چند assertion وجود ندارد.

##### متد *powerOfTwo(int)*
پنج متد تولید شد. شامل می‌شوند:
- درست زیر مرز پایین
- مرز پایین
- درست بالای مرز پایین
- دو تست عادی
- مرز بالا
- درست بالای مرز بالا

MC/DC داریم.  
در واقع دو بهبود وجود دارد!
*درست بالای مرز پایین* و *مرز بالا* در دفعه قبل تست نشده بودند.
در هر دو بار، مدل‌ها دو تست عادی تولید کردند.

##### متد *reverseArrayNumbers(int[], MyMath)*
فقط یک متد برای یک تست عادی تولید شد، همان‌طور که Continue.dev انجام داد.
MC/DC محقق شد.

#### واحد FileIO
جالب اینکه ‎@BeforeEach در دو واحد باقی‌مانده استفاده شده است.
این به‌احتمال زیاد به‌دلیل مقداردهی اولیه فایل موقت است.
```java
public class FileIOTestGemma4_P1 {

  private FileIO fileIO;
  private File tempFile;

  @BeforeEach
  void setUp() throws IOException {
    fileIO = new FileIO();
    // Create a temporary file for testing
    tempFile = File.createTempFile("testfile", ".txt");
  }

  @Test
  void testReadFileSuccess() throws IOException {
    String content = "Line 1\nLine 2\nLine 3";
    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write(content);
    }

    // Testing the behavior of the public method
    String[] lines = fileIO.readFile(tempFile.getAbsolutePath());

    assertNotNull(lines);
    assertEquals(3, lines.length);
    assertEquals("Line 1", lines[0]);
    assertEquals("Line 2", lines[1]);
    assertEquals("Line 3", lines[2]);
  }

  @Test
  void testReadFileEmptyFile() throws IOException {
    // Create an empty file
    tempFile.createNewFile();

    // The method uses BufferedReader and readLine(), which correctly handles an empty file resulting in an empty array
    String[] lines = fileIO.readFile(tempFile.getAbsolutePath());

    assertNotNull(lines);
    assertEquals(0, lines.length);
  }

  @Test
  void testReadFileNonExistentFile() {
    // Use a path that definitely does not exist
    String nonExistentPath = "non_existent_file_" + System.currentTimeMillis() + ".txt";

    // The original implementation catches IOException and prints stack trace, 
    // but still returns an empty list via stream().toArray().
    String[] lines = fileIO.readFile(nonExistentPath);

    assertNotNull(lines);
    assertTrue(lines.length == 0);
  }
}
```
سه متد تست تولید شد که شامل می‌شوند:
- یک تست عادی
- تست فایل خالی
- یک فایل ناموجود

MC/DC محقق شد.
از نظر تست، تفاوتی با Continue.dev وجود ندارد؛ با این حال این بار تست‌ها به‌صورت باینری در فایل‌های موقت
نمی‌نویسند
و همچنین متد ‎@AfterEach برای پاک‌سازی فایل موقت وجود ندارد.

#### واحد NameSearch
```java
public class NameSearchTestGemma4_P1 {

  private NameSearch nameSearch;
  private File tempFile;

  @BeforeEach
  void setUp() throws IOException {
    nameSearch = new NameSearch();
    // Create a temporary file for testing
    tempFile = File.createTempFile("" +
            "testnames", ".txt");
  }

  @Test
  void testFindNamesInFileWhenNamesStartWithPrefix() throws IOException {
    String content = "apple\napricot\nbanana\napply\n";
    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write(content);
    }

    // Dependency injection requires FileIO. We mock file creation for testing, 
    // but since dependencies cannot be mocked, we use a TestFileIO implementation 
    // or assume a controlled environment. For this controlled test, we use a helper approach
    // to handle the file dependency if FileIO needs *actual* file operations.
    // Since we cannot strictly mock FileIO in a dependency-free way within this scope,
    // we will pass a file object that the concrete FileIO handles.

    // For clean testing, we must ensure the FileIO used here is robust.
    // The internal implementation of FileIO, when reading from this tempFile, should work.

    // Running the test with the dependency injection
    String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), new dmst.seip.angor.junitdemo.io.FileIO(), "ap");

    assertNotNull(result);
    assertEquals(3, result.length);
    assertArrayEquals(new String[]{"apple", "apricot", "apply"}, result);
  }

  @Test
  void testFindNamesInFileWithNoMatches() throws IOException {
    String content = "banana\norange\norange";
    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write(content);
    }

    String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), new dmst.seip.angor.junitdemo.io.FileIO(), "z");

    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void testFindNamesInFileWithEmptyContent() throws IOException {
    // Create an empty file
    tempFile.createNewFile();

    String[] result = nameSearch.findNamesInFileThatStartWith(tempFile.getAbsolutePath(), new dmst.seip.angor.junitdemo.io.FileIO(), "a");

    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void testFindNamesInFileWithNonExistFile() {
    // Test failure case for FileIO dependency
    String nonExistentPath = "non_existent_file_" + System.currentTimeMillis() + ".txt";

    // The FileIO implementation catches IOException and returns an empty array.
    String[] result = nameSearch.findNamesInFileThatStartWith(nonExistentPath, new dmst.seip.angor.junitdemo.io.FileIO(), "test");

    assertNotNull(result);
    assertEquals(0, result.length);
  }
}
```
چهار متد تست تولید شد که شامل می‌شوند:
- یک تست عادی
- یک تست بدون نتیجه
- تست فایل خالی
- تست فایل ناموجود

تست به MC/DC دست می‌یابد.  
در مقایسه با کدی که Continue.dev تولید کرد، تست فایل ناموجود جدید است،
اما این هم غیرضروری است، زیرا این FileIO است
که باید فایل ناموجود را مدیریت کند.

#### مقایسه
تفاوت‌های جزئی‌ای وجود داشت:
- تست بیشتر روی مقادیر مرزی برای متد *powerOfTwo*
- یک تست غیرضروری برای واحد *NameSearch*.
- چند تفاوت دیگر که برای هر متد ذکر کردم.
- OpenCode ۱ دقیقه و ۳۴ ثانیه بیشتر از Continue.dev برای تولید وقت گرفت.

در مجموع، تفاوت اساسی‌ای وجود ندارد.

## دور ۲
حالا آن را دوباره با پرامپتی پیچیده‌تر که به Continue.dev داده بودم اجرا می‌کنیم.
همچنین از این پس اندازه‌گیری نکردم که Continue.dev چقدر برای تولید کد زمان می‌برد،
اما به‌وضوح OpenCode بسیار طولانی‌تر می‌گیرد، به‌ویژه با GPT-OSS.
> برای این واحدها، با استفاده از JUnit5 تست واحد تولید کن. برای وابستگی‌ها از mock استفاده نکن.
> روی مقادیر مرزی تمرکز کن و آن‌ها را با تست‌های عادی جایگزین کن (مثلاً اگر ۰ یک مرز است،
> مقدار -۱، ۰ و ۱ را تست کن). سعی کن در هر متد فقط از یک assertion استفاده کنی. از ‎@TempDir
> برای ذخیره‌سازی موقت استفاده کن. مطمئن شو که حالت‌های مرزی زیادی مانند آرایه‌ها یا رشته‌های خالی را تست می‌کنی. کلاس‌های
> تست تولیدشده باید به "Test(ModelName)_P2" ختم شوند.

### GPT-OSS:20b
این بار ۲۱ دقیقه و ۴۵ ثانیه برای تولید خروجی طول کشید.
#### واحد MyMath
```java
class MyMathTestGPTOSS_P2 {

  private final MyMath mm = new MyMath();

  @Test
  void checkSign_positive() {
    assertEquals("positive", mm.checkSign(1));
  }

  @Test
  void checkSign_negative() {
    assertEquals("negative", mm.checkSign(-1));
  }

  @Test
  void checkSign_zeroThrows() {
    assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
  }

  @Test
  void reverseNumber_positive() {
    assertEquals(-5, mm.reverseNumber(5));
  }

  @Test
  void reverseNumber_negative() {
    assertEquals(5, mm.reverseNumber(-5));
  }
}
```

##### متد *checkSign(int)*
سه متد تولید شد که شامل می‌شوند:
- درست بالای مرز
- درست زیر مرز
- صفر (مرز)

تست‌ها MC/DC دارند.  
این با خروجی تولیدشده توسط Continue.dev مطابقت دارد.

##### متد *reverseNumber(int)*
سه متد تولید شد که شامل می‌شوند:
- یک مثبت عادی
- یک منفی عادی
- صفر (در مرز)

تست‌ها MC/DC دارند.  
Continue.dev بهتر عمل کرد، زیرا به‌جای مقادیر عادی، مقادیر مرزی را تست کرد.

#### واحد MyAdvancedMath
```java
class MyAdvancedMathTestGPTOSS_P2 {

  private final MyAdvancedMath math = new MyAdvancedMath();

  @Test
  void add_positiveNumbers() {
    assertEquals(5, math.add(2,3));
  }

  @Test
  void add_negativeInputThrows() {
    assertThrows(IllegalArgumentException.class, () -> math.add(-1,5));
  }

  @Test
  void add_overflowThrows() {
    int big = Integer.MAX_VALUE - 1;
    assertThrows(IllegalArgumentException.class, () -> math.add(big,2));
  }

  @Test
  void powerOfTwo_zero() {
    assertEquals(1, math.powerOfTwo(0));
  }

  @Test
  void powerOfTwo_maxBoundary() {
    assertEquals(1073741824, math.powerOfTwo(30)); // 2^30
  }

  @Test
  void powerOfTwo_negativeThrows() {
    assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(-1));
  }

  @Test
  void powerOfTwo_overflowThrows() {
    assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(31));
  }

  @Test
  void reverseArrayNumbers_empty() {
    int[] input = new int[]{};
    int[] result = math.reverseArrayNumbers(input, new MyMath());
    assertArrayEquals(new int[]{}, result);
  }

  @Test
  void reverseArrayNumbers_normal() {
    int[] input = new int[]{-1,0,5};
    int[] result = math.reverseArrayNumbers(input, new MyMath());
    assertArrayEquals(new int[]{1,0,-5}, result);
  }
}
```
##### متد *add(int, int)*
سه متد تولید شد که شامل می‌شوند:
- x و y مثبت
- y برابر -۱ و x مثبت است
- تست سرریز

از آنجا که x هرگز منفی نیست، پوشش تصمیم داریم.
این‌ها نیز همان تست‌هایی هستند که Continue.dev تولید کرد.

##### متد *powerOfTwo(int)*
چهار متد تولید شد. تست‌ها شامل می‌شوند:
- درست زیر مرز پایین
- مرز پایین
- مرز بالا
- درست بالای مرز بالا

MC/DC محقق شد.  
Continue.dev علاوه بر این، درست بالای مرز پایین را هم پوشش داد.

##### متد *reverseArrayNumbers(int[], MyMath)*
دو متد تولید شد که شامل می‌شوند:
- تست آرایه خالی
- یک تست عادی

MC/DC محقق شد.  
این‌ها معادل تست‌های Continue.dev هستند.

#### واحد FileIO
```java
class FileIOTestGPTOSS_P2 {

  private final FileIO fileIO = new FileIO();

  @Test
  void readFile_emptyFile(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("empty.txt");
    Files.createFile(file);
    assertArrayEquals(new String[]{}, fileIO.readFile(file.toString()));
  }

  @Test
  void readFile_multipleLines(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("lines.txt");
    Files.write(file, java.util.List.of("one", "two", "three"));
    assertArrayEquals(new String[]{"one","two","three"}, fileIO.readFile(file.toString()));
  }
}
```
دو متد تولید شد که شامل می‌شوند:
- یک فایل خالی
- یک فایل با چند خط

حالت تست فایل ناموجود پوشش داده نشده و در نتیجه پوشش دستوری وجود ندارد.  
Continue.dev حالت تست فایل تک‌خطی را هم پوشش داده بود.

#### واحد NameSearch
```java
class NameSearchTestGPTOSS_P2 {

  private final NameSearch ns = new NameSearch();
  private final FileIO fileIO = new FileIO();

  @Test
  void findNamesInFileThatStartWith_match() throws IOException {
    @TempDir Path tempDir = java.nio.file.Files.createTempDirectory("names");
    Path file = tempDir.resolve("names.txt");
    Files.write(file, java.util.List.of("alice","bob","alex","carol"));
    String[] result = ns.findNamesInFileThatStartWith(file.toString(), fileIO, "al");
    assertArrayEquals(new String[]{"alice","alex"}, result);
  }

  @Test
  void findNamesInFileThatStartWith_noMatch() throws IOException {
    @TempDir Path tempDir = java.nio.file.Files.createTempDirectory("names");
    Path file = tempDir.resolve("names.txt");
    Files.write(file, java.util.List.of("bob","carol"));
    String[] result = ns.findNamesInFileThatStartWith(file.toString(), fileIO, "al");
    assertArrayEquals(new String[]{}, result);
  }
}
```
دو متد با چند assertion تولید شد که شامل می‌شوند:
- یک تست بدون نتیجه.
- یک تست با چند نتیجه

تست به MC/DC دست می‌یابد.  
Continue.dev یک تست با نتیجه واحد و یک تست فایل خالی داشت.

#### مقایسه
حالا تفاوت‌ها بیشترند. Continue.dev بسیار بهتر عمل کرد:
- OpenCode از ذخیره‌سازی موقت JUnit استفاده کرد
- Continue.dev در پوشش حالت‌های مرزی و مقادیر مرزی بهتر عمل کرد
- Continue.dev ۴ مورد بیشتر را پوشش داد.


### Gemma4:e4b
خوشبختانه برخلاف دفعه قبل با هیچ مشکلی مواجه نشدم.
تولید کدها ۸ دقیقه و ۴۰ ثانیه طول کشید.

#### واحد MyMath
```java
public class MyMathTestGemma4_P2 {

  MyMath myMath;

  @BeforeEach
  void setUp() {
    myMath = new MyMath();
  }

  @Test
  void testCheckSignPositive() {
    String result = myMath.checkSign(1);
    assertEquals("positive", result);
  }

  @Test
  void testCheckSignNegative() {
    String result = myMath.checkSign(-1);
    assertEquals("negative", result);
  }

  @Test
  void testCheckSignBoundary_Positive() {
    String result = myMath.checkSign(Integer.MAX_VALUE);
    assertEquals("positive", result);
  }

  @Test
  void testCheckSignBoundary_Negative() {
    String result = myMath.checkSign(Integer.MIN_VALUE);
    assertEquals("negative", result);
  }

  @Test
  void testCheckSignBoundary_Zero() {
    assertThrows(IllegalArgumentException.class, () -> {
      myMath.checkSign(0);
    }, "Expected IllegalArgumentException for number zero");
  }

  @Test
  void testReverseNumberPositive() {
    int result = myMath.reverseNumber(5);
    assertEquals(-5, result);
  }

  @Test
  void testReverseNumberNegative() {
    int result = myMath.reverseNumber(-5);
    assertEquals(5, result);
  }

  @Test
  void testReverseNumberZero() {
    int result = myMath.reverseNumber(0);
    assertEquals(0, result);
  }
}
```

##### متد *checkSign(int)*
پنج متد تولید شد که شامل می‌شوند:
- درست زیر مرز
- صفر (مرز)
- درست بالای مرز
- Integer.MAX_VALUE
- Integer.MIN_VALUE

تست‌ها MC/DC دارند.  
این با خروجی تولیدشده توسط Continue.dev مطابقت دارد.

##### متد *reverseNumber(int)*
سه متد تولید شد که شامل می‌شوند:
- یک عدد مثبت عادی
- یک عدد منفی عادی
- صفر (در مرز)

تست‌ها MC/DC دارند.  
بدترین عملکرد را داشت. برخلاف Continue.dev حالت مرزی Integer.MIN_VALUE را پوشش نداد.
همچنین Continue.dev یک تست درست بالای مرز داشت.

#### واحد MyAdvancedMath
```java
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
```
##### متد *add(int, int)*
چهار متد تولید شد که شامل می‌شوند:
- دو تست با x و y مثبت
- x منفی و y مثبت است
- تست سرریز

y هرگز منفی نیست، بنابراین پوشش شرط وجود دارد.
یک تست عادی اضافه و افزونه تولید شد، اما Continue.dev هم
یک تست افزونه تولید کرد که اینجا وجود ندارد.
Continue.dev منفی بودن هر دو x و y را تست کرد در حالی که OpenCode فقط منفی بودن x را تست کرد.
Continue.dev همچنین یک تست نزدیک به سرریز داشت. اعداد تست‌شده بین آن‌ها
بسیار متفاوت بودند.  
Continue.dev در اینجا عملکرد به‌مراتب بهتری در پوشش و عمق نشان داد.

##### متد *powerOfTwo(int)*
پنج متد تولید شد. تست‌ها شامل می‌شوند:
- درست زیر مرز پایین
- مرز پایین
- درست بالای مرز پایین
- مرز بالا
- درست بالای مرز بالا

MC/DC داریم.  
Continue.dev دو تست عادی تولید کرد در حالی که OpenCode هیچ‌کدام تولید نکرد.
از آنجا که در حال بررسی مقادیر مرزی هستیم، لزوماً نیازی به تست
مقادیر عادی نداریم.

##### متد *reverseArrayNumbers(int[], MyMath)*
سه متد تولید شد. شامل می‌شوند:
- یک تست عادی
- تست آرایه تک‌عضوی
- یک آرایه خالی

MC/DC محقق شد.
OpenCode بهتر عمل کرد، چون تست آرایه تک‌عضوی دارد و تست افزونه ندارد.

#### واحد FileIO
```java
public class FileIOTestGemma4_P2 {

  FileIO fileIO;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    fileIO = new FileIO();
  }

  @Test
  void testReadFileNormalContent() throws IOException {
    // A. Setup a temporary file with multiple lines
    Path filePath = tempDir.resolve("test_multi.txt");
    String content = "Line One\nLine Two\nLast Line";
    Files.writeString(filePath, content);

    // B. Execute the test
    String[] result = fileIO.readFile(filePath.toString());

    // C. Assert
    assertEquals(3, result.length);
    assertArrayEquals(new String[]{"Line One", "Line Two", "Last Line"}, result);
  }

  @Test
  void testReadFileEmptyFile() throws IOException {
    // A. Setup a temporary empty file
    Path filePath = tempDir.resolve("empty.txt");
    Files.createFile(filePath);

    // B. Execute the test
    String[] result = fileIO.readFile(filePath.toString());

    // C. Assert
    assertEquals(0, result.length);
  }

  @Test
  void testReadFileSingleLine() throws IOException {
    // A. Setup a temporary file with one line
    Path filePath = tempDir.resolve("single.txt");
    Files.writeString(filePath, "Only One Line");

    // B. Execute the test
    String[] result = fileIO.readFile(filePath.toString());

    // C. Assert
    assertEquals(1, result.length);
    assertArrayEquals(new String[]{"Only One Line"}, result);
  }

  @Test
  void testReadFileNonExistingFile() {
    // The file does not exist, expecting the content reading to fail gracefully (via exception catching or specific return value, if possible).
    // Since the original method relies on e.printStackTrace() on IOException, we test that it doesn't crash and returns empty array.
    String nonExistentPath = tempDir.resolve("does_not_exist.txt").toString();

    String[] result = fileIO.readFile(nonExistentPath);

    // Asserting that it returns an empty array in case of failure/exception
    assertEquals(0, result.length);
  }

  @Test
  void testReadFileWhitespaceOnlyFile() throws IOException {
    // A. Setup a temporary file with only whitespace
    Path filePath = tempDir.resolve("whitespace.txt");
    Files.writeString(filePath, "\n  \t\n");

    // B. Execute the test
    String[] result = fileIO.readFile(filePath.toString());

    // C. Assert (readLine() handles empty lines, so we expect several lines containing whitespace)
    // Note: Because readLine() treats the first line as the content and subsequent empty lines will be null, 
    // and the buffer reader reads until null, we check if the structure is preserved.
    assertTrue(result.length > 0);
  }
}
```
پنج متد تولید شد که شامل می‌شوند:
- یک تست عادی
- تست فایل تک‌خطی
- تست فایل خالی
- یک فایل ناموجود
- تست فایل فقط فاصله‌دار

MC/DC محقق شد.  
OpenCode علاوه بر آن *تست فایل تک‌خطی* و *تست فایل فقط فاصله‌دار* را هم تولید کرد.

#### واحد NameSearch
```java
public class NameSearchTestGemma4_P2 {

  NameSearch nameSearch;
  FileIO fileIO;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    nameSearch = new NameSearch();
    fileIO = new FileIO(); // Initialize FileIO for use in the test
  }

  @Test
  void testFindNamesStartWithNormal() throws IOException {
    // Setup: File content
    Path filePath = tempDir.resolve("names.txt");
    String content = "Apple\nBanana\nApricot\nGrape";
    Files.writeString(filePath, content);

    // Execute
    String[] results = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "Ap");

    // Assert
    assertEquals(2, results.length);
    assertArrayEquals(new String[]{"Apple", "Apricot"}, results);
  }

  @Test
  void testFindNamesStartWithAllMatches() throws IOException {
    // Setup: File content
    Path filePath = tempDir.resolve("all_names.txt");
    String content = "Cat\nCar\nCoat";
    Files.writeString(filePath, content);

    // Execute
    String[] results = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "C");

    // Assert
    assertEquals(3, results.length);
    assertArrayEquals(new String[]{"Cat", "Car", "Coat"}, results);
  }

  @Test
  void testFindNamesStartWithPrefixNonExistent() throws IOException {
    // Setup: File content
    Path filePath = tempDir.resolve("names.txt");
    String content = "Apple\nBanana\nApricot\nGrape";
    Files.writeString(filePath, content);

    // Execute
    String[] results = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "XYZ");

    // Assert
    assertEquals(0, results.length);
    assertArrayEquals(new String[0], results);
  }

  @Test
  void testFindNamesWithEmptyPrefix() throws IOException {
    // Setup: File content
    Path filePath = tempDir.resolve("names.txt");
    String content = "Apple\nBanana\nApricot";
    Files.writeString(filePath, content);

    // Execute: Empty prefix should match all lines
    String[] results = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, "");

    // Assert
    assertEquals(3, results.length);
    assertArrayEquals(new String[]{"Apple", "Banana", "Apricot"}, results);
  }

  @Test
  void testFindNamesInEmptyFile() {
    // Setup: Create an empty file
    Path emptyFilePath = tempDir.resolve("empty.txt");
    try {
      Files.createFile(emptyFilePath);
    } catch (IOException e) {
      // Ignore cleanup errors for testing structure
    }

    // Execute
    // We need to handle the fact that fileIO.readFile might fail/read nothing on a non-existent/empty file
    // Based on FileIO, it reads contents, so an empty file *is* read as an array of size 0.
    String[] results = nameSearch.findNamesInFileThatStartWith(emptyFilePath.toString(), fileIO, "A");

    // Assert
    assertEquals(0, results.length);
    assertArrayEquals(new String[0], results);
  }
}
```
پنج متد تولید شد که شامل می‌شوند:
- یک تست عادی
- تست همه‌تطابق
- یک تست بدون نتیجه
- تست فایل خالی
- تست با پیشوند خالی

تست به MC/DC دست می‌یابد.  
در مقایسه با Continue.dev، OpenCode یک تست افزونه «همه‌تطابق» تولید کرد.
این به‌طور ضمنی در تست پیشوند خالی تست شده است! و همچنین لازم نیست.


#### مقایسه
تفاوت‌ها بیشتر می‌شوند. با پرامپتی پیچیده‌تر، متغیرهای بیشتری وجود دارند
که خروجی AI را بیشتر متغیر می‌کنند:
- Continue.dev پوشش برتری از مقادیر مرزی نشان داد.
- OpenCode در پوشش حالت‌های مرزی بهتر عمل کرد.

## دور ۳
این دور پایانی است. بیایید برای آخرین بار دوباره آن را اجرا کنیم.
> برای این واحدها، با استفاده از JUnit5 تست واحد تولید کن. برای وابستگی‌ها از mock استفاده نکن.
> مطمئن شو تست مقادیر مرزی محکمی داری (خود مرز، درست بالای آن
> و درست زیر آن را تست کن). سعی کن در هر متد فقط از یک assertion استفاده کنی. از ‎@TempDir
> برای ذخیره‌سازی موقت استفاده کن. مطمئن شو حالت‌های مرزی زیادی مانند آرایه‌ها یا رشته‌های خالی را تست می‌کنی.
> پوشش MC/DC را محقق کن. برای مقادیر عادی از تست‌های پارامتری استفاده کن. از تولید تست‌های افزونه
> پرهیز کن. کلاس‌های تست تولیدشده باید به "Test(modelName)_P3" ختم شوند.


### GPT-OSS:20b
۲۳ دقیقه و ۵۳ ثانیه برای تولید خروجی طول کشید.

#### واحد MyMath
```java
class MyMathTestGPTOSS_P3 {

  private final MyMath myMath = new MyMath();

  @ParameterizedTest
  @ValueSource(ints = {1, 42, Integer.MAX_VALUE})
  void checkSign_withPositiveNumbers_returnsPositive(int number) {
    assertEquals("positive", myMath.checkSign(number));
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, -42, Integer.MIN_VALUE})
  void checkSign_withNegativeNumbers_returnsNegative(int number) {
    assertEquals("negative", myMath.checkSign(number));
  }

  @Test
  void checkSign_withZero_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, -1, 0, Integer.MAX_VALUE, Integer.MIN_VALUE})
  void reverseNumber_returnsOppositeSign(int number) {
    assertEquals(-number, myMath.reverseNumber(number));
  }
}
```
##### متد *checkSign(int)*
سه متد تولید شد که دو مورد آن‌ها پارامتری است. شامل می‌شوند:
- یک مثبت عادی
- یک منفی عادی
- درست بالای مرز
- درست زیر مرز
- صفر (مرز)

تست‌ها MC/DC دارند.  
این تست‌های تولیدشده در مقایسه با Continue.dev بسیار بهتر هستند.
از تست‌های پارامتری استفاده شده و مقادیر مرزی بررسی شده‌اند.

##### متد *reverseNumber(int)*
یک متد واحد تولید شد. این متد پارامتری است و شامل می‌شود:
- درست بالای مرز
- درست زیر مرز
- صفر (در مرز)
- حالت مرزی Integer.MIN
- Integer.MAX

تست‌ها MC/DC دارند.  
برخلاف Continue.dev، حالت مرزی Integer.MIN_VALUE موجود است.  
همچنین از تست پارامتری استفاده شده. Integer.MAX یک تست افزونه است.

#### واحد MyAdvancedMath
```java
class MyAdvancedMathTestGPTOSS_P3 {

  private final MyAdvancedMath math = new MyAdvancedMath();
  private final MyMath mm = new MyMath();

  @ParameterizedTest
  @ValueSource(ints = {0, 1, Integer.MAX_VALUE / 2})
  void add_withPositiveInputs_returnsSum(int x) {
    int y = x;
    assertEquals(x + y, math.add(x, y));
  }

  @Test
  void add_withNegativeInput_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> math.add(-1, 5));
  }

  @Test
  void add_overflow_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> math.add(Integer.MAX_VALUE, 1));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 10, 30})
  void powerOfTwo_validValues_returnsCorrect(int power) {
    int expected = (int)Math.pow(2, power);
    assertEquals(expected, math.powerOfTwo(power));
  }

  @Test
  void powerOfTwo_boundaryBelowZero_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(-1));
  }

  @Test
  void powerOfTwo_boundaryAbove30_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(31));
  }

  @Test
  void powerOfTwo_exactBoundaryZero_returnsOne() {
    assertEquals(1, math.powerOfTwo(0));
  }

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE})
  void reverseArrayNumbers_withVariousArrays_reversesSigns(int input) {
    int[] arr = new int[]{input};
    int[] result = math.reverseArrayNumbers(arr, mm);
    assertEquals(-input, result[0]);
  }
}
```
##### متد *add(int, int)*
سه متد تولید شد که شامل می‌شوند:
- یک تست پارامتری برای هر دو ورودی نامنفی (شامل هر دو صفر)
- x منفی و y مثبت است
- تست سرریز

مانند Continue.dev، MC/DC وجود ندارد زیرا y هرگز منفی نیست.  
«هر دو صفر» یک تست جدید است، و نیز «هر دو برابر (Integer.MAX_VALUE / 2)».

##### متد *powerOfTwo(int)*
چهار متد تولید شد که شامل می‌شوند:
- یک تست پارامتری معتبر
  - مرز پایین
  - درست بالای مرز
  - یک مقدار عادی
  - درست زیر مرز بالا
- درست زیر مرز پایین
- مرز بالا
- مرز پایین (تکراری)

MC/DC داریم.  
OpenCode علاوه بر آن، مرز بالا را هم در تست پارامتری خود پوشش داد.

##### متد *reverseArrayNumbers(int[], MyMath)*
دو متد تولید شد که شامل می‌شوند:
- تست آرایه خالی
- یک تست عادی

MC/DC محقق شد.  
OpenCode برای این متد از تست پارامتری با یک آرایه واحد استفاده کرد، که کاملاً نامعقول است.
Continue.dev دست‌کم حالت تست آرایه خالی را پوشش داده بود.

#### واحد FileIO
```java
class FileIOTestGPTOSS_P3 {

  private final FileIO io = new FileIO();

  @TempDir
  File tempDir;

  @Test
  void readFile_withEmptyFile_returnsEmptyArray() throws IOException {
    File file = new File(tempDir, "empty.txt");
    file.createNewFile();
    assertNotNull(io.readFile(file.getAbsolutePath()));
    assertEquals(0, io.readFile(file.getAbsolutePath()).length);
  }

  @Test
  void readFile_withMultipleLines_returnsArrayWithLines() throws IOException {
    File file = new File(tempDir, "lines.txt");
    try (var writer = new FileWriter(file)) {
      writer.write("first\nsecond\nthird");
    }
    String[] result = io.readFile(file.getAbsolutePath());
    assertArrayEquals(new String[]{"first", "second", "third"}, result);
  }

  @Test
  void readFile_withNonExistentFile_returnsEmptyArray() {
    // The method does not throw on non-existent but returns empty array because exception is caught
    String[] result = io.readFile("nonexistent.file");
    assertNotNull(result);
  }
}
```
سه متد تولید شد که شامل می‌شوند:
- یک فایل خالی
- یک فایل ناموجود
- یک فایل با چند خط

MC/DC محقق شد.
Continue.dev تست فایل تک‌خطی را پوشش داده بود در حالی که OpenCode تست فایل ناموجود را پوشش داد.

#### واحد NameSearch
```java
class NameSearchTestGPTOSS_P3 {

  private final NameSearch ns = new NameSearch();
  private final FileIO io = new FileIO();

  @TempDir
  File tempDir;

  @Test
  void findNamesInFileThatStartWith_noMatches_returnsEmptyArray() throws IOException {
    File file = new File(tempDir, "names.txt");
    try (var w = new FileWriter(file)) {
      w.write("alice\nbob\ncarl");
    }
    String[] result = ns.findNamesInFileThatStartWith(file.getAbsolutePath(), io, "z");
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void findNamesInFileThatStartWith_multipleMatches_returnsArray() throws IOException {
    File file = new File(tempDir, "names.txt");
    try (var w = new FileWriter(file)) {
      w.write("charlie\ncharles\nalice\ncharles");
    }
    String[] result = ns.findNamesInFileThatStartWith(file.getAbsolutePath(), io, "char");
    assertArrayEquals(new String[]{"charlie", "charles", "charles"}, result);
  }

  @Test
  void findNamesInFileThatStartWith_emptyFile_returnsEmptyArray() throws IOException {
    File file = new File(tempDir, "empty.txt");
    file.createNewFile();
    String[] result = ns.findNamesInFileThatStartWith(file.getAbsolutePath(), io, "a");
    assertEquals(0, result.length);
  }
}
```
چهار متد با چند assertion تولید شد که شامل می‌شوند:
- یک تست بدون نتیجه.
- یک تست با فایل خالی
- یک تست با چند نتیجه

تست به MC/DC دست می‌یابد.  
Continue.dev یک تست با نتیجه واحد داشت در حالی که OpenCode
یک تست فایل خالی تولید کرد.


#### مقایسه
حالا تفاوت‌ها آشکارترند:
- OpenCode استفاده برتر از تست‌های پارامتری نشان داد که به پوشش بهتر مقادیر مرزی منجر شد.
- Continue.dev یک تست بیشتر برای حالت‌های مرزی نسبت به OpenCode داشت.

در مجموع، OpenCode اینجا بهتر عمل کرد. بیش از اندازه کم.


### Gemma4:e4b
متأسفانه با این مدل به مشکل برخوردم. مدل دو بار در تولید متوقف شد و مجبور شدم به آن پرامپت بدهم تا ادامه دهد.
هر سه پرامپت در مجموع ۱۰ دقیقه و ۴۵ ثانیه طول کشید.

#### واحد MyMath
یک خطای نحوی وجود دارد. دور اولین منبع CSV پرانتز وجود ندارد.
```java
public class MyMathTestGemma4_P3 {

  private final MyMath myMath = new MyMath();

  // --- Tests for checkSign(int number) ---

  @ParameterizedTest(name = "checkSign({0}) should return {1}")
  @CsvSource({
          "1, 'positive'",
          ("Integer.MAX_VALUE", "positive"), // Boundary check
          ("Integer.MAX_VALUE - 1", "positive"),
          ("-1", "negative"),
          ("Integer.MIN_VALUE", "negative"), // Boundary check
          ("Integer.MIN_VALUE + 1", "negative")
  })
  void checkSign_PositiveOrNegative(int number, String expected) {
    assertEquals(expected, myMath.checkSign(number));
  }
  
  @ParameterizedTest(name = "checkSign({0}) should throw IllegalArgumentException")
  @CsvSource({"0"})
  void checkSign_ThrowsExceptionForZero(int zero) {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      myMath.checkSign(zero);
    });
    assertEquals("Number had no sign", exception.getMessage());
  }
  
  // --- Tests for reverseNumber(int number) ---
  
  @ParameterizedTest(name = "reverseNumber({0}) should return {1}")
  @CsvSource({
          ("123", -123),
          ("-456", 456),
          ("0", 0)
          })
  void reverseNumber_BasicScenarios(int input, int expected) {
    assertEquals(expected, myMath.reverseNumber(input));
  }
  
  @Test
  void reverseNumber_IntegerRangeBoundaries() {
    // Testing max and min values for safety, although standard int arithmetic handles it.
    // The implementation simply negates the number.
    assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MAX_VALUE));
    assertEquals(Integer.MAX_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
  }
}
```

##### متد *checkSign(int)*
دو متد تولید شد که شامل می‌شوند:
- یک تست پارامتری ترکیبی
  - درست بالای مرز
  - Integer.MAX_VALUE
  - Integer.MAX_VALUE - 1
  - درست زیر مرز
  - Integer.MIN_VALUE
  - Integer.MIN_VALUE -1
- یک تست پارامتری که فقط شامل یک صفر است؟!

- تست پارامتری مقادیر مثبت
    - یک مثبت عادی
    - درست بالای مرز
- تست پارامتری مقادیر منفی
    - یک منفی عادی
    - درست زیر مرز
- یک تست برای ۰


تست‌ها MC/DC دارند.  
Continue.dev تست‌های ایده‌آل تولید کرد، اما OpenCode دو اشتباه کرد.
نخست، تست پارامتری را با پاس دادن نتیجه هم مخلوط کرد.
دوم، یک تست پارامتری با تنها یک مقدار ساخت!
همچنین، تمام تست‌های مقادیر حدی افزونه هستند.

##### متد *reverseNumber(int)*
دو متد تولید شد که شامل می‌شوند:
- یک تست پارامتری ترکیبی
  - یک مثبت عادی
  - یک منفی عادی
  - صفر
- تست پارامتری مقادیر حدی
  - Integer.MIN_VALUE
  - Integer.MAX_VALUE

مدل اشتباهی احمقانه مرتکب شد. در مقادیر حدی، متوجه نشد که مقادیر MAX و MIN
معکوس یکدیگر نیستند!
Continue.dev از تست پارامتری استفاده نکرد، اما تست مقدار MIN را درست تولید کرد.


#### واحد MyAdvancedMath
```java
public class MyAdvancedMathTestGemma4_P3 {
    private MyMath myMathInstance;

    @BeforeEach
    void setUp() {
        myMathInstance = new MyMath();
    }

    // --- Tests for add(int x, int y) ---

    @ParameterizedTest(name = "add({0}, {1}) should return {2}")
    @CsvSource({
            ("1", "2", "3"), // Small positive numbers
            ("10000", "20000", "30000"),
            ("Integer.MAX_VALUE - 10", "20", "Integer.MAX_VALUE - 8"),
            ("1", "Integer.MIN_VALUE + 1", "Integer.MIN_VALUE + 2") // Test wrap around boundaries
    })
    void add_PositiveInputs(int x, int y, int expected) {
        assertEquals(expected, myAdvancedMath.add(x, y));
    }

    @Test
    void add_NegativeInputShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.add(-1, 1);
        });
        assertEquals("Input numbers should be positive.", exception.getMessage());
    }

    @Test
    void add_OverflowShouldThrowException() {
        // Test case: x > MAX_VALUE - y
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            // Use MAX_VALUE / 2 + 1 and MAX_VALUE / 2 - 1 to exceed MAX_VALUE
            myAdvancedMath.add(Integer.MAX_VALUE - 1, 2);
        });
        assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
    }

    // --- Tests for powerOfTwo(int power) ---

    @ParameterizedTest(name = "powerOfTwo({0}) should return {1}")
    @CsvSource({
            ("0", "1"),      // Boundary case: 2^0
            ("1", "2"),      // Smallest power
            ("10", "1024"),  // Normal value
            ("30", 1073741824) // Boundary case: 2^30
    })
    void powerOfTwo_ValidPowers(int power, int expected) {
        assertEquals(expected, myAdvancedMath.powerOfTwo(power));
    }

    @Test
    void powerOfTwo_NegativePowerShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(-1);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    @Test
    void powerOfTwo_PowerGreaterThan30ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            myAdvancedMath.powerOfTwo(31);
        });
        assertEquals("power should be 0<=power<=30", exception.getMessage());
    }

    // --- Tests for reverseArrayNumbers(int[] array, MyMath mm) ---

    @Test
    void reverseArrayNumbers_NormalCase() {
        int[] array = {1, 2, 3};
        int[] expected = {-1, -2, -3};
        // Use MyMath instance that was set up in @BeforeEach
        MyMath advancedMath = new MyAdvancedMath(); 
        
        int[] actual = advancedMath.reverseArrayNumbers(array, myMathInstance);
        assertArrayEquals(expected, actual);
    }

    @Test
    void reverseArrayNumbers_MixedSignCase() {
        int[] array = {10, -20, 30};
        int[] expected = {-10, 20, -30};
        MyAdvancedMath advancedMath = new MyAdvancedMath();
        
        int[] actual = advancedMath.reverseArrayNumbers(array, myMathInstance);
        assertArrayEquals(expected, actual);
    }
    
    @Test
    void reverseArrayNumbers_EmptyArrayCase() {
        int[] array = {};
        int[] expected = {};
        MyAdvancedMath advancedMath = new MyAdvancedMath();

        int[] actual = advancedMath.reverseArrayNumbers(array, myMathInstance);
        assertArrayEquals(expected, actual);
    }
}
```
##### متد *add(int, int)*
سه متد تولید شد که شامل می‌شوند:
- یک تست پارامتری با هر دو ورودی مثبت عادی
- تست x منفی و y مثبت
- یک تست سرریز

مقدار سوم در تست پارامتری باعث استثنای سرریز می‌شود!  
MC/DC محقق شد.  
Continue.dev بهتر عمل کرد زیرا MC/DC دارد. این مدل واقعاً علاقه‌مند است که به‌جای نوع استثنا،
پیام استثنا را assert کند.

##### متد *powerOfTwo(int)*
سه متد تولید شد که شامل می‌شوند:
- یک تست پارامتری عادی:
  - مرز پایین
  - درست بالای مرز
  - یک مقدار عادی
  - مرز بالا
- درست زیر مرز پایین
- درست بالای مرز بالا

تست‌های تولیدشده توسط Continue.dev و OpenCode برای این متد معادل هستند.

##### متد *reverseArrayNumbers(int[], MyMath)*
سه متد تولید شد. شامل می‌شوند:
- تست همه مثبت
- یک تست ترکیبی
- یک آرایه خالی

MC/DC محقق شد.  
Continue.dev یک تست «همه صفر» داشت و OpenCode یک تست «همه مثبت» دارد. هر دو می‌توانند افزونه باشند.

#### واحد FileIO
```java
public class FileIO_TestGemma4_P3 {

  private FileIO fileIO;

  @BeforeEach
  void setUp() {
    fileIO = new FileIO();
  }

  /**
   * Reads a file with standard content and verifies content retrieval.
   */
  @Test
  void readFile_StandardContent(@TempDir Path tempDir) throws IOException {
    Path filePath = tempDir.resolve("test_file.txt");
    String content = "First line\nSecond line\nThird line";

    // Write content to the temporary file
    Files.write(filePath, content.getBytes());

    String[] expectedArray = {"First line", "Second line", "Third line"};
    String[] actualArray = fileIO.readFile(filePath.toString());

    assertArrayEquals(expectedArray, actualArray);
  }

  /**
   * Reads an empty file, verifying an empty array is returned.
   */
  @Test
  void readFile_EmptyFile(@TempDir Path tempDir) throws IOException {
    Path filePath = tempDir.resolve("empty_file.txt");
    // Create an empty file
    Files.write(filePath, new byte[0]);

    String[] expectedArray = new String[0];
    String[] actualArray = fileIO.readFile(filePath.toString());

    assertArrayEquals(expectedArray, actualArray);
  }

  /**
   * Reads a file containing boundary/edge case lines (e.g., empty line).
   */
  @Test
  void readFile_EdgeCaseLines(@TempDir Path tempDir) throws IOException {
    Path filePath = tempDir.resolve("edge_case_file.txt");
    // Line 1: Good content, Line 2: Empty line, Line 3: Only spaces
    String content = "Name A\n\n   \nName B";

    // Write content to the temporary file
    Files.write(filePath, content.getBytes());

    // BufferedReader reads the empty line as a single empty string element
    String[] expectedArray = {"Name A", "", "   ", "Name B"};
    String[] actualArray = fileIO.readFile(filePath.toString());

    assertArrayEquals(expectedArray, actualArray);
  }

  /**
   * Tests reading a file that does not exist, verifying exception handling does not crash the test.
   */
  @Test
  void readFile_FileNotFound(@TempDir Path tempDir) {
    // Use a Path that definitely does not exist in the temp directory
    Path nonExistentFile = tempDir.resolve("non_existent.txt");

    // The method catches IOException and prints the stack trace but returns an array.
    // We primarily test that it does not throw a runtime exception.
    assertDoesNotThrow(() -> {
      String[] actualArray = fileIO.readFile(nonExistentFile.toString());
      // When failure occurs, it returns an empty array of strings
      assertTrue(actualArray.length == 0);
    });
  }
}
```
چهار متد تولید شد که شامل می‌شوند:
- یک تست عادی
- تست فایل خالی
- یک فایل ناموجود
- تست فایلی که در میان محتوایش خط خالی دارد

MC/DC محقق شد.  
Continue.dev تست تک‌خطی و تست فقط خط خالی داشت، در حالی که OpenCode فایلی با خط خالی در میان محتوا دارد.
بنابراین Continue.dev یک حالت مرزی بیشتر از OpenCode پوشش داد.

#### واحد NameSearch
```java
public class NameSearchTestGemma4_P3 {

  private NameSearch nameSearch;

  // Note: Since NameSearch depends on FileIO, and FileIO relies on the actual file system,
  // we will mock the FileIO behavior using @TempDir to simulate successful file reads.

  @BeforeEach
  void setUp() {
    nameSearch = new NameSearch();
  }

  /**
   * Test case for finding names starting with a specific prefix in a well-formed file.
   */
  @Test
  void findNamesInFileThatStartWith_FoundNames(@TempDir Path tempDir) throws IOException {
    Path filePath = tempDir.resolve("names_file.txt");
    String content = "Alice Smith\nBob Johnson\nAlex Brown\nAndrew Taylor";
    Files.write(filePath, content.getBytes());

    // We use the FileIO class we tested previously, assuming a successful read operation.
    FileIO fileIO = new FileIO();

    String prefix = "A";
    String[] expectedArray = {"Alice Smith", "Alex Brown", "Andrew Taylor"};

    String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, prefix);

    assertArrayEquals(expectedArray, actualArray);
  }

  /**
   * Test case for finding no names matching the prefix.
   */
  @Test
  void findNamesInFileThatStartWith_NoMatches(@TempDir Path tempDir) throws IOException {
    Path filePath = tempDir.resolve("names_file_2.txt");
    String content = "Alpha\nBeta\nGamma";
    Files.write(filePath, content.getBytes());

    FileIO fileIO = new FileIO();

    String prefix = "X";
    String[] expectedArray = {};

    String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, prefix);

    assertArrayEquals(expectedArray, actualArray);
  }

  /**
   * Test case for an empty file, simulating zero names.
   */
  @Test
  void findNamesInFileThatStartWith_EmptyFile(@TempDir Path tempDir) throws IOException {
    Path filePath = tempDir.resolve("empty_names_file.txt");
    Files.write(filePath, new byte[0]);

    FileIO fileIO = new FileIO();

    String prefix = "N/A";
    String[] expectedArray = {};

    String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, prefix);

    assertArrayEquals(expectedArray, actualArray);
  }

  /**
   * Test case to check when the name list only contains names that match the prefix.
   */
  @Test
  void findNamesInFileThatStartWith_AllMatch(@TempDir Path tempDir) throws IOException {
    Path filePath = tempDir.resolve("all_names_file.txt");
    String content = "apple\napricot\napple pie";
    Files.write(filePath, content.getBytes());

    FileIO fileIO = new FileIO();

    String prefix = "app";
    String[] expectedArray = {"apple", "apricot", "apple pie"};

    String[] actualArray = nameSearch.findNamesInFileThatStartWith(filePath.toString(), fileIO, prefix);

    assertArrayEquals(expectedArray, actualArray);
  }
}
```
دفعه قبل، مدل با این متد بسیار بد عمل کرد.  
چهار متد تولید شد که شامل می‌شوند:
- یک تست عادی
- یک تست بدون نتیجه
- تست فایل خالی
- تست تطابق کامل

تست به MC/DC دست می‌یابد.  
در مقایسه با Continue، تست تطابق کامل اضافه شده، اما به باور من واقعاً مهم نیست.


#### مقایسه
Continue.dev در همه متدها MC/DC دارد، اما OpenCode ندارد.  
در مجموع، Continue.dev کمی بهتر عمل کرد.


## نتیجه‌گیری نهایی
فرض می‌کنم دلیل طولانی‌تر بودن تولید تست‌های OpenCode نسبت به Continue.dev،
استفاده از ابزارهای بیشتر توسط OpenCode است.
می‌بینیم که مدل‌ها در نوشتن بهترند و این امر را باید به ابزارهای OpenCode نسبت داد.
این تفاوت چشمگیر بود.  
در پرامپت اول تفاوت‌های جزئی بود، اما با پرامپت پیچیده‌تر این تفاوت‌ها بزرگ‌تر شدند.
در برخی متدها تفاوت‌های اساسی بود، اما برای مقایسه کلی از «تفاوت اساسی» استفاده نمی‌کنم.
شاید این‌طور نباشد برای پروژه‌های بعدی که پیچیده‌تر از این خواهند بود.
فایل‌های Excel که تست‌های تولیدشده توسط Continue.dev و OpenCode را مقایسه می‌کنند
ارائه شده‌اند.





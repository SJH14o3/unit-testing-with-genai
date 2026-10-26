# آزمایش با OpenCode در تولید تست واحد با بهره‌گیری از Skills
## معرفی Skills
Agent Skills یک قالب باز و سبک برای گسترش توانایی‌های عامل‌های هوش مصنوعی
با دانش تخصصی و گردش‌کارهای ویژه است. در هستهٔ خود،
هر skill یک پوشه است که فایل SKILL.md را در بر می‌گیرد.
این فایل شامل فراداده‌ها (حداقل name و description) و
دستورالعمل‌هایی است که به یک عامل می‌گوید چگونه یک کار مشخص را انجام دهد.  


### نحوهٔ نصب Skills
OpenCode و بسیاری از عامل‌های دیگر از skills پشتیبانی می‌کنند. نخست باید
skillهایی را که قصد استفاده از آن‌ها را دارید پیدا کنید. مخازن زیادی در GitHub
وجود دارد که skillهای گوناگونی دارند. با این حال، من از این وب‌سایت استفاده کردم تا یافتن skillها آسان‌تر باشد:  
https://agentskillsforall.com/  
این وب‌سایت رایگان هم هست.  
پس از آنکه skillای را که می‌خواهید در این وب‌سایت به کار ببرید یافتید، آن را نصب می‌کنید.
این وب‌سایت دستوری ارائه می‌دهد که می‌توانید آن را روی CLI اجرا کنید تا ابزار نصب شود.
پس از اجرای دستور، از شما خواسته می‌شود نصب را سفارشی‌سازی کنید. 
از شما پرسیده می‌شود که می‌خواهید این skill با چه ابزارهای کمک‌کد سازگار باشد.
به‌طور پیش‌فرض، OpenCode انتخاب شده بود. پس از آن، از شما پرسیده می‌شود که آیا skill را
برای یک پروژهٔ مشخص نصب کنید یا به‌صورت سراسری. اگر می‌خواهید آن را برای یک
پروژهٔ مشخص نصب کنید، باید دستور را در پوشهٔ پروژه اجرا کنید.  
همچنین می‌توانید skillها را به‌صورت دستی نصب کنید؛ کافی است پوشهٔ skill را از 
مخزن دانلود کرده و آن را در پوشهٔ skills درست کپی کنید. البته باید یک فایل json را
پیکربندی کنید که کار زیادی نیست. من نتوانستم skill را از طریق دستور نصب کنم،
بنابراین دستی نصبش کردم.  


### نحوهٔ به‌کارگیری یک skill با OpenCode
اگر چند skill نصب کرده باشید، OpenCode به‌طور خودکار تشخیص می‌دهد که آیا برای
prompt (دستور) شما skill مفیدی وجود دارد یا نه! برای نمونه، اگر از یک هوش مصنوعی بخواهم تست واحد
بندویسد، در میان skillهای موجود جست‌وجو می‌کند و بررسی می‌کند که آیا skill مناسبی برای
تست واحد وجود دارد یا نه. با این حال، می‌توانید به‌طور صریح هم از یک skill استفاده کنید؛
کافی است از عامل بخواهید که از یک skill مشخص استفاده کند. 

## تولید تست‌های واحد با OpenCode و skills
دو skill مربوط به تست واحد با JUnit پیدا کردم. لینک‌های آن‌ها اینجاست:  
https://agentskillsforall.com/skills/java-junit (junit-5-skill)  
https://agentskillsforall.com/skills/junit-skill (java-junit)  

این برنامهٔ من برای آزمایش skillها است:  
من skillها را در ۳ دور با یک prompt بسیار ساده امتحان خواهم کرد:
> برای این واحدها تست واحد تولید کن. وابستگی‌ها را mock نکن.

از آنجا که هر skill یک چارچوب قوی برای تست واحد فراهم کرده است، ساده‌ترین prompt را
می‌دهم. با این حال، چون هر دو skill راهنمایی برای mock دارند، اضافه کردم که وابستگی‌ها
mock نشوند.  
در دور اول، از مدل محلی GPT-OSS با اولین skill استفاده می‌کنم که پیچیده‌تر به نظر می‌رسد.
هدف، مقایسهٔ آن با زمانی است که تست‌های واحد را بدون استفاده از skill تولید کردم.  
در دور دوم، از "MiMo V2.5 (medium variant)" با skill دوم استفاده می‌کنم.  
در دور آخر، از "DeepSeek V4 Flash (default variant)" با هر دو skill استفاده می‌کنم و آن را با
دور قبلی مقایسه می‌کنم تا ببینم اگر چند skill مشابه استفاده شود چه اتفاقی می‌افتد.

## دور ۱: GPT-OSS با "junit-5-skill"
به یاد داشته باشید که هدف، مقایسهٔ تست‌های تولیدشده با تست‌هایی است که از skill استفاده نکردند:
> برای این واحدها تست واحد تولید کن. کلاس‌های تست تولیدشده باید با "TestGPTOSS_SP" پایان یابند.
> از skill "junit-5-skill" استفاده کن. وابستگی‌ها را mock نکن.

پس از ۲۷ دقیقه و ۳۵ ثانیه تولید همهٔ فایل‌های تست به پایان رسید!
بدون skill، این کار ۱۹ دقیقه و ۴۱ ثانیه طول کشید. بیشتر طول کشید چون باید
همهٔ context (زمینه) مربوط به skill را می‌خواند.

#### واحد MyMath
```java
class MyMathTestGPTOSS_SP {

  private MyMath myMath;

  @BeforeEach
  void setUp() {
    myMath = new MyMath();
  }

  @Test
  @DisplayName("checkSign returns positive for positive numbers")
  void checkSignPositive() {
    assertEquals("positive", myMath.checkSign(5));
  }

  @Test
  @DisplayName("checkSign returns negative for negative numbers")
  void checkSignNegative() {
    assertEquals("negative", myMath.checkSign(-3));
  }

  @Test
  @DisplayName("checkSign throws IllegalArgumentException for zero")
  void checkSignZeroThrows() {
    assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
  }

  @Test
  @DisplayName("reverseNumber returns negative of input")
  void reverseNumberPositive() {
    assertEquals(-10, myMath.reverseNumber(10));
  }

  @Test
  @DisplayName("reverseNumber returns positive of input")
  void reverseNumberNegative() {
    assertEquals(15, myMath.reverseNumber(-15));
  }
}
```
##### متد *checkSign(int)*
سه متد تولید شده‌اند که شامل می‌شوند:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- صفر (مرز)

تست‌ها MC/DC دارند.  
این با خروجی تولیدشده بدون skill مطابقت دارد.

##### متد *reverseNumber(int)*
سه متد تکی تولید شده‌اند که شامل می‌شوند:
- یک عدد مثبت معمولی
- یک عدد منفی معمولی
- صفر (در مرز)

تست‌ها MC/DC دارند.  
این معادل تولید بدون skill است. 

#### واحد MyAdvancedMath
```java
class MyAdvancedMathTestGPTOSS_SP {

  private MyAdvancedMath myAdvancedMath;

  @BeforeEach
  void setUp() {
    myAdvancedMath = new MyAdvancedMath();
  }

  @Test
  @DisplayName("add positive numbers returns sum")
  void addPositive() {
    assertEquals(15, myAdvancedMath.add(7, 8));
  }

  @ParameterizedTest(name = "add({0}, {1}) -> {2}")
  @CsvSource("5,3,8", "10,20,30", "0,0,0")
  void addWithCsv(int x, int y, int expected) {
    assertEquals(expected, myAdvancedMath.add(x, y));
  }

  @Test
  @DisplayName("add throws for negative input")
  void addNegativeThrows() {
    assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 5));
    assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(4, -2));
  }

  @Test
  @DisplayName("add throws for integer overflow")
  void addOverflowThrows() {
    assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(Integer.MAX_VALUE, 1));
  }

  @Test
  @DisplayName("powerOfTwo 0 returns 1")
  void powerZero() {
    assertEquals(1, myAdvancedMath.powerOfTwo(0));
  }

  @Test
  @DisplayName("powerOfTwo 5 returns 32")
  void powerFive() {
    assertEquals(32, myAdvancedMath.powerOfTwo(5));
  }

  @ParameterizedTest(name = "powerOfTwo({0}) -> {1}")
  @CsvSource("1,2", "2,4", "3,8", "30,1073741824")
  void powerCsv(int power, int expected) {
    assertEquals(expected, myAdvancedMath.powerOfTwo(power));
  }

  @Test
  @DisplayName("powerOfTwo throws for negative power")
  void powerNegativeThrows() {
    assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1));
  }

  @Test
  @DisplayName("powerOfTwo throws for power > 30")
  void powerTooLargeThrows() {
    assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
  }

  @Test
  @DisplayName("reverseArrayNumbers reverses signs of each element")
  void reverseArrayNumbers() {
    int[] input = {1, -2, 3, -4};
    int[] expected = {-1, 2, -3, 4};
    assertArrayEquals(expected, myAdvancedMath.reverseArrayNumbers(input, new MyMath()));
  }
}
```
##### متد *add(int, int)*
چهار متد تولید شده‌اند که شامل می‌شوند:
- x و y مثبت
- یک تست پارامتری معتبر
  - هر دو x و y مثبت
  - هر دو صفر
- x مثبت و y منفی و برعکس
- تست سرریز (overflow)

تست‌ها MC/DC دارند.  
این یک بهبود قابل‌توجه است. علاوه بر آنچه
عامل بدون skill تولید کرده بود، یک تست پارامتری و تستِ هر دو صفر هم داریم.
با این حال، در یک متد از دو assertion (ادعا) استفاده کرد. پیش از این هم همین‌طور بود.

##### متد *powerOfTwo(int)*
پنج متد تولید شد. تست‌ها شامل می‌شوند:
- درست زیر کران پایین
- کران پایین
- یک تست پارامتری
  - درست بالای کران پایین
  - دو تست معمولی
  - کران بالا
- درست بالای کران بالا

MC/DC داریم.  
اینجا هم بهبودهایی هست. یک تست پارامتری استفاده شد و درست بالای
کران پایین و کران بالا پوشش داده شدند.

##### متد *reverseArrayNumbers(int[], MyMath)*
تنها یک متد برای یک تست معمولی تولید شده است.  
این معادل تست‌های تولیدشده بدون skill است.

#### واحد FileIO
```java
class FileIOTestGPTOSS_SP {

  private FileIO fileIO;

  @BeforeEach
  void setUp() {
    fileIO = new FileIO();
  }

  @Test
  @DisplayName("readFile returns lines as array for a small file")
  void readSmallFile() throws IOException {
    Path temp = Files.createTempFile("fileio-test", ".txt");
    Files.writeString(temp, "line1\nline2\nline3");
    String[] result = fileIO.readFile(temp.toString());
    assertArrayEquals(new String[]{"line1", "line2", "line3"}, result);
    Files.deleteIfExists(temp);
  }

  @Test
  @DisplayName("readFile returns empty array for empty file")
  void readEmptyFile() throws IOException {
    Path temp = Files.createTempFile("fileio-empty", ".txt");
    String[] result = fileIO.readFile(temp.toString());
    assertArrayEquals(new String[]{}, result);
    Files.deleteIfExists(temp);
  }
}
```
دو متد تولید شده‌اند که شامل می‌شوند:
- یک تست معمولی
- تست فایل خالی

ما به exception برخورد نمی‌کنیم، بنابراین پوشش دستور (statement coverage) نداریم.
در دور قبل فقط یک تست معمولی تولید شده بود.

#### واحد NameSearch
```java
class NameSearchTestGPTOSS_SP {

  private NameSearch nameSearch;
  private FileIO fileIO;

  @BeforeEach
  void setUp() {
    nameSearch = new NameSearch();
    fileIO = new FileIO();
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns matching names")
  void findMatchingNames() throws IOException {
    Path temp = Files.createTempFile("names", ".txt");
    Files.writeString(temp, "Alice\nAlfred\nbob\nALBERT");
    String[] result = nameSearch.findNamesInFileThatStartWith(temp.toString(), fileIO, "Al");
    assertArrayEquals(new String[]{"Alice", "Alfred", "ALBERT"}, result);
    Files.deleteIfExists(temp);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns empty array when no match")
  void noMatches() throws IOException {
    Path temp = Files.createTempFile("emptysearch", ".txt");
    Files.writeString(temp, "John\nJane");
    String[] result = nameSearch.findNamesInFileThatStartWith(temp.toString(), fileIO, "Xy");
    assertArrayEquals(new String[]{}, result);
    Files.deleteIfExists(temp);
  }
}
```
دو متد تست تولید شده‌اند که شامل می‌شوند:
- یک تست معمولی
- یک تست بدون نتیجه.

این تست به MC/DC می‌رسد.  
این دقیقاً همان چیزی است که عامل بدون هیچ skill تولید کرد.

#### مقایسه
به نظر می‌رسد هرچه متد پیچیده‌تر باشد، بهبود بیشتری خواهد داشت.
متدهای *add* و *powerOfTwo* که پیچیده‌ترین متدهای
پروژه هستند، بهبود قابل‌توجهی داشتند. همچنین یک edge case دیگر
در متد *readFile* پوشش داده شد. سایر بهبودها استفاده از
متغیرهای private و مقداردهی آن‌ها پیش از هر تست و نمایش یک نام
برای هر متد است. نام‌گذاری متدها هم افتضاح است. نه‌تنها
با test شروع نمی‌شوند، بلکه دقیقاً هم نشان نمی‌دهند که
چه کار می‌کنند. برای نمونه، متدی با نام "powerZero" متد *powerOfTwo* را با ۰ تست می‌کند.
بهتر بود "testPowerOfZeroIsOne" باشد.  
همچنین باید در نظر گرفته شود که مدل برای تولید
تست‌ها ۵۰٪ زمان بیشتری برد.

## دور ۲ (MiMo V2.5 با "java-junit")
حالا یک مدل ابری را امتحان می‌کنم. همچنین skill دیگر استفاده می‌شود. تنها prompt
تغییر کرده تا مدل مجبور شود کلاس‌ها را بر اساس نام خودش نام‌گذاری کند.
> برای این واحدها تست واحد تولید کن. کلاس‌های تست تولیدشده باید با "TestMiMo_SP" پایان یابند.
> از skill "java-junit" استفاده کن. وابستگی‌ها را mock نکن.

تولید تست‌های واحد ۱ دقیقه و ۳۷ ثانیه طول کشید.
#### واحد MyMath
```java
@DisplayName("MyMath Tests")
class MyMathTestMiMo_SP {

  private final MyMath myMath = new MyMath();

  @Test
  @DisplayName("checkSign returns 'positive' for positive numbers")
  void checkSign_shouldReturnPositive_whenNumberIsPositive() {
    assertEquals("positive", myMath.checkSign(5));
  }

  @ParameterizedTest(name = "checkSign({0}) should return 'negative'")
  @ValueSource(ints = {-1, -100, -999})
  @DisplayName("checkSign returns 'negative' for negative numbers")
  void checkSign_shouldReturnNegative_whenNumberIsNegative(int number) {
    assertEquals("negative", myMath.checkSign(number));
  }

  @Test
  @DisplayName("checkSign throws exception when input is zero")
  void checkSign_shouldThrowException_whenNumberIsZero() {
    assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
  }

  @Test
  @DisplayName("reverseNumber negates positive number")
  void reverseNumber_shouldNegatePositiveNumber() {
    assertEquals(-5, myMath.reverseNumber(5));
  }

  @Test
  @DisplayName("reverseNumber negates negative number")
  void reverseNumber_shouldNegateNegativeNumber() {
    assertEquals(5, myMath.reverseNumber(-5));
  }

  @Test
  @DisplayName("reverseNumber returns zero when input is zero")
  void reverseNumber_shouldReturnZero_whenInputIsZero() {
    assertEquals(0, myMath.reverseNumber(0));
  }
}
```

##### متد *checkSign(int)*
سه متد تولید شده‌اند که شامل می‌شوند:
- یک تست مثبت معمولی
- یک تست پارامتری برای اعداد منفی
  - درست بالای مرز
  - تست دو مقدار منفی معمولی
- صفر (مرز)

تست‌ها MC/DC دارند.  
جالب است که فقط برای منفی‌ها از تست پارامتری استفاده کرد. باید
برای مثبت‌ها هم همین کار را می‌کرد. ایده‌آل می‌شد.

##### متد *reverseNumber(int)*
سه متد تولید شده‌اند که شامل می‌شوند:
- یک مثبت معمولی
- یک منفی معمولی
- صفر (در مرز)

تست‌ها MC/DC دارند.  
این تولید تست بسیار پایه‌ای است؛ به نظر می‌رسد skill کاری انجام نداده است.

#### واحد MyAdvancedMath
```java
@DisplayName("MyAdvancedMath Tests")
class MyAdvancedMathTestMiMo_SP {

  private final MyAdvancedMath math = new MyAdvancedMath();
  private final MyMath myMath = new MyMath();

  // --- add tests ---

  @Test
  @DisplayName("add returns sum of two positive integers")
  void add_shouldReturnSum_whenBothPositive() {
    assertEquals(5, math.add(2, 3));
  }

  @Test
  @DisplayName("add returns zero when both inputs are zero")
  void add_shouldReturnZero_whenBothZero() {
    assertEquals(0, math.add(0, 0));
  }

  @Test
  @DisplayName("add throws exception when first number is negative")
  void add_shouldThrowException_whenFirstIsNegative() {
    assertThrows(IllegalArgumentException.class, () -> math.add(-1, 5));
  }

  @Test
  @DisplayName("add throws exception when second number is negative")
  void add_shouldThrowException_whenSecondIsNegative() {
    assertThrows(IllegalArgumentException.class, () -> math.add(5, -1));
  }

  @Test
  @DisplayName("add throws exception on integer overflow")
  void add_shouldThrowException_whenOverflow() {
    assertThrows(IllegalArgumentException.class, () -> math.add(Integer.MAX_VALUE, 1));
  }

  // --- powerOfTwo tests ---

  @ParameterizedTest(name = "powerOfTwo({0}) should return correct result")
  @CsvSource({
          "0, 1",
          "1, 2",
          "2, 4",
          "10, 1024",
          "30, 1073741824"
  })
  @DisplayName("powerOfTwo returns correct result for valid powers")
  void powerOfTwo_shouldReturnCorrectResult_whenPowerIsWithinRange(int power, int expected) {
    assertEquals(expected, math.powerOfTwo(power));
  }

  @Test
  @DisplayName("powerOfTwo throws exception when power is negative")
  void powerOfTwo_shouldThrowException_whenPowerIsNegative() {
    assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(-1));
  }

  @Test
  @DisplayName("powerOfTwo throws exception when power is greater than 30")
  void powerOfTwo_shouldThrowException_whenPowerExceeds30() {
    assertThrows(IllegalArgumentException.class, () -> math.powerOfTwo(31));
  }

  // --- reverseArrayNumbers tests ---

  @Test
  @DisplayName("reverseArrayNumbers reverses sign of each element")
  void reverseArrayNumbers_shouldReverseSignsOfAllElements() {
    int[] input = {1, -2, 3, 0};
    int[] result = math.reverseArrayNumbers(input, myMath);
    assertArrayEquals(new int[]{-1, 2, -3, 0}, result);
  }

  @Test
  @DisplayName("reverseArrayNumbers handles empty array")
  void reverseArrayNumbers_shouldHandleEmptyArray() {
    int[] input = {};
    int[] result = math.reverseArrayNumbers(input, myMath);
    assertArrayEquals(new int[]{}, result);
  }

  @Test
  @DisplayName("reverseArrayNumbers handles single element array")
  void reverseArrayNumbers_shouldHandleSingleElementArray() {
    int[] input = {42};
    int[] result = math.reverseArrayNumbers(input, myMath);
    assertArrayEquals(new int[]{-42}, result);
  }
}

```
##### متد *add(int, int)*
پنج متد تولید شده‌اند که شامل می‌شوند:
- x و y مثبت
- هر دو صفر
- x منفی و y مثبت و برعکس
- تست سرریز

MC/DC محقق شد.  
این‌ها تست‌های خوبی هستند. با تست‌های پارامتری و مقادیر مرزی
می‌شد بیشتر بهبودشان داد.

##### متد *powerOfTwo(int)*
سه متد تولید شده است. تست‌ها شامل می‌شوند:
- یک تست پارامتری مقادیر معتبر
  - کران پایین
  - درست بالای کران پایین
  - دو تست معمولی
  - کران بالا
- درست زیر کران پایین
- درست بالای کران بالا

MC/DC محقق شد.  
عامل در این تست هم خوب عمل کرد. اگر یکی از تست‌های معمولی با ۲۹
جایگزین می‌شد، ایده‌آل می‌شد.

##### متد *reverseArrayNumbers(int[], MyMath)*
سه متد تولید شده‌اند که شامل می‌شوند:
- تست آرایهٔ خالی
- یک تست معمولی
- تست آرایهٔ تک‌عضوی

MC/DC محقق شد.  
همهٔ edge caseها پوشش داده شدند.

#### واحد FileIO
```java
@DisplayName("FileIO Tests")
class FileIOTestMiMo_SP {

  private final FileIO fileIO = new FileIO();

  @TempDir
  Path tempDir;

  @Test
  @DisplayName("readFile returns lines from a valid file")
  void readFile_shouldReturnLines_whenFileExists() throws IOException {
    File file = tempDir.resolve("names.txt").toFile();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write("Alice\nBob\nCharlie\n");
    }

    String[] result = fileIO.readFile(file.getAbsolutePath());
    assertArrayEquals(new String[]{"Alice", "Bob", "Charlie"}, result);
  }

  @Test
  @DisplayName("readFile returns empty array for empty file")
  void readFile_shouldReturnEmptyArray_whenFileIsEmpty() throws IOException {
    File file = tempDir.resolve("empty.txt").toFile();
    file.createNewFile();

    String[] result = fileIO.readFile(file.getAbsolutePath());
    assertArrayEquals(new String[]{}, result);
  }

  @Test
  @DisplayName("readFile handles single line file")
  void readFile_shouldReturnSingleLine_whenFileHasOneLine() throws IOException {
    File file = tempDir.resolve("single.txt").toFile();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write("OnlyLine\n");
    }

    String[] result = fileIO.readFile(file.getAbsolutePath());
    assertArrayEquals(new String[]{"OnlyLine"}, result);
  }

  @Test
  @DisplayName("readFile returns empty array for non-existent file")
  void readFile_shouldReturnEmptyArray_whenFileDoesNotExist() {
    String[] result = fileIO.readFile("non_existent_file.txt");
    assertArrayEquals(new String[]{}, result);
  }
}
```
چهار متد تولید شده‌اند که شامل می‌شوند:
- یک فایل خالی
- یک فایل با چند خط
- یک فایل با یک خط
- یک فایل ناموجود

MC/DC محقق شد.
این خوب انجام شده است. می‌شد edge caseهای بیشتری را پوشش داد، ولی واقعاً ضروری نیستند.

#### واحد NameSearch
```java
@DisplayName("NameSearch Tests")
class NameSearchTestMiMo_SP {

  private NameSearch nameSearch;
  private FileIO fileIO;
  private File namesFile;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() throws IOException {
    nameSearch = new NameSearch();
    fileIO = new FileIO();
    namesFile = tempDir.resolve("names.txt").toFile();
    try (FileWriter writer = new FileWriter(namesFile)) {
      writer.write("Alice\nBob\nCharlie\nAmanda\nDavid\nAnna\n");
    }
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns matching names")
  void findNamesInFileThatStartWith_shouldReturnMatchingNames() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "A");
    assertArrayEquals(new String[]{"Alice", "Amanda", "Anna"}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns empty array when no matches")
  void findNamesInFileThatStartWith_shouldReturnEmpty_whenNoMatches() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "Z");
    assertArrayEquals(new String[]{}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns single match")
  void findNamesInFileThatStartWith_shouldReturnSingleMatch() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "D");
    assertArrayEquals(new String[]{"David"}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns all names when prefix is empty")
  void findNamesInFileThatStartWith_shouldReturnAll_whenPrefixIsEmpty() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "");
    assertArrayEquals(new String[]{"Alice", "Bob", "Charlie", "Amanda", "David", "Anna"}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns empty array for empty file")
  void findNamesInFileThatStartWith_shouldReturnEmpty_whenFileIsEmpty() throws IOException {
    File emptyFile = tempDir.resolve("empty.txt").toFile();
    emptyFile.createNewFile();

    String[] result = nameSearch.findNamesInFileThatStartWith(
            emptyFile.getAbsolutePath(), fileIO, "A");
    assertArrayEquals(new String[]{}, result);
  }
}
```
پنج متد تولید شده‌اند که شامل می‌شوند:
- تست چند نتیجه‌ای
- تست تک نتیجه‌ای
- تست فایل خالی
- تست پیشوند خالی
- تست بدون تطابق

این تست به MC/DC می‌رسد.  
این یک تولید تست واحد عالی است.

#### نتیجه‌گیری
می‌توانم ببینم که skills ساختار تست‌های تولیدشده را بهبود می‌دهند.  
تنها edge caseای که باید پوشش داده می‌شد، Integer.MIN_VALUE در 
متد *reverseNumber* است. نام‌گذاری متدها خیلی خوب انجام شده، اگر
شروع نشدن با "test" را نادیده بگیریم.
پتانسیل بیشتری برای استفاده از تست‌های پارامتری، بررسی بیشتر مقادیر مرزی و
قطعاً استفاده از فضای ذخیره‌سازی موقت JUnit وجود دارد.

## دور ۳ (DeepSeek V4 Flash با variant پیش‌فرض)
هدف اصلی این است که ببینیم اگر از چند skill استفاده کنیم چه اتفاقی می‌افتد. ممکن است برخی
ناهماهنگی‌ها پیش بیاید. prompt تقریباً همان است:
> برای این واحدها تست واحد تولید کن. کلاس‌های تست تولیدشده باید با "TestDeepSeek_SP" پایان یابند.
> از skillهای "java-junit" و "junit-5-skill" استفاده کن. وابستگی‌ها را mock نکن.

تولید تست‌ها ۲ دقیقه و ۳۶ ثانیه طول کشید.

#### واحد MyMath
```java
@DisplayName("MyMath Tests (DeepSeek_SP)")
class MyMathTestDeepSeek_SP {

  private final MyMath myMath = new MyMath();

  @Test
  @DisplayName("checkSign returns 'positive' for positive numbers")
  void checkSign_shouldReturnPositive_whenNumberIsPositive() {
    assertEquals("positive", myMath.checkSign(1));
  }

  @ParameterizedTest(name = "checkSign({0}) should return 'positive'")
  @ValueSource(ints = {2, 100, Integer.MAX_VALUE})
  @DisplayName("checkSign returns 'positive' for various positive numbers")
  void checkSign_shouldReturnPositive_forVariousPositiveNumbers(int number) {
    assertEquals("positive", myMath.checkSign(number));
  }

  @ParameterizedTest(name = "checkSign({0}) should return 'negative'")
  @ValueSource(ints = {-1, -50, Integer.MIN_VALUE})
  @DisplayName("checkSign returns 'negative' for various negative numbers")
  void checkSign_shouldReturnNegative_forVariousNegativeNumbers(int number) {
    assertEquals("negative", myMath.checkSign(number));
  }

  @Test
  @DisplayName("checkSign throws IllegalArgumentException when input is zero")
  void checkSign_shouldThrowException_whenNumberIsZero() {
    Exception exception = assertThrows(IllegalArgumentException.class,
            () -> myMath.checkSign(0));
    assertEquals("Number had no sign", exception.getMessage());
  }

  @ParameterizedTest(name = "reverseNumber({0}) should return {1}")
  @org.junit.jupiter.params.provider.CsvSource({
          "5, -5",
          "-3, 3",
          "0, 0",
          "1, -1",
          "-1, 1"
  })
  @DisplayName("reverseNumber reverses sign correctly")
  void reverseNumber_shouldReverseSign_correctly(int input, int expected) {
    assertEquals(expected, myMath.reverseNumber(input));
  }

  @Test
  @DisplayName("reverseNumber handles Integer.MAX_VALUE")
  void reverseNumber_shouldHandleMaxValue() {
    assertEquals(Integer.MIN_VALUE + 1, myMath.reverseNumber(Integer.MAX_VALUE));
  }

  @Test
  @DisplayName("reverseNumber handles Integer.MIN_VALUE")
  void reverseNumber_shouldHandleMinValue() {
    assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
  }
}

```
##### متد *checkSign(int)*
چهار متد تولید شده‌اند. شامل می‌شوند:
- درست بالای مرز
- یک تست پارامتری با دو مقدار مثبت معمولی
- یک تست پارامتری برای اعداد منفی
  - درست زیر مرز
  - یک عدد منفی معمولی
- صفر (مرز)

تست‌ها MC/DC دارند.  
تنها ایراد این است که ۱ در متد مستقل خودش تست شد، اما
‎-۱ درون یک تست پارامتری تست شد. این یک ناهماهنگی عجیب بود.

##### متد *reverseNumber(int)*
سه متد تولید شده‌اند. شامل می‌شوند:
- یک تست پارامتری ترکیبی
  - یک منفی معمولی
  - یک مثبت معمولی
  - مرز
  - درست زیر مرز
  - درست بالای مرز
- Integer.MAX
- edge case مربوط به Integer.MIN

تست‌ها MC/DC دارند.  
این یک تست عالی برای این متد است.

#### واحد MyAdvancedMath
```java
@DisplayName("MyAdvancedMath Tests (DeepSeek_SP)")
class MyAdvancedMathTestDeepSeek_SP {

  private final MyAdvancedMath math = new MyAdvancedMath();
  private final MyMath myMath = new MyMath();

  @ParameterizedTest(name = "add({0}, {1}) should return {2}")
  @CsvSource({
          "1, 2, 3",
          "0, 5, 5",
          "10, 20, 30",
          "0, 0, 0",
          "100, 200, 300"
  })
  @DisplayName("add returns correct sum for positive inputs")
  void add_shouldReturnCorrectSum_whenBothInputsArePositive(int x, int y, int expected) {
    assertEquals(expected, math.add(x, y));
  }

  @Test
  @DisplayName("add throws exception when first argument is negative")
  void add_shouldThrowException_whenFirstArgumentIsNegative() {
    Exception exception = assertThrows(IllegalArgumentException.class,
            () -> math.add(-1, 5));
    assertEquals("Input numbers should be positive.", exception.getMessage());
  }

  @Test
  @DisplayName("add throws exception when second argument is negative")
  void add_shouldThrowException_whenSecondArgumentIsNegative() {
    assertThrows(IllegalArgumentException.class,
            () -> math.add(5, -1));
  }

  @Test
  @DisplayName("add throws exception when both arguments are negative")
  void add_shouldThrowException_whenBothArgumentsAreNegative() {
    assertThrows(IllegalArgumentException.class,
            () -> math.add(-1, -1));
  }

  @Test
  @DisplayName("add throws exception on integer overflow")
  void add_shouldThrowException_whenResultOverflows() {
    Exception exception = assertThrows(IllegalArgumentException.class,
            () -> math.add(Integer.MAX_VALUE, 1));
    assertEquals("Cannot fit the result in an Integer.", exception.getMessage());
  }

  @Test
  @DisplayName("add throws exception when result would overflow near max boundary")
  void add_shouldThrowException_whenResultExceedsMaxInt() {
    assertThrows(IllegalArgumentException.class,
            () -> math.add(Integer.MAX_VALUE - 5, 10));
  }

  @ParameterizedTest(name = "powerOfTwo({0}) should return {1}")
  @CsvSource({
          "0, 1",
          "1, 2",
          "2, 4",
          "3, 8",
          "5, 32",
          "10, 1024",
          "20, 1048576",
          "30, 1073741824"
  })
  @DisplayName("powerOfTwo returns correct result for valid powers")
  void powerOfTwo_shouldReturnCorrectResult_whenPowerIsValid(int power, int expected) {
    assertEquals(expected, math.powerOfTwo(power));
  }

  @ParameterizedTest(name = "powerOfTwo({0}) should throw exception")
  @ValueSource(ints = {-1, -5, -100})
  @DisplayName("powerOfTwo throws exception when power is negative")
  void powerOfTwo_shouldThrowException_whenPowerIsNegative(int power) {
    assertThrows(IllegalArgumentException.class,
            () -> math.powerOfTwo(power));
  }

  @Test
  @DisplayName("powerOfTwo throws exception when power is 31")
  void powerOfTwo_shouldThrowException_whenPowerIs31() {
    assertThrows(IllegalArgumentException.class,
            () -> math.powerOfTwo(31));
  }

  @Test
  @DisplayName("powerOfTwo throws exception when power is 100")
  void powerOfTwo_shouldThrowException_whenPowerIsFarOutOfRange() {
    assertThrows(IllegalArgumentException.class,
            () -> math.powerOfTwo(100));
  }

  @Test
  @DisplayName("reverseArrayNumbers reverses signs when using real MyMath")
  void reverseArrayNumbers_shouldReverseSignsOfAllElements() {
    int[] input = {10, -20, 30, -40, 0};
    int[] expected = {-10, 20, -30, 40, 0};
    assertArrayEquals(expected, math.reverseArrayNumbers(input, myMath));
  }

  @Test
  @DisplayName("reverseArrayNumbers returns empty array when input is empty")
  void reverseArrayNumbers_shouldReturnEmptyArray_whenInputIsEmpty() {
    int[] input = {};
    assertArrayEquals(new int[]{}, math.reverseArrayNumbers(input, myMath));
  }

  @Test
  @DisplayName("reverseArrayNumbers handles single element array")
  void reverseArrayNumbers_shouldHandleSingleElement() {
    int[] input = {7};
    assertArrayEquals(new int[]{-7}, math.reverseArrayNumbers(input, myMath));
  }

  @Test
  @DisplayName("reverseArrayNumbers modifies the original array")
  void reverseArrayNumbers_shouldModifyOriginalArray() {
    int[] input = {1, 2, 3};
    math.reverseArrayNumbers(input, myMath);
    assertArrayEquals(new int[]{-1, -2, -3}, input);
  }

  @Test
  @DisplayName("reverseArrayNumbers handles array with all zeros")
  void reverseArrayNumbers_shouldHandleAllZeros() {
    int[] input = {0, 0, 0};
    assertArrayEquals(new int[]{0, 0, 0}, math.reverseArrayNumbers(input, myMath));
  }
}

```
##### متد *add(int, int)*
شش متد تولید شده‌اند که شامل می‌شوند:
- یک تست پارامتری معتبر
  - سه تست هر دو مثبت
  - x صفر است و y مثبت
  - هر دو صفر هستند
- x منفی است و y مثبت و برعکس
- هر دو منفی
- دو تست سرریز

MC/DC محقق شد.  
این هم یک تست خوب است. مقادیر مرزی به‌خوبی تست شده‌اند. تست دومِ سرریز
غیرضروری بود.

##### متد *powerOfTwo(int)*
شش متد تولید شده‌اند که شامل می‌شوند:
- یک تست پارامتری معتبر
  - کران پایین
  - درست بالای کران پایین
  - پنج مقدار معمولی
  - کران بالا
- یک تست پارامتری نامعتبر
  - درست زیر کران پایین
  - دو مقدار منفی معمولی
- درست بالای کران بالا
- عبور از کران بالا با فاصلهٔ زیاد

MC/DC داریم.  
در مجموع، تست‌های خوبی تولید شده‌اند، با این حال، آخرین تست غیرضروری بود.

##### متد *reverseArrayNumbers(int[], MyMath)*
پنج متد تولید شده‌اند که شامل می‌شوند:
- یک تست معمولی
- تست آرایهٔ خالی
- آرایهٔ تک‌عضوی
- بررسی تغییر آرایهٔ اصلی
- آرایهٔ تماماً صفر

MC/DC محقق شد.  
دو تست آخر بی‌ضرورت به نظر می‌رسند. بررسی تغییر آرایه انجام شده چون پارامتر آرایه در متد،
ارجاعی به آرایهٔ اصلی است، پس این یک ویژگی از java است. صفر درون واحد *MyMath*
بررسی شده است، بنابراین تست کردنش اینجا غیرضروری است.

#### واحد FileIO
```java
@DisplayName("FileIO Tests (DeepSeek_SP)")
class FileIOTestDeepSeek_SP {

  private final FileIO fileIO = new FileIO();

  @TempDir
  Path tempDir;

  @Test
  @DisplayName("readFile returns all lines from valid file")
  void readFile_shouldReturnLines_whenFileExists() throws IOException {
    File file = tempDir.resolve("test.txt").toFile();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write("line1\nline2\nline3\n");
    }
    assertArrayEquals(new String[]{"line1", "line2", "line3"},
            fileIO.readFile(file.getAbsolutePath()));
  }

  @Test
  @DisplayName("readFile returns empty array for empty file")
  void readFile_shouldReturnEmptyArray_whenFileIsEmpty() throws IOException {
    File file = tempDir.resolve("empty.txt").toFile();
    file.createNewFile();
    assertArrayEquals(new String[]{}, fileIO.readFile(file.getAbsolutePath()));
  }

  @Test
  @DisplayName("readFile handles single line file")
  void readFile_shouldReturnSingleLine_whenFileHasOneLine() throws IOException {
    File file = tempDir.resolve("single.txt").toFile();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write("soleEntry\n");
    }
    assertArrayEquals(new String[]{"soleEntry"},
            fileIO.readFile(file.getAbsolutePath()));
  }

  @Test
  @DisplayName("readFile handles file with trailing newline")
  void readFile_shouldHandleTrailingNewline() throws IOException {
    File file = tempDir.resolve("trailing.txt").toFile();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write("first\nsecond\n");
    }
    assertArrayEquals(new String[]{"first", "second"},
            fileIO.readFile(file.getAbsolutePath()));
  }

  @Test
  @DisplayName("readFile returns empty array for non-existent file")
  void readFile_shouldReturnEmptyArray_whenFileDoesNotExist() {
    assertArrayEquals(new String[]{},
            fileIO.readFile(tempDir.resolve("nonexistent.txt").toString()));
  }

  @Test
  @DisplayName("readFile handles file with special characters")
  void readFile_shouldHandleSpecialCharacters() throws IOException {
    File file = tempDir.resolve("special.txt").toFile();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write("héllo\nwörld\n");
    }
    assertArrayEquals(new String[]{"héllo", "wörld"},
            fileIO.readFile(file.getAbsolutePath()));
  }
}
```
شش متد تولید شده‌اند که شامل می‌شوند:
- یک تست معمولی
- یک تست فایل خالی
- یک تست فایل تک‌خطی
- خطوط جدید انتهایی
- فایل ناموجود
- فایلی حاوی نویسه‌های خاص

MC/DC محقق شد.
edge caseها به‌خوبی پوشش داده شده‌اند، اما برخی از آن‌ها صرفاً ضروری‌اند. خود FileReader در java
خط خالی در انتها را null در نظر می‌گیرد. خود java با نویسه‌های خاص برخورد می‌کند، نه
واحد ما.

#### واحد NameSearch
```java
@DisplayName("NameSearch Tests (DeepSeek_SP)")
class NameSearchTestDeepSeek_SP {

  private NameSearch nameSearch;
  private FileIO fileIO;
  private File namesFile;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() throws IOException {
    nameSearch = new NameSearch();
    fileIO = new FileIO();
    namesFile = tempDir.resolve("names.txt").toFile();
    try (FileWriter writer = new FileWriter(namesFile)) {
      writer.write("Alice\nBob\nCharlie\nAmanda\nDavid\nAnna\nAlex\n");
    }
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns names matching prefix")
  void findNamesInFileThatStartWith_shouldReturnMatchingNames() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "A");
    assertAll("should find all names starting with A",
            () -> assertArrayEquals(new String[]{"Alice", "Amanda", "Anna", "Alex"}, result));
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns empty array when no match")
  void findNamesInFileThatStartWith_shouldReturnEmptyArray_whenNoMatch() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "Z");
    assertArrayEquals(new String[]{}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns single match")
  void findNamesInFileThatStartWith_shouldReturnSingleMatch() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "B");
    assertArrayEquals(new String[]{"Bob"}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns all names with empty prefix")
  void findNamesInFileThatStartWith_shouldReturnAllNames_whenPrefixIsEmpty() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "");
    assertArrayEquals(
            new String[]{"Alice", "Bob", "Charlie", "Amanda", "David", "Anna", "Alex"},
            result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns empty for empty file")
  void findNamesInFileThatStartWith_shouldReturnEmpty_whenFileIsEmpty() throws IOException {
    File emptyFile = tempDir.resolve("empty.txt").toFile();
    emptyFile.createNewFile();
    String[] result = nameSearch.findNamesInFileThatStartWith(
            emptyFile.getAbsolutePath(), fileIO, "A");
    assertArrayEquals(new String[]{}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith handles prefix with special characters")
  void findNamesInFileThatStartWith_shouldHandleSpecialPrefix() throws IOException {
    File specialFile = tempDir.resolve("special.txt").toFile();
    try (FileWriter writer = new FileWriter(specialFile)) {
      writer.write("@lpha\n#hash\n@lso\n");
    }
    String[] result = nameSearch.findNamesInFileThatStartWith(
            specialFile.getAbsolutePath(), fileIO, "@");
    assertArrayEquals(new String[]{"@lpha", "@lso"}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith is case-sensitive")
  void findNamesInFileThatStartWith_shouldBeCaseSensitive() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            namesFile.getAbsolutePath(), fileIO, "a");
    assertArrayEquals(new String[]{}, result);
  }

  @Test
  @DisplayName("findNamesInFileThatStartWith returns empty for non-existent file")
  void findNamesInFileThatStartWith_shouldReturnEmpty_whenFileDoesNotExist() {
    String[] result = nameSearch.findNamesInFileThatStartWith(
            tempDir.resolve("nope.txt").toString(), fileIO, "A");
    assertArrayEquals(new String[]{}, result);
  }
}
```
هشت متد تولید شده‌اند که شامل می‌شوند:
- یک تست با چند نتیجه
- یک تست بدون تطابق
- یک تست تک‌نتیجه‌ای
- تست پیشوند خالی
- تست فایل خالی
- تست پیشوند خاص
- تست حساسیت به بزرگی و کوچکی حروف
- تست فایل ناموجود

این تست به MC/DC می‌رسد.  
مانند واحد تست *FileIO*، بسیاری از تست‌های بالا غیرضروری‌اند: تست پیشوند خاص،
تست حساسیت به حروف و تست فایل ناموجود. همهٔ edge caseهای ضروری پوشش داده شده‌اند.


#### نتیجه‌گیری
عامل عالی عمل کرد. تعجب می‌کنم که آیا به خاطر خودِ مدل بوده است. بنابراین در یک دور اضافه،
دوباره با همین مدل ولی بدون هیچ skill تست تولید می‌کنم. نام‌گذاری متدها خوب است، با این حال،
با test شروع نمی‌شوند. مقادیر مرزی خیلی خوب بررسی شده‌اند. MC/DC بدون اینکه من
اشاره کنم محقق شد. edge caseها به‌خوبی پوشش داده شدند، اما تست کردن برخی از آن‌ها غیرضروری بود.  
ترکیب کردن skills هیچ ناهماهنگی‌ای ایجاد نکرد و این دور بهترین عملکرد را تاکنون داشت.
پس روی کاغذ، ترکیب skills تست‌های واحد تولیدشده را بهبود می‌دهد، اما برای ارزیابی واقعی
این موضوع باید تست‌های بیشتری انجام شود.

## دور اضافه: DeepSeek V4 Flash با variant پیش‌فرض بدون skill
از آنجا که این مدل عالی است، باید عملکردش را هنگام تولید تست واحد
بدون هیچ skill مقایسه کنم.
> برای این واحدها تست واحد تولید کن. وابستگی‌ها را mock نکن و از هیچ skill استفاده نکن.
> کلاس‌های تست تولیدشده باید با "TestDeepSeek_NS" پایان یابند.

تولید تست‌ها ۳۳٫۹ ثانیه طول کشید، ۲ دقیقه کمتر از دور قبل.

#### واحد MyMath
```java
public class MyMathTestDeepSeek_NS {

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
  void testCheckSignZeroThrowsException() {
    MyMath mm = new MyMath();
    assertThrows(IllegalArgumentException.class, () -> mm.checkSign(0));
  }

  @Test
  void testReverseNumberPositive() {
    MyMath mm = new MyMath();
    assertEquals(-7, mm.reverseNumber(7));
  }

  @Test
  void testReverseNumberNegative() {
    MyMath mm = new MyMath();
    assertEquals(4, mm.reverseNumber(-4));
  }

  @Test
  void testReverseNumberZero() {
    MyMath mm = new MyMath();
    assertEquals(0, mm.reverseNumber(0));
  }
}

```
##### متد *checkSign(int)*
سه متد تولید شده‌اند. شامل می‌شوند:
- یک تست مثبت معمولی
- یک تست منفی معمولی
- صفر (مرز)

تست‌ها MC/DC دارند.  
این یک مجموعه تست بسیار پایه‌ای است. در دور قبل، تست‌های پارامتری و مقادیر مرزی
استفاده شده بودند.

##### متد *reverseNumber(int)*
سه متد تولید شده‌اند. شامل می‌شوند:
- یک تست مثبت معمولی
- یک تست منفی معمولی
- صفر (مرز)

تست‌ها MC/DC دارند.  
این هم بسیار پایه‌ای است. دور قبل اینجا ایده‌آل بود. فاصله بسیار زیادی وجود دارد.

#### واحد MyAdvancedMath
```java
public class MyAdvancedMathTestDeepSeek_NS {

  private final MyAdvancedMath mam = new MyAdvancedMath();

  @ParameterizedTest
  @CsvSource({"1, 2, 3", "0, 5, 5", "100, 200, 300", "2147483646, 1, 2147483647"})
  void testAddValidInputs(int x, int y, int expected) {
    assertEquals(expected, mam.add(x, y));
  }

  @Test
  void testAddNegativeXThrows() {
    assertThrows(IllegalArgumentException.class, () -> mam.add(-1, 5));
  }

  @Test
  void testAddNegativeYThrows() {
    assertThrows(IllegalArgumentException.class, () -> mam.add(5, -1));
  }

  @Test
  void testAddBothNegativeThrows() {
    assertThrows(IllegalArgumentException.class, () -> mam.add(-1, -1));
  }

  @Test
  void testAddOverflowThrows() {
    assertThrows(IllegalArgumentException.class, () -> mam.add(Integer.MAX_VALUE, 1));
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 2", "2, 4", "3, 8", "4, 16", "5, 32", "10, 1024", "30, 1073741824"})
  void testPowerOfTwoValidInputs(int power, int expected) {
    assertEquals(expected, mam.powerOfTwo(power));
  }

  @Test
  void testPowerOfTwoNegativeThrows() {
    assertThrows(IllegalArgumentException.class, () -> mam.powerOfTwo(-1));
  }

  @Test
  void testPowerOfTwoAbove30Throws() {
    assertThrows(IllegalArgumentException.class, () -> mam.powerOfTwo(31));
  }

  @Test
  void testReverseArrayNumbers() {
    int[] input = {1, -2, 3, -4, 0};
    int[] expected = {-1, 2, -3, 4, 0};
    assertArrayEquals(expected, mam.reverseArrayNumbers(input, new MyMath()));
  }

  @Test
  void testReverseArrayNumbersEmpty() {
    int[] input = {};
    assertArrayEquals(new int[]{}, mam.reverseArrayNumbers(input, new MyMath()));
  }
}

```
##### متد *add(int, int)*
پنج متد تولید شده‌اند که شامل می‌شوند:
- یک تست پارامتری معتبر
  - دو تست هر دو مثبت
  - x صفر است و y مثبت
  - مجموع x و y درست زیر سرریز است
- x منفی است و y مثبت و برعکس
- هر دو منفی
- یک تست سرریز

MC/DC محقق شد.  
نمی‌دانم چرا، ولی در این واحد پیچیده، عامل خیلی بهتر عمل می‌کند! این
به اندازهٔ دور قبل خوب است. دور قبل یک تستِ هر دو صفر را پوشش داده بود، در حالی که این دور یک تستِ نزدیک به سرریز را پوشش داده است. همچنین از یک تست پارامتری
استفاده کرد.

##### متد *powerOfTwo(int)*
سه متد تولید شده‌اند که شامل می‌شوند:
- یک تست پارامتری معتبر
  - کران پایین
  - درست بالای کران پایین
  - پنج مقدار معمولی
  - کران بالا
- درست زیر کران پایین
- درست بالای کران بالا

MC/DC داریم.  
این یک مجموعه تست عالی است. دور قبل همین بود اما با چند تست غیرضروری.

##### متد *reverseArrayNumbers(int[], MyMath)*
دو متد تولید شده‌اند که شامل می‌شوند:
- یک تست معمولی
- تست آرایهٔ خالی

MC/DC محقق شد.  
آرایهٔ تک‌عضوی از قلم افتاده است. دور قبل آن را پوشش داده بود، و همچنین
چند edge case غیرضروری هم داشت.

#### واحد FileIO
```java
public class FileIOTestDeepSeek_NS {

  @TempDir
  Path tempDir;

  @Test
  void testReadFileWithMultipleLines() throws Exception {
    Path file = tempDir.resolve("names.txt");
    Files.write(file, "Alice\nBob\nCharlie".getBytes());

    FileIO fileIo = new FileIO();
    String[] result = fileIo.readFile(file.toString());
    assertArrayEquals(new String[]{"Alice", "Bob", "Charlie"}, result);
  }

  @Test
  void testReadFileEmptyFile() throws Exception {
    Path file = tempDir.resolve("empty.txt");
    Files.createFile(file);

    FileIO fileIo = new FileIO();
    String[] result = fileIo.readFile(file.toString());
    assertEquals(0, result.length);
  }

  @Test
  void testReadFileNonExistent() {
    FileIO fileIo = new FileIO();
    String[] result = fileIo.readFile("nonexistent.txt");
    assertEquals(0, result.length);
  }
}
```
سه متد تولید شده‌اند که شامل می‌شوند:
- یک تست معمولی
- یک تست فایل خالی
- فایل ناموجود

MC/DC محقق شد.
edge caseها به‌خوبی پوشش داده شده‌اند. عامل وقتی از skills استفاده کرد سه تست بیشتر
تولید کرده بود. اگرچه برخی از آن‌ها غیرضروری بودند.

#### واحد NameSearch
```java
public class NameSearchTestDeepSeek_NS {

  @TempDir
  Path tempDir;

  @Test
  void testFindNamesInFileThatStartWithMatchingPrefix() throws Exception {
    Path file = tempDir.resolve("names.txt");
    Files.write(file, "Alice\nBob\nCharlie\nAlex".getBytes());

    NameSearch ns = new NameSearch();
    String[] result = ns.findNamesInFileThatStartWith(
            file.toString(), new FileIO(), "Al");
    assertArrayEquals(new String[]{"Alice", "Alex"}, result);
  }

  @Test
  void testFindNamesInFileThatStartWithNoMatch() throws Exception {
    Path file = tempDir.resolve("names.txt");
    Files.write(file, "Bob\nCharlie".getBytes());

    NameSearch ns = new NameSearch();
    String[] result = ns.findNamesInFileThatStartWith(
            file.toString(), new FileIO(), "X");
    assertEquals(0, result.length);
  }

  @Test
  void testFindNamesInFileThatStartWithEmptyFile() throws Exception {
    Path file = tempDir.resolve("empty.txt");
    Files.createFile(file);

    NameSearch ns = new NameSearch();
    String[] result = ns.findNamesInFileThatStartWith(
            file.toString(), new FileIO(), "A");
    assertEquals(0, result.length);
  }
}

```
سه متد تولید شده‌اند که شامل می‌شوند:
- یک تست با چند نتیجه
- تست بدون تطابق
- تست فایل خالی

این تست به MC/DC می‌رسد.  
فاصلهٔ بین دورها بیشتر شده است. این دور باید تست تک‌نتیجه‌ای و
تست پیشوند خالی را پوشش می‌داد. دور قبل هشت تست داشت، اما برخی از آن‌ها غیرضروری بودند.


#### مقایسه
بعد از بررسی *MyMath* خیلی مطمئن بودم که skills هستند که عملکرد این عامل را
بالا برده‌اند، اما پس از آن واحد، فاصلهٔ بین دو دور خیلی کم شد!  
قطعاً ساختار دور قبل بسیار بهتر از این دور است، چون skills یک چارچوب
برای کار کردن مدل فراهم می‌کنند. همچنین در دور قبل edge caseهای بیشتری پوشش داده شد، اما برخی از آن‌ها
غیرضروری بودند.  
به‌طور پیش‌فرض، این مدل عالی است، ولی کاربران می‌توانند با skills آن را بیشتر بهبود دهند.
skills باعث شدند عامل مقادیر مرزی، edge caseها و ساختاری بسیار بهتر را پوشش دهد.
متأسفانه، تست‌های تکراری بیشتری تولید شد.


## نتیجه‌گیری نهایی
تعجب‌آور نیست که skills عملکرد عامل‌ها را بهبود دادند، به‌ویژه با فراهم کردن 
یک چارچوب برای کار کردنشان که به ساختار تست‌ها جهش چشمگیری داد.
عامل‌ها بدون هیچ promptی از تست‌های پارامتری استفاده کردند. بهبود در مقادیر مرزی اندک بود
که احتمالاً به حساب تست‌های پارامتری است. در تست edge case هم بهبودی صورت گرفت.  
تولید تست‌ها زمان بیشتری می‌برد چون عامل باید skill را هم بخواند و تست‌های تکراری بیشتری
تولید خواهد شد.  
استفاده از skills باید انجام شود اگر با مصرف توکن و زمان بیشتر برای تولید کد موافق باشید.
همان‌طور که دیدیم، DeepSeek برای تولید تست با دو skill پنج برابر بیشتر زمان برد.
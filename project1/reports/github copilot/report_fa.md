# آزمایش با GitHub Copilot در تولید تست واحد
## معرفی GitHub Copilot
GitHub Copilot یک مولد کد، تکمیل‌کننده کد و دستیار هوش مصنوعی برای برنامه‌نویسی است
که توسط GitHub (متعلق به Microsoft) توسعه و نگهداری می‌شود. می‌توان آن را
روی بسیاری از محیط‌های توسعه یکپارچه (IDE) مانند Visual Studio Code (VS Code)،
Visual Studio، Eclipse، محصولات JetBrains و غیره نصب کرد. در اکتبر ۲۰۲۱ منتشر شد.
این یک مدل نیست، بلکه به کاربر اجازه می‌دهد مدلی را برای تولید کد انتخاب کند.
تا این لحظه، کاربران می‌توانند مدل‌های بسیاری از ارائه‌دهندگان مختلف را انتخاب کنند.
این فهرست برخی مدل‌های موجود از ارائه‌دهندگان مختلف را در زمان نگارش این مارک‌داون نشان می‌دهد:
- Anthropic:
  - Claude Haiku 4.5
  - Claude Sonnet 4.6
  - Claude Opus 4.6
- Google:
  - Gemini 3 Flash
  - Gemini 3.1 Pro
- OpenAI:
  - GPT-5.1-Codex-Mini
  - GPT-5.3-Codex
- xAI:
  - Grok Code Fast 1

GitHub Copilot سه طرح در دسترس برای همه دارد. طرح رایگان کاربر را به مدل‌های پایه و
تکمیل کد بسیار محدود محدود می‌کند. همچنین یک طرح ویژه دانشجویی وجود دارد که به
دانشجویان اجازه می‌دهد رایگان از GitHub Copilot استفاده کنند، با محدودیتی سخاوتمندانه.
این ۳۰۰ توکن است که در ابتدای هر ماه بازنشانی می‌شود. هر گفتگو ۱ توکن را به‌علاوه ضریب
مصرف توکن مدل انتخاب‌شده مصرف می‌کند. برای مثال، ضریب GPT-5.1-Codex-Mini برابر ۰٫۳۳x
است در حالی که ضریب Claude Opus 4.6 برابر ۳x است.

### نحوه نصب روی Visual Studio Code
1. Visual Studio Code 2022 17.6 یا بالاتر را نصب کنید
2. اگر هنوز حساب GitHub ندارید، یک حساب بسازید.
3. افزونه **GitHub Copilot Chat** را نصب کنید. توجه داشته باشید که افزونه **GitHub Copilot**
    بیشتر بر تکمیل کد تمرکز دارد، در حالی که دیگری یک رابط چت‌بات معمولی فراهم می‌کند.

حالا باید آماده باشید.

### نحوه استفاده از GitHub Copilot
آیکون چت باید در نوار بالای Visual Studio Code ظاهر شده باشد. کلیک روی آن یک
زبانه چت را باز می‌کند. اینجا می‌توانید پرامپت خود را بنویسید، به نشست‌های قبلی خود دسترسی داشته باشید،
مدل را انتخاب کنید، نوع عامل (agent) را انتخاب کنید و نشست عامل را انتخاب کنید.

در فیلد پرامپت می‌توانید برخی از فایل‌های موجود در پوشه باز فعلی را ضمیمه کنید؛
این‌ها نقش افزودن زمینه (context) را ایفا می‌کنند. هرچند در صورت نیاز می‌تواند فایل‌های دیگر را هم برای زمینه بخواند.

اینجا می‌توانید مدل خود را انتخاب کنید. لازم است ذکر کنم که مدل‌ها پنجره‌های زمینه متفاوتی دارند.

سه عامل وجود دارد که می‌توانید انتخاب کنید:
1. Agent: در حالت agent فایل‌های فضای کاری شما را ویرایش می‌کند.
2. Ask: در زمینه (context) می‌پرسد یا ویرایش می‌کند.
3. Plan: پژوهش می‌کند و طرح‌های چندمرحله‌ای را ترسیم می‌کند.

چهار نشست عامل وجود دارد که می‌توانید انتخاب کنید:
1. Local: اجرای وظایف در چت VS Code. عامل از طریق چت تکرار می‌کند و به‌صورت تعاملی کار می‌کند تا
   تغییرات را در فضای کاری اصلی خود پیاده‌سازی کند.
2. Background: واگذاری وظایف به یک عامل پس‌زمینه که به‌صورت محلی روی ماشین شما اجرا می‌شود. عامل
   از طریق چت تکرار می‌کند و به‌صورت ناهمگام در یک Git worktree کار می‌کند تا تغییرات را
   جدا از فضای کاری اصلی، با استفاده از GitHub Copilot CLI پیاده‌سازی کند.
3. Cloud: واگذاری وظایف به عامل کدنویسی GitHub Copilot. عامل از طریق چت تکرار می‌کند و
   به‌صورت ناهمگام در فضای ابری کار می‌کند تا تغییرات و در صورت نیاز pull request را پیاده‌سازی کند.
4. Claude: واگذاری وظایف به Claude Agent SDK با استفاده از مدل‌های Claude موجود در
   اشتراک GitHub Copilot شما. عامل از طریق چت تکرار می‌کند و به‌صورت تعاملی کار می‌کند تا
   تغییرات را در فضای کاری اصلی خود پیاده‌سازی کند.

پس از اینکه پرامپت خود را نوشتید و گزینه‌ها را تنظیم کردید، کافی است دکمه
ارسال را کلیک کنید. در چت، عامل وضعیت فعلی خود را نشان می‌دهد؛ مثلاً اینکه آیا در حال
درک زمینه ارائه‌شده است، در حال تولید کد است و غیره.


## تولید تست‌های واحد با GitHub Copilot
حالا می‌توانیم تست‌های واحد تولید کنیم. از آنجا که این یک مدل نیست و شما مدل را انتخاب
می‌کنید و مدل‌ها از نظر اندازه، ارائه‌دهنده و میزان مصرف توکن با هم فرق دارند،
من سه مدل مختلف را با سه ضریب توکن متفاوت امتحان می‌کنم. این‌ها شامل می‌شوند:
- OpenAI GPT-5.1-Codex-Mini (۰٫۳۳x)
- Google Gemini 3.1 Pro (۱x)
- Anthropic Claude Opus 4.6 (۳x)

لازم است ذکر کنم که هیچ‌کدام از مدل‌های بالا تعداد پارامتر رسمی
ندارند.

همچنین به هر مدل پرامپت‌های متفاوتی می‌دهم تا ببینیم مهندسی پرامپت
چقدر می‌تواند روی تست‌های تولیدشده اثر بگذارد.

### تولید تست‌های واحد با OpenAI GPT-5.1-Codex-Mini
با ضعیف‌ترین مدل، OpenAI GPT-5.1-Codex-Mini شروع می‌کنیم. هر ۴ فایل را به‌عنوان
زمینه اضافه کردم و این پرامپت بسیار ساده را دادم:
> برای فایل‌های ارائه‌شده تست واحد تولید کن.

همان‌طور که گفتم این یک پرامپت بسیار ساده است. فقط ایده اصلی را ارائه کردم. حتی نگفتم
تست‌ها کجا ذخیره شوند، از چه ابزارهایی استفاده شود و تست‌ها چقدر پیچیده باشند.
پیش از ارسال پرامپت، تست‌های قبلی موجود در مخزن را جابه‌جا کردم
تا مدل از آن‌ها مرجع نگیرد یا آن‌ها را بازنویسی نکند.
مدل با خواندن هر چهار فایل شروع کرد، سپس پوشه‌های io، math و search را دقیقاً همان‌جا
که تست‌های خود پروژه بودند ایجاد کرد.

خودم GPT را به انتهای نام فایل‌ها و کلاس‌ها اضافه کردم.
بیایید فایل‌های تولیدشده را ببینیم. با ساده‌ترین آن‌ها، «MyMathTestGPT»، شروع می‌کنیم:
توجه داشته باشید که package و importها را از همه فایل‌ها در سراسر این گزارش حذف کرده‌ام.
#### واحد MyMathTest
```java
class MyMathTestGPT {

    private final MyMath math = new MyMath();

    @Test
    void checkSign_positiveNumber_returnsPositive() {
        assertEquals("positive", math.checkSign(7));
    }

    @Test
    void checkSign_negativeNumber_returnsNegative() {
        assertEquals("negative", math.checkSign(-12));
    }

    @Test
    void checkSign_zero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> math.checkSign(0));
    }

    @Test
    void reverseNumber_flipsSign() {
        assertEquals(-42, math.reverseNumber(42));
    }
}
```
نام تست‌ها نشان می‌دهد با چه سناریو و چه خروجی‌ای سروکار داریم. یک مشکل این است که
مدل با اضافه نکردن «تست» به ابتدای نام متدها اشتباه کرد؛ تا جایی که می‌دانم این یک قرارداد است.
همچنین در قرارداد نام‌گذاری متدهای جاوا معمولاً camelCase استفاده می‌شود. وضعیت نام‌گذاری
در همه تست‌های واحد تولیدشده یکسان است.

##### متد *checkSign(int)*
همه سه نتیجه ممکن پوشش داده شده‌اند.
عدد مثبت «positive» برمی‌گرداند، عدد منفی «negative» برمی‌گرداند و صفر استثنا پرتاب می‌کند.
پوشش statement، decision، condition، condition/decision و MC/DC را دارد.
این متد بسیار ساده است و همه نقاط تصمیم تنها یک شرط دارند، بنابراین
آسان بود. من شخصاً تست‌های بیشتری برای بررسی مقادیر مرزی
با ۱، -۱، Integer.MAX_VALUE و Integer.MIN_VALUE اضافه می‌کردم. در مجموع،
این متد ساده است و تست‌ها خوب هستند. در مقایسه با تست‌های موجود،
برابریم. تست‌های موجود نیز هر سه سناریو را با سه تست پوشش می‌دهند.

##### متد *reverseNumber(int)*
این متد تنها یک خط ساده کد دارد و هیچ نقطه تصمیمی ندارد.
یک تست تنها همه پوشش‌های گفته‌شده را پوشش داد. من شخصاً یک بررسی
برای عدد منفی و صفر هم اضافه می‌کردم تا سناریوهای بیشتری پوشش داده شود. بررسی مقادیر مرزی
هم خوب می‌بود.  
افزودن تست‌های بیشتر استحکام را افزایش می‌دهد، اما تست‌های بیشتر به
مدیریت، نگهداری و زمان تست سخت‌تر منجر می‌شود. در مقایسه با تست‌های موجود،
بدتر عمل کرد. در حالی که GPT تنها یک عدد مثبت را پوشش داد، تست‌های موجود
مثبت، منفی و صفر را با سه تست پوشش دادند.
#### واحد MyAdvancedMathTest
این یک واحد پیچیده‌تر است که شامل ۳ متد با حلقه و تصمیم چندشرطی است.
در اینجا فایل تولیدشده آمده است:
```java
class MyAdvancedMathTestGPT {

    private final MyAdvancedMath advancedMath = new MyAdvancedMath();

    @Test
    void add_positiveNumbers_returnsSum() {
        assertEquals(9, advancedMath.add(4, 5));
    }

    @Test
    void add_negativeInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(-1, 5));
    }

    @Test
    void add_overflow_throwsIllegalArgumentException() {
        int nearLimit = Integer.MAX_VALUE;
        assertThrows(IllegalArgumentException.class, () -> advancedMath.add(nearLimit, 1));
    }

    @Test
    void powerOfTwo_validInputs_returnExpectedResults() {
        assertEquals(1, advancedMath.powerOfTwo(0));
        assertEquals(1024, advancedMath.powerOfTwo(10));
    }

    @Test
    void powerOfTwo_tooLarge_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(31));
    }

    @Test
    void powerOfTwo_negative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> advancedMath.powerOfTwo(-3));
    }

    @Test
    void reverseArrayNumbers_flipsEachValue() {
        int[] values = {1, -2, 3};
        int[] copy = values.clone();

        int[] result = advancedMath.reverseArrayNumbers(copy, new MyMath());

        assertArrayEquals(new int[]{-1, 2, -3}, result);
        assertArrayEquals(result, copy);
    }
}
```
##### متد *add(int, int)*
ما برای این متد ۳ تست داریم.
یک تست عادی، یک تست سرریز و یک تست ورودی منفی.
همه خطوط کد اجرا می‌شوند (statement coverage).
تست‌ها همچنین پوشش condition دارند، اما پوشش decision ندارند، چون
در تست‌ها هرگز یک عدد منفی را به *y* نسبت نمی‌دهیم. اگر این پروژه پروژه‌ای بحرانی‌تر بود
که در آن یک نقص می‌توانست به آسیب شدید یا فاجعه‌بار منجر شود، این تست‌ها کافی نبودند.
برای یک متد add ساده، این تست‌ها خوب هستند.
من فکر می‌کنم یک مورد 0 + 0 و یک مورد Integer.MAX_VALUE + 0 هم باید تولید می‌شد.
در مقایسه با تست‌های
موجود، آن‌ها یک تست بیشتر دارند که 0 + 0 را پوشش می‌دهد و بررسی‌های
پیچیده‌تری برای استثناها با استفاده از **@Rule**. با آن، می‌توانید نه‌تنها بررسی کنید که کلاس استثنا
مطابقت دارد، بلکه می‌توانید بررسی کنید که پیام استثنا با پیامی که
انتظار دارید مطابقت دارد. برای مدل‌های بعدی، از عامل‌ها می‌خواهم از **@Rule** برای
تطبیق پیچیده‌تر، از جمله پیام‌های استثنا استفاده کنند. هرچند متوجه شدم که
بررسی پیام استثنا در تست واحد واقعاً خیلی مهم نیست.

##### متد *powerOfTwo(int)*
در ۳ متد، ۴ تست تولید شد.  
متدی که شامل دو تست است، حالت مرزی توان ۰ و یک تست عادی را پوشش می‌دهد.  
دو تست دیگر به IllegalArgumentException منجر می‌شوند؛ یکی توان منفی
و دیگری نتیجه‌ای است که در Integer جا نمی‌شود.  
تست‌ها پوشش MC/DC دارند.  
بررسی مقادیر مرزی مانند ۱، ۲۹ و ۳۰ جا افتاده است.  
در مقایسه با تست‌های موجود، آن‌ها فقط از یک
تست پارامتری استفاده کردند، هرچند تنها سه تست عادی را پوشش دادند و استثناهای
ممکن را مدیریت نکردند. برای مدل بعدی از آن‌ها می‌خواهم برای
این متد از تست‌های پارامتری استفاده کنند.

##### متد *reverseArrayNumbers(int[], MyMath)*
تنها یک تست با سه عنصر تولید شد که مقادیر مثبت و منفی را پوشش می‌داد.  
یک تست برای صفر جا افتاده است.  
برابری نتیجه را دو بار assert می‌کنیم، یک بار با نتیجه مورد انتظار و بار دوم
با بررسی آرایه پاس‌داده‌شده و آرایه بازگشتی (چون تابع روی آرایه مرجع اثر می‌گذارد).
یک تست برای آرایه خالی باید تولید می‌شد. این تست همه
پوشش‌های ذکرشده را دارد، چون تابع تنها یک حلقه ساده دارد.  
در تست‌های موجود، دو متد بود. یکی با پارامترهای عادی و دیگری با mock کردن MyMath.
ما کاری با mocking نداریم که قلمرو تست یکپارچه است.  
در این آزمایش‌ها، هرچند از دو مدل بعدی می‌خواهم از mocking استفاده کنند. باید زودتر
پرسیده بودم که آیا mocking را شامل کنم یا نه. من تست‌هایی را که از mocking استفاده کردند ارزیابی نخواهم کرد.
#### واحد FileIOTest
این واحد با فایل‌ها کار می‌کند. بیایید فایل تست تولیدشده را ببینیم:
```java
class FileIOTestGPT {

    @Test
    void readFile_returnsLinesInSameOrder(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        List<String> lines = List.of("Alice", "Bob", "Charlie");
        Files.write(file, lines, StandardCharsets.UTF_8);

        FileIO fileIO = new FileIO();
        String[] result = fileIO.readFile(file.toString());

        assertArrayEquals(lines.toArray(new String[0]), result);
    }

    @Test
    void readFile_emptyFile_returnsEmptyArray(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);

        FileIO fileIO = new FileIO();
        String[] result = fileIO.readFile(file.toString());

        assertEquals(0, result.length);
    }
}
```
برای تنها متد این کلاس، دو تست تولید شد. از یک پوشه موقت برای نگهداری
فایل در آن استفاده می‌کند. اولین تست یک سناریوی ساده است. تست دوم یک فایل خالی است.
ما به استثنا برنمی‌خوریم، پس پوشش statement نداریم و بنابراین به یک تست برای
مسیر فایل ناموجود نیاز داریم. بهتر بود
یک تست برای فاصله‌های خالی هم وجود داشت. در مقایسه با تست موجود،
ما بهتر عمل کردیم، چون آن‌ها از پوشه موقت
استفاده نکردند، بلکه از فایل ذخیره‌شده در *test/resources* استفاده کردند. آن تنها یک تست دارد در حالی که
تست دوم GPT سناریوی فایل خالی را پوشش می‌دهد.

#### واحد NameSearchTest
واحد پایانی. ببینیم GPT چه کرد:
```java
class NameSearchTestGPT {

    private final NameSearch search = new NameSearch();
    private final FileIO fileIO = new FileIO();

    @Test
    void findNamesInFileThatStartWith_returnsMatchingNames(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        List<String> names = List.of("Anna", "Bob", "Ansel", "Charlie");
        Files.write(file, names, StandardCharsets.UTF_8);

        String[] matches = search.findNamesInFileThatStartWith(file.toString(), fileIO, "An");

        assertArrayEquals(new String[]{"Anna", "Ansel"}, matches);
    }

    @Test
    void findNamesInFileThatStartWith_noMatches_returnsEmpty(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.write(file, List.of("Bob", "Charlie"), StandardCharsets.UTF_8);

        String[] matches = search.findNamesInFileThatStartWith(file.toString(), fileIO, "Z");

        assertEquals(0, matches.length);
    }
}
```
باز هم، دو تست برای یک متد. یک تست یک سناریوی عادی است و تست دیگر یک
نتیجه خالی. اینجا از *fileIO* برای خواندن فایل استفاده می‌کنیم، پس یک وابستگی داریم، اما
تمرکزمان روی یکپارچه‌سازی آن در این واحد نیست.  
می‌گویم این بدترین تست‌های تولیدشده برای یک واحد بود. چند مورد مرزی وجود دارد که GPT از دست داد:
- فایل خالی
- پیشوند خالی
- آرگومان‌های null

در مقایسه با تست‌های موجود در مخزن، GPT بهتر عمل کرد. یک تست بیشتر با یک مورد مرزی تولید کرد
و از یک پوشه موقت استفاده کرد.

#### نتیجه‌گیری
با یک مدل ضعیف و یک پرامپت ساده. به نظر می‌رسد تست‌های تولیدشده خوب هستند. می‌توانم
فرض کنم حتی با این مدل ضعیف، یک پرامپت خوب تفاوت بزرگی ایجاد می‌کرد.
با مدل بعدی، که روی کاغذ بهتر است و با پرامپت پیچیده‌تر،
انتظار دارم بهبود محسوسی در تست‌های تولیدشده ببینم.


### تولید تست‌های واحد با Google Gemini 3.1 Pro
برویم سراغ یک مدل بهتر. ضریب مصرف توکن این مدل
۱x است، ۳ برابر بیشتر از مدل قبلی. من به این مدل پرامپتی پیچیده‌تر می‌دهم که
عمدتاً کاستی‌های مدل قبلی، از جمله بررسی مقادیر مرزی را اضافه می‌کند. پرامپت این است:

> برای فایل‌های افزوده‌شده در زمینه تست واحد ایجاد کن.
> برای بررسی پیچیده‌تر، از جمله تطبیق پیام‌های استثنا، از @Rule استفاده کن.
> برای MyAdvancedMathTest.powerOfTwo، یک تست پارامتری اضافه کن.
> برای reverseArrayNumbers، از mock کردن MyMath استفاده کن.
> برای findNamesInFileThatStartWith، از mock کردن FileIO استفاده کن.
> بررسی مقادیر مرزی را اضافه کن.
> در صورت نیاز از test suite استفاده کن.
> پسوند "Gemini" را به همه کلاس‌های تست تولیدشده اضافه کن.

همان‌طور که می‌بینید، پرامپت جزئیات و دستورهای بسیار بیشتری دارد.
همه واحدها را به‌عنوان زمینه اضافه کردم. اینجا از @Rule برای تست
بهتر استثنا استفاده می‌کنیم که اکنون آن را غیرضروری می‌دانم.
همچنین از یک تست پارامتری استفاده می‌کنیم. برای دو متد
که وابستگی دارند، تست‌های تولیدشده با mocking نادیده گرفته می‌شوند.
مدل قبلی برای
بررسی مقادیر مرزی کم کار کرد، بنابراین آن را هم در پرامپت آوردم.
از عامل خواستم در صورت نیاز از test suite استفاده کند. برای شناسایی
همه تست‌های تولیدشده، همچنین از عامل خواستم پسوند *Gemini* را
اضافه کند تا بدانیم این تست‌ها توسط Gemini 3.1 Pro تولید شده‌اند.  

پیش از ارسال پرامپت، همه تست‌های موجود را به یک مکان
موقت منتقل کردم. پیام را فرستادم و عامل شروع به کار کرد. ابتدا
فایل‌های maven را خواند، سپس شروع به تولید تست‌ها در
پوشه استاندارد کرد. تولید همه تست‌ها کمتر از ۲ دقیقه طول کشید.
مدل قبلی بیشتر طول کشید. اما می‌توانم آن را به اتصال اینترنت خود نسبت دهم
چون اولین پرامپت با خطای اتصال مواجه شد. تست‌ها را با
**JUnit 4** و **Mockito** برای mocking تولید کرد. بیایید تست‌های واحد
تولیدشده را ببینیم. آن‌ها را به همان ترتیب قبلی بررسی می‌کنم. یک مشکل
که متوجه شدم این است که مدل از "Test" در نام‌ها استفاده نکرد، که
احتمالاً به این دلیل بود که از آن خواستم پسوند "Gemini" را اضافه کند. همچنین نام
متدها اکنون با test شروع می‌شوند، GPT از این قرارداد پیروی نکرد.

#### واحد MyMath
```java
public class MyMathGemini {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCheckSignPositive() {
        MyMath myMath = new MyMath();
        Assert.assertEquals("positive", myMath.checkSign(5));
    }

    @Test
    public void testCheckSignNegative() {
        MyMath myMath = new MyMath();
        Assert.assertEquals("negative", myMath.checkSign(-5));
    }

    @Test
    public void testCheckSignZero() {
        MyMath myMath = new MyMath();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Number had no sign");
        myMath.checkSign(0);
    }

    @Test
    public void testReverseNumber() {
        MyMath myMath = new MyMath();
        Assert.assertEquals(-5, myMath.reverseNumber(5));
        Assert.assertEquals(5, myMath.reverseNumber(-5));
        Assert.assertEquals(0, myMath.reverseNumber(0));
    }
}
```
##### متد *checkSign(int)*.  
سه تست تولید شد.
با یک عدد مثبت، یک عدد منفی و صفر. متأسفانه،
تست بررسی مقادیر مرزی مانند ۱ و -۱ را تولید نکرد، حتی وقتی از آن خواستم این کار را بکند.
اگر یک تست جهش (mutation test) انجام دهیم و
*( number > 0 )* به *( number > 1 )* تغییر کند، تست‌ها این را پاس می‌دهند.
پوشش MC/DC را دارد.

##### متد *reverseNumber(int)*
یک متد با سه assert تولید شد. مانند متد قبلی، هیچ بررسی مرزی وجود ندارد،
به‌ویژه Integer.MIN_VALUE که باعث استثنای سرریز می‌شود.

در مجموع، تست‌های تولیدشده از بررسی مقادیر مرزی ناقص‌اند، هرچند من آن را در پرامپت آورده بودم.

فهمیدم که @Rule در JUnit 5 منسوخ شده است.
اینجا از آن برای بررسی اینکه آیا پیام استثنا با آنچه انتظار می‌رود مطابقت دارد استفاده شده،
در مدل بعدی از @Rule استفاده نمی‌کنم. (این به این دلیل بود که من
نمی‌دانستم بررسی پیام استثنا اهمیت زیادی ندارد)

#### واحد MyAdvancedMath
از آنجا که از عامل خواستم برای یک تابع در این
کلاس از تست پارامتری استفاده کند، دو فایل داریم:
```java
public class MyAdvancedMathGemini {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAddNormal() {
        MyAdvancedMath math = new MyAdvancedMath();
        Assert.assertEquals(5, math.add(2, 3));
    }

    @Test
    public void testAddNegativeX() {
        MyAdvancedMath math = new MyAdvancedMath();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Input numbers should be positive.");
        math.add(-1, 5);
    }

    @Test
    public void testAddNegativeY() {
        MyAdvancedMath math = new MyAdvancedMath();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Input numbers should be positive.");
        math.add(5, -1);
    }

    @Test
    public void testAddOverflow() {
        MyAdvancedMath math = new MyAdvancedMath();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot fit the result in an Integer.");
        math.add(Integer.MAX_VALUE, 1);
    }

    @Test
    public void testPowerOfTwoNegative() {
        MyAdvancedMath math = new MyAdvancedMath();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("power should be 0<=power<=30");
        math.powerOfTwo(-1);
    }

    @Test
    public void testPowerOfTwoGreaterThan30() {
        MyAdvancedMath math = new MyAdvancedMath();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("power should be 0<=power<=30");
        math.powerOfTwo(31);
    }

    @Test
    public void testReverseArrayNumbers() {
        MyAdvancedMath math = new MyAdvancedMath();
        MyMath myMathMock = mock(MyMath.class);
        when(myMathMock.reverseNumber(1)).thenReturn(-1);
        when(myMathMock.reverseNumber(-2)).thenReturn(2);
        when(myMathMock.reverseNumber(3)).thenReturn(-3);

        int[] input = {1, -2, 3};
        int[] expected = {-1, 2, -3};
        int[] result = math.reverseArrayNumbers(input, myMathMock);

        Assert.assertArrayEquals(expected, result);
        verify(myMathMock, times(1)).reverseNumber(1);
        verify(myMathMock, times(1)).reverseNumber(-2);
        verify(myMathMock, times(1)).reverseNumber(3);
    }
}
```
```java
@RunWith(Parameterized.class)
public class MyAdvancedMathPowerOfTwoParameterizedGemini {

    private int power;
    private int expectedResult;

    public MyAdvancedMathPowerOfTwoParameterizedGemini(int power, int expectedResult) {
        this.power = power;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 1},
                {1, 2},
                {2, 4},
                {3, 8},
                {4, 16},
                {10, 1024},
                {30, 1073741824}
        });
    }

    @Test
    public void testPowerOfTwo() {
        MyAdvancedMath math = new MyAdvancedMath();
        Assert.assertEquals(expectedResult, math.powerOfTwo(power));
    }
}
```
تست‌های بیشتری تولید شد. به‌ویژه یک تست پارامتری و یک تست با mocking.

##### متد *add(int, int)*
۴ تست وجود دارد. یک تست عادی، دو تست با آرگومان منفی و
یک تست برای سرریز. به نظر من یک تست *0 + 0* جا افتاده است. شاید می‌شد
تست‌های بیشتری با پوشش مقادیر مرزی، مانند *Integer.MAX_VALUE + 0*، داشت.

##### متد *powerOfTwo(int)*
عامل ۴ متد تست تولید کرد که -۱ و
۳۱ را به‌عنوان مقادیر مرزی پوشش می‌دهد. بهتر است ۰، ۱ و ۳۰ که در
تست‌های پارامتری بیرون از آن موجود هستند، به‌صورت توابع جداگانه برای آزمودن
صریح مرزها وجود داشته باشند و برای تست پارامتری فقط چند مقدار
معمولی داده شود.

##### reverseArrayNumbers(int[] array, MyMath mm)
از mocking استفاده شده است. تنها یک تست داریم که باعث
نادیده‌ماندن بسیاری از موارد مرزی مانند آرایه خالی، آرایه null و غیره می‌شود. حتی
این تست هم شامل یک صفر برای آزمون نیست. در مجموع، تست
تولیدشده برای این متد افتضاح بود. حتی با یک پرامپت پیچیده و
با وجود قوی‌تر بودن مدل، دقیقاً مثل GPT عمل کرد!

#### FileIO
```java
public class FileIOGemini {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testReadFileValid() {
        FileIO fileIO = new FileIO();
        // Using the existing names_valid.txt in src/test/resources
        String filepath = "src/test/resources/names_valid.txt";
        String[] result = fileIO.readFile(filepath);
        Assert.assertNotNull(result);
    }

    @Test
    public void testReadFileInvalid() {
        FileIO fileIO = new FileIO();
        String filepath = "src/test/resources/does_not_exist.txt";
        String[] result = fileIO.readFile(filepath);
        Assert.assertEquals(0, result.length);
    }
}
```
برای این کلاس با تنها یک متد، دو تست تولید شد.
از فایل موجود *names_valid.txt* برای یک تست استفاده کرد و
برای تست دیگر، از فایلی استفاده کرد که وجود ندارد. ما به
هیچ استثنایی برنمی‌خوریم، اما از ExpectedException استفاده می‌کند؟
تست اول فقط بررسی می‌کند که نتیجه null نباشد، در حالی که
باید بررسی کند که آرایه نتیجه با آنچه انتظار داریم برابر است.

تست دوم مسیر فایلی را می‌دهد که وجود ندارد. متد
به استثنا برمی‌خورد و یک آرایه خالی برمی‌گرداند. بررسی
اندازه آرایه با ۰ احتمالاً بهینه‌تر از assert کردن
برابری دو آرایه خالی است.

بسیاری از موارد مرزی پوشش داده نشدند. عامل در این واحد ضعیف عمل کرد.
این مورد برای GPT هم صادق بود.

#### NameSearch
```java
public class NameSearchGemini {

    @Test
    public void testFindNamesInFileThatStartWith() {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIOMock = mock(FileIO.class);
        
        String filepath = "dummy_path.txt";
        String[] mockNames = {"Alice", "Bob", "Charlie", "Alex", "David"};
        when(fileIOMock.readFile(filepath)).thenReturn(mockNames);

        String[] expected = {"Alice", "Alex"};
        String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIOMock, "Al");

        Assert.assertArrayEquals(expected, result);
        verify(fileIOMock, times(1)).readFile(filepath);
    }

    @Test
    public void testFindNamesInFileThatStartWithNoMatch() {
        NameSearch nameSearch = new NameSearch();
        FileIO fileIOMock = mock(FileIO.class);
        
        String filepath = "dummy_path.txt";
        String[] mockNames = {"Alice", "Bob", "Charlie", "Alex", "David"};
        when(fileIOMock.readFile(filepath)).thenReturn(mockNames);

        String[] expected = {};
        String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIOMock, "Z");

        Assert.assertArrayEquals(expected, result);
        verify(fileIOMock, times(1)).readFile(filepath);
    }
}
```
دو متد تولید شد. در مجموع، کیفیت تست‌ها ضعیف است.
تنها یک مورد مرزی برای نتیجه خالی پوشش داده شد، در حالی که فایل خالی
و پسوند خالی هم باید بررسی می‌شدند. آرگومان‌ها
همچنین می‌توانستند null باشند.

#### نتیجه‌گیری
حتی با پرامپت پیچیده‌تر، این تست‌های تولیدشده انتظارات
من را برآورده نکردند. تست‌های بیشتری باید انجام شود تا فهمیده شود مشکل خودِ مدل است.
مدل آخر را برای کمال خواستم و انتظارم را بسیار بالاتر نگه می‌دارم.


### تولید تست‌های واحد با Anthropic Claude Opus 4.6
> برای فایل‌های افزوده‌شده در زمینه تست واحد ایجاد کن. از JUnit 5 استفاده کن.
مطمئن شو تست‌ها برای هر متد با هم پوشش MC/DC دارند.
در هر تصمیم یک تست مرزی محکم انجام بده. مطمئن شو مقداری دقیقاً بالای مرز، دقیقاً روی مرز و دقیقاً زیر مرز پوشش داده می‌شود.
تست‌هایی با کمترین و بیشترین مقدار integer یا سایر انواع مقادیر را شامل شو، به‌ویژه اگر بتواند به‌طور منفی روی کارکرد تأثیر بگذارد، مانند سرریز.
اگر تستی به استثنا منجر شد، پیام استثنا را هم assert کن.
در صورت نیاز تست پارامتری را شامل شو.
اگر یک واحد وابستگی دارد mocking را شامل شو (از mocking در همه متدهای تست استفاده نکن).
همه موارد مرزی ممکن مانند آرایه خالی، آرگومان null، همه حالت‌های ممکن IO و غیره را پوشش بده.
در صورت نیاز از test suite استفاده کن.
نام کلاس‌های تولیدشده باید با "ClaudeTest" تمام شود.
بهترین تلاش خود را بکن، خودت چیزهایی را که در پرامپت جا انداخته‌ام پیدا کن.
تست‌های تولیدشده باید ۱۰ از ۱۰ باشند، کمال!

حدود ۱۰ دقیقه طول کشید تا عامل همه تست‌ها را تولید کند.
این مدل همیشه برای تکمیل خروجی‌اش زمان زیادی می‌برد.
۱۴۲۹ خط تولید کرد (شامل کامنت‌ها، کدها و فاصله‌های خالی)!
مدل با خواندن واحدها شروع کرد، سپس یک فهرست todo
برای خودش ساخت، بعد همه فایل‌های تست را تولید کرد.
همچنین یک test suite تولید کرد.

انتظار من بسیار بالا است؛ نه‌تنها از بهترین مدل
موجود استفاده می‌کنیم، بلکه کمال را هم خواسته‌ام. هرچند ۱۴۲۹ خط بسیار زیاد است.
این ممکن است افراط باشد.

#### MyMath
```java
/**
 * Comprehensive unit tests for {@link MyMath} with MC/DC coverage,
 * boundary value analysis, and edge case testing.
 */
@DisplayName("MyMath Tests")
public class MyMathClaudeTest {

    private MyMath myMath;

    @BeforeEach
    void setUp() {
        myMath = new MyMath();
    }

    // =========================================================================
    // checkSign(int number) tests
    // =========================================================================
    @Nested
    @DisplayName("checkSign")
    class CheckSignTests {

        // --- MC/DC: Decision 1: number > 0 ---
        // Condition: number > 0 is a single condition.
        // true  → "positive"
        // false → falls through to next decision

        // --- MC/DC: Decision 2: number < 0 (reached when number <= 0) ---
        // Condition: number < 0 is a single condition.
        // true  → "negative"
        // false → exception (number == 0)

        @Test
        @DisplayName("returns 'positive' for positive number (MC/DC: number>0 = true)")
        void checkSign_positiveNumber_returnsPositive() {
            assertEquals("positive", myMath.checkSign(5));
        }

        @Test
        @DisplayName("returns 'negative' for negative number (MC/DC: number>0 = false, number<0 = true)")
        void checkSign_negativeNumber_returnsNegative() {
            assertEquals("negative", myMath.checkSign(-5));
        }

        @Test
        @DisplayName("throws IllegalArgumentException for zero (MC/DC: number>0 = false, number<0 = false)")
        void checkSign_zero_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myMath.checkSign(0)
            );
            assertEquals("Number had no sign", ex.getMessage());
        }

        // --- Boundary value analysis around 0 ---

        @Test
        @DisplayName("boundary: number = 1 (just above 0)")
        void checkSign_justAboveZero_returnsPositive() {
            assertEquals("positive", myMath.checkSign(1));
        }

        @Test
        @DisplayName("boundary: number = 0 (at boundary)")
        void checkSign_atZero_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myMath.checkSign(0));
        }

        @Test
        @DisplayName("boundary: number = -1 (just below 0)")
        void checkSign_justBelowZero_returnsNegative() {
            assertEquals("negative", myMath.checkSign(-1));
        }

        // --- Extreme values ---

        @Test
        @DisplayName("Integer.MAX_VALUE returns 'positive'")
        void checkSign_intMaxValue_returnsPositive() {
            assertEquals("positive", myMath.checkSign(Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("Integer.MIN_VALUE returns 'negative'")
        void checkSign_intMinValue_returnsNegative() {
            assertEquals("negative", myMath.checkSign(Integer.MIN_VALUE));
        }

        // --- Parameterized positive values ---
        @ParameterizedTest(name = "checkSign({0}) = positive")
        @ValueSource(ints = {1, 2, 10, 100, 1000, Integer.MAX_VALUE})
        @DisplayName("parameterized: various positive numbers")
        void checkSign_variousPositiveNumbers_returnsPositive(int number) {
            assertEquals("positive", myMath.checkSign(number));
        }

        // --- Parameterized negative values ---
        @ParameterizedTest(name = "checkSign({0}) = negative")
        @ValueSource(ints = {-1, -2, -10, -100, -1000, Integer.MIN_VALUE})
        @DisplayName("parameterized: various negative numbers")
        void checkSign_variousNegativeNumbers_returnsNegative(int number) {
            assertEquals("negative", myMath.checkSign(number));
        }
    }

    // =========================================================================
    // reverseNumber(int number) tests
    // =========================================================================
    @Nested
    @DisplayName("reverseNumber")
    class ReverseNumberTests {

        @Test
        @DisplayName("reverses a positive number to negative")
        void reverseNumber_positiveNumber_returnsNegative() {
            assertEquals(-5, myMath.reverseNumber(5));
        }

        @Test
        @DisplayName("reverses a negative number to positive")
        void reverseNumber_negativeNumber_returnsPositive() {
            assertEquals(5, myMath.reverseNumber(-5));
        }

        @Test
        @DisplayName("reverses zero to zero")
        void reverseNumber_zero_returnsZero() {
            assertEquals(0, myMath.reverseNumber(0));
        }

        // --- Boundary values ---

        @Test
        @DisplayName("boundary: number = 1")
        void reverseNumber_one_returnsMinusOne() {
            assertEquals(-1, myMath.reverseNumber(1));
        }

        @Test
        @DisplayName("boundary: number = -1")
        void reverseNumber_minusOne_returnsOne() {
            assertEquals(1, myMath.reverseNumber(-1));
        }

        // --- Extreme / overflow edge cases ---

        @Test
        @DisplayName("Integer.MAX_VALUE reversal")
        void reverseNumber_intMaxValue_returnsNegative() {
            assertEquals(-Integer.MAX_VALUE, myMath.reverseNumber(Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("Integer.MIN_VALUE reversal causes overflow (stays MIN_VALUE)")
        void reverseNumber_intMinValue_overflows() {
            // -Integer.MIN_VALUE overflows in two's complement,
            // resulting in Integer.MIN_VALUE itself.
            // This is a known integer overflow edge case.
            assertEquals(Integer.MIN_VALUE, myMath.reverseNumber(Integer.MIN_VALUE));
        }

        @Test
        @DisplayName("-(MAX_VALUE) reversal returns MAX_VALUE")
        void reverseNumber_negativeMaxValue_returnsMaxValue() {
            assertEquals(Integer.MAX_VALUE, myMath.reverseNumber(-Integer.MAX_VALUE));
        }

        // --- Parameterized: verify reverse is an involution (f(f(x)) = x) ---
        @ParameterizedTest(name = "reverseNumber(reverseNumber({0})) = {0}")
        @ValueSource(ints = {0, 1, -1, 42, -42, 1000, -1000, Integer.MAX_VALUE})
        @DisplayName("parameterized: double reversal is identity")
        void reverseNumber_doubleReversal_returnsOriginal(int number) {
            assertEquals(number, myMath.reverseNumber(myMath.reverseNumber(number)));
        }
    }
}
```
در مقایسه با مدل‌های قبلی، متدهای تست بسیار بیشتری تولید شد.
از کلاس تودرتو برای بررسی هر تابع در یک کلاس متناظر استفاده می‌کند،
جالب است. من قبلاً این الگو را ندیده بودم. همچنین از یک *@beforeEach*
برای نمونه‌سازی MyMath استفاده می‌شود. این کار اندازه کد را کاهش می‌دهد. این
برای دیگر تست‌های واحد تولیدشده هم استفاده شده است.

##### متد *checkSign(int)*
۱۰ تست تولید شد، شامل ۲ تست پارامتری. تست‌ها شامل موارد زیر هستند:
- یک عدد مثبت عادی (۵)
- یک عدد منفی عادی (-۵)
- صفر که باعث استثنا می‌شود، پیام استثنا هم assert می‌شود
- درست بالای مرز ۰ (۱)
- روی مرز (۰) یک تکرار داریم، به نظر می‌رسد
- درست زیر مرز ۰ (-۱)
- بیشینه عدد ۳۲ بیتی (۲۱۴۷۴۸۳۶۴۷)
- کمینه عدد ۳۲ بیتی (-۲۱۴۷۴۸۳۶۴۸)
- تست پارامتری ۵ عدد مثبت، (۱) با «درست بالای مرز» تکراری است
- تست پارامتری ۵ عدد منفی، (-۱) با «درست زیر مرز» تکراری است

جز داشتن ۳ مورد تکراری، می‌گویم اینجا به کمال رسیدیم.
همان‌طور که در کامنت‌ها آمده، پوشش MC/DC به دست آمده.
کمال قطعاً به قیمت افزونگی و افراط در تست واحد تمام شد.

##### متد *reverseNumber(int)*
مدل ۹ تست تولید کرد، شامل یک
تست پارامتری. تست‌ها شامل موارد زیر هستند:
- یک عدد مثبت عادی (۵)
- یک عدد منفی عادی (-۵)
- صفر که خروجی‌اش برابر ورودی است
- درست بالای مرز (۱)
- درست زیر مرز (-۱)
- بیشینه عدد ۳۲ بیتی (۲۱۴۷۴۸۳۶۴۷)
- کمینه عدد ۳۲ بیتی (-۲۱۴۷۴۸۳۶۴۸) که به سرریز منجر می‌شود
- معکوس بیشینه عدد ۳۲ بیتی (-۲۱۴۷۴۸۳۶۴۷)
- یک تست پارامتری که assert می‌کند معکوسِ معکوسِ عدد برابر خودش است.

باز هم، چند تست تکراری مثل ۱، ۰ و -۱ وجود دارد. بهتر است
این مقادیر مرزی را در تست‌های جداگانه داشته باشیم نه در یک تست
پارامتری. MC/DC محقق شده است. مرزها هم به‌شدت بررسی شده‌اند.


#### MyAdvancedMath
در مورد افراط، به یاد بیاورید وقتی مدل‌های قبلی کل ۶ متد تولید می‌کردند،
اینجا بیش از ۵۰ تا داریم!!!
```java
package dmst.seip.angor.junitdemo.math;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive unit tests for {@link MyAdvancedMath} with MC/DC coverage,
 * boundary value analysis, edge case testing, and mocking.
 */
@DisplayName("MyAdvancedMath Tests")
public class MyAdvancedMathClaudeTest {

    private MyAdvancedMath myAdvancedMath;

    @BeforeEach
    void setUp() {
        myAdvancedMath = new MyAdvancedMath();
    }

    // =========================================================================
    // add(int x, int y) tests
    // =========================================================================
    @Nested
    @DisplayName("add")
    class AddTests {

        // --- MC/DC for Decision 1: (x < 0 || y < 0) ---
        // To achieve MC/DC for a 2-condition OR:
        //   TC1: x<0=T, y<0=F → decision=T  (x independently causes true)
        //   TC2: x<0=F, y<0=T → decision=T  (y independently causes true)
        //   TC3: x<0=F, y<0=F → decision=F  (both false → false)

        @Test
        @DisplayName("MC/DC: x<0 independently causes exception (x<0=T, y<0=F)")
        void add_negativeXPositiveY_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(-1, 5)
            );
            assertEquals("Input numbers should be positive.", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: y<0 independently causes exception (x<0=F, y<0=T)")
        void add_positiveXNegativeY_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(5, -1)
            );
            assertEquals("Input numbers should be positive.", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: both non-negative → no exception (x<0=F, y<0=F)")
        void add_bothNonNegative_returnsSum() {
            assertEquals(10, myAdvancedMath.add(5, 5));
        }

        @Test
        @DisplayName("MC/DC: both negative → exception (x<0=T, y<0=T)")
        void add_bothNegative_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(-3, -7)
            );
            assertEquals("Input numbers should be positive.", ex.getMessage());
        }

        // --- MC/DC for Decision 2: y > Integer.MAX_VALUE - x (overflow check) ---
        // Reached when x>=0 && y>=0.
        // Single condition, so T/F covers MC/DC.

        @Test
        @DisplayName("MC/DC: overflow condition true → exception")
        void add_overflowDetected_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(Integer.MAX_VALUE, 1)
            );
            assertEquals("Cannot fit the result in an Integer.", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: overflow condition false → returns sum")
        void add_noOverflow_returnsSum() {
            assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(Integer.MAX_VALUE, 0));
        }

        // --- Boundary: x around 0 ---

        @Test
        @DisplayName("boundary: x = -1 (just below 0), y = 0 → exception")
        void add_xJustBelowZero_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(-1, 0));
        }

        @Test
        @DisplayName("boundary: x = 0, y = 0 → returns 0")
        void add_xAtZeroYAtZero_returnsZero() {
            assertEquals(0, myAdvancedMath.add(0, 0));
        }

        @Test
        @DisplayName("boundary: x = 1 (just above 0), y = 0 → returns 1")
        void add_xJustAboveZero_returnsOne() {
            assertEquals(1, myAdvancedMath.add(1, 0));
        }

        // --- Boundary: y around 0 ---

        @Test
        @DisplayName("boundary: x = 0, y = -1 (just below 0) → exception")
        void add_yJustBelowZero_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.add(0, -1));
        }

        @Test
        @DisplayName("boundary: x = 0, y = 1 (just above 0) → returns 1")
        void add_yJustAboveZero_returnsOne() {
            assertEquals(1, myAdvancedMath.add(0, 1));
        }

        // --- Boundary: overflow boundary (y = MAX_VALUE - x) ---

        @Test
        @DisplayName("boundary: y exactly at overflow limit → no exception")
        void add_yExactlyAtOverflowLimit_returnsSum() {
            // x=1, y=MAX_VALUE-1 → y == MAX_VALUE-x → just within bounds
            assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(1, Integer.MAX_VALUE - 1));
        }

        @Test
        @DisplayName("boundary: y one above overflow limit → exception")
        void add_yOneAboveOverflowLimit_throwsException() {
            // x=1, y=MAX_VALUE → y > MAX_VALUE-1 → overflow
            assertThrows(IllegalArgumentException.class,
                    () -> myAdvancedMath.add(1, Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("boundary: y one below overflow limit → sum OK")
        void add_yOneBelowOverflowLimit_returnsSum() {
            // x=2, y=MAX_VALUE-3 → y < MAX_VALUE-2 → within bounds
            assertEquals(Integer.MAX_VALUE - 1, myAdvancedMath.add(2, Integer.MAX_VALUE - 3));
        }

        // --- Extreme values ---

        @Test
        @DisplayName("both MAX_VALUE → overflow exception")
        void add_bothMaxValue_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> myAdvancedMath.add(Integer.MAX_VALUE, Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("MAX_VALUE + 0 = MAX_VALUE (no overflow)")
        void add_maxValuePlusZero_returnsMaxValue() {
            assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(Integer.MAX_VALUE, 0));
        }

        @Test
        @DisplayName("0 + MAX_VALUE = MAX_VALUE (no overflow)")
        void add_zeroPlusMaxValue_returnsMaxValue() {
            assertEquals(Integer.MAX_VALUE, myAdvancedMath.add(0, Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("x = MIN_VALUE → exception (negative)")
        void add_xMinValue_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> myAdvancedMath.add(Integer.MIN_VALUE, 0));
        }

        @Test
        @DisplayName("y = MIN_VALUE → exception (negative)")
        void add_yMinValue_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> myAdvancedMath.add(0, Integer.MIN_VALUE));
        }

        // --- Parameterized: valid additions ---

        @ParameterizedTest(name = "add({0}, {1}) = {2}")
        @CsvSource({
                "0, 0, 0",
                "1, 1, 2",
                "0, 5, 5",
                "5, 0, 5",
                "100, 200, 300",
                "1000000, 2000000, 3000000"
        })
        @DisplayName("parameterized: valid additions return correct sum")
        void add_validInputs_returnsCorrectSum(int x, int y, int expected) {
            assertEquals(expected, myAdvancedMath.add(x, y));
        }

        // --- Parameterized: negative input exceptions ---

        @ParameterizedTest(name = "add({0}, {1}) throws exception")
        @CsvSource({
                "-1, 0",
                "0, -1",
                "-1, -1",
                "-100, 50",
                "50, -100"
        })
        @DisplayName("parameterized: negative inputs throw exception")
        void add_negativeInputs_throwsException(int x, int y) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.add(x, y)
            );
            assertEquals("Input numbers should be positive.", ex.getMessage());
        }
    }

    // =========================================================================
    // powerOfTwo(int power) tests
    // =========================================================================
    @Nested
    @DisplayName("powerOfTwo")
    class PowerOfTwoTests {

        // --- MC/DC for Decision 1: power == 0 ---
        // Single condition: true → return 1, false → continue

        @Test
        @DisplayName("MC/DC: power == 0 → returns 1")
        void powerOfTwo_zero_returnsOne() {
            assertEquals(1, myAdvancedMath.powerOfTwo(0));
        }

        @Test
        @DisplayName("MC/DC: power != 0, valid → computes result")
        void powerOfTwo_nonZeroValid_returnsResult() {
            assertEquals(2, myAdvancedMath.powerOfTwo(1));
        }

        // --- MC/DC for Decision 2: (power < 0 || power > 30) ---
        // Reached when power != 0.
        // TC1: power<0=T, power>30=F → decision=T (power<0 independently causes true)
        // TC2: power<0=F, power>30=T → decision=T (power>30 independently causes true)
        // TC3: power<0=F, power>30=F → decision=F (both false)

        @Test
        @DisplayName("MC/DC: power<0 independently causes exception (power<0=T, power>30=F)")
        void powerOfTwo_negative_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(-1)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: power>30 independently causes exception (power<0=F, power>30=T)")
        void powerOfTwo_above30_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(31)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }

        @Test
        @DisplayName("MC/DC: both conditions false → computes (power<0=F, power>30=F)")
        void powerOfTwo_withinRange_returnsResult() {
            assertEquals(1024, myAdvancedMath.powerOfTwo(10));
        }

        // --- Boundary at 0: -1, 0, 1 ---

        @Test
        @DisplayName("boundary: power = -1 (just below 0) → exception")
        void powerOfTwo_justBelowZero_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(-1));
        }

        @Test
        @DisplayName("boundary: power = 0 → returns 1")
        void powerOfTwo_atZero_returnsOne() {
            assertEquals(1, myAdvancedMath.powerOfTwo(0));
        }

        @Test
        @DisplayName("boundary: power = 1 (just above 0) → returns 2")
        void powerOfTwo_justAboveZero_returnsTwo() {
            assertEquals(2, myAdvancedMath.powerOfTwo(1));
        }

        // --- Boundary at 30: 29, 30, 31 ---

        @Test
        @DisplayName("boundary: power = 29 (just below 30) → returns 2^29")
        void powerOfTwo_justBelow30_returnsResult() {
            assertEquals(536870912, myAdvancedMath.powerOfTwo(29)); // 2^29
        }

        @Test
        @DisplayName("boundary: power = 30 (at upper bound) → returns 2^30")
        void powerOfTwo_at30_returnsResult() {
            assertEquals(1073741824, myAdvancedMath.powerOfTwo(30)); // 2^30
        }

        @Test
        @DisplayName("boundary: power = 31 (just above 30) → exception")
        void powerOfTwo_justAbove30_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> myAdvancedMath.powerOfTwo(31));
        }

        // --- Extreme values ---

        @Test
        @DisplayName("Integer.MIN_VALUE → exception")
        void powerOfTwo_intMinValue_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(Integer.MIN_VALUE)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }

        @Test
        @DisplayName("Integer.MAX_VALUE → exception")
        void powerOfTwo_intMaxValue_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(Integer.MAX_VALUE)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }

        // --- Parameterized: valid power computations ---

        @ParameterizedTest(name = "2^{0} = {1}")
        @CsvSource({
                "0, 1",
                "1, 2",
                "2, 4",
                "3, 8",
                "4, 16",
                "5, 32",
                "10, 1024",
                "15, 32768",
                "20, 1048576",
                "25, 33554432",
                "30, 1073741824"
        })
        @DisplayName("parameterized: valid powers return correct result")
        void powerOfTwo_validPower_returnsCorrectResult(int power, int expected) {
            assertEquals(expected, myAdvancedMath.powerOfTwo(power));
        }

        // --- Parameterized: invalid power values ---

        @ParameterizedTest(name = "powerOfTwo({0}) throws exception")
        @ValueSource(ints = {-1, -10, -100, Integer.MIN_VALUE, 31, 32, 100, Integer.MAX_VALUE})
        @DisplayName("parameterized: invalid powers throw exception")
        void powerOfTwo_invalidPower_throwsException(int power) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> myAdvancedMath.powerOfTwo(power)
            );
            assertEquals("power should be 0<=power<=30", ex.getMessage());
        }
    }

    // =========================================================================
    // reverseArrayNumbers(int[] array, MyMath mm) tests
    // =========================================================================
    @Nested
    @DisplayName("reverseArrayNumbers")
    class ReverseArrayNumbersTests {

        // --- Tests WITHOUT mocking (using real MyMath) ---

        @Test
        @DisplayName("reverses array of positive numbers")
        void reverseArrayNumbers_positiveNumbers_returnsReversed() {
            MyMath mm = new MyMath();
            int[] input = {1, 2, 3};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{-1, -2, -3}, result);
        }

        @Test
        @DisplayName("reverses array of negative numbers")
        void reverseArrayNumbers_negativeNumbers_returnsPositive() {
            MyMath mm = new MyMath();
            int[] input = {-4, -5, -6};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{4, 5, 6}, result);
        }

        @Test
        @DisplayName("reverses array of mixed numbers")
        void reverseArrayNumbers_mixedNumbers_returnsReversed() {
            MyMath mm = new MyMath();
            int[] input = {-3, 0, 7};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{3, 0, -7}, result);
        }

        @Test
        @DisplayName("empty array returns empty array")
        void reverseArrayNumbers_emptyArray_returnsEmptyArray() {
            MyMath mm = new MyMath();
            int[] input = {};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{}, result);
        }

        @Test
        @DisplayName("single element array")
        void reverseArrayNumbers_singleElement_returnsReversed() {
            MyMath mm = new MyMath();
            int[] input = {42};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{-42}, result);
        }

        @Test
        @DisplayName("array with zeros")
        void reverseArrayNumbers_zeros_returnsZeros() {
            MyMath mm = new MyMath();
            int[] input = {0, 0, 0};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertArrayEquals(new int[]{0, 0, 0}, result);
        }

        @Test
        @DisplayName("array with extreme values")
        void reverseArrayNumbers_extremeValues_handlesCorrectly() {
            MyMath mm = new MyMath();
            int[] input = {Integer.MAX_VALUE, Integer.MIN_VALUE};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            // -MAX_VALUE is valid; -MIN_VALUE overflows back to MIN_VALUE
            assertArrayEquals(new int[]{-Integer.MAX_VALUE, Integer.MIN_VALUE}, result);
        }

        @Test
        @DisplayName("modifies the input array in place (returns same reference)")
        void reverseArrayNumbers_modifiesInputArray_sameReference() {
            MyMath mm = new MyMath();
            int[] input = {1, 2, 3};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mm);
            assertSame(input, result);
        }

        // --- Null argument edge cases ---

        @Test
        @DisplayName("null array throws NullPointerException")
        void reverseArrayNumbers_nullArray_throwsNPE() {
            MyMath mm = new MyMath();
            assertThrows(NullPointerException.class,
                    () -> myAdvancedMath.reverseArrayNumbers(null, mm));
        }

        @Test
        @DisplayName("null MyMath throws NullPointerException for non-empty array")
        void reverseArrayNumbers_nullMyMath_throwsNPE() {
            int[] input = {1, 2, 3};
            assertThrows(NullPointerException.class,
                    () -> myAdvancedMath.reverseArrayNumbers(input, null));
        }

        @Test
        @DisplayName("null MyMath with empty array returns empty array (loop not entered)")
        void reverseArrayNumbers_nullMyMathEmptyArray_returnsEmptyArray() {
            int[] input = {};
            // Loop body never executes, so null mm is never dereferenced
            int[] result = myAdvancedMath.reverseArrayNumbers(input, null);
            assertArrayEquals(new int[]{}, result);
        }
    }

    // =========================================================================
    // reverseArrayNumbers with Mock (verify interactions)
    // =========================================================================
    @Nested
    @DisplayName("reverseArrayNumbers with Mock")
    @ExtendWith(MockitoExtension.class)
    class ReverseArrayNumbersMockTests {

        @Mock
        private MyMath mockMyMath;

        @Test
        @DisplayName("mock: verifies reverseNumber called for each element")
        void reverseArrayNumbers_mockMyMath_verifiesInteraction() {
            when(mockMyMath.reverseNumber(1)).thenReturn(-1);
            when(mockMyMath.reverseNumber(2)).thenReturn(-2);
            when(mockMyMath.reverseNumber(3)).thenReturn(-3);

            int[] input = {1, 2, 3};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mockMyMath);

            assertArrayEquals(new int[]{-1, -2, -3}, result);

            verify(mockMyMath, times(1)).reverseNumber(1);
            verify(mockMyMath, times(1)).reverseNumber(2);
            verify(mockMyMath, times(1)).reverseNumber(3);
            verifyNoMoreInteractions(mockMyMath);
        }

        @Test
        @DisplayName("mock: empty array → reverseNumber never called")
        void reverseArrayNumbers_mockEmptyArray_noCalls() {
            int[] input = {};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mockMyMath);

            assertArrayEquals(new int[]{}, result);
            verifyNoInteractions(mockMyMath);
        }

        @Test
        @DisplayName("mock: single element array → reverseNumber called once")
        void reverseArrayNumbers_mockSingleElement_calledOnce() {
            when(mockMyMath.reverseNumber(99)).thenReturn(-99);

            int[] input = {99};
            int[] result = myAdvancedMath.reverseArrayNumbers(input, mockMyMath);

            assertArrayEquals(new int[]{-99}, result);
            verify(mockMyMath, times(1)).reverseNumber(99);
        }

        @Test
        @DisplayName("mock: verifies call order with large array")
        void reverseArrayNumbers_mockLargeArray_allElementsProcessed() {
            int[] input = new int[100];
            for (int i = 0; i < 100; i++) {
                input[i] = i;
                when(mockMyMath.reverseNumber(i)).thenReturn(-i);
            }

            int[] result = myAdvancedMath.reverseArrayNumbers(input, mockMyMath);

            for (int i = 0; i < 100; i++) {
                assertEquals(-i, result[i]);
                verify(mockMyMath).reverseNumber(i);
            }
        }
    }
}
```
##### AdvancedMath.add(int, int)
تست‌های تولیدشده شامل موارد زیر هستند:
- x مثبت، y منفی و برعکس
- هر دو منفی
- سرریز
- بررسی مرزی در ۰ برای x و y به‌طور جداگانه
- هر دو صفر
- بررسی مرزی در کمینه و بیشینه مقادیر integer
- تست‌های پارامتری معمولی
- تست‌های پارامتری با اعداد منفی

در مجموع، ۲۱ تست تولید شد. باز هم تکرار وجود دارد. همان اعداد مرزی در
تست‌های پارامتری. می‌توانیم نتیجه بگیریم که در تست‌های پارامتری، عامل به‌اشتباه
بعضی ورودی‌هایی را که قبلاً آزموده بود، از جمله مقادیر مرزی را قرار می‌دهد.

##### AdvancedMath.powerOfTwo(int)
۱۵ متد تولید شد. تست‌ها شامل موارد زیر هستند:
- بررسی مرزی در مرز پایین (-۱، ۰ ، ۱)
- بررسی مرزی در مرز بالا (۲۹، ۳۰، ۳۱)
- مقادیر حدی (کمینه و بیشینه integer)
- تست پارامتری معمولی
- تست پارامتری نامعتبر

نه‌تنها در تست پارامتری مقادیر تکراری وجود دارد، بلکه در
خود تست‌های معمولی هم! با وجود اینکه چقدر خوب
مرزها بررسی شده‌اند، این تست‌های اخیر نادیده گرفته شدند. معتقدم بررسی
مقادیر بیشینه برای این متد غیرضروری است، چون تفاوت واقعی با
یک مقدار عادی با همان علامت ندارند.

##### AdvancedMath.ReverseArrayNumber(int[], MyMath)
۱۵ متد تولید شد. برخی شامل mocking هستند که ارزیابی نمی‌کنم. موارد آزمون شامل:
- یک آرایه عادی با اعداد صحیح مثبت
- یک آرایه عادی با اعداد صحیح منفی
- یک آرایه مخلوط (شامل یک عدد مثبت، صفر و یک عدد منفی)
- آرایه خالی
- آرایه با یک عنصر
- آرایه پر از صفر
- آرایه با مقادیر حدی (که باعث سرریز هم می‌شود)
- assert کردن برابری آرایه بازگشتی و آرایه مرجع
- آرایه null
- MyMath null
- MyMath null با آرایه خالی (به استثنا نمی‌رسد)

معندم که در اینجا همه موارد مرزی پوشش داده شدند.

#### واحد FileIO
برای این واحد با تنها یک متد، ۱۷ تست تولید شد.
```java
package dmst.seip.angor.junitdemo.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Comprehensive unit tests for {@link FileIO} with boundary value analysis,
 * IO edge cases, and temporary file testing.
 */
@DisplayName("FileIO Tests")
public class FileIOClaudeTest {

    private FileIO fileIO;

    @BeforeEach
    void setUp() {
        fileIO = new FileIO();
    }

    // =========================================================================
    // readFile(String filepath) - Normal cases
    // =========================================================================
    @Nested
    @DisplayName("readFile - normal cases")
    class ReadFileNormalTests {

        @Test
        @DisplayName("reads valid file from test resources (names_valid.txt)")
        void readFile_validResourceFile_returnsAllLines() {
            // Use the test resource file that exists in the project
            String filepath = "src/test/resources/names_valid.txt";
            String[] result = fileIO.readFile(filepath);
            assertNotNull(result);
            assertEquals(9, result.length);
            assertEquals("Mitch", result[0]);
            assertEquals("Logan", result[8]);
        }

        @Test
        @DisplayName("reads file with multiple lines and verifies content order")
        void readFile_multipleLines_preservesOrder(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("multiline.txt");
            Files.write(file, java.util.List.of("alpha", "beta", "gamma", "delta"));

            String[] result = fileIO.readFile(file.toString());

            assertEquals(4, result.length);
            assertEquals("alpha", result[0]);
            assertEquals("beta", result[1]);
            assertEquals("gamma", result[2]);
            assertEquals("delta", result[3]);
        }

        @Test
        @DisplayName("reads file with single line")
        void readFile_singleLine_returnsSingleElementArray(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("single.txt");
            Files.writeString(file, "only one line");

            String[] result = fileIO.readFile(file.toString());

            assertEquals(1, result.length);
            assertEquals("only one line", result[0]);
        }
    }

    // =========================================================================
    // readFile - Edge cases
    // =========================================================================
    @Nested
    @DisplayName("readFile - edge cases")
    class ReadFileEdgeCaseTests {

        @Test
        @DisplayName("empty file returns empty array")
        void readFile_emptyFile_returnsEmptyArray(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("empty.txt");
            Files.writeString(file, "");

            String[] result = fileIO.readFile(file.toString());

            assertNotNull(result);
            assertEquals(0, result.length);
        }

        @Test
        @DisplayName("file with only whitespace lines returns those lines")
        void readFile_whitespaceLines_returnsWhitespace(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("whitespace.txt");
            Files.write(file, java.util.List.of("  ", "\t", "   "));

            String[] result = fileIO.readFile(file.toString());

            assertEquals(3, result.length);
            assertEquals("  ", result[0]);
            assertEquals("\t", result[1]);
            assertEquals("   ", result[2]);
        }

        @Test
        @DisplayName("file with empty lines between content")
        void readFile_emptyLinesBetweenContent_preservesEmptyLines(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("gaps.txt");
            Files.write(file, java.util.List.of("line1", "", "line3", "", "line5"));

            String[] result = fileIO.readFile(file.toString());

            assertEquals(5, result.length);
            assertEquals("line1", result[0]);
            assertEquals("", result[1]);
            assertEquals("line3", result[2]);
            assertEquals("", result[3]);
            assertEquals("line5", result[4]);
        }

        @Test
        @DisplayName("file with special characters")
        void readFile_specialCharacters_readsCorrectly(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("special.txt");
            Files.write(file, java.util.List.of(
                    "hello world!",
                    "café",
                    "line with\ttab",
                    "!@#$%^&*()"
            ));

            String[] result = fileIO.readFile(file.toString());

            assertEquals(4, result.length);
            assertEquals("hello world!", result[0]);
            assertEquals("café", result[1]);
            assertEquals("line with\ttab", result[2]);
            assertEquals("!@#$%^&*()", result[3]);
        }

        @Test
        @DisplayName("very long lines are read correctly")
        void readFile_veryLongLines_readsCorrectly(@TempDir Path tempDir) throws IOException {
            String longLine = "A".repeat(10000);
            Path file = tempDir.resolve("longline.txt");
            Files.writeString(file, longLine);

            String[] result = fileIO.readFile(file.toString());

            assertEquals(1, result.length);
            assertEquals(longLine, result[0]);
        }

        @Test
        @DisplayName("file with many lines")
        void readFile_manyLines_readsAll(@TempDir Path tempDir) throws IOException {
            java.util.List<String> lines = new java.util.ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                lines.add("Line " + i);
            }
            Path file = tempDir.resolve("many.txt");
            Files.write(file, lines);

            String[] result = fileIO.readFile(file.toString());

            assertEquals(1000, result.length);
            assertEquals("Line 0", result[0]);
            assertEquals("Line 999", result[999]);
        }
    }

    // =========================================================================
    // readFile - IO error / invalid input cases
    // =========================================================================
    @Nested
    @DisplayName("readFile - IO and error handling")
    class ReadFileErrorTests {

        @Test
        @DisplayName("non-existent file returns empty array (IOException caught)")
        void readFile_nonExistentFile_returnsEmptyArray() {
            String[] result = fileIO.readFile("this/file/does/not/exist.txt");

            assertNotNull(result);
            assertEquals(0, result.length);
        }

        @Test
        @DisplayName("non-existent file prints stack trace to stderr")
        void readFile_nonExistentFile_printsStackTrace() {
            PrintStream originalErr = System.err;
            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errContent));
            try {
                fileIO.readFile("nonexistent_file_12345.txt");
                String errorOutput = errContent.toString();
                assertTrue(errorOutput.contains("java.io.FileNotFoundException"));
            } finally {
                System.setErr(originalErr);
            }
        }

        @Test
        @DisplayName("null filepath throws NullPointerException")
        void readFile_nullFilepath_throwsNPE() {
            assertThrows(NullPointerException.class, () -> fileIO.readFile(null));
        }

        @Test
        @DisplayName("filepath is a directory → returns empty array (IOException caught)")
        void readFile_directory_returnsEmptyArray(@TempDir Path tempDir) {
            // Attempting to read a directory as a file triggers an IOException
            String[] result = fileIO.readFile(tempDir.toString());

            assertNotNull(result);
            // Behavior is platform-dependent; on some platforms a directory may
            // not be readable as a file, resulting in empty or IOException
            // On most platforms, FileReader on a directory throws IOException
        }

        @Test
        @DisplayName("empty string filepath returns empty array (IOException)")
        void readFile_emptyStringFilepath_returnsEmptyArray() {
            // new File("") may or may not throw, depending on platform
            // but attempting to read it should fail gracefully
            String[] result = fileIO.readFile("");
            assertNotNull(result);
        }

        @Test
        @DisplayName("filepath with only spaces")
        void readFile_spacesOnlyFilepath_returnsEmptyArray() {
            String[] result = fileIO.readFile("   ");
            assertNotNull(result);
            assertEquals(0, result.length);
        }
    }

    // =========================================================================
    // readFile - Return type validation
    // =========================================================================
    @Nested
    @DisplayName("readFile - return type")
    class ReadFileReturnTypeTests {

        @Test
        @DisplayName("returns String array, not null, for valid file")
        void readFile_validFile_neverReturnsNull(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("test.txt");
            Files.writeString(file, "content");

            String[] result = fileIO.readFile(file.toString());

            assertNotNull(result);
            assertInstanceOf(String[].class, result);
        }

        @Test
        @DisplayName("return array is independent of internal list")
        void readFile_returnedArray_isNewArray(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("test.txt");
            Files.write(file, java.util.List.of("a", "b"));

            String[] result1 = fileIO.readFile(file.toString());
            String[] result2 = fileIO.readFile(file.toString());

            assertNotSame(result1, result2);
            assertArrayEquals(result1, result2);
        }
    }
}
```
برخی متدها از پوشه موقت استفاده می‌کنند و برخی دیگر نه. این
برای مدل‌های قبلی رخ نداد.
تست‌های تولیدشده شامل موارد زیر هستند:
- تست عادی
- فایل تک‌خطی
- فایل خالی
- فایلی که فقط فاصله‌های خالی دارد
- فایلی با خطوط خالی بین محتوا
- فایلی با نویسه‌های ویژه
- فایلی با یک خط بسیار بلند
- فایلی با هزار خط
- فایل ناموجود
- filepath با مقدار null
- filepath که یک پوشه است
- filepath با رشته خالی
- filepath با فقط فاصله‌ها
- assert نوع بازگشتی
- آرایه بازگشتی مستقل از لیست داخلی است

موارد مرزی زیادی که حتی به ذهنم نرسیده بود، مانند یک خط بسیار بلند
در این تست‌ها پوشش داده شده‌اند، اما آیا وقتی BufferReader
از قبل یک کلاس کتابخانه‌ای معتبر جاوا است، برخی از آن‌ها لازم بود؟ بررسی برخی جزئیات پیاده‌سازی مثل
بررسی stack trace لازم نبود. 

#### واحد NameSearch
برای آخرین واحد این گزارش، عامل ۲۴ متد تولید کرد.
```java
package dmst.seip.angor.junitdemo.search;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dmst.seip.angor.junitdemo.io.FileIO;

/**
 * Comprehensive unit tests for {@link NameSearch} with MC/DC coverage,
 * boundary value analysis, mocking, and edge case testing.
 */
@DisplayName("NameSearch Tests")
public class NameSearchClaudeTest {

    private NameSearch nameSearch;

    @BeforeEach
    void setUp() {
        nameSearch = new NameSearch();
    }

    // =========================================================================
    // findNamesInFileThatStartWith - Integration tests (real FileIO)
    // =========================================================================
    @Nested
    @DisplayName("findNamesInFileThatStartWith - Integration with real FileIO")
    class IntegrationTests {

        @Test
        @DisplayName("finds names starting with 'M' in test resource file")
        void findNames_prefixM_returnsMatches() {
            FileIO fileIO = new FileIO();
            String filepath = "src/test/resources/names_valid.txt";

            String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIO, "M");

            assertEquals(2, result.length);
            assertEquals("Mitch", result[0]);
            assertEquals("Matt", result[1]);
        }

        @Test
        @DisplayName("finds names starting with 'C' in test resource file")
        void findNames_prefixC_returnsMatches() {
            FileIO fileIO = new FileIO();
            String filepath = "src/test/resources/names_valid.txt";

            String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIO, "C");

            assertEquals(2, result.length);
            assertEquals("C.J", result[0]);
            assertEquals("Caroline", result[1]);
        }

        @Test
        @DisplayName("prefix with no match returns empty array")
        void findNames_noMatch_returnsEmpty() {
            FileIO fileIO = new FileIO();
            String filepath = "src/test/resources/names_valid.txt";

            String[] result = nameSearch.findNamesInFileThatStartWith(filepath, fileIO, "Z");

            assertNotNull(result);
            assertEquals(0, result.length);
        }
    }

    // =========================================================================
    // findNamesInFileThatStartWith - Mocked FileIO tests
    // =========================================================================
    @Nested
    @DisplayName("findNamesInFileThatStartWith - Mocked FileIO")
    @ExtendWith(MockitoExtension.class)
    class MockedTests {

        @Mock
        private FileIO mockFileIO;

        // --- MC/DC for Decision: name.startsWith(prefix) ---
        // Single condition: true → add to list, false → skip
        // Two tests: one matching, one not matching → full MC/DC

        @Test
        @DisplayName("MC/DC: name.startsWith(prefix) = true → included in results")
        void findNames_matchingPrefix_included() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Al");

            assertEquals(1, result.length);
            assertEquals("Alice", result[0]);
        }

        @Test
        @DisplayName("MC/DC: name.startsWith(prefix) = false → excluded from results")
        void findNames_nonMatchingPrefix_excluded() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Bob"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Al");

            assertEquals(0, result.length);
        }

        // --- Comprehensive matching tests ---

        @Test
        @DisplayName("multiple names, some match prefix")
        void findNames_mixedNames_returnsOnlyMatches() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob", "Alex", "Charlie", "Amanda"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Al");

            assertEquals(2, result.length);
            assertEquals("Alice", result[0]);
            assertEquals("Alex", result[1]);
        }

        @Test
        @DisplayName("all names match prefix")
        void findNames_allMatch_returnsAll() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Alex", "Amanda"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A");

            assertEquals(3, result.length);
        }

        @Test
        @DisplayName("no names match prefix")
        void findNames_nonMatch_returnsEmpty() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob", "Charlie"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Z");

            assertEquals(0, result.length);
        }

        // --- Empty prefix (boundary) ---

        @Test
        @DisplayName("empty prefix matches all names (every string starts with empty)")
        void findNames_emptyPrefix_matchesAll() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob", "Charlie"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "");

            assertEquals(3, result.length);
            assertEquals("Alice", result[0]);
            assertEquals("Bob", result[1]);
            assertEquals("Charlie", result[2]);
        }

        // --- Empty file ---

        @Test
        @DisplayName("file with no names returns empty array")
        void findNames_emptyFile_returnsEmpty() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A");

            assertEquals(0, result.length);
        }

        // --- Case sensitivity ---

        @Test
        @DisplayName("prefix matching is case-sensitive")
        void findNames_caseSensitive_exactMatch() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"alice", "Alice", "ALICE"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A");

            assertEquals(1, result.length);
            assertEquals("Alice", result[0]);
        }

        @Test
        @DisplayName("lowercase prefix does not match uppercase names")
        void findNames_lowercasePrefix_noUppercaseMatch() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "a");

            assertEquals(0, result.length);
        }

        // --- Prefix equals name exactly ---

        @Test
        @DisplayName("prefix equal to full name matches")
        void findNames_prefixEqualsName_matches() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice", "Bob"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Alice");

            assertEquals(1, result.length);
            assertEquals("Alice", result[0]);
        }

        // --- Prefix longer than any name ---

        @Test
        @DisplayName("prefix longer than any name matches nothing")
        void findNames_prefixLongerThanNames_noMatch() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Al", "Bo"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "Alice");

            assertEquals(0, result.length);
        }

        // --- Verify FileIO interaction ---

        @Test
        @DisplayName("verifies readFile is called once with correct filepath")
        void findNames_verifiesFileIOInteraction() {
            when(mockFileIO.readFile("mypath/names.txt"))
                    .thenReturn(new String[]{"Test"});

            nameSearch.findNamesInFileThatStartWith("mypath/names.txt", mockFileIO, "T");

            verify(mockFileIO, times(1)).readFile("mypath/names.txt");
            verifyNoMoreInteractions(mockFileIO);
        }

        // --- Special characters in prefix ---

        @Test
        @DisplayName("prefix with special characters")
        void findNames_specialCharPrefix_matchesCorrectly() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"C.J", "Caroline", "Charlie"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "C.");

            assertEquals(1, result.length);
            assertEquals("C.J", result[0]);
        }

        // --- Names with empty strings ---

        @Test
        @DisplayName("file content with empty string names, non-empty prefix → empty strings don't match")
        void findNames_emptyStringNames_nonEmptyPrefixNoMatch() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"", "", "Alice"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A");

            assertEquals(1, result.length);
            assertEquals("Alice", result[0]);
        }

        @Test
        @DisplayName("empty prefix matches empty string names too")
        void findNames_emptyPrefix_matchesEmptyNames() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"", "Alice"});

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "");

            assertEquals(2, result.length);
        }

        // --- Parameterized prefix tests ---

        @ParameterizedTest(name = "prefix ''{0}'' finds matching names")
        @ValueSource(strings = {"M", "N", "C", "J", "L", "S", "Lo"})
        @DisplayName("parameterized: various prefixes on names_valid content")
        void findNames_variousPrefixes_returnCorrectCounts(String prefix) {
            // Simulate names_valid.txt content
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{
                            "Mitch", "Newmie", "C.J", "Matt", "Caroline",
                            "Jason", "Lani", "Summer", "Logan"
                    });

            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, prefix);

            // Verify each name in result actually starts with prefix
            for (String name : result) {
                assertTrue(name.startsWith(prefix),
                        "Expected '" + name + "' to start with '" + prefix + "'");
            }

            // Verify no matching names were missed
            String[] allNames = {"Mitch", "Newmie", "C.J", "Matt", "Caroline",
                    "Jason", "Lani", "Summer", "Logan"};
            long expectedCount = java.util.Arrays.stream(allNames)
                    .filter(n -> n.startsWith(prefix))
                    .count();
            assertEquals(expectedCount, result.length);
        }
    }

    // =========================================================================
    // Null argument tests (no mocking needed)
    // =========================================================================
    @Nested
    @DisplayName("findNamesInFileThatStartWith - null arguments")
    @ExtendWith(MockitoExtension.class)
    class NullArgumentTests {

        @Mock
        private FileIO mockFileIO;

        @Test
        @DisplayName("null fileIo throws NullPointerException")
        void findNames_nullFileIO_throwsNPE() {
            assertThrows(NullPointerException.class,
                    () -> nameSearch.findNamesInFileThatStartWith("test.txt", null, "A"));
        }

        @Test
        @DisplayName("null prefix throws NullPointerException (in startsWith)")
        void findNames_nullPrefix_throwsNPE() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{"Alice"});

            assertThrows(NullPointerException.class,
                    () -> nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, null));
        }

        @Test
        @DisplayName("null prefix with empty file → no NPE (loop not entered)")
        void findNames_nullPrefixEmptyFile_noException() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{});

            // Loop never executes, so null prefix doesn't cause startsWith call
            String[] result = nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, null);
            assertEquals(0, result.length);
        }

        @Test
        @DisplayName("null filepath is passed through to FileIO (behavior depends on FileIO)")
        void findNames_nullFilepath_delegatesToFileIO() {
            when(mockFileIO.readFile(null))
                    .thenThrow(new NullPointerException());

            assertThrows(NullPointerException.class,
                    () -> nameSearch.findNamesInFileThatStartWith(null, mockFileIO, "A"));
        }

        @Test
        @DisplayName("file content with null element causes NPE on startsWith")
        void findNames_fileReturnsNullElement_throwsNPE() {
            when(mockFileIO.readFile("test.txt"))
                    .thenReturn(new String[]{null, "Alice"});

            assertThrows(NullPointerException.class,
                    () -> nameSearch.findNamesInFileThatStartWith("test.txt", mockFileIO, "A"));
        }
    }
}
```
متأسفانه mocking بیش از حد استفاده شد و بعضی از موارد مرزی
هم با mocking نوشته شدند. این هم باعث
چند تکرار شده است. من mocking را ارزیابی نمی‌کنم.  
این تست‌ها شامل موارد زیر هستند:
- دو تست عادی
- آرایه خالی
- جست‌وجوی بدون تطابق
- تطابق همه خطوط با پیشوند
- پیشوند خالی
- فایل خالی
- حساسیت به بزرگی و کوچکی حروف
- پیشوند برابر با یک خط کامل
- پیشوند بلندتر از هر نامی
- نویسه ویژه در پیشوند
- محتوای فایل با نام‌های رشته خالی
- پیشوند خالی با رشته خالی هم تطابق دارد
- تست پارامتری
- FileIO با مقدار null
- پیشوند null
- مسیر فایل null
- عنصر null در خروجی readFile

بعضی از موارد آزمون در FileIO انجام شده بودند و معتقدم
غیرضروری بودند، مانند محتوای فایل با رشته خالی.
به نظر من فقط "C" یا "M" باید آزموده می‌شد.
فکر نمی‌کنم چیزی مثل عنصر null در آرایه رشته‌ای
اصلاً ممکن باشد، اما به هر حال آزموده شد؟ همچنین مانند تست واحد
قبلی، آزمودن کتابخانه‌های جاوا مانند
آزمودن نویسه‌های ویژه و حساسیت به بزرگی و کوچکی حروف غیرضروری است.

#### نتیجه‌گیری
این بار، عامل انتظار من را برآورده کرد. بی‌نقص نبود.
تکرارهای زیادی وجود داشت. بررسی مرزی با این
مدل خوب انجام شده بود. هرچند از Gemini خواستم بررسی مرزی انجام دهد،
انجام نداد، اما Claude قطعاً آن را انجام داد، یعنی مدل اهمیت دارد.
پرامپت پیچیده‌تر هم نقش مهمی داشت. با این حال
کار بسیار مهمی که باید انجام شود این است که بررسی کنیم هر مدل با
همان پرامپت چه عملکردی خواهد داشت. اگر دوباره از Opus خواسته باشم، از آن می‌خواهم
از mocking استفاده نکند، سعی کند تست‌های تکراری، به‌ویژه با
بررسی مرزی و تست‌های پارامتری که هم‌پوشانی زیادی داشتند، را وارد نکند.
همچنین به آن می‌گویم کتابخانه‌های جاوا را تست نکند. آن‌ها از قبل
تست شده‌اند و قابل اعتماد هستند.

## نتیجه‌گیری نهایی
مدل و پرامپت هر دو قطعاً مهم هستند. این یک رقابت منصفانه
نبود چون Claude Opus، که قوی‌ترین مدل است، با پرامپتی دقیق‌تر
خیلی بهتر عمل کرد. هرچه جزئیات بیشتری اضافه کنید،
قطعاً بهتر می‌شود. یک مقایسه خوب برای یافتن
بهترین مدل موجود در GitHub Copilot این است که
همان پرامپت را به عامل با همان مصرف توکن بدهید.
همچنین یک فایل Excel ساختم که شامل مقایسه
هر متد در تمام مدل‌ها است. در اینجا هر متد را
با تست‌های تولیدشده مقایسه نخواهم کرد.


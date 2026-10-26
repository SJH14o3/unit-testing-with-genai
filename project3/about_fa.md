## پروژه دشوار
این keycloak است، یک سامانه متن‌باز مدیریت هویت و دسترسی که
کاربران با آن احراز هویت می‌کنند و نه با برنامه‌های کاربردی مجزا. این یعنی برنامه‌های
کاربردی شما نیازی به رسیدگی به فرم‌های ورود، احراز هویت کاربران و ذخیره‌سازی کاربران ندارند.
Keycloak قابلیت‌هایی مانند اتحاد کاربران (user federation)، احراز هویت قوی، مدیریت کاربران، مجوزدهی دقیق و موارد بیشتر را فراهم می‌کند.  
پیوند مخزن: https://github.com/keycloak/keycloak

این پروژه ۲۶ ماژول سطح‌بالای Maven دارد که مستقیماً در `pom.xml` ریشه تعریف شده‌اند.
در زمان نگارش این متن، ۳۲٬۴۱۵ کامیت وجود دارد.

این پروژه بسیار بزرگی است. من برای همهٔ آن تست تولید نخواهم کرد.
در عوض، ۱۰ فایل را انتخاب کرده‌ام که حدود ۱۰٬۰۰۰ خط کد دارند.
این فایل‌ها وابستگی‌های زیادی دارند. در اینجا مجاز به استفاده از mocking هستم.
برای تست واحد این پروژه، mockهای زیادی لازم بود.

## فایل‌های انتخاب‌شده برای تست
(LOC, Statements, Decisions)  
`model\jpa\src\main\java\org\keycloak\models\jpa\UserAdapter.java`
(۵۱۸، ۱۲۵۰، ۱۳۸)  
`model\jpa\src\main\java\org\keycloak\models\jpa\JpaUserProvider.java`
(۱۱۴۲، ۳۷۲۶، ۲۰۸)  
`server-spi-private\src\main\java\org\keycloak\models\utils\RepresentationToModel.java`
(۱۴۵۹، ۵۲۶۵، ۶۶۴)  
`server-spi-private\src\main\java\org\keycloak\models\utils\ModelToRepresentation.java`
(۱۳۰۴، ۳۹۴۳، ۲۳۰)  
`server-spi-private\src\main\java\org\keycloak\models\utils\KeycloakModelUtils.java`
(۹۱۸، ۲۱۱۶، ۳۰۸)  
`services\src\main\java\org\keycloak\protocol\oidc\TokenManager.java`
(۱۳۲۵، ۳۶۸۱، ۴۳۷)  
`services\src\main\java\org\keycloak\protocol\oidc\OIDCLoginProtocol.java`
(۴۶۰، ۱۰۲۶، ۹۹)  
`services\src\main\java\org\keycloak\protocol\oidc\endpoints\AuthorizationEndpoint.java`
(۳۵۸، ۲۲۱۲، ۱۷۶)  
`services\src\main\java\org\keycloak\authentication\AuthenticationProcessor.java`
(۱۰۶۰، ۲۸۲۴، ۲۰۶)  
`services\src\main\java\org\keycloak\services\managers\RealmManager.java`
(۷۲۰، ۱۹۴۱، ۲۲۰)  

در مجموع: (۹۲۶۴، ۲۷۹۸۴، ۲۶۸۶)

LOC با اسکریپتی محاسبه می‌شود که خطوط دارای یک دستور java را می‌شمارد.  
تعداد statements و decisions توسط JaCoCo ارائه می‌شود.

برای درک اینکه این پروژه چقدر پیچیده است، `pom.xml` مربوط به Gson سه
برچسب وابستگی داشت، در حالی که keycloak در "pom.xml" اصلی خود ۱۷۵ برچسب دارد.

`project3\run` شامل مخزنی مینیمال با فایل‌های بالا و
تمام وابستگی‌های لازم برای اجرای تست‌ها روی keycloak است.

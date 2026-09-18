# 05 - الترجمة (Translations / Localization)

## ⚠️ القاعدة الأساسية

> **كل نص يظهر للمستخدم يجب أن يمر عبر `LocaleController`.**
> لا تكتب `setText("Hello")` مباشرة. استخدم
> `setText(LocaleController.getString(R.string.Hello))`.

```java
// ❌ ممنوع
textView.setText("Saved");
Toast.makeText(ctx, "تم الحفظ", Toast.LENGTH_SHORT).show();
titleView.setText("Settings");

// ✅ الطريقة الصحيحة
textView.setText(LocaleController.getString(R.string.Saved));
BulletinFactory.of(this).createSimpleBulletin(R.drawable.ic_check, LocaleController.getString(R.string.Saved)).show();
titleView.setText(LocaleController.getString(R.string.Settings));
```

---

## LocaleController — الدوال الأساسية

```java
// messenger/LocaleController.java (4490 سطر)
public class LocaleController {
    public static boolean isRTL;            // هل اللغة الحالية RTL
    public static int nameDisplayOrder;     // 1 = first name first, 2 = last name first
    public static boolean is24HourFormat;   // 12h/24h

    // 1) نص عادي
    public static String getString(@StringRes int res);
    public static String getString(String key);                          // deprecated

    // 2) نص مع متغيرات (placeholders)
    public static String formatString(@StringRes int res, Object... args);
    public static String formatString(String key, int res, Object... args);

    // 3) جمع/مفرد (Plural)
    public static String getPluralString(String key, int plural);
    public static String formatPluralString(String key, int plural, Object... args);

    // 4) Plain string interpolation
    public static String formatStringSimple(String string, Object... args);

    // 5) Time / Date formatting
    public static FastDateFormat getFormatterDay();
    public static FastDateFormat getFormatterDayMonth();
    public static FastDateFormat getFormatterYear();
    public static FastDateFormat getFormatterWeek();
    public static FastDateFormat getFormatterWeekLong();
    public static FastDateFormat getFormatterMonth();

    // 6) أدوات
    public static String getStringParamForNumber(int number);
    public static String formatTTLString(int ttl);
    public static String formatCallDuration(int duration);
    public static String formatDistance(float meters);
    public static CharSequence bold(CharSequence text);
    public static String nullable(String val);  // null إذا كان LOC_ERR
}
```

### استخدامات عملية

```java
// 1) نص ثابت
String text = LocaleController.getString(R.string.AppName);
// في strings.xml:  <string name="AppName">HudGram</string>

// 2) نص بمتغير
String text = LocaleController.formatString(R.string.WelcomeMsg, userName);
// في strings.xml:  <string name="WelcomeMsg">Welcome, %1$s!</string>

// 3) جمع/مفرد (يهم للغات مثل العربية التي لها 6 أشكال)
String text = LocaleController.formatPluralString("NewMessages", count);
// في strings.xml:
//   <string name="NewMessages">No new messages</string>
//   <string name="NewMessages_one">1 new message</string>
//   <string name="NewMessages_two">2 new messages</string>
//   <string name="NewMessages_few">%d new messages</string>
//   <string name="NewMessages_many">%d new messages</string>
//   <string name="NewMessages_other">%d new messages</string>

// 4) نص من السيرفر (مفاتيح غير موجودة محلياً)
String text = LocaleController.getString("SomeServerKey");
// يعود إلى التحميل من سيرفر تيليجرام إذا لم يوجد محلياً
```

---

## ملفات الترجمة (Localization Files)

```
TMessagesProj/src/main/res/
├── values/          — الافتراضي (إنجليزية)
│   ├── strings.xml  (10,662 سطر — ~6,500+ مفتاح)
│   ├── colors.xml
│   ├── styles.xml
│   ├── attrs.xml
│   └── ...
├── values-ar/       — العربية (10,787 سطر)
├── values-de/       — الألمانية
├── values-es/       — الإسبانية
├── values-it/       — الإيطالية
├── values-ko/       — الكورية
├── values-nl/       — الهولندية
├── values-pt-rBR/   — البرتغالية البرازيلية
├── values-ru/       — الروسية
├── values-uk/       — الأوكرانية
└── values-night/    — ثيم داكن (ألوان فقط)
```

> **ملاحظة:** المشروع لا يستخدم `_en`/`_es_ar` المجلدات — كل اللغات تذهب إلى
> `values-XX/` مع كود اللغة كـ suffix.

---

## بنية ملف الترجمة

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- مفاتيح اللغة الأساسية -->
    <string name="AppName">HudGram</string>
    <string name="AppNameBeta">HudGram Beta</string>
    <string name="LanguageName">English</string>
    <string name="English">English</string>
    <string name="LanguageNameInEnglish">English</string>
    <string name="LanguageCode">en</string>
    <string name="ContinueOnThisLanguage">Continue in English</string>

    <!-- مفتاح عام -->
    <string name="YourPhone">Your Phone</string>

    <!-- مفتاح مع متغير (1$s = string, 1$d = int) -->
    <string name="WelcomeMsg">Welcome, %1$s!</string>
    <string name="ShortNumberInfo">The number you provided is too short for **%1$s**:\n\n**%2$s**\n\n…</string>

    <!-- جمع/مفرد — حسب قواعد CLDR -->
    <string name="NewMessages">No new messages</string>
    <string name="NewMessages_one">%d new message</string>
    <string name="NewMessages_other">%d new messages</string>
</resources>
```

### تنسيقات خاصة يدعمها المشروع
- `\n` — سطر جديد
- `**bold**` — نص عريض (Markdown خفيف)
- `` `code` `` — code
- `[link](url)` — رابط
- `%1$s`, `%2$d` — placeholders

---

## إضافة ترجمة جديدة — خطوات كاملة

### 1) اختر مفتاح (Key) فريد
- snake_case، يبدأ بحرف كبير، وصفي
- مثال: `MyFeature.SaveButton` أو `MyFeatureSaveButton`

### 2) أضف في `values/strings.xml` (الافتراضي = إنجليزية)
```xml
<string name="MyFeatureSaveButton">Save changes</string>
```

### 3) أضف في `values-ar/strings.xml` (العربية)
```xml
<string name="MyFeatureSaveButton">حفظ التغييرات</string>
```

### 4) (اختياري) أضف في لغات أخرى تريد دعمها
- `values-de/strings.xml` — ألمانية
- `values-ru/strings.xml` — روسية
- إلخ.

### 5) استخدمه في الكود
```java
String text = LocaleController.getString(R.string.MyFeatureSaveButton);
```

---

## إضافة جمع/مفرد (Plural)

**الخطوة 1:** في `values/strings.xml`:
```xml
<plurals name="NewMessages">
    <item quantity="one">%d new message</item>
    <item quantity="other">%d new messages</item>
</plurals>
```

**الخطوة 2:** (للعربية، تحتاج 6 أشكال):
في `values-ar/strings.xml`:
```xml
<plurals name="NewMessages">
    <item quantity="zero">لا توجد رسائل جديدة</item>
    <item quantity="one">رسالة جديدة واحدة</item>
    <item quantity="two">رسالتان جديدتان</item>
    <item quantity="few">%d رسائل جديدة</item>
    <item quantity="many">%d رسالة جديدة</item>
    <item quantity="other">%d رسائل جديدة</item>
</plurals>
```

**الخطوة 3:** في الكود:
```java
String text = LocaleController.formatPluralString("NewMessages", count);
```

### كميات CLDR
- `zero` — صفر (عربي فقط)
- `one` — 1
- `two` — 2 (عربي)
- `few` — 3-10 (عربي)
- `many` — 11-99 (عربي)
- `other` — الباقي (الإنجليزية تستخدم هذا فقط)

---

## مفاتيح الترجمة الخاصة بـ Markdown داخل النصوص

```xml
<!-- نص عريض في تيليجرام -->
<string name="ShortNumberInfo">The number you provided is too short for **%1$s**:\n\n**%2$s**\n\n…</string>

<!-- رابط -->
<string name="PrivacyPolicyLink">Read our [Privacy Policy](%1$s)</string>

<!-- Code -->
<string name="ErrorCodeMessage">Error code: `%1$s`</string>
```

في الكود، يجب أن يمر النص عبر SpannableBuilder خاص (`formatSpannable`)
ليتم عرض **bold** و [link] بشكل صحيح. الأغلب في `LocaleController.formatString`
يستخدم `String.format` ويعود `String` عادي (لا Spannable).
إذا كنت تريد Markdown حي، استخدم:

```java
// في مكونات تنسيق الرسائل، هذا يعمل:
CharSequence spannable = SpannableBuilder.from(text, key);

// في أغلب الحالات، Markdown خفيف جداً: **bold** فقط
// يمكن استبداله بـ AndroidUtilities.replaceSingleLinkBold(text)
```

---

## التحقق من ترجمة موجودة

```java
// في الكود، اعرف هل المفتاح موجود أم LOC_ERR
String text = LocaleController.getString(R.string.MyKey);
if (text.startsWith("LOC_ERR")) {
    // المفتاح غير موجود
}
```

---

## اختيار اللغة

- المستخدم يختار من `LanguageSelectActivity`
- يُحفظ في `SharedConfig.selectedLanguage` (string)
- عند `Application.onCreate`، يقرأ `LocaleController` اللغة ويُحمّل الـ strings

### إضافة لغة جديدة

1) أنشئ مجلد `values-XX/` (مثل `values-fr/`)
2) انسخ `strings.xml` من `values/`، ترجم القيم، واحفظ
3) أضف في `LanguageSelectActivity` الكود المناسب
4) (اختياري) أضف في `BuildVars` أي ثوابت متعلقة باللغة

---

## أفضل الممارسات

| افعل | لا تفعل |
|------|------|
| `LocaleController.getString(R.string.X)` | `"X"` نص ثابت |
| `LocaleController.formatString(R.string.X, value)` | `"X " + value` (ترجمة) |
| `LocaleController.formatPluralString("X", n)` | `n == 1 ? "..." : "..."` |
| اجعل الجمل كاملة (مع الفاعل) | تقطيع الجملة (`getString(R.string.Welcome) + name`) |
| ضع الجمل في ملفات strings | ترجمة داخل الكود |

### مثال: جملة كاملة vs تقطيع

```java
// ❌ تقطيع
String text = LocaleController.getString(R.string.WelcomeMsg) + " " + userName;
// النتيجة: "Welcome %1$s" — لكن في بعض اللغات، الاسم يوضع في البداية

// ✅ جملة كاملة
String text = LocaleController.formatString(R.string.WelcomeMsg, userName);
// في الإنجليزي: "Welcome, %1$s!" → "Welcome, Ahmed!"
// في الياباني: "%1$sさん、ようこそ!" → "Ahmedさん、ようこそ!"
```

---

## تذكير: كيف يعمل الـ locale في تيليجرام

- `LocaleController` يحتفظ بـ `currentLocale` (Java `Locale`)
- الـ `resourcesCacheMap` يخزن mapping من R.string.* إلى key النص
- عند `getString(R.string.X)`:
  1. يبحث عن الـ key من الـ `resourcesCacheMap`
  2. يقرأ النص من `currentLocale` (resources)
  3. إذا لم يوجد، يرجع النص الافتراضي
  4. إذا لم يوجد أبداً، يرجع `"LOC_ERR:keyName"`
- للنصوص من السيرفر (dynamic)، يستخدم `getServerString(key)`

---

## نصائح للترجمة للعربية

| ❌ ترجمة سيئة | ✅ ترجمة جيدة |
|------|------|
| "حفظ التغييرات بنجاح" (مباشرة) | "تم حفظ التغييرات" (طبيعية) |
| "هل تريد المتابعة؟" (بسيطة) | "هل تودّ المتابعة؟" (أفصح) |
| "إعدادات الإشعارات" (مكررة) | "إعدادات التنبيهات" أو "تنبيهات الإشعارات" |
| "غير معروف" (مبتذلة) | "مجهول" |

- استخدم صيغة المذكر الغالب (مثل تيليجرام)
- احترم علامات الترقيم العربية: `،` بدلاً من `,`، `؛` بدلاً من `;`، `؟` بدلاً من `?`
- لا تنسخ ترجمة Google — اقرأ النص الأصلي وفهم المعنى
- **الـ RTL مهم** — لا تضع نصاً في سياق يحتاج لف LTR

---

## اختصارات للترجمة (Cheat Sheet)

```java
// 1) ترجمة بسيطة
LocaleController.getString(R.string.X)

// 2) بمتغير
LocaleController.formatString(R.string.X, arg1, arg2)

// 3) جمع
LocaleController.formatPluralString("X", count)

// 4) من سيرفر (fallback)
LocaleController.getString("serverKey")

// 5) Plain interpolation (بدون locale)
LocaleController.formatStringSimple("%1$s likes %2$s", name, item)

// 6) bold span
LocaleController.bold(text)

// 7) null safe
String displayText = LocaleController.nullable(text);  // null إذا كان LOC_ERR
```

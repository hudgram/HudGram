# 04 - نظام الألوان والثيم (Theme & Colors)

## ⚠️ القاعدة الأساسية

> **لا تستخدم أي لون ثابت في الكود. أبداً.**
> استخدم دائماً `Theme.getColor(Theme.key_…)` أو
> `Theme.ResourcesProvider.getColor(key)`.

```java
// ❌ ممنوع تماماً
textView.setTextColor(0xff000000);
view.setBackgroundColor(Color.WHITE);
paint.setColor(0x33999999);

// ✅ الطريقة الصحيحة
textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
paint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
```

---

## `Theme` — الكلاس الرئيسي

```java
// ui/ActionBar/Theme.java (10627 سطر)
public class Theme {
    public static final int ACTION_BAR_PHOTO_VIEWER_COLOR = 0x7f000000;
    public static final int ACTION_BAR_MEDIA_PICKER_COLOR = 0xff333333;
    public static final int ACTION_BAR_VIDEO_EDIT_COLOR  = 0xff000000;
    public static final int ACTION_BAR_PLAYER_COLOR       = 0xffffffff;

    // عدد الألوان: 777+ (colorsCount)
    public static int colorsCount;

    // كل لون له مفتاح (key) ثابت
    public static final int key_windowBackgroundWhite = colorsCount++;
    public static final int key_chat_inBubble = colorsCount++;
    // ... 775+ key آخر
}
```

### الـ 3 دوال الأساسية للحصول على لون

```java
// 1) الافتراضي
int color = Theme.getColor(Theme.key_windowBackgroundWhite);

// 2) مع isDefault flag (هل هو من الثيم الافتراضي أم مُعدّل)
boolean[] isDefault = new boolean[1];
int color = Theme.getColor(Theme.key_windowBackgroundWhite, isDefault);

// 3) من ResourcesProvider (مفيد لثيمات مخصصة — مثل ثيم محادثة)
Theme.ResourcesProvider provider = ...;
int color = Theme.getColor(Theme.key_chat_inBubble, provider);
```

### دوال أخرى مفيدة

```java
// لون افتراضي إذا لم يكن معرّفاً
int color = Theme.getDefaultColor(Theme.key_X);

// لون من accent (هوية المستخدم الملوّنة)
int color = Theme.getDefaultAccentColor(Theme.key_X);

// لون النص (يختار أسود/أبيض حسب الـ background)
int color = Theme.getNonAnimatedColor(Theme.key_chat_messageTextIn);

// اللون المراد للـ service message (يخدم الرسائل النظام)
int color = Theme.getServiceMessageColor();

// لون خلفية الـ service message
int color = Theme.getServiceMessageColor() & 0x00ffffff | 0x32000000;
```

---

## فئات الـ Color Keys

### خلفية النوافذ (Window Backgrounds)
```
key_windowBackgroundWhite             — خلفية شاشات عادية
key_windowBackgroundGray              — خلفية شاشات ثانوية
key_windowBackgroundGrayShadow        — ظل
key_dialogBackground                  — خلفية Dialog
key_dialogTopBackground               — خلفية الـ header للـ Dialog
```

### نصوص (Text)
```
key_windowBackgroundWhiteBlackText    — نص أساسي
key_windowBackgroundWhiteGrayText     — نص ثانوي
key_windowBackgroundWhiteGrayText2..8 — تدرجات رمادي
key_windowBackgroundWhiteHintText     — placeholder
key_windowBackgroundWhiteValueText    — value على اليمين
key_windowBackgroundWhiteLinkText      — روابط
key_windowBackgroundWhiteLinkSelection — تحديد رابط
key_windowBackgroundWhiteBlueText     — أزرق (روابط/أسئلة)
key_windowBackgroundWhiteBlueText2..7 — تدرجات أزرق
key_windowBackgroundWhiteGreenText    — أخضر (نجاح)
key_windowBackgroundWhiteGreenText2   — أخضر ثانوي
key_windowBackgroundWhiteRedText      — أحمر (خطأ)
key_dialogTextBlack
key_dialogTextGray
key_dialogTextGray2..4
key_dialogTextHint
key_dialogTextLink
key_dialogTextBlue
key_dialogTextBlue2..4
```

### ActionBar
```
key_actionBarDefault                  — خلفية الـ TopBar
key_actionBarDefaultIcon              — لون أيقونة TopBar
key_actionBarDefaultTitle             — لون العنوان
key_actionBarDefaultSubtitle          — لون العنوان الفرعي
key_actionBarDefaultSelector          — لون الضغط
key_actionBarWhiteSelector            — خلفية بيضاء
key_actionBarActionModeDefault        — خلفية action mode
key_actionBarActionModeDefaultIcon
key_actionBarActionModeDefaultTitle
key_actionBarActionModeDefaultSubtitle
key_actionBarActionModeDefaultSelector
```

### عناصر الإدخال (Input Fields)
```
key_windowBackgroundWhiteInputField           — خلفية
key_windowBackgroundWhiteInputFieldActivated   — عند التفعيل
key_dialogInputField
key_dialogInputFieldActivated
key_dialogSearchBackground
key_dialogSearchHint
key_dialogSearchIcon
key_dialogSearchText
```

### Checkbox / Radio / Switch
```
key_checkboxSquareBackground
key_checkboxSquareCheck
key_checkboxSquareUnchecked
key_checkboxSquareDisabled
key_dialogCheckboxSquareBackground
key_switchTrack
key_switchTrackChecked
key_switchTrackBlue
key_switchTrackBlueChecked
key_switchTrackBlueThumb
key_switchTrackBlueThumbChecked
key_switch2Track
key_switch2TrackChecked
key_dialogRadioBackground
key_dialogRadioBackgroundChecked
key_dialogRoundCheckBox
key_dialogRoundCheckBoxCheck
```

### Progress
```
key_progressCircle            — لون دائرة التحميل
key_dialogLineProgress
key_dialogLineProgressBackground
key_dialog_inlineProgress
key_dialog_inlineProgressBackground
```

### أزرار
```
key_dialogButton               — نص زر
key_dialogButtonSelector       — خلفية الزر
```

### Bubble (محادثات)
```
key_chat_inBubble              — خلفية فقاعة واردة
key_chat_inBubbleShadow
key_chat_inBubbleSelectedOverlay
key_chat_outBubble             — خلفية فقاعة صادرة
key_chat_outBubbleShadow
key_chat_outBubbleSelected
key_chat_outBubbleGradient1..3 — تدرج خلفية الفقاعة الصادرة
key_chat_inMessageText
key_chat_outMessageText
key_chat_messageTextIn         — نص الرسالة الواردة
key_chat_messageTextOut        — نص الرسالة الصادرة
key_chat_messageLinkIn         — روابط واردة
key_chat_messageLinkOut        — روابط صادرة
key_chat_inTextSelectionHighlight
key_chat_outTextSelectionHighlight
key_chat_inTextSelectionCursor
key_chat_outTextSelectionCursor
```

### Status icons (محادثات)
```
key_chat_outSentCheck          — ✓
key_chat_outSentCheckRead      — ✓✓
key_chat_outSentCheckSelected
key_chat_outSentClock
key_chat_outSentClockSelected
key_chat_outViews              — عدد المشاهدات
key_chat_outViewsSelected
key_chat_outMediaIcon
key_chat_outMediaIconSelected
key_chat_outMenu
key_chat_outMenuSelected
key_chat_inInstant             — ⚡
key_chat_outInstant
key_chat_inInstantSelected
key_chat_outInstantSelected
key_chat_inAudioProgress
key_chat_outAudioProgress
key_chat_inAudioSelected
key_chat_outAudioSelected
```

### Service Message
```
key_chat_serviceText
key_chat_serviceLink
key_chat_serviceIcon
key_chat_serviceBackground
key_chat_serviceBackgroundSelected
key_chat_serviceBackgroundSelector
```

### Media cells
```
key_sharedMediaStartStopLoadColor
key_sharedMediaLocationPlaceholderColor
key_sharedMediaPhotoPlaceholderColor
key_sharedMediaVideoPlaceholderColor
key_photoBackground
```

### Selector
```
key_listSelector                — خلفية التحديد للقوائم
key_settings_listSelector
key_dialogButtonSelector
key_dialogSwipeRemove           — عند السحب للحذف
```

### Floating Button
```
key_dialogFloatingButton        — زر عائم
key_dialogFloatingButtonPressed
key_dialogFloatingIcon
```

---

## ResourcesProvider (للثيمات المخصصة)

محادثة واحدة ممكن يكون لها ثيم خاص (لون مخصص للـ peer). في هذه الحالة،
استخدم `ResourcesProvider` بدلاً من استدعاء `Theme` مباشرة.

```java
// داخل ChatActivity، هذا الـ provider يمثل ثيم المحادثة الحالية
protected Theme.ResourcesProvider resourceProvider = new Theme.ResourcesProvider() {
    @Override
    public int getColor(int key) {
        return Theme.getColor(key, resourceProvider);  // قد يعيد اللون المخصص
    }

    @Override
    public Drawable getDrawable(String drawableKey) {
        return Theme.getThemeDrawable(drawableKey);
    }

    @Override
    public boolean isDark() {
        return Theme.isCurrentThemeDark();
    }

    @Override
    public boolean hasGradientService() {
        return /* ... */ false;
    }
};
```

**الاستخدام في View:**

```java
// بدلاً من
int color = Theme.getColor(Theme.key_chat_inBubble);

// استخدم
int color = Theme.getColor(Theme.key_chat_inBubble, resourceProvider);
```

> **القاعدة:** إذا كنت تبني View داخل ChatActivity، استخدم `resourceProvider`.
> إذا كنت تبني View في DialogsActivity/ProfileActivity (ليس داخل محادثة)،
> استخدم `Theme.getColor(key)` مباشرة.

---

## التعامل مع Dark/Light Mode

الثيم التلقائي يعتمد على إعدادات النظام. لكن المشروع يدعم:
- `Theme.isCurrentThemeDark()` — هل الثيم الحالي داكن؟
- `Theme.changeColorAccent(color)` — حوّل اللون بناءً على accent الثيم

```java
// 1) لون يتغير حسب dark/light
int color = Theme.getColor(Theme.key_windowBackgroundWhite);  // داكن: أسود، فاتح: أبيض

// 2) الـ Theme.Colorable interface — Views تستجيب لتغيير الثيم
public interface Colorable {
    public void updateColors();
}
// الكلاسات التي تنفذها: ActionBar, ActionBarLayout, SizeNotifierFrameLayout, ...

// 3) الاستماع لتغيير الثيم
NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.themeChanged);
// في didReceivedNotification:
if (id == NotificationCenter.themeChanged) {
    updateColors();  // أعد رسم الواجهة
}
```

---

## تطبيق SelectorDrawable

```java
// ضغط عام
view.setBackgroundDrawable(Theme.getSelectorDrawable(false));

// ضغط بخلفية بيضاء
view.setBackgroundDrawable(Theme.getSelectorDrawable(true));

// ضغط بلون مخصص
view.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 0));

// قناع مستطيل دائري
view.setBackgroundDrawable(Theme.createSelectorDrawable(color, 0, dp(8)));

// القناع (mask) محدد مسبقاً
view.setBackgroundDrawable(Theme.createSelectorDrawable(color, Theme.RECT_SELECTOR));
view.setBackgroundDrawable(Theme.createSelectorDrawable(color, Theme.ROUND_SELECTOR));
```

### Mask Types
- `0` — مستطيل عادي
- `Theme.RECT_SELECTOR`
- `Theme.ROUND_SELECTOR`
- `Theme.SELECTORRECTBGROUND` — مستطيل للخلفية

---

## Color Animations (تغيير اللون تدريجياً)

```java
// عند تبديل ثيم، يحدث تغيير اللون تلقائياً. لكن إذا أردت animate يدوياً:
Theme.setAnimatingColor(true);
Theme.setAnimatedColor(Theme.key_windowBackgroundWhite, newColor);
// ... do work
Theme.setAnimatingColor(false);
```

---

## إضافة لون جديد (Theme Key)

```java
// 1) في Theme.java — أضف:
public static final int key_my_brand_new_color = colorsCount++;

// 2) في defaultColors[][] — أضف القيمة الافتراضية للـ day theme و night theme
// (ابحث عن defaultColors داخل Theme.java)

// 3) في fallbackKey — حدد ما هو اللون الذي يعوّض عند عدم التعريف
// Theme.getFallbackKey(int key)
```

> **في العادة لا تحتاج لإضافة لون جديد.** ابحث أولاً — غالباً يوجد لون
> يناسب حالتك من الـ 777 الموجودة.

---

## Anti-Aliasing و Shader

```java
// لا تنسخ الـ Paint من Theme
Paint paint = Theme.getThemePaint(Theme.PAINT_CLEAR);

// احصل على paint من الثيم
Paint paint = Theme.getThemePaint("chat_messageTextPaint", resourceProvider);
```

---

## ملخص سريع

| الموقف | الكود |
|------|------|
| لون نص أساسي | `Theme.getColor(Theme.key_windowBackgroundWhiteBlackText)` |
| لون نص ثانوي | `Theme.getColor(Theme.key_windowBackgroundWhiteGrayText)` |
| لون رابط | `Theme.getColor(Theme.key_windowBackgroundWhiteLinkText)` |
| لون أزرق (تفاعل) | `Theme.getColor(Theme.key_windowBackgroundWhiteBlueText)` |
| لون خطأ | `Theme.getColor(Theme.key_dialogTextRed)` |
| لون نجاح | `Theme.getColor(Theme.key_windowBackgroundWhiteGreenText)` |
| خلفية شاشات | `Theme.getColor(Theme.key_windowBackgroundWhite)` |
| خلفية حوار | `Theme.getColor(Theme.key_dialogBackground)` |
| خلفية فقاعة واردة | `Theme.getColor(Theme.key_chat_inBubble)` |
| خلفية فقاعة صادرة | `Theme.getColor(Theme.key_chat_outBubble)` |
| لون ActionBar | `Theme.getColor(Theme.key_actionBarDefault)` |
| لون TopBar icon | `Theme.getColor(Theme.key_actionBarDefaultIcon)` |
| لون TopBar title | `Theme.getColor(Theme.key_actionBarDefaultTitle)` |

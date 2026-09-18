# HudGram Knowledge Base

> Base معرفية شاملة للمشروع موجهة لوكلاء البرمجة (AI / Human) الذين يعملون على
> **تطوير تجربة المستخدم والـ UI فقط** — بدون أي تعديل على بروتوكول Telegram (MTProto / TL).

## الهدف من هذه القاعدة
أن يستطيع أي وكيل:
- إضافة **نصوص مترجمة** (عربية/إنجليزية/أي لغة) بالصياغة الصحيحة
- استخدام **ألوان من ثيم تيليجرام** دون أي hardcoded color
- إضافة **شاشة جديدة** (Fragment/Activity) تتبع نفس أنماط المشروع
- استخدام **Layouts, Cells, Components, ActionBar** الموجودة بشكل صحيح
- التعامل مع **Dialogs, BottomSheets, Bulletins, Toasts** الموحّدة
- التعامل مع **Multi-Account** بأمان
- احترام **RTL / Dark mode / Dynamic theme** دون كسرها

---

## فهرس الملفات

| الملف | المحتوى |
|------|---------|
| [**AI-AGENT-PLAYBOOK.md**](./AI-AGENT-PLAYBOOK.md) | **دليل الوكلاء الذكيين — اقرأه أولاً** |
| [00-app-overview.md](./00-app-overview.md) | نظرة عامة على بنية التطبيق، الموديولات، نقطة الدخول |
| [01-architecture.md](./01-architecture.md) | طبقات النظام (Layers) وكيف تتواصل |
| [02-screen-catalog.md](./02-screen-catalog.md) | فهرس الشاشات (225 شاشة) وتصنيفها |
| [03-navigation.md](./03-navigation.md) | كيف تنتقل بين الشاشات (BaseFragment/ActionBarLayout) |
| [04-theme-colors.md](./04-theme-colors.md) | **دليل الألوان** — كيف تحصل على لون من الثيم |
| [05-translations.md](./05-translations.md) | **دليل الترجمة** — كيف تضيف نصاً مترجماً بشكل صحيح |
| [06-actionbar.md](./06-actionbar.md) | ActionBar و Menu (TopBar) |
| [07-recyclerlistview-and-cells.md](./07-recyclerlistview-and-cells.md) | القوائم و Cells الجاهزة |
| [08-dialogs-sheets-bulletins.md](./08-dialogs-sheets-bulletins.md) | النوافذ والقوائم المنبثقة والإشعارات |
| [09-custom-components.md](./09-custom-components.md) | الـ Custom Views الأساسية (BackupImageView, AvatarDrawable...) |
| [10-layout-and-dimensions.md](./10-layout-and-dimensions.md) | LayoutHelper, dp/sp, Gravity, RTL |
| [11-style-guide.md](./11-style-guide.md) | قواعد ستايل إلزامية لأي تعديل UI |
| [12-extending-without-protocol.md](./12-extending-without-protocol.md) | كيف تضيف ميزة UI دون لمس البروتوكول |
| [13-quick-recipes.md](./13-quick-recipes.md) | وصفات جاهزة (إضافة قائمة، إضافة شاشة، إضافة ترجمة...) |

---

## القواعد الذهبية (اقرأها أول)

1. **لا تستخدم أكواد لونية** — استخدم دائماً `Theme.getColor(Theme.key_…)` أو
   `Theme.ResourcesProvider.getColor(key)`.
2. **لا تضع نصوصاً في الكود** — استخدم `LocaleController.getString(R.string.YourKey)`.
3. **لا تنشئ Dialog/Snackbar عادي** — استخدم `AlertsCreator` أو `BottomSheet` أو
   `BulletinFactory`.
4. **كل شاشة يجب أن ترث `BaseFragment`** — لا تنشئ `Activity` إلا إذا كانت
   في `AndroidManifest.xml` كـ entry point.
5. **كل عملية حسابية للأبعاد بـ `dp()`** — لا تستخدم قيم `px` مباشرة.
6. **احترم الـ RTL** — استخدم `LayoutHelper.getAbsoluteGravityStart()` بدلاً من
   `Gravity.LEFT`، واستخدم `LocaleController.isRTL`.
7. **لا تستدعي `ConnectionsManager` أو `TLRPC` مباشرة** — هذا من اختصاص
   `MessagesController` وغيره، وأنت كمطوّر UI تستدعي دوال عالية المستوى فقط.
8. **كل واجهة يجب أن تدعم Dark Mode** — لا تعتمد على لون فاتح/غامق ثابت.

---

## رموز سريعة (Cheat Sheet)

```java
// لون من الثيم
int color = Theme.getColor(Theme.key_windowBackgroundWhite);

// لون من ثيم مخصص (مثل ثيم محادثة)
Theme.ResourcesProvider provider = chatActivity.getResourceProvider();
int color = Theme.getColor(Theme.key_chat_inBubble, provider);

// نص مترجم
String text = LocaleController.getString(R.string.YourKey);

// نص مترجم مع متغيرات
String text = LocaleController.formatString(R.string.WelcomeMsg, "أحمد");

// جمع/مفرد حسب اللغة
String text = LocaleController.formatPluralString("NewMessages", count);

// قائمة منبثقة (Bottom Sheet)
BottomSheet sheet = AlertsCreator.createSimpleAlert(ctx, title, message);
sheet.show();

// Toast/Bulletin
BulletinFactory.global().createSimpleBulletin(R.drawable.ic_check, text).show();

// إضافة عنصر في ActionBar
ActionBarMenuItem item = actionBar.createMenu().addItem(id, R.drawable.ic_edit);
item.setOnClickListener(v -> { /* ... */ });
```

---

## بنية المشروع (مختصرة)

```
app/src/main/java/org/telegram/
├── messenger/        # Business logic + utilities (لا تلمسه إلا بحذر)
│   ├── LocaleController.java    # الترجمة
│   ├── AndroidUtilities.java    # dp/sp/utilities
│   ├── MessagesController.java  # عمليات تيليجرام
│   └── ...
├── tgnet/            # بروتوكول MTProto (ممنوع اللمس)
│   ├── ConnectionsManager.java
│   └── TLRPC.java
└── ui/               # كل الـ UI (هنا تعمل)
    ├── ActionBar/    # Navigation, Theme, BaseFragment, ActionBar
    ├── Cells/        # عناصر القوائم (140 cell)
    ├── Components/   # Custom Views (485 component)
    └── *.java        # 225 Activity/Fragment
```

---

## للبدء السريع

- لإضافة **زر/قائمة**: اقرأ [06-actionbar.md](./06-actionbar.md) و [13-quick-recipes.md](./13-quick-recipes.md)
- لإضافة **شاشة جديدة**: اقرأ [03-navigation.md](./03-navigation.md) و [12-extending-without-protocol.md](./12-extending-without-protocol.md)
- لإضافة **ترجمة**: اقرأ [05-translations.md](./05-translations.md)
- لتغيير **لون/ثيم**: اقرأ [04-theme-colors.md](./04-theme-colors.md)
- لإضافة **عنصر قائمة (cell)**: اقرأ [07-recyclerlistview-and-cells.md](./07-recyclerlistview-and-cells.md)

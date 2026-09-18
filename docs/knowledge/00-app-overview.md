# 00 - نظرة عامة على التطبيق (App Overview)

## ما هو المشروع؟
مشروع **HudGram** — وهو تعديل (fork) على تطبيق **Telegram for Android v5.x**
(الاسم الرسمي للحزمة: `org.telegram.messenger`). مبني على كود Nikita Kudashov ومجتمع Telegram.

التطبيق يدعم:
- حسابات متعددة (حتى 4 حسابات في النسخة الحالية)
- محادثات فردية وجماعية وقنوات
- رسائل بريدية، بوتات، ملصقات، صور، فيديو، صوت
- مكالمات صوتية/فيديو (VoIP مبني على WebRTC)
- سِمات (Themes) مدمجة ومخصصة
- استبدال ثيم كامل (لون، خلفية، خط، OKLCH)
- وضع ليلي تلقائي
- محادثات سرية (E2E)
- أرشيف، فلاتر، فولدرات، قفل بكلمة سر، تمرير سري، وكل مزايا تيليجرام الحديثة

---

## نقطة الدخول (Entry Points)

تقرأ من `AndroidManifest.xml` في `TMessagesProj/src/main/AndroidManifest.xml`:

| المكون | الكلاس | الدور |
|--------|--------|------|
| `Application` | `org.telegram.messenger.ApplicationLoader` | تهيئة عامة، Push/Maps/Location providers |
| `Activity` | `org.telegram.ui.LaunchActivity` | أول Activity تُفتح بعد تسجيل الدخول، تستضيف التنقل |
| `Activity` | `org.telegram.ui.CallsActivity` | شاشة المكالمات المغلقة عند قفل الشاشة |
| `Activity` | `org.telegram.ui.BubbleActivity` | وضع الفقاعة (Bubble) |
| `Activity` | `org.telegram.ui.ShareActivity` | استقبال المشاركة من تطبيقات أخرى |
| `Activity` | `org.telegram.ui.ExternalActionActivity` | معالجة الـ Intent الخارجية |
| `Activity` | `org.telegram.ui.PopupNotificationActivity` | إشعار منبثق أثناء المكالمة |
| `Service` | `org.telegram.messenger.NotificationsService` | FCM push |
| `Service` | `org.telegram.messenger.LocationSharingService` | مشاركة موقع مباشر |
| `Service` | `org.telegram.messenger.MusicPlayerService` | مشغّل موسيقى الخلفية |
| `Service` | `org.telegram.messenger.voip.VoIPService` | خدمة المكالمات |
| `Service` | `org.telegram.messenger.VoIPPreNotificationService` | إنذار قبل المكالمة |
| `Service` | `org.telegram.messenger.KeepAliveJob` | إبقاء التطبيق حياً (Doze) |
| `Receiver` | `org.telegram.messenger.SmsReceiver` | قراءة SMS لكود الدخول |
| `Receiver` | `org.telegram.messenger.PushListenerController$GcmPushListenerService` | استلام Push |

> **ملاحظة:** كل UI "داخلي" بعد تسجيل الدخول هو **Fragment** يُدار داخل
> `LaunchActivity` عبر `ActionBarLayout` (ليس Activity مستقل).

---

## الموديولات الرئيسية (Packages)

```
org.telegram/
├── messenger/         ← طبقة Business Logic (174 ملف)
│   ├── AccountInstance.java          (Singleton للحسابات)
│   ├── BaseController.java           (أب الـ Controllers)
│   ├── ApplicationLoader.java        (App class)
│   ├── UserConfig.java               (بيانات الحساب الحالي)
│   ├── MessagesController.java       (أكبر ملف - 24k سطر)
│   ├── MessagesStorage.java          (قاعدة بيانات محلية)
│   ├── MediaDataController.java      (Stickers, GIFs, Saved)
│   ├── MediaController.java          (تشغيل وسائط)
│   ├── ContactsController.java       (جهات الاتصال)
│   ├── NotificationsController.java  (إشعارات)
│   ├── SendMessagesHelper.java       (إرسال رسائل)
│   ├── SecretChatHelper.java         (محادثات سرية)
│   ├── DownloadController.java       (التحميل التلقائي)
│   ├── LocationController.java       (GPS)
│   ├── ImageLoader.java              (تحميل صور)
│   ├── ImageReceiver.java            (عرض صورة في View)
│   ├── FileLoader.java               (تحميل ملفات)
│   ├── FileLoadOperation.java        (عملية تحميل واحدة)
│   ├── LocaleController.java         ★ الترجمة
│   ├── SharedConfig.java             (إعدادات المستخدم)
│   ├── AndroidUtilities.java         ★ dp/sp, utilities
│   ├── Emoji.java                    (خريطة الإيموجي)
│   ├── FileLog.java                  (تسجيل أخطاء)
│   ├── NotificationCenter.java       (Event bus داخلي)
│   ├── BuildVars.java                (ثوابت build)
│   └── ...
│
├── tgnet/             ← بروتوكول MTProto (ممنوع)
│   ├── ConnectionsManager.java
│   ├── TLRPC.java                   (78k سطر - كل الـ TL schema)
│   ├── SerializedData.java
│   ├── NativeByteBuffer.java
│   └── tl/                          (TL methods مفصولة)
│
└── ui/                ← طبقة UI (هنا تعمل أنت)
    ├── ActionBar/      (29 ملف)
    │   ├── BaseFragment.java       ★ الأب لكل الشاشات
    │   ├── ActionBar.java          ★ الـ TopBar
    │   ├── ActionBarLayout.java    (Navigation stack)
    │   ├── ActionBarMenu.java
    │   ├── ActionBarMenuItem.java
    │   ├── Theme.java              ★ 777 لون + أدوات
    │   ├── AlertDialog.java        ★ نافذة تأكيد
    │   ├── BottomSheet.java        ★ نافذة سفلية
    │   ├── BottomSheetTabs.java
    │   ├── DrawerLayoutContainer.java
    │   └── INavigationLayout.java
    │
    ├── Cells/          (140 cell جاهز)
    │   ├── BaseCell.java           (أب الخلايا)
    │   ├── TextCell.java           (عنصر قائمة بسيط)
    │   ├── TextSettingsCell.java   (عنصر إعدادات)
    │   ├── TextDetailCell.java
    │   ├── TextCheckCell.java      (Toggle)
    │   ├── TextInfoCell.java       (نص توضيحي)
    │   ├── HeaderCell.java         (عنوان قسم)
    │   ├── ShadowSectionCell.java  (فاصل)
    │   ├── DividerCell.java
    │   ├── DialogCell.java         (صف محادثة)
    │   ├── ChatMessageCell.java    (رسالة محادثة)
    │   ├── UserCell.java           (صف مستخدم)
    │   └── ... (135 آخر)
    │
    ├── Components/     (485 custom view)
    │   ├── RecyclerListView.java   ★ قائمة محسّنة
    │   ├── BackupImageView.java    ★ عرض صورة
    │   ├── AvatarDrawable.java     ★ رسم أفاتار
    │   ├── AlertsCreator.java      ★ نوافذ جاهزة
    │   ├── BulletinFactory.java    ★ Toasts
    │   ├── LayoutHelper.java       ★ منشئ LayoutParams
    │   ├── SizeNotifierFrameLayout (FrameLayout يكبر مع الكيبورد)
    │   ├── BlurView / CubicBezierInterpolator
    │   └── ... (480 آخر)
    │
    ├── Adapters/       (14 Adapter لـ RecyclerView)
    │   └── DialogsAdapter.java / SearchAdapter.java / ...
    │
    ├── ChatActivity.java        ★ (45k سطر) - شاشة المحادثة
    ├── DialogsActivity.java     ★ شاشة قائمة المحادثات
    ├── ProfileActivity.java     ★ شاشة البروفايل
    ├── SettingsActivity.java    ★ شاشة الإعدادات
    ├── LaunchActivity.java      (8k سطر) - entry Activity
    ├── LoginActivity.java
    └── ... (105+ Activity آخر)
```

---

## كيف يعمل التطبيق (High-Level)

```
[ApplicationLoader.onCreate()]
        │
        ▼
[LaunchActivity] (بعد login)
        │
        ▼
[ActionBarLayout] ── Navigation Stack
        │
        ├─ [DialogsActivity]      (الشاشة الرئيسية)
        │       └─ RecyclerListView of DialogCell
        │
        ├─ [ChatActivity]         (عند الضغط على محادثة)
        │       └─ RecyclerListView of ChatMessageCell
        │
        ├─ [ProfileActivity]      (عند الضغط على اسم)
        ├─ [SettingsActivity]
        ├─ [MediaActivity]        (الوسائط المشتركة)
        └─ [أي BaseFragment آخر]

كل Fragment يستهلك البيانات عبر:
   AccountInstance.getInstance(currentAccount)
        └─ .getMessagesController()    →  Chat/Dialog/Message cache
        └─ .getMessagesStorage()      →  DB queries
        └─ .getConnectionsManager()   →  network (لا تستدعيه يدوياً)
        └─ .getUserConfig()           →  current account data
```

---

## الـ Multi-Account

التطبيق يدعم **4 حسابات في نفس الوقت**:
- `UserConfig.selectedAccount` ← الـ account النشط حالياً
- `UserConfig.MAX_ACCOUNT_COUNT = 4`
- `AccountInstance.getInstance(num)` ← Singleton لكل account
- كل `BaseFragment` له `currentAccount` و يستدعي `getMessagesController()` على الـ account الصحيح

> **كقاعدة UI:** لا تحتاج للتعامل مع Multi-Account يدوياً.
> `BaseFragment` يربطك بالـ account الصحيح تلقائياً.
> فقط إذا كنت تكتب **Service أو Receiver**، حدد الـ account.

---

## لا تلمس

| المجلد | السبب |
|--------|------|
| `tgnet/` | بروتوكول MTProto. أي تغيير هنا قد يكسر الاتصال بالسيرفرات |
| `TMessagesProj/jni/tgnet/` | تنفيذ C++ للبروتوكول |
| `TMessagesProj/jni/voip/webrtc/` | تنفيذ WebRTC من جوجل |
| `tgnet/tl/TL_*.java` | مخططات TL — تولّد من schema |
| `MessagesController.java`, `MessagesStorage.java` | منطق حساس — افهمها قبل ما تلمسها |

**يمكنك** (بل يُفضّل) العمل على:
- `ui/ActionBar/Theme.java` (إضافة ألوان جديدة بحذر)
- `ui/ActionBar/BaseFragment.java` (نادراً)
- `ui/Cells/`, `ui/Components/`, `ui/*.java` (كل الشاشات)
- `res/values*/strings.xml` (الترجمات)
- `messenger/SharedConfig.java` (إضافة إعدادات جديدة)

---

## مرجع سريع للأسطر المهمة

| الملف | السطر | الوصف |
|------|------|------|
| `messenger/ApplicationLoader.java:51` | class | نقطة دخول التطبيق |
| `messenger/AccountInstance.java:8` | class | مدير الحسابات |
| `messenger/BaseController.java:6` | class | أب كل Controller |
| `messenger/LocaleController.java:61` | class | الترجمة |
| `messenger/AndroidUtilities.java:245` | class | dp/sp/utilities |
| `messenger/UserConfig.java:24` | class | بيانات الحساب |
| `messenger/NotificationCenter.java:24` | class | Event bus |
| `ui/LaunchActivity.java:248` | class | أول Activity |
| `ui/ActionBar/BaseFragment.java:72` | abstract class | أب كل الشاشات |
| `ui/ActionBar/ActionBar.java:74` | class | الـ TopBar |
| `ui/ActionBar/Theme.java:153` | class | نظام الألوان |
| `ui/ActionBar/AlertDialog.java:89` | class | Dialog عادي |
| `ui/ActionBar/BottomSheet.java` | class | Bottom sheet |
| `ui/Components/RecyclerListView.java:98` | class | قائمة محسّنة |
| `ui/Components/AlertsCreator.java:174` | class | مصنع الـ Dialogs |
| `ui/Components/BulletinFactory.java:62` | class | Toasts |
| `ui/Components/LayoutHelper.java:25` | class | منشئ LayoutParams |

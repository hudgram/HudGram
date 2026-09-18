# 12 - توسيع التطبيق بدون لمس البروتوكول (Extending Without Protocol)

## القاعدة الذهبية

> **بروتوكول Telegram (MTProto / tgnet / TL_schema) مقدّس — لا تلمسه.**
> أي ميزة UI جديدة **يجب** أن تستهلك البيانات من Controllers الموجودة.

---

## ما هو "البروتوكول"؟

كل ما يتعلق **بالاتصال بسيرفرات Telegram**:
- `org.telegram.tgnet.*` (الكلاسات الـ Java)
- `TMessagesProj/jni/tgnet/*` (الـ C++)
- `org.telegram.messenger.voip.webrtc.*` (WebRTC)
- `org.telegram.tgnet.tl.TL_*.java` (TL schema)
- `org.telegram.tgnet.TLRPC.java` (Generated types)
- `org.telegram.tgnet.ConnectionsManager.java` (low-level connections)

> **استثناء وحيد:** `MessagesController.java` و `MediaDataController.java` و
> `NotificationsController.java` هي Controllers عالية المستوى — **تستدعيها**،
> لكنك لا تنشئ methods جديدة في `ConnectionsManager` أو تضيف TL.

---

## كيف تحصل على البيانات

### 1) من Cache (البيانات المحفوظة في الذاكرة)
```java
// المستخدمين
TLRPC.User user = getMessagesController().getUser(userId);
SparseArray<TLRPC.User> users = getMessagesController().users;  // كل المستخدمين

// المحادثات
ArrayList<TLRPC.Dialog> dialogs = getMessagesController().getAllDialogs();
TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(dialogId);

// الرسائل
ArrayList<MessageObject> messages = getMessagesController().getMessageList(dialogId, type);
MessageObject message = getMessagesController().getMessage(messageId);

// الـ Chat
TLRPC.Chat chat = getMessagesController().getChat(chatId);
TLRPC.ChatFull chatInfo = getMessagesController().getChatFull(chatId);

// الـ Stickers/Media
ArrayList<TLRPC.Document> stickers = getMediaDataController().getStickers(stickerSetNum);
```

### 2) من قاعدة البيانات المحلية
```java
// Load messages
getMessagesStorage().loadMessages(dialogId, count, maxId, fromUid, replyThread, classGuid, loadType, lastReadIdString, isTopic, topicId, 0, false, new MessagesStorage.IntCallback() {
    @Override
    public void run(int resultCount, LongSparseArray<...> data, ...) {
        // loaded
    }
});

// Load dialogs
getMessagesStorage().getDialogs(folderId, ...);

// Search messages
getMessagesStorage().searchMessages(...);
```

### 3) من MediaDataController (stickers, GIFs, emoji)
```java
// Sticker sets
getMediaDataController().getStickerSets(new IntCallback() { ... });

// Emojis
getMediaDataController().getEmojiPacks(...);

// Saved stickers
getMediaDataController().getSavedStickers();
```

---

## أنماط شائعة للتوسع

### 1) إضافة شاشة جديدة (بدون بيانات جديدة)
```java
// فقط UI — البيانات من Controllers الموجودة
public class MySettingsFragment extends BaseFragment {
    @Override
    public View createView(Context context) {
        // ... use existing controllers for data
    }
}
```

### 2) إضافة مزخرفة (decorator) لخلية موجودة
```java
// بدلاً من إنشاء cell جديدة، ورث من cell موجودة
public class MyTextCell extends TextCell {
    @Override
    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, MeasureSpec.makeMeasureSpec(dp(72), MeasureSpec.EXACTLY));
    }
}
```

### 3) إضافة ثيم جديد (Theme Accent)
```java
// في Theme.java
public static final int key_my_new_brand_color = colorsCount++;
// ثم في defaultColors[][] أضف القيم
```

### 4) إضافة خلفية (Wallpaper) مخصصة
- في `WallpapersListActivity`، أضف الخلفية
- في `BackgroundGradientDrawable`، أضف الـ gradient

### 5) إضافة "Premium Feature" للـ UI
- في `PremiumGradient`، أضف التصميم
- في `StarsController` (إذا متاح)، أضف المنطق
- **لا تنشئ methods جديدة في ConnectionsManager** (هذا ممنوع)

### 6) إضافة ترجمة
- فقط أضف في `values-ar/strings.xml` (راجع [05-translations.md](./05-translations.md))

### 7) إضافة تاب جديد في الإعدادات
- في `SettingsActivity` (أو `MainTabsActivity`)، أضف تاب
- الـ Fragment الجديد يرث `BaseFragment`

### 8) إضافة فلتر/مجلد مخصص
- البيانات من `MessagesController.getDialogsByFolder(folderId)`
- الواجهة من `FiltersView` (في `ui/Adapters/`)

---

## ما يجب تجنبه

### ❌ لا تضيف TL methods جديدة
```java
// ❌ ممنوع: في tgnet/tl/TL_*.java
public static class TL_myNewRequest extends TLObject { ... }
```

### ❌ لا تنشئ connections جديدة
```java
// ❌ ممنوع
getConnectionsManager().sendRequest(new TL_myNewRequest(...));
```

### ❌ لا تضيف دوال في MessagesController ترسل requests جديدة
```java
// ❌ ممنوع (إلا إذا كنت تعرف تماماً ما تفعل)
public void myNewServerRequest() {
    TL_req_my_new_method req = new TL_req_my_new_method();
    ConnectionsManager.getInstance(currentAccount).sendRequest(req, ...);
}
```

### ❌ لا تلمس C++ code في `jni/`
- `jni/tgnet/` — MTProto C++ — ممنوع
- `jni/voip/webrtc/` — WebRTC — ممنوع
- `jni/tgcalls/` — Group calls — ممنوع

---

## ما يمكنك فعله بأمان

### ✅ أضف UI جديدة
- Fragment / Activity جديد
- Cell جديدة
- Custom View جديدة
- Dialog / BottomSheet / Bulletin

### ✅ أضف ترجمات
- فقط في `values-*/strings.xml`

### ✅ أضف موارد UI
- Drawable في `res/drawable/`
- Layout في `res/layout/`
- Animation في `res/anim/`

### ✅ أضف إعدادات
- في `SharedConfig.java` (احذر، file كبير)
- أو استخدم `SharedPreferences` منفصلة

### ✅ أضف ألوان ثيم جديدة
- في `Theme.java`، أضف `key_my_color = colorsCount++`
- في `defaultColors`، أضف القيم

### ✅ أضف Notification Observer
- اشترك في `NotificationCenter.events`
- لا تنشئ event جديدة إلا إذا لزم

---

## نمط: "استخدم ما هو موجود"

### مثال: "أريد عرض تاريخ إنشاء المستخدم"

```java
// ❌ غلط: أنشئ request جديد لجلب تاريخ الإنشاء
public void loadUserCreationDate(long userId) {
    // ❌ ممنوع
    TL_users_getFullUser req = new TL_users_getFullUser();
    req.id = new TL_inputUser(userId);
    ConnectionsManager.getInstance(currentAccount).sendRequest(req, ...);
}

// ✅ صح: استخدم البيانات الموجودة في UserFull
TLRPC.UserFull userFull = getMessagesController().getUserFull(userId);
if (userFull != null) {
    int creationDate = userFull.about != null ? ... : -1;  // ربما غير متاح
    // أو ابحث عن حقل موجود في TLRPC.UserFull
}
```

### مثال: "أريد إضافة ستاتيك مخصصة للـ user"

```java
// ❌ غلط: أنشئ request جديد
public void loadCustomStatus() {
    // ❌ ممنوع
    TL_account_getCustomStatus req = new TL_account_getCustomStatus();
    ConnectionsManager.getInstance(currentAccount).sendRequest(req, ...);
}

// ✅ صح: استخدم User.status الموجود
TLRPC.User user = getMessagesController().getUser(userId);
if (user != null && user.status != null) {
    if (user.status instanceof TLRPC.TL_userStatusOnline) {
        // online now
    } else if (user.status instanceof TLRPC.TL_userStatusOffline) {
        // offline, was online at user.status.was_online
    }
}
```

### مثال: "أريد إرسال رسالة عند event معين"

```java
// ❌ غلط: أنشئ method في SendMessagesHelper
public void sendCustomMessage(...) {
    // ... custom logic
}

// ✅ صح: استخدم SendMessagesHelper.sendMessage الموجود
getSendMessagesHelper().sendMessage(messageText, dialogId, ...);
```

---

## حدود "ما يمكنك فعله" في Controllers

### ✅ مسموح: إضافة methods خفيفة (قراءة فقط)
```java
// في MessagesController.java
public List<TLRPC.User> getMyFriends() {
    List<TLRPC.User> result = new ArrayList<>();
    for (TLRPC.User user : users.values()) {
        if (user.mutual_contact) {
            result.add(user);
        }
    }
    return result;
}
```

### ⚠️ بحذر: إضافة method ترسل request (تحتاج اختبار)
```java
// لا تلمسها إلا إذا كنت تعرف ماذا تفعل
```

### ❌ ممنوع: تعديل بنية الـ TL أو الـ serialization
```java
// ❌ في TLRPC.java
public class TLRPC {
    // ❌ لا تضيف classes جديدة هنا
    public static class TL_myNewType extends TLObject { ... }
}
```

---

## ملخص: مسموح / ممنوع

| النشاط | مسموح |
|------|------|
| إضافة Fragment / Cell / View | ✅ |
| إضافة ترجمة | ✅ |
| إضافة Drawable / Animation | ✅ |
| إضافة Setting في `SharedConfig` | ✅ |
| إضافة Theme Color | ✅ |
| الاشتراك في `NotificationCenter` | ✅ |
| قراءة من Controllers (getter calls) | ✅ |
| تعديل `MessagesController` بإضافة **getter** (read-only) | ✅ بحذر |
| إنشاء TL method جديدة | ❌ |
| إضافة request sender في Controller | ❌ بحذر شديد |
| تعديل C++ | ❌ |
| تعديل `TLRPC.java` | ❌ |
| تعديل `ConnectionsManager.java` | ❌ |

---

## إذا احتجت فعلاً method جديدة (Server-side)

هذا **نادر الحدوث**. في 99% من الحالات، البيانات موجودة.

إذا احتجت ذلك:
1. راجع **Telegram API docs**: https://core.telegram.org/api
2. **قدّم طلب على** https://github.com/DrKLO/Telegram أو
3. **في المشروع المخصص** (hudgram)، إذا كان عندك backend خاص
4. **كحل أخير**: fork وأنشئ TL جديد في `tgnet/tl/TL_custom.java`، أضف
   `ConnectionsManager.sendRequest`، لكن **اعلم أن هذا يكسر التوافق مع
   السيرفرات الرسمية**

> **القاعدة:** كل ميزة جديدة من Telegram الرسمية تأتي من upstream
> (`DrKLO/Telegram` على GitHub). اجمع التحديثات، لا تخترع.

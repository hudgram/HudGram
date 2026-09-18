# 01 - المعمارية (Architecture)

## الطبقات الأساسية

```
┌────────────────────────────────────────────────────────┐
│  Presentation (UI)   →  org.telegram.ui.*              │
│  - Activities, Fragments, Cells, Components            │
│  - يحوي State خاص بالشاشة فقط                          │
│  - يستهلك بيانات من Business Layer عبر Observers       │
└────────────────────────────────────────────────────────┘
                       ▲
                       │  AccountInstance.getXxx()
                       │
┌────────────────────────────────────────────────────────┐
│  Business Logic       →  org.telegram.messenger.*      │
│  - Controllers (Messages, Media, Contacts...)          │
│  - Caches, State, Listeners                            │
│  - يخفي البروتوكول عن UI                               │
└────────────────────────────────────────────────────────┘
                       ▲
                       │
┌────────────────────────────────────────────────────────┐
│  Protocol (MTProto)   →  org.telegram.tgnet.*          │
│  - TLRPC, ConnectionsManager, SerializedData           │
│  - ممنوع اللمس                                         │
└────────────────────────────────────────────────────────┘
```

---

## الـ BaseController Pattern

كل Controller في `messenger/` يرث `BaseController`:

```java
// messenger/BaseController.java
public class BaseController {
    protected final int currentAccount;
    private AccountInstance parentAccountInstance;

    public BaseController(int num) {
        parentAccountInstance = AccountInstance.getInstance(num);
        currentAccount = num;
    }

    protected final AccountInstance getAccountInstance() { ... }
    protected final MessagesController getMessagesController() { ... }
    protected final ContactsController getContactsController() { ... }
    protected final MediaDataController getMediaDataController() { ... }
    protected final ConnectionsManager getConnectionsManager() { ... }
    protected final NotificationCenter getNotificationCenter() { ... }
    protected final UserConfig getUserConfig() { ... }
    protected final MessagesStorage getMessagesStorage() { ... }
    protected final NotificationsController getNotificationsController() { ... }
    protected final DownloadController getDownloadController() { ... }
    protected final SendMessagesHelper getSendMessagesHelper() { ... }
    protected final LocationController getLocationController() { ... }
    protected final AppGlobalConfig getAppGlobalConfig() { ... }
    protected final PersistColorPalette getColorPalette() { ... }
}
```

> **فائدة لمطوّر UI:** أنت لا تنشئ Controller — تستهلكه فقط.
> داخل `BaseFragment` توجد `protected` getters جاهزة (انظر [03-navigation.md](./03-navigation.md)).

---

## AccountInstance Pattern

```java
// messenger/AccountInstance.java
public class AccountInstance {
    private int currentAccount;
    private static volatile AccountInstance[] Instance = new AccountInstance[4];

    public static AccountInstance getInstance(int num) { ... }  // Singleton

    // Getters لكل Controller
    public MessagesController getMessagesController() { ... }
    public MessagesStorage getMessagesStorage() { ... }
    public ConnectionsManager getConnectionsManager() { ... }
    public NotificationsController getNotificationsController() { ... }
    public NotificationCenter getNotificationCenter() { ... }
    public UserConfig getUserConfig() { ... }
    public DownloadController getDownloadController() { ... }
    public SendMessagesHelper getSendMessagesHelper() { ... }
    public MediaDataController getMediaDataController() { ... }
    public ContactsController getContactsController() { ... }
    public LocationController getLocationController() { ... }
    public GiftAuctionController getGiftAuctionsController() { ... }
    public PersistColorPalette getColorPalette() { ... }
}
```

**طريقة الاستخدام من `BaseFragment`:**

```java
public class MyFragment extends BaseFragment {
    @Override
    public View createView(Context context) {
        // بدلاً من: AccountInstance.getInstance(currentAccount).getMessagesController()
        // استخدم:
        getMessagesController().loadDialogs(...);
        getUserConfig().clientUserId;
        getMessagesStorage().loadMessages(...);
    }
}
```

---

## NotificationCenter (Event Bus داخلي)

```java
// messenger/NotificationCenter.java
public class NotificationCenter {
    public static final int dialogsNeedReload = 1;
    public static final int closeChats = 2;
    public static final int messagesDeleted = 3;
    public static final int messageReceivedByServer = ...;
    public static final int updateInterfaces = ...;
    // ... أكثر من 100 event
    public static final int didReceivedNewMessages = ...;
    public static final int messagesRead = ...;
    public static final int dialogsUnreadCounterChanged = ...;
    public static final int updateUserStatus = ...;
    public static final int didUpdateUserPhotos = ...;
    public static final int didUpdateGroupIcons = ...;
    public static final int didUpdateMessagesViews = ...;
    public static final int reactionsDidLoad = ...;
    public static final int themeAccentListUpdated = ...;
    public static final int nightModeEnabled = ...;
    public static final int locationPermissionDenied = ...;
    // ... (200+ event)
}
```

**الاشتراك في حدث:**

```java
NotificationCenter.NotificationCenterDelegate delegate = new NotificationCenter.NotificationCenterDelegate() {
    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.messagesRead) {
            // تحديث الواجهة
            updateList();
        }
    }
};

// في onFragmentCreate
NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.messagesRead);
// + في onFragmentDestroy
NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.messagesRead);
```

**إرسال حدث:**

```java
NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload);
```

**أنماط الأحداث الشائعة:**

| الحدث | المعاملات (args) |
|------|-----------------|
| `didReceivedNewMessages` | `ArrayList<MessageObject>`, `boolean` |
| `updateInterfaces` | `int messageId`, `boolean` |
| `messagesDeleted` | `ArrayList<Integer> ids`, `ArrayList<Long> dialogIds`, `boolean` |
| `dialogsUnreadCounterChanged` | (لا شيء) |
| `themeChanged` | (لا شيء) — عند تبديل الثيم |
| `nightModeEnabled` | (لا شيء) |
| `didUpdateUserStatus` | `long userId` |
| `messagesRead` | `long dialogId`, `int lastReadId`, `boolean` |
| `fileLoaded` | `File file`, `int type`, `boolean` |
| `mediaCountDidLoad` | `long dialogId`, `int type`, `int count` |

---

## المسارات الحرجة (Hot Paths)

### 1. عند تشغيل التطبيق
```
ApplicationLoader.onCreate()
   → init push/maps/location providers
   → LocaleController (تحميل لغة الجهاز)
   → Theme (تحميل الثيم المحفوظ)
   → DispatchQueuePool (تهيئة الـ threads)
```

### 2. تسجيل الدخول
```
LoginActivity
   → phoneNumber
   → code
   → ConnectionsManager.sendRequest(auth.sendCode)
   → getMessagesController().getUser()  // يحفظ في UserConfig
   → MessagesController  // يبدأ جلب البيانات
   → MessagesStorage     // يفتح DB
   → LaunchActivity
```

### 3. فتح محادثة
```
DialogsActivity.onItemClick
   → presentFragment(new ChatActivity(args))
   → ChatActivity.createView
   → MessagesController.getDialogMessages()
   → ChatActivityAdapter (RecyclerView)
   → ChatMessageCell (item view)
```

### 4. إرسال رسالة
```
ChatActivity.sendMessage
   → SendMessagesHelper.sendMessage  (في BaseController)
   → ConnectionsManager.sendRequest  (mtproto — لا تلمسه)
   → NotificationCenter.postNotificationName(messageReceivedByServer)
   → ChatActivity يدث الـ RecyclerView
```

---

## Pattern الـ "Fragment مع State"

كل `BaseFragment` يتبع نفس النمط:

```java
public class MyFragment extends BaseFragment {
    private SomeAdapter adapter;
    private RecyclerListView listView;
    private ActionBarMenuItem searchItem;

    @Override
    public View createView(Context context) {
        // 1. ActionBar
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString(R.string.MyTitle));
        actionBar.setAllowOverlayTitle(true);

        // 2. Menu
        ActionBarMenu menu = actionBar.createMenu();
        searchItem = menu.addItem(search_id, R.drawable.ic_search_white);
        searchItem.setIsSearchField(true).setActionBarMenuItemSearchListener(...);

        // 3. Content
        fragmentView = new FrameLayout(context);
        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(adapter = new SomeAdapter());
        ((FrameLayout) fragmentView).addView(listView);

        // 4. Observers
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.eventX);

        return fragmentView;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.eventX);
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.eventX) {
            // تحديث الـ adapter
        }
    }
}
```

---

## مخططات الاعتماديات (Dependency Map)

```
BaseFragment
   │
   ├─ uses ─→ ActionBar (TopBar)
   │              └─ has ─→ ActionBarMenu
   │                            └─ has ─→ ActionBarMenuItem
   │
   ├─ uses ─→ RecyclerListView (قائمة)
   │              └─ uses ─→ LayoutHelper
   │
   ├─ uses ─→ Theme.getColor(...)   (ألوان)
   │              └─ uses ─→ ResourcesProvider (للـ per-chat themes)
   │
   ├─ uses ─→ LocaleController.getString(...)  (نصوص)
   │
   ├─ uses ─→ AndroidUtilities.dp(...)  (أبعاد)
   │
   ├─ extends (protected getters) ─→ BaseController
   │                                       │
   │                                       └─ uses ─→ AccountInstance
   │                                                              │
   │                                                              └─ has ─→ MessagesController
   │                                                              └─ has ─→ MediaDataController
   │                                                              └─ has ─→ MessagesStorage
   │                                                              └─ ... (16 controller)
   │
   └─ uses ─→ NotificationCenter (events)
```

---

## اعتبارات الأداء

| الشيء | القاعدة |
|------|--------|
| إعادة رسم الـ RecyclerView | استخدم `notifyItemChanged(index)` بدلاً من `notifyDataSetChanged()` |
| تحميل صور | استخدم `ImageReceiver` مع `setRoundRadius` و `setShouldGenerateQualityThumb` |
| Color animation | استخدم `Theme.setAnimatingColor(true)` ثم `setAnimatedColor(key, color)` — لا تعدّل يدوياً |
| Theme switch | لا تعيد إنشاء الـ Views — `Theme.applyTheme()` يحدّث الألوان تلقائياً |
| Multi-Account | تذكّر تمرير `currentAccount` لكل استدعاء على AccountInstance |
| Handler | استخدم `AndroidUtilities.runOnUIThread(runnable)` للنقل إلى UI thread |
| Backpressure | الـ Listeners قد تأتي من thread خلفي — ضع `runOnUIThread` عند تحديث View |

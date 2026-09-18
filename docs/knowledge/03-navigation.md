# 03 - التنقل بين الشاشات (Navigation)

## كيف تنتقل من شاشة لشاشة

**كل التنقل يدور حول 3 كلاسات:**
1. `BaseFragment` — الشاشة (الـ View + State)
2. `INavigationLayout` — مكدس التنقل (back stack)
3. `ActionBarLayout` — التنفيذ الفعلي لـ Navigation

---

## القاعدة الذهبية

> **لا تنشئ `Activity` جديدة إلا إذا كانت في `AndroidManifest.xml` كـ entry point
> لهاتف أو لميزة نظام.** أي شاشة جديدة = `BaseFragment` جديد.

```java
// ❌ غلط: إنشاء Activity جديدة
Intent intent = new Intent(ctx, MyActivity.class);
startActivity(intent);

// ✅ صح: عرض Fragment
presentFragment(new MyFragment());
```

---

## BaseFragment — الأب

```java
// messenger/BaseController-like for UI
public abstract class BaseFragment {
    public boolean isFinished;
    protected boolean finishing;
    public Dialog visibleDialog;
    protected int currentAccount = UserConfig.selectedAccount;

    public View fragmentView;
    protected INavigationLayout parentLayout;
    protected ActionBar actionBar;
    protected Theme.ResourcesProvider resourceProvider;

    // دورة الحياة
    public boolean onFragmentCreate() { return true; }
    public void onFragmentDestroy() { ... }
    public void onResume() { ... }
    public void onPause() { ... }
    public void onConfigurationChanged(Configuration newConfig) {}
    public void onUserLeaveHint() {}
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {}
    public void onRequestPermissionsResultFragment(...) {}

    // الـ View
    public View createView(Context context) { ... }   // تنشئه أنت
    public View getFragmentView() { return fragmentView; }

    // الـ ActionBar
    public ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context, resourceProvider);
        return actionBar;
    }
    public ActionBar getActionBar() { return actionBar; }

    // Navigation
    public boolean presentFragment(BaseFragment fragment) { ... }
    public boolean presentFragment(BaseFragment fragment, boolean removeLast) { ... }
    public boolean presentFragment(INavigationLayout.NavigationParams params) { ... }
    public void finishFragment() { ... }
    public boolean finishFragment(boolean animated) { ... }
    public void removeSelfFromStack() { ... }
    public boolean onBackPressed() { return false; }

    // Context / Parent
    public Context getContext() { ... }
    public Activity getParentActivity() { ... }
    public INavigationLayout getParentLayout() { ... }
    public LaunchActivity getParentActivityAsLaunchActivity() { ... }

    // Dialogs
    public boolean dismissCurrentDialog() { ... }
    public Dialog showDialog(Dialog dialog) { ... }

    // Helpers — get controllers without manual lookup
    protected final int getCurrentAccount() { return currentAccount; }
    protected final MessagesController getMessagesController() { ... }
    protected final MessagesStorage getMessagesStorage() { ... }
    protected final SendMessagesHelper getSendMessagesHelper() { ... }
    protected final NotificationsController getNotificationsController() { ... }
    protected final MediaDataController getMediaDataController() { ... }
    protected final ContactsController getContactsController() { ... }
    protected final LocationController getLocationController() { ... }
    protected final DownloadController getDownloadController() { ... }
    protected final ConnectionsManager getConnectionsManager() { ... }
    protected final UserConfig getUserConfig() { ... }
    protected final NotificationCenter getNotificationCenter() { ... }
    protected final SecretChatHelper getSecretChatHelper() { ... }
    protected final ImageUpdater getImageUpdater() { ... }
}
```

### قائمة الـ Getters المتاحة في BaseFragment

كلها `protected`، أي متاحة داخل الـ Fragment مباشرة:

| Getter | ترجع |
|------|------|
| `getMessagesController()` | `MessagesController` |
| `getMessagesStorage()` | `MessagesStorage` |
| `getSendMessagesHelper()` | `SendMessagesHelper` |
| `getNotificationsController()` | `NotificationsController` |
| `getMediaDataController()` | `MediaDataController` |
| `getContactsController()` | `ContactsController` |
| `getLocationController()` | `LocationController` |
| `getDownloadController()` | `DownloadController` |
| `getConnectionsManager()` | `ConnectionsManager` (لا تستدعيه — استخدم Controllers) |
| `getUserConfig()` | `UserConfig` |
| `getNotificationCenter()` | `NotificationCenter` |
| `getSecretChatHelper()` | `SecretChatHelper` |
| `getImageUpdater()` | `ImageUpdater` |
| `getAccountInstance()` | `AccountInstance` |
| `getCurrentAccount()` | `int` |

---

## إنشاء Fragment جديد — الهيكل الأساسي

```java
package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;

public class MyNewFragment extends BaseFragment {

    @Override
    public boolean onFragmentCreate() {
        // اشترك في الأحداث قبل إنشاء الـ View
        NotificationCenter.getInstance(currentAccount).addObserver(this,
            NotificationCenter.didReceivedNewMessages);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(currentAccount).removeObserver(this,
            NotificationCenter.didReceivedNewMessages);
    }

    @Override
    public View createView(Context context) {
        // 1) أنشئ الـ ActionBar
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString(R.string.MyTitle));
        actionBar.setAllowOverlayTitle(true);

        // 2) أنشئ محتوى الشاشة
        fragmentView = new FrameLayout(context);
        // ...

        return fragmentView;
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didReceivedNewMessages) {
            // حدث الواجهة
        }
    }
}
```

> **ملاحظة:** لا تحتاج لتنفيذ `NotificationCenter.NotificationCenterDelegate`
> في `BaseFragment` — الـ base class ينفذه تلقائياً. فقط override `didReceivedNotification`.

---

## التنقل — كل العمليات الممكنة

```java
// عرض Fragment فوق الحالي
presentFragment(new SomeFragment());

// عرض وإزالة الحالي من المكدس
presentFragment(new SomeFragment(), true);

// عرض مع إعدادات
INavigationLayout.NavigationParams params = new INavigationLayout.NavigationParams();
params.fragment = new SomeFragment();
params.removeLast = true;
params.forceWithoutAnimation = false;
presentFragment(params);

// إنهاء الشاشة الحالية
finishFragment();
finishFragment(false); // بدون أنميشن

// إزالة من المكدس دون إنهاء
removeSelfFromStack();
```

---

## الـ Navigation Stack (INavigationLayout)

```java
// messenger/.../ActionBar/INavigationLayout.java
public interface INavigationLayout {
    // الـ Stack APIs
    boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation);
    boolean removeFragmentFromStack(BaseFragment fragment);
    BaseFragment getLastFragment();

    // Preview (فتح فوق كنافذة منبثقة)
    boolean presentFragmentAsPreview(BaseFragment fragment);
    boolean presentFragmentAsPreviewWithMenu(BaseFragment fragment, ActionBarPopupWindow.ActionBarPopupWindowLayout menu);
    void movePreviewFragment(float dy);
    void finishPreviewFragment();

    // Tabs
    void setFragmentPanTranslationOffset(int offset);

    // Lifecycle
    void resumeDelayedFragmentAnimation();
}
```

> **متى تستخدم preview vs presentFragment؟**
> - `presentFragment` = شاشة كاملة، تملأ الـ container، تستبدل الـ back stack
> - `presentFragmentAsPreview` = ينزلق من الأسفل، يغطي الشاشة جزئياً، **يغلق عند لمس خارجها**

---

## معاملات الـ Fragment عبر `arguments`

```java
// الإرسال
Bundle args = new Bundle();
args.putLong("dialog_id", dialogId);
args.putInt("topic_id", topicId);
SomeFragment fragment = new SomeFragment();
fragment.setArguments(args);
presentFragment(fragment);

// الاستلام (داخل createView)
public class SomeFragment extends BaseFragment {
    private long dialogId;
    private int topicId;

    @Override
    public boolean onFragmentCreate() {
        arguments = getArguments();
        dialogId = arguments.getLong("dialog_id");
        topicId = arguments.getInt("topic_id");
        return super.onFragmentCreate();
    }
}
```

---

## الـ LaunchActivity — مكدس الـ Fragments

```java
// ui/LaunchActivity.java
public class LaunchActivity extends BasePermissionsActivity
        implements INavigationLayout.INavigationLayoutDelegate, ... {

    public ActionBarLayout actionBarLayout;            // المكدس الرئيسي
    public ActionBarLayout layersActionBarLayout;      // المكدس الثانوي (مثلاً MediaActivity)
    public DialogsActivity dialogsActivity;            // الـ root
    public ViewGroup contentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = new SizeNotifierFrameLayout(this);
        setContentView(contentView);

        actionBarLayout = new ActionBarLayout(this, ...);
        contentView.addView(actionBarLayout);

        // الـ root fragment = DialogsActivity
        if (savedInstanceState == null) {
            dialogsActivity = new DialogsActivity(null);
            dialogsActivity.setDelegate(this);
            actionBarLayout.addFragmentToStack(dialogsActivity);
        }
    }

    @Override
    public boolean onFragmentCreate() { ... }    // delegated to current fragment
    @Override
    public void presentFragment(BaseFragment fragment) { ... }
    // ...
}
```

---

## في أي Container تعمل؟

| إذا أردت | استخدم |
|------|------|
| شاشة عادية | `presentFragment(new MyFragment())` |
| شاشة فوق شاشة (preview) | `presentFragmentAsPreview(new MyFragment())` |
| Dialog عادي | `AlertsCreator.createSimpleAlert(...).show()` |
| Bottom sheet | `new BottomSheet.Builder(context).setTitle(...).create().show()` |
| Popup Window من ActionBar | `actionBar.createMenu().addItem(id, drawable).setSubItem(...)` |
| Snackbar/Toast | `BulletinFactory.global().createSimpleBulletin(...).show()` |

---

## مثال كامل: Fragment مع ActionBar وقائمة

```java
public class MembersListFragment extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    private RecyclerListView listView;
    private MembersAdapter adapter;
    private ActionBarMenuItem searchItem;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(currentAccount).addObserver(this,
            NotificationCenter.dialogMembersUpdated);
        return true;
    }

    @Override
    public View createView(Context context) {
        // ActionBar
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString(R.string.ChannelMembers));
        actionBar.setAllowOverlayTitle(true);

        ActionBarMenu menu = actionBar.createMenu();
        searchItem = menu.addItem(1, R.drawable.ic_search_white);
        searchItem.setIsSearchField(true);
        searchItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchExpand() {}
            @Override
            public void onSearchCollapse() { adapter.search(null); }
            @Override
            public void onTextChanged(EditText editText) {
                adapter.search(editText.getText().toString());
            }
        });

        // Content
        fragmentView = new SizeNotifierFrameLayout(context);
        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(adapter = new MembersAdapter());
        ((SizeNotifierFrameLayout) fragmentView).addView(listView,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return fragmentView;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(currentAccount).removeObserver(this,
            NotificationCenter.dialogMembersUpdated);
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.dialogMembersUpdated) {
            // update adapter
        }
    }
}
```

---

## أخطاء شائعة

| ❌ غلط | ✅ صح |
|------|------|
| `new Activity()` ثم `startActivity` | `presentFragment(new Fragment())` |
| `Fragment.onCreate()` بدون `super` | استدعِ `super.onCreate()` ثم `super.onResume()` |
| `findViewById` على `R.id.something` | أنشئ الـ View داخل `createView` |
| نسيان `removeObserver` | ضع في `onFragmentDestroy` |
| `getActivity().getResources()` | `getContext()` أو `fragmentView.getContext()` |
| `setContentView` في Fragment | غلط — لا تستخدمه. ضع الـ views في `fragmentView` |
| استخدام `android.app.Fragment` | استخدم `androidx.fragment.app.Fragment` فقط إذا لزم، لكن الأفضل: `BaseFragment` |
| `setHasOptionsMenu(true)` | لا حاجة — `ActionBar` من `ActionBarLayout` |
| Layout خارجي للـ Fragment (`R.layout.foo`) | كل شيء يُنشأ برمجياً في `createView` (نمط المشروع) |

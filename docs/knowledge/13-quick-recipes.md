# 13 - وصفات جاهزة (Quick Recipes)

> نسخ-ولصق جاهز لأكثر المهام شيوعاً.

---

## Recipe 1: إضافة ترجمة (عربية + إنجليزية)

**المطلوب:** مفتاح "حفظ التغييرات" في `R.string.SaveChanges`

### الخطوة 1: `TMessagesProj/src/main/res/values/strings.xml`
```xml
<string name="SaveChanges">Save changes</string>
```

### الخطوة 2: `TMessagesProj/src/main/res/values-ar/strings.xml`
```xml
<string name="SaveChanges">حفظ التغييرات</string>
```

### الخطوة 3: استخدمها
```java
textView.setText(LocaleController.getString(R.string.SaveChanges));
```

### مع متغير
```xml
<!-- en -->
<string name="WelcomeMsg">Welcome, %1$s!</string>
<!-- ar -->
<string name="WelcomeMsg">أهلاً، %1$s!</string>
```
```java
String text = LocaleController.formatString(R.string.WelcomeMsg, userName);
```

### جمع/مفرد
```xml
<!-- en -->
<plurals name="NewMessages">
    <item quantity="one">%d new message</item>
    <item quantity="other">%d new messages</item>
</plurals>
<!-- ar -->
<plurals name="NewMessages">
    <item quantity="zero">لا توجد رسائل جديدة</item>
    <item quantity="one">رسالة جديدة واحدة</item>
    <item quantity="two">رسالتان جديدتان</item>
    <item quantity="few">%d رسائل جديدة</item>
    <item quantity="many">%d رسالة جديدة</item>
    <item quantity="other">%d رسائل جديدة</item>
</plurals>
```
```java
String text = LocaleController.formatPluralString("NewMessages", count);
```

---

## Recipe 2: إنشاء Fragment جديد

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
        // اختياري: اشترك في أحداث
        NotificationCenter.getInstance(currentAccount).addObserver(this,
            NotificationCenter.didReceivedNewMessages);
        return super.onFragmentCreate();
    }

    @Override
    public View createView(Context context) {
        // 1) ActionBar
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString(R.string.MyTitle));
        actionBar.setAllowOverlayTitle(true);

        // 2) Content
        fragmentView = new FrameLayout(context);

        // 3) إرجاع الـ View
        return fragmentView;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(currentAccount).removeObserver(this,
            NotificationCenter.didReceivedNewMessages);
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didReceivedNewMessages) {
            // حدّث الواجهة
        }
    }
}
```

**لاستدعائها من Fragment آخر:**
```java
presentFragment(new MyNewFragment());
```

---

## Recipe 3: قائمة إعدادات بسيطة

```java
public class SettingsFragment extends BaseFragment {

    private RecyclerListView listView;
    private SettingsAdapter adapter;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString(R.string.Settings));

        fragmentView = new FrameLayout(context);
        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(adapter = new SettingsAdapter());

        // مثال على الـ rows
        List<SettingsAdapter.Row> rows = new ArrayList<>();
        rows.add(new SettingsAdapter.Row(SettingsAdapter.ROW_TYPE_HEADER, "القسم 1", null, false));
        rows.add(new SettingsAdapter.Row(SettingsAdapter.ROW_TYPE_CHECK, "تمكين", null, true, true));
        rows.add(new SettingsAdapter.Row(SettingsAdapter.ROW_TYPE_TEXT, "اللغة", "العربية", false, true));
        rows.add(new SettingsAdapter.Row(SettingsAdapter.ROW_TYPE_SHADOW));
        rows.add(new SettingsAdapter.Row(SettingsAdapter.ROW_TYPE_INFO, "نص توضيحي", null, false, false));
        adapter.setData(rows);

        ((FrameLayout) fragmentView).addView(listView,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return fragmentView;
    }

    private static class SettingsAdapter extends RecyclerListView.SelectionAdapter {
        static final int ROW_TYPE_HEADER = 0;
        static final int ROW_TYPE_TEXT = 1;
        static final int ROW_TYPE_CHECK = 2;
        static final int ROW_TYPE_SHADOW = 3;
        static final int ROW_TYPE_INFO = 4;

        private final Context context;
        private final List<Row> rows = new ArrayList<>();

        SettingsAdapter() { this.context = null; }

        @Override public int getItemCount() { return rows.size(); }
        @Override public int getItemViewType(int position) { return rows.get(position).type; }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            switch (viewType) {
                case ROW_TYPE_HEADER: v = new HeaderCell(parent.getContext()); break;
                case ROW_TYPE_CHECK:  v = new TextCheckCell(parent.getContext()); break;
                case ROW_TYPE_INFO:   v = new TextInfoCell(parent.getContext()); break;
                case ROW_TYPE_SHADOW: v = new ShadowSectionCell(parent.getContext()); break;
                default:              v = new TextSettingsCell(parent.getContext());
            }
            return new RecyclerListView.Holder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Row row = rows.get(position);
            switch (row.type) {
                case ROW_TYPE_HEADER:
                    ((HeaderCell) holder.itemView).setText(row.title); break;
                case ROW_TYPE_CHECK:
                    ((TextCheckCell) holder.itemView).setTextAndCheck(row.title, row.checked, false); break;
                case ROW_TYPE_INFO:
                    ((TextInfoCell) holder.itemView).setText(row.title); break;
                case ROW_TYPE_TEXT:
                    ((TextSettingsCell) holder.itemView).setTextAndValue(row.title, row.value, row.needDivider); break;
            }
        }

        void setData(List<Row> data) {
            rows.clear();
            rows.addAll(data);
            notifyDataSetChanged();
        }

        static class Row {
            int type;
            String title, value;
            boolean checked, needDivider;
            Row(int type) { this.type = type; }
            Row(int type, String title, String value, boolean needDivider) {
                this(type); this.title = title; this.value = value; this.needDivider = needDivider;
            }
            Row(int type, String title, String value, boolean checked, boolean needDivider) {
                this(type, title, value, needDivider); this.checked = checked;
            }
        }
    }
}
```

---

## Recipe 4: AlertDialog مع نعم/لا

```java
AlertsCreator.showSimpleConfirmAlert(this,
    LocaleController.getString(R.string.DeleteTitle),
    LocaleController.getString(R.string.DeleteMessage),
    LocaleController.getString(R.string.Delete),
    true,  // isConfirmRed
    () -> { /* perform delete */ }
);
```

### أو يدوياً
```java
new AlertDialog.Builder(getContext())
    .setTitle(LocaleController.getString(R.string.DeleteTitle))
    .setMessage(LocaleController.getString(R.string.DeleteMessage))
    .setPositiveButton(LocaleController.getString(R.string.Delete), (dialog, which) -> {
        deleteItem();
    })
    .setNegativeButton(LocaleController.getString(R.string.Cancel), null)
    .show();
```

---

## Recipe 5: BottomSheet (قائمة خيارات)

```java
new BottomSheet.Builder(getContext())
    .setTitle(LocaleController.getString(R.string.Options))
    .setItems(new CharSequence[] {
        LocaleController.getString(R.string.Share),
        LocaleController.getString(R.string.Edit),
        LocaleController.getString(R.string.Delete)
    }, (dialog, which) -> {
        switch (which) {
            case 0: share(); break;
            case 1: edit(); break;
            case 2: delete(); break;
        }
    })
    .create()
    .show();
```

---

## Recipe 6: Bulletin (Toast ذكي)

```java
// نجاح
BulletinFactory.of(this)
    .createSimpleBulletin(R.drawable.ic_check, LocaleController.getString(R.string.Saved))
    .show();

// خطأ
BulletinFactory.of(this)
    .createSimpleBulletin(BulletinFactory.ICON_TYPE_WARNING, "Something went wrong")
    .show();

// Undo (تراجع)
BulletinFactory.of(this)
    .createUndoBulletin(
        LocaleController.getString(R.string.MessageDeleted),
        LocaleController.getString(R.string.Undo),
        () -> { /* restore */ }
    )
    .show();
```

---

## Recipe 7: إضافة زر بحث في ActionBar

```java
ActionBarMenu menu = actionBar.createMenu();
ActionBarMenuItem search = menu.addItem(1, R.drawable.ic_search_white);
search.setIsSearchField(true);
search.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
    @Override public void onSearchExpand() {}
    @Override public void onSearchCollapse() { adapter.search(null); }
    @Override public void onTextChanged(EditText editText) {
        adapter.search(editText.getText().toString());
    }
});
```

---

## Recipe 8: عرض صورة (Avatar)

```java
BackupImageView avatar = new BackupImageView(context);
avatar.setRoundAsCircle(true);
avatar.setSize(dp(40), dp(40));
avatar.setImage(ImageLocation.getForUser(user, ImageLocation.TYPE_SMALL), null,
    new AvatarDrawable().setInfo(userId, user.first_name + " " + user.last_name, TLRPC.User.class),
    user.first_name);

container.addView(avatar, LayoutHelper.createFrame(dp(40), dp(40), Gravity.CENTER_VERTICAL | LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT, dp(16), 0, dp(8), 0));
```

---

## Recipe 9: قائمة Popup (ActionBar overflow)

```java
ActionBarMenuItem item = menu.addItem(1, R.drawable.ic_more);
item.addSubItem(10, LocaleController.getString(R.string.Share));
item.addSubItem(11, LocaleController.getString(R.string.Edit));
item.addSubItem(12, LocaleController.getString(R.string.Delete));

// أو يدوياً
ActionBarPopupWindow.ActionBarPopupWindowLayout layout = ...;
ActionBarMenuItem.addItem(layout, R.drawable.ic_copy, color, iconColor, "Copy", false, 0);
```

---

## Recipe 10: Switch في قائمة

```java
TextCheckCell cell = (TextCheckCell) holder.itemView;
cell.setTextAndCheck(LocaleController.getString(R.string.Notifications), isEnabled, false);
cell.setOnCheckedChangeListener((v, isChecked) -> {
    // حفظ التغيير
    SharedConfig.setNotificationsEnabled(isChecked);
});
```

---

## Recipe 11: Date/Time Picker

```java
// Date picker
AlertsCreator.createDatePickerDialog(context, 1900, 2100, 0, 1, 0, 2026,
    LocaleController.getString(R.string.ChooseDate), false,
    new AlertsCreator.DatePickerDelegate() {
        @Override
        public void didSelectDate(int day, int month, int year) {
            // ...
        }
    });

// Time picker
AlertsCreator.createTimePickerDialog(context, title, 0, 0, 24 * 60 - 1,
    selectedTime -> { /* ... */ });

// Schedule (with time)
AlertsCreator.createScheduleDatePickerDialog(context, dialogId, datePickerDelegate)
    .create()
    .show();
```

---

## Recipe 12: Circle Progress

```java
RadialProgressView progress = new RadialProgressView(context);
progress.setProgressColor(Theme.getColor(Theme.key_progressCircle));
progress.setProgress(0.5f, true);
```

---

## Recipe 13: Add to Favorites / Star

```java
// Toggle favorite
int starCount = (int) SharedConfig.getFavoritesCount();
if (isFavorited) {
    SharedConfig.removeFavorite(dialogId);
} else {
    SharedConfig.addFavorite(dialogId);
}

// إشعار
BulletinFactory.of(this)
    .createSimpleBulletin(R.drawable.ic_star,
        isFavorited ? LocaleController.getString(R.string.RemovedFromFavorites)
                    : LocaleController.getString(R.string.AddedToFavorites))
    .show();
```

---

## Recipe 14: صورة مع Ripple + Circle

```java
// FrameLayout يحتوي على image و overlay
FrameLayout wrapper = new FrameLayout(context);
wrapper.setBackground(Theme.getSelectorDrawable(false));

BackupImageView image = new BackupImageView(context);
image.setRoundAsCircle(true);
image.setImage(ImageLocation.getForUser(user, ImageLocation.TYPE_SMALL), null, null, null);
wrapper.addView(image, LayoutHelper.createFrame(dp(40), dp(40)));

wrapper.setOnClickListener(v -> { /* ... */ });
```

---

## Recipe 15: Async Task (مع UI thread update)

```java
Utilities.stageQueue.postRunnable(() -> {
    // background work
    final List<Item> result = loadData();

    AndroidUtilities.runOnUIThread(() -> {
        // update UI
        adapter.setData(result);
    });
});
```

---

## Recipe 16: Custom Toast مع Action

```java
BulletinFactory.of(this)
    .createSimpleBulletin(
        R.drawable.ic_check,
        LocaleController.getString(R.string.Saved),
        LocaleController.getString(R.string.Undo),
        () -> { restoreLast() }
    )
    .show();
```

---

## Recipe 17: اشتراك في أحداث Controller

```java
// في onFragmentCreate
@Override
public boolean onFragmentCreate() {
    super.onFragmentCreate();
    NotificationCenter.getInstance(currentAccount).addObserver(this,
        NotificationCenter.didReceivedNewMessages);
    return true;
}

// في onFragmentDestroy
@Override
public void onFragmentDestroy() {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(currentAccount).removeObserver(this,
        NotificationCenter.didReceivedNewMessages);
}

// Handler
@Override
public void didReceivedNotification(int id, int account, Object... args) {
    if (id == NotificationCenter.didReceivedNewMessages) {
        @SuppressWarnings("unchecked")
        ArrayList<MessageObject> messages = (ArrayList<MessageObject>) args[0];
        // update UI
    }
}
```

---

## Recipe 18: قراءة بيانات من Controllers

```java
// User
TLRPC.User user = getMessagesController().getUser(userId);
String name = UserObject.getUserName(user);

// Dialog
ArrayList<TLRPC.Dialog> dialogs = getMessagesController().getAllDialogs();
TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(dialogId);

// Messages
ArrayList<MessageObject> messages = getMessagesController().getMessageList(dialogId, 0);

// Chat
TLRPC.Chat chat = getMessagesController().getChat(chatId);

// Sticker set
TLRPC.TL_messages_stickerSet set = getMediaDataController().getStickerSet(setNum);
```

---

## Recipe 19: تنسيق التاريخ

```java
// اليوم / أمس / الأسبوع / التاريخ الكامل
String formatted;
if (message.isOut() && message.messageOwner.date == (int) (System.currentTimeMillis() / 1000)) {
    formatted = LocaleController.getString(R.string.Today);
} else if (isYesterday(message.messageOwner.date)) {
    formatted = LocaleController.getString(R.string.Yesterday);
} else {
    formatted = LocaleController.stringForMessageListDate(message.messageOwner.date);
}

// الوقت
String time = LocaleController.getInstance().getFormatterDay().format(message.messageOwner.date * 1000L);
```

---

## Recipe 20: إنشاء Adapter لـ Grid

```java
public class MyGridAdapter extends RecyclerListView.SelectionAdapter {
    private final List<Item> items = new ArrayList<>();

    @Override public int getItemCount() { return items.size(); }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout cell = new FrameLayout(parent.getContext());
        cell.setLayoutParams(new RecyclerView.LayoutParams(dp(80), dp(80)));
        ImageView image = new ImageView(parent.getContext());
        cell.addView(image, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return new RecyclerListView.Holder(cell);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item = items.get(position);
        ImageView image = (ImageView) ((FrameLayout) holder.itemView).getChildAt(0);
        // image.setImageResource(R.drawable.foo);
    }
}

// في Fragment
listView.setLayoutManager(new GridLayoutManager(context, 3));
listView.setAdapter(adapter);
```

---

## Recipe 21: Helper: Format Bytes

```java
public static String formatFileSize(long size) {
    if (size < 1024) return size + " B";
    int z = (63 - Long.numberOfLeadingZeros(size)) / 10;
    return String.format("%.1f %sB", (double) size / (1L << (z * 10)), " KMGTPE".charAt(z));
}
```

---

## Recipe 22: Pull-to-Refresh (SwipeRefresh)

```java
// المشروع لا يستخدم SwipeRefreshLayout من Google. بدلاً منه:
// في DialogsActivity، الـ refresh يحدث عند العودة للـ Tab
// أو يمكن استخدام:

ContentLoadingProgressBar loading = new ContentLoadingProgressBar(context);
loading.show();

// عند الانتهاء
loading.hide();
```

---

## Recipe 23: Display Avatar with Online Status

```java
BackupImageView avatar = new BackupImageView(context);
avatar.setRoundAsCircle(true);
avatar.setImage(ImageLocation.getForUser(user, ImageLocation.TYPE_SMALL), null, null, null);

// Online indicator (نقطة خضراء صغيرة)
View onlineDot = new View(context);
Drawable onlineDrawable = Theme.createCircleDrawable(dp(8),
    Theme.getColor(Theme.key_status_online));
onlineDot.setBackground(onlineDrawable);
((FrameLayout) avatar.getParent()).addView(onlineDot,
    LayoutHelper.createFrame(dp(8), dp(8), Gravity.BOTTOM | Gravity.RIGHT));
```

---

## Recipe 24: تنسيق رسالة (text + emoji + links)

```java
// للحصول على Spannable مع emoji rendering
String text = "Hello **world** and [link](https://telegram.org)";
CharSequence spannable = SpannableStringBuilder.valueOf(text);
// ثم مرّره عبر MessageObject.parseLinks(...)
// أو AndroidUtilities.replaceSingleLinkBold
```

---

## Recipe 25: Table (Row × Column)

```java
TableLayout table = new TableLayout(context);
table.setStretchAllColumns(true);

TableRow row = new TableRow(context);
TextView key = new TextView(context);
key.setText("Name");
key.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
row.addView(key);

TextView value = new TextView(context);
value.setText("HudGram");
value.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
value.setGravity(Gravity.RIGHT);
row.addView(value);

table.addView(row);
```

---

## Recipe 26: Loading View

```java
FrameLayout loadingContainer = new FrameLayout(context);
RadialProgressView progress = new RadialProgressView(context);
progress.setProgressColor(Theme.getColor(Theme.key_progressCircle));
loadingContainer.addView(progress, LayoutHelper.createFrame(dp(40), dp(40), Gravity.CENTER));

// show/hide
loadingContainer.setVisibility(View.VISIBLE);
// ... do work ...
loadingContainer.setVisibility(View.GONE);
```

---

## Recipe 27: Sticky Header للقائمة

```java
// StickyHeader (في RecyclerListView)
listView.setStickyHeader(true);
listView.setStickyHeaderOffset(0);

// Cell فوق
HeaderCell stickyHeader = new HeaderCell(context);
stickyHeader.setText(LocaleController.getString(R.string.SectionTitle));
listView.addHeaderView(stickyHeader, 0, dp(40), true);
```

---

## Recipe 28: Sectioned List (للقوائم المجمّعة)

```java
public class MySectionsAdapter extends RecyclerListView.SectionsAdapter {

    @Override public int getSectionCount() { return 3; }
    @Override public int getCountForSection(int section) { return 5; }

    @Override
    public Object getItem(int section, int position) { return items.get(section).get(position); }

    @Override
    public int getItemViewType(int section, int position) {
        return section * 100 + position;  // unique per section
    }

    @Override
    public View getView(int section, int position, View convertView, ViewGroup parent) {
        TextCell cell = convertView instanceof TextCell ? (TextCell) convertView : new TextCell(parent.getContext());
        Item item = (Item) getItem(section, position);
        cell.setText(item.title, false);
        return cell;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        HeaderCell cell = convertView instanceof HeaderCell ? (HeaderCell) convertView : new HeaderCell(parent.getContext());
        cell.setText(sections.get(section).title);
        return cell;
    }
}
```

---

## Recipe 29: Lock Fragment بـ Passcode

```java
if (SharedConfig.passcodeHash.length() > 0) {
    // اعرض شاشة PasscodeActivity
    presentFragment(new PasscodeActivity());
    return fragmentView;
}
```

---

## Recipe 30: Capture Screen

```java
View view = fragmentView;
Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
Canvas canvas = new Canvas(bitmap);
view.draw(canvas);
File file = new File(ApplicationLoader.getFilesDirFixed(), "screenshot.png");
bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
```

---

## الأخطاء الأكثر شيوعاً وحلولها

| المشكلة | الحل |
|------|------|
| "Cannot resolve symbol R" | نظّف المشروع: `Build > Clean` |
| النص لا يظهر | تأكد من استدعاء `LocaleController.getString` |
| الخلفية بيضاء في dark mode | استخدم `Theme.getColor(Theme.key_windowBackgroundWhite)` |
| Layout مكسور في RTL | استخدم `LayoutHelper.getAbsoluteGravityStart` |
| Color أصفر/أخضر (debug) | قد يكون `Theme.getDefaultColor` يرجع 0xffff0000 إذا الـ key غير معرّف |
| Image لا تظهر | تأكد من استخدام `BackupImageView` + `setImage` |
| List بطيئة | استخدم `notifyItemChanged` بدلاً من الكل |
| NPE على User | تحقق من `if (user != null)` أولاً |
| `LOC_ERR:keyName` | المفتاح غير موجود في strings.xml |

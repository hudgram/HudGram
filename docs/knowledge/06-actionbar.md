# 06 - ActionBar (Top Bar)

## ما هو الـ ActionBar؟
هو **شريط العنوان العلوي** في كل Fragment. يحتوي على:
- زر الرجوع (يسار أو يمين حسب RTL)
- العنوان (Title)
- العنوان الفرعي (Subtitle) — اختياري
- قائمة (Menu) — يمين أو يسار

الكلاس الرئيسي: `ui/ActionBar/ActionBar.java` (2,127 سطر)

---

## الإعداد الأساسي

داخل `BaseFragment.createView`:

```java
@Override
public View createView(Context context) {
    // 1) الرجوع
    actionBar.setBackButtonImage(R.drawable.ic_ab_back);

    // 2) العنوان
    actionBar.setTitle(LocaleController.getString(R.string.MyTitle));

    // 3) السماح بتراكب العنوان على المحتوى (للقوائم)
    actionBar.setAllowOverlayTitle(true);

    // 4) إضافة Menu
    ActionBarMenu menu = actionBar.createMenu();
    ActionBarMenuItem search = menu.addItem(search_id, R.drawable.ic_search_white);
    search.setOnClickListener(v -> { /* toggle search */ });
    // ... add more items

    // 5) باقي الـ View
    fragmentView = new FrameLayout(context);
    // ...
    return fragmentView;
}
```

---

## ActionBar — كل العمليات

```java
// العنوان
actionBar.setTitle(CharSequence value);
actionBar.setTitle(CharSequence value, Drawable rightDrawable);
actionBar.setTitleColor(int color);
actionBar.setTitleRightMargin(int dpValue);
actionBar.setTitleScrollNonFitText(boolean);
actionBar.centerTitle();

// العنوان الفرعي
actionBar.setSubtitle(CharSequence value);
actionBar.setSubtitleColor(int color);
actionBar.createAdditionalSubtitleTextView();

// الرجوع
actionBar.setBackButtonImage(int resource);
actionBar.setBackButtonDrawable(Drawable drawable);
actionBar.setBackButtonContentDescription(CharSequence description);
actionBar.getBackButton();  // returns ImageView

// الخلفية
actionBar.setBackgroundColor(int color);
actionBar.setActionModeTopColor(int color);

// Action Mode (شريط التحديد عند تحديد عناصر)
actionBar.setActionModeColor(int color);
actionBar.setActionModeOverrideColor(int color);
ActionBarMenu actionMode = actionBar.createActionMode();
actionBar.showActionMode();
actionBar.showActionMode(boolean animated);
actionBar.hideActionMode();

// الترجمة (animation)
actionBar.setTranslationY(float translationY);
```

### `setAllowOverlayTitle(boolean)` — مهم
- `false`: الـ Title يكون داخل الـ ActionBar بارتفاع ثابت
- `true`: الـ Title يطفو فوق المحتوى (تختفي الـ ActionBar عند scroll)

---

## ActionBarMenu — القائمة

```java
ActionBarMenu menu = actionBar.createMenu();

// 1) عنصر بأيقونة فقط
ActionBarMenuItem item1 = menu.addItem(id1, R.drawable.ic_edit);

// 2) عنصر بأيقونة + لون خلفية
ActionBarMenuItem item2 = menu.addItem(id2, R.drawable.ic_more, Theme.getColor(Theme.key_actionBarDefault));

// 3) عنصر بنص (بدون أيقونة)
ActionBarMenuItem item3 = menu.addItem(id3, LocaleController.getString(R.string.Done));

// 4) عنصر بعرض مخصص
ActionBarMenuItem item4 = menu.addItemWithWidth(id4, R.drawable.ic_settings, dp(56));

// 5) أيقونة مخصصة
Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_custom);
ActionBarMenuItem item5 = menu.addItem(id5, icon);

// عند الضغط
actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
    @Override
    public void onItemClick(int id) {
        if (id == id1) { /* edit */ }
        else if (id == id2) { /* more */ }
    }
});
```

---

## ActionBarMenuItem — العنصر الفردي

```java
ActionBarMenuItem item = menu.addItem(1, R.drawable.ic_search_white);

// إعدادات أساسية
item.setOnClickListener(v -> { /* ... */ });
item.setOverrideMenuClick(boolean);
item.setIconColor(int color);  // تلقائياً = Theme.actionBarDefaultIcon
item.setBackgroundColor(int color);

// أيقونة بديلة (مثل حالة التحديد)
item.setIcon(R.drawable.ic_search_active);

// إضافة submenu (popup)
TextView sub1 = item.addSubItem(10, LocaleController.getString(R.string.Rename));
TextView sub2 = item.addSubItem(11, LocaleController.getString(R.string.Delete));
TextView sub3 = item.addSubItem(12, LocaleController.getString(R.string.Share));
// sub items يتم استقبال الضغط في نفس listener (id = 10/11/12)

// إضافة subitem مع أيقونة
TextView sub4 = item.addSubItemWithIcon(13, R.drawable.ic_copy, LocaleController.getString(R.string.Copy));

// Subitem فاصل
item.addSubItem(separatorId, LocaleController.getString(R.string.SectionHeader), true /* isHeader */);

// استبدال submenu
item.openSubMenu(new int[] {20, 21}, new String[] {"Option 1", "Option 2"});

// حقل البحث
item.setIsSearchField(true);
item.setIsSearchField(true, boolean wrapInScrollView);
item.getSearchField();  // EditText
item.setSearchFieldHint(CharSequence hint);
item.setSearchFieldText(CharSequence text);
item.setSearchCursorColor(int color);
item.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
    @Override public void onSearchExpand() {}
    @Override public void onSearchCollapse() { search(null); }
    @Override public void onTextChanged(EditText editText) {
        search(editText.getText().toString());
    }
});

// التحكم
item.setAllowCloseAnimation(boolean);
item.getTextView();  // TextView داخل العنصر
item.getSearchClearButton();  // زر مسح حقل البحث

// Hiding
item.setVisibility(View.GONE);
item.setEnabled(false);
```

---

## أنماط ActionBar الشائعة

### 1) شاشة بسيطة: back + title
```java
actionBar.setBackButtonImage(R.drawable.ic_ab_back);
actionBar.setTitle(LocaleController.getString(R.string.Settings));
```

### 2) شاشة مع بحث
```java
actionBar.setBackButtonImage(R.drawable.ic_ab_back);
actionBar.setTitle(LocaleController.getString(R.string.MyTitle));

ActionBarMenu menu = actionBar.createMenu();
ActionBarMenuItem search = menu.addItem(1, R.drawable.ic_search_white);
search.setIsSearchField(true);
search.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
    @Override
    public void onSearchExpand() {
        // اخفِ القوائم الأخرى
    }
    @Override
    public void onSearchCollapse() {
        adapter.search(null);
    }
    @Override
    public void onTextChanged(EditText editText) {
        adapter.search(editText.getText().toString());
    }
});
```

### 3) شاشة مع أكثر من زر
```java
ActionBarMenu menu = actionBar.createMenu();
menu.addItem(1, R.drawable.ic_search_white).setOnClickListener(v -> toggleSearch());
menu.addItem(2, R.drawable.ic_more).addSubItem(10, LocaleController.getString(R.string.Rename));
                                                                       .addSubItem(11, LocaleController.getString(R.string.Delete));
```

### 4) شاشات الإعدادات (بدون back)
```java
// DialogsActivity لا تعرض back
actionBar.setTitle(LocaleController.getString(R.string.AppName));
```

### 5) شاشة مع Avatar
```java
// لبروفايل مستخدم مثلاً
actionBar.setTitle(user.fullName);
BackupImageView avatar = new BackupImageView(context);
avatar.setRoundRadius(dp(20));
avatar.setImage(user.photo, null);
actionBar.addView(avatar, LayoutHelper.createLinear(dp(40), dp(40), Gravity.CENTER_VERTICAL | Gravity.LEFT, dp(8), 0, 0, 0));
```

### 6) Action Mode (شريط التحديد)
```java
// بعد تحديد عناصر
ActionBarMenu actionMode = actionBar.createActionMode();
actionMode.addItem(1, R.drawable.ic_share);
actionMode.addItem(2, R.drawable.ic_delete);
actionMode.addItem(3, R.drawable.ic_forward);
actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
    @Override public void onItemClick(int id) {
        if (id == 1) { shareSelected(); }
        else if (id == 2) { deleteSelected(); }
        else if (id == 3) { forwardSelected(); }
    }
});
actionBar.showActionMode();

// عند الانتهاء
actionBar.hideActionMode();
```

---

## ActionBarPopupWindow (Menu خارج ActionBar)

```java
// قائمة منبثقة (popup) يمكن استدعاؤها من أي View
ActionBarPopupWindow.ActionBarPopupWindowLayout layout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context);
layout.setupAnimations();

// إضافة عناصر
int iconColor = Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon);
int textColor = Theme.getColor(Theme.key_actionBarDefaultSubmenuItem);
int backgroundColor = Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground);

ActionBarMenuItem.addItem(layout, R.drawable.ic_copy, textColor, iconColor,
    LocaleController.getString(R.string.Copy), false, 0);
ActionBarMenuItem.addItem(layout, R.drawable.ic_share, textColor, iconColor,
    LocaleController.getString(R.string.Share), false, 0);

// عرض الـ popup
ActionBarPopupWindow popupWindow = new ActionBarPopupWindow(layout,
    LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
popupWindow.setBackgroundDrawable(BackgroundDrawable.create(context));
popupWindow.setOutsideTouchable(true);
popupWindow.setAnimationStyle(R.style.PopupContextAnimation);
popupWindow.showAsDropDown(anchorView, 0, -dp(8));

// عند الضغط
layout.setOnItemClickListener(id -> {
    if (id == copyId) { /* ... */ }
    popupWindow.dismiss();
});
```

---

## ActionBar — كل المفاتيح المهمة للألوان

| Key | متى يُستخدم |
|-----|------|
| `key_actionBarDefault` | خلفية الـ TopBar |
| `key_actionBarDefaultIcon` | لون الأيقونات (menu, back) |
| `key_actionBarDefaultTitle` | لون العنوان |
| `key_actionBarDefaultSubtitle` | لون الـ subtitle |
| `key_actionBarDefaultSelector` | خلفية الضغط |
| `key_actionBarActionModeDefault` | خلفية الـ action mode |
| `key_actionBarActionModeDefaultIcon` | أيقونات action mode |
| `key_actionBarActionModeDefaultTitle` | عنوان action mode |
| `key_actionBarActionModeDefaultSubtitle` | subtitle لـ action mode |
| `key_actionBarActionModeDefaultSelector` | خلفية الضغط action mode |
| `key_actionBarWhiteSelector` | selector أبيض (لـ white action bar) |
| `key_actionBarDefaultSubmenuItem` | نص عنصر submenu |
| `key_actionBarDefaultSubmenuItemIcon` | أيقونة submenu |
| `key_actionBarDefaultSubmenuBackground` | خلفية submenu |

---

## نصائح

1. **دائماً** استخدم `LocaleController.getString` للعناوين.
2. **لا تنسَ** `setAllowOverlayTitle(true)` للقوائم الطويلة (مثل DialogsActivity).
3. **عند تبديل اللغة**، أعد استدعاء `setTitle` مع `getString` — التطبيق لا يفعلها تلقائياً.
4. **الـ Avatar في ActionBar** — أنشئ `BackupImageView` وأضفه كـ view عادي.
5. **اختر icons من مجموعة `ic_ab_*`** (action bar) — `ic_ab_back`، `ic_ab_more`، إلخ.
6. **الحجم القياسي للأيقونات**: `dp(24)`، العرض: `dp(48)`، اللمس: `dp(56)`.
7. **القوائم الكثيرة**: استخدم `addSubItem` (popup) بدلاً من `addItem` متعدد.

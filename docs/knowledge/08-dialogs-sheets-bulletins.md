# 08 - النوافذ والقوائم المنبثقة والإشعارات (Dialogs, BottomSheets, Bulletins)

## ملخص سريع

| المكوّن | الكلاس | متى يُستخدم |
|------|------|------|
| **AlertDialog** | `ui/ActionBar/AlertDialog.java` | نافذة تأكيد تقليدية (نص + buttons) |
| **BottomSheet** | `ui/ActionBar/BottomSheet.java` | قائمة منبثقة من الأسفل (Material) |
| **Bulletin** | `ui/Components/BulletinFactory.java` | Toast ذكي أعلى/أسفل الشاشة |
| **PopupWindow** | `ui/ActionBar/ActionBarPopupWindow.java` | قائمة منبثقة من ActionBar |
| **UndoBulletin** | `BulletinFactory.createUndoBulletin` | إشعار مع زر "تراجع" |
| **SnackBar** | المشروع لا يستخدم | (يستخدم Bulletin بدلاً منه) |

---

## 1) AlertDialog (Dialog عادي)

### بسيط — عبر `AlertsCreator`
```java
import org.telegram.ui.Components.AlertsCreator;

AlertsCreator.createSimpleAlert(context, LocaleController.getString(R.string.Title), LocaleController.getString(R.string.Message))
    .setPositiveButton(LocaleController.getString(R.string.OK), (dialog, which) -> {
        // OK clicked
    })
    .setNegativeButton(LocaleController.getString(R.string.Cancel), null)
    .show();
```

### مع Builder كامل
```java
AlertDialog.Builder builder = new AlertDialog.Builder(context);
builder.setTitle(LocaleController.getString(R.string.Title));
builder.setMessage(LocaleController.getString(R.string.Message));
builder.setPositiveButton(LocaleController.getString(R.string.OK), (dialog, which) -> {});
builder.setNegativeButton(LocaleController.getString(R.string.Cancel), (dialog, which) -> {});
builder.setNeutralButton(LocaleController.getString(R.string.Neutral), null);
builder.setOnCancelListener(dialog -> {});

AlertDialog dialog = builder.create();
dialog.show();
```

### مع View مخصص
```java
AlertDialog.Builder builder = new AlertDialog.Builder(context);
View customView = LayoutInflater.from(context).inflate(R.layout.my_layout, null);
// (أو أنشئه برمجياً)
builder.setView(customView);
builder.setPositiveButton(...);
```

### AlertsCreator — دوال جاهزة مهمة
```java
AlertsCreator.createSimpleAlert(ctx, text)
AlertsCreator.createSimpleAlert(ctx, title, text)
AlertsCreator.createSimpleAlert(ctx, title, text, resourcesProvider)
AlertsCreator.createSimpleAlert(ctx, title, text, positive, positiveAction, resourcesProvider)
AlertsCreator.createNoAccessAlert(ctx, title, message, resourcesProvider)
AlertsCreator.showSimpleConfirmAlert(baseFragment, title, message, confirm, isRed, onConfirm)
AlertsCreator.showUpdateAppAlert(context, text, updateApp)
AlertsCreator.showPayForMessageAlert(currentAccount, dialogId, stars, count, confirmed)
AlertsCreator.showOpenUrlAlert(fragment, url, punycode, ask)
AlertsCreator.showBlockReportSpamAlert(fragment, dialogId, user, chat, encryptedChat, ...)
AlertsCreator.showCustomNotificationsDialog(fragment, did, topicId, ...)
AlertsCreator.createTimezonePickerDialog(context, title, timezone, whenPicked)
AlertsCreator.createTimePickerDialog(context, title, time, min, max, whenPicked)
AlertsCreator.createDatePickerDialog(context, ...)
AlertsCreator.createScheduleDatePickerDialog(...)
AlertsCreator.createSoundFrequencyPickerDialog(...)
AlertsCreator.createBirthdayPickerDialog(...)
AlertsCreator.showLeaveGroupWithFutureOwner(...)
AlertsCreator.showAlertWithCheckbox(...)
AlertsCreator.createSupportAlert(...)
```

### AlertsCreator من داخل Fragment
```java
BulletinFactory.of(this).createSimpleBulletin(...).show();
AlertsCreator.showSimpleAlert(this, title, text, "OK", () -> {}, resourcesProvider);
```

> **في Fragment** استخدم `AlertsCreator.showXxx(fragment, ...)` التي تأخذ `BaseFragment`
> — فهي تربط الـ Dialog بـ Fragment لإدارة الذاكرة.

---

## 2) BottomSheet

### بسيط
```java
BottomSheet sheet = new BottomSheet.Builder(context)
    .setTitle(LocaleController.getString(R.string.Options))
    .setItems(new CharSequence[] {
        LocaleController.getString(R.string.Share),
        LocaleController.getString(R.string.Edit),
        LocaleController.getString(R.string.Delete)
    }, (dialog, which) -> {
        if (which == 0) { /* share */ }
        else if (which == 1) { /* edit */ }
        else if (which == 2) { /* delete */ }
    })
    .create();
sheet.show();
```

### مع View مخصص
```java
BottomSheet sheet = new BottomSheet.Builder(context)
    .setTitle("Custom Title")
    .setCustomView(customView, true)  // true = with padding
    .setApplyBottomPadding(false)
    .create();
sheet.show();
```

### مع Tabs (BottomSheetTabs)
```java
BottomSheet sheet = new BottomSheet.Builder(context)
    .setTitle(...)
    .create();
BottomSheetTabs tabs = sheet.getTabs();
tabs.addTab(tab1Title);
tabs.addTab(tab2Title);
tabs.addTab(tab3Title);
```

### مع Input Field
```java
BottomSheet sheet = new BottomSheet.Builder(context)
    .setTitle(LocaleController.getString(R.string.Rename))
    .setApplyTopPadding(false)
    .setCustomView(editText, false)
    .setPositiveButton(LocaleController.getString(R.string.Save), (dialog, which) -> {
        String newName = editText.getText().toString();
    })
    .create();
sheet.show();
```

### مع Theme Resources Provider (داخل محادثة)
```java
BottomSheet sheet = new BottomSheet.Builder(context, false, resourceProvider)
    .setTitle(...)
    .create();
```

### BottomSheet كأسلوب Material
```java
import org.telegram.ui.ActionBar.BottomSheet;

BottomSheet.BottomSheetCell cell = new BottomSheet.BottomSheetCell(context, 0);
cell.setTextAndIcon(LocaleController.getString(R.string.Rename), R.drawable.ic_edit);
cell.setOnClickListener(v -> {});

LinearLayout container = new LinearLayout(context);
container.setOrientation(LinearLayout.VERTICAL);
container.addView(cell);

BottomSheet sheet = new BottomSheet.Builder(context).setCustomView(container).create();
sheet.show();
```

---

## 3) Bulletin (Toast ذكي)

### في Fragment
```java
import org.telegram.ui.Components.BulletinFactory;

BulletinFactory.of(this)
    .createSimpleBulletin(R.drawable.ic_check, LocaleController.getString(R.string.SavedMessage))
    .show();
```

### Global (من أي مكان)
```java
BulletinFactory.global()
    .createSimpleBulletin(R.drawable.ic_check, "Saved!")
    .show();
```

### على Container محدد
```java
BulletinFactory.of(containerLayout, resourceProvider)
    .createSimpleBulletin(R.drawable.ic_check, "Saved")
    .show();
```

### مع Subtext
```java
BulletinFactory.of(this)
    .createSimpleBulletin(R.drawable.ic_warning, "Title", "Subtext here")
    .show();
```

### مع زر (Undo)
```java
BulletinFactory.of(this)
    .createUndoBulletin(
        LocaleController.getString(R.string.MessageDeleted),
        LocaleController.getString(R.string.Undo),
        () -> { /* undo */ }
    )
    .show();
```

### مع صورة (ملصق/ستيكر)
```java
BulletinFactory.of(this)
    .createSimpleBulletin(document, LocaleController.getString(R.string.StickerAdded))
    .show();
```

### مع Emoji كبير
```java
BulletinFactory.of(this)
    .createSimpleBulletin(emojiDrawable, "👋 Hello!")
    .show();
```

### Error / Warning
```java
BulletinFactory.showError(error);  // TLRPC.TL_error
BulletinFactory.of(this).createSimpleBulletin(BulletinFactory.ICON_TYPE_WARNING, "Warning message").show();
```

### Bulletin مخصص
```java
BulletinFactory.of(this)
    .createSimpleBulletin(iconResId, text, subtext, button, () -> {})
    .show();
```

---

## 4) PopupWindow (للـ ActionBar menu الفرعي)

```java
ActionBarPopupWindow.ActionBarPopupWindowLayout layout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context);

int textColor = Theme.getColor(Theme.key_actionBarDefaultSubmenuItem);
int iconColor = Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon);

ActionBarMenuItem.addItem(layout, R.drawable.ic_copy, textColor, iconColor,
    LocaleController.getString(R.string.Copy), false, 0);
ActionBarMenuItem.addItem(layout, R.drawable.ic_edit, textColor, iconColor,
    LocaleController.getString(R.string.Edit), false, 0);
ActionBarMenuItem.addSeparator(layout);  // فاصل
ActionBarMenuItem.addItem(layout, R.drawable.ic_delete, textColor, iconColor,
    LocaleController.getString(R.string.Delete), false, 0);

ActionBarPopupWindow popup = new ActionBarPopupWindow(layout, WRAP_CONTENT, WRAP_CONTENT);
popup.setBackgroundDrawable(BackgroundDrawable.create(context));
popup.setOutsideTouchable(true);
popup.setAnimationStyle(R.style.PopupContextAnimation);
popup.showAsDropDown(anchorView, 0, -dp(8));
```

### مع sub-item
```java
ActionBarMenuItem.addItem(layout, R.drawable.ic_more, textColor, iconColor,
    LocaleController.getString(R.string.More), false, 0)
    .setOnClickListener(v -> {
        // open nested popup
    });
```

---

## 5) عند الضغط على Cell — قائمة السياق

```java
listView.setOnItemLongClickListener((view, position) -> {
    Item item = adapter.getItem(position);
    showContextMenu(view, position, item);
    return true;
});

private void showContextMenu(View view, int position, Item item) {
    BottomSheet sheet = new BottomSheet.Builder(context)
        .setItems(new CharSequence[] {
            LocaleController.getString(R.string.Share),
            LocaleController.getString(R.string.Edit),
            LocaleController.getString(R.string.Delete)
        }, (dialog, which) -> { ... })
        .create();
    sheet.show();
}
```

---

## 6) Dialog مع Custom Type

```java
// Type 0 = message, 2 = loading, 3 = spinner
new AlertDialog(context, AlertDialog.ALERT_TYPE_SPINNER);
```

---

## 7) معاملات (args) — Dialogs من Fragment

```java
// في createView، إذا كان الـ Dialog ظاهر سابقاً عند onPause
@CallSuper
public void onPause() {
    super.onPause();
    try {
        if (visibleDialog != null && visibleDialog.isShowing() && dismissDialogOnPause(visibleDialog)) {
            visibleDialog.dismiss();
            visibleDialog = null;
        }
    } catch (Exception e) {
        FileLog.e(e);
    }
}

protected boolean dismissDialogOnPause(Dialog dialog) {
    return true;  // أغلق الـ Dialog عند إيقاف الـ Fragment
}
```

---

## 8) Sheet مع Tabs (الإعدادات الجديدة)

```java
BottomSheet sheet = new BottomSheet.Builder(context)
    .setTitle(LocaleController.getString(R.string.Choose))
    .create();

BottomSheetTabs tabs = sheet.getTabs();
tabs.addTab(LocaleController.getString(R.string.Tab1));
tabs.addTab(LocaleController.getString(R.string.Tab2));

tabs.setOnTabSelectedListener(index -> {
    // change content
});
sheet.show();
```

---

## 9) DialogButtons (نمط المشروع)

```java
// الزر الإيجابي (يمين) — عادة OK / Save
.setPositiveButton(LocaleController.getString(R.string.OK), ...)

// الزر السلبي (يسار) — عادة Cancel
.setNegativeButton(LocaleController.getString(R.string.Cancel), ...)

// زر محايد (وسط) — نادر
.setNeutralButton(LocaleController.getString(R.string.Neutral), null)

// ألوان الزر
// theme.windowBackgroundWhiteBlueText (default) → أزرق
// theme.windowBackgroundWhiteRedText      → أحمر (للحذف)
```

### مثال: تأكيد الحذف بزر أحمر
```java
AlertsCreator.showSimpleConfirmAlert(this,
    LocaleController.getString(R.string.DeleteChatTitle),
    LocaleController.getString(R.string.DeleteChatMessage),
    LocaleController.getString(R.string.Delete),  // isConfirmRed = true
    true,
    () -> deleteChat()
);
```

---

## 10) الـ Theme ResourcesProvider

```java
// Default theme
AlertsCreator.createSimpleAlert(ctx, title, msg).show();

// في محادثة (themed)
AlertsCreator.createSimpleAlert(ctx, title, msg, resourceProvider).show();

// في BottomSheet
new BottomSheet.Builder(context, false, resourceProvider).setTitle(...).create().show();
```

---

## 11) عند إغلاق Dialog

```java
AlertDialog dialog = builder.create();
dialog.setOnDismissListener(d -> {
    // cleanup
});
dialog.show();
```

---

## 12) الحوار عن تعطيل مانع الإعلانات/الروابط

```java
AlertsCreator.showOpenUrlAlert(fragment, url, false, true);
// يفتح: Browser الداخلي + تأكيد إذا الرابط خارجي
```

---

## 13) الـ Dialogs في AlertDialog.Builder — جميع الخيارات

```java
new AlertDialog.Builder(context)
    .setTitle(title)                    // CharSequence
    .setMessage(message)                // CharSequence
    .setView(customView)                // View
    .setPositiveButton(text, listener)  // String, OnClickListener
    .setNegativeButton(text, listener)
    .setNeutralButton(text, listener)
    .setOnCancelListener(listener)
    .setOnDismissListener(listener)
    .setOnShowListener(listener)
    .setItems(items, listener)          // CharSequence[], OnClickListener
    .setMultiChoiceItems(items, checked, listener)
    .setSingleChoiceItems(items, checked, listener)
    .setCustomTitle(view)
    .setCancelable(cancelable)
    .create()
    .show();
```

---

## 14) الاختصارات

```java
// 1) رسالة قصيرة
BulletinFactory.global().createSimpleBulletin(R.drawable.ic_check, "Done!").show();

// 2) تأكيد
AlertsCreator.showSimpleConfirmAlert(this, "Delete?", "Sure?", "Delete", true, () -> {});

// 3) قائمة خيارات
new BottomSheet.Builder(ctx).setItems(new CharSequence[]{"A", "B"}, (d, w) -> {}).create().show();

// 4) Dialog مخصص
new AlertDialog.Builder(ctx).setTitle("X").setMessage("Y").setPositiveButton("OK", null).show();

// 5) Undo
BulletinFactory.of(this).createUndoBulletin("Deleted", "Undo", () -> restore()).show();
```

---

## ملخص: متى تستخدم ماذا؟

```
هل هي رسالة قصيرة (1-2 ثانية)؟
  └─→ BulletinFactory

هل هي قائمة خيارات (3-7)؟
  └─→ BottomSheet

هل هي تأكيد (نعم/لا)؟
  └─→ AlertDialog عبر AlertsCreator

هل هي قائمة فرعية من ActionBar؟
  └─→ PopupWindow عبر ActionBarPopupWindow

هل هي form (إدخال)؟
  └─→ BottomSheet مع custom view

هل هي رسالة خطأ من السيرفر؟
  └─→ BulletinFactory.showError(error)
```

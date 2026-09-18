# دليل الوكلاء الذكياء (AI Agent Playbook)

> **اقرأ هذا أولاً قبل أي مهمة UI.**

---

## 0) الهوية (قبل كل شيء)

أنت وكيل يعمل على **HudGram** — وهو fork من **Telegram for Android v5.x**.

قواعدك الثابتة:
1. ✅ **UI / UX فقط** — لا تلمس البروتوكول
2. ✅ **استخدم Theme + LocaleController** — دائماً
3. ✅ **احترم RTL + Dark Mode** — دائماً
4. ✅ **العمل من Controllers الموجودة** — لا تنشئ connections جديدة
5. ❌ **ممنوع** تعديل `tgnet/`, `TLRPC.java`, `jni/`
6. ❌ **ممنوع** استخدام ألوان/نصوص ثابتة

---

## 1) قبل أي مهمة — استكشف

```bash
# 1) اعرف السياق
ls TMessagesProj/src/main/java/org/telegram/ui/
ls TMessagesProj/src/main/java/org/telegram/ui/Cells/ | grep -i "الكلمة"
ls TMessagesProj/src/main/java/org/telegram/ui/Components/ | grep -i "الكلمة"

# 2) اعرف الـ theme colors المتاحة
grep "key_XX" TMessagesProj/src/main/java/org/telegram/ui/ActionBar/Theme.java

# 3) اعرف الـ strings المتاحة
grep "XX" TMessagesProj/src/main/res/values/strings.xml

# 4) اعرف نمط الـ Fragment/Activity المشابه
# ابحث عن Fragment موجود مشابه لما تريد بناءه
```

---

## 2) قبل البدء — اقرأ الوثائق

حسب المهمة، اقرأ:

| المهمة | اقرأ |
|------|------|
| إضافة Fragment | [03-navigation.md](./03-navigation.md), [02-screen-catalog.md](./02-screen-catalog.md) |
| إضافة Cell | [07-recyclerlistview-and-cells.md](./07-recyclerlistview-and-cells.md) |
| ترجمة | [05-translations.md](./05-translations.md) |
| ألوان / ثيم | [04-theme-colors.md](./04-theme-colors.md) |
| ActionBar | [06-actionbar.md](./06-actionbar.md) |
| Dialog / Sheet | [08-dialogs-sheets-bulletins.md](./08-dialogs-sheets-bulletins.md) |
| مكون مخصص | [09-custom-components.md](./09-custom-components.md) |
| Layout | [10-layout-and-dimensions.md](./10-layout-and-dimensions.md) |
| قواعد الستايل | [11-style-guide.md](./11-style-guide.md) |
| بدون لمس البروتوكول | [12-extending-without-protocol.md](./12-extending-without-protocol.md) |
| وصفة جاهزة | [13-quick-recipes.md](./13-quick-recipes.md) |

---

## 3) قائمة التحقق — قبل كتابة أي كود

- [ ] هل قرأت الوثائق المناسبة؟
- [ ] هل وجدت `R.string.X` الموجود أم أحتاج لإضافة مفتاح؟
- [ ] هل وجدت `Theme.key_X` الموجود أم أحتاج لإضافة لون؟
- [ ] هل وجدت `Cell` أو `Component` جاهز يناسب؟
- [ ] هل الـ `Fragment` يرث `BaseFragment`؟
- [ ] هل سأستخدم `presentFragment` أم Dialog أم Sheet؟
- [ ] هل الواجهة ستدعم RTL؟
- [ ] هل الواجهة ستدعم Dark Mode؟

---

## 4) قائمة التحقق — بعد كتابة الكود

- [ ] كل الألوان من `Theme.getColor`؟
- [ ] كل النصوص من `LocaleController`؟
- [ ] كل الأبعاد من `dp()` / `sp()`؟
- [ ] تم استخدام `LayoutHelper`؟
- [ ] `super.onFragmentDestroy()` تم استدعاؤها قبل `removeObserver`؟
- [ ] الـ `Fragment` يفكك الموارد في `onFragmentDestroy`؟
- [ ] لـ `ChatActivity` فقط: استخدام `resourceProvider`؟
- [ ] لا يوجد `setText("...")` ثابت؟
- [ ] لا يوجد `Color.WHITE` / `Color.BLACK` / `#FF...`؟
- [ ] لا يوجد `Log.d` (استخدم `FileLog`)؟
- [ ] لا يوجد `Toast` (استخدم `BulletinFactory`)؟
- [ ] لا يوجد `new Activity()` (استخدم `presentFragment`)؟
- [ ] لا يوجد لمس لـ `tgnet/`, `TLRPC.java`, `jni/`؟

---

## 5) أنماط البحث (Grep) الشائعة

```bash
# 1) ابحث عن theme key
grep "key_chat_inBubble" TMessagesProj/src/main/java/org/telegram/ui/ActionBar/Theme.java

# 2) ابحث عن string
grep "WelcomeMsg" TMessagesProj/src/main/res/values/strings.xml
grep "WelcomeMsg" TMessagesProj/src/main/res/values-ar/strings.xml

# 3) ابحث عن cell
ls TMessagesProj/src/main/java/org/telegram/ui/Cells/ | grep "Text"

# 4) ابحث عن component
ls TMessagesProj/src/main/java/org/telegram/ui/Components/ | grep "Dialog"

# 5) ابحث عن event في NotificationCenter
grep "didReceivedNewMessages" TMessagesProj/src/main/java/org/telegram/messenger/NotificationCenter.java

# 6) اعرف أي colors تُستخدم في dialog
grep "Theme.getColor" TMessagesProj/src/main/java/org/telegram/ui/ActionBar/AlertDialog.java
```

---

## 6) متى تسأل المستخدم

اسأل المستخدم (استخدم `question` tool) إذا:

1. **غموض في المتطلبات**: "هل تريد شاشة جديدة أم dialog؟"
2. **تعارض في التصميم**: "هل تريد Dark Mode أم ثيم ثابت؟"
3. **اختيار لون**: "هل تستخدم Theme.X أم Theme.Y؟" — لكن أعرض أمثلة
4. **أيقونة**: "ما اسم الأيقونة من مجموعة المشروع؟" — لكن اقترح
5. **سلوك**: "عند الضغط على X، هل تنتقل لشاشة Y أم تعرض dialog؟"

**لا تسأل** إذا:
- الإجابة واضحة من النمط
- يوجد طريقة قياسية في المشروع

---

## 7) متى لا تسأل وفقط افعل

- إذا كان النمط واضح (مثل: "أضف ترجمة لـ X" → فقط أضف في strings.xml)
- إذا كان الجواب "استخدم Theme.getColor(...)" — فعل ذلك
- إذا كان الجواب "أضف Cell جديدة" — استخدم BaseCell
- إذا كان الجواب "أضف Fragment" — استخدم BaseFragment

---

## 8) أمثلة على مهام وحلولها

### مهمة: "أضف زر 'حفظ' في شاشة الإعدادات"
```java
// 1) في strings.xml (en + ar)
<string name="Save">Save</string>
<string name="Save">حفظ</string>

// 2) في Fragment
actionBar.createMenu().addItem(1, R.drawable.ic_save).setOnClickListener(v -> {
    save();
});
```

### مهمة: "غيّر لون الـ TopBar إلى أزرق"
```java
// ❌ غلط
actionBar.setBackgroundColor(0xFF0088CC);

// ✅ صح
actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
// أو إذا كنت تبني ثيماً:
// راجع Theme.java و defaultColors
```

### مهمة: "أضف خانة بحث في قائمة جهات الاتصال"
```java
// 1) في createView
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

// 2) في Adapter
public void search(String query) {
    // filter list
    notifyDataSetChanged();
}
```

### مهمة: "أضف إشعار توست عند الحفظ"
```java
// ❌ غلط
Toast.makeText(ctx, "تم الحفظ", Toast.LENGTH_SHORT).show();

// ✅ صح
BulletinFactory.of(this)
    .createSimpleBulletin(R.drawable.ic_check, LocaleController.getString(R.string.Saved))
    .show();
```

### مهمة: "أضف dialog للتأكيد قبل الحذف"
```java
AlertsCreator.showSimpleConfirmAlert(this,
    LocaleController.getString(R.string.DeleteTitle),
    LocaleController.getString(R.string.DeleteMessage),
    LocaleController.getString(R.string.Delete),
    true,  // red
    () -> { deleteItem(); }
);
```

### مهمة: "اجعل الواجهة تدعم Dark Mode"
```java
// ❌ غلط
view.setBackgroundColor(Color.WHITE);
textView.setTextColor(Color.BLACK);

// ✅ صح
view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
// Theme يتبدل تلقائياً بين dark/light
```

---

## 9) استكشاف الأخطاء (Troubleshooting)

| الخطأ | السبب المحتمل | الحل |
|------|------|------|
| `Cannot resolve symbol R` | لم يحدث `Clean & Build` | نفّذ `./gradlew clean` |
| `LOC_ERR:key` | المفتاح غير موجود في strings.xml | أضف المفتاح |
| `Theme.getDefaultColor` يرجع أحمر | المفتاح غير معرّف في `defaultColors` | أضف اللون |
| `NullPointerException` على `user` | `user` null | تحقق بـ `if (user != null)` |
| النص لا يترجم | استخدام `"..."` بدلاً من `LocaleController.getString` | استبدل |
| Color أحمر ساطع | لم تختر `Theme.key` صحيح | ابحث في `Theme.java` |
| Layout مكسور في RTL | استخدام `Gravity.LEFT` ثابت | استخدم `LayoutHelper.getAbsoluteGravityStart` |
| `IndexOutOfBoundsException` في RecyclerView | تعديل القائمة خارج الـ Adapter | استخدم `notifyItemInserted/Removed` |
| `IllegalStateException: Fragment not attached` | استدعاء شيء بعد `onDestroy` | تحقق من `isAdded` |
| `OutOfMemory` في الصور | تحميل صور كبيرة بدون `setShouldGenerateQualityThumb` | قلل الحجم |

---

## 10) اختصارات سريعة (Cheat Sheet)

```java
// === الأساسيات ===
LocaleController.getString(R.string.X)
Theme.getColor(Theme.key_X)
AndroidUtilities.dp(16)
LayoutHelper.createFrame(w, h, gravity, left, top, right, bottom)
presentFragment(new MyFragment())
NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.eventX)

// === UI ===
BackupImageView.setImage(location, imageFilter, placeholderDrawable, name)
AvatarDrawable.setInfo(id, name, TLRPC.User.class)
BulletinFactory.of(this).createSimpleBulletin(icon, text).show()
AlertsCreator.createSimpleAlert(ctx, title, text).show()
new BottomSheet.Builder(ctx).setItems(items, listener).create().show()
AlertsCreator.createDatePickerDialog(ctx, 1900, 2100, 0, 1, 0, 2026, title, false, delegate)
AlertsCreator.createScheduleDatePickerDialog(ctx, dialogId, delegate).create().show()
```

---

## 11) الفلسفة العامة (TLDR)

1. **UI فقط** — البروتوكول = `tgnet/` = ممنوع
2. **اعتمد على** — `Controllers` الموجودة + `BaseFragment` + `Theme` + `LocaleController`
3. **ادمج** — `AlertsCreator` + `BottomSheet` + `BulletinFactory` + Cells الجاهزة
4. **احترم** — RTL + Dark Mode + Multi-Account
5. **اختبر** — في Light و Dark، في LTR و RTL، على أحجام شاشات مختلفة
6. **وثّق** — أي مفتاح جديد تضيفه، أي لون، أي دالة مساعدة

> **مهم:** لا تخترع. إذا كان يوجد pattern في المشروع، اتبعه.
> ابحث عن Fragment مشابه لما تبني، وانسخ النمط.

# 11 - قواعد الستايل (Style Guide)

> هذه قواعد **إلزامية** لأي تعديل UI. اتبعها دائماً.

---

## 1. الألوان (Colors)

### ✅ افعل
- `Theme.getColor(Theme.key_X)` أو `Theme.getColor(Theme.key_X, resourceProvider)`
- `Theme.getColor(Theme.key_X, isDefault)` إذا كنت تريد معرفة هل هو معدّل

### ❌ لا تفعل
- `0xff000000` (لون ثابت)
- `Color.WHITE` / `Color.BLACK` / `Color.RED`
- `R.color.X` (لا تستخدم ألوان من `colors.xml` للـ UI)
- `#FFFFFF` في XML

### استثناء
- في `values-night/colors.xml` و `values/colors.xml` لتعريف ألوان الـ OS (شريط الحالة، إلخ)
- في `attrs.xml` للـ XML attrs

---

## 2. النصوص (Strings)

### ✅ افعل
```java
LocaleController.getString(R.string.MyKey)
LocaleController.formatString(R.string.WelcomeMsg, name)
LocaleController.formatPluralString("NewMessages", count)
```

### ❌ لا تفعل
```java
"مرحبا"  // نص ثابت
getString(R.string.Hello) + " " + name  // تقطيع
```

### إضافة مفتاح
1. أضف في `values/strings.xml` (en)
2. أضف في `values-ar/strings.xml` (ar)
3. (اختياري) أضف في لغات أخرى

---

## 3. الأبعاد (Dimensions)

### ✅ افعل
```java
view.setPadding(dp(16), dp(8), dp(16), dp(8));
view.setLayoutParams(LayoutHelper.createFrame(dp(40), dp(40)));
```

### ❌ لا تفعل
```java
view.setPadding(48, 24, 48, 24);  // pixels!
view.getLayoutParams().width = 100;  // raw px
```

### استثناء
- داخل `AndroidUtilities` و `Theme` فقط — حيث القيم بـ px مقصودة

---

## 4. RTL (Right-to-Left)

### ✅ افعل
```java
Gravity.TOP | LayoutHelper.getAbsoluteGravityStart()
view.setPaddingRelative(dp(16), 0, dp(16), 0);
if (LocaleController.isRTL) { /* RTL */ }
```

### ❌ لا تفعل
```java
Gravity.LEFT   // LTR only
view.setPadding(..., dp(16), ..., 0);  // left=0 → not RTL safe
```

### تحقق
- شغّل التطبيق بـ Arabic language
- تأكد أن كل شيء ينقلب بشكل صحيح

---

## 5. Dark Mode

### ✅ افعل
- كل color تأخذه من `Theme.getColor(...)` — يختار dark/light تلقائياً
- اختبر بـ Night Mode في النظام

### ❌ لا تفعل
- استخدام `Theme.getColor(Theme.key_windowBackgroundWhite)` ثم التحقق يدوياً
- فرض `Color.WHITE` للنص

---

## 6. Theme Switch

### ✅ افعل
- اشترك في `NotificationCenter.themeChanged`
- في `didReceivedNotification`، أعد رسم الواجهة

### ❌ لا تفعل
- حفظ ألوان كـ fields
- افتراض أن الثيم لا يتغير

---

## 7. Layout

### ✅ افعل
- استخدام `LayoutHelper.createFrame/Linear/Relative`
- تحديد margins/paddings بالـ `dp()`
- استخدام `MATCH_PARENT` و `WRAP_CONTENT` (من `LayoutHelper`)

### ❌ لا تفعل
- `new FrameLayout.LayoutParams(w, h)` مباشرة
- `setMargins(left, top, right, bottom)` مع px
- `setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))` بدلاً من `LayoutHelper.createLinear(...)`

---

## 8. الموارد (Resources)

### ✅ افعل
- `R.string.MyKey` (مع `LocaleController.getString`)
- `R.drawable.ic_X` (للأيقونات)
- `R.dimen.X` (نادر، معظمها يكون `dp()`)

### ❌ لا تفعل
- `getResources().getString(R.string.X)` (استخدم `LocaleController`)
- `R.string.X` بدون locale controller

---

## 9. Image Loading

### ✅ افعل
- `BackupImageView.setImage(...)` للأيقونات والصور
- `AvatarDrawable` للأفاتار بدون صورة

### ❌ لا تفعل
- `Picasso` أو `Glide` (لا يستخدم في المشروع)
- `BitmapFactory.decodeFile()` مباشرة
- `imageView.setImageBitmap(...)` لتيليمتري

---

## 10. الـ Fragments

### ✅ افعل
- ترث `BaseFragment`
- تنشئ الـ View في `createView`
- تستخدم `presentFragment` للتنقل
- `super.onFragmentDestroy()` ثم `removeObserver`

### ❌ لا تفعل
- تمديد `Activity` (إلا إذا كان entry point)
- `setContentView(...)` (خاص بـ Activity)
- نسيان `super.onCreate/onResume/onPause`

---

## 11. الـ Dialogs

### ✅ افعل
- `AlertsCreator.createSimpleAlert(...)` للنوافذ البسيطة
- `BottomSheet` للقوائم
- `BulletinFactory` للـ Toast

### ❌ لا تفعل
- `new AlertDialog.Builder(ctx)` بدون helper
- `Toast.makeText(...)` (استخدم `BulletinFactory`)
- `Snackbar` (لا يستخدم في المشروع)

---

## 12. Click & Long Click

### ✅ افعل
- `view.setOnClickListener(...)`
- `listView.setOnItemClickListener(...)`
- `view.setOnLongClickListener(... return true)`

### ❌ لا تفعل
- `view.setOnTouchListener(...)` للنقر
- نسيان `view.setClickable(true)` قبل `setOnClickListener` (لـ custom views)

---

## 13. Logging

### ✅ افعل
```java
FileLog.d("debug message");
FileLog.e(exception);
FileLog.e("Error: " + message);
```

### ❌ لا تفعل
```java
Log.d("TAG", "...");  // لا يستخدم في الإنتاج
System.out.println(...);
```

---

## 14. Threading

### ✅ افعل
```java
AndroidUtilities.runOnUIThread(() -> { /* update UI */ });
// أو
getParentActivity().runOnUiThread(...);
```

### ❌ لا تفعل
- تحديث UI من background thread
- `new Thread(() -> { /* update view */ }).start();`

---

## 15. RecyclerView / ListView Updates

### ✅ افعل
```java
adapter.notifyItemChanged(position);
adapter.notifyItemRangeInserted(start, count);
```

### ❌ لا تفعل
- `notifyDataSetChanged()` لكل تغيير صغير
- تعديل `items` خارج الـ Adapter

---

## 16. Exception Handling

### ✅ افعل
```java
try {
    // risky code
} catch (Exception e) {
    FileLog.e(e);
}
```

### ❌ لا تفعل
```java
try { ... } catch (Exception e) { }  // swallowed
throw new RuntimeException("...");  // بدون catch
```

---

## 17. Hardcoded Strings — قائمة المراجعة

قبل أي commit، تأكد:
- [ ] لا توجد `setText("...")` في الكود
- [ ] لا توجد `"..."` في `Toast.makeText` / `Snackbar` / Bulletin
- [ ] لا توجد أسماء مستخدمين/أرقام hardcoded في القوالب
- [ ] كل `formatString` و `formatPluralString` تستخدم المفتاح الصحيح

---

## 18. الهيكلة — قائمة المراجعة

- [ ] Fragment يرث `BaseFragment`
- [ ] `createView` ينشئ View جديد (لا يستخدم layout XML إلا للضرورة)
- [ ] `onFragmentDestroy` يستدعي `super` ثم `removeObserver`
- [ ] `presentFragment` للتنقل (لا `startActivity`)
- [ ] `AlertsCreator` / `BottomSheet` / `BulletinFactory` للنوافذ
- [ ] `LayoutHelper` للـ LayoutParams
- [ ] `dp()` / `sp()` للأبعاد

---

## 19. الأداء — قائمة المراجعة

- [ ] لا `notifyDataSetChanged()` لكل تغيير
- [ ] استخدام `BackupImageView` مع thumbnail
- [ ] لا Bitmap جديد في `onDraw`
- [ ] لا allocations في `onDraw` أو `onMeasure`
- [ ] استخدام `view.setTag(R.id.x, value)` بدلاً من `findViewById` المتكرر

---

## 20. الأمان (للتطبيق)

- [ ] لا تكتب في `getExternalFilesDir` (آمن، لكن استخدم `getFilesDir` أولاً)
- [ ] لا تطبع بيانات المستخدم في log
- [ ] لا تحفظ tokens في `SharedPreferences` (استخدم EncryptedSharedPreferences إذا لزم)
- [ ] تحقق من أذونات قبل الاستخدام

---

## ملخص القواعد الذهبية

1. 🎨 **لون من الثيم** — دائماً
2. 🌐 **نص من LocaleController** — دائماً
3. 📏 **dp/sp من AndroidUtilities** — دائماً
4. 🧭 **Fragment + BaseFragment** — دائماً
5. 🔔 **NotificationCenter** — للـ events
6. 💾 **SharedConfig** — للإعدادات
7. 🪟 **AlertsCreator / BottomSheet / Bulletin** — للنوافذ
8. 🛑 **لا MTProto / لا tgnet** — ممنوع

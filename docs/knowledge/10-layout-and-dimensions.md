# 10 - التخطيط والأبعاد (Layout & Dimensions)

## الأبعاد — AndroidUtilities

```java
// 1) dp → px (مستدير)
int px = AndroidUtilities.dp(16);  // 16dp to pixels

// 2) dpr (دقيق)
int px = AndroidUtilities.dpr(15.5f);

// 3) dp2 → px (مقصوص)
int px = AndroidUtilities.dp2(15.5f);

// 4) dpf2 (float → float)
float px = AndroidUtilities.dpf2(1.5f);

// 5) sp → px (نص)
int px = AndroidUtilities.sp(14);

// 6) densities
AndroidUtilities.density  // float
AndroidUtilities.displaySize  // Point

// 7) المقياس (multiplicator)
float scale = AndroidUtilities.density;
```

### متى تستخدم كل واحدة؟

| الدالة | متى | مثال |
|------|------|------|
| `dp(value)` | **دائماً** للأبعاد | عرض، ارتفاع، margins، paddings |
| `dpr(value)` | للحسابات الدقيقة (الخطوط) | عرض النص المتوقع |
| `dp2(value)` | للأماكن التي لا تريد فيها ceil (مثل الموضع) | x، y |
| `dpf2(value)` | للحسابات التي تحتاج float (مثل alpha) | shadow radius |
| `sp(value)` | لحجم الخط | textSize |

> **القاعدة:** 99% من الوقت تستخدم `dp(value)`.

---

## شائعة الأبعاد

```java
// 1dp, 4dp, 8dp, 12dp, 16dp, 20dp, 24dp, 32dp, 40dp, 48dp, 56dp, 64dp

// ActionBar / TopBar
dp(56)   // ارتفاع الـ ActionBar
dp(48)   // عرض زر في ActionBar
dp(40)   // أيقونة كبيرة
dp(24)   // أيقونة عادية

// Paddings
dp(16)   // padding شائع
dp(8)     // بين عناصر
dp(12)   // بين عناصر
dp(20)   // padding كبير

// Margins
dp(20)   // margin لليسار/اليمين
dp(16)   // margin داخل cell

// Avatar
dp(40)   // avatar صغير (في قائمة)
dp(64)   // avatar في بروفايل
dp(100)  // avatar كبير

// Botones
dp(48)   // ارتفاع زر شائع
dp(40)   // زر صغير
dp(36)   // زر في شريط

// أيقونات
dp(24)   // حجم أيقونة عادي
dp(20)   // أيقونة في قائمة
dp(16)   // أيقونة داخل cell

// نصوص
sp(14)   // نص ثانوي
sp(15)   // نص عادي
sp(16)   // نص عادي (للعناوين)
sp(18)   // عنوان فرعي
sp(20)   // عنوان قسم
sp(24)   // عنوان كبير
sp(28)   // عنوان شاشة
```

---

## الجاذبية (Gravity)

```java
// Constants
Gravity.TOP | Gravity.LEFT
Gravity.TOP | Gravity.RIGHT
Gravity.TOP | Gravity.CENTER_HORIZONTAL
Gravity.CENTER
Gravity.CENTER_VERTICAL | Gravity.LEFT
Gravity.CENTER_VERTICAL | Gravity.RIGHT
Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL

// RTL safe
Gravity.TOP | (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT)
// أو
Gravity.TOP | LayoutHelper.getAbsoluteGravityStart()
```

### LayoutHelper.getAbsoluteGravityStart
```java
// LTR (مثل en, ru) → Gravity.LEFT
// RTL (مثل ar, he, fa) → Gravity.RIGHT
LayoutHelper.getAbsoluteGravityStart() == Gravity.LEFT
LayoutHelper.getAbsoluteGravityEnd() == Gravity.RIGHT
```

---

## الهوامش (Margins & Padding)

```java
// In LayoutParams
LayoutHelper.createFrame(width, height, gravity, left, top, right, bottom);
// أو
LayoutHelper.createFrame(width, height, gravity, dp(16), dp(8), dp(16), dp(8));

// في الـ view نفسه
view.setPadding(dp(16), dp(8), dp(16), dp(8));
view.setPaddingRelative(dp(16), dp(8), dp(16), dp(8));  // RTL aware
```

---

## LayoutParams — ملخص سريع

```java
// FrameLayout
addView(view, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, dp(48)));  // ملء
addView(view, LayoutHelper.createFrame(dp(40), dp(40), Gravity.TOP | Gravity.LEFT, dp(16), dp(8), 0, 0));

// LinearLayout (الـ 0 = match parent في height)
addView(view, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, Gravity.CENTER_VERTICAL, dp(16), 0, dp(16), 0));
addView(view, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1f));  // weight=1

// RelativeLayout
addView(view, LayoutHelper.createRelative(dp(40), dp(40), 0, 0, 0, 0, RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.ALIGN_PARENT_LEFT, 0));

// ScrollView
addView(view, LayoutHelper.createScroll(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP));
```

---

## الأحجام الديناميكية

```java
// حجم الشاشة
int screenWidth = AndroidUtilities.displaySize.x;
int screenHeight = AndroidUtilities.displaySize.y;

// أقل بُعد (مفيد للـ tablets)
int minSide = AndroidUtilities.getMinTabletSide();
boolean isTablet = AndroidUtilities.isTablet();

// شاشة مخصصة (status bar, nav bar)
int statusBarHeight = AndroidUtilities.statusBarHeight;
int currentNavigationBarHeight = AndroidUtilities.getCurrentNavigationBarHeight();
```

---

## الـ Typefaces (الخطوط)

```java
// 1) Bold / Medium
Typeface bold = AndroidUtilities.bold();
textView.setTypeface(bold);

// 2) خط مخصص
Typeface customFont = AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM);
textView.setTypeface(customFont);

// المتوفرة
AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM             // "fonts/rmedium.ttf"
AndroidUtilities.TYPEFACE_ROBOTO_EXTRA_BOLD         // "fonts/rextrabold.ttf"
AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM_ITALIC      // "fonts/rmediumitalic.ttf"
AndroidUtilities.TYPEFACE_ROBOTO_MONO               // "fonts/rmono.ttf"
AndroidUtilities.TYPEFACE_MERRIWEATHER_BOLD          // "fonts/mw_bold.ttf"
AndroidUtilities.TYPEFACE_COURIER_NEW_BOLD          // "fonts/courier_new_bold.ttf"
```

---

## الأقواس والأيقونات (Vector Drawable)

```java
// 1) Drawable من الموارد
Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_settings);

// 2) ColorDrawable
Drawable color = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundWhite));

// 3) Drawable مع ColorFilter
Drawable tinted = ContextCompat.getDrawable(context, R.drawable.ic_settings);
tinted.setColorFilter(new PorterDuffColorFilter(
    Theme.getColor(Theme.key_actionBarDefaultIcon),
    PorterDuff.Mode.SRC_IN));

// 4) StateListDrawable (selector)
StateListDrawable states = new StateListDrawable();
states.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
states.addState(new int[]{}, normalDrawable);
```

---

## الرسوميات (Drawing)

```java
// 1) Drawable
canvas.drawRoundRect(rectF, dp(8), dp(8), paint);

// 2) Path
Path path = new Path();
path.moveTo(0, 0);
path.lineTo(dp(100), dp(100));
canvas.drawPath(path, paint);

// 3) Gradient
LinearGradient gradient = new LinearGradient(
    0, 0, 0, dp(100),
    Theme.getColor(Theme.key_chat_outBubbleGradient1),
    Theme.getColor(Theme.key_chat_outBubbleGradient2),
    Shader.TileMode.CLAMP);
paint.setShader(gradient);

// 4) Canvas.save/restore
canvas.save();
canvas.translate(0, dp(8));
canvas.restore();
```

---

## النسب (Aspect Ratios)

```java
// فيديو 16:9
int width = screenWidth - dp(32);
int height = width * 9 / 16;

// مربع
int size = (screenWidth - dp(48)) / 3;  // grid of 3
```

---

## التحويلات

```java
// الوقت
int minutes = seconds / 60;
int hours = minutes / 60;
int days = hours / 24;

// النص
String.format("%02d:%02d", hours, minutes);

// المسافة
String formatted = AndroidUtilities.formatDistance(meters);  // 1.2 km

// المدة
String formatted = AndroidUtilities.formatDuration(seconds);  // 1:23:45
```

---

## الإعدادات (Settings)

```java
// SharedPreferences
SharedPreferences prefs = ApplicationLoader.applicationContext.getSharedPreferences("main", Context.MODE_PRIVATE);
prefs.edit().putString("key", "value").apply();
String value = prefs.getString("key", "default");

// SharedConfig (إعدادات سريعة)
SharedConfig.useSystemBoldFont = true;
SharedConfig.fontSize = 14;  // sp

// UserConfig (معلومات الحساب)
int currentAccount = UserConfig.selectedAccount;
long myUserId = UserConfig.getInstance(currentAccount).clientUserId;
```

---

## الـ Logging

```java
FileLog.d("debug message");
FileLog.e(exception);
FileLog.e("Error: " + message);
```

---

## الـ Animation (القيم الشائعة)

```java
// مدد
ViewConfiguration.getLongPressTimeout()  // 500ms
ViewConfiguration.getTapTimeout()         // 100ms

// durations
150L   // press feedback
250L   // open dialog
300L   // color animation
400L   // fragment transition
500L   // sheet open
700L   // spring

// interpolators
CubicBezierInterpolator.EASE_OUT      // open
CubicBezierInterpolator.EASE_IN        // close
CubicBezierInterpolator.EASE_IN_OUT   // both
new DecelerateInterpolator(2f)          // manual
new AccelerateDecelerateInterpolator()  // default
new OvershootInterpolator(1.5f)         // pop
```

---

## العلامات والـ IDs (R.id)

```java
R.id.fit_width_tag
R.id.parent_tag
R.id.object_tag
R.id.avatar_tag
```

---

## اعتبارات RTL

```java
// 1) دائماً استخدم LayoutHelper
addView(view, LayoutHelper.createFrame(WRAP, WRAP, Gravity.TOP | LayoutHelper.getAbsoluteGravityStart(), dp(16), 0, dp(16), 0));

// 2) Padding نسبي (start/end) بدلاً من left/right
view.setPaddingRelative(dp(16), dp(8), dp(16), dp(8));

// 3) Text alignment
textView.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);

// 4) Gravity للـ text
textView.setGravity(Gravity.CENTER_VERTICAL | (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT));
```

---

## Touch / Input

```java
// Press feedback
view.setBackground(Theme.getSelectorDrawable(false));
view.setClickable(true);

// Long press
view.setOnLongClickListener(v -> { ...; return true; });

// Multi-touch
view.setLongClickable(true);

// Keyboard
editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
editText.setOnEditorActionListener((v, actionId, event) -> { ... });
```

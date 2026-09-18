# 09 - المكونات المخصصة الأساسية (Custom Components)

> المشروع فيه **485 custom view**. هذه أهمها التي ستحتاجها.

---

## 1) `BackupImageView` — عرض صورة

**أهم View في المشروع.** يعرض صورة مع caching + blur + thumb + placeholder.

```java
import org.telegram.ui.Components.BackupImageView;

BackupImageView imageView = new BackupImageView(context);

// صورة من TLRPC
imageView.setImage(ImageLocation.getForUser(user, ImageLocation.TYPE_SMALL), null,
    ContextCompat.getDrawable(context, R.drawable.ic_placeholder),
    userFullName);

// صورة من ملف محلي
imageView.setImage(filePath, null, null, null);

// صورة من URL
imageView.setImage(url, null, null, null);

// صورة من TLRPC.Photo
imageView.setImage(photo, null, null, null);

// صورة من TLRPC.Document
imageView.setImage(document, null, null, null);

// صورة مربعة / دائرية
imageView.setRoundRadius(dp(20));  // دائرة
imageView.setRoundRadius(dp(8));   // مستطيل دائري
// أو
imageView.setRoundAsCircle(true);

// إعدادات
imageView.setSize(dp(120), dp(120));  // override
imageView.setShouldGenerateQualityThumb(true);
imageView.setAutoRepeat(false);  // GIF
imageView.setCrossfadeWithThumbImage(true);

// تحديث
imageView.invalidate();

// لإلغاء الصورة
imageView.setImageDrawable(null);
```

### خيارات شائعة
| Property | الوصف |
|----------|------|
| `setRoundRadius` | مستدير الحواف |
| `setRoundAsCircle` | دائرة |
| `setSize` | إجبار الحجم |
| `setShouldGenerateQualityThumb` | إنشاء thumb بجودة عالية |
| `setAutoRepeat` | إيقاف تكرار GIF |
| `setCrossfadeWithThumbImage` | تلاشي مع الـ thumb |
| `setColorFilter` | فلتر لوني |

---

## 2) `AvatarDrawable` — رسم Avatar محلياً

```java
import org.telegram.ui.Components.AvatarDrawable;

AvatarDrawable avatar = new AvatarDrawable();
avatar.setInfo(userId, fullName, TLRPC.User.class);  // أو Chat.class

// عند تحديث البيانات
avatar.setInfo(userId, "أحمد", TLRPC.User.class);
avatar.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfile));

// كـ drawable لـ ImageView
imageView.setImageDrawable(avatar);

// كـ خلفية
view.setBackground(avatar);

// يدوياً
avatar.draw(canvas);
```

### أنواع الـ Avatars
- `AVATAR_TYPE_NORMAL = 0` — مستخدم عادي
- `AVATAR_TYPE_SAVED = 1` — Saved Messages
- `AVATAR_TYPE_ARCHIVED = 2` — Archived chats
- `AVATAR_TYPE_SHARES = 3` — Shared links
- `AVATAR_TYPE_FILTER_CONTACTS = 4`
- `AVATAR_TYPE_FILTER_NON_CONTACTS = 5`
- `AVATAR_TYPE_FILTER_GROUPS = 6`
- `AVATAR_TYPE_FILTER_CHANNELS = 7`
- `AVATAR_TYPE_REPLIES = 12` — Replies
- (أنواع أخرى لأنواع مختلفة)

---

## 3) `RecyclerListView` — القائمة المحسّنة

راجع [07-recyclerlistview-and-cells.md](./07-recyclerlistview-and-cells.md).

---

## 4) `LayoutHelper` — منشئ LayoutParams

```java
import org.telegram.ui.Components.LayoutHelper;

// MATCH_PARENT, WRAP_CONTENT ثوابت

// FrameLayout
LayoutHelper.createFrame(int width, int height);
LayoutHelper.createFrame(int width, int height, int gravity);
LayoutHelper.createFrame(int width, int height, int gravity, float left, float top, float right, float bottom);

// LinearLayout
LayoutHelper.createLinear(int width, int height, float weight, int gravity, int left, int top, int right, int bottom);
LayoutHelper.createLinear(int width, int height, int gravity, int left, int top, int right, int bottom);

// RelativeLayout
LayoutHelper.createRelative(int width, int height, int left, int top, int right, int bottom);

// أمثلة
addView(view, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
addView(view, LayoutHelper.createFrame(dp(40), dp(40), Gravity.TOP | Gravity.LEFT, dp(16), dp(8), 0, 0));
addView(view, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1f, Gravity.CENTER_VERTICAL, 0, 0, 0, 0));
```

---

## 5) `SizeNotifierFrameLayout` — FrameLayout يكبر مع الكيبورد

```java
import org.telegram.ui.Components.SizeNotifierFrameLayout;

SizeNotifierFrameLayout container = new SizeNotifierFrameLayout(context);
container.setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate() {
    @Override
    public void onSizeChanged(int keyboardHeight, boolean isWidthGreater) {
        // تكبير/تصغير المحتوى
    }
});
```

---

## 6) `CubicBezierInterpolator` — Animations طبيعية

```java
import org.telegram.ui.Components.CubicBezierInterpolator;

CubicBezierInterpolator easeOut = new CubicBezierInterpolator(0.25, 0.1, 0.25, 1.0);
ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
anim.setInterpolator(easeOut);
anim.start();

// الموجودة
CubicBezierInterpolator.EASE_OUT
CubicBezierInterpolator.EASE_IN
CubicBezierInterpolator.EASE_IN_OUT
```

---

## 7) `CombinedDrawable` + `BackgroundDrawable`

```java
// خلفية مع ripple
Drawable ripple = Theme.getSelectorDrawable(false);
Drawable background = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundWhite));
Drawable combined = new CombinedDrawable(background, ripple, 0, 0);
combined.setFullSize(true);
view.setBackground(combined);

// خلفية مع ظل
Drawable shadow = new ShadowDrawable(context, 0, Theme.getColor(Theme.key_windowBackgroundWhite), 100, 16);
view.setBackground(shadow);
```

---

## 8) `RadialProgressView` — دائرة التحميل

```java
import org.telegram.ui.Components.RadialProgressView;

RadialProgressView progress = new RadialProgressView(context);
progress.setProgress(0.65f, true);
progress.setProgressColor(Theme.getColor(Theme.key_progressCircle));
```

---

## 9) `LineProgressView` — خط تقدم

```java
LineProgressView progress = new LineProgressView(context);
progress.setProgress(0.5f, true);
progress.setBackColor(Theme.getColor(Theme.key_dialogLineProgressBackground));
progress.setProgressColor(Theme.getColor(Theme.key_dialogLineProgress));
```

---

## 10) `SimpleTextView` — TextView مع animations

```java
import org.telegram.ui.ActionBar.SimpleTextView;

SimpleTextView title = new SimpleTextView(context);
title.setTextSize(18);
title.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
title.setTypeface(AndroidUtilities.bold());
title.setText("Title");

// مع أيقونة
title.setRightDrawable(R.drawable.ic_verified);
```

---

## 11) `EditTextBoldCursor` — حقل إدخال مخصص

```java
import org.telegram.ui.Components.EditTextBoldCursor;

EditTextBoldCursor editText = new EditTextBoldCursor(context);
editText.setHint(LocaleController.getString(R.string.Search));
editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
editText.setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
editText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
```

---

## 12) `CloseProgressDrawable2` — زر الإغلاق X

```java
CloseProgressDrawable2 close = new CloseProgressDrawable2();
close.setColor(Theme.getColor(Theme.key_actionBarDefaultIcon));
ImageView image = new ImageView(context);
image.setImageDrawable(close);
```

---

## 13) `CheckBox` مخصص (مع animation)

```java
CheckBox checkBox = new CheckBox(context, R.drawable.checkbox_states);
checkBox.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText),
                 Theme.getColor(Theme.key_windowBackgroundWhiteGrayText),
                 Theme.getColor(Theme.key_checkboxSquareDisabled));
checkBox.setChecked(true, true);
```

---

## 14) `Switch` مخصص

```java
import org.telegram.ui.Components.Switch;

Switch switchView = new Switch(context);
switchView.setColors(Theme.getColor(Theme.key_switchTrack),
                     Theme.getColor(Theme.key_switchTrackChecked),
                     Theme.getColor(Theme.key_switchTrackBlue),
                     Theme.getColor(Theme.key_switchTrackBlueChecked));
switchView.setChecked(true, true);

switchView.setOnCheckedChangeListener((v, isChecked) -> { ... });
```

---

## 15) `AvatarPreviewer` — عرض الـ Avatar

```java
AvatarPreviewer previewer = new AvatarPreviewer(context);
previewer.setUser(user);
previewer.setOnDeletePressed(() -> {});
```

---

## 16) `AlertDialog.Builder` (تفصيلي)

راجع [08-dialogs-sheets-bulletins.md](./08-dialogs-sheets-bulletins.md).

---

## 17) `BottomSheet` (تفصيلي)

راجع [08-dialogs-sheets-bulletins.md](./08-dialogs-sheets-bulletins.md).

---

## 18) `BulletinFactory` (تفصيلي)

راجع [08-dialogs-sheets-bulletins.md](./08-dialogs-sheets-bulletins.md).

---

## 19) `AnimatedEmojiDrawable` / `AnimatedEmojiSpan`

```java
import org.telegram.ui.Components.AnimatedEmojiDrawable;

AnimatedEmojiDrawable.SparseIntArray intArray = new AnimatedEmojiDrawable.SparseIntArray();
AnimatedEmojiDrawable drawable = AnimatedEmojiDrawable.make(PlayPauseViews.start, currentAccount, document);
drawable.setColorFilter(Theme.getAnimatedEmojiColorFilter(resourceProvider));
drawable.setBounds(0, 0, dp(24), dp(24));
canvas.draw(drawable);
```

---

## 20) `Spoilers` (نص مخفي)

```java
import org.telegram.ui.Components.spoilers.SpoilersTextView;

SpoilersTextView textView = new SpoilersTextView(context);
textView.setText(LocaleController.getString(R.string.HiddenText));
// المستخدم يسحب للكشف عن النص
```

---

## 21) `PaintSwatch` (اختيار لون)

```java
PaintSwatchView swatch = new PaintSwatchView(context);
swatch.setColor(Theme.getColor(Theme.key_chat_inBubble));
swatch.setOnClick(v -> selectColor(...));
```

---

## 22) `LayoutHelper` Advanced

```java
// ScrollView
LayoutHelper.createScroll(int width, int height, int gravity);
LayoutHelper.createScroll(int width, int height, int gravity, float left, float top, float right, float bottom);

// ViewPager
LayoutHelper.createViewPager(int width, int height);
```

---

## 23) `FireworksEffect` / `SnowflakesEffect` (مؤثرات)

```java
FireworksEffect fireworks = new FireworksEffect(context);
fireworks.start();

// أو
SnowflakesEffect snow = new SnowflakesEffect(context);
snow.start();
```

---

## 24) `PhotoViewer` (عارض الصور)

```java
PhotoViewer.getInstance().setParentActivity(parentActivity);
PhotoViewer.getInstance().openPhoto(photo, photoParent, photoIndex, options);
```

---

## 25) `LocationDrawable` (موقع في رسالة)

```java
LocationDrawable drawable = new LocationDrawable(loc, currentAccount);
drawable.setRoundRadius(dp(12));
```

---

## 26) `LetterDrawable` (حرف في قسم)

```java
LetterDrawable drawable = new LetterDrawable();
drawable.setLetter("A");
drawable.setColor(Theme.getColor(Theme.key_chats_nameMessage));
```

---

## 27) `SearchingForUserDrawable`

```java
SearchingForUserDrawable searching = new SearchingForUserDrawable();
searching.start();
```

---

## 28) `AvatarSpan`

```java
AvatarSpan span = new AvatarSpan(avatarDrawable, dp(20));
textView.setText(span);
```

---

## نمط الاختيار عند الحاجة

| أحتاج إلى | استخدم |
|------|------|
| عرض صورة | `BackupImageView` |
| رسم avatar بدون صورة | `AvatarDrawable` |
| قائمة | `RecyclerListView` |
| LayoutParams | `LayoutHelper` |
| FrameLayout مع كيبورد | `SizeNotifierFrameLayout` |
| Animation smooth | `CubicBezierInterpolator` |
| خلفية مع ظل | `ShadowDrawable` |
| خلفية مع ripple | `CombinedDrawable` |
| TextView في ActionBar | `SimpleTextView` |
| EditText | `EditTextBoldCursor` |
| Switch | `Switch` |
| CheckBox | `CheckBox` |
| دالة تحميل | `RadialProgressView` |
| دالة تحميل خطية | `LineProgressView` |
| دائرة تحميل بسيطة | `CheckBox` أو `ProgressBar` |
| Dialog | `AlertDialog` |
| Bottom Sheet | `BottomSheet` |
| Toast | `BulletinFactory` |
| Popup | `ActionBarPopupWindow` |
| Date/Time picker | `AlertsCreator.createXxxPickerDialog` |
| Photo viewer | `PhotoViewer` |
| Avatar في TextView | `AvatarSpan` |

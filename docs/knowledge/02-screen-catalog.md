# 02 - فهرس الشاشات (Screen Catalog)

> المشروع يحتوي على **225 Activity/Fragment** في `org.telegram.ui.*`.
> هذا الدليل يصنّفها ويصف كل شاشة بسرعة حتى تعرف أين تعمل.

---

## 1. شاشات الإطلاق والمصادقة (Launch & Auth)

| الملف | الدور | ملاحظات |
|------|------|--------|
| `LaunchActivity.java` | أول Activity (entry point في manifest) | يستضيف كل الـ Fragments بعد تسجيل الدخول |
| `IntroActivity.java` | تعريف بالتطبيق قبل تسجيل الدخول (3 شرائح) | يُعرض مرة واحدة |
| `LoginActivity.java` | شاشة تسجيل الدخول الرئيسية (phone + code) |  |
| `ActionIntroActivity.java` | تعريف سريع بشاشات الـ Login |  |
| `CountrySelectActivity.java` | اختيار الدولة لكود الهاتف | قائمة بحث |
| `PasscodeActivity.java` | إدخال كلمة سر التطبيق |  |
| `TwoStepVerificationActivity.java` | كلمة سر ثنائية (2FA) |  |
| `TwoStepVerificationSetupActivity.java` | إعداد 2FA |  |
| `LogoutActivity.java` | شاشة تأكيد تسجيل الخروج |  |
| `BasePermissionsActivity.java` | أب Activities الأذونات |  |

---

## 2. القائمة الرئيسية (Main Tabs)

| الملف | الدور |
|------|------|
| `MainTabsActivity.java` | الحاوية الرئيسية للـ Tabs (Chats / Contacts / Calls) |
| `DialogsActivity.java` ★ | **شاشة قائمة المحادثات** (الـ Tab الافتراضي) — الأكثر استخداماً |
| `ContactsActivity.java` | شاشة جهات الاتصال |
| `CallLogActivity.java` | سجل المكالمات |

---

## 3. المحادثات (Chats)

| الملف | الدور |
|------|------|
| `ChatActivity.java` ★★ | **أكبر شاشة (45k سطر)** — عرض/كتابة رسائل، attachments، رد، تعديل، حذف، تثبيت، إلخ |
| `ChatEditActivity.java` | تعديل رسالة |
| `ChatEditTypeActivity.java` | تغيير نوع الرسالة (نص/استفتاء/...) |
| `ChatLinkActivity.java` | إنشاء/إدارة رابط دعوة |
| `ChatReactionsEditActivity.java` | اختيار/إدارة التفاعلات |
| `ChatRightsEditActivity.java` | إدارة صلاحيات الأعضاء |
| `ChatUsersActivity.java` | قائمة أعضاء المحادثة |
| `PopupNotificationActivity.java` | إشعار منبثق لمكالمة واردة |
| `ArticleViewer.java` ★ | قارئ مقالات Instant View |

---

## 4. البروفايل (Profile)

| الملف | الدور |
|------|------|
| `ProfileActivity.java` ★ | شاشة البروفايل (مستخدم/مجموعة/قناة) — مشترك، إعلام، Wallpaper، لون، admin |
| `UserInfoActivity.java` | نسخة مبسطة لبروفايل مستخدم من رسالة |
| `ProfileNotificationsActivity.java` | إعدادات الإشعارات لمحادثة محددة |
| `ChannelColorActivity.java` | تغيير لون محادثة (peer color) |
| `GroupColorActivity.java` | تغيير لون مجموعة |
| `PeerColorActivity.java` | منتقي ألوان peer (موحّد) |
| `ChannelWallpaperActivity.java` | اختيار خلفية محادثة لقناة |
| `ChannelBoostLayout.java` | عرض بوستات القناة (Layout مخصص) |

---

## 5. الإعدادات (Settings)

| الملف | الدور |
|------|------|
| `SettingsActivity.java` ★ | شاشة الإعدادات الرئيسية |
| `DataSettingsActivity.java` | إعدادات البيانات والتخزين |
| `DataAutoDownloadActivity.java` | التحميل التلقائي للوسائط |
| `DataUsageActivity.java` | إحصائيات استخدام البيانات |
| `DataUsage2Activity.java` | نسخة جديدة من نفس الشاشة |
| `CacheControlActivity.java` | إدارة الكاش |
| `CacheChatsExceptionsFragment.java` | استثناءات كاش لمحادثات معينة |
| `LiteModeSettingsActivity.java` | إعدادات Lite Mode |
| `NotificationsSettingsActivity.java` | إعدادات الإشعارات العامة |
| `NotificationsCustomSettingsActivity.java` | إشعارات مخصصة لمحادثة |
| `NotificationsSoundActivity.java` | اختيار صوت الإشعار |
| `PrivacySettingsActivity.java` | الخصوصية (آخر ظهور، رقم الهاتف، ...) |
| `PrivacyControlActivity.java` | أذونات محادثات (دعوة، رسائل...) |
| `PrivacyUsersActivity.java` | استثناءات خصوصية لمستخدمين محددين |
| `SessionsActivity.java` | الجلسات النشطة |
| `PasskeysActivity.java` | مفاتيح المرور (WebAuthn) |
| `PrivacyControlActivity.java` | من يمكنه رؤية/الاتصال بي |
| `LanguageSelectActivity.java` | اختيار لغة التطبيق |
| `RestrictedLanguagesSelectActivity.java` | تقييد محتوى بلغة معينة |
| `QuickRepliesSettingsActivity.java` | الردود السريعة |
| `SaveToGallerySettingsActivity.java` | الحفظ في المعرض |
| `WallpapersListActivity.java` | قائمة الخلفيات |
| `ThemeActivity.java` | إدارة الثيمات (مدمج + مستورد) |
| `ThemePreviewActivity.java` | معاينة ثيم |
| `ThemeSetUrlActivity.java` | تثبيت ثيم من رابط |

---

## 6. الوسائط (Media)

| الملف | الدور |
|------|------|
| `MediaActivity.java` ★ | شاشة "الوسائط المشتركة" لمحادثة (صور/فيديو/ملفات/روابط/صوت) |
| `PhotoPickerActivity.java` | اختيار صور من المعرض |
| `PhotoAlbumPickerActivity.java` | اختيار ألبوم |
| `PhotoPickerSearchActivity.java` | بحث Gif |
| `PhotoCropActivity.java` | قص صورة (avatar) |
| `CalendarActivity.java` | تقويم (انتقالات/استخراج رسالة بيوم) |
| `IdenticonActivity.java` | معرّف فريد مرئي |
| `QrActivity.java` | مسح/عرض QR |

---

## 7. المرفقات والإرسال (Compose)

| الملف | الدور |
|------|------|
| `PollCreateActivity.java` | إنشاء استفتاء |
| `LocationActivity.java` | مشاركة/إرسال موقع |
| `ForwardActivity.java` (داخل ChatActivity) | إعادة توجيه |
| `GroupCreateActivity.java` | إنشاء مجموعة |
| `GroupCreateFinalActivity.java` | خطوة التأكيد |
| `ChannelCreateActivity.java` | إنشاء قناة |
| `GroupInviteActivity.java` | دعوة أعضاء |
| `InviteContactsActivity.java` | دعوة من جهات الاتصال |
| `FilterCreateActivity.java` | إنشاء فلتر |
| `FilterChatlistActivity.java` | قائمة محادثات (chat folder) |
| `FiltersSetupActivity.java` | خطوات إعداد الفلاتر |
| `TopicCreateFragment.java` | إنشاء توبك (موضوع) |
| `TopicsFragment.java` | قائمة توبكات في مجموعة |

---

## 8. البحث والاكتشاف (Search & Discovery)

| الملف | الدور |
|------|------|
| `SearchActivity.java` (ليس نشاط منفصل) | البحث يحدث في DialogsActivity (الـ search field) |
| `FeaturedStickersActivity.java` | ملصقات مميزة |
| `ArchivedStickersActivity.java` | ملصقات مؤرشفة |
| `GroupStickersActivity.java` | ملصقات مجموعة |
| `StickersActivity.java` | إدارة الملصقات |
| `ArchiveSettingsActivity.java` | إعدادات الأرشيف |
| `ArchivedStickersActivity.java` | الملصقات المؤرشفة |
| `TooManyCommunitiesActivity.java` | عرض عند تجاوز عدد المجموعات |
| `EnableTopicsActivity.java` | تفعيل التوبكات لمجموعة |

---

## 9. الحساب (Account)

| الملف | الدور |
|------|------|
| `ChangeNameActivity.java` | تعديل الاسم الأول/الأخير |
| `ChangeBioActivity.java` | تعديل البايو |
| `ChangeUsernameActivity.java` | تعديل اسم المستخدم |
| `AutoDeleteMessagesActivity.java` | الحذف التلقائي للرسائل |
| `MemberRequestsActivity.java` | طلبات الانضمام |
| `PassportActivity.java` | Telegram Passport (وثائق) |
| `IdenticonActivity.java` | رمز مرئي للحساب |

---

## 10. الإحصاءات والقنوات (Stats & Channels)

| الملف | الدور |
|------|------|
| `StatisticActivity.java` | إحصاءات قناة |
| `MessageStatisticActivity.java` | إحصاءات رسالة |
| `ChannelAdminLogActivity.java` | سجل إدارة القناة |
| `ManageLinksActivity.java` | إدارة روابط دعوة |
| `BoostedChannelActivity.java` (داخل) | قائمة القنوات المدعومة |
| `PostSuggestionsEditActivity.java` | اقتراحات النشر |

---

## 11. VoIP والمكالمات

| الملف | الدور |
|------|------|
| `CallsActivity.java` | شاشة المكالمة (عند قفل الشاشة) |
| `GroupCallActivity.java` | مكالمة جماعية |
| `VoIPFragment.java` | الـ Fragment داخل المكالمة |
| `VoIPPermissionActivity.java` | طلب إذن المكالمة |
| `VoIPFeedbackActivity.java` | ملاحظات بعد المكالمة |

---

## 12. Webview و Bots

| الملف | الدور |
|------|------|
| `WebviewActivity.java` | WebView مخصص |
| `PaymentFormActivity.java` | نموذج دفع |
| `RecurrentPaymentsActivity` (داخل) | المدفوعات المتكررة |

---

## 13. القصص والمحتوى الجديد (Stories & New)

| الملف | الدور |
|------|------|
| `Stories/*` | مديولات القصص (مجلد فرعي) |
| `ReactionsDoubleTapManageActivity.java` | إدارة التفاعلات بالنقر المزدوج |
| `BubbleActivity.java` | وضع الفقاعة |
| `GradientHeaderActivity.java` | هيدر متدرّج |

---

## 14. منوعات (Misc)

| الملف | الدور |
|------|------|
| `TestActivity.java` | للاختبار |
| `ViewPagerActivity.java` | مبدّل ViewPager عام |
| `EditWidgetActivity.java` | تعديل ويدجت |
| `FeedWidgetConfigActivity.java` | إعدادات ويدجت feed |
| `ChatsWidgetConfigActivity.java` | إعدادات ويدجت المحادثات |
| `ContactsWidgetConfigActivity.java` | إعدادات ويدجت جهات الاتصال |
| `PasskeyActivity.java` | إعداد Passkey |
| `DialogOrContactPickerActivity.java` | منتقي محادثة/جهة اتصال |
| `UsersSelectActivity.java` | اختيار مستخدمين |
| `ExternalActionActivity.java` | إجراءات خارجية (intent) |
| `ShareActivity.java` | استلام مشاركة |
| `CameraScanActivity.java` | مسح كاميرا (QR) |

---

## Cells (عناصر القوائم الجاهزة — 140 cell)

### أساسية (Settings/General)
- `TextCell.java` — عنوان + subtitle + أيقونة
- `TextSettingsCell.java` — قائمة إعدادات
- `TextDetailCell.java` — عنوان + سطرين
- `TextCheckCell.java` — Switch
- `TextCheckCell2.java` — CheckBox
- `TextRadioCell.java` — Radio button
- `TextInfoCell.java` — فقرة معلوماتية
- `TextInfoPrivacyCell.java` — فقرة privacy
- `TextRightIconCell.java` — عنوان + أيقونة يمين
- `HeaderCell.java` — عنوان قسم
- `ShadowSectionCell.java` — فاصل بظل
- `DividerCell.java` — خط فاصل
- `GraySectionCell.java` — فاصل رمادي
- `EmptyCell.java` — فراغ
- `FixedHeightEmptyCell.java` — فراغ بطول محدد
- `LoadingCell.java` — دائرة تحميل

### مستخدمين ومحادثات
- `DialogCell.java` — صف محادثة في قائمة
- `DialogMeUrlCell.java` — روابط "محادثاتي"
- `UserCell.java` / `UserCell2.java` — صف مستخدم
- `UserInfoCell.java`
- `GroupCreateUserCell.java`
- `ManageChatUserCell.java`
- `ProfileChannelCell.java`
- `CheckBoxUserCell.java`
- `InviteUserCell.java`
- `SessionCell.java` — جلسة
- `SettingsSuggestionCell.java`
- `DialogRadioCell.java` — اختيار محادثة
- `RadioCell.java`
- `RadioButtonCell.java`

### رسائل
- `ChatMessageCell.java` ★★ — **أكبر cell (29k سطر)** — يعرض رسالة بكل أشكالها
- `ChatActionCell.java` — رسالة نظام (action)
- `ChatLoadingCell.java`
- `ChatUnreadCell.java` — فاصل "رسائل جديدة"
- `DialogsEmptyCell.java`
- `DialogsHintCell.java`
- `DialogsRequestedEmptyCell.java`
- `TopicExceptionCell.java`
- `TopicSearchCell.java`

### ملصقات وGif
- `StickerCell.java` / `StickerEmojiCell.java`
- `StickerSetCell.java` / `StickerSetNameCell.java`
- `StickerSetGroupInfoCell.java`
- `FeaturedStickerSetCell.java` / `FeaturedStickerSetCell2.java`
- `FeaturedStickerSetInfoCell.java`
- `ArchivedStickerSetCell.java`

### رسومات وتفاعل
- `EmojiReplacementCell.java`
- `EditEmojiTextCell.java`
- `AvailableReactionCell.java`
- `PollCreateCheckCell.java`
- `PhotoPickerPhotoCell.java`
- `PhotoAttachPhotoCell.java` / `PhotoAttachCameraCell.java`
- `PhotoEditToolCell.java` / `PhotoEditRadioCell.java`
- `PatternCell.java`
- `ThemeCell.java` / `ThemeTypeCell.java` / `ThemePreviewMessagesCell.java` / `ThemesHorizontalListCell.java`
- `WallpaperCell.java`
- `RadioColorCell.java` / `TextColorCell.java` / `TextColorThemeCell.java`
- `PaymentInfoCell.java` / `TextPriceCell.java`
- `BotButton.java` / `BotHelpCell.java` / `BotSwitchCell.java` / `BotAskCell.java` / `BotAskCellDrawable.java`

### موقع وجهات اتصال
- `LocationCell.java` / `LocationDirectionCell.java`
- `LocationLoadingCell.java` / `LocationPoweredCell.java`
- `SendLocationCell.java` / `SharingLiveLocationCell.java`
- `ContextLinkCell.java`

### استفتاءات ومشاركات
- `SharedAudioCell.java` / `SharedDocumentCell.java`
- `SharedLinkCell.java` / `SharedMediaSectionCell.java`
- `SharedPhotoVideoCell.java` / `SharedPhotoVideoCell2.java`
- `ShareDialogCell.java` / `ShareTopicCell.java`

### المظهر (UI shell)
- `AppIconsSelectorCell.java` — اختيار أيقونة التطبيق
- `AccountSelectCell.java` — اختيار حساب
- `LanguageCell.java` — اختيار لغة
- `MentionCell.java`
- `HashtagSearchCell.java`
- `UnconfirmedAuthHintCell.java`
- `AboutLinkCell.java`

---

## أهم 10 شاشات يجب أن تعرفها

| # | الملف | لماذا مهم |
|---|------|----------|
| 1 | `ChatActivity.java` | كل المنطق البصري للرسالة موجود هنا |
| 2 | `DialogsActivity.java` | قائمة المحادثات — افهم نمط Adapter |
| 3 | `ProfileActivity.java` | نموذج لشاشة "تفاصيل كيان" |
| 4 | `SettingsActivity.java` | نموذج لشاشة إعدادات |
| 5 | `LaunchActivity.java` | كيف تُدار كل الـ Fragments |
| 6 | `MediaActivity.java` | تابات + ViewPager |
| 7 | `LoginActivity.java` | نموذج لشاشة إدخال |
| 8 | `ThemeActivity.java` | كيف يبني ثيم |
| 9 | `GroupCreateActivity.java` | معالج متعدد الخطوات |
| 10 | `PhotoPickerActivity.java` | مثال لتطبيق يستخدم الـ GridLayoutManager |

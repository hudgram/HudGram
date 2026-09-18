# دليل تقديم Hudgram إلى Google Play (سياسات الخصوصية وأمان البيانات)

يحتوي هذا المستند على النصوص والخيارات الجاهزة للتقديم في Google Play Console لضمان الموافقة على التطبيق دون مشاكل.

---

## 1. نموذج سياسة الخصوصية (Privacy Policy Template)

> [!IMPORTANT]
> يجب عليك نسخ هذا النص وحفظه في صفحة ويب عامة (مثلاً باستخدام **GitHub Pages**، أو **Google Sites**، أو أي موقع آخر مجاني) ووضع الرابط الخاص بها في حقل "سياسة الخصوصية" في Google Play Console.

### النص البرمجي لسياسة الخصوصية (باللغة الإنجليزية - مفضلة لدى جوجل):

```markdown
# Privacy Policy for Hudgram

**Effective Date: June 13, 2026**

Hudgram ("we", "our", or "us") is a messaging application built using the official Telegram API and MTProto protocol. This Privacy Policy describes how we collect, use, and process your data when you use the Hudgram application.

### 1. Data Processing and Telegram API
Hudgram is a client application based on the open-source Telegram for Android project. All core messaging data (including chats, groups, channels, media, and contacts) is securely transmitted, processed, and stored directly on Telegram's official cloud servers. We do NOT host or run our own messaging servers.

Your interactions are protected by Telegram’s privacy policy and security protocols. For more details, please refer to the official [Telegram Privacy Policy](https://telegram.org/privacy).

### 2. Information We Collect and Process
To provide messaging services, the following data is processed:
* **Account Information:** To create a Hudgram account, you must provide your phone number. This number is sent directly to Telegram to authenticate you and send your verification code.
* **Contacts:** If you grant permission, Hudgram accesses and syncs your device contacts to Telegram servers so you can connect with your friends. We do not store your contacts on any third-party servers.
* **User-Generated Content (UGC):** Your messages, photos, videos, documents, and other media are processed and transmitted through Telegram’s encrypted network to deliver them to your recipients.
* **Device Identifiers:** We may collect basic device identifiers (such as Firebase Cloud Messaging tokens) solely to deliver push notifications for incoming messages.

### 3. Data Encryption and Security
All communications and data transfers in Hudgram are encrypted in transit using Telegram’s proprietary MTProto security protocol and standard TLS encryption. 

### 4. Data Deletion and Retention
You have full control over your data. You can delete your messages, media, or your entire account at any time. Account deletion can be performed directly within the app or via the official Telegram account deactivation page. Once deleted, your data is permanently removed from Telegram's servers.

### 5. Children's Privacy
Our services are not intended for individuals under the age of 16 (or 13 depending on local jurisdiction). We do not knowingly collect personal data from children.

### 6. Changes to This Policy
We may update this Privacy Policy from time to time. We will notify you of any changes by updating the "Effective Date" at the top of this page.

### 7. Contact Us
If you have any questions about this Privacy Policy, please contact us at:
* Email: support@hudgram.com (أو ضع بريدك الإلكتروني هنا)
```

---

## 2. دليل تعبئة استمارة "أمان البيانات" (Data Safety Questionnaire Guide)

عند تعبئة استمارة **Data Safety** في لوحة تحكم مطور جوجل بلاي، اختر الإجابات التالية بدقة:

### القسم 1: جمع البيانات ومشاركتها (Data Collection & Sharing)
* **هل يجمع تطبيقك أو يشارك أيًا من أنواع بيانات المستخدمين المطلوبة؟**
  * اختر: **نعم (Yes)**.
* **هل يتم تشفير جميع بيانات المستخدمين التي يجمعها تطبيقك أثناء نقلها؟**
  * اختر: **نعم (Yes)**.
* **هل توفر طريقة للمستخدمين لطلب حذف بياناتهم؟**
  * اختر: **نعم (Yes)**.

---

### القسم 2: تفصيل أنواع البيانات المجمعة (Data Types Checklist)

قم بتحديد البيانات التالية والإقرار بجمعها من أجل **وظائف التطبيق (App Functionality)**:

| نوع البيانات (Data Type) | خيار الجمع (Collected) | خيار المشاركة (Shared) | الغرض (Purpose) |
| :--- | :---: | :---: | :--- |
| **Personal Info > Phone number** | نعم | لا | وظائف التطبيق وتوثيق الحساب (App Functionality / Account Management) |
| **Personal Info > User IDs** | نعم | لا | وظائف التطبيق (App Functionality) |
| **Contacts** | نعم | لا | وظائف التطبيق ومزامنة الأصدقاء (App Functionality / Contact Management) |
| **Photos and Videos** | نعم | لا | وظائف التطبيق وإرسال الوسائط (App Functionality) |
| **Files and Docs** | نعم | لا | وظائف التطبيق وإرسال الملفات (App Functionality) |
| **Device or other IDs** | نعم | لا | إرسال الإشعارات الفورية (App Functionality / Push Notifications) |

> [!NOTE]
> لجميع الأنواع المذكورة أعلاه، عندما تسألك جوجل: **"هل تتم معالجة هذه البيانات بشكل سريع (Ephemeral) أم يتم تخزينها؟"**
> اختر: **يتم تخزينها (Stored)** لأن الرسائل والجهات المخزنة سحابياً في سيرفرات تيليجرام تظل محفوظة ليتمكن المستخدم من الوصول إليها من أي جهاز آخر.

---

## 3. إقرار المحتوى المُنشأ بواسطة المستخدم (UGC Declaration)

تفرض جوجل شروطاً صارمة على تطبيقات المحادثات لمنع الإساءة. إذا سألتك جوجل عن كيفية التعامل مع المحتوى الضار (UGC)، يمكنك كتابة الإقرار التالي:

### نص الإقرار باللغة الإنجليزية:
> "Hudgram is a client application based on the Telegram API. The app fully complies with Google Play's UGC policy by integrating Telegram's robust, built-in reporting and blocking tools. Users can instantly block any abusive user, which prevents them from sending further messages. Additionally, users can report abusive content, messages, groups, or channels directly to Telegram's moderation team for review and removal."

---

## 4. إقرار صلاحيات دخول التطبيق للمراجعة (App Access Credentials)

لكي يقوم مراجع جوجل بفحص التطبيق وتجاوز شاشة تسجيل الدخول بـ SMS، يجب عليك توفير حساب تجريبي في قسم **App Access**:

1. انتقل إلى **App Content** ثم **App Access**.
2. اختر **All or some functionality is restricted** (بعض أو كل الوظائف مقيدة).
3. اضغط على **Add new credential** وأدخل البيانات التالية:
   * **Phone Number / Username**: أدخل رقم هاتف تجريبي حقيقي قمت بتهيئته مسبقاً (أو رقم حقيقي تستطيع استقبال الرمز عليه وتفعيله).
   * **Password / Verification Code**: أدخل كود التفعيل التجريبي (مثلاً إذا كان حساباً اختبارياً من تيليجرام).
   * **Any other instructions**: اكتب لهم بالإنجليزية:
     > "Please use the provided phone number to sign in. The verification code is static and configured for testing, or please contact us immediately to supply the active code if it is dynamic."

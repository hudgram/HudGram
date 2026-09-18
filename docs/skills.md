# Telegram/Hudgram Developer Skills & Guidelines

This document outlines the core coding standards, UI/UX aesthetics, localization rules, and development practices established for the Hudgram project. Refer to this guide to maintain absolute consistency, premium aesthetics, and native performance.

---

## 1. UI Styling & Design Aesthetics

### Theme Compatibility (No Hardcoded Colors)
* **Rule**: Never use hardcoded color values (such as `#FFFFFF`, `#1A1A1A`, or custom RGB/ARGB values) in UI layouts or Java code.
* **Practice**: Always retrieve colors dynamically from the active Telegram theme engine using official theme keys.
* **Implementation**:
  ```java
  // Get color using active resourceProvider
  int textColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourceProvider);
  int accentColor = Theme.getColor(Theme.key_chats_actionBackground, resourceProvider);
  int dividerColor = Theme.getColor(Theme.key_divider, resourceProvider);
  ```
* **Benefit**: Ensures 100% compatibility with Light mode, Dark mode, and custom user-installed Telegram themes.

### Modern Settings Card UX
* **Rule**: Settings screens must follow Telegram's modern card layout. Avoid flat, continuous lists.
* **Practice**:
  - Group settings items in inset cards.
  - Apply **12dp corner radius** to the card container.
  - Apply **16dp left/right margins** and **8dp top/bottom margins** to the cards.
  - Set `setClipToOutline(true)` on card layout wrappers to clip child views and ripple backgrounds neatly to the rounded corners.

### Native Telegram Widgets & Cells
* **Rule**: Rely on Telegram's built-in UI cells rather than writing raw layouts or standard Android views (like `Button`, `TextView`, or standard `CheckBox`).
* **Key Components**:
  - `HeaderCell`: Used for section titles inside cards.
  - `TextSettingsCell`: Used for single-line click action setting items (with/without icon and value description).
  - `TextDetailSettingsCell`: Used for two-line setting items containing a title and a detailed description.
  - `TextCheckCell` / `TextDetailCheckCell`: Used for settings containing toggle switches.
  - `TextInfoPrivacyCell`: Used for standalone description texts below cards.
  - `ShadowSectionCell` / `UItem.asShadow()`: Used for spacing between cards.
* **Constructor Verification Warning**:
  > [!WARNING]
  > Always verify the exact signature of a Telegram cell/widget constructor before instantiation. Some cells in the codebase only accept `Context` (e.g. `new TextDetailSettingsCell(context)`), while others might accept a `ResourcesProvider` (e.g. `new TextDetailSettingsCell(context, resourceProvider)`). Passing an unsupported argument will cause compilation failures.

---

## 2. Localization & String Management

### Zero Raw Java Strings
* **Rule**: Never write user-facing text strings directly in Java files.
* **Practice**: All strings must be externalized into `strings.xml` resource files.

### Multilingual Support (English & Arabic)
* **Rule**: For every new key, add localization in both:
  - English: [strings.xml](file:///f:/ProjectsAPP/TelegramApp/TMessagesProj/src/main/res/values/strings.xml)
  - Arabic: [strings.xml](file:///f:/ProjectsAPP/TelegramApp/TMessagesProj/src/main/res/values-ar/strings.xml)
* **Practice**: Use `LocaleController` to fetch localized strings:
  ```java
  // Plain text
  String appName = LocaleController.getString("AppName", R.string.AppName);
  
  // Formatted string with variables
  String version = LocaleController.formatString("AboutHudgramVersion", R.string.AboutHudgramVersion, versionName, packageName);
  ```

### Prevention of Resource Duplication
* **Rule**: Do not add duplicate XML resource keys in `strings.xml`. Adding a key that already exists causes Gradle resource merger task failures (`Found item String/KeyName more than one time`).
* **Practice**: Always check if a string key exists before writing a new one. If you need a new string, choose a unique key prefix (e.g., `HudAbout...` or `HudSettings...`).

### Activity Localization Helper
* **Practice**: Keep a dynamic `getString(String key)` helper inside custom settings activities to map custom keys to their resources cleanly.

---

## 3. Deep Linking & Settings Navigation

### Query Parameter-Based URL Format
* **Rule**: Settings URLs must be generated in the query-parameter format:
  `https://t.me/hudsettings?k=<key>&r=<slug>`
  *(Do NOT use sub-paths like `t.me/hudsettings/<key>`)*.
* **Why**:
  - **Fallback**: On official Telegram clients, since `hudsettings` is a channel username, it will gracefully fall back to opening the `@hudsettings` channel page (ignoring query parameters) instead of showing a "Message not found" post error.
  - **Interception**: Hudgram intercepts query parameters `k` (setting key) and `r` (row slug) to launch the local settings screen and focus the row.

### Safe Scroll & Highlight Implementation
* **Rule**: Execute layout scrolling (`scrollToPositionWithOffset`) **outside** of the `highlightRow` callback.
* **Implementation**:
  ```java
  public void scrollToRow(String key, Runnable unknown) {
      if (listView == null) return;
      int position = listView.findPositionByItemSlug(key);
      if (position != -1) {
          LinearLayoutManager layoutManager = (LinearLayoutManager) listView.getLayoutManager();
          if (layoutManager != null) {
              layoutManager.scrollToPositionWithOffset(position, AndroidUtilities.dp(60));
          }
          listView.highlightRow(() -> position);
      } else {
          if (unknown != null) unknown.run();
      }
  }
  ```
* **Why**: Triggering layout scrolls inside the callback causes recursive layout passes, which resets the scroll state or cancels the hover highlight effect.

### Key Overrides & Parameter Passing
* **Rule**: Every setting fragment subclassing `BaseHudSettingsActivity` must override `getKey()` to return its unique setting key (e.g., `"drafts"`, `"quickReply"`).
* **Practice**: When routing deep links in `LinkManager.java`, set the `"scroll_to"` argument on the fragment bundle before presentation to trigger native scroll-on-transition:
  ```java
  if (!TextUtils.isEmpty(slug)) {
      Bundle args = new Bundle();
      args.putString("scroll_to", slug);
      fragment.setArguments(args);
  }
  ```

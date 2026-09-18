package com.hudgram.ui.changelog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import io.noties.markwon.Markwon;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class HudChangelogBottomSheet extends BottomSheet {

    private static final String PREF_NAME = "hudgram_changelog_pref";
    private static final String KEY_LAST_SHOWN_VERSION = "last_shown_changelog_build";

    private boolean dismissAllowed = false;

    public static boolean showIfNeeded(Context context) {
        if (context == null) {
            return false;
        }
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String currentVersionId = BuildVars.APP_VERSION_CODE + "_" + BuildVars.HUD_VERSION;
        String lastShown = preferences.getString(KEY_LAST_SHOWN_VERSION, "");

        if (!currentVersionId.equals(lastShown)) {
            preferences.edit().putString(KEY_LAST_SHOWN_VERSION, currentVersionId).apply();
            show(context);
            return true;
        }
        return false;
    }

    public static void show(Context context) {
        if (context == null) {
            return;
        }
        new HudChangelogBottomSheet(context, false, null).show();
    }

    public HudChangelogBottomSheet(Context context, boolean needFocus, Theme.ResourcesProvider resourcesProvider) {
        super(context, needFocus, resourcesProvider);
        setDimBehind(true);

        // Prevent accidental dismiss via swipe, touch outside, or back button
        setCanDismissWithSwipe(false);
        setCanDismissWithTouchOutside(false);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        boolean isRtl = LocaleController.isRTL;
        int primaryText = Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider);
        int dialogBg = Theme.getColor(Theme.key_dialogBackground, resourcesProvider);
        int btnBgColor = Theme.getColor(Theme.key_featuredStickers_addButton, resourcesProvider);
        int btnPressedColor = Theme.getColor(Theme.key_featuredStickers_addButtonPressed, resourcesProvider);
        int btnTextColor = Theme.getColor(Theme.key_featuredStickers_buttonText, resourcesProvider);

        // Root container with height constraint leaving a distinct top gap
        FrameLayout rootLayout = new FrameLayout(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int screenHeight = AndroidUtilities.displaySize.y;
                int topGap = Math.max(AndroidUtilities.dp(84), AndroidUtilities.statusBarHeight + AndroidUtilities.dp(48));
                int maxAllowedHeight = screenHeight - topGap;
                int size = MeasureSpec.getSize(heightMeasureSpec);
                if (size > maxAllowedHeight || MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxAllowedHeight, MeasureSpec.AT_MOST);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };

        // Elegant rounded top background
        GradientDrawable rootBg = new GradientDrawable();
        rootBg.setColor(dialogBg);
        rootBg.setCornerRadii(new float[]{
                AndroidUtilities.dp(16), AndroidUtilities.dp(16),
                AndroidUtilities.dp(16), AndroidUtilities.dp(16),
                0, 0, 0, 0
        });
        rootLayout.setBackground(rootBg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootLayout.setClipToOutline(true);
        }

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        // 1. Drag Handle
        View dragHandle = new View(context);
        GradientDrawable handleDrawable = new GradientDrawable();
        handleDrawable.setShape(GradientDrawable.RECTANGLE);
        handleDrawable.setCornerRadius(AndroidUtilities.dp(2.5f));
        handleDrawable.setColor(Theme.getColor(Theme.key_sheet_scrollUp, resourcesProvider));
        dragHandle.setBackground(handleDrawable);
        mainLayout.addView(dragHandle, LayoutHelper.createLinear(36, 5, Gravity.CENTER_HORIZONTAL, 0, 10, 0, 8));

        // 2. Scrollable Body for Markdown Document
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        scrollView.setVerticalScrollBarEnabled(false);

        LinearLayout scrollContent = new LinearLayout(context);
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        scrollContent.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(6), AndroidUtilities.dp(20), AndroidUtilities.dp(12));

        // Markdown Document TextView (Rendered in clean black / dark text, Markdown layout)
        TextView markdownTextView = new TextView(context);
        markdownTextView.setTextColor(primaryText);
        markdownTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        markdownTextView.setLineSpacing(AndroidUtilities.dp(5), 1.0f);
        if (isRtl) {
            markdownTextView.setGravity(Gravity.RIGHT);
            markdownTextView.setTextDirection(View.TEXT_DIRECTION_RTL);
        } else {
            markdownTextView.setGravity(Gravity.LEFT);
            markdownTextView.setTextDirection(View.TEXT_DIRECTION_LTR);
        }

        // Render Markdown content using Markwon and apply Rich Text Badges
        String markdownContent = buildMarkdownText();
        try {
            Markwon markwon = Markwon.create(context);
            Spanned spanned = markwon.toMarkdown(markdownContent);
            SpannableStringBuilder ssb = new SpannableStringBuilder(spanned);
            applyChangelogBadges(ssb, resourcesProvider);
            markdownTextView.setText(ssb);
        } catch (Throwable e) {
            FileLog.e(e);
            markdownTextView.setText(markdownContent);
        }

        scrollContent.addView(markdownTextView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        scrollView.addView(scrollContent, LayoutHelper.createScroll(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.NO_GRAVITY));
        mainLayout.addView(scrollView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 0, 1.0f));

        // 3. Bottom Action Buttons Bar (Horizontal side-by-side layout)
        LinearLayout bottomBar = new LinearLayout(context);
        bottomBar.setOrientation(LinearLayout.VERTICAL);
        bottomBar.setBackgroundColor(dialogBg);
        bottomBar.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(6), AndroidUtilities.dp(16), AndroidUtilities.dp(16));

        // Divider above buttons
        View divider = new View(context);
        divider.setBackgroundColor(Theme.getColor(Theme.key_divider, resourcesProvider));
        bottomBar.addView(divider, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1, 0, 0, 0, 10));

        // Horizontal Row for Buttons
        LinearLayout buttonsRow = new LinearLayout(context);
        buttonsRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonsRow.setGravity(Gravity.CENTER_VERTICAL);

        // Button 1: Official Channel (Secondary tinted button)
        FrameLayout channelButton = new FrameLayout(context);
        channelButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(
                AndroidUtilities.dp(12),
                ColorUtils.setAlphaComponent(btnBgColor, 35),
                ColorUtils.setAlphaComponent(btnBgColor, 75)
        ));
        channelButton.setOnClickListener(v -> {
            Browser.openUrl(getContext(), BuildVars.HUDGRAM_CHANNEL_URL);
            // Stays open until user confirms with "Got it"
        });

        TextView channelButtonText = new TextView(context);
        channelButtonText.setText(LocaleController.getString("HudChangelogChannelBtn", R.string.HudChangelogChannelBtn));
        channelButtonText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        channelButtonText.setTypeface(AndroidUtilities.bold());
        channelButtonText.setTextColor(btnBgColor);
        channelButtonText.setGravity(Gravity.CENTER);
        channelButton.addView(channelButtonText, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 4, 0, 4, 0));

        // Button 2: Got It / Understand (Primary filled button)
        FrameLayout gotItButton = new FrameLayout(context);
        gotItButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(
                AndroidUtilities.dp(12),
                btnBgColor,
                btnPressedColor
        ));
        gotItButton.setOnClickListener(v -> {
            dismissAllowed = true;
            dismiss();
        });

        TextView gotItButtonText = new TextView(context);
        gotItButtonText.setText(LocaleController.getString("HudChangelogCloseBtn", R.string.HudChangelogCloseBtn));
        gotItButtonText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        gotItButtonText.setTypeface(AndroidUtilities.bold());
        gotItButtonText.setTextColor(btnTextColor);
        gotItButtonText.setGravity(Gravity.CENTER);
        gotItButton.addView(gotItButtonText, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 4, 0, 4, 0));

        // Symmetric horizontal layout with 8dp spacing
        buttonsRow.addView(channelButton, LayoutHelper.createLinear(0, 48, 1.0f, 0, 0, 4, 0));
        buttonsRow.addView(gotItButton, LayoutHelper.createLinear(0, 48, 1.0f, 4, 0, 0, 0));

        bottomBar.addView(buttonsRow, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        mainLayout.addView(bottomBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        rootLayout.addView(mainLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setCustomView(rootLayout);
    }

    @Override
    protected boolean canDismissWithSwipe() {
        return false;
    }

    @Override
    protected boolean canDismissWithTouchOutside() {
        return false;
    }

    @Override
    public void onBackPressed() {
        // Do not dismiss on back press unless "Got it" button is clicked
    }

    @Override
    public void dismiss() {
        if (dismissAllowed) {
            super.dismiss();
        }
    }

    private void applyChangelogBadges(SpannableStringBuilder ssb, Theme.ResourcesProvider resourcesProvider) {
        if (ssb == null) {
            return;
        }

        String[] tags = new String[]{
                LocaleController.getString("HudChangelogTagAdd", R.string.HudChangelogTagAdd),
                LocaleController.getString("HudChangelogTagImprove", R.string.HudChangelogTagImprove),
                LocaleController.getString("HudChangelogTagFix", R.string.HudChangelogTagFix),
                LocaleController.getString("HudChangelogTagModify", R.string.HudChangelogTagModify),
                LocaleController.getString("HudChangelogTagExclusive", R.string.HudChangelogTagExclusive),
                LocaleController.getString("HudChangelogTagBase", R.string.HudChangelogTagBase)
        };

        int[] bgColors = new int[]{
                Theme.getColor(Theme.key_avatar_backgroundGreen, resourcesProvider),
                Theme.getColor(Theme.key_avatar_backgroundBlue, resourcesProvider),
                Theme.getColor(Theme.key_avatar_backgroundRed, resourcesProvider),
                Theme.getColor(Theme.key_avatar_backgroundViolet, resourcesProvider),
                Theme.getColor(Theme.key_avatar_backgroundOrange, resourcesProvider),
                Theme.getColor(Theme.key_avatar_backgroundCyan, resourcesProvider)
        };

        int textColor = Theme.getColor(Theme.key_featuredStickers_buttonText, resourcesProvider);

        String textStr = ssb.toString();
        for (int i = 0; i < tags.length; i++) {
            String tag = tags[i];
            String target = "[" + tag + "]";
            int index = 0;
            while ((index = textStr.indexOf(target, index)) >= 0) {
                int end = index + target.length();
                ssb.setSpan(new ChangelogBadgeSpan(tag, bgColors[i], textColor), index, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                index = end;
            }
        }
    }

    private String buildMarkdownText() {
        StringBuilder sb = new StringBuilder();

        String tagBase = "[" + LocaleController.getString("HudChangelogTagBase", R.string.HudChangelogTagBase) + "]";
        String tagImprove = "[" + LocaleController.getString("HudChangelogTagImprove", R.string.HudChangelogTagImprove) + "]";
        String tagFix = "[" + LocaleController.getString("HudChangelogTagFix", R.string.HudChangelogTagFix) + "]";
        String tagModify = "[" + LocaleController.getString("HudChangelogTagModify", R.string.HudChangelogTagModify) + "]";
        String tagExclusive = "[" + LocaleController.getString("HudChangelogTagExclusive", R.string.HudChangelogTagExclusive) + "]";
        String tagAdd = "[" + LocaleController.getString("HudChangelogTagAdd", R.string.HudChangelogTagAdd) + "]";

        // Document Title & Subtitle
        sb.append("# ").append(LocaleController.getString("HudChangelogTitle", R.string.HudChangelogTitle)).append("\n\n");
        sb.append(LocaleController.getString("HudChangelogSubtitle", R.string.HudChangelogSubtitle)).append("\n\n");
        sb.append("---\n\n");

        // === RELEASE 1: HUDGRAM v1.0.1 (Current Update) ===
        sb.append("## ").append(LocaleController.getString("HudChangelogV101Header", R.string.HudChangelogV101Header)).append("\n\n");

        // 1. Telegram Communities
        sb.append("### ").append(LocaleController.getString("HudChangelogV101SecCommunities", R.string.HudChangelogV101SecCommunities)).append("\n\n");
        sb.append("- ").append(tagBase).append(" ").append(LocaleController.getString("HudChangelogV101Comm1", R.string.HudChangelogV101Comm1)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV101Comm2", R.string.HudChangelogV101Comm2)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV101Comm3", R.string.HudChangelogV101Comm3)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV101Comm4", R.string.HudChangelogV101Comm4)).append("\n\n");

        // 2. Rich Article & Text Editor
        sb.append("### ").append(LocaleController.getString("HudChangelogV101SecEditor", R.string.HudChangelogV101SecEditor)).append("\n\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV101Edit1", R.string.HudChangelogV101Edit1)).append("\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV101Edit2", R.string.HudChangelogV101Edit2)).append("\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV101Edit3", R.string.HudChangelogV101Edit3)).append("\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV101Edit4", R.string.HudChangelogV101Edit4)).append("\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV101Edit5", R.string.HudChangelogV101Edit5)).append("\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV101Edit6", R.string.HudChangelogV101Edit6)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV101Edit7", R.string.HudChangelogV101Edit7)).append("\n\n");

        // 3. Welcome, Gifts & Chats
        sb.append("### ").append(LocaleController.getString("HudChangelogV101SecChats", R.string.HudChangelogV101SecChats)).append("\n\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV101Chat1", R.string.HudChangelogV101Chat1)).append("\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV101Chat2", R.string.HudChangelogV101Chat2)).append("\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV101Chat3", R.string.HudChangelogV101Chat3)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV101Chat4", R.string.HudChangelogV101Chat4)).append("\n\n");

        // 4. Performance & System
        sb.append("### ").append(LocaleController.getString("HudChangelogV101SecPerformance", R.string.HudChangelogV101SecPerformance)).append("\n\n");
        sb.append("- ").append(tagBase).append(" ").append(LocaleController.getString("HudChangelogV101Perf1", R.string.HudChangelogV101Perf1)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV101Perf2", R.string.HudChangelogV101Perf2)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV101Perf3", R.string.HudChangelogV101Perf3)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV101Perf4", R.string.HudChangelogV101Perf4)).append("\n");
        sb.append("- ").append(tagFix).append(" ").append(LocaleController.getString("HudChangelogV101Perf5", R.string.HudChangelogV101Perf5)).append("\n");
        sb.append("- ").append(tagFix).append(" ").append(LocaleController.getString("HudChangelogV101Perf6", R.string.HudChangelogV101Perf6)).append("\n\n");

        sb.append("---\n\n");

        // === RELEASE 2: HUDGRAM v1.0.0 (Launch Version Comprehensive) ===
        sb.append("## ").append(LocaleController.getString("HudChangelogV100Header", R.string.HudChangelogV100Header)).append("\n\n");

        // 1. Appearance
        sb.append("### ").append(LocaleController.getString("HudChangelogV100SecAppearance", R.string.HudChangelogV100SecAppearance)).append("\n\n");
        sb.append("- ").append(tagExclusive).append(" ").append(LocaleController.getString("HudChangelogV100App1", R.string.HudChangelogV100App1)).append("\n");
        sb.append("- ").append(tagExclusive).append(" ").append(LocaleController.getString("HudChangelogV100App2", R.string.HudChangelogV100App2)).append("\n\n");

        // 2. Main Screen
        sb.append("### ").append(LocaleController.getString("HudChangelogV100SecMain", R.string.HudChangelogV100SecMain)).append("\n\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV100Main1", R.string.HudChangelogV100Main1)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV100Main2", R.string.HudChangelogV100Main2)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV100Main3", R.string.HudChangelogV100Main3)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV100Main4", R.string.HudChangelogV100Main4)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV100Main5", R.string.HudChangelogV100Main5)).append("\n\n");

        // 3. Chat Settings & Tools
        sb.append("### ").append(LocaleController.getString("HudChangelogV100SecChat", R.string.HudChangelogV100SecChat)).append("\n\n");
        sb.append("- ").append(tagExclusive).append(" ").append(LocaleController.getString("HudChangelogV100Chat1", R.string.HudChangelogV100Chat1)).append("\n");
        sb.append("- ").append(tagExclusive).append(" ").append(LocaleController.getString("HudChangelogV100Chat2", R.string.HudChangelogV100Chat2)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV100Chat3", R.string.HudChangelogV100Chat3)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV100Chat4", R.string.HudChangelogV100Chat4)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV100Chat5", R.string.HudChangelogV100Chat5)).append("\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV100Chat6", R.string.HudChangelogV100Chat6)).append("\n\n");

        // 4. Instant Translation
        sb.append("### ").append(LocaleController.getString("HudChangelogV100SecTrans", R.string.HudChangelogV100SecTrans)).append("\n\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV100Trans1", R.string.HudChangelogV100Trans1)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV100Trans2", R.string.HudChangelogV100Trans2)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV100Trans3", R.string.HudChangelogV100Trans3)).append("\n\n");

        // 5. Auto-Reply & Scheduled Messages
        sb.append("### ").append(LocaleController.getString("HudChangelogV100SecAuto", R.string.HudChangelogV100SecAuto)).append("\n\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV100Auto1", R.string.HudChangelogV100Auto1)).append("\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV100Auto2", R.string.HudChangelogV100Auto2)).append("\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV100Auto3", R.string.HudChangelogV100Auto3)).append("\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV100Auto4", R.string.HudChangelogV100Auto4)).append("\n\n");

        // 6. Notifications, Network & Backup
        sb.append("### ").append(LocaleController.getString("HudChangelogV100SecOther", R.string.HudChangelogV100SecOther)).append("\n\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV100Oth1", R.string.HudChangelogV100Oth1)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV100Oth2", R.string.HudChangelogV100Oth2)).append("\n");
        sb.append("- ").append(tagImprove).append(" ").append(LocaleController.getString("HudChangelogV100Oth3", R.string.HudChangelogV100Oth3)).append("\n");
        sb.append("- ").append(tagAdd).append(" ").append(LocaleController.getString("HudChangelogV100Oth4", R.string.HudChangelogV100Oth4)).append("\n");

        return sb.toString();
    }

    private static class ChangelogBadgeSpan extends ReplacementSpan {
        private final String text;
        private final int bgColor;
        private final int textColor;
        private final TextPaint badgeTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        private final Paint badgeBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float measuredTextWidth = -1;

        public ChangelogBadgeSpan(String text, int bgColor, int textColor) {
            this.text = text;
            this.bgColor = bgColor;
            this.textColor = textColor;
            badgeTextPaint.setTypeface(AndroidUtilities.bold());
            badgeTextPaint.setTextSize(AndroidUtilities.dp(11));
            badgeBgPaint.setStyle(Paint.Style.FILL);
        }

        private void ensureMeasure() {
            if (measuredTextWidth < 0) {
                measuredTextWidth = badgeTextPaint.measureText(text);
            }
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence charSequence, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            ensureMeasure();
            return (int) (measuredTextWidth + AndroidUtilities.dp(16) + AndroidUtilities.dp(6));
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence charSequence, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            ensureMeasure();
            badgeBgPaint.setColor(bgColor);
            badgeTextPaint.setColor(textColor);

            float paddingH = AndroidUtilities.dp(8);
            float badgeWidth = measuredTextWidth + paddingH * 2;
            float badgeHeight = AndroidUtilities.dp(19);

            float badgeTop = y - AndroidUtilities.dp(13.5f);
            float badgeBottom = badgeTop + badgeHeight;

            AndroidUtilities.rectTmp.set(x, badgeTop, x + badgeWidth, badgeBottom);
            float cornerRadius = AndroidUtilities.dp(5);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, cornerRadius, cornerRadius, badgeBgPaint);

            Paint.FontMetricsInt fm = badgeTextPaint.getFontMetricsInt();
            float textY = badgeTop + (badgeHeight - (fm.descent - fm.ascent)) / 2f - fm.ascent;
            canvas.drawText(text, x + paddingH, textY, badgeTextPaint);
        }
    }
}

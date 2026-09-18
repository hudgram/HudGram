# 07 - القوائم و Cells (RecyclerListView & Cells)

## الكلاسات الأساسية

| الكلاس | الموقع | الدور |
|------|------|------|
| `RecyclerListView` | `ui/Components/` | قائمة محسّنة (مثل `RecyclerView` لكن مخصصة) |
| `RecyclerListView.SelectionAdapter` | داخلي | أب الـ Adapters |
| `BaseCell` | `ui/Cells/` | أب كل خلايا القائمة (item views) |
| `LayoutHelper` | `ui/Components/` | منشئ `LayoutParams` |

---

## لماذا `RecyclerListView` بدلاً من `RecyclerView`؟

- مدمج مع `FastScroll` (للقوائم الطويلة)
- مدمج مع `Selector` (highlight عند الضغط)
- مدمج مع `Sections` (headers + items)
- مدمج مع `EmptyView` (view فارغ)
- مدمج مع `OnItemClickListener` (لا حاجة لـ `onClick` على كل cell)
- RTL aware تلقائياً

---

## إنشاء قائمة بسيطة

### 1) Adapter

```java
public class MyAdapter extends RecyclerListView.SelectionAdapter {

    private final List<MyItem> items = new ArrayList<>();

    public void setData(List<MyItem> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return true;  // هل يقبل الضغط
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = new TextCell(parent.getContext());
        view.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        return new RecyclerListView.Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextCell cell = (TextCell) holder.itemView;
        MyItem item = items.get(position);
        cell.setText(item.title, item.subtitle != null);  // text + value
    }
}
```

### 2) استخدام في Fragment

```java
private RecyclerListView listView;
private MyAdapter adapter;

@Override
public View createView(Context context) {
    fragmentView = new FrameLayout(context);

    listView = new RecyclerListView(context);
    listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    listView.setAdapter(adapter = new MyAdapter());

    listView.setOnItemClickListener((view, position) -> {
        MyItem item = adapter.getItem(position);
        // open chat, etc.
    });

    listView.setOnItemLongClickListener((view, position) -> {
        // long click
        return true;
    });

    ((FrameLayout) fragmentView).addView(listView,
        LayoutHelper.createFrame(MATCH_PARENT, MATCH_PARENT));
    return fragmentView;
}
```

### 3) إعدادات إضافية

```java
// empty view
listView.setEmptyView(emptyView);

// fast scroll
adapter = new MyAdapter() {
    @Override
    public String getLetter(int position) { /* first letter for fast scroll */ return "A"; }
    @Override
    public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) { ... }
};

// sections (header + items)
adapter = new RecyclerListView.SectionsAdapter() { ... };
```

---

## Cells الجاهزة (140+ cell)

### `TextCell` — الأكثر استخداماً
```java
// Constructor
TextCell cell = new TextCell(context);
cell.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, dp(48)));

// Without subtitle
cell.setText("Title", false);

// With subtitle
cell.setText("Title", "Subtitle text", true);

// With right icon
cell.setTextAndIcon("Title", R.drawable.ic_settings);

// With value (text on the right)
cell.setTextAndValue("Title", "Value", false);

// With right checkmark
cell.setChecked(true);

// Padding
cell.setPadding(dp(16), 0, dp(16), 0);
```

### `TextSettingsCell` — للإعدادات (يستخدم في ProfileActivity)
```java
TextSettingsCell cell = new TextSettingsCell(context);
cell.setTextAndValue("Language", "English", false);
cell.setTextAndCheck("Notifications", true, false);
cell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
cell.setNeedDivider(true);
```

### `TextCheckCell` — Switch
```java
TextCheckCell cell = new TextCheckCell(context);
cell.setTextAndCheck("Enable notifications", true, false);

// عند التغيير
cell.setOnCheckedChangeListener((v, isChecked) -> { ... });
```

### `TextCheckCell2` — CheckBox (دائري)
```java
TextCheckCell2 cell = new TextCheckCell2(context);
cell.setTextAndCheck("Option", true, true);
```

### `TextRadioCell` — Radio button
```java
TextRadioCell cell = new TextRadioCell(context);
cell.setTextAndValue("Option A", isSelected, true);
```

### `TextInfoCell` — فقرة نصية
```java
TextInfoCell cell = new TextInfoCell(context);
cell.setText(LocaleController.getString(R.string.PrivacyInfoText));
```

### `HeaderCell` — عنوان قسم
```java
HeaderCell cell = new HeaderCell(context);
cell.setText(LocaleController.getString(R.string.SectionTitle));
```

### `ShadowSectionCell` — فاصل مع ظل
```java
ShadowSectionCell cell = new ShadowSectionCell(context);
cell.setBottomBackground(Theme.getColor(Theme.key_windowBackgroundWhite));
```

### `GraySectionCell` — فاصل رمادي
```java
GraySectionCell cell = new GraySectionCell(context);
cell.setText(LocaleCell.getString(R.string.SubSection));
```

### `UserCell` — صف مستخدم
```java
UserCell cell = new UserCell(context, 4, false, false);
cell.setData(user, name, status, false, false, 0);
cell.setNameColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
```

### `DialogCell` — صف محادثة (للـ DialogsActivity)
```java
DialogCell cell = new DialogCell(context, false);
cell.setDialog(dialogObject, messagesController, true, false, true);
```

---

## بناء Cell مخصص

إذا لم تجد cell جاهز، ابنِ واحد. الـ `BaseCell` يوفر:
- قياس افتراضي
- خلفية `windowBackgroundWhite`
- إدارة `setTag` للـ IDs

```java
public class MyCustomCell extends BaseCell {
    private final TextView titleView;
    private final TextView subtitleView;
    private final ImageView iconView;

    public MyCustomCell(Context context) {
        super(context);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        titleView = new TextView(context);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleView.setTypeface(AndroidUtilities.bold());
        titleView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        addView(titleView, LayoutHelper.createFrame(MATCH_PARENT, WRAP_CONTENT,
            Gravity.TOP | (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT),
            16, 12, 16, 0));

        subtitleView = new TextView(context);
        subtitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        subtitleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        subtitleView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        addView(subtitleView, LayoutHelper.createFrame(MATCH_PARENT, WRAP_CONTENT,
            Gravity.TOP | (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT),
            16, 36, 16, 12));
    }

    public void setData(String title, String subtitle) {
        titleView.setText(title);
        subtitleView.setText(subtitle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(dp(64), MeasureSpec.EXACTLY));
    }
}
```

### استخدام BaseCell
```java
public abstract class BaseCell extends FrameLayout {
    public BaseCell(Context context) {
        super(context);
    }

    public BaseCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onConfigChanged() {  // override for theme changes
    }
}
```

---

## LayoutParams — `LayoutHelper`

```java
import org.telegram.ui.Components.LayoutHelper;

// Constants
LayoutHelper.MATCH_PARENT = -1
LayoutHelper.WRAP_CONTENT = -2

// FrameLayout
LayoutHelper.createFrame(int width, int height);
LayoutHelper.createFrame(int width, int height, int gravity);
LayoutHelper.createFrame(int width, int height, int gravity, float left, float top, float right, float bottom);

// RelativeLayout
LayoutHelper.createRelative(int width, int height, int left, int top, int right, int bottom);
LayoutHelper.createRelative(int width, int height, int alignParent);

// LinearLayout
LayoutHelper.createLinear(int width, int height, float weight, int gravity, int left, int top, int right, int bottom);
LayoutHelper.createLinear(int width, int height, int gravity, int left, int top, int right, int bottom);

// ScrollView
LayoutHelper.createScroll(int width, int height, int gravity);

// RTL aware
LayoutHelper.getAbsoluteGravityStart();  // returns Gravity.LEFT in LTR, RIGHT in RTL
LayoutHelper.getAbsoluteGravityEnd();
```

### Gravity
```java
// Compose
Gravity.TOP | Gravity.LEFT
Gravity.CENTER
Gravity.CENTER_VERTICAL | Gravity.LEFT

// RTL safe
Gravity.TOP | LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT
// أو
Gravity.TOP | LayoutHelper.getAbsoluteGravityStart()
```

---

## إنشاء Adapter متقدم

### مع multi-view types
```java
public class MultiTypeAdapter extends RecyclerListView.SelectionAdapter {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_FOOTER = 2;

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;
        if (position == getItemCount() - 1) return TYPE_FOOTER;
        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new RecyclerListView.Holder(new HeaderCell(parent.getContext()));
            case TYPE_FOOTER:
                return new RecyclerListView.Holder(new TextInfoCell(parent.getContext()));
            case TYPE_ITEM:
            default:
                return new RecyclerListView.Holder(new TextCell(parent.getContext()));
        }
    }
}
```

### مع Sections
```java
public class MySectionsAdapter extends RecyclerListView.SectionsAdapter {
    @Override
    public int getCountForSection(int section) { return 5; }
    @Override
    public int getSectionCount() { return 3; }
    @Override
    public Object getItem(int section, int position) { return data.get(position); }
    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) { ... }
    @Override
    public int getItemViewType(int section, int position) { return 0; }
    @Override
    public View getView(int section, int position, View convertView, ViewGroup parent) { ... }
}
```

### مع Headers (للقوائم الرأسية)
```java
listView.addHeaderView(headerView, 0, dp(40), false);
listView.addHeaderView(sectionView, 0, dp(8), false);
```

---

## تحديث Adapter

```java
// الكل — بطيء
adapter.notifyDataSetChanged();

// عنصر واحد
adapter.notifyItemChanged(position);

// نطاق
adapter.notifyItemRangeChanged(start, count);

// إدراج/حذف
adapter.notifyItemInserted(position);
adapter.notifyItemRemoved(position);

// نقل
adapter.notifyItemMoved(from, to);
```

---

## Empty View

```java
View emptyView = new View(context);
// ... customize
listView.setEmptyView(emptyView);
listView.setEmptyViewAnimationType(RecyclerListView.EMPTY_VIEW_ANIMATION_TYPE_ALPHA);
// أو EMPTY_VIEW_ANIMATION_TYPE_ALPHA_SCALE
```

---

## FastScroll (للقوائم الطويلة)

```java
public class MyAdapter extends RecyclerListView.FastScrollAdapter {
    @Override
    public String getLetter(int position) {
        if (position < 0 || position >= items.size()) return "";
        return String.valueOf(items.get(position).title.charAt(0)).toUpperCase();
    }

    @Override
    public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
        position[0] = (int) (progress * items.size());
    }

    @Override
    public float getScrollProgress(RecyclerListView listView) {
        LinearLayoutManager lm = (LinearLayoutManager) listView.getLayoutManager();
        int first = lm.findFirstVisibleItemPosition();
        int total = getItemCount();
        return total == 0 ? 0 : first / (float) Math.max(1, total - 1);
    }
}
```

---

## Click & Long Click

```java
// Single click
listView.setOnItemClickListener((view, position, x, y) -> {
    // open detail
});

// Long click
listView.setOnItemLongClickListener((view, position) -> {
    // show context menu
    return true;
});

// Extended listener
listView.setOnItemClickListenerExtended((view, position, x, y) -> { ... });

// Touch interception
listView.setOnInterceptTouchListener(event -> {
    if (someCondition) return true;  // consume event
    return false;
});
```

---

## Selection Mode (التحديد)

```java
listView.setSelectable(true);
listView.setOnItemClickListener((view, position) -> {
    if (listView.isSelected(position)) {
        listView.deselect(position);
    } else {
        listView.select(position);
    }
});

int selectedCount = listView.getSelectedItemCount();
SparseBooleanArray selected = listView.getSelectedItems();
```

---

## نمط قائمة الإعدادات الكامل

```java
// SettingsActivity يستخدم هذا النمط
public class SettingsAdapter extends RecyclerListView.SelectionAdapter {

    public static final int ROW_TYPE_HEADER = 0;
    public static final int ROW_TYPE_TEXT = 1;
    public static final int ROW_TYPE_CHECK = 2;
    public static final int ROW_TYPE_SHADOW = 3;

    private List<Row> rows = new ArrayList<>();
    private Context context;

    public void setData(List<Row> rows) {
        this.rows = rows;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).type;
    }

    @Override
    public int getItemCount() { return rows.size(); }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case ROW_TYPE_HEADER:
                v = new HeaderCell(parent.getContext()); break;
            case ROW_TYPE_CHECK:
                v = new TextCheckCell(parent.getContext()); break;
            case ROW_TYPE_SHADOW:
                v = new ShadowSectionCell(parent.getContext()); break;
            case ROW_TYPE_TEXT:
            default:
                v = new TextSettingsCell(parent.getContext()); break;
        }
        return new RecyclerListView.Holder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Row row = rows.get(position);
        switch (row.type) {
            case ROW_TYPE_HEADER:
                ((HeaderCell) holder.itemView).setText(row.title); break;
            case ROW_TYPE_CHECK:
                ((TextCheckCell) holder.itemView).setTextAndCheck(row.title, row.checked, false); break;
            case ROW_TYPE_TEXT:
                ((TextSettingsCell) holder.itemView).setTextAndValue(row.title, row.value, row.needDivider); break;
        }
    }

    public static class Row {
        public int type;
        public String title;
        public String value;
        public boolean checked;
        public boolean needDivider;
    }
}
```

---

## نصائح

1. **دائماً** ضع `Theme.getColor(Theme.key_windowBackgroundWhite)` كخلفية افتراضية للـ cell.
2. **استخدم `setIsEnabled`** (الـ `isEnabled()` override) لتعطيل التحديد.
3. **للنصوص الطويلة**: استخدم `TextView` مع `setMaxLines` و `setEllipsize`.
4. **للـ RTL**: اقلب الـ gravity بناءً على `LocaleController.isRTL`.
5. **للـ dividers**: ضع في الـ Row flag `needDivider` ومرّر للـ Cell.
6. **مستوى التحديث**: استخدم `notifyItemChanged(pos)` بدلاً من الكل عند الإمكان.
7. **لا تنسَ `viewType`**: عند استخدام cells متعددة، عرّف `getItemViewType`.
8. **في الـ ChatActivity**: الـ Adapter كبير جداً (45k سطر) — لا تنسخ نمطه. ابدأ بسيط.

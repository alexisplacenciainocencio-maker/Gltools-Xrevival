package com.nightmare.gltools.ui;

import android.app.Activity;
import android.graphics.Color;
import androidx.annotation.ColorInt;

public class iOSColorPalette {

    // iOS Semantic Colors — Dark Mode
    public static final int SYSTEM_BACKGROUND = Color.parseColor("#000000");
    public static final int SECONDARY_SYSTEM_BACKGROUND = Color.parseColor("#1C1C1E");
    public static final int TERTIARY_SYSTEM_BACKGROUND = Color.parseColor("#2C2C2E");
    public static final int SYSTEM_GROUPED_BACKGROUND = Color.parseColor("#000000");
    public static final int SECONDARY_SYSTEM_GROUPED_BACKGROUND = Color.parseColor("#1C1C1E");

    public static final int LABEL = Color.parseColor("#FFFFFF");
    public static final int SECONDARY_LABEL = Color.parseColor("#EBEBF599"); // 60% white
    public static final int TERTIARY_LABEL = Color.parseColor("#EBEBF54D"); // 30% white
    public static final int PLACEHOLDER_TEXT = Color.parseColor("#EBEBF54D");

    public static final int SYSTEM_BLUE = Color.parseColor("#0A84FF");
    public static final int SYSTEM_GREEN = Color.parseColor("#30D158");
    public static final int SYSTEM_RED = Color.parseColor("#FF453A");
    public static final int SYSTEM_ORANGE = Color.parseColor("#FF9F0A");
    public static final int SYSTEM_YELLOW = Color.parseColor("#FFD60A");
    public static final int SYSTEM_PURPLE = Color.parseColor("#BF5AF2");
    public static final int SYSTEM_TEAL = Color.parseColor("#64D2FF");
    public static final int SYSTEM_INDIGO = Color.parseColor("#5E5CE6");
    public static final int SYSTEM_PINK = Color.parseColor("#FF375F");
    public static final int SYSTEM_GRAY = Color.parseColor("#8E8E93");
    public static final int SYSTEM_GRAY2 = Color.parseColor("#636366");
    public static final int SYSTEM_GRAY3 = Color.parseColor("#48484A");
    public static final int SYSTEM_GRAY4 = Color.parseColor("#3A3A3C");
    public static final int SYSTEM_GRAY5 = Color.parseColor("#2C2C2E");
    public static final int SYSTEM_GRAY6 = Color.parseColor("#1C1C1E");

    public static final int SEPARATOR = Color.parseColor("#54545866");
    public static final int OPAQUE_SEPARATOR = Color.parseColor("#38383A");

    public static final int TINT = SYSTEM_BLUE;

    @ColorInt public static int systemBackground() { return SYSTEM_BACKGROUND; }
    @ColorInt public static int secondarySystemBackground() { return SECONDARY_SYSTEM_BACKGROUND; }
    @ColorInt public static int tertiarySystemBackground() { return TERTIARY_SYSTEM_BACKGROUND; }
    @ColorInt public static int label() { return LABEL; }
    @ColorInt public static int secondaryLabel() { return SECONDARY_LABEL; }
    @ColorInt public static int tertiaryLabel() { return TERTIARY_LABEL; }
    @ColorInt public static int tint() { return TINT; }
    @ColorInt public static int systemBlue() { return SYSTEM_BLUE; }
    @ColorInt public static int systemGreen() { return SYSTEM_GREEN; }
    @ColorInt public static int systemRed() { return SYSTEM_RED; }
    @ColorInt public static int systemOrange() { return SYSTEM_ORANGE; }
    @ColorInt public static int systemGray() { return SYSTEM_GRAY; }
    @ColorInt public static int separator() { return SEPARATOR; }

    public static void apply(Activity activity) {
        activity.getWindow().getDecorView().setBackgroundColor(SYSTEM_BACKGROUND);
    }
}

package com.gl.revival

import android.content.Context
import android.content.res.Configuration

object iOSColorPalette {
    private fun isDark(ctx: Context): Boolean =
        (ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    @JvmStatic
    fun systemBackground(ctx: Context): Int = if (isDark(ctx)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()

    @JvmStatic
    fun secondarySystemBackground(ctx: Context): Int = if (isDark(ctx)) 0xFF1C1C1E.toInt() else 0xFFF2F2F7.toInt()

    @JvmStatic
    fun tertiarySystemBackground(ctx: Context): Int = if (isDark(ctx)) 0xFF2C2C2E.toInt() else 0xFFFFFFFF.toInt()

    @JvmStatic
    fun label(ctx: Context): Int = if (isDark(ctx)) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()

    @JvmStatic
    fun secondaryLabel(ctx: Context): Int = if (isDark(ctx)) 0x99EBEBF5.toInt() else 0x993C3C43.toInt()

    @JvmStatic
    fun tertiaryLabel(ctx: Context): Int = if (isDark(ctx)) 0x4DEBEBF5.toInt() else 0x4D3C3C43.toInt()

    @JvmStatic
    fun placeholderText(ctx: Context): Int = if (isDark(ctx)) 0x4DEBEBF5.toInt() else 0x4D3C3C43.toInt()

    @JvmStatic
    fun separator(ctx: Context): Int = if (isDark(ctx)) 0x99545458.toInt() else 0x4A3C3C43.toInt()

    @JvmStatic
    fun opaqueSeparator(ctx: Context): Int = if (isDark(ctx)) 0xFF38383A.toInt() else 0xFFC6C6C8.toInt()

    @JvmStatic
    fun link(ctx: Context): Int = if (isDark(ctx)) 0xFF0A84FF.toInt() else 0xFF007AFF.toInt()

    @JvmStatic
    fun systemBlue(ctx: Context): Int = if (isDark(ctx)) 0xFF0A84FF.toInt() else 0xFF007AFF.toInt()

    @JvmStatic
    fun systemBlueLow(ctx: Context): Int = if (isDark(ctx)) 0x1A0A84FF else 0x1A007AFF

    @JvmStatic
    fun systemGreen(ctx: Context): Int = if (isDark(ctx)) 0xFF30D158.toInt() else 0xFF34C759.toInt()

    @JvmStatic
    fun systemIndigo(ctx: Context): Int = if (isDark(ctx)) 0xFF5E5CE6.toInt() else 0xFF5856D6.toInt()

    @JvmStatic
    fun systemOrange(ctx: Context): Int = if (isDark(ctx)) 0xFFFF9F0A.toInt() else 0xFFFF9500.toInt()

    @JvmStatic
    fun systemPink(ctx: Context): Int = if (isDark(ctx)) 0xFFFF375F.toInt() else 0xFFFF2D55.toInt()

    @JvmStatic
    fun systemPurple(ctx: Context): Int = if (isDark(ctx)) 0xFFBF5AF2.toInt() else 0xFFAF52DE.toInt()

    @JvmStatic
    fun systemRed(ctx: Context): Int = if (isDark(ctx)) 0xFFFF453A.toInt() else 0xFFFF3B30.toInt()

    @JvmStatic
    fun systemTeal(ctx: Context): Int = if (isDark(ctx)) 0xFF64D2FF.toInt() else 0xFF5AC8FA.toInt()

    @JvmStatic
    fun systemYellow(ctx: Context): Int = if (isDark(ctx)) 0xFFFFD60A.toInt() else 0xFFFFCC00.toInt()

    @JvmStatic
    fun systemGray(ctx: Context): Int = 0xFF8E8E93.toInt()

    @JvmStatic
    fun systemGray2(ctx: Context): Int = if (isDark(ctx)) 0xFF636366.toInt() else 0xFFAEAEB2.toInt()

    @JvmStatic
    fun systemGray3(ctx: Context): Int = if (isDark(ctx)) 0xFF48484A.toInt() else 0xFFC7C7CC.toInt()

    @JvmStatic
    fun systemGray4(ctx: Context): Int = if (isDark(ctx)) 0xFF3A3A3C.toInt() else 0xFFD1D1D6.toInt()

    @JvmStatic
    fun systemGray5(ctx: Context): Int = if (isDark(ctx)) 0xFF2C2C2E.toInt() else 0xFFE5E5EA.toInt()

    @JvmStatic
    fun systemGray6(ctx: Context): Int = if (isDark(ctx)) 0xFF1C1C1E.toInt() else 0xFFF2F2F7.toInt()

    @JvmStatic
    fun glassOverlay(ctx: Context): Int = if (isDark(ctx)) 0x801C1C1E.toInt() else 0x80FFFFFF.toInt()

    @JvmStatic
    fun glassBorder(ctx: Context): Int = 0x40FFFFFF

    @JvmStatic
    fun glassShadow(ctx: Context): Int = 0x1A000000
}

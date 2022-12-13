package com.cncoderx.flatdesign;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

/**
 * @author Luffy
 */
public final class FlatWidgetCompat {

    @NonNull
    private static FlatWidgetDrawable getFlatWidgetDrawable(@NonNull View view) {
        Drawable drawable = view.getBackground();
        if (!(drawable instanceof FlatWidgetDrawable)) {
            throw new AssertionError("View background must be 'FlatWidgetDrawable'");
        }
        return (FlatWidgetDrawable) drawable;
    }

    @Nullable
    public static ColorStateList getColor(@NonNull View view) {
        return getFlatWidgetDrawable(view).getColor();
    }

    public static void setColor(@NonNull View view, @ColorInt int color) {
        getFlatWidgetDrawable(view).setColor(color);
    }

    public static void setColor(@NonNull View view, @Nullable ColorStateList colorStateList) {
        getFlatWidgetDrawable(view).setColor(colorStateList);
    }

    public static float getCornerRadius(@NonNull View view) {
        return getFlatWidgetDrawable(view).getCornerRadius();
    }

    public static void setCornerRadius(@NonNull View view, float cornerRadius) {
        getFlatWidgetDrawable(view).setCornerRadius(cornerRadius);
    }

    @Nullable
    public static float[] getCornerRadii(@NonNull View view) {
        return getFlatWidgetDrawable(view).getCornerRadii();
    }

    public static void setCornerRadii(@NonNull View view, @Nullable float[] cornerRadii) {
        getFlatWidgetDrawable(view).setCornerRadii(cornerRadii);
    }

    public static void setStroke(@NonNull View view, int width, @ColorInt int color) {
        getFlatWidgetDrawable(view).setStroke(width, color);
    }

    public static void setStroke(@NonNull View view, int width, @Nullable ColorStateList colorStateList) {
        getFlatWidgetDrawable(view).setStroke(width, colorStateList);
    }

    public static void setShadow(@NonNull View view, @ColorInt int color, int radius, int dx, int dy) {
        getFlatWidgetDrawable(view).setShadow(color, radius, dx, dy);
    }

    public static void setShadow(@NonNull View view, @Nullable ColorStateList colorStateList, int radius, int dx, int dy) {
        getFlatWidgetDrawable(view).setShadow(colorStateList, radius, dx, dy);
    }

    public static void setLinearGradient(@NonNull View view, int[] colors, float angle) {
        getFlatWidgetDrawable(view).setLinearGradient(colors, angle);
    }

    public static void setPadding(@NonNull View view, @Px int left, @Px int top, @Px int right, @Px int bottom) {
        getFlatWidgetDrawable(view).setPadding(left, top, right, bottom);
    }
}

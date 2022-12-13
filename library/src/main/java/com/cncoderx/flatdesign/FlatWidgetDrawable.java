package com.cncoderx.flatdesign;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

/**
 * @author Luffy
 */
public class FlatWidgetDrawable extends Drawable {
    private final FlatDrawableState mDrawableState;

    private final RectF mRect = new RectF();
    private final Path mPath = new Path();
    private final PaintCompat mFillPaint = new PaintCompat(Paint.ANTI_ALIAS_FLAG);
    private Paint mStrokePaint;
    private ColorFilter mColorFilter;
    private int mAlpha = 0xFF;
    private boolean mPathIsDirty;
    private boolean mGradientIsDirty;

    @Nullable
    public static FlatWidgetDrawable parse(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlatWidgetDrawable);
        if (a.getIndexCount() == 0) {
            a.recycle();
            return null;
        }

        ColorStateList color = a.getColorStateList(R.styleable.FlatWidgetDrawable_flatColor);
        int cornerRadius = a.getDimensionPixelSize(R.styleable.FlatWidgetDrawable_flatCornerRadius, 0);
        int cornerTopLeftRadius = a.getDimensionPixelSize(R.styleable.FlatWidgetDrawable_flatCornerTopLeftRadius, cornerRadius);
        int cornerTopRightRadius = a.getDimensionPixelSize(R.styleable.FlatWidgetDrawable_flatCornerTopRightRadius, cornerRadius);
        int cornerBottomLeftRadius = a.getDimensionPixelSize(R.styleable.FlatWidgetDrawable_flatCornerBottomLeftRadius, cornerRadius);
        int cornerBottomRightRadius = a.getDimensionPixelSize(R.styleable.FlatWidgetDrawable_flatCornerBottomRightRadius, cornerRadius);
        int strokeWidth = a.getDimensionPixelOffset(R.styleable.FlatWidgetDrawable_flatStrokeWidth, 0);
        ColorStateList strokeColor = a.getColorStateList(R.styleable.FlatWidgetDrawable_flatStrokeColor);
        ColorStateList shadowColor = a.getColorStateList(R.styleable.FlatWidgetDrawable_flatShadowColor);
        int shadowRadius = a.getDimensionPixelOffset(R.styleable.FlatWidgetDrawable_flatShadowRadius, 0);
        int shadowDx = a.getDimensionPixelOffset(R.styleable.FlatWidgetDrawable_flatShadowDx, 0);
        int shadowDy = a.getDimensionPixelOffset(R.styleable.FlatWidgetDrawable_flatShadowDy, 0);
        int contentPadding = a.getDimensionPixelOffset(R.styleable.FlatWidgetDrawable_flatContentPadding, 0);
        int contentPaddingHorizontal = a.getDimensionPixelOffset(R.styleable.FlatWidgetDrawable_flatContentPaddingHorizontal, contentPadding);
        int contentPaddingVertical = a.getDimensionPixelOffset(R.styleable.FlatWidgetDrawable_flatContentPaddingVertical, contentPadding);
        int contentPaddingLeft = a.getDimensionPixelOffset(R.styleable.FlatWidgetDrawable_flatContentPaddingLeft, contentPaddingHorizontal);
        int contentPaddingRight = a.getDimensionPixelOffset(R.styleable.FlatWidgetDrawable_flatContentPaddingRight, contentPaddingHorizontal);
        int contentPaddingTop = a.getDimensionPixelOffset(R.styleable.FlatWidgetDrawable_flatContentPaddingTop, contentPaddingVertical);
        int contentPaddingBottom = a.getDimensionPixelOffset(R.styleable.FlatWidgetDrawable_flatContentPaddingBottom, contentPaddingVertical);
        ColorStateList gradientStartColor = a.getColorStateList(R.styleable.FlatWidgetDrawable_flatGradientStartColor);
        ColorStateList gradientCenterColor = a.getColorStateList(R.styleable.FlatWidgetDrawable_flatGradientCenterColor);
        ColorStateList gradientEndColor = a.getColorStateList(R.styleable.FlatWidgetDrawable_flatGradientEndColor);
        float gradientAngle = a.getFloat(R.styleable.FlatWidgetDrawable_flatGradientAngle, 0);
        a.recycle();

        FlatDrawableState ds = new FlatDrawableState();
        ds.mColor = color;

        if (cornerTopLeftRadius != cornerRadius || cornerTopRightRadius != cornerRadius || cornerBottomLeftRadius != cornerRadius || cornerBottomRightRadius != cornerRadius) {
            ds.mCornerRadiusArray = new float[]{
                    cornerTopLeftRadius, cornerTopLeftRadius,
                    cornerTopRightRadius, cornerTopRightRadius,
                    cornerBottomRightRadius, cornerBottomRightRadius,
                    cornerBottomLeftRadius, cornerBottomLeftRadius};
        }
        ds.mCornerRadius = cornerRadius;

        ds.mStrokeWidth = strokeWidth;
        ds.mStrokeColor = strokeColor;
        ds.mShadowColor = shadowColor;

        ds.mShadowRadius = shadowRadius;
        ds.mShadowDx = shadowDx;
        ds.mShadowDy = shadowDy;

        if (gradientStartColor != null && gradientEndColor != null) {
            if (gradientCenterColor != null) {
                ds.mGradientColors = new int[3];
                ds.mGradientColors[0] = gradientStartColor.getDefaultColor();
                ds.mGradientColors[1] = gradientCenterColor.getDefaultColor();
                ds.mGradientColors[2] = gradientEndColor.getDefaultColor();
            } else {
                ds.mGradientColors = new int[2];
                ds.mGradientColors[0] = gradientStartColor.getDefaultColor();
                ds.mGradientColors[1] = gradientEndColor.getDefaultColor();
            }
        }
        ds.mGradientOrientation = getGradientOrientation(gradientAngle);

        if (contentPaddingLeft != 0 || contentPaddingRight != 0 || contentPaddingTop != 0 || contentPaddingBottom != 0) {
            ds.mPadding = new Rect(contentPaddingLeft, contentPaddingTop, contentPaddingRight, contentPaddingBottom);
        }

        return new FlatWidgetDrawable(ds);
    }

    FlatWidgetDrawable(FlatDrawableState ds) {
        mDrawableState = ds;
        updateLocalState();
    }

    private void updateLocalState() {
        final FlatDrawableState ds = mDrawableState;

        if (ds.mColor != null) {
            final int[] state = getState();
            int color = ds.mColor.getColorForState(state, 0);
            mFillPaint.setColor(color);
        }

        if (ds.mShadowRadius > 0 && ds.mShadowColor != null) {
            final int[] state = getState();
            int shadowColor = ds.mShadowColor.getColorForState(state, 0);
            mFillPaint.setShadowLayer(ds.mShadowRadius, ds.mShadowDx, ds.mShadowDy, shadowColor);
        }

        if (ds.mStrokeWidth > 0) {
            mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mStrokePaint.setStyle(Paint.Style.STROKE);
            mStrokePaint.setStrokeWidth(ds.mStrokeWidth);
            if (ds.mStrokeColor != null) {
                final int[] state = getState();
                mStrokePaint.setColor(ds.mStrokeColor.getColorForState(state, 0));
            }
        }
    }

    @Override
    public boolean getPadding(@NonNull Rect padding) {
        final FlatDrawableState ds = mDrawableState;
        if (ds.mPadding != null) {
            padding.set(ds.mPadding);
        } else {
            padding.setEmpty();
        }
        if (ds.mShadowRadius > 0) {
            padding.offset(ds.mShadowRadius, ds.mShadowRadius);
        }
        return padding.left != 0 || padding.top != 0 || padding.right != 0 || padding.bottom != 0;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mPathIsDirty = true;
        mGradientIsDirty = true;
        invalidateSelf();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        boolean invalidateSelf = false;

        final FlatDrawableState ds = mDrawableState;
        final ColorStateList backgroundColors = ds.mColor;
        if (backgroundColors != null) {
            final int newColor = backgroundColors.getColorForState(state, 0);
            final int oldColor = mFillPaint.getColor();
            if (oldColor != newColor) {
                mFillPaint.setColor(newColor);
                invalidateSelf = true;
            }
        }

        final ColorStateList shadowColors = ds.mShadowColor;
        if (shadowColors != null) {
            final int newColor = shadowColors.getColorForState(state, 0);
            final int oldColor = mFillPaint.getShadowLayerColor();
            if (oldColor != newColor) {
                mFillPaint.setShadowLayer(
                        mFillPaint.getShadowLayerRadius(),
                        mFillPaint.getShadowLayerDx(),
                        mFillPaint.getShadowLayerDy(),
                        newColor);
                invalidateSelf = true;
            }
        }

        if (mStrokePaint != null) {
            final ColorStateList strokeColors = ds.mStrokeColor;
            if (strokeColors != null) {
                final int newColor = strokeColors.getColorForState(state, 0);
                final int oldColor = mStrokePaint.getColor();
                if (oldColor != newColor) {
                    mStrokePaint.setColor(newColor);
                    invalidateSelf = true;
                }
            }
        }

        if (invalidateSelf) {
            invalidateSelf();
            return true;
        }

        return false;
    }

    @Nullable
    @Override
    public ConstantState getConstantState() {
        return mDrawableState;
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        buildPathIfDirty();
        buildGradientIfDirty();

        int fillAlpha = modulateAlpha(mFillPaint.getAlpha());
        mFillPaint.setAlpha(fillAlpha);
        mFillPaint.setColorFilter(mColorFilter);
        canvas.drawPath(mPath, mFillPaint);

        if (mStrokePaint != null) {
            int strokeAlpha = modulateAlpha(mStrokePaint.getAlpha());
            mStrokePaint.setAlpha(strokeAlpha);
            mStrokePaint.setColorFilter(mColorFilter);
            canvas.drawPath(mPath, mStrokePaint);
        }
    }

    private void buildPathIfDirty() {
        final FlatDrawableState ds = mDrawableState;
        if (mPathIsDirty) {
            ensureValidRect();
            mPath.reset();

            if (ds.mStrokeWidth > 0) {
                float inset = ds.mStrokeWidth * 0.5f;
                mRect.inset(inset, inset);
            }

            if (!isEmptyFloatArray(ds.mCornerRadiusArray)) {
                mPath.addRoundRect(mRect, ds.mCornerRadiusArray, Path.Direction.CW);
            } else if (ds.mCornerRadius > 0) {
                mPath.addRoundRect(mRect, ds.mCornerRadius, ds.mCornerRadius, Path.Direction.CW);
            } else {
                mPath.addRect(mRect, Path.Direction.CW);
            }

            mPathIsDirty = false;
        }
    }

    private void buildGradientIfDirty() {
        final FlatDrawableState ds = mDrawableState;
        if (mGradientIsDirty) {
            if (ds.mGradientColors != null) {
                final RectF r = mRect;
                final float x0, x1, y0, y1;

                switch (ds.mGradientOrientation) {
                    case TOP_BOTTOM:
                        x0 = r.left;
                        y0 = r.top;
                        x1 = r.left;
                        y1 = r.bottom;
                        break;
                    case TR_BL:
                        x0 = r.right;
                        y0 = r.top;
                        x1 = r.left;
                        y1 = r.bottom;
                        break;
                    case RIGHT_LEFT:
                        x0 = r.right;
                        y0 = r.top;
                        x1 = r.left;
                        y1 = r.top;
                        break;
                    case BR_TL:
                        x0 = r.right;
                        y0 = r.bottom;
                        x1 = r.left;
                        y1 = r.top;
                        break;
                    case BOTTOM_TOP:
                        x0 = r.left;
                        y0 = r.bottom;
                        x1 = r.left;
                        y1 = r.top;
                        break;
                    case BL_TR:
                        x0 = r.left;
                        y0 = r.bottom;
                        x1 = r.right;
                        y1 = r.top;
                        break;
                    case LEFT_RIGHT:
                        x0 = r.left;
                        y0 = r.top;
                        x1 = r.right;
                        y1 = r.top;
                        break;
                    default:
                        x0 = r.left;
                        y0 = r.top;
                        x1 = r.right;
                        y1 = r.bottom;
                        break;
                }

                mFillPaint.setShader(new LinearGradient(x0, y0, x1, y1,
                        ds.mGradientColors, null, Shader.TileMode.CLAMP));

                if (ds.mColor == null) {
                    mFillPaint.setColor(Color.BLACK);
                }
            }
            mGradientIsDirty = false;
        }
    }

    private boolean ensureValidRect() {
        FlatDrawableState ds = mDrawableState;
        Rect bound = getBounds();
        mRect.set(bound);
        if (ds.mShadowRadius > 0) {
            mRect.inset(ds.mShadowRadius, ds.mShadowRadius);
        }
        return !mRect.isEmpty();
    }

    private boolean isEmptyFloatArray(float[] array) {
        if (array != null && array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != 0) return false;
            }
        }
        return true;
    }

    public ColorStateList getColor() {
        return mDrawableState.mColor;
    }

    public void setColor(@ColorInt int color) {
        setColor(ColorStateList.valueOf(color));
    }

    public void setColor(@Nullable ColorStateList colorStateList) {
        final FlatDrawableState ds = mDrawableState;
        ds.mColor = colorStateList;

        if (ds.mColor != null) {
            final int[] state = getState();
            mFillPaint.setColor(ds.mColor.getColorForState(state, 0));
        }

        invalidateSelf();
    }

    public float getCornerRadius() {
        return mDrawableState.mCornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        mDrawableState.mCornerRadius = cornerRadius;
        mPathIsDirty = true;
        invalidateSelf();
    }

    @Nullable
    public float[] getCornerRadii() {
        return mDrawableState.mCornerRadiusArray.clone();
    }

    public void setCornerRadii(@Nullable float[] cornerRadii) {
        mDrawableState.mCornerRadiusArray = cornerRadii;
        mPathIsDirty = true;
        invalidateSelf();
    }

    public void setStroke(int width, @ColorInt int color) {
        setStroke(width, ColorStateList.valueOf(color));
    }

    public void setStroke(int width, @Nullable ColorStateList colorStateList) {
        final FlatDrawableState ds = mDrawableState;
        ds.mStrokeWidth = width;
        ds.mStrokeColor = colorStateList;

        if (mStrokePaint == null)  {
            mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mStrokePaint.setStyle(Paint.Style.STROKE);
        }

        if (ds.mStrokeWidth > 0) {
            mStrokePaint.setStrokeWidth(ds.mStrokeWidth);
        }

        if (ds.mStrokeColor != null) {
            final int[] state = getState();
            mStrokePaint.setColor(ds.mStrokeColor.getColorForState(state, 0));
        }

        invalidateSelf();
    }

    public void setShadow(@ColorInt int color, int radius, int dx, int dy) {
        setShadow(ColorStateList.valueOf(color), radius, dx, dy);
    }

    public void setShadow(@Nullable ColorStateList colorStateList, int radius, int dx, int dy) {
        final FlatDrawableState ds = mDrawableState;
        ds.mShadowColor = colorStateList;
        ds.mShadowRadius = radius;
        ds.mShadowDx = dx;
        ds.mShadowDy = dy;

        if (ds.mShadowRadius > 0 && ds.mShadowColor != null) {
            final int[] state = getState();
            int shadowColor = ds.mShadowColor.getColorForState(state, 0);
            mFillPaint.setShadowLayer(ds.mShadowRadius, ds.mShadowDx, ds.mShadowDy, shadowColor);
        }

        mPathIsDirty = true;
        mGradientIsDirty = true;
        invalidateSelf();
    }

    public void setLinearGradient(int[] colors, float angle) {
        final FlatDrawableState ds = mDrawableState;
        ds.mGradientColors = colors;
        ds.mGradientOrientation = getGradientOrientation(angle);
        mGradientIsDirty = true;
        invalidateSelf();
    }

    public void setPadding(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        final FlatDrawableState ds = mDrawableState;
        if (ds.mPadding == null) {
            ds.mPadding = new Rect();
        }
        ds.mPadding.set(left, top, right, bottom);

        invalidateSelf();
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setAlpha(int alpha) {
        if (mAlpha != alpha) {
            mAlpha = alpha;
            invalidateSelf();
        }
    }

    @Nullable
    @Override
    public ColorFilter getColorFilter() {
        return mColorFilter;
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        if (mColorFilter != colorFilter) {
            mColorFilter = colorFilter;
            invalidateSelf();
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    private int modulateAlpha(int alpha) {
        int scale = mAlpha + (mAlpha >> 7);
        return alpha * scale >> 8;
    }

    private static GradientDrawable.Orientation getGradientOrientation(float angle) {
        int index = Math.round((((angle % 360) + 360) % 360) / 45);

        GradientDrawable.Orientation orientation;
        switch (index) {
            case 0:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case 1:
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            case 2:
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case 3:
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case 4:
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case 5:
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
            case 6:
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
            case 7:
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
            default:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
        }
        return orientation;
    }

    private static class FlatDrawableState extends ConstantState {
        private float mCornerRadius;
        private float[] mCornerRadiusArray;
        private int mStrokeWidth;
        private ColorStateList mStrokeColor;

        private ColorStateList mShadowColor;
        private int mShadowRadius;
        private int mShadowDx;
        private int mShadowDy;

        private ColorStateList mColor;
        private Rect mPadding;

        private int[] mGradientColors;
        private GradientDrawable.Orientation mGradientOrientation;

        @NonNull
        @Override
        public Drawable newDrawable() {
            return new FlatWidgetDrawable(this);
        }

        @Override
        public int getChangingConfigurations() {
            return 0;
        }
    }

    private static class PaintCompat extends Paint {
        private Object[] shadowObjects;

        public PaintCompat() {
        }

        public PaintCompat(int flags) {
            super(flags);
        }

        public PaintCompat(Paint paint) {
            super(paint);
        }

        @Override
        public void setShadowLayer(float radius, float dx, float dy, long shadowColor) {
            super.setShadowLayer(radius, dx, dy, shadowColor);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                shadowObjects = new Object[4];
                shadowObjects[0] = radius;
                shadowObjects[1] = dx;
                shadowObjects[2] = dy;
                shadowObjects[3] = shadowColor;
            }
        }

        public float getShadowLayerRadius() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return super.getShadowLayerRadius();
            } else {
                return shadowObjects != null ? (float) shadowObjects[0] : 0;
            }
        }

        public float getShadowLayerDx() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return super.getShadowLayerDx();
            } else {
                return shadowObjects != null ? (float) shadowObjects[1] : 0;
            }
        }

        public float getShadowLayerDy() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return super.getShadowLayerDy();
            } else {
                return shadowObjects != null ? (float) shadowObjects[2] : 0;
            }
        }

        public int getShadowLayerColor() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return super.getShadowLayerColor();
            } else {
                return shadowObjects != null ? Color.toArgb((long) shadowObjects[3]) : 0;
            }
        }

        public long getShadowLayerColorLong() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return super.getShadowLayerColorLong();
            } else {
                return shadowObjects != null ? (long) shadowObjects[3] : 0;
            }
        }
    }
}

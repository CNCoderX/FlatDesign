package com.cncoderx.flatdesign;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatViewInflater;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.AppCompatToggleButton;

import java.lang.reflect.Method;

/**
 * @author Luffy
 */
public class FlatWidgetInflater extends AppCompatViewInflater {

    @NonNull
    @Override
    protected AppCompatTextView createTextView(Context context, AttributeSet attrs) {
        return rInflate(super.createTextView(context, attrs), context, null, attrs);
    }

    @NonNull
    @Override
    protected AppCompatImageView createImageView(Context context, AttributeSet attrs) {
        return rInflate(super.createImageView(context, attrs), context, null, attrs);
    }

    @NonNull
    @Override
    protected AppCompatButton createButton(Context context, AttributeSet attrs) {
        return rInflate(super.createButton(context, attrs), context, null, attrs);
    }

    @NonNull
    @Override
    protected AppCompatEditText createEditText(Context context, AttributeSet attrs) {
        return rInflate(super.createEditText(context, attrs), context, null, attrs);
    }

    @NonNull
    @Override
    protected AppCompatSpinner createSpinner(Context context, AttributeSet attrs) {
        return rInflate(super.createSpinner(context, attrs), context, null, attrs);
    }

    @NonNull
    @Override
    protected AppCompatImageButton createImageButton(Context context, AttributeSet attrs) {
        return rInflate(super.createImageButton(context, attrs), context, null, attrs);
    }

    @NonNull
    @Override
    protected AppCompatCheckBox createCheckBox(Context context, AttributeSet attrs) {
        return rInflate(super.createCheckBox(context, attrs), context, null, attrs);
    }

    @NonNull
    @Override
    protected AppCompatRadioButton createRadioButton(Context context, AttributeSet attrs) {
        return rInflate(super.createRadioButton(context, attrs), context, null, attrs);
    }

    @NonNull
    @Override
    protected AppCompatCheckedTextView createCheckedTextView(Context context, AttributeSet attrs) {
        return rInflate(super.createCheckedTextView(context, attrs), context, null, attrs);
    }

    @NonNull
    @Override
    protected AppCompatAutoCompleteTextView createAutoCompleteTextView(Context context, AttributeSet attrs) {
        return rInflate(super.createAutoCompleteTextView(context, attrs), context, null, attrs);
    }

    @NonNull
    @Override
    protected AppCompatMultiAutoCompleteTextView createMultiAutoCompleteTextView(Context context, AttributeSet attrs) {
        return rInflate(super.createMultiAutoCompleteTextView(context, attrs), context, null, attrs);
    }

    @NonNull
    @Override
    protected AppCompatRatingBar createRatingBar(Context context, AttributeSet attrs) {
        return rInflate(super.createRatingBar(context, attrs), context, null, attrs);
    }

    @NonNull
    @Override
    protected AppCompatSeekBar createSeekBar(Context context, AttributeSet attrs) {
        return rInflate(super.createSeekBar(context, attrs), context, null, attrs);
    }

    @NonNull
    @Override
    protected AppCompatToggleButton createToggleButton(Context context, AttributeSet attrs) {
        return rInflate(super.createToggleButton(context, attrs), context, null, attrs);
    }

    @Nullable
    @Override
    protected View createView(Context context, String name, AttributeSet attrs) {
        return rInflate(super.createView(context, name, attrs), context, name, attrs);
    }

    private <T extends View> T rInflate(@Nullable View view, Context context, String name, AttributeSet attrs) {
        if (view == null) {
            try {
                Method method = AppCompatViewInflater.class.getDeclaredMethod(
                        "createViewFromTag", Context.class, String.class, AttributeSet.class);
                method.setAccessible(true);
                view = (View) method.invoke(this, context, name, attrs);
                method.setAccessible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (view != null && view.getBackground() == null) {
            FlatWidgetDrawable drawable = FlatWidgetDrawable.parse(context, attrs);
            if (drawable != null) {
                view.setBackground(drawable);
            }
        }

        return (T) view;
    }
}

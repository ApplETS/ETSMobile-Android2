package ca.etsmtl.applets.etsmobile.binding;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Sonphil on 22-12-17.
 */

public class BindingAdapter {
    @android.databinding.BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @android.databinding.BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    @android.databinding.BindingAdapter({"android:tint"})
    public static void setImageViewTint(ImageView imageView, @ColorRes int colorResource) {
        if (colorResource > 0) {
            Context context = imageView.getContext();

            imageView.setColorFilter(ContextCompat.getColor(context, colorResource));
        }
    }

    @android.databinding.BindingAdapter({"android:textColor"})
    public static void setTextViewTextColor(TextView textView, @ColorRes int colorResource) {
        if (colorResource > 0) {
            Context context = textView.getContext();

            textView.setTextColor(ContextCompat.getColor(context, colorResource));
        }
    }

    @android.databinding.BindingAdapter({"android:text"})
    public static void setTextViewText(TextView textView, @StringRes int stringResource) {
        if (stringResource > 0) {
            textView.setText(stringResource);
        }
    }
}

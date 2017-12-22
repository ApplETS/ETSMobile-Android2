package ca.etsmtl.applets.etsmobile.ui;

import android.view.View;

/**
 * Created by Sonphil on 22-12-17.
 */

public class BindingAdapter {
    @android.databinding.BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}

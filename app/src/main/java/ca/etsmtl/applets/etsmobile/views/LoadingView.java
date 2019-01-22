package ca.etsmtl.applets.etsmobile.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import ca.etsmtl.applets.etsmobile2.R;

public class LoadingView extends RelativeLayout {

    private ProgressBar progressBar;
    private TextView textView;

    public LoadingView(Context context) {
        super(context);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public static void hideLoadingView(LoadingView loadingView) {
        if (loadingView != null) {
            loadingView.hideProgessBar();
            loadingView.setVisibility(View.GONE);
        }
    }

    public void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.base_layout, this, true);
        progressBar = (ProgressBar) findViewById(R.id.base_layout_loading_pb);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.ets_red), PorterDuff.Mode.SRC_IN);
        textView = (TextView) findViewById(R.id.base_layout_error_tv);
        textView.setVisibility(View.GONE);
    }

    public void showLoadingView(){
        this.setVisibility(View.VISIBLE);
    }

    public void setMessageError(String text){
        textView.setVisibility(View.VISIBLE);
        textView.setText(text);
    }

    public void hideMessageError(){
        textView.setVisibility(View.GONE);
    }

    public void hideProgessBar(){
        progressBar.setVisibility(View.GONE);
        progressBar.clearAnimation();
    }
}

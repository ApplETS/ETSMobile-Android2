package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.etsmtl.applets.etsmobile.ui.activity.MainActivity;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.views.LoadingView;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by steve on 2016-03-12.
 */
public class MonETSFragment extends BaseFragment {
    //Intégration CAS here?


    @Bind(R.id.webView)
    WebView webView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_web_view, container, false);
        super.onCreateView(inflater, v, savedInstanceState);
        ( (MainActivity)getActivity()).setTitle(getFragmentTitle());

        webView = (WebView) v.findViewById(R.id.webView);

        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        loadingView.showLoadingView();


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // super.onPageFinished(view, url);
                LoadingView.hideLoadingView(loadingView);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
        });

        webView.loadUrl("https://portail.etsmtl.ca/");
        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());
        return v;
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.menu_section_1_monETS);
    }
}

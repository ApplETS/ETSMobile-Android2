package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile2.R;

public class FAQFragment extends WebFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_faq, container, false);
        WebView faqWebView = (WebView) v.findViewById(R.id.faq_webview);
        faqWebView.getSettings().setJavaScriptEnabled(true);
        faqWebView.getSettings().setAppCachePath(getActivity().getCacheDir().getAbsolutePath());
        faqWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        if(Utility.isNetworkAvailable(getActivity())){
            //faqWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
            faqWebView.loadUrl("http://www.clubapplets.ca/faq/");
        } else {
            //faqWebView.getSettings().get
            Toast.makeText(getActivity(), "Une connexion internet est requise pour télécharger la page FAQ", Toast.LENGTH_LONG).show();
        }

        return v;
    }

}

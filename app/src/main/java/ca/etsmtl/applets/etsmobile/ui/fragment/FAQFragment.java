package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

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
        faqWebView.loadUrl("http://www.clubapplets.ca/faq/");
        return v;
    }
}

package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile.views.LoadingView;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Phil on 17/11/13. Modified by Laurence on 05/02/14.
 */
public class BiblioFragment extends WebFragment {

	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_web_view, container, false);
		super.onCreateView(inflater, v, savedInstanceState);

		/*webView = (WebView) v.findViewById(R.id.webView);

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
				view.loadUrl("file:///android_asset/webview_error_page.html");
			}
		});

		webView.loadUrl(getActivity().getString(R.string.url_biblio));*/
		AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());
		return v;
	}
}

package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.view.View;

public class WebFragment extends BaseFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		assert progressBar != null;
		progressBar.setVisibility(View.INVISIBLE);
	}
}

package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.view.View;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile2.R;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public abstract class HttpFragment extends BaseFragment implements RequestListener<Object> {

	protected DataManager dataManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dataManager = DataManager.getInstance(getActivity());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateUI();
	}

	abstract void updateUI();

	@Override
	public void onRequestFailure(SpiceException arg0) {
		progressBar.setVisibility(View.GONE);
		errorMessageTv.setVisibility(View.VISIBLE);
		errorMessageTv.setText(getString(R.string.error_JSON_PARSING));

	}

	@Override
	public void onRequestSuccess(Object arg0) {
		progressBar.setVisibility(View.GONE);

	}
}

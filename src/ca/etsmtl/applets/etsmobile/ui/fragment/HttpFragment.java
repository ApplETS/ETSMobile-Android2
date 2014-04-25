package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import ca.etsmtl.applets.etsmobile.http.DataManager;

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
}

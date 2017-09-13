package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.views.LoadingView;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Base implementation for fragments that use the network
 * 
 * @author Philippe
 * 
 */
public abstract class HttpFragment extends BaseFragment implements RequestListener<Object> {

	protected DataManager dataManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dataManager = DataManager.getInstance(getActivity());
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateUI();
	}

	/**
	 * Method call after onActivityCreated so the child class can send http
	 * request
	 */
	abstract void updateUI();

	@Override
	public void onRequestFailure(SpiceException e) {
		Log.w("onRequestFailure httpfr", e);
		if (loadingView != null) {
			loadingView.hideProgessBar();
			if (loadingView.isShown()) {
				loadingView.setMessageError(getString(R.string.error_JSON_PARSING));
			}
		}
	}

	@Override
	public void onRequestSuccess(Object o) {
		if(loadingView !=null && getActivity() != null){
            getActivity().runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    LoadingView.hideLoadingView(loadingView);
                }
            });
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		dataManager.stop();
	}
}

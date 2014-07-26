package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ca.etsmtl.applets.etsmobile2.R;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Phil on 17/11/13.
 */
public class NewsFragment extends HttpFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_news, container, false);
		return v;
	}
	
	@Override
	public void onRequestFailure(SpiceException arg0) {

	}

	@Override
	public void onRequestSuccess(Object arg0) {

	}

	@Override
	void updateUI() {
	}
}

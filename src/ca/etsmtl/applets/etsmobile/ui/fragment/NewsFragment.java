package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import ca.etsmtl.applets.etsmobile2.R;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Phil on 17/11/13.
 */
public class NewsFragment extends HttpFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		layoutId = R.layout.fragment_news;
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

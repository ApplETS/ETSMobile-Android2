package ca.etsmtl.applets.etsmobile.ui.fragment;

import ca.etsmtl.applets.etsmobile2.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Phil on 18/11/13.
 */
public class AboutFragment extends WebFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_about, container, false);
	}
}

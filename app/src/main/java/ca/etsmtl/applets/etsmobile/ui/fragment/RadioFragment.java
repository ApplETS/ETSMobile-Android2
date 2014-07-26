package ca.etsmtl.applets.etsmobile.ui.fragment;

import ca.etsmtl.applets.etsmobile2.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Phil on 17/11/13.
 */
public class RadioFragment extends WebFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v  = inflater.inflate(R.layout.fragment_radio,  container, false);
		return v;
	}
}
